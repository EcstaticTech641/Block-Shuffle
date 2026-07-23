package com.ronlab.blockshuffle.listener;

import com.ronlab.blockshuffle.BlockShufflePlugin;
import com.ronlab.rga.api.event.MinigameConcludeEvent;
import com.ronlab.rga.api.event.MinigameStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@NullMarked
public class RGAEventListener implements Listener {

    private static final String MINIGAME_ID = "blockshuffle";

    private final BlockShufflePlugin plugin;
    private final PlayerListener playerListener;
    private final Function<String, @Nullable World> worldResolver;

    public RGAEventListener(BlockShufflePlugin plugin, PlayerListener playerListener) {
        this(plugin, playerListener, Bukkit::getWorld);
    }

    public RGAEventListener(BlockShufflePlugin plugin, PlayerListener playerListener, Function<String, @Nullable World> worldResolver) {
        this.plugin = plugin;
        this.playerListener = playerListener;
        this.worldResolver = worldResolver;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMinigameStart(MinigameStartEvent event) {
        if (!MINIGAME_ID.equalsIgnoreCase(event.getMinigameId())) {
            return;
        }

        // If game is already in progress, abort initialization
        if (this.plugin.isInProgress()) {
            this.plugin.getLogger().warning("Received MinigameStartEvent for BlockShuffle, but a session is already in progress!");
            event.setCancelled(true);
            return;
        }

        String worldName = event.getWorldName();
        World world = this.worldResolver.apply(worldName);
        if (world == null) {
            this.plugin.getLogger().severe("Cannot start BlockShuffle: target world '" + worldName + "' not found!");
            event.setCancelled(true);
            return;
        }

        List<UUID> playerUuids = event.getPlayerUuids();
        if (playerUuids.isEmpty()) {
            this.plugin.getLogger().warning("Cannot start BlockShuffle: player UUID payload is empty!");
            event.setCancelled(true);
            return;
        }

        this.plugin.getLogger().info("Starting BlockShuffle via RGA for world: " + worldName + " with " + playerUuids.size() + " players.");
        this.plugin.setInProgress(true);
        this.playerListener.startGame(playerUuids, world);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMinigameConclude(MinigameConcludeEvent event) {
        if (!MINIGAME_ID.equalsIgnoreCase(event.getMinigameId())) {
            return;
        }

        // Populate scores map for RGA
        Map<UUID, Integer> currentScores = this.playerListener.getScores();
        Map<UUID, Number> eventScores = event.getScores();
        eventScores.putAll(currentScores);

        this.plugin.getLogger().info("Concluding BlockShuffle via RGA for world: " + event.getWorldName());

        // Perform game cleanup
        this.playerListener.resetGame();
        this.plugin.setInProgress(false);
    }
}
