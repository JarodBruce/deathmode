package com.github.jarodbruce.deathmode;

import org.bukkit.plugin.java.JavaPlugin;

public final class DeathModePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("DeathMode Template Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DeathMode Template Disabled!");
    }
}
