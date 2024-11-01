package com.toraden.toraiine;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.Objects;

public class SignDel {
    public boolean Del(Player player, int id) throws ClassNotFoundException {
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

            PreparedStatement statement = con.prepareStatement("SELECT * FROM iine WHERE id = ? AND user = ? ");
            statement.setInt(1, id);
            statement.setString(2, Objects.requireNonNull(player.getPlayer()).getDisplayName());

            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return false;
            }

            try {
                double x = rs.getInt("x");
                double y = rs.getInt("y");
                double z = rs.getInt("z");

                // 座標にあるブロックを取得して削除
                World world = Bukkit.getWorld(rs.getString("world"));
                Location location = new Location(world, x, y, z);
                Block block = Objects.requireNonNull(world).getBlockAt(location);

                if (block.getType() != Material.AIR) {
                    block.setType(Material.AIR);  // ブロックを壊す

                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
                return false;
            }
            statement = con.prepareStatement(
                    "DELETE From iine WHERE id = ? AND user = ? ");
            statement.setInt(1, id);
            statement.setString(2, Objects.requireNonNull(player.getPlayer()).getDisplayName());
            statement.execute();

        } catch (
        SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}
