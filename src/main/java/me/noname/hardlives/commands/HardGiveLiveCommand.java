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
            sender.sendMessage(ChatColor.RED + "Poprawne użycie: /dajzycie <gracz> <ilość>");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Ta komenda jest tylko dla graczy!");
            return true;
        }

        String playerName = args[0];
        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + args[1] + " nie jest liczbą!");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);
        try {
            if (api.isBanned(target)) {
                api.unBan(target);
                api.addLives(target, amount);
                api.removeLives((Player) sender, amount);
                sender.sendMessage("§aPomyślnie wysłano życie graczowi " + target.getName());
            } else {
                if (api.getLives((Player) sender) >= amount) {
                    api.addLives(target, amount);
                    api.removeLives((Player) sender, amount);
                    sender.sendMessage("§aPomyślnie wysłano życie graczowi " + target.getName());
                } else {
                    sender.sendMessage("§cMasz za mało żyć!");
                }
            }
        } catch (SQLException e) {
            sender.sendMessage("§cBłąd podczas operacji SQL: " + e.getMessage());
        }

        return true;
    }




}
