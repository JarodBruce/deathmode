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

        Player player = (Player) sender;

        if (args.length == 1 && args[0].equalsIgnoreCase(this.completeList[0])) {
            try {
                // 値の型を推測して設定
                Object convertedValue = convertStringToAppropriateType("true");
                config.set("deathmode.enabled", convertedValue);
                config.saveConfig();
                plugin.getServer().broadcastMessage("deathmode is enabled");
            } catch (Exception e) {
                player.sendMessage("§c設定の更新に失敗しました: " + e.getMessage());
                plugin.getLogger().severe(String.format("設定の更新に失敗: %s", e.getMessage()));
            }

        } else if (args.length == 1 && args[0].equalsIgnoreCase(this.completeList[1])) {
            try {
                // 値の型を推測して設定
                Object convertedValue = convertStringToAppropriateType("false");
                config.set("deathmode.enabled", convertedValue);
                config.saveConfig();
                plugin.getServer().broadcastMessage("deathmode is disabled");
            } catch (Exception e) {
                player.sendMessage("§c設定の更新に失敗しました: " + e.getMessage());
                plugin.getLogger().severe(String.format("設定の更新に失敗: %s", e.getMessage()));
            }

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
        // チーム取得
        org.bukkit.scoreboard.Team team = player.getScoreboard().getPlayerTeam(player);
        String teamName = team != null ? team.getName() : "none";

        // ユーザーネーム取得
        String username = player.getName();

        // 現在のゲームモード取得
        org.bukkit.GameMode gameMode = player.getGameMode();

        String logMessage = "death: " + username + ", team: " + teamName + ", gamemode: " + gameMode;

        if (config.isDebugEnabled()) {
            plugin.getLogger().info(logMessage);
        }

        if (config.getAllowedPlayers().contains(username) || config.getAllowedTeams().contains(teamName)) {
            if (config.getBeforeDeathGameMode().stream().map(Object::toString).anyMatch(s -> s.equalsIgnoreCase(gameMode.name()))) {
                Object after = config.getAfterDeathGameMode();
                String mode = (after instanceof java.util.List && !((java.util.List<?>) after).isEmpty())
                        ? String.valueOf(((java.util.List<?>) after).get(0))
                        : String.valueOf(after);
                try {
                    player.setGameMode(org.bukkit.GameMode.valueOf(mode.toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    plugin.getLogger().warning("Invalid AfterDeathGameMode: " + mode);
                }
            }
        }
    }

}
