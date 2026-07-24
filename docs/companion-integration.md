# Companion Plugin Integration Guide

This document details the architectural patterns and event handling mechanisms used by companion minigame plugins (such as Block-Shuffle) to integrate cleanly with **Ronlab Game Assistant (RGA)** using `com.ronlab:rga-api:1.11.0`.

---

## 1. Overview

Ronlab Game Assistant provides an event-driven session lifecycle model for Minecraft minigame plugins. Companion plugins register event listeners to receive session initialization payloads (`MinigameStartEvent`) and report session results (`MinigameConcludeEvent`), decoupling game logic from session orchestration, world management, and queue management.

---

## 2. Paper Manifest Configuration (`paper-plugin.yml`)

To allow standalone execution while enabling RGA integration when present, companion plugins declare a soft-dependency on `RonlabGameAssistant` in `paper-plugin.yml`:

```yaml
name: BlockShuffle
version: '${project.version}'
main: com.ronlab.blockshuffle.BlockShufflePlugin
api-version: '26.1'

dependencies:
  server:
    RonlabGameAssistant:
      load: BEFORE
      required: false
      join-classpath: true
```

The `load: BEFORE` directive guarantees that RGA initializes prior to the companion plugin, ensuring plugin presence checks evaluate accurately during `onEnable()`.

---

## 3. Runtime Listener Registration

Companion plugins guard RGA listener registration behind runtime plugin checks:

```java
if (Bukkit.getPluginManager().isPluginEnabled("RonlabGameAssistant")) {
    getLogger().info("RonlabGameAssistant detected! Registering RGAEventListener...");
    getServer().getPluginManager().registerEvents(new RGAEventListener(this, playerListener), this);
} else {
    getLogger().info("RonlabGameAssistant not detected. Running in standalone mode.");
}
```

This pattern ensures full backward compatibility for standalone servers running without RGA.

---

## 4. Event Lifecycle Integration

### MinigameStartEvent Lifecycle

When RGA prepares a minigame session following world loading, it fires `com.ronlab.rga.api.event.MinigameStartEvent`.

1. **Minigame ID Verification**: The listener verifies that `event.getMinigameId()` matches its target identifier (`"blockshuffle"`).
2. **State Validation**: If a session is already active, the event is cancelled via `event.setCancelled(true)`.
3. **Payload Extraction**:
   - `event.getWorldName()` provides the target world.
   - `event.getPlayerUuids()` provides the participant UUID list.
4. **Session Launch**: The listener delegates session initialization to the internal game controller.

```java
@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
public void onMinigameStart(MinigameStartEvent event) {
    if (!"blockshuffle".equalsIgnoreCase(event.getMinigameId())) {
        return;
    }

    if (this.plugin.isInProgress()) {
        event.setCancelled(true);
        return;
    }

    World world = Bukkit.getWorld(event.getWorldName());
    if (world == null || event.getPlayerUuids().isEmpty()) {
        event.setCancelled(true);
        return;
    }

    this.plugin.setInProgress(true);
    this.playerListener.startGame(event.getPlayerUuids(), world);
}
```

---

### MinigameConcludeEvent & Score Mutations

When RGA concludes a session (or when an external session termination occurs), it fires `com.ronlab.rga.api.event.MinigameConcludeEvent`.

1. **Score Mutation**: The companion plugin retrieves active player scores and copies them into `event.getScores()` (`Map<UUID, Number>`). RGA consumes these score values for statistics, leaderboards, and rewards.
2. **Resource Cleanup**: The companion plugin invokes `resetGame()` to clear scoreboards, cancel scheduler tasks, remove bossbars, and reset internal state.

```java
@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
public void onMinigameConclude(MinigameConcludeEvent event) {
    if (!"blockshuffle".equalsIgnoreCase(event.getMinigameId())) {
        return;
    }

    Map<UUID, Integer> currentScores = this.playerListener.getScores();
    event.getScores().putAll(currentScores);

    this.playerListener.resetGame();
    this.plugin.setInProgress(false);
}
```

---

## 5. Natural Session Conclusion

When a session completes organically within the companion plugin (e.g. a player reaches victory conditions or all opponents leave), the companion plugin notifies RGA using console command dispatch:

```java
private void concludeRGA() {
    if (this.gameWorld == null) return;
    if (Bukkit.getPluginManager().isPluginEnabled("RonlabGameAssistant")) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rga conclude " + this.gameWorld.getName());
    }
}
```

RGA processes the conclude command, fires `MinigameConcludeEvent` to collect final scores, and performs world tear-down.

---

## 6. Standalone Fallback Mode

If `RonlabGameAssistant` is not enabled, companion plugins provide full administrative command (`/blockshuffle [stop]`) and interactive GUI functionality (`/blockshuffle`), ensuring complete independence when deployed outside of RGA suites.
