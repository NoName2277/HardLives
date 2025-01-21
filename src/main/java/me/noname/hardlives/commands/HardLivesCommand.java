package me.noname.hardlives.commands;

import me.noname.hardlives.HardApi;
import me.noname.hardlives.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HardLivesCommand implements CommandExecutor, TabCompleter {

    private final HardApi api;
    private final Main plugin;

    public HardLivesCommand(HardApi api, Main plugin) {
        this.api = api;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof org.bukkit.entity.Player) {
                org.bukkit.entity.Player player = (org.bukkit.entity.Player) sender;
                try {
                    sender.sendMessage("§aMasz aktualnie §a§l" + api.getLives(player) + " §azycia");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Ta komenda musi byc wykonana przez gracza.");
            }
            return true;
        } else if (args.length >= 2) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

            if (args[0].equalsIgnoreCase("unban")) {
                if (!sender.hasPermission("HardLives.admin")) {
                    sender.sendMessage(ChatColor.RED + "Nie masz uprawnień do użycia tej komendy!");
                    return true;
                }

                if (target == null || !target.hasPlayedBefore()) {
                    sender.sendMessage(ChatColor.RED + "Nie znaleziono gracza o nicku: " + args[1]);
                    return true;
                }

                try {
                    if (api.isBanned(target)) {
                        api.unBanByName(target);
                        sender.sendMessage("§aPomyślnie odbanowano gracza §l" + target.getName() + "§a!");
                    } else {
                        sender.sendMessage("§cGracz §l" + target.getName() + " §cnie jest zbanowany.");
                    }
                } catch (SQLException e) {
                    sender.sendMessage("§cWystąpił błąd podczas odbanowywania gracza.");
                    e.printStackTrace();
                }
                return true;
            }

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Nie ma gracza o takim nicku");
                return true;
            }

            int amount = 0;

            if (args.length == 3) {
                if (sender.hasPermission("HardLives.admin")) {
                    try {
                        amount = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + args[2] + " nie jest liczbą");
                        return true;
                    }

                    if (args[0].equalsIgnoreCase("dodaj")) {
                        try {
                            api.addLives(target, amount);
                            sender.sendMessage("§aPomyślnie dodano zycia graczowi §l" + target.getName());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (args[0].equalsIgnoreCase("usun")) {
                        try {
                            api.removeLives(target, amount);
                            sender.sendMessage("§aPomyślnie usunieto zycia graczowi §l" + target.getName());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                    } else if (args[0].equalsIgnoreCase("ustaw")) {
                        try {
                            api.setLives(target, amount);
                            sender.sendMessage("§aPomyślnie ustawiono zycia graczowi §l" + target.getName());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                return true;
            }
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("uball")) {
                try {
                    api.unBanAll();
                    sender.sendMessage("§aPomyślnie odbanowano wszystkich graczy!");
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                plugin.reloadConfig();
                sender.sendMessage("§aPomyślnie przeładowano config!");
            }
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender.hasPermission("HardLives.admin")) {
                ArrayList<String> list = new ArrayList<>();
                list.add("dodaj");
                list.add("usun");
                list.add("ustaw");
                list.add("reload");
                list.add("uball");
                list.add("unban");
                return list;
            }
            return null;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("unban")) {
            List<String> playerNames = new ArrayList<>();
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                playerNames.add(player.getName());
            }
            return playerNames;
        }
        return null;
    }
}
