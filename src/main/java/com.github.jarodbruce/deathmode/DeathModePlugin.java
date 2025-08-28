package com.github.jarodbruce.deathmode;

import org.bukkit.plugin.java.JavaPlugin;

public final class DeathModePlugin extends JavaPlugin {

    private Config config;

    @Override
    public void onEnable() {
        getLogger().info("DeathMode Enabled!");

        // 設定ファイルを読み込み
        config = new Config(this);

        DeathMode deathMode = new DeathMode(this, config);

        // register command executor/tab completer
        var deathModeCommand = getCommand("deathmode");
        if (deathModeCommand != null) {
            deathModeCommand.setExecutor(deathMode);
            deathModeCommand.setTabCompleter(deathMode);
        }

        // Register event listener
        getServer().getPluginManager().registerEvents(deathMode, this);
    }

    public Config getPluginConfig() {
        return config;
    }

    @Override
    public void onDisable() {
        getLogger().info("DeathMode Disabled!");
    }
}
