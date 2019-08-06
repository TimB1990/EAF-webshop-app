package model;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import Utility.DBconnect;

import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ProceduresArtikel {
	
	//define price-pattern
	private static Locale currentLocale = Locale.getDefault();
	private static NumberFormat numberFormatter = NumberFormat.getNumberInstance(currentLocale);
	private static final String pricePattern = "#,##0.00";
	private static DecimalFormat decimalFormatter = (DecimalFormat) numberFormatter;
	
	//this method lists the products that belong to a category-name
	public static List<String[]> list(HttpServletRequest req, String categoryName) throws ClassNotFoundException, SQLException{
		List<String[]>artikelList = new ArrayList<String[]>();
		
		//connect to DB and obtain parse a request object that contains credentials to decide whether 'guest' or a customer / user has access.
		Connection conn = DBconnect.getConnection(req);
		
		//create callable statement to call stored procedure list_artikelen
		CallableStatement stmt = conn.prepareCall("{call list_artikelen(?)}");
		
		//set variable at '?' position
		stmt.setString(1,categoryName);
		
		//get resultset
		ResultSet rs = stmt.executeQuery();
		
		//iterate over resultset.
		while(rs.next()) {
			String artikelnr = "" + rs.getInt("artikelnr");
			String productnaam = rs.getString("productnaam");
			
			decimalFormatter.applyPattern(pricePattern);
			
			String prijs = decimalFormatter.format(rs.getDouble("prijs"));
			String gewicht = "" + rs.getInt("gewicht");

			//create String array of retrieved values from resultset.
			String[]array = {artikelnr, productnaam, prijs, gewicht};
			artikelList.add(array);
			
		}
		
		return artikelList;
	}
	
	public static String[]read(HttpServletRequest req, Integer nr) throws ClassNotFoundException, SQLException{
		
		//CallableStatement stmt = conn.prepareCall("{call list_artikelen(?)}");
		
		//create string[] that contains data of single product (artikel).
		String[]artikel = new String[7];
		
		//connect to DB and obtain parse a request object that contains credentials to decide whether 'guest' or a customer / user has access.
		Connection conn = DBconnect.getConnection(req);
		
		//create callable statement to call stored procedure 'read_artikel'
		CallableStatement stmt = conn.prepareCall("{call read_artikel(?)}");
		
		//set variable at '?' position
		stmt.setInt(1, nr);
		
		//get resultset
		ResultSet rs = stmt.executeQuery();
		
		//iterate over resultset
		while(rs.next()) {
			String artikelnr = "" + rs.getInt("artikelnr");
			String categorieID = "" + rs.getInt("categorieID");
			String productnaam = rs.getString("productnaam");
			String productomschrijving = rs.getString("productomschrijving");
			
			decimalFormatter.applyPattern(pricePattern);
			
			String prijs = decimalFormatter.format(rs.getDouble("prijs"));
			String BTW = "" + rs.getInt("btw");
			String gewicht = "" + rs.getInt("gewicht");

			//set String[]array = {artikelnr, categorieID, productnaam, productomschrijving, prijs};
			artikel[0] = artikelnr;
			artikel[1] = categorieID;
			artikel[2] = productnaam;
			artikel[3] = productomschrijving;
			artikel[4] = prijs;
			artikel[5] = gewicht;
			artikel[6] = BTW;
		}
		conn.close();
		return artikel;
	}

}