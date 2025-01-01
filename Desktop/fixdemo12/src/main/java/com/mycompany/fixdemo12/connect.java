package com.mycompany.fixdemo12;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connect {

    public Connection getConnection() {
        Connection conn = null;
        try {
            // Cài đặt driver JDBC cho Microsoft SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Đường dẫn và thông tin đăng nhập cơ sở dữ liệu
            String url = "jdbc:sqlserver://localhost:1433;databaseName=student";
            String username = "sa";
            String password = "1234";

            // Thiết lập kết nối
            conn = DriverManager.getConnection(url, username, password);

            // In thông báo nếu kết nối thành công
            System.out.println("Ket noi thanh cong!");
        } catch (ClassNotFoundException e) {
            // Xử lý lỗi không tìm thấy driver
            System.out.println("Không tìm thấy driver JDBC!");
            e.printStackTrace();
        } catch (SQLException e) {
            // Xử lý lỗi kết nối cơ sở dữ liệu
            System.out.println("Ket noi that bai!");
            e.printStackTrace();
        }
        return conn;
    }

    public static void main(String[] args) {
        connect db = new connect();
        try (Connection conn = db.getConnection()) {
            if (conn != null) {
                System.out.println("Ket noi toi CSDL thanh cong trong main!");
            } else {
                System.out.println("Khong the ket noi toi CSDL trong main!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
