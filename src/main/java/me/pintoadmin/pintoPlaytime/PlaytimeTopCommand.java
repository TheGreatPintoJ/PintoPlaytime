package me.pintoadmin.pintoPlaytime;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.Map;
import java.util.UUID;

public class PlaytimeTopCommand implements CommandExecutor {
    private final PintoPlaytime plugin;
    public PlaytimeTopCommand(PintoPlaytime plugin) {
        this.plugin = plugin;
        plugin.getCommand("playtimetop").setExecutor(this);
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!sender.hasPermission("pintoplaytime.top")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }
        sender.sendMessage(ChatColor.AQUA+"Current top playtimes:");
        int index = 0;
        ResultSet rs = plugin.getPlaytimeManager().getTopPlaytimes();
        try {
            while (rs.next()) {
                if(index > 9) break;
                OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(rs.getString("uuid")));
                sender.sendMessage(ChatColor.GOLD+""+(index+1)+". "+ChatColor.AQUA+player.getName()+": "+ChatColor.YELLOW+formatTime(rs.getInt("playtime")));
                index++;
            }
        } catch (SQLException e){
            plugin.getLogger().severe("Error getting top playtimes");
        }
        if(index == 0){
            sender.sendMessage(ChatColor.RED+"No top playtime data available.");
        }
        return true;
    }

    private String formatTime(int number){
        int hours = number / 3600;
        int minutes = (number % 3600) / 60;
        int seconds = number % 60;
        return hours + "h " + minutes + "m" + (seconds > 0 ? " " + seconds + "s" : "");
    }
}
