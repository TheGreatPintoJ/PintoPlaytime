package me.pintoadmin.pintoPlaytime;

import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getPluginManager;

public final class PintoPlaytime extends JavaPlugin {
    private final SQLiteManager sqLiteManager = new SQLiteManager(this);
    private final ConfigLoader configLoader = new ConfigLoader(this);
    private final PlaytimeManager playtimeManager = new PlaytimeManager(this);

    @Override
    public void onEnable() {
        getPluginManager().registerEvents(new PlayerEvents(this), this);
        new PlaytimeCommand(this);
    }

    @Override
    public void onDisable() {}

    public SQLiteManager getSqLiteManager() {
        return sqLiteManager;
    }
    public ConfigLoader getConfigLoader() {
        return configLoader;
    }
    public PlaytimeManager getPlaytimeManager() {
        return playtimeManager;
    }
}
