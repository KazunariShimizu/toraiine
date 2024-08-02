package com.toraden.toraiine;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.sql.*;

public class ClickSign implements Listener {

    @EventHandler
    public void Iine(PlayerInteractEvent e){
        Player player = e.getPlayer();//プレイヤー名を変数playerに代入
        String player_name = e.getPlayer().getDisplayName();
        World world = player.getWorld();//プレイヤーのいるワールドを変数worldに代入
        String world_name = player.getWorld().getName();
        Action action = e.getAction();//プレイヤーが行った動作を変数actionに代入
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_AIR) return;
        if(e.getClickedBlock().getState() instanceof Sign ) {
                if (e.getAction() == Action.LEFT_CLICK_BLOCK) return;

                //クリックしたブロックを取得・状態を取得・それが看板ならば
                Sign signboard = (Sign) e.getClickedBlock().getState();
                Location location = (Location) e.getClickedBlock().getLocation();
                //Sign型（看板型）の変数signboardに看板の情報を代入
                //本来は型が違うが(Sign)とつけて看板型に変更している　　　　
                String signString = signboard.getLine(0);

                //看板の２行目を取得（０から数える）
                if (signString.equals("[iine]")) {
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
                        String sqlUpdate = "UPDATE iine SET  iine = ? where id = ? ;";
                        PreparedStatement psUpdate = con.prepareStatement(sqlUpdate);
                        if (rs.next()) {
                            if (world_name.equals(rs.getString("world"))) {
                                player.sendMessage(ChatColor.BOLD + rs.getString("user") + "さんが立てた" + rs.getString("title") + "看板がありました!");
                                int iineok = Integer.parseInt(rs.getString("iine")) + 1;
                                psUpdate.setString(1, String.valueOf(iineok));
                                psUpdate.setString(2, rs.getString("id"));
                                psUpdate.executeUpdate();
                                signboard.setLine(0, "[iine]");
                                signboard.setLine(1, ChatColor.BOLD + "★ID" + rs.getString("id"));
                                signboard.setLine(2, ChatColor.BOLD + rs.getString("title"));
                                signboard.setLine(3, ChatColor.AQUA + "★イイネ!" + iineok);
                                signboard.update();
                                player.sendMessage(ChatColor.BOLD + "iineしました!");
                            }
                        }
                        e.setCancelled(true);
                        return;
                    } catch (SQLException | ClassNotFoundException error) {
                        error.printStackTrace();
                    }
                }

        }

    }
}
