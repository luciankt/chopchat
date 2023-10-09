package mc.chopchat.chopchat.commands;

import mc.chopchat.chopchat.handlers.ChatHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Mute implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Return if no args
        if (args.length == 0) {
            return false;
        }

        // Find player to mute
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

        // Get duration
        double duration = -1;
        boolean hasDuration = false;
        if (args.length > 1 && isNumeric(args[1])) {
            hasDuration = true;
            duration = Double.parseDouble(args[1]);
            if (duration <= 0 || duration == Integer.MAX_VALUE)
                duration = -1;
        }

        // Get reason
        String reason = "";
        for (int i = 1 + (hasDuration ? 1 : 0); i < args.length; i++) {
            reason += args[i] + " ";
        }
        reason = reason.replaceAll("\\|","").trim();
        // Default reason if none provided
        if (reason.length() == 0) {
            reason = "Muted by an operator.";
        }

        // Mute player
        String senderName = sender instanceof Player ? sender.getName() : "the console";
        ChatHandler.mutePlayer(player, duration, reason, senderName);
        return true;
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }
}
