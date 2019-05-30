package model;
import java.util.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import model.DBconnect;

public class ProceduresArtikel {
	public static List<String[]> list(String categoryName) throws ClassNotFoundException, SQLException{
		List<String[]>artikelList = new ArrayList<String[]>();
		Connection conn = DBconnect.getConnection();
		Statement stmt = conn.createStatement();
		String query = "call list_artikelen('" + categoryName + "')";
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next()) {
			String artikelnr = "" + rs.getInt("artikelnr");
			String productnaam = rs.getString("productnaam");
			
			Locale currentLocale = Locale.getDefault();
			NumberFormat numberFormatter = NumberFormat.getNumberInstance(currentLocale);
			
			String pricePattern = "#,##0.00";
			DecimalFormat decimalFormatter = (DecimalFormat) numberFormatter;
			decimalFormatter.applyPattern(pricePattern);
			String prijs = decimalFormatter.format(rs.getDouble("prijs"));

			String[]array = {artikelnr, productnaam, prijs};
			artikelList.add(array);
			
		}
		
		return artikelList;
	}

}