package com.github.jarodbruce.deathmode;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final Config config;
    private final String[] completeList = new String[]{"reload", "list"};

    public ConfigCommand(JavaPlugin plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 権限チェック
        if (!sender.hasPermission("deathmode.admin")) {
            sender.sendMessage("§cこのコマンドを実行する権限がありません");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            try {
                config.reloadConfig();
                sender.sendMessage("§a設定ファイルを再読み込みしました");
                plugin.getLogger().info(sender.getName() + " が設定ファイルを再読み込みしました");
            } catch (Exception e) {
                sender.sendMessage("§c設定ファイルの再読み込みに失敗しました: " + e.getMessage());
                plugin.getLogger().severe("設定ファイルの再読み込みに失敗: " + e.getMessage());
            }
        } else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            try {
                sender.sendMessage("§e=== 設定一覧 ===");
                for (String configEntry : config.getConfigList()) {
                    sender.sendMessage(configEntry);
                }
                plugin.getLogger().info(sender.getName() + " が設定一覧を表示しました");
            } catch (Exception e) {
                sender.sendMessage("§c設定一覧の取得に失敗しました: " + e.getMessage());
                plugin.getLogger().severe("設定一覧の取得に失敗: " + e.getMessage());
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList(completeList);
        }
        return null;
    }
}
