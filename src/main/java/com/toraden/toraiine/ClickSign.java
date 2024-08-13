package com.toraden.toraiine;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.sql.*;

public class ClickSign implements Listener {

    @EventHandler
    public void Iine(PlayerInteractEvent e) {
        Player player = e.getPlayer();  //プレイヤー名を変数playerに代入
        String world_name = player.getWorld().getName();
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_AIR) return;
        if (e.getClickedBlock().getState() instanceof Sign) {
            if (e.getAction() == Action.LEFT_CLICK_BLOCK) return;

            //クリックしたブロックを取得・状態を取得・それが看板ならば
            Sign signboard = (Sign) e.getClickedBlock().getState();
            Location location = (Location) e.getClickedBlock().getLocation();
            //Sign型（看板型）の変数signboardに看板の情報を代入
            //本来は型が違うが(Sign)とつけて看板型に変更している　　　　
            String signString = signboard.getLine(0);

            //看板の２行目を取得（０から数える）
            if (signString.equals("[iine]")) {
                DataBase dataBase = DataBase.getInstance(null, null, null, null);
                try {
                    Connection con = dataBase.getConnection();

                    String sqlSelect = "SELECT * FROM iine where x = ? and y = ? and z = ? ;";
                    PreparedStatement psSelect = con.prepareStatement(sqlSelect);
                    psSelect.setInt(1, location.getBlockX());
                    psSelect.setInt(2, location.getBlockY());
                    psSelect.setInt(3, location.getBlockZ());
                    ResultSet rs = psSelect.executeQuery();

                    // データベースに対する処理
                    String sqlUpdate = "UPDATE iine SET  iine = ? where id = ? ;";
                    PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);
                    if (rs.next()) {
                        if (world_name.equals(rs.getString("world"))) {
                            player.sendMessage(ChatColor.BOLD + rs.getString("user") + "さんが立てた" + rs.getString("title") + "看板がありました!");
                            int iineOK = Integer.parseInt(rs.getString("iine")) + 1;
                            psUpdate.setString(1, String.valueOf(iineOK));
                            psUpdate.setString(2, rs.getString("id"));
                            psUpdate.executeUpdate();
                            signboard.setLine(0, "[iine]");
                            signboard.setLine(1, ChatColor.BOLD + "★ID" + rs.getString("id"));
                            signboard.setLine(2, ChatColor.BOLD + rs.getString("title"));
                            signboard.setLine(3, ChatColor.AQUA + "★イイネ!" + iineOK);
                            signboard.update();
                            player.sendMessage(ChatColor.BOLD + "iineしました!");
                        }
                    }
                    e.setCancelled(true);
                } catch (SQLException error) {
                    error.printStackTrace();
                }
            }

        }

    }
}
