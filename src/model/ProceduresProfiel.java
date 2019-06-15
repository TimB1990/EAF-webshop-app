package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class ProceduresProfiel {
	
	public static List<String>read(String user, String password) throws ClassNotFoundException, SQLException{
		//String[]profielData = new String[7];
		List<String> profielData = new ArrayList<String>();
		Connection conn = DBconnect.getConnection();
		Statement stmt = conn.createStatement();
		String sql = "call read_profiel('" + user + "','" + password + "')";
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next()) {
			profielData.add("" + rs.getInt(1));
			for(int i = 2; i <= 8; i++) {
				profielData.add(rs.getString(i));
			}

		}
		else {
			profielData.add("NotFound");
		}
		
		conn.close();	
		return profielData;	
	
	}
}
