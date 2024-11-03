package com.toraden.toraiine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private static DataBase instance;
    private static Connection connection;
    private final String name;
    private final String url;
    private final String user;
    private final String password;

    // シングルトン用コンストラクタ
    // 1回しか呼ばれない
    private DataBase(String name, String url, String user, String password) {
        this.name = name;
        this.url = url;
        this.user = user;
        this.password = password;
        try {
            // 1回だけDriverを初期化（forName）すれば良い
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.connect();
    }

    // データベースに接続する
    private void connect() {
        try {
            // データベースに接続しconnectionオブジェクトを取得する
            connection = DriverManager.getConnection("jdbc:mysql://%s/%s?".formatted(this.url, this.name), this.user, this.password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // インスタンスを取得するメソッド
    public static DataBase getInstance(String name, String url, String user, String password) {
        if (instance == null) {
            // 最初の1回だけここが実行される
            // 最初の1回以外はgetInstance(null, null,...)等で呼ばれてもここを通らないので引数が無視される
            // 最初の1回でthis.nameやthis.urlに正しい値を保存しているのでthis.connect()は問題なく実行出来るロジック
            instance = new DataBase(name, url, user, password);
        }
        return instance;
    }

    // 有効な接続を返すメソッド
    public Connection getConnection() {
        try {
            // 接続が閉じられているか簡易なQueryを投げて例外の発生有無で確認する
            if (connection != null) {
                connection.prepareStatement("SELECT 1").executeQuery();
            }
        } catch (SQLException e) {
            this.connect();
        }
        return connection;
    }
}