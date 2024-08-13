package com.toraden.toraiine;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class ToraIine extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        // もし設定ファイルがまだなければ、デフォルトの設定を保存
        this.saveDefaultConfig();
        // 設定ファイルを読み込む
        FileConfiguration config = this.getConfig();
        DataBase.DB_NAME = config.getString("DATABASE_NAME");

        Server server = this.getServer();
        PluginManager pluginManager = server.getPluginManager();
        pluginManager.registerEvents(new SignPlace(), this);
        Objects.requireNonNull(getCommand("iinelist")).setExecutor(new Commands());
        Objects.requireNonNull(getCommand("iinetp")).setExecutor(new Commands());
        pluginManager.registerEvents(new ClickSign(), this);
        getLogger().info("toraiine 有効");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("toraiine 無効");
    }
}