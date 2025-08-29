package com.github.jarodbruce.deathmode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeathMode implements CommandExecutor, TabCompleter, Listener {

    private final JavaPlugin plugin;
    private final Config config;
    // players who enabled deathmode mode via command
    private final Set<UUID> enabledPlayers = new HashSet<>();
    private final String[] completeList = new String[]{"enable", "disable", "config"};

    public DeathMode(JavaPlugin plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // スペクテイター機能が無効の場合
        if (!config.isdeathmodeEnabled()) {
            sender.sendMessage("スペクテイター機能は現在無効になっています");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("スペクテイター機能は現在無効になっています");
            for (String configEntry : config.getConfigList()) {
                plugin.getServer().broadcastMessage(configEntry);
            }
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 1 && args[0].equalsIgnoreCase(this.completeList[0])) {
            plugin.getServer().broadcastMessage("deathmode is enabled");
            player.sendMessage("§aあなたは死亡モードを有効にしました");

        } else if (args.length == 1 && args[0].equalsIgnoreCase(this.completeList[1])) {
            plugin.getServer().broadcastMessage("deathmode is disabled");

        } else if (args.length == 1 && args[0].equalsIgnoreCase(this.completeList[2])) {
            // show config list
            try {
                player.sendMessage("§e=== deathmode ===");
                for (String configEntry : config.getConfigList()) {
                    player.sendMessage(configEntry);
                }

                if (config.isDebugEnabled()) {
                    plugin.getLogger().info(String.format("Player %s viewed config list", player.getName()));
                }
            } catch (Exception e) {
                player.sendMessage("§c設定一覧の取得に失敗しました: " + e.getMessage());
                plugin.getLogger().severe(String.format("設定一覧の取得に失敗: %s", e.getMessage()));
            }
        } else if (args.length >= 2 && args[0].equalsIgnoreCase(this.completeList[2]) && args[1].equalsIgnoreCase("add")) {

            if (args.length == 4) {
                // /deathmode config add <array_key> <value>
                String arrayKey = args[2];
                String value = args[3];

                try {
                    addToArray(arrayKey, value);
                    config.saveConfig();

                    player.sendMessage("§a配列に値を追加しました: §6" + arrayKey + " §f+= §a" + value);
                    plugin.getLogger().info(String.format("Player %s added to array: %s += %s", player.getName(), arrayKey, value));
                } catch (Exception e) {
                    player.sendMessage("§c配列への追加に失敗しました: " + e.getMessage());
                    plugin.getLogger().severe(String.format("配列への追加に失敗: %s", e.getMessage()));
                }
            } else {
                player.sendMessage("§cUsage: /deathmode config add <array_key> <value>");
            }
        } else if (args.length >= 2 && args[0].equalsIgnoreCase(this.completeList[2]) && args[1].equalsIgnoreCase("remove")) {

            if (args.length == 4) {
                String arrayKey = args[2];
                String value = args[3];

                try {
                    removeFromArray(arrayKey, value);
                    config.saveConfig();

                    player.sendMessage("§a配列から値を削除しました: §6" + arrayKey + " §f-= §c" + value);
                    plugin.getLogger().info(String.format("Player %s removed from array: %s -= %s", player.getName(), arrayKey, value));
                } catch (Exception e) {
                    player.sendMessage("§c配列からの削除に失敗しました: " + e.getMessage());
                    plugin.getLogger().severe(String.format("配列からの削除に失敗: %s", e.getMessage()));
                }
            } else {
                player.sendMessage("§cUsage: /deathmode config remove <array_key> <value>");
            }
        } else if (args.length >= 2 && args[0].equalsIgnoreCase(this.completeList[2]) && args[1].equalsIgnoreCase("edit")) {

            if (args.length == 4) {
                // /deathmode config edit <key> <value>
                String key = args[2];
                String value = args[3];

                try {
                    // 値の型を推測して設定
                    Object convertedValue = convertStringToAppropriateType(value);
                    config.set(key, convertedValue);
                    config.saveConfig();

                    player.sendMessage("§a設定を更新しました: §6" + key + " §f= §a" + value);
                    plugin.getLogger().info(String.format("Player %s updated config: %s = %s", player.getName(), key, value));
                } catch (Exception e) {
                    player.sendMessage("§c設定の更新に失敗しました: " + e.getMessage());
                    plugin.getLogger().severe(String.format("設定の更新に失敗: %s", e.getMessage()));
                }
            } else {
                player.sendMessage("§cUsage: /deathmode config edit <array_key> <value>");
            }
        }

        if (config.isDebugEnabled()) {
            this.plugin.getLogger().info(String.format("deathmode command executed by %s", sender.getName()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList(this.completeList);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("config")) {
            return Arrays.asList("edit", "add", "remove");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("config") && args[1].equalsIgnoreCase("edit")) {
            // 設定キーの候補を提供
            return Arrays.asList(
                    "deathmode.enabled",
                    "deathmode.enableMessage",
                    "deathmode.disableMessage",
                    "deathmode.deathLogging",
                    "deathmode.autodeathmodeOnDeath",
                    "deathmode.teams",
                    "deathmode.players",
                    "deathmode.BeforeDeathGameMode",
                    "deathmode.AfterDeathGameMode",
                    "messages.noPermission",
                    "messages.playerOnly",
                    "messages.usage",
                    "debug"
            );
        } else if (args.length == 3 && args[0].equalsIgnoreCase("config")
                && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
            // 配列キーの候補を提供
            return Arrays.asList("deathmode.teams", "deathmode.players",
                    "deathmode.BeforeDeathGameMode", "deathmode.AfterDeathGameMode");
        } else if (args.length == 4 && args[0].equalsIgnoreCase("config") && args[1].equalsIgnoreCase("edit")) {
            // boolean値のキーに対する候補
            String key = args[2];
            if (key.contains("enabled") || key.contains("deathLogging")
                    || key.contains("autodeathmodeOnDeath") || key.equals("debug")) {
                return Arrays.asList("true", "false");
            }
        }

        return null;
    }

    private void addToArray(String arrayKey, String value) {
        List<String> currentList = new ArrayList<>();

        switch (arrayKey) {
            case "deathmode.teams":
                currentList = new ArrayList<>(config.getAllowedTeams());
                break;
            case "deathmode.players":
                currentList = new ArrayList<>(config.getAllowedPlayers());
                break;
            case "deathmode.BeforeDeathGameMode":
                currentList = new ArrayList<>(config.getBeforeDeathGameMode());
                break;
            default:
                throw new IllegalArgumentException("サポートされていない配列キーです: " + arrayKey);
        }

        if (!currentList.contains(value)) {
            currentList.add(value);
            config.set(arrayKey, currentList);
        } else {
            throw new IllegalArgumentException("値 '" + value + "' は既に存在します");
        }
    }

    private void removeFromArray(String arrayKey, String value) {
        List<String> currentList = new ArrayList<>();

        switch (arrayKey) {
            case "deathmode.teams":
                currentList = new ArrayList<>(config.getAllowedTeams());
                break;
            case "deathmode.players":
                currentList = new ArrayList<>(config.getAllowedPlayers());
                break;
            case "deathmode.BeforeDeathGameMode":
                currentList = new ArrayList<>(config.getBeforeDeathGameMode());
                break;
            default:
                throw new IllegalArgumentException("Unsupported key: " + arrayKey);
        }

        if (currentList.contains(value)) {
            currentList.remove(value);
            config.set(arrayKey, currentList);
        } else {
            throw new IllegalArgumentException("not found '" + value + "'");
        }
    }

    private Object convertStringToAppropriateType(String value) {
        // boolean値の場合
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return "true".equalsIgnoreCase(value);
        }

        // 整数値の場合
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            // 整数でない場合は次をチェック
        }

        // 小数値の場合
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            // 小数でない場合は文字列として扱う
        }

        // デフォルトは文字列
        return value;
    }

    @org.bukkit.event.EventHandler
    public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent event) {
        Player player = event.getEntity();

        // デスログが無効の場合は何もしない
        if (!config.isDeathLoggingEnabled()) {
            return;
        } else {
            event.setDeathMessage(null);
        }

        // only handle if this player enabled deathmode via our command
        if (!enabledPlayers.contains(player.getUniqueId())) {
            // 自動スペクテイター設定が有効で、プラグインが有効な場合
            if (config.isAutodeathmodeOnDeath() && config.isdeathmodeEnabled()) {
                enabledPlayers.add(player.getUniqueId());
                // 少し遅延してスペクテイターモードに設定（リスポーン後）
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    player.setGameMode(org.bukkit.GameMode.SPECTATOR);
                    player.sendMessage("死亡により自動的にスペクテイターモードになりました");
                }, 20L); // 1秒後
            }
            return;
        }

        // チーム取得
        org.bukkit.scoreboard.Team team = player.getScoreboard().getPlayerTeam(player);
        String teamName = team != null ? team.getName() : "none";

        // ユーザーネーム取得
        String username = player.getName();

        // 現在のゲームモード取得
        org.bukkit.GameMode gameMode = player.getGameMode();

        String logMessage = "death: " + username + ", team: " + teamName + ", gamemode: " + gameMode;

        if (config.isDebugEnabled()) {
            plugin.getServer().broadcastMessage("[DEBUG] " + logMessage);
        } else {
            plugin.getLogger().info(logMessage);
        }
    }

}
