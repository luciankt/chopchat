package mc.chopchat.chopchat.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CBan implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Return if no args
        if (args.length == 0) {
            return false;
        }

        // Find player to ban
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

        // Get reason
        String reason = "";
        for (int i = 1; i < args.length; i++) {
            reason += args[i] + " ";
        }
        reason = reason.replaceAll("\\|","").trim();
        // Default reason if none provided
        if (reason.length() == 0) {
            reason = "Banned by an operator.";
        }

        // Message broadcast
        String finalReason = reason;
        Player finalPlayer = player;
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.sendMessage(getBanMsg(finalPlayer, finalReason));
        });

        // Ban player
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        Bukkit.dispatchCommand(console, "ban-ip " + player.getName() + reason);
        return true;
    }

    public TextComponent getBanMsg(Player player, String reason) {
        return Component
                .text("")
                .append(Component.text(player.getName(), NamedTextColor.DARK_RED, TextDecoration.BOLD))
                .append(Component.text(" was banned for", NamedTextColor.RED))
                .append(Component.text(reason, NamedTextColor.RED));
    }
}
