package mc.chopchat.chopchat.handlers;

import mc.chopchat.chopchat.ChopChat;
import mc.chopchat.chopchat.util.MuteEntry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinHandler implements Listener {

    public PlayerJoinHandler(ChopChat plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MuteEntry.fetchMuteData(player);
    }
}
