package Utility;
import java.sql.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class DBconnect {
	
	
	public static Connection getConnection(HttpServletRequest req) throws SQLException, ClassNotFoundException {
		
		String[]credentials = {"guest","guest"};

		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://localhost:3306/eafwebshop?serverTimezone=UTC";
			
		//check is there is a session
		if (req.getSession() != null) {
			HttpSession session = req.getSession();
			//check is session attribute 'credentials' is present
			if (session.getAttribute("credentials")!= null) {
				credentials = (String[]) session.getAttribute("credentials");
			}
		}
	
		//return connection object, along with credentials, either user specific, or guest
		Connection conn = DriverManager.getConnection(url, credentials[0], credentials[1]);
		return conn;
		}
}
