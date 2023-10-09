package mc.chopchat.chopchat;

import mc.chopchat.chopchat.commands.*;
import mc.chopchat.chopchat.handlers.*;
import mc.chopchat.chopchat.util.MuteEntry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class ChopChat extends JavaPlugin {

    private static ChopChat plugin;
    public static List<String> BANNED_WORDS;
    public static int SPAM_MSG_COUNT;
    public static int SPAM_MSG_COOLDOWN;
    public static int DUPLICATE_MSG_COOLDOWN;
    public static boolean ENABLE_SPAM_FILTER;
    public static TextComponent WARN_SPAM;
    public static TextComponent WARN_ENGLISH;

    @Override
    public void onEnable() {
        plugin = this;

        // Get config
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        BANNED_WORDS = (List<String>) config.getList("banned_substrings");
        SPAM_MSG_COUNT = config.getInt("spam_msg_count");
        SPAM_MSG_COOLDOWN = config.getInt("spam_msg_cooldown") * 1000;
        DUPLICATE_MSG_COOLDOWN = config.getInt("duplicate_msg_cooldown") * 1000;
        ENABLE_SPAM_FILTER = config.getBoolean("enable_spam_filter");
        WARN_SPAM = Component.text(config.getString("warn_spam"), NamedTextColor.RED);
        WARN_ENGLISH = Component.text(config.getString("warn_english"), NamedTextColor.RED);

       // Handlers
        new ChatHandler(this);
        new PlayerJoinHandler(this);
        new CommandHandler(this);

        // Clocking methods
        ChatHandler.checkMuteExpiration(1000);

        // Commands
        getCommand("lockchat").setExecutor(new LockChat());
        getCommand("alert").setExecutor(new Alert());
        getCommand("mute").setExecutor(new Mute());
        getCommand("unmute").setExecutor(new Unmute());
        getCommand("clearchat").setExecutor(new ClearChat());

        // Get persistent mute data from players' NBT
        Bukkit.getOnlinePlayers().forEach(player -> MuteEntry.fetchMuteData(player));
    }

    @Override
    public void onDisable() {
    }

    public static ChopChat getPlugin() {
        return plugin;
    }
}
