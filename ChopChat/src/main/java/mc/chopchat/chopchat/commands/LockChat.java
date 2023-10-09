package mc.chopchat.chopchat.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LockChat implements CommandExecutor {
    public static boolean isChatLocked;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!isChatLocked) {
            // Lock chat
            isChatLocked = true;
            Bukkit.getServer().broadcast(getLockChatMsg());
        } else {
            // Unlock chat
            isChatLocked = false;
            Bukkit.getServer().broadcast(getUnlockChatMsg());
        }
        return true;
    }

    private static TextComponent getLockChatMsg() {
        return Component
                .text("\n\n\n\n\n")
                .append(Component.text("-------------------------------\n\n", NamedTextColor.YELLOW, TextDecoration.STRIKETHROUGH, TextDecoration.BOLD))
                .append(Component.text("Chat is now locked.", NamedTextColor.RED, TextDecoration.BOLD))
                .append(Component.text("\nOnly moderators can send chat messages.", NamedTextColor.WHITE, TextDecoration.ITALIC))
                .append(Component.text("\n\n-------------------------------", NamedTextColor.YELLOW, TextDecoration.STRIKETHROUGH, TextDecoration.BOLD))
                .append(Component.text("\n"));
    }

    private static TextComponent getUnlockChatMsg() {
        return Component
                .text("\n\n\n\n\n")
                .append(Component.text("-------------------------------\n\n", NamedTextColor.AQUA, TextDecoration.STRIKETHROUGH, TextDecoration.BOLD))
                .append(Component.text("Chat is now unlocked.", NamedTextColor.GREEN, TextDecoration.BOLD))
                .append(Component.text("\nEveryone can send chat messages.", NamedTextColor.WHITE, TextDecoration.ITALIC))
                .append(Component.text("\n\n-------------------------------", NamedTextColor.AQUA, TextDecoration.STRIKETHROUGH, TextDecoration.BOLD))
                .append(Component.text("\n"));
    }

    public static TextComponent getChatFailMsg() {
        return Component.text("You can't send messages because the chat is locked.", NamedTextColor.RED);
    }
}
