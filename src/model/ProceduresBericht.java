package model;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import Utility.DBconnect;

import java.sql.*;
import java.text.SimpleDateFormat;

public class ProceduresBericht {
	
	//this method lists all messages that will be displayed on the homepage
	public static List<String[]> listBericht(HttpServletRequest req) throws ClassNotFoundException, SQLException{
		List<String[]>berichtList = new ArrayList<String[]>();
		
		//connect to database, parse in request to obtain guest or user priveleges, create resultset from stored procedure 'list_bericht'.
		Connection conn = DBconnect.getConnection(req);
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("call list_bericht()");
		
		//iterate over resultset
		while(rs.next()) {
			String ID = "" + rs.getInt("ID");
			String onderwerp = rs.getString("onderwerp");
			String inhoud = rs.getString("inhoud");

			//define current timestamp
			Timestamp timestamp = rs.getTimestamp("datum");
			
			//format timestamp in dd MMM yyyy, HH:mm format. 
			String fTimestamp = new SimpleDateFormat("dd MMM yyyy, HH:mm").format(timestamp);
			
			//create string[] object containing data from bericht.
			String[]array = {ID, onderwerp, inhoud, fTimestamp};
			
			//add string array to message list.
			berichtList.add(array);
			
		}
		
		return berichtList;
	}

}