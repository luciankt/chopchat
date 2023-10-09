package mc.chopchat.chopchat.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Alert implements CommandExecutor {
    public static boolean isChatLocked;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Return if no content
        if (args.length == 0) {
            return false;
        }

        // Send alert
        TextComponent alertMsg = createAlertMsg(args);
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(alertMsg));
        return true;
    }

    private static TextComponent createAlertMsg(String[] content) {
        String output = "";
        for (String s : content) {
            output += s + " ";
        }
        return Component
                .text("\n")
                .append(Component.text("[", NamedTextColor.WHITE, TextDecoration.BOLD))
                .append(Component.text("ALERT", NamedTextColor.RED, TextDecoration.BOLD))
                .append(Component.text("] ", NamedTextColor.WHITE, TextDecoration.BOLD))
                .append(Component.text(output, NamedTextColor.WHITE))
                .append(Component.text("\n"));
    }
}
