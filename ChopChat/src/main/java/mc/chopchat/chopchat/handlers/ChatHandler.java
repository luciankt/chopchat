package mc.chopchat.chopchat.handlers;

import mc.chopchat.chopchat.ChopChat;
import mc.chopchat.chopchat.commands.LockChat;
import mc.chopchat.chopchat.util.MuteEntry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static mc.chopchat.chopchat.ChopChat.*;

public class ChatHandler implements Listener {

    public static HashMap<UUID, MuteEntry> mutedPlayers = new HashMap<>(); // stores players' chat mute data
    private static HashMap<UUID, Integer> recentMsgCount = new HashMap<>(); // dynamic amount of recent messages
    private static HashMap<UUID, String> lastMsg = new HashMap<>(); // contents of the latest message

    public ChatHandler(ChopChat plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChatMessage(AsyncChatEvent event) {
        String message = PlainTextComponentSerializer.plainText().serialize(event.originalMessage());
        Player player = event.getPlayer();

        // Block message if muted, chat locked, spam, etc.
        if (!runChatValidator(message, player)) {
            blockMessage(event);
        }

        // Provide color chat codes for operators
        if (player.hasPermission("colorchat")) {
            event.message(Component.text(message.replaceAll("&([a-f0-9k-or])", "§$1")));
        }
    }

    /**
     * Validates a message by verifying user is not muted, chat is not locked,
     * it is not spam or non-English, etc.
     * @param message The contents of the message
     * @param player The player who sent the message
     * @return false if it should be blocked, true if message is OK
     */
    public static boolean runChatValidator(String message, Player player) {
        UUID uuid = player.getUniqueId();

        // Block slurs and mute
        if (hasMatchingSubstring(message, BANNED_WORDS)) {
            mutePlayer(player, -1, "Inappropriate language", "the chat filter");
            return false;
        }

        // Block message if muted
        if (isMuted(player)) {
            MuteEntry muteEntry = getMuteEntry(player);
            player.sendMessage(muteEntry.getMuteMessage());
            return false;
        }

        // Locked chat
        if (LockChat.isChatLocked && !player.hasPermission("lockchat")) {
            player.sendMessage(LockChat.getChatFailMsg());
            return false;
        }

        // Spam filter
        if (ENABLE_SPAM_FILTER && !player.hasPermission("spam")) {
            // Message flooding
            incrementMsgCount(uuid);
            if (recentMsgCount.get(uuid) > SPAM_MSG_COUNT) {
                player.sendMessage(WARN_SPAM);
                return false;
            }
            // Message duplication
            String prevMsg = lastMsg.get(uuid);
            if (prevMsg != null && prevMsg.equalsIgnoreCase(message)) {
                player.sendMessage(WARN_SPAM);
                return false;
            } else {
                logLastMsg(uuid, message);
            }
            // Block messages with non-English characters
            for (int i = 0; i < message.length(); i++) {
                if ((int) message.charAt(i) > 127) {
                    player.sendMessage(WARN_ENGLISH);
                    return false;
                }
            }
        }
        return true;
    }

    private static void incrementMsgCount(UUID uuid) {
        // Increment recent messages count
        Integer recentMsgs = recentMsgCount.get(uuid);
        recentMsgs = recentMsgs == null ? 0 : recentMsgs.intValue();
        recentMsgCount.put(uuid, recentMsgs + 1);

        // Decrement recent messages count after X seconds
        new Thread(() -> {
            try {
                Thread.sleep(SPAM_MSG_COOLDOWN);
                recentMsgCount.put(uuid, recentMsgCount.get(uuid) - 1);
            } catch (InterruptedException e) {
                Bukkit.getLogger().info(e.getMessage());
            }
        }).start();
    }

    private static void logLastMsg(UUID uuid, String message) {
        lastMsg.put(uuid, message);
        new Thread(() -> {
            try {
                Thread.sleep(DUPLICATE_MSG_COOLDOWN);
                String lastMessage = lastMsg.get(uuid);
                if (lastMessage != null && lastMessage.equals(message))
                    lastMsg.remove(uuid);
            } catch (InterruptedException e) {
                Bukkit.getLogger().info(e.getMessage());
            }
        }).start();
    }

    public static MuteEntry mutePlayer(Player player, double duration, String reason, String senderName) {
        // Mute player
        long time = Instant.now().getEpochSecond();
        MuteEntry muteEntry = new MuteEntry(player, time, duration, reason, senderName);
        mutedPlayers.put(player.getUniqueId(), muteEntry);
        player.sendMessage(muteEntry.getMuteMessage());

        // Send messages to console & Mods
        Bukkit.getLogger().info(PlainTextComponentSerializer.plainText().serialize(muteEntry.getMuteModMsg()));
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (p.hasPermission("mute"))
                p.sendMessage(muteEntry.getMuteModMsg());
        });

        // Store in player data
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(MuteEntry.getNamespacedKey(), PersistentDataType.STRING, muteEntry.toString());
        return muteEntry;
    }

    public static MuteEntry unmutePlayer(Player player) {
        player.getPersistentDataContainer().remove(MuteEntry.getNamespacedKey());
        return mutedPlayers.remove(player.getUniqueId());
    }

    public static MuteEntry expireMute(Player player) {
        // Send message to user
        player.sendMessage(MuteEntry.getMuteExpiredMsg());
        // Send messages to console & Mods
        TextComponent expiredModMsg = MuteEntry.getMuteExpiredModMsg(player);
        Bukkit.getLogger().info(PlainTextComponentSerializer.plainText().serialize(expiredModMsg));
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (p.hasPermission("mute"))
                p.sendMessage(expiredModMsg);
        });
        return unmutePlayer(player);
    }

    private void blockMessage(AsyncChatEvent event) {
        String message = PlainTextComponentSerializer.plainText().serialize(event.originalMessage());
        Bukkit.getLogger().info("Blocked message from " + event.getPlayer().getName() + ": " + message);
        event.setCancelled(true);
    }

    private static boolean isMuted(Player player) {
        return getMuteEntry(player) != null;
    }

    private static MuteEntry getMuteEntry(Player player) {
        return mutedPlayers.get(player.getUniqueId());
    }

    private static boolean hasMatchingSubstring(String str, List<String> substrings) {
        str = str.toLowerCase();
        for (String substring : substrings){
            if (str.contains(substring)) {
                return true;
            }
        }
        return false;
    }

    public static void checkMuteExpiration(int delay) {
        // Check all mutes to see if they are now expired
        long time = Instant.now().getEpochSecond();
        for (MuteEntry entry : mutedPlayers.values()) {
            double duration = entry.getDuration();
            if (duration > 0 && time - entry.getTimeStarted() >= duration * 60) {
                // Remove mute if time expired
                expireMute(entry.getPlayer());
                break;
            }
        };

        // Call again in one second
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                checkMuteExpiration(delay);
            } catch (InterruptedException e) {
                Bukkit.getLogger().info(e.getMessage());
                checkMuteExpiration(delay);
            }
        }).start();
    }
}