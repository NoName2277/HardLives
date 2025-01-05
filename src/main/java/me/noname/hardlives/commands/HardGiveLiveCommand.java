package me.noname.hardlives.commands;

import me.noname.hardlives.HardApi;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class HardGiveLiveCommand implements CommandExecutor {

    private final HardApi api;
    public HardGiveLiveCommand(HardApi api){
        this.api = api;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 2) {
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Ta komenda jest tylko dla graczy!");
            return true;
        }

        String playerName = args[0];
        int amount = 0;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + args[1] + " nie jest liczbą!");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);

        if (target.isBanned()) {
            try {
                api.removeBanAndAddLives(target, amount);
                api.removeLives((OfflinePlayer) sender, amount);
                sender.sendMessage("§aPomyślnie wysłano życia graczowi " + target.getName());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                if (api.getLives((OfflinePlayer) sender) >= amount) {
                    api.addLives(target, amount);
                    api.removeLives((OfflinePlayer) sender, amount);
                    sender.sendMessage("§aPomyślnie wysłano życia graczowi " + target.getName());
                }else{
                    sender.sendMessage("§cMasz za mało żyć!");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }
}
