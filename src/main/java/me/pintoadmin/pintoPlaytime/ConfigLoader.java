package me.pintoadmin.pintoPlaytime;

import org.bukkit.configuration.file.*;

import java.io.*;
import java.util.*;

public class ConfigLoader {
    private final PintoPlaytime plugin;
    private final List<Map<String, String>> milestones = new ArrayList<>();

    public ConfigLoader(PintoPlaytime plugin) {
        this.plugin = plugin;

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        plugin.saveDefaultConfig();
    }

    public List<Map<String, String>> getMilestones() {
        if (milestones.isEmpty()) {
            FileConfiguration config = plugin.getConfig();
            List<Map<?, ?>> configMilestones = config.getMapList("milestones");
            for (Map<?, ?> milestone : configMilestones) {
                Map<String, String> milestoneMap = new HashMap<>();
                for (Map.Entry<?, ?> entry : milestone.entrySet()) {
                    milestoneMap.put(entry.getKey().toString(), entry.getValue().toString());
                }
                milestones.add(milestoneMap);
            }
        }
        return milestones;
    }
}
