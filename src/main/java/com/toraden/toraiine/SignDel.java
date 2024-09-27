package com.toraden.toraiine;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class SignDel {
    public boolean Del(Player player, int id){
        DataBase dataBase = DataBase.getInstance(null, null, null, null);
        try {
            Connection con = dataBase.getConnection();

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
