package com.ronlab.blockshuffle;

import com.ronlab.blockshuffle.command.BlockShuffleCommand;
import com.ronlab.blockshuffle.listener.PlayerListener;
import com.ronlab.blockshuffle.listener.RGAEventListener;
import com.ronlab.blockshuffle.menu.BlockShuffleMenu;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.logging.Logger;

@NullMarked
public class BlockShufflePlugin extends JavaPlugin {
    private @Nullable File settingsFile;
    private boolean inProgress;
    private @Nullable PlayerListener playerListener;
    private @Nullable BlockShuffleMenu blockShuffleMenu;

    public static @Nullable Logger LOGGER;

    @Override
    public void onEnable() {
        LOGGER = this.getLogger();
        this.settingsFile = this.getDataFolder().toPath().resolve("settings.yml").toFile();
        this.createSettingsFile();

        YamlConfiguration settings = YamlConfiguration.loadConfiguration(this.settingsFile);

        this.playerListener = new PlayerListener(settings, this);
        this.blockShuffleMenu = new BlockShuffleMenu(this.playerListener, settings, this);

        this.getServer().getPluginManager().registerEvents(this.playerListener, this);
        this.getServer().getPluginManager().registerEvents(this.blockShuffleMenu, this);

        var command = this.getCommand("blockshuffle");
        if (command != null) {
            command.setExecutor(new BlockShuffleCommand(this.playerListener, this.blockShuffleMenu, this, settings));
        }

        // Wire RGA Event Integration if RonlabGameAssistant is present
        if (Bukkit.getPluginManager().isPluginEnabled("RonlabGameAssistant")) {
            this.getLogger().info("RonlabGameAssistant detected! Registering RGAEventListener...");
            this.getServer().getPluginManager().registerEvents(new RGAEventListener(this, this.playerListener), this);
        } else {
            this.getLogger().info("RonlabGameAssistant not detected. Running in standalone mode.");
        }
    }

    @Override
    public void onDisable() {
        if (this.playerListener != null && this.isInProgress()) {
            this.playerListener.resetGame();
        }
    }

    private void createSettingsFile() {
        if (this.settingsFile != null && !this.settingsFile.exists()) {
            this.saveResource("settings.yml", false);
        }
    }

    public boolean isInProgress() {
        return this.inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public @Nullable PlayerListener getPlayerListener() {
        return this.playerListener;
    }

    public @Nullable BlockShuffleMenu getBlockShuffleMenu() {
        return this.blockShuffleMenu;
    }
}
