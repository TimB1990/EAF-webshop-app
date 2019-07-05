package model;
import java.util.*;

import Utility.DBconnect;

import java.sql.*;
import java.text.SimpleDateFormat;

public class ProceduresBericht {
	public static List<String[]> listBericht() throws ClassNotFoundException, SQLException{
		List<String[]>berichtList = new ArrayList<String[]>();
		Connection conn = DBconnect.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("call list_bericht()");
		while(rs.next()) {
			String ID = "" + rs.getInt("ID");
			String onderwerp = rs.getString("onderwerp");
			String inhoud = rs.getString("inhoud");

			Timestamp timestamp = rs.getTimestamp("datum");
			String fTimestamp = new SimpleDateFormat("dd MMM yyyy, HH:mm").format(timestamp);
			
			String[]array = {ID, onderwerp, inhoud, fTimestamp};
			berichtList.add(array);
			
			//resolved: stored procedure now fixed
		}
		
		return berichtList;
	}

}