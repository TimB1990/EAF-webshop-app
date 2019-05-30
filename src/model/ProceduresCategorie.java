package model;
import java.util.*;
import java.sql.*;
import model.DBconnect;

public class ProceduresCategorie {
	public static List<String[]> listCategorie() throws ClassNotFoundException, SQLException{
		List<String[]>categorieList = new ArrayList<String[]>();
		Connection conn = DBconnect.getConnection();
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
	
	public static String[] read(String catNaam) throws ClassNotFoundException, SQLException{
		String[]categorie = new String[4];
		String sql = "call read_categorie('"+catNaam+"')";
		Connection conn = DBconnect.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()) {
			String categorieID = "" + rs.getInt(1);
			String omschrijving = rs.getString(2);
			String afbeelding = rs.getString(3);
			String naam = rs.getString(4);
			
			categorie[0] = categorieID;
			categorie[1] = omschrijving;
			categorie[2] = afbeelding;
			categorie[3] = naam;

		}
		conn.close();
		return categorie;
	}

}

