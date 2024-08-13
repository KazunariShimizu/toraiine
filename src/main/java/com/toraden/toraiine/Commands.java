package com.toraden.toraiine;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

import static org.bukkit.Bukkit.*;


public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("iinelist")) {
            if (args.length != 0) {
                int id = Integer.parseInt(args[0]);
                Player p = (Player) sender;
                sendMessage(p, id);
            } else {
                Player p = (Player) sender;
                sendMessage(p, 0);
            }
        } else if (command.getName().equalsIgnoreCase("iinetp")) {
            if (args.length != 0) {
                int id = Integer.parseInt(args[0]);
                Player p = (Player) sender;
                teleport(p, id);
            }
        }
        return false;
    }

    public void sendMessage(Player p, int id) {
        String DATABASE_NAME = DataBase.DB_NAME;
        String URL = "jdbc:mysql://" + DataBase.URL + "/%s".formatted(DATABASE_NAME);
        //DB接続用・ユーザ定数
        String USER = DataBase.USER;
        String PASS = DataBase.PASS;

        Connection con;
        try {
            //MySQL に接続する
            Class.forName("com.mysql.cj.jdbc.Driver");
            //データベースに接続
            con = DriverManager.getConnection(URL, USER, PASS);

            //処理
            String sqlSelect = "SELECT * FROM iine where id >= ? LIMIT 10;";
            PreparedStatement psSelect = con.prepareStatement(sqlSelect);
            psSelect.setInt(1, id);
            ResultSet rs = psSelect.executeQuery();
            // データベースに対する処理
            int endId = 0;
            while (rs.next()) {
                BaseComponent message = new TextComponent("ID" + rs.getString("id") + " " + rs.getString("user") + " " + rs.getString("title") + "★iine" + rs.getString("iine") + " " + rs.getString("x") + " " + rs.getString("y") + " " + rs.getString("z"));
                p.spigot().sendMessage(message);
                endId = Integer.parseInt(rs.getString("id"));
            }
            TextComponent message = new TextComponent("▼次へ");
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/iinelist " + endId));
            p.spigot().sendMessage(message);
        } catch (SQLException | ClassNotFoundException error) {
            error.printStackTrace();
        }
    }

    public void teleport(Player p, int id) {
        String DATABASE_NAME = DataBase.DB_NAME;
        String URL = "jdbc:mysql://" + DataBase.URL + "/%s".formatted(DATABASE_NAME);
        //DB接続用・ユーザ定数
        String USER = DataBase.USER;
        String PASS = DataBase.PASS;

        Connection con;
        try {
            //MySQL に接続する
            Class.forName("com.mysql.cj.jdbc.Driver");
            //データベースに接続
            con = DriverManager.getConnection(URL, USER, PASS);

            //処理
            String sqlSelect = "SELECT * FROM iine Where id = ? ;";
            PreparedStatement psSelect = con.prepareStatement(sqlSelect);
            psSelect.setInt(1, id);
            ResultSet rs = psSelect.executeQuery();
            // データベースに対する処理
            if (rs.next()) {
                int x = Integer.parseInt(rs.getString("x"));
                int y = Integer.parseInt(rs.getString("y"));
                int z = Integer.parseInt(rs.getString("z"));
                World w = getServer().getWorld(rs.getString("world"));
                BaseComponent message = new TextComponent("ID" + rs.getString("id") + " " + rs.getString("user") + " " + rs.getString("title") + " " + rs.getString("x") + " " + rs.getString("y") + " " + rs.getString("z") + " に到着！");
                p.spigot().sendMessage(message);
                Location loc = new Location(w, x, y, z);
                p.teleport(loc);
            }
        } catch (SQLException | ClassNotFoundException error) {
            error.printStackTrace();
        }
    }
}
