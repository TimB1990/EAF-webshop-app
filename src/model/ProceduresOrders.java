
package model;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import Utility.DBconnect;

public class ProceduresOrders {
	
	private static Locale currentLocale = Locale.getDefault();
	private static NumberFormat numberFormatter = NumberFormat.getNumberInstance(currentLocale);
	private static String pricePattern = "#,##0.00";
	private static DecimalFormat decimalFormatter = (DecimalFormat) numberFormatter;
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy");
	
	//this method is used to create a new bestelnr
	public static int createBestelnr(HttpServletRequest req) throws ClassNotFoundException, SQLException {
		
		//define int for bestelnr
		int bestelnr = 0;
		
		//connect to database, parse in request to obtain guest or user priveleges.
		Connection conn = DBconnect.getConnection(req);
		
		//create stmt and define query to call stored procedure 'create_bestelnr'.
		Statement stmt = conn.createStatement();
		String sql = "call create_bestelnr()";
		
		//get resultset from query
		ResultSet rs = stmt.executeQuery(sql);
		
		//set the new bestelnr generated by the stored procedure.
		if(rs.next()) {
			bestelnr += rs.getInt(1);
		}
		
		//close connection and return new bestelnr.
		conn.close();
		return bestelnr;
	}
	
	//this method is used to insert data into mysql besteld table in order to keep record of the products beeing ordered.
	public static void insertIntoBesteld(HttpServletRequest req, int artikelnr, int aantal, int bestelnr) throws ClassNotFoundException, SQLException {
		
		//connect to database, parse in request to obtain guest or user priveleges.
		Connection conn = DBconnect.getConnection(req);
		
		//create a callable statement and assign variables in sequence of '?' position.
		CallableStatement stmt = conn.prepareCall("{call create_besteld(?,?,?)}");
		stmt.setInt(1,artikelnr);
		stmt.setInt(2, aantal);
		stmt.setInt(3,bestelnr);
	
		//do executeUpdate because no resultset will be returned
		stmt.executeUpdate();
		
		//close connection.
		conn.close();
		
	}
	

	//this method is used to insert data into the mysql bestellinge-table to keep record of the orders to be processed.
	public static void insertIntoBestellingen(HttpServletRequest req, int bestelnr, int klantnr) throws ClassNotFoundException, SQLException {
		Connection conn = DBconnect.getConnection(req);
		CallableStatement stmt = conn.prepareCall("{call create_bestelling(?,?)}");
		stmt.setInt(1, bestelnr);
		stmt.setInt(2, klantnr);
		
		stmt.executeUpdate();
		conn.close();	
	}
	
	//this method is used to create a list of the orders of given customer.
	public static List<String[]>listCustomerOrders(HttpServletRequest req, String klantnr) throws ClassNotFoundException, SQLException{
		
		decimalFormatter.applyPattern(pricePattern);
		List<String[]>customerOrderList = new ArrayList<String[]>();
		
		//connect to database, parse in request to obtain guest or user priveleges.
		Connection conn = DBconnect.getConnection(req);
		
		//use callable statement to call stored procedure 'list_klant_bestelling'
		CallableStatement stmt = conn.prepareCall("{call list_klant_bestelling(?)}");
		
		//set variable to '?' position.
		stmt.setString(1,klantnr);
		
		//get resultset.
		ResultSet rs = stmt.executeQuery();
		
		//iterate over resultset.
		while(rs.next()) {
			
			//retrieve data from resultset, and use formats for 'orderDatum' and 'totaalBedrag'
			String bestelnr = "" + rs.getInt(1);
			String orderDatumString = dateFormatter.format(rs.getDate(2));
			String totaalbedragString = decimalFormatter.format(rs.getDouble(3));
			String orderstatus = rs.getString(4);
			
			//create string[] to contain order data.
			String[]orderDataArray = {bestelnr, orderDatumString, totaalbedragString, orderstatus};
			
			//list string[] that contains order data.
			customerOrderList.add(orderDataArray);
			
		}

		conn.close();	
		return customerOrderList;	
	
	}
	

