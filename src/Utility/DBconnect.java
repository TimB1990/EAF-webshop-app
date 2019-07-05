package Utility;
import java.sql.*;

public class DBconnect {
	
	
	public static Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://localhost:3306/webshop?serverTimezone=UTC";
		String username = "root";
		String password = "gtagpw7*////";
		Connection conn = DriverManager.getConnection(url, username, password);
		return conn;
	}
}
