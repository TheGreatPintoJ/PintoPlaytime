package me.pintoadmin.pintoPlaytime;

import net.luckperms.api.*;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getPluginManager;

public final class PintoPlaytime extends JavaPlugin {
    private final SQLiteManager sqLiteManager = new SQLiteManager(this);
    private final ConfigLoader configLoader = new ConfigLoader(this);
    private final PlaytimeManager playtimeManager = new PlaytimeManager(this);
    private final PlayerEvents playerEvents = new PlayerEvents(this);

    private LuckPerms luckPermsApi = null;

    @Override
    public void onEnable() {
        if(LuckPermsProvider.get() != null){
            getLogger().info("LuckPerms detected! Integrating...");
            luckPermsApi = LuckPermsProvider.get();
        }

        getPluginManager().registerEvents(playerEvents, this);
        new PlaytimeCommand(this);
    }

    @Override
    public void onDisable() {
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

    public LuckPerms getLuckPermsApi() {
        return luckPermsApi;
    }
}