	//this method is used to read an order
	public static List<String[]>readOrder(HttpServletRequest req, String bestelnr) throws ClassNotFoundException, SQLException{
			
		decimalFormatter.applyPattern(pricePattern);
		List<String[]>orderArtikelList = new ArrayList<String[]>();

		//connect to database, parse in request to obtain guest or user priveleges.
		Connection conn = DBconnect.getConnection(req);
		
		//create callable statement to call stored procedure 'read_bestelling' and parse in 'bestelnr' at '?' position.
		CallableStatement stmt = conn.prepareCall("{call read_bestelling(?)}");
		stmt.setString(1,bestelnr);
		
		//get resultset
		ResultSet rs = stmt.executeQuery();
		
		//iterate over resultset
		while(rs.next()) {
			
			//retrieve data from resultset, and format them.
			String artikelnr = "" + rs.getInt(1);
			String productnaam = rs.getString(2);
			String prijs = decimalFormatter.format(rs.getDouble(3));
			String aantal = "" + rs.getInt(4);
			String gewicht = "" + rs.getInt(5);
			String totaalgewicht = "" + rs.getInt(6);
			String BTW = "" + rs.getInt(7);
			String bedragExBTW = decimalFormatter.format(rs.getDouble(8));
			String bedrag = decimalFormatter.format(rs.getDouble(9));
			
			//create string[] object that contains data.
			String[]orderArtikelArray = {artikelnr, productnaam, prijs, aantal, gewicht, totaalgewicht, BTW, bedragExBTW, bedrag};
			
			//add string[] to list
			orderArtikelList.add(orderArtikelArray);
			
		}

		conn.close();	
		return orderArtikelList;	
	
	}
	
	//this method is used to display a cost specification that is shown when user sees order.
	public static List<String>getCostSpecification(List<String[]>orderArtikelList){
		
		decimalFormatter.applyPattern(pricePattern);
		List<String>kostenSpecificatieList = new ArrayList<String>();
		
		double totaalExBTW = 0;
		double btw9 = 0;
		double btw21 = 0;
		double orderGewicht = 0;
		double verzendkosten = 0;
		int aantalArtikelen = 0;
		int aantalPaketten = 1;
		double totaalbedrag = 0;
		
		for(String[]item : orderArtikelList) {
			
			/*calculate aantal artikelen, totaalExBTW and ordergewicht*/
			aantalArtikelen += Integer.parseInt(item[3]);
			orderGewicht += Double.parseDouble(item[5].replace(',','.')) / 1000;
			totaalExBTW += Double.parseDouble(item[7].replace(',','.'));
			
			/*Calculate BTW9, 21*/
			if(item[6].contentEquals("9")) {
				btw9 += Double.parseDouble(item[8].replace(',','.'))*0.09;
			}
			else {
				btw21 += Double.parseDouble(item[8].replace(',','.')) *  0.21;
			}	
		}
		
		//check whether orderweight is more than 20kg
		if(orderGewicht > 20) {
			
			//define amount of full packages, each 20kg.
			aantalPaketten += (int) orderGewicht / 20;
			verzendkosten += (aantalPaketten * (12.20*1.21));
			
			//define rest weight in order to calculate distribution costs left.
			int restPakketGewicht = (int) orderGewicht % 20;
			
			//check is rest-weight is less than 10kg
			if(restPakketGewicht > 0 && restPakketGewicht < 10) {
				
				//set 'verzendkosten' to 6.95 * 1.21 (21% VAT)
				verzendkosten += (6.95*1.21);
			}
			else {
				
				//set 'verzendkosten' to 12.20 * 1.21 (21% VAT)
				verzendkosten += (12.20*1.21);
			}
		}
		else {
			verzendkosten = (6.95*1.21);
		}
		
		//calculate 'totaalbedrag' by adding up costs.
		totaalbedrag = totaalExBTW + btw9 + btw21 + verzendkosten;
		
		//add specification data to list-object to be shown, and format data when needed. 
		kostenSpecificatieList.add("" + aantalArtikelen);
		kostenSpecificatieList.add(decimalFormatter.format(totaalExBTW));
		kostenSpecificatieList.add(decimalFormatter.format(btw9));
		kostenSpecificatieList.add(decimalFormatter.format(btw21));
		kostenSpecificatieList.add(decimalFormatter.format(totaalExBTW + btw9 + btw21));
		kostenSpecificatieList.add(decimalFormatter.format(orderGewicht));
		kostenSpecificatieList.add("" + aantalPaketten);
		kostenSpecificatieList.add(decimalFormatter.format(verzendkosten));
		kostenSpecificatieList.add(decimalFormatter.format(totaalbedrag));
		
		//return list-object.
		return kostenSpecificatieList;
	}
	
}
