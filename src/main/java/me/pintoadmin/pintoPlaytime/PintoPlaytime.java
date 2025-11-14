package me.pintoadmin.pintoPlaytime;

import net.luckperms.api.*;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getPluginManager;

public final class PintoPlaytime extends JavaPlugin {
    private final SQLiteManager sqLiteManager = new SQLiteManager(this);
    private final ConfigLoader configLoader = new ConfigLoader(this);
    private PlaytimeManager playtimeManager = new PlaytimeManager(this);;
    private PlayerEvents playerEvents = new PlayerEvents(this);
    private LuckPermsHook luckPermsHook;

    private boolean luckpermsInstalled = false;

    @Override
    public void onLoad(){
        if(getServer().getPluginManager().getPlugin("LuckPerms") != null){
            luckpermsInstalled = true;
            getLogger().info("LuckPerms detected!");
        } else {
            getLogger().info("LuckPerms not installed! Use LuckPerms for better integration.");
        }
    }

    @Override
    public void onEnable() {
        if(luckpermsInstalled){
            luckPermsHook = new LuckPermsHook(this);
        }
        getPluginManager().registerEvents(playerEvents, this);
        new PlaytimeCommand(this);
        new PlaytimeTopCommand(this);
    }

    @Override
    public void onDisable() {
        if(playerEvents != null)
            playerEvents.stopTasks();
        sqLiteManager.deinit();
    }

    public SQLiteManager getSqLiteManager() {
        return sqLiteManager;
    }
    public ConfigLoader getConfigLoader() {
        return configLoader;
    }
    public PlaytimeManager getPlaytimeManager() {
        return playtimeManager;
    }
    public LuckPermsHook getLuckPermsHook(){
        return luckPermsHook;
    }

    public boolean getLuckPermsInstalled() {
        return luckpermsInstalled;
    }
}
