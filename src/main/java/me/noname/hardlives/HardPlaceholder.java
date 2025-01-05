package me.noname.hardlives;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public class HardPlaceholder extends PlaceholderExpansion {

    private final HardApi api;

    public HardPlaceholder(HardApi api, Main main){
        this.api = api;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "HardLives";
    }

    @Override
    public @NotNull String getAuthor() {
        return "NoName0";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if(params.equalsIgnoreCase("lives")){
            try {
                return api.test(player);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
