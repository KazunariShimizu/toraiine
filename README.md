# toraiine

## 開発環境

### インストール

下記をインストールします。

- Docker Desktop
- IntelliJ IDEA
- JDK (21以上を推奨)
- Minecraft (Prism Launcher推奨)

### セットアップ

#### データベース

下記コマンドでMariaDBをDockerで立ち上げます。

```bash
cd infra
docker compose up -d
```

3306/tcp でMariaDBが立ち上がります。
設定は `infra/compose.yaml` で定義している内容になります。

| username | password | DB name |
|----------|----------|---------|
| root     | password | iine    |

#### Minecraftクライアント

Debug実行をしたい場合、breakpointでコード実行を一時停止したまま一定時間経過すると  
Timeoutが発生してクラッシュする。  
これを防ぐために、サーバ側のTimeout値とクライアント側のTimeout値を増やす必要がある。  
デフォルトだと、サーバ側は60秒、クライアント側は30秒となっている。

サーバ側は `spigot.yml`の `timeout-time` で大きな秒数を設定している。  
クライアント側はFabricの場合は [TimeOutOut](https://modrinth.com/mod/timeoutout) をインストールし、  
`.minecraft/config/timeoutout.json` の `"readTimeoutSeconds"` を下記のように設定する  
```json
{
  "readTimeoutSeconds": 999999,
}
```

これで、サーバとクライアントのTimeoutを伸ばすことでDebugで一時停止してもTimeoutしなくなる。

#### IntelliJ IDEA

Debug実行は[公式の情報](https://docs.papermc.io/paper/dev/debugging)を参考に設定する。  
`build.gradle`にて、`toraiine-1.0-SNAPSHOT.jar`をbuildして生成した後、自動的に `infra/minecraft_server/plugins/`  
内にコピーするようにしている為、build後はサーバを再起動するだけで新しいコードが読み込まれる。

#### Minecraftサーバ

下記のコマンドで起動する
```bash
cd infra/minecraft_server
./start.sh
```

## 使用方法

papermcの使用を前提とする。
plugins フォルダーにjarを入れて起動する。

ゲーム内で看板の
一行目に[iine]
に行目にメッセージを入力する。
設置するだけでiine看板が出来上がる。

ゲーム内で看板に右クリックでイイネの回数が増加する。

#### コマンド

/iinelist 数字　イイネ看板ののリストが表示される。
/iinetp 数字　IDの数字を入力するとそこにTPされる。
/deliine 数字　IDの数字を入力すると設置者だけが削除することができる。

