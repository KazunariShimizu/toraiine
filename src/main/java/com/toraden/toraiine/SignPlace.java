package com.toraden.toraiine;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.sql.*;
import java.util.Objects;

public class SignPlace implements Listener {
    @EventHandler
    public void PlacePoint(SignChangeEvent signChangeEvent) {
        Player player = signChangeEvent.getPlayer();
        String player_name = signChangeEvent.getPlayer().getDisplayName();
        String world_name = player.getWorld().getName();
        Block block = signChangeEvent.getBlock();
        Location loc = block.getLocation();

        String signString = signChangeEvent.getLine(0);
        String titleString = signChangeEvent.getLine(1);

        if (Objects.requireNonNull(signString).contains("[iine]")) {
            SaveCheckPoint(signChangeEvent, player, player_name, titleString, loc, world_name);
        }
    }

    public void SaveCheckPoint(SignChangeEvent signChangeEvent, Player player, String player_name, String titleString, Location location, String world_name) {

        DataBase dataBase = DataBase.getInstance(null, null, null, null);
        Connection con = dataBase.getConnection();
        try {
            String sqlSelect = "SELECT * FROM iine where x = ? and y = ? and z = ? ;";
            PreparedStatement psSelect = con.prepareStatement(sqlSelect);

            psSelect.setInt(1, location.getBlockX());
            psSelect.setInt(2, location.getBlockY());
            psSelect.setInt(3, location.getBlockZ());
            ResultSet rs = psSelect.executeQuery();
            // データベースに対する処理
            String sqlInsert = "INSERT INTO iine (user,world,title,x,y,z,iine,date) values (?,?,?,?,?,?,?,?);";
            PreparedStatement psInsert = con.prepareStatement(sqlInsert);
            if (!rs.next()) {
                psInsert.setString(1, String.valueOf(player_name));
                psInsert.setString(2, String.valueOf(world_name));
                psInsert.setString(3, String.valueOf(titleString));
                psInsert.setInt(4, location.getBlockX());
                psInsert.setInt(5, location.getBlockY());
                psInsert.setInt(6, location.getBlockZ());
                psInsert.setInt(7, 0);
                psInsert.setTimestamp(8, new Timestamp(System.currentTimeMillis()));

                psInsert.execute();
                player.sendMessage("イイネポイントを設置しました");
                signChangeEvent.setLine(0, "[iine]");
                signChangeEvent.setLine(1, ChatColor.BOLD + "★いいねしてね！★");
                signChangeEvent.setLine(2, ChatColor.BOLD + titleString);
                signChangeEvent.setLine(3, ChatColor.AQUA + "★イイネ!0");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
