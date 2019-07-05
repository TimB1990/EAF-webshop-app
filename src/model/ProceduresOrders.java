
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

import Utility.DBconnect;

public class ProceduresOrders {
	
	private static Locale currentLocale = Locale.getDefault();
	private static NumberFormat numberFormatter = NumberFormat.getNumberInstance(currentLocale);
	private static String pricePattern = "#,##0.00";
	private static DecimalFormat decimalFormatter = (DecimalFormat) numberFormatter;
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy");
	
	
	public static int createBestelnr() throws ClassNotFoundException, SQLException {
		int bestelnr = 0;
		Connection conn = DBconnect.getConnection();
		Statement stmt = conn.createStatement();
		String sql = "call create_bestelnr()";
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()) {
			bestelnr += rs.getInt(0);
		}
		
		conn.close();
		return bestelnr;
	}
	
	public static void insertIntoBesteld(int artikelnr, int aantal, int bestelnr) throws ClassNotFoundException, SQLException {
		Connection conn = DBconnect.getConnection();
		CallableStatement stmt = conn.prepareCall("{call create_besteld(?,?,?)}");
		stmt.setInt(1,artikelnr);
		stmt.setInt(2, aantal);
		stmt.setInt(3,bestelnr);
	
		stmt.executeUpdate();
		
	}
	
	/*
	 * CallableStatement callableStatement =
    connection.prepareCall("{call calculateStatistics(?, ?)}");

	callableStatement.setString(1, "param1");
	callableStatement.setInt   (2, 123);

	callableStatement.registerOutParameter(1, java.sql.Types.VARCHAR);
	callableStatement.registerOutParameter(2, java.sql.Types.INTEGER);

	ResultSet result = callableStatement.executeQuery();
	while(result.next()) { ... }

	String out1 = callableStatement.getString(1);
	int    out2 = callableStatement.getInt   (2);
	 * */
	
	public static void insertIntoBestellingen(int bestelnr, int klantnr) throws ClassNotFoundException, SQLException {
		Connection conn = DBconnect.getConnection();
		CallableStatement stmt = conn.prepareCall("{call create_bestelling(?,?)}");
		stmt.setInt(1, bestelnr);
		stmt.setInt(2, klantnr);
		
		stmt.executeUpdate();
		
	}
	
	public static List<String[]>listCustomerOrders(String klantnr) throws ClassNotFoundException, SQLException{
		
		decimalFormatter.applyPattern(pricePattern);
		List<String[]>customerOrderList = new ArrayList<String[]>();
		
		Connection conn = DBconnect.getConnection();
		Statement stmt = conn.createStatement();
		String sql = "call list_klant_bestelling("+klantnr+")";
		ResultSet rs = stmt.executeQuery(sql);
		
		while(rs.next()) {
			String bestelnr = "" + rs.getInt(1);
			String orderDatumString = dateFormatter.format(rs.getDate(2));
			String totaalbedragString = decimalFormatter.format(rs.getDouble(3));
			String orderstatus = rs.getString(4);
			
			String[]orderDataArray = {bestelnr, orderDatumString, totaalbedragString, orderstatus};
			customerOrderList.add(orderDataArray);
			
		}

		conn.close();	
		return customerOrderList;	
	
	}
	
	//functions below should be updated to datastructure given in PendingOrderController --
	
	public static List<String[]>readOrder(String bestelnr) throws ClassNotFoundException, SQLException{
			
		decimalFormatter.applyPattern(pricePattern);
		List<String[]>orderArtikelList = new ArrayList<String[]>();

		
		Connection conn = DBconnect.getConnection();
		Statement stmt = conn.createStatement();
		String sql = "call read_bestelling("+bestelnr+")";
		ResultSet rs = stmt.executeQuery(sql);
		
		while(rs.next()) {
			
			String artikelnr = "" + rs.getInt(1);
			String productnaam = rs.getString(2);
			String prijs = decimalFormatter.format(rs.getDouble(3));
			String aantal = "" + rs.getInt(4);
			String gewicht = "" + rs.getInt(5);
			String totaalgewicht = "" + rs.getInt(6);
			String BTW = "" + rs.getInt(7);
			String bedragExBTW = decimalFormatter.format(rs.getDouble(8));
			String bedrag = decimalFormatter.format(rs.getDouble(9));
			
			String[]orderArtikelArray = {artikelnr, productnaam, prijs, aantal, gewicht, totaalgewicht, BTW, bedragExBTW, bedrag};
			orderArtikelList.add(orderArtikelArray);
			
		}

		conn.close();	
		return orderArtikelList;	
	
	}
	
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
		
		if(orderGewicht > 20) {
			aantalPaketten += (int) orderGewicht / 20;
			verzendkosten += (aantalPaketten * (12.20*1.21));
			
			int restPakketGewicht = (int) orderGewicht % 20;
			if(restPakketGewicht > 0 && restPakketGewicht < 10) {
				verzendkosten += (6.95*1.21);
			}
			else {
				verzendkosten += (12.20*1.21);
			}
		}
		else {
			verzendkosten = (6.95*1.21);
		}
		//pay attention to this section for comparison with createOrderSpecification in PendingOrderController.java
		totaalbedrag = totaalExBTW + btw9 + btw21 + verzendkosten;
		kostenSpecificatieList.add("" + aantalArtikelen);
		kostenSpecificatieList.add(decimalFormatter.format(totaalExBTW));
		kostenSpecificatieList.add(decimalFormatter.format(btw9));
		kostenSpecificatieList.add(decimalFormatter.format(btw21));
		kostenSpecificatieList.add(decimalFormatter.format(totaalExBTW + btw9 + btw21));
		kostenSpecificatieList.add(decimalFormatter.format(orderGewicht));
		kostenSpecificatieList.add("" + aantalPaketten);
		kostenSpecificatieList.add(decimalFormatter.format(verzendkosten));
		kostenSpecificatieList.add(decimalFormatter.format(totaalbedrag));
		
		return kostenSpecificatieList;
	}
	
}
