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
    public void PlacePoint(SignChangeEvent e){
        Player player = e.getPlayer();
        String player_name = e.getPlayer().getDisplayName();
        String world_name = player.getWorld().getName();
        Block block = e.getBlock();
        Location loc = block.getLocation();


        String signString = e.getLine(0);
        String titleString = e.getLine(1);

        if(Objects.requireNonNull(signString).contains("[iine]")) {
            SaveCheckPoint(e,player,player_name,titleString,loc, world_name);
        }

    }
    public void SaveCheckPoint(SignChangeEvent e, Player player, String player_name, String titleString , Location location , String world_name){

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

            //処理
            String sqlSelect = "SELECT * FROM iine where x = ? and y = ? and z = ? ;";
            PreparedStatement psSelect = con.prepareStatement(sqlSelect);

            psSelect.setInt(1, location.getBlockX());
            psSelect.setInt(2, location.getBlockY());
            psSelect.setInt(3, location.getBlockZ());
            ResultSet rs = psSelect.executeQuery();
            // データベースに対する処理
            String sqlInsert = "INSERT INTO iine (user,world,title,x,y,z,iine,date) values (?,?,?,?,?,?,?,?);";
            PreparedStatement psInsert = con.prepareStatement(sqlInsert);
            if(!rs.next()) {
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
                e.setLine(0,"[iine]");
                e.setLine(1,ChatColor.BOLD + "★いいねしてね！★");
                e.setLine(2,ChatColor.BOLD + titleString);
                e.setLine(3,ChatColor.AQUA + "★イイネ!0");
            }
        } catch (SQLException | ClassNotFoundException error) {
            error.printStackTrace();
        }
    }
}
