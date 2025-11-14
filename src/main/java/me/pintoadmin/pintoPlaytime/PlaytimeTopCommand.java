package me.pintoadmin.pintoPlaytime;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

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

        Map<String, String> playtimeTops = plugin.getPlaytimeManager().getTopPlaytimes();
        int index = 0;
        for(Map.Entry<String, String> entry : playtimeTops.entrySet()) {
            if(index == 9) break;
            OfflinePlayer player = plugin.getServer().getOfflinePlayer(UUID.fromString(entry.getKey()));
            if(plugin.getConfigLoader().getExclusions().contains(player.getName())) continue;
            sender.sendMessage(ChatColor.GOLD+""+index+". "+ChatColor.AQUA+player.getName()+": "+ChatColor.YELLOW+entry.getValue());
            index++;
        }
        if(index == 0){
            sender.sendMessage(ChatColor.RED+"No top playtime data available.");
        }
        return true;
    }
}
