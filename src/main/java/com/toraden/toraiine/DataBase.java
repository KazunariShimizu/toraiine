package com.toraden.toraiine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private static DataBase instance;
    private static Connection con;
    private final String dbName;
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    // シングルトン用コンストラクタ
    private DataBase(String dbName, String dbUrl, String dbUser, String dbPassword) {
        this.dbName = dbName;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        connect();
    }

    // データベースに接続するメソッド
    private void connect() {
        try {
            // Load JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // データベースに接続しconnectionオブジェクトを取得する
            con = DriverManager.getConnection("jdbc:mysql://%s/%s".formatted(dbUrl, dbName), dbUser, dbPassword);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    // インスタンスを取得するメソッド
    public static DataBase getInstance(String dbName, String dbUrl, String dbUser, String dbPassword) {
        if (instance == null) {
            instance = new DataBase(dbName, dbUrl, dbUser, dbPassword);
        }
        return instance;
    }

    // 有効な接続を返すメソッド
    public Connection getConnection() {
        try {
            // 接続が閉じられている場合は再接続
            if (con == null || con.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
}