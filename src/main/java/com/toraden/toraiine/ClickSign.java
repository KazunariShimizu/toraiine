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
    public void Iine(PlayerInteractEvent playerInteractEvent) {
        Player player = playerInteractEvent.getPlayer();
        if (playerInteractEvent.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }
        String world_name = player.getWorld().getName();

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

        DataBase dataBase = DataBase.getInstance(null, null, null, null);
        try {
            Connection con = dataBase.getConnection();

            PreparedStatement statement = con.prepareStatement("SELECT * FROM iine WHERE x = ? AND y = ? AND z = ?");
            statement.setInt(1, signBoardLocation.getBlockX());
            statement.setInt(2, signBoardLocation.getBlockY());
            statement.setInt(3, signBoardLocation.getBlockZ());
            ResultSet rs = statement.executeQuery();

            // データベースに対する処理
            String sqlUpdate = "UPDATE iine SET iine = ? where id = ?";
            PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);
            if (rs.next()) {
                if (world_name.equals(rs.getString("world"))) {
                    player.sendMessage(ChatColor.BOLD + rs.getString("user") + "さんが立てた" + rs.getString("title") + "看板がありました!");
                    int iineOK = Integer.parseInt(rs.getString("iine")) + 1;
                    psUpdate.setString(1, String.valueOf(iineOK));
                    psUpdate.setString(2, rs.getString("id"));
                    psUpdate.executeUpdate();
                    frontSignSide.setLine(0, "[iine]");
                    frontSignSide.setLine(1, ChatColor.BOLD + "★ID" + rs.getString("id"));
                    frontSignSide.setLine(2, ChatColor.BOLD + rs.getString("title"));
                    frontSignSide.setLine(3, ChatColor.AQUA + "★イイネ!" + iineOK);
                    signBoard.update();
                    player.sendMessage(ChatColor.BOLD + "iineしました!");
                }
            }
            playerInteractEvent.setCancelled(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
