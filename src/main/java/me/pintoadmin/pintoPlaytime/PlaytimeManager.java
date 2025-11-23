package me.pintoadmin.pintoPlaytime;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import java.sql.*;
import java.util.*;

public class PlaytimeManager {
    private final PintoPlaytime plugin;
    public PlaytimeManager(PintoPlaytime plugin) {
        this.plugin = plugin;
    }

    public void checkMilestones() throws SQLException {
        Statement statement = plugin.getSqLiteManager().getConnection().createStatement();
        ResultSet playtimes = statement.executeQuery("SELECT * FROM playtimes;");

        while (playtimes.next()) {
            String uuid = playtimes.getString("uuid");
            int playtime = playtimes.getInt("playtime");
            for (var milestone : plugin.getConfigLoader().getMilestones()) {
                int milestoneTime = 0;
                char milestoneUnit = milestone.get("time").getFirst().charAt(milestone.get("time").getFirst().length() - 1);
                int milestoneValue = Integer.parseInt(milestone.get("time").getFirst().substring(0, milestone.get("time").getFirst().length() - 1));
                switch (milestoneUnit) {
                    case 'm' -> milestoneTime = milestoneValue * 60;
                    case 'h' -> milestoneTime = milestoneValue * 3600;
                    case 'd' -> milestoneTime = milestoneValue * 86400;
                }

                if (playtime >= milestoneTime) {
                    OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(UUID.fromString(uuid));

                    if (playtime == milestoneTime) {
                        if (milestone.get("messages") != null && !milestone.get("messages").isEmpty()) {
                            for (String entry : milestone.get("messages")) {
                                String message = entry
                                        .replace("{player}", offlinePlayer.getPlayer().getName())
                                        .replace("{time}", String.valueOf(milestoneTime));
                                String[] split = message.split(":");
                                if (split.length != 2) {
                                    plugin.getLogger().warning(split[0] + " is an invalid milestone message type. Valid types are 'ALL', 'PLAYER'");
                                    continue;
                                }
                                String messageType = split[0];
                                String messageContent = split[1];
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
                        if (milestone.containsKey("commands")) {
                            for (String entry : milestone.get("commands")) {
                                String command = entry
                                        .replace("{player}", offlinePlayer.getPlayer().getName())
                                        .replace("{time}", String.valueOf(milestoneTime));
                                if (!command.isEmpty()) {
                                    Bukkit.getScheduler().runTask(plugin, () -> {
                                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
                                    });
                                }
                            }
                        }
                    }

                    if (milestone.containsKey("permissions")) {
                        for (String entry : milestone.get("permissions")) {
                            if (entry != null && !entry.isEmpty()) {
                                if (plugin.getLuckPermsInstalled()) {
                                    LuckPermsHook hook = plugin.getLuckPermsHook();
                                    hook.givePermission(UUID.fromString(uuid), entry);
                                } else {
                                    if (offlinePlayer.isOnline()) {
                                        Player player = offlinePlayer.getPlayer();
                                        if (player != null && !player.hasPermission(entry)) {
                                            player.addAttachment(plugin, entry, true);
                                        }
                                    }
                                }
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
                return formatTime(rs.getInt("playtime"));
            } else {
                return "0h 0m";
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error getting playtime for player " + playerName + ": " + e.getMessage());
            return null;
        }
    }
    public int getJoins(String playerName){
        OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(playerName);

        try {
            PreparedStatement ps = plugin.getSqLiteManager().getConnection()
                    .prepareStatement("SELECT timesjoined FROM playtimes WHERE uuid = ?;");
            ps.setString(1, offlinePlayer.getUniqueId().toString());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("timesjoined");
            } else {
                return 0;
            }
        } catch (SQLException e){
            plugin.getLogger().severe("Error getting timesjoined for player " + playerName + ": " + e.getMessage());
        }
        return 0;
    }
    public ResultSet getTopPlaytimes(){
        Map<String, String> finalMap = new HashMap<>();

        try {
            Connection conn = plugin.getSqLiteManager().getConnection();

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM playtimes ORDER BY playtime DESC;");
            return ps.executeQuery();
        } catch (SQLException e){
            plugin.getLogger().severe("Error getting top playtimes");
        }
        return null;
    }

    private String formatTime(int number){
        int hours = number / 3600;
        int minutes = (number % 3600) / 60;
        int seconds = number % 60;
        return hours + "h " + minutes + "m" + (seconds > 0 ? " " + seconds + "s" : "");
    }
    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
