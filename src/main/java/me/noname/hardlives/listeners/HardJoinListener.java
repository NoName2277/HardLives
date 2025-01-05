package me.noname.hardlives.listeners;

import me.noname.hardlives.HardApi;
import me.noname.hardlives.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class HardJoinListener implements Listener {

    private final Main plugin;
    private final HardApi api;
    public HardJoinListener(HardApi api, Main plugin){
        this.api = api;
        this.plugin = plugin;
    }

    @EventHandler
    public void onHardJoin(PlayerJoinEvent event) throws SQLException {
        if(!(api.playerExists(event.getPlayer()))){
            api.addPlayer(event.getPlayer());
            api.setLives(event.getPlayer(), plugin.getConfig().getInt("StartLives"));
        }
        api.updateTab(event.getPlayer());
    }

}
