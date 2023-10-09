package mc.chopchat.chopchat.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChat implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
       String senderName = sender instanceof Player ? sender.getName() : "the console";
       Bukkit.getServer().sendMessage(getClearChatMsg(senderName));
       return true;
    }

    private TextComponent getClearChatMsg(String senderName) {
        return Component.text("\n".repeat(100))
                .append(Component.text("The chat was cleared by ", NamedTextColor.YELLOW, TextDecoration.BOLD))
                .append(Component.text(senderName, NamedTextColor.GOLD, TextDecoration.BOLD))
                .append(Component.text("!\n\n", NamedTextColor.YELLOW, TextDecoration.BOLD));
    }
}
