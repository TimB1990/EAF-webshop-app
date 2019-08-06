package model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import Utility.DBconnect;

public class ProceduresContactformulier {
	
	//this method is used to put contactform data into database.
	public static void insertIntoContactform(HttpServletRequest req, String naam, String email, String onderwerp, String bericht) throws ClassNotFoundException, SQLException {
		
		//connect to database, parse in request to obtain guest or user priveleges.
		Connection conn = DBconnect.getConnection(req);
		
		//create callable statement to call stored procedure create_contactformulier.
		CallableStatement stmt = conn.prepareCall("{call create_contactformulier(?,?,?,?)}");
		
		//set variables to sequence of '?' position.
		stmt.setString(1,naam);
		stmt.setString(2,email);
		stmt.setString(3,onderwerp);
		stmt.setString(4,bericht);

		//use executeUpdate() because no resultset will be returned.
		stmt.executeUpdate();
		
		//close connection
		conn.close();
		
	}	
}

