package model;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import Utility.DBconnect;

import java.sql.*;

public class ProceduresCategorie {
	
	//this method lists all categories in drop-down menu from header.
	public static List<String[]> listCategorie(HttpServletRequest req) throws ClassNotFoundException, SQLException{
		
		List<String[]>categorieList = new ArrayList<String[]>();
		
		//connect to database, parse in request to obtain guest or user priveleges, create resultset from stored procedure 'list_categorie'.
		Connection conn = DBconnect.getConnection(req);
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("call list_categorie()");
		while(rs.next()) {
			String categorieID = "" + rs.getInt("categorieID");
			String categorieNaam = rs.getString("naam");
			String[]array = {categorieID, categorieNaam};
			categorieList.add(array);
		}
		
		conn.close();
		return categorieList;
	}
	
	//this method reads a single category and displays its data
	public static String[] read(HttpServletRequest req, String catNaam) throws ClassNotFoundException, SQLException{
		
		//define string[] object to obtain data from categorie.
		String[]categorie = new String[4];
		
		//connect to database, parse in request to obtain guest or user priveleges
		Connection conn = DBconnect.getConnection(req);
		
		//create callable statement to call stored procedure read_categorie, and parse variable 'catNaam' at '?' position.
		CallableStatement stmt = conn.prepareCall("{call read_categorie(?)}");
		stmt.setString(1,catNaam);
		
		//get resultset
		ResultSet rs = stmt.executeQuery();
		
		//iterate over resultset
		while(rs.next()) {
			
			//get data from resultset entry
			String categorieID = "" + rs.getInt(1);
			String omschrijving = rs.getString(2);
			String afbeelding = rs.getString(3);
			String naam = rs.getString(4);
			
			//push data to string[] object. 
			categorie[0] = categorieID;
			categorie[1] = omschrijving;
			categorie[2] = afbeelding;
			categorie[3] = naam;

		}
		conn.close();
		return categorie;
	}

}

