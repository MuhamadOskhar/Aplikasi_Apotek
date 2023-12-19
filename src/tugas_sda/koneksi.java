package tugas_sda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class koneksi {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/aplikasi_apotek";
    private static final String USERNAME = "root"; // Ganti dengan username MySQL Anda
    private static final String PASSWORD = "tesdoang"; // Ganti dengan password MySQL Anda

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new SQLException("Failed to connect to the database", e);
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
