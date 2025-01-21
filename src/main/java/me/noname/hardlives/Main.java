package me.noname.hardlives;

import me.noname.hardlives.commands.HardGiveLiveCommand;
import me.noname.hardlives.commands.HardLivesCommand;
import me.noname.hardlives.commands.PotionOfLifeCommand;
import me.noname.hardlives.listeners.HardBanEvent;
import me.noname.hardlives.listeners.HardJoinListener;
import me.noname.hardlives.listeners.PotionOfLifeListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class Main extends JavaPlugin {

    private HardApi api;


    @Override
    public void onEnable() {
        enable();
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

    }

    private void enable() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            api = new HardApi(getDataFolder().getAbsolutePath() + "/lives.db", this);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to database!  " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }
        enableListeners();
        enableCommands();
        new HardPlaceholder(api, this).register();
    }

    private void enableListeners(){
        getServer().getPluginManager().registerEvents(new HardJoinListener(api, this), this);
        getServer().getPluginManager().registerEvents(new PotionOfLifeListener(api), this);
        getServer().getPluginManager().registerEvents(new HardBanEvent(api, this), this);
    }
    private void enableCommands(){
        getCommand("zycia").setExecutor(new HardLivesCommand(api, this));
        getCommand("zycia").setTabCompleter(new HardLivesCommand(api, this));
        getCommand("eliksirzycia").setExecutor(new PotionOfLifeCommand(api));
        getCommand("dajzycie").setExecutor(new HardGiveLiveCommand(api));
    }



    @Override
    public void onDisable() {
        try {
            api.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
