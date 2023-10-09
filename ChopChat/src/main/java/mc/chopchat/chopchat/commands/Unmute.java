package mc.chopchat.chopchat.commands;

import mc.chopchat.chopchat.handlers.ChatHandler;
import mc.chopchat.chopchat.util.MuteEntry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Unmute implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Return if no args
        if (args.length == 0) {
            return false;
        }

        // Find player to unmute
        Player player = null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(args[0])) {
                player = p;
                break;
            }
        }
        // Player not found
        if (player == null) {
            sender.sendMessage("Player with name '" + args[0] + "' could not be found.");
            return true;
        }

        // Unmute player
        MuteEntry mute = ChatHandler.unmutePlayer(player);
        player.sendMessage(getUnmuteMsg());

        // No mute existed
        if (mute == null) {
            sender.sendMessage(player.getName() + " is not currently muted.");
            return true;
        }

        // Send message to mods
        TextComponent unmuteModMsg = getUnmuteModMsg(mute, sender);
        Bukkit.getLogger().info(PlainTextComponentSerializer.plainText().serialize(unmuteModMsg));
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (p.hasPermission("mute"))
                p.sendMessage(unmuteModMsg);
        });
        return true;
    }

    private static TextComponent getUnmuteModMsg(MuteEntry mute, CommandSender sender) {
        String senderName = sender instanceof Player ? sender.getName() : "the console";

        return Component
                .text("")
                .append(Component.text(mute.getPlayer().getName(), NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                .append(Component.text(" was unmuted by " + senderName + ".", NamedTextColor.GREEN))
                .append(Component.text(""));
    }

    private static TextComponent getUnmuteMsg() {
        return Component
                .text("You have been unmuted. You can send chat messages now.", NamedTextColor.GREEN);
    }
}
