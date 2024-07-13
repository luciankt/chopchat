package mc.chopchat.chopchat.handlers;

import mc.chopchat.chopchat.ChopChat;
import mc.chopchat.chopchat.util.MuteEntry;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinHandler implements Listener {

    public PlayerJoinHandler(ChopChat plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MuteEntry.fetchMuteData(player);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        // Player forbidden from log-in due to being banned
        if (e.getResult().equals(PlayerLoginEvent.Result.KICK_BANNED)) {
            // Run ban-ip command on ip of failed connection
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            Bukkit.dispatchCommand(console, "minecraft:ban " + e.getPlayer().getName());
            Bukkit.dispatchCommand(console, "minecraft:ban-ip " + e.getRealAddress().getHostAddress());
        }
    }

}
