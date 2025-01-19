package me.noname.hardlives.listeners;

import me.noname.hardlives.HardApi;
import me.noname.hardlives.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.sql.Timestamp;

public class HardJoinListener implements Listener {

    private final Main plugin;
    private final HardApi api;
    public HardJoinListener(HardApi api, Main plugin){
        this.api = api;
        this.plugin = plugin;
    }

    @EventHandler
    public void onHardJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        try {
            if (!api.playerExists(player)) {
                api.addPlayer(player);
                api.setLives(player, plugin.getConfig().getInt("StartLives"));
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (api.isBanned(player)) {
                Timestamp banEndTime = api.getDeathTime(player);
                if (banEndTime.compareTo(now) > 0) {
                    player.kickPlayer("§cBrak żyć! Możesz dołączyć ponownie po: " + banEndTime);
                    return;
                }
                api.unBan(player);
                api.setLives(player, plugin.getConfig().getInt("livesafterban"));
            }
            api.updateTab(player);
        } catch (SQLException e) {
            plugin.getLogger().severe("Błąd SQL podczas obsługi gracza: " + player.getName() + " - " + e.getMessage());
        }
    }



}
