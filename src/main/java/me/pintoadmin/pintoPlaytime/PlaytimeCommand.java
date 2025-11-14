package me.pintoadmin.pintoPlaytime;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class PlaytimeCommand implements CommandExecutor {
    private final PintoPlaytime plugin;

    public PlaytimeCommand(PintoPlaytime plugin) {
        this.plugin = plugin;
        plugin.getCommand("playtime").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("pintoplaytime.playtime")) return true;
        if(args.length == 0 && sender instanceof Player player) {
            if(!sender.hasPermission("pintoplaytime.playtime.get.self")) {
                sender.sendMessage(ChatColor.RED+"You do not have permission to use this command.");
                return true;
            }
            String playtime = plugin.getPlaytimeManager().getPlaytime(player.getName());
            sender.sendMessage(ChatColor.GOLD+"You have " + playtime + " playtime.");
            return true;
        } else if(args.length == 1) {
            if(!sender.hasPermission("pintoplaytime.playtime.get.other")) return true;
            String playtime = plugin.getPlaytimeManager().getPlaytime(args[0]);
            sender.sendMessage(ChatColor.GOLD+"Player " + args[0] + " has " + playtime + " playtime.");
            return true;
        } else if(args.length != 2) {
            if(!sender.hasPermission("pintoplaytime.playtime.set")) return true;
            sender.sendMessage(ChatColor.RED+"Usage: /playtime <player> <[time](m/h/d)>");
            return true;
        }
        String player = args[0];
        String timeArg = args[1];
        sender.sendMessage(ChatColor.GREEN+"Set playtime for player " + player + " to " + timeArg);

        plugin.getPlaytimeManager().setPlaytime(player, timeArg);
        return true;
    }
}
