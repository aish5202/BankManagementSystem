package bankmanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DBConnection {

    private static final String DRIVER = "oracle.jdbc.OracleDriver";
    private static final String URL    = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USER   = "c##aiswarya";
    private static final String PASS   = "aiswarya";

    private static Connection conn = null;

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName(DRIVER);
                conn = DriverManager.getConnection(URL, USER, PASS);
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "Oracle JDBC driver not found!\nAdd ojdbc8.jar to Libraries.",
                "Driver Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Database connection failed!\n" + e.getMessage(),
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
        return conn;
    }

    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}