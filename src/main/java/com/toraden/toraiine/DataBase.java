package com.toraden.toraiine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private static DataBase instance;
    private static Connection con;

    // シングルトン用コンストラクタ
    private DataBase(String dbName, String dbUrl, String dbUser, String dbPassword) {
        try {
            // Load JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // データベースに接続しconnectionオブジェクトを取得する
            con = DriverManager.getConnection("jdbc:mysql://%s/%s".formatted(dbUrl, dbName), dbUser, dbPassword);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static DataBase getInstance(String dbName, String dbUrl, String dbUser, String dbPassword) {
        if (instance == null) {
            instance = new DataBase(dbName, dbUrl, dbUser, dbPassword);
        }
        return instance;
    }

    public Connection getConnection() {
        return con;
    }
}
