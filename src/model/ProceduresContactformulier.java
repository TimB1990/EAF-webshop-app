package model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import Utility.DBconnect;

public class ProceduresContactformulier {
	
	public static void insertIntoContactform(String naam, String email, String onderwerp, String bericht) throws ClassNotFoundException, SQLException {
		Connection conn = DBconnect.getConnection();
		CallableStatement stmt = conn.prepareCall("{call create_contactformulier(?,?,?,?)}");
		stmt.setString(1,naam);
		stmt.setString(2,email);
		stmt.setString(3,onderwerp);
		stmt.setString(4,bericht);

		stmt.executeUpdate();
		conn.close();
		
	}	
}

