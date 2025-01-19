package me.noname.hardlives.listeners;

import me.noname.hardlives.HardApi;
import me.noname.hardlives.Main;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class HardBanEvent implements Listener {

    private final Main plugin;
    private final HardApi api;
    public HardBanEvent(HardApi api, Main plugin){
        this.api = api;
        this.plugin = plugin;
    }


    @EventHandler
    public void on(PlayerDeathEvent event) throws SQLException {
        Player player = event.getEntity();

        Date date = new Date(System.currentTimeMillis() + plugin.getConfig().getInt("BanTimeInMinute") * 60 * 1000);
        if (api.getLives(player) == 0) {
            api.setLives(player, plugin.getConfig().getInt("livesafterban"));
            long currentMillis = System.currentTimeMillis();
            long millisIn24Hours = 24 * 60 * 60 * 1000;
            Timestamp timestamp = new Timestamp(currentMillis + millisIn24Hours);
            api.ban(player, timestamp);
        }

        if (!api.hasLives(player)) return;

        api.removeLives(player, 1);
        player.sendMessage("§aUmarłeś. Straciłeś 1 życie");
        api.updateTab(player);
    }
}
