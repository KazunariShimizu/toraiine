package com.toraden.toraiine;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.sql.*;
import java.util.Objects;

public class ClickSign implements Listener {
    @EventHandler
    public void Iine(PlayerInteractEvent playerInteractEvent) throws ClassNotFoundException {
        if (playerInteractEvent.getAction() == Action.LEFT_CLICK_BLOCK ||
                playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR ||
                playerInteractEvent.getAction() == Action.LEFT_CLICK_AIR) {
            return;
        }

        // Playerが右クリックしたブロックのステート（状態）を取得
        BlockState blockState = Objects.requireNonNull(playerInteractEvent.getClickedBlock()).getState();
        if (!(blockState instanceof Sign signBoard)) {
            // 看板でなければ処理しないので早期return
            return;
        }
        // 看板である場合
        Location signBoardLocation = signBoard.getLocation();
        SignSide frontSignSide = signBoard.getSide(Side.FRONT);

        if (!(frontSignSide.getLine(0).equals("[iine]"))) {
            // 看板の1行目が[iine]でなければ処理しないので早期return
            return;
        }

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

            PreparedStatement statement = con.prepareStatement("SELECT * FROM iine WHERE x = ? AND y = ? AND z = ?");
            statement.setInt(1, signBoardLocation.getBlockX());
            statement.setInt(2, signBoardLocation.getBlockY());
            statement.setInt(3, signBoardLocation.getBlockZ());
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return;
            }

            doIine(signBoard, playerInteractEvent.getPlayer(), rs, con, frontSignSide);
            playerInteractEvent.setCancelled(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void doIine(Sign signBoard, Player player, ResultSet rs, Connection con, SignSide frontSignSide) throws SQLException {
        player.sendMessage(ChatColor.BOLD + rs.getString("user") + "さんが立てた" +
                rs.getString("title") + "看板がありました!");
        int iineCount = Integer.parseInt(rs.getString("iine")) + 1;
        PreparedStatement psUpdate = con.prepareStatement("UPDATE iine SET iine = ? where id = ?");
        psUpdate.setInt(1, iineCount);
        psUpdate.setInt(2, rs.getInt("id"));
        psUpdate.executeUpdate();
        frontSignSide.setLine(1, ChatColor.BOLD + "★ID" + rs.getString("id"));
        frontSignSide.setLine(2, ChatColor.BOLD + rs.getString("title"));
        frontSignSide.setLine(3, ChatColor.AQUA + "★イイネ!" + iineCount);
        signBoard.update();
        player.sendMessage(ChatColor.BOLD + "iineしました!");
    }
}
