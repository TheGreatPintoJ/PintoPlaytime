package me.pintoadmin.pintoPlaytime;

import org.bukkit.*;
import org.bukkit.entity.*;

import java.sql.*;
import java.util.*;

public class PlaytimeManager {
    private final PintoPlaytime plugin;
    public PlaytimeManager(PintoPlaytime plugin) {
        this.plugin = plugin;
    }

    public void checkMilestones(boolean ignoreMessages) throws SQLException {
        Statement statement = plugin.getSqLiteManager().getConnection().createStatement();
        ResultSet playtimes = statement.executeQuery("SELECT * FROM playtimes;");

        while (playtimes.next()) {
            String uuid = playtimes.getString("uuid");
            int playtime = playtimes.getInt("playtime");
            for (var milestone : plugin.getConfigLoader().getMilestones()) {
                int milestoneTime = 0;
                char milestoneUnit = milestone.get("time").charAt(milestone.get("time").length() - 1);
                int milestoneValue = Integer.parseInt(milestone.get("time").substring(0, milestone.get("time").length() - 1));
                switch (milestoneUnit) {
                    case 'm' -> milestoneTime = milestoneValue * 60;
                    case 'h' -> milestoneTime = milestoneValue * 3600;
                    case 'd' -> milestoneTime = milestoneValue * 86400;
                }

                if (playtime >= milestoneTime) {
                    if(playtime == milestoneTime) ignoreMessages = false;
                    OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(UUID.fromString(uuid));

                    if(milestone.get("message") != null && !milestone.get("message").isEmpty()) {
                        String message = milestone.get("message")
                                .replace("{player}", offlinePlayer.getName())
                                .replace("{time}", String.valueOf(milestoneTime));
                        String[] split = message.split(":");
                        if (split.length != 2) {
                            plugin.getLogger().warning(split[0] + " is an invalid milestone message type. Valid types are 'ALL', 'PLAYER'");
                            continue;
                        }
                        String messageType = split[0];
                        String messageContent = split[1];
                        if (!ignoreMessages) {
                            if (messageType.equalsIgnoreCase("ALL")) {
                                plugin.getServer().broadcastMessage(color(messageContent));
                            } else if (messageType.equalsIgnoreCase("PLAYER")) {
                                if (offlinePlayer.isOnline()) {
                                    Player player = offlinePlayer.getPlayer();
                                    if (player != null) {
                                        player.sendMessage(color(messageContent));
                                    }
                                }
                            } else {
                                plugin.getLogger().warning(messageType + " is an invalid milestone message type. Valid types are 'ALL', 'PLAYER'");
                            }
                        }
                    }

                    String permission = milestone.get("permission");
                    if (permission != null && !permission.isEmpty()) {
                        if (offlinePlayer.isOnline()) {
                            Player player = offlinePlayer.getPlayer();
                            if (player != null && !player.hasPermission(permission)) {
                                player.addAttachment(plugin, permission, true);
                            }
                        }
                    }
                }
            }
        }
    }
    public void setPlaytime(String playerName, String timeArg) {
        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(playerName);

        int timeValue = Integer.parseInt(timeArg.substring(0, timeArg.length() - 1));
        char timeUnit = timeArg.charAt(timeArg.length() - 1);
        int playtimeInSeconds = switch (timeUnit) {
            case 'm' -> timeValue * 60;
            case 'h' -> timeValue * 3600;
            case 'd' -> timeValue * 86400;
            default -> 0;
        };

        try {
            PreparedStatement ps = plugin.getSqLiteManager().getConnection()
                    .prepareStatement("INSERT INTO playtimes (uuid, playtime) VALUES (?, ?) " +
                            "ON CONFLICT(uuid) DO UPDATE SET playtime = ?;");
            ps.setString(1, offlinePlayer.getUniqueId().toString());
            ps.setInt(2, playtimeInSeconds);
            ps.setInt(3, playtimeInSeconds);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Error setting playtime for player " + playerName + ": " + e.getMessage());
        }
    }
    public String getPlaytime(String playerName){
        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(playerName);

        try {
            PreparedStatement ps = plugin.getSqLiteManager().getConnection()
                    .prepareStatement("SELECT playtime FROM playtimes WHERE uuid = ?;");
            ps.setString(1, offlinePlayer.getUniqueId().toString());

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int playtime = rs.getInt("playtime");
                int hours = playtime / 3600;
                int minutes = (playtime % 3600) / 60;
                int seconds = playtime % 60;
                return hours + "h " + minutes + "m" + (seconds > 0 ? " " + seconds + "s" : "");
            } else {
                return "0h 0m";
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error getting playtime for player " + playerName + ": " + e.getMessage());
            return null;
        }
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
