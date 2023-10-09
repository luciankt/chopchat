package mc.chopchat.chopchat.util;

import mc.chopchat.chopchat.ChopChat;
import mc.chopchat.chopchat.handlers.ChatHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class MuteEntry {
    Player player;
    long timeStarted;
    double duration;
    String reason;
    String senderName;

    public MuteEntry(Player p, long t, double d, String r, String s) {
        this.player = p;
        this.timeStarted = t;
        this.duration = d;
        this.reason = r;
        this.senderName = s;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public long getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(long timeStarted) {
        this.timeStarted = timeStarted;
    }

    public TextComponent getMuteMessage() {
        String durationStr = duration == Math.ceil(duration) ? (int) duration + "" : "" + duration;
        return Component
                .text("\n")
                .append(Component.text("✖ ", NamedTextColor.DARK_RED))
                .append(Component.text("You are muted " + (duration == -1 ? "" : "for "), NamedTextColor.RED))
                .append(Component.text((duration == -1 ? "permanently" : durationStr + " minute" + (duration == 1 ? "" : "s")),
                        NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text(" with the reason: ", NamedTextColor.RED))
                .append(Component.text(reason, NamedTextColor.YELLOW))
                .append(Component.text("\n"));
    }

    public TextComponent getMuteModMsg() {
        String durationStr = duration == Math.ceil(duration) ? (int) duration + "" : "" + duration;
        return Component
                .text("")
                .append(Component.text(player.getName(), NamedTextColor.DARK_RED, TextDecoration.BOLD))
                .append(Component.text(" was muted " + (duration == -1 ? "" : "for "), NamedTextColor.RED))
                .append(Component.text((duration == -1 ? "permanently" : durationStr + " minute" + (duration == 1 ? "" : "s")),
                        NamedTextColor.GOLD))
                .append(Component.text(" by " + senderName + " with the reason: ", NamedTextColor.RED))
                .append(Component.text(reason, NamedTextColor.YELLOW))
                .append(Component.text(""));
    }

    public static TextComponent getMuteExpiredMsg() {
        return Component.text("Your mute has expired. You can send chat messages now.", NamedTextColor.GREEN);
    }

    public static TextComponent getMuteExpiredModMsg(Player player) {
        return Component.text(player.getName() + "'s mute has expired.", NamedTextColor.GREEN);
    }

    public static NamespacedKey getNamespacedKey() {
        return new NamespacedKey(ChopChat.getPlugin(), "muteentry");
    }

    public static void fetchMuteData(Player player) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        String muteEntryString = data.get(MuteEntry.getNamespacedKey(), PersistentDataType.STRING);
        if (muteEntryString != null) {
            String[] muteEntryParts = muteEntryString.split("\\|");
            MuteEntry muteEntry = new MuteEntry(player, Long.parseLong(muteEntryParts[0]), Double.parseDouble(muteEntryParts[1]), muteEntryParts[2], muteEntryParts[3]);
            ChatHandler.mutedPlayers.put(player.getUniqueId(), muteEntry);
            Bukkit.getLogger().info("Fetched mute entry for " + player.getName() + " -- " + muteEntryString);
        }
    }

    @Override
    public String toString() {
        return String.format("%s|%s|%s|%s", timeStarted, duration, reason, senderName);
    }
}
