package com.ronlab.blockshuffle.listener;

import com.ronlab.blockshuffle.BlockShufflePlugin;
import com.ronlab.rga.api.event.MinigameConcludeEvent;
import com.ronlab.rga.api.event.MinigameStartEvent;
import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RGAEventListenerTest {

    @Mock
    private BlockShufflePlugin plugin;

    @Mock
    private PlayerListener playerListener;

    @Mock
    private World world;

    @Mock
    private Logger logger;

    @Mock
    private Function<String, World> worldResolver;

    private RGAEventListener rgaEventListener;

    @BeforeEach
    void setUp() {
        lenient().when(plugin.getLogger()).thenReturn(logger);
        rgaEventListener = new RGAEventListener(plugin, playerListener, worldResolver);
    }

    @Test
    @DisplayName("onMinigameStart should ignore non-matching minigame ID")
    void onMinigameStart_ignoresOtherGames() {
        MinigameStartEvent event = new MinigameStartEvent("spleef", "Spleef", "world", List.of(UUID.randomUUID()));

        rgaEventListener.onMinigameStart(event);

        assertFalse(event.isCancelled());
        verify(plugin, never()).setInProgress(anyBoolean());
    }

    @Test
    @DisplayName("onMinigameStart should cancel event if game is already in progress")
    void onMinigameStart_cancelsIfInProgress() {
        when(plugin.isInProgress()).thenReturn(true);
        MinigameStartEvent event = new MinigameStartEvent("blockshuffle", "BlockShuffle", "world", List.of(UUID.randomUUID()));

        rgaEventListener.onMinigameStart(event);

        assertTrue(event.isCancelled());
        verify(playerListener, never()).startGame(anyList(), any());
    }

    @Test
    @DisplayName("onMinigameStart should cancel event if world is not found")
    void onMinigameStart_cancelsIfWorldNotFound() {
        when(worldResolver.apply("non_existent_world")).thenReturn(null);
        when(plugin.isInProgress()).thenReturn(false);

        MinigameStartEvent event = new MinigameStartEvent("blockshuffle", "BlockShuffle", "non_existent_world", List.of(UUID.randomUUID()));

        rgaEventListener.onMinigameStart(event);

        assertTrue(event.isCancelled());
        verify(playerListener, never()).startGame(anyList(), any());
    }

    @Test
    @DisplayName("onMinigameStart should start game on valid payload")
    void onMinigameStart_startsGameOnValidPayload() {
        when(worldResolver.apply("game_world")).thenReturn(world);
        when(plugin.isInProgress()).thenReturn(false);

        UUID playerUuid = UUID.randomUUID();
        MinigameStartEvent event = new MinigameStartEvent("blockshuffle", "BlockShuffle", "game_world", List.of(playerUuid));

        rgaEventListener.onMinigameStart(event);

        assertFalse(event.isCancelled());
        verify(plugin).setInProgress(true);
        verify(playerListener).startGame(List.of(playerUuid), world);
    }

    @Test
    @DisplayName("onMinigameConclude should populate event scores and reset game")
    void onMinigameConclude_populatesScoresAndResets() {
        UUID playerUuid = UUID.randomUUID();
        when(playerListener.getScores()).thenReturn(Map.of(playerUuid, 3));

        MinigameConcludeEvent event = new MinigameConcludeEvent("blockshuffle", "BlockShuffle", "game_world", List.of(playerUuid), Map.of());

        rgaEventListener.onMinigameConclude(event);

        assertEquals(3, event.getScores().get(playerUuid));
        verify(playerListener).resetGame();
        verify(plugin).setInProgress(false);
    }
}
