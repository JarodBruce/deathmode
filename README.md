# DeathMode - Minecraft Plugin

プレイヤーが死亡した際に、指定されたチームまたはプレイヤーのゲームモードを自動的に変更する Minecraft Paper プラグイン (v1.16.5 対応) です。  
PvP やチーム戦などで、死亡したプレイヤーをスペクテイターモードに切り替えて観戦させることができます。

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.16.5-brightgreen.svg)](https://papermc.io/)
[![Java Version](https://img.shields.io/badge/Java-11-orange.svg)](https://adoptopenjdk.net/)

---

## � 機能

- **自動ゲームモード変更**: プレイヤー死亡時に、設定に基づいてゲームモードを自動変更
- **チーム・プレイヤー指定**: 特定のチームやプレイヤーのみを対象にできます
- **柔軟な設定**: JSON 形式の設定ファイルで細かくカスタマイズ可能
- **ログ機能**: 死亡イベントの詳細ログ（チーム、ゲームモードを記録）
- **リアルタイム設定変更**: サーバーを再起動せずに設定を変更・適用可能

---

### 本番環境での利用

1. `target/deathmode-1.0-SNAPSHOT.jar` をサーバーの `plugins` フォルダにコピー
2. サーバーを再起動
3. `plugins/DeathMode/config.json` で設定を調整

---

## ⚙️ 設定

### 設定ファイル (`config.json`)

```json
{
  "deathmode": {
    "enabled": false,
    "teams": ["team1", "team2"],
    "players": ["player1", "player2"],
    "BeforeDeathGameMode": ["survival", "adventure"],
    "AfterDeathGameMode": "spectator",
    "enableMessage": "enabled deathmode",
    "disableMessage": "disabled deathmode",
    "deathLogging": true,
    "autoDeathmodeOnDeath": false
  },
  "debug": false
}
```

### 設定項目の説明

| 項目                   | 説明                         | デフォルト値                |
| ---------------------- | ---------------------------- | --------------------------- |
| `enabled`              | DeathMode の有効/無効        | `false`                     |
| `teams`                | 対象チーム一覧               | `["team1", "team2"]`        |
| `players`              | 対象プレイヤー一覧           | `["player1", "player2"]`    |
| `BeforeDeathGameMode`  | 死亡前のゲームモード（対象） | `["survival", "adventure"]` |
| `AfterDeathGameMode`   | 死亡後のゲームモード         | `"spectator"`               |
| `enableMessage`        | 有効化メッセージ             | `"enabled deathmode"`       |
| `disableMessage`       | 無効化メッセージ             | `"disabled deathmode"`      |
| `deathLogging`         | 死亡ログの記録               | `true`                      |
| `autoDeathmodeOnDeath` | 死亡時の自動モード変更       | `false`                     |
| `debug`                | デバッグモード               | `false`                     |

---

## 🎮 コマンド

### 基本コマンド

```bash
/deathmode enable              # DeathModeを有効化
/deathmode disable             # DeathModeを無効化
/deathmode config              # 設定一覧を表示
```

### 設定変更コマンド

```bash
# 設定値の変更
/deathmode config edit <key> <value>

# 配列への要素追加
/deathmode config add <array_key> <value>

# 配列からの要素削除
/deathmode config remove <array_key> <value>
```

### コマンド例

```bash
# DeathModeを有効化
/deathmode enable

# 対象チームに "pvp_team" を追加
/deathmode config add deathmode.teams pvp_team

# 対象プレイヤーに "Steve" を追加
/deathmode config add deathmode.players Steve

# 死亡後のゲームモードを "creative" に変更
/deathmode config edit deathmode.AfterDeathGameMode creative

# 設定の確認
/deathmode config
```

---

## 🔧 権限

- `deathmode.admin` - 管理者権限（すべてのコマンドの実行が可能）

---

## 📝 使用例

### PvP トーナメントでの利用

1. 参加チームを設定

   ```bash
   /deathmode config add deathmode.teams red_team
   /deathmode config add deathmode.teams blue_team
   ```

2. DeathMode を有効化

   ```bash
   /deathmode enable
   ```

3. プレイヤーが死亡すると自動的にスペクテイターモードに変更され、戦闘から除外されます

### 特定プレイヤーのみ対象にする場合

```bash
/deathmode config add deathmode.players Alice
/deathmode config add deathmode.players Bob
/deathmode enable
```

---

## 🛠️ 開発環境

### プロジェクト構成

```
deathmode/
├── docker-compose.yml         # Docker Compose設定
├── Dockerfile                 # Dockerイメージ定義
├── plugin-src/                # プラグインソースコード
│   ├── pom.xml               # Maven設定
│   └── src/main/java/        # Javaソースコード
│       └── com.github.jarodbruce.deathmode/
│           ├── DeathModePlugin.java    # メインプラグインクラス
│           ├── DeathMode.java          # コマンド・イベント処理
│           ├── Config.java             # 設定管理
│           └── ConfigCommand.java      # 設定コマンド
├── scripts/
│   └── entrypoint.sh          # コンテナ起動スクリプト
└── server-data/               # サーバーデータ（永続化）
    ├── plugins/               # プラグインフォルダ
    └── world/                 # ワールドデータ
```

### ビルド

```bash
mvn clean package
```

## 📄 ライセンス

このプロジェクトは MIT ライセンスの下で公開されています。
