package me.noname.hardlives;

import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HardApi {

    private final Connection connection;

    private final Main main;
    private NamespacedKey key = new NamespacedKey(Main.getProvidingPlugin(Main.class), "amount");

    public HardApi(String path, Main main) throws SQLException {
        this.main = main;
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS players (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "username TEXT NOT NULL, " +
                    "lives INTEGER NOT NULL DEFAULT 0)");
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void addPlayer(OfflinePlayer player) throws SQLException {
        //this should error if the player already exists
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (uuid, username) VALUES (?, ?)")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.executeUpdate();
        }
    }

    public boolean playerExists(OfflinePlayer player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        }
    }

    public void setLives(OfflinePlayer player, int lives) throws SQLException {
        if (!playerExists(player)) {
            addPlayer(player);
        } else {
            try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET lives = ? WHERE uuid = ?")) {
                preparedStatement.setInt(1, lives);
                preparedStatement.setString(2, player.getUniqueId().toString());
                preparedStatement.executeUpdate();
            }
            updateTab((Player) player); // Call tabFormat after updating lives
        }
    }

    public int getLives(OfflinePlayer player) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT lives FROM players WHERE uuid = ?")) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("lives");
            } else {
                return 0; // Return 0 if the player has no points
            }
        }
    }

    public void addLives(OfflinePlayer player, int lives) throws SQLException {
        if (!playerExists(player)) {
            addPlayer(player);
        } else {
            try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET lives = ? WHERE uuid = ?")) {
                preparedStatement.setInt(1, getLives(player) + lives);
                preparedStatement.setString(2, player.getUniqueId().toString());
                preparedStatement.executeUpdate();
            }

            if (player instanceof Player) {
                updateTab((Player) player);
            }
        }
    }

    public void removeLives(OfflinePlayer player, int lives) throws SQLException{
        if (!playerExists(player)) {
            addPlayer(player);
        } else {
            try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET lives = ? WHERE uuid = ?")) {
                preparedStatement.setInt(1, getLives(player) - lives);
                preparedStatement.setString(2, player.getUniqueId().toString());
                preparedStatement.executeUpdate();
            }
        }
    }

    public boolean hasLives(OfflinePlayer player) throws SQLException {
        return getLives(player) > 0;
    }

    public void updateTab(Player player) throws SQLException {
        if(main.getConfig().getBoolean("enableTab", true)) {
            StringBuilder hearts = new StringBuilder();
            for (int i = 0; i < getLives(player); i++)
                hearts.append(ChatColor.DARK_RED + "❤").append(" ");
            new ComponentBuilder(hearts.toString().trim())
                    .create();
            if (getLives(player) <= 0) {
                player.setPlayerListFooter("§c§lBrak żyć! Po śmierci dostajesz bana!");
            } else if (getLives(player) < 10) {
                player.setPlayerListFooter(String.valueOf(hearts));
            } else if (getLives(player) >= 10) {
                player.setPlayerListFooter(ChatColor.DARK_RED + "§lMasz aktualnie " + getLives(player) + ChatColor.DARK_RED + "x§l❤");
            }
        }
    }

    public String test(OfflinePlayer player) throws SQLException {
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < getLives(player); i++)
            hearts.append(ChatColor.DARK_RED + "❤").append(" ");
        new ComponentBuilder(hearts.toString().trim())
                .create();
        if (getLives(player) <= 0) {
            return ChatColor.DARK_RED + "§lBrak żyć! Po śmierci dostajesz bana!";
        } else if (getLives(player) < 10) {
            return (String.valueOf(hearts));
        } else if (getLives(player) >= 10) {
          return ChatColor.DARK_RED + "§lMasz aktualnie " + getLives(player) + ChatColor.DARK_RED + "x§l❤";
        }
        return "Error";
    }

    public void removeBanAndAddLives(OfflinePlayer player, int lives) throws SQLException {
        BanList banList = Bukkit.getBanList(BanList.Type.NAME);
        if (banList.isBanned(player.getName())) {
            banList.pardon(player.getName());
            addLives(player, lives);
        }
    }

    public ItemStack potionOfLive(int amount) {
        ItemStack itemStack = new ItemStack(Material.POTION);
        PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
        meta.setColor(Color.RED);
        meta.setDisplayName("§6§lEliksir Życia");
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(key, PersistentDataType.INTEGER, amount);
        List<String> lore = new ArrayList<>();
        if (container.get(key, PersistentDataType.INTEGER) == 1) {
            lore.add("§aEliksir Zycia o wartości §6§l1 §ażycia");
            meta.setLore(lore);
        } else {
            lore.add("Eliksir Zycia o wartości " + container.get(key, PersistentDataType.INTEGER) + "żyć");
            meta.setLore(lore);
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }

}
