package mc.chopchat.chopchat.handlers;

import io.papermc.paper.event.player.AsyncChatEvent;
import mc.chopchat.chopchat.ChopChat;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandHandler implements Listener {

    public CommandHandler(ChopChat plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    private String[] chatCommands = {"me", "tm", "teammsg", "w", "msg", "tell"};

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();

        // Block command if invalidated
        if (usesChatCommand(event.getMessage())) {
            if (!ChatHandler.runChatValidator(message, player)) {
                blockCommand(event);
            }
        }
    }

    private void blockCommand(PlayerCommandPreprocessEvent event) {
        Bukkit.getLogger().info("Blocked command from " + event.getPlayer().getName() + ": " + event.getMessage());
        event.setCancelled(true);
    }

    private boolean usesChatCommand(String message) {
        for (String cmd : chatCommands) {
            if (message.startsWith("/" + cmd)) {
                return true;
            }
        }
        return false;
    }
}
