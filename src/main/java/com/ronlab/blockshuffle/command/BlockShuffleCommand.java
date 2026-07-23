package com.ronlab.blockshuffle.command;

import com.ronlab.blockshuffle.BlockShufflePlugin;
import com.ronlab.blockshuffle.listener.PlayerListener;
import com.ronlab.blockshuffle.menu.BlockShuffleMenu;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class BlockShuffleCommand implements CommandExecutor {
    private final String stopGame;
    private final String stopError;

    private final PlayerListener playerListener;
    private final BlockShuffleMenu blockShuffleMenu;
    private final BlockShufflePlugin plugin;

    public BlockShuffleCommand(PlayerListener playerListener, BlockShuffleMenu blockShuffleMenu, BlockShufflePlugin plugin, YamlConfiguration settings) {
        String sg = settings.getString("stopgame");
        String se = settings.getString("stoperror");
        this.stopGame = sg != null ? sg : "Game stopped!";
        this.stopError = se != null ? se : "No game in progress!";
        this.playerListener = playerListener;
        this.blockShuffleMenu = blockShuffleMenu;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("blockshuffle")) return true;

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("You cannot execute this command from the console.", NamedTextColor.RED));
            return true;
        }
        if (!player.hasPermission("blockshuffle.admin")) {
            player.sendMessage(Component.text("You do not have permission to execute this command.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            this.blockShuffleMenu.show(player);
        } else if (args[0].equalsIgnoreCase("stop")) {
            if (!this.plugin.isInProgress()) {
                player.sendMessage(prefix().append(Component.text(this.stopError, NamedTextColor.RED)));
            } else {
                this.playerListener.stopGame();
                Bukkit.broadcast(prefix().append(Component.text(this.stopGame, NamedTextColor.GREEN)));
            }
        } else {
            player.sendMessage(Component.text("Usage: /blockshuffle [stop]", NamedTextColor.YELLOW));
        }
        return true;
    }

    private Component prefix() {
        return Component.text("<BlockShuffle> ", NamedTextColor.GOLD);
    }
}
