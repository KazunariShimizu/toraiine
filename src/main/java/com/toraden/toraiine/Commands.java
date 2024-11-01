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

import java.sql.*;
import static org.bukkit.Bukkit.*;

public class Commands implements CommandExecutor {
    @Override
    public boolean onCommand( CommandSender sender, Command command,  String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "iinelist":
                if (args.length >= 2) {
                    sender.sendMessage("/iinelist 【番号】の形式で入力してください");
                    sender.sendMessage("例: /iinelist 3");
                    return false;
                }

                if (args.length == 1) {
                    try {
                        this.showIineList((Player)sender, Integer.parseInt(args[0]));
                    } catch (NumberFormatException | ClassNotFoundException e) {
                        // TODO 例外をログに出すなり将来なにか対応する
                        // e.printStackTrace();
                        sender.sendMessage("/iinelist 【番号】の形式で入力してください");
                        sender.sendMessage("例: /iinelist 3");
                        return false;
                    }
                } else {
                    try {
                        this.showIineList((Player) sender, 0);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
            case "iinetp":
                if (args.length != 1) {
                    sender.sendMessage("/iinetp 【ID番号】の形式で入力してください");
                    sender.sendMessage("例: /iinetp 3");
                    return false;
                }
                try {
                    this.teleport((Player)sender, Integer.parseInt(args[0]));
                } catch (NumberFormatException e) {
                    // TODO 例外をログに出すなり将来なにか対応する
                    // e.printStackTrace();
                    sender.sendMessage("/iinetp 【番号】の形式で入力してください");
                    sender.sendMessage("例: /iinetp 3");
                    return false;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "deliine":
                if (args.length != 1) {
                    sender.sendMessage("/deliine 【ID番号】の形式で入力してください");
                    sender.sendMessage("例: /deliine 3");
                    return false;
                }
                try {
                    SignDel decline = new SignDel();
                    if(decline.Del((Player)sender, Integer.parseInt(args[0]))){
                        sender.sendMessage( "iine id "+Integer.parseInt(args[0])+"を削除しました");
                    }
                } catch (NumberFormatException e) {
                    // TODO 例外をログに出すなり将来なにか対応する
                    // e.printStackTrace();
                    sender.sendMessage("iineIdの所持権限がないかコマンドが間違っています。/deliine 【番号】の形式で入力してください");
                    sender.sendMessage("例: /deliine 3");
                    return false;
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
        }
        // コマンド実行成功を意味する
        return true;
    }

    /*
     * Playerに「いいね」の一覧を表示する
     * @param player 「いいね」の一覧を表示するPlayer
     * @param id 「いいね」一覧の何番目から表示するか
     */
    public void showIineList(Player player, int id) throws ClassNotFoundException {
        String DATABASE_NAME = DataBase.DB_NAME;
        String URL = "jdbc:mysql://"+DataBase.URL+"/%s".formatted(DATABASE_NAME);
        //DB接続用・ユーザ定数
        String USER = DataBase.USER;
        String PASS = DataBase.PASS;
        Connection con = null;
        Statement stmt = null;
        try {
            //MySQL に接続する
            Class.forName("com.mysql.cj.jdbc.Driver");
            //データベースに接続
            con = DriverManager.getConnection(URL, USER, PASS);

            PreparedStatement statement = con.prepareStatement("SELECT * FROM iine WHERE id >= ? LIMIT 10;");
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            int endId = 0;
            while (rs.next()) {
                BaseComponent message = new TextComponent("ID" + rs.getString("id") +
                        " " + rs.getString("user") + " " + rs.getString("title") +
                        "★iine" + rs.getString("iine") + " " + rs.getString("x") +
                        " " + rs.getString("y") + " " + rs.getString("z"));
                player.spigot().sendMessage(message);
                endId = Integer.parseInt(rs.getString("id"));
            }
            TextComponent message = new TextComponent("▼次へ");
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/iinelist " + endId));
            player.spigot().sendMessage(message);
        } catch (SQLException e) {
            // TODO 例外をログに出すなり将来なにか対応する
            // e.printStackTrace();
        }
    }

    public void teleport(Player player, int id) throws ClassNotFoundException {
        String DATABASE_NAME = DataBase.DB_NAME;
        String URL = "jdbc:mysql://"+DataBase.URL+"/%s".formatted(DATABASE_NAME);
        //DB接続用・ユーザ定数
        String USER = DataBase.USER;
        String PASS = DataBase.PASS;
        Connection con = null;
        Statement stmt = null;
        try {
            //MySQL に接続する
            Class.forName("com.mysql.cj.jdbc.Driver");
            //データベースに接続
            con = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement psSelect = con.prepareStatement("SELECT * FROM iine WHERE id = ?");
            psSelect.setInt(1, id);
            ResultSet rs = psSelect.executeQuery();

            if (!rs.next()) {
                player.sendMessage("テレポートに失敗しました");
                return;
            }

            int x = Integer.parseInt(rs.getString("x"));
            int y = Integer.parseInt(rs.getString("y"));
            int z = Integer.parseInt(rs.getString("z"));
            World world = org.bukkit.Bukkit.getServer().getWorld(rs.getString("world"));
            BaseComponent message = new TextComponent("ID" + rs.getString("id") + " " +
                    rs.getString("user") + " " + rs.getString("title") + " " +
                    rs.getString("x") + " " + rs.getString("y") + " " +
                    rs.getString("z") + " に到着！");
            player.spigot().sendMessage(message);
            Location loc = new Location(world, x, y, z);
            player.teleport(loc);
        } catch (SQLException e) {
            // TODO 例外をログに出すなり将来なにか対応する
            // e.printStackTrace();
        }
    }
}
