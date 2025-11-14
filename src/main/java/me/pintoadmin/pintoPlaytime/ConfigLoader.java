package me.pintoadmin.pintoPlaytime;

import org.bukkit.configuration.file.*;

import java.util.*;

public class ConfigLoader {
    private final PintoPlaytime plugin;
    private final List<String> exclusions = new ArrayList<>();
    private final List<Map<String, List<String>>> milestones = new ArrayList<>();

    public ConfigLoader(PintoPlaytime plugin) {
        this.plugin = plugin;

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        plugin.saveDefaultConfig();
        updateConfig();
    }

    public List<Map<String, List<String>>> getMilestones() {
        if (milestones.isEmpty()) {
            FileConfiguration config = plugin.getConfig();
            List<Map<?, ?>> configMilestones = config.getMapList("milestones");
            for (Map<?, ?> milestone : configMilestones) {
                Map<String, List<String>> milestoneMap = new HashMap<>();
                for (Map.Entry<?, ?> entry : milestone.entrySet()) {
                    milestoneMap.put(entry.getKey().toString(), entry.getValue() instanceof List<?> ? (List<String>) entry.getValue() : Arrays.asList(entry.getValue().toString()));
                }
                milestones.add(milestoneMap);
            }
        }
        return milestones;
    }

    public List<String> getExclusions(){
        if(exclusions.isEmpty()){
            FileConfiguration config = plugin.getConfig();
            exclusions.addAll(config.getStringList("playtop_exclude"));
        }
        return exclusions;
    }

    private void updateConfig(){
        FileConfiguration config = plugin.getConfig();
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }
}
