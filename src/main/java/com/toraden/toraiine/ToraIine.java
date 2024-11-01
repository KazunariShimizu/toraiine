package com.toraden.toraiine;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@SuppressWarnings("unused")
public final class ToraIine extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        // もし設定ファイルがまだなければ、デフォルトの設定を保存
        this.saveDefaultConfig();
        // 設定ファイルを読み込む
        FileConfiguration conf=getConfig();
        DataBase.DB_NAME = conf.getString("DATABASE_NAME");
        DataBase.URL = conf.getString("URL");
        DataBase.USER = conf.getString("USER");
        DataBase.PASS = conf.getString("PASS");

        Server server = this.getServer();
        PluginManager pluginManager = server.getPluginManager();
        pluginManager.registerEvents(new SignPlace(), this);
        pluginManager.registerEvents(new ClickSign(), this);
        Objects.requireNonNull(getCommand("iinelist")).setExecutor(new Commands());
        Objects.requireNonNull(getCommand("iinetp")).setExecutor(new Commands());
        Objects.requireNonNull(getCommand("deliine")).setExecutor(new Commands());
        getLogger().info("toraiine 有効");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("toraiine 無効");
    }
}