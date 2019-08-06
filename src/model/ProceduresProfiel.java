package model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import Utility.DBconnect;

public class ProceduresProfiel {
	
	public static List<String>read(HttpServletRequest req, String user, String password) throws ClassNotFoundException, SQLException{
		
		//define list-object of string-type to contain profile data. 
		List<String> profielData = new ArrayList<String>();
		
		//connect to database, parse in request to obtain user priveleges.
		Connection conn = DBconnect.getConnection(req);
		
		//create callable statement to call stored procedure 'read_profiel' and parse in 'user' and 'password' at '?' positions.
		CallableStatement stmt = conn.prepareCall("{call read_profiel(?,?)}");
		stmt.setString(1,user);
		stmt.setString(2,password);
		
		//get resultset.
		ResultSet rs = stmt.executeQuery();
		
		//if resultset has any results, continue.
		if(rs.next()) {
			
			//retrieve klantnr and username to be shown on profile page.
			profielData.add("" + rs.getInt(1));
			profielData.add(rs.getString(2));

			//retrieve other information about user from 4th column, the 3rd would contain the password that isn't displayed.
			for(int i = 4; i <= 9; i++) {
				profielData.add(rs.getString(i));
			}

		}
		else {
			//otherwise define profile data as not found.
			profielData.add("NotFound");
		}
		
		//close connection and return profile data list-object.
		conn.close();	
		return profielData;	
	
	}
}
