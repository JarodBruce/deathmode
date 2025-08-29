package com.github.jarodbruce.deathmode;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Config {

    private final JavaPlugin plugin;
    private JsonObject config;
    private final Gson gson;
    private final String configFileName = "config.json";

    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gson = new Gson();
        initializeConfig();
    }

    /**
     * 設定ファイルを読み込む プラグインフォルダに設定ファイルがない場合は、リソースからコピーする
     */
    private void initializeConfig() {
        loadConfig();
    }

    public void loadConfig() {
        try {
            // プラグインフォルダのパス
            Path configPath = Paths.get(plugin.getDataFolder().getAbsolutePath(), configFileName);

            // プラグインフォルダが存在しない場合は作成
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            // 設定ファイルが存在しない場合は、リソースからコピー
            if (!Files.exists(configPath)) {
                copyDefaultConfig(configPath);
            }

            // 設定ファイルを読み込み
            String content = new String(Files.readAllBytes(configPath), StandardCharsets.UTF_8);
            config = gson.fromJson(content, JsonObject.class);

            plugin.getLogger().info("設定ファイルを読み込みました: " + configPath);
        } catch (IOException e) {
            plugin.getLogger().severe(String.format("設定ファイルの読み込みに失敗しました: %s", e.getMessage()));
            // デフォルト設定を使用
            config = new JsonObject();
        } catch (Exception e) {
            plugin.getLogger().severe(String.format("設定ファイルの解析に失敗しました: %s", e.getMessage()));
            // デフォルト設定を使用
            config = new JsonObject();
        }
    }

    /**
     * リソースからデフォルト設定ファイルをコピー
     */
    private void copyDefaultConfig(Path targetPath) throws IOException {
        try (InputStream inputStream = plugin.getResource(configFileName)) {
            if (inputStream != null) {
                Files.copy(inputStream, targetPath);
                plugin.getLogger().info("デフォルト設定ファイルをコピーしました");
            } else {
                plugin.getLogger().warning("デフォルト設定ファイルが見つかりません");
            }
        }
    }

    /**
     * 設定ファイルを保存
     */
    public void saveConfig() {
        try {
            Path configPath = Paths.get(plugin.getDataFolder().getAbsolutePath(), configFileName);
            String json = gson.toJson(config);
            Files.write(configPath, json.getBytes(StandardCharsets.UTF_8));
            plugin.getLogger().info("設定ファイルを保存しました");
        } catch (IOException e) {
            plugin.getLogger().severe(String.format("設定ファイルの保存に失敗しました: %s", e.getMessage()));
        }
    }

    /**
     * 設定ファイルを再読み込み
     */
    public void reloadConfig() {
        loadConfig();
    }

    // deathmode設定の取得メソッド
    public boolean isdeathmodeEnabled() {
        return getBoolean("deathmode.enabled", false);
    }

    public String getEnableMessage() {
        return getString("deathmode.enableMessage", "deathmode enabled");
    }

    public String getDisableMessage() {
        return getString("deathmode.disableMessage", "deathmode disabled");
    }

    public boolean isDeathLoggingEnabled() {
        return getBoolean("deathmode.deathLogging", true);
    }

    public boolean isAutodeathmodeOnDeath() {
        return getBoolean("deathmode.autodeathmodeOnDeath", false);
    }

    public List<String> getAllowedTeams() {
        return getStringList("deathmode.teams", Arrays.asList("team1", "team2"));
    }

    public List<String> getAllowedPlayers() {
        return getStringList("deathmode.players", Arrays.asList("player1", "player2"));
    }

    public List<String> getBeforeDeathGameMode() {
        return getStringList("deathmode.BeforeDeathGameMode", Arrays.asList("survival"));
    }

    public String getAfterDeathGameMode() {
        return getString("deathmode.AfterDeathGameMode", "spectator");
    }

    // デバッグ設定
    public boolean isDebugEnabled() {
        return getBoolean("debug", false);
    }

    // 汎用的な設定取得メソッド
    private String getString(String path, String defaultValue) {
        try {
            String[] keys = path.split("\\.");
            JsonObject current = config;

            for (int i = 0; i < keys.length - 1; i++) {
                if (current.has(keys[i]) && current.get(keys[i]).isJsonObject()) {
                    current = current.getAsJsonObject(keys[i]);
                } else {
                    return defaultValue;
                }
            }

            String lastKey = keys[keys.length - 1];
            if (current.has(lastKey) && current.get(lastKey).isJsonPrimitive()) {
                return current.get(lastKey).getAsString();
            }
        } catch (Exception e) {
            plugin.getLogger().warning(String.format("設定値の取得に失敗しました: %s - %s", path, e.getMessage()));
        }
        return defaultValue;
    }

    private boolean getBoolean(String path, boolean defaultValue) {
        try {
            String[] keys = path.split("\\.");
            JsonObject current = config;

            for (int i = 0; i < keys.length - 1; i++) {
                if (current.has(keys[i]) && current.get(keys[i]).isJsonObject()) {
                    current = current.getAsJsonObject(keys[i]);
                } else {
                    return defaultValue;
                }
            }

            String lastKey = keys[keys.length - 1];
            if (current.has(lastKey) && current.get(lastKey).isJsonPrimitive()) {
                return current.get(lastKey).getAsBoolean();
            }
        } catch (Exception e) {
            plugin.getLogger().warning(String.format("設定値の取得に失敗しました: %s - %s", path, e.getMessage()));
        }
        return defaultValue;
    }

    private List<String> getStringList(String path, List<String> defaultValue) {
        try {
            String[] keys = path.split("\\.");
            JsonObject current = config;

            for (int i = 0; i < keys.length - 1; i++) {
                if (current.has(keys[i]) && current.get(keys[i]).isJsonObject()) {
                    current = current.getAsJsonObject(keys[i]);
                } else {
                    return defaultValue;
                }
            }

            String lastKey = keys[keys.length - 1];
            if (current.has(lastKey) && current.get(lastKey).isJsonArray()) {
                List<String> result = new ArrayList<>();
                for (JsonElement element : current.getAsJsonArray(lastKey)) {
                    if (element.isJsonPrimitive()) {
                        result.add(element.getAsString());
                    }
                }
                return result;
            }
        } catch (Exception e) {
            plugin.getLogger().warning(String.format("設定値の取得に失敗しました: %s - %s", path, e.getMessage()));
        }
        return defaultValue;
    }

    public void set(String path, Object value) {
        try {
            String[] keys = path.split("\\.");
            JsonObject current = config;

            // 最後のキー以外をたどって、必要に応じてオブジェクトを作成
            for (int i = 0; i < keys.length - 1; i++) {
                if (!current.has(keys[i]) || !current.get(keys[i]).isJsonObject()) {
                    current.add(keys[i], new JsonObject());
                }
                current = current.getAsJsonObject(keys[i]);
            }

            // 最後のキーに値を設定
            String lastKey = keys[keys.length - 1];
            current.add(lastKey, gson.toJsonTree(value));
        } catch (Exception e) {
            plugin.getLogger().warning(String.format("設定値の設定に失敗しました: %s - %s", path, e.getMessage()));
        }
    }

    public List<String> getConfigList() {
        List<String> configList = new ArrayList<>();
        if (config != null) {
            traverseConfig(config, "", configList);
        }
        return configList;
    }

    private void traverseConfig(JsonObject jsonObject, String prefix, List<String> configList) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            String currentPath = prefix.isEmpty() ? key : prefix + "." + key;

            if (value.isJsonObject()) {
                // ネストしたオブジェクトの場合、再帰的に処理
                traverseConfig(value.getAsJsonObject(), currentPath, configList);
            } else {
                // 値がある場合、リストに追加
                String valueStr;
                if (value.isJsonPrimitive()) {
                    valueStr = value.getAsString();
                } else {
                    valueStr = value.toString();
                }
                configList.add("§6" + currentPath + "§f: §a" + valueStr);
            }
        }
    }
}
