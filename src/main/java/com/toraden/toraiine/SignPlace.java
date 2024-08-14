package com.toraden.toraiine;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.sql.*;
import java.util.Objects;

public class SignPlace implements Listener {
    @EventHandler
    public void PlacePoint(SignChangeEvent signChangeEvent) {
        String signString = signChangeEvent.getLine(0);
        if (Objects.requireNonNull(signString).equals("[iine]")) {
            this.SaveCheckPoint(signChangeEvent);
        }
    }

    public void SaveCheckPoint(SignChangeEvent signChangeEvent) {
        DataBase dataBase = DataBase.getInstance(null, null, null, null);
        Connection con = dataBase.getConnection();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM iine WHERE x = ? AND y = ? AND z = ?");

            statement.setInt(1, signChangeEvent.getBlock().getLocation().getBlockX());
            statement.setInt(2, signChangeEvent.getBlock().getLocation().getBlockY());
            statement.setInt(3, signChangeEvent.getBlock().getLocation().getBlockZ());
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                // 既にレコードが存在するならINSERTしない
                return;
            }
            PreparedStatement psInsert = con.prepareStatement(
                    "INSERT INTO iine (user, world, title, x, y, z, iine, date) values (?, ?, ?, ?, ?, ?, ?, ?)");
            psInsert.setString(1, signChangeEvent.getPlayer().getDisplayName());
            psInsert.setString(2, signChangeEvent.getPlayer().getWorld().getName());

            // 説明変数: getLine(1)じゃ何なのか分からないので変数名で何かを明示する
            String title = signChangeEvent.getLine(1);

            psInsert.setString(3, title);
            psInsert.setInt(4, signChangeEvent.getBlock().getLocation().getBlockX());
            psInsert.setInt(5, signChangeEvent.getBlock().getLocation().getBlockY());
            psInsert.setInt(6, signChangeEvent.getBlock().getLocation().getBlockZ());
            psInsert.setInt(7, 0);
            psInsert.setTimestamp(8, new Timestamp(System.currentTimeMillis()));

            psInsert.execute();
            signChangeEvent.getPlayer().sendMessage("イイネポイントを設置しました");
            signChangeEvent.setLine(0, "[iine]");
            signChangeEvent.setLine(1, ChatColor.BOLD + "★いいねしてね！★");
            signChangeEvent.setLine(2, ChatColor.BOLD + title);
            signChangeEvent.setLine(3, ChatColor.AQUA + "★イイネ!0");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
