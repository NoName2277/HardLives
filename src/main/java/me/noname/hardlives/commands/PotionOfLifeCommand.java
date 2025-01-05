package me.noname.hardlives.commands;

import me.noname.hardlives.HardApi;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PotionOfLifeCommand implements CommandExecutor {

    private final HardApi api;
    public PotionOfLifeCommand(HardApi api){
        this.api = api;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cTa komenda jest tylko dla graczy");
        }else{
            Player player = (Player) sender;
            if(args.length == 1){
                int amount = 0;
                try {
                    amount = Integer.parseInt(args[0]);
                }catch (NumberFormatException e){
                    sender.sendMessage(ChatColor.RED + args[0] + " nie jest liczbą");
                }
                player.getInventory().addItem(api.potionOfLive(amount));
                player.sendMessage("§aPomyślnie dostałeś §6§lEliksir Życia §ao wartości §l" + amount + " §ażyć");
            }else{
                player.getInventory().addItem(api.potionOfLive(1));
                player.sendMessage("§aPomyślnie dostałeś §6§lEliksir Życia");
            }
        }
        return false;
    }
}
