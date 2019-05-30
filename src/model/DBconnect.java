package model;
import java.sql.*;

public class DBconnect {
	
	
	public static Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/webshop?serverTimezone=UTC", "root", "root");
		return conn;
	}
}
