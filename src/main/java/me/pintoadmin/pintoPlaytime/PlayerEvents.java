package me.pintoadmin.pintoPlaytime;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.*;

import java.sql.*;
import java.util.*;


public class PlayerEvents implements Listener {
    private final PintoPlaytime plugin;
    private Map<Player, BukkitRunnable> playtimeTasks = new HashMap<>();

    public PlayerEvents(PintoPlaytime plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        try {
            Statement statement = plugin.getSqLiteManager().getConnection().createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS playtimes (uuid TEXT PRIMARY KEY, playtime INTEGER);");
        } catch (SQLException e) {
            plugin.getLogger().severe("Error initializing playtime for player " + player.getName() + ": " + e.getMessage());
        }
        BukkitRunnable playtimeTask = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    plugin.getPlaytimeManager().checkMilestones();

                    PreparedStatement ps = plugin.getSqLiteManager().getConnection()
                            .prepareStatement("INSERT INTO playtimes (uuid, playtime) VALUES (?, 1) " +
                                    "ON CONFLICT(uuid) DO UPDATE SET playtime = playtime + 1;");
                    ps.setString(1, player.getUniqueId().toString());
                    ps.executeUpdate();
                } catch (SQLException e) {
                    plugin.getLogger().severe("Error updating playtime for player " + player.getName() + ": " + e.getMessage());
                    this.cancel();
                }
            }
        };
        playtimeTask.runTaskTimerAsynchronously(plugin, 20L, 20L);
        playtimeTasks.put(player, playtimeTask);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        BukkitRunnable playtimeTask = playtimeTasks.remove(player);
        if (playtimeTask != null) {
            playtimeTask.cancel();
        }
    }
}
