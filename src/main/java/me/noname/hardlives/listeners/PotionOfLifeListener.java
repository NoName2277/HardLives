package me.noname.hardlives.listeners;

import me.noname.hardlives.HardApi;
import me.noname.hardlives.Main;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.sql.SQLException;
import java.util.List;

public class PotionOfLifeListener implements Listener {

    private final HardApi api;
    public PotionOfLifeListener(HardApi api){
        this.api = api;
    }

    private NamespacedKey key = new NamespacedKey(Main.getProvidingPlugin(Main.class), "amount");

    private int getLiveAmount(ItemStack itemStack){
        if(!itemStack.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) return 0;
        return itemStack.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
    }

    @EventHandler
    public void potionOfLiveConsumeEvent(PlayerItemConsumeEvent event) throws SQLException {
        Player player = event.getPlayer();
        if(!event.getItem().getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER)) return;
        api.addLives(player, getLiveAmount(event.getItem()));
        if(getLiveAmount(event.getItem()) ==1) {
            player.sendMessage("§aPomyślnie dodano §a§l1 życie");
            api.updateTab(player);
        }else{
            player.sendMessage("§aPomyślnie dodano §a§l" + getLiveAmount(event.getItem()) + " §ażycia");
            api.updateTab(player);
        }
    }

}
