package controllers;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "PendingOrderController", urlPatterns = { "/newOrder" })
public class PendingOrderController extends HttpServlet {

	//declare decimalformat for displaying float in specific pricePattern (0,00) to be stored as String
	private static final long serialVersionUID = 1L;
	private static Locale currentLocale = Locale.getDefault();
	private static NumberFormat numberFormatter = NumberFormat.getNumberInstance(currentLocale);
	private static String pricePattern = "#,##0.00";
	private static DecimalFormat decimalFormatter = (DecimalFormat) numberFormatter;

	HttpSession session;
	String contentRoot;
	String message;
	String disabledButton;

	public PendingOrderController() {
		super();
	}

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// apply decimalFormat pattern
		decimalFormatter.applyPattern(pricePattern);

		// get session
		session = request.getSession();

		// get loggedIn attribute
		String loggedIn = (String) session.getAttribute("loggedIn");

		// check if user is logged in
		if (loggedIn == null || loggedIn.contentEquals("false")) {
			message = "please login";
			contentRoot = "loginForm";
		} else {
			
			// check if cartItemList is null, if so than set attribute 'disabledButton' to lock bevestigen button
			if(request.getSession().getAttribute("cartItemList") == null) {	
				disabledButton = "disabled";
			}
			else {
				
				//get cartItemList from sessionScope
				List<String[]>cartItemList = (List<String[]>) request.getSession().getAttribute("cartItemList");
				
				//check is cartItemList is empty if so set disabledButton attribute to value 'disabled'.
				if(cartItemList.size() < 1) {
					disabledButton = "disabled";
				}
				else {
					disabledButton = "";
				}
			}
			
			// retrieve attribute cartItemList from sessionScope, item = {artikelnr,
			// productnaam, gewicht, btw, artikelprijs, aantal, bedrag}
			List<String[]> cartItemList = (List<String[]>) session.getAttribute("cartItemList");
			
			// initialize variables for totalOrderSpecification that need to be incremented, during loop over cartItemList
			int artikelenTotaal = 0;
			double btw9totaal = 0.0, btw21totaal = 0.0, totaalBedragExBtw = 0;
			double orderGewicht = 0.0;

			// create orderArtikelDetails-list from cartItemList, item = {artikelnr,
			// artikelprijs, aantal, stukgewicht, totaalgewicht, bedragExBtw, btw9, btw21,
			// bedragIncBtw}
			
			//create list of string-arrays that should contain details of ordered products. 
			List<String[]> orderArtikelDetailList = new ArrayList<String[]>();

			// do for-loop over cartItemList. 
			for (int i = 0; i < cartItemList.size(); i++) {

				//get artikelnr from first arrayindex of current list-index
				int artikelnr = Integer.parseInt(cartItemList.get(i)[0]);
				
				//get productname from second arrayindex of current list-index
				String product = cartItemList.get(i)[1];

				//get amount of specific product from 6th arrayindex of current list-index
				int aantal = Integer.parseInt(cartItemList.get(i)[5]);
				
				//increment total amount of articles ordered by amount of specific product.
				artikelenTotaal += aantal;

				//get weight of single product from 3rd arrayindex of current list-index;
				double stukgewicht = Double.parseDouble(cartItemList.get(i)[2]);
				
				//convert double to int
				int stukgewichtInt = (int) stukgewicht;
				
				//get btw-percentage from 4th arrayindex of current list-index
				int btw = Integer.parseInt(cartItemList.get(i)[3]);
				
				//set btw9 and btw21 default to 0
				double btw9 = 0, btw21 = 0;

				//calculate totalweight in kg's for each product
				double totaalgewicht = (stukgewicht * aantal) / 1000;
				
				//increment orderweight by totaalgewicht calculated above.
				orderGewicht += totaalgewicht;

				//get artikelprijs by parsing string on 5th arrayindex of current list-index while ',' is replaced by '.' to obtain valid double.
				double artikelprijs = Double.parseDouble(cartItemList.get(i)[4].replace(',', '.'));

				//calculate bedragIncBtw of current orderline
				double bedragIncBtw = aantal * artikelprijs;

				//check whether given btw-percentage (VAT-percentage) is 9%
				if (btw == 9) {
					
					//increase btw9 of the current orderline
					btw9 += bedragIncBtw * 0.09;
					
					//increase btw9totaal by btw9 
					btw9totaal += btw9;
					
				} else {
					
					//otherwise increase btw21 of the current orderline
					btw21 += bedragIncBtw * 0.21;
					
					//increase btw21totaal by btw21 
					btw21totaal += btw21;
				}

				//calculate bedragExBtw of current orderline
				double bedragExBtw = bedragIncBtw - btw9 - btw21;
				
				//increase totaalBedragExBtw by bedragExBtw of current orderline. 
				totaalBedragExBtw += bedragExBtw;

				//create orderArtikelDetails string array
				String[] orderArtikelDetails = new String[10];
				orderArtikelDetails[0] = "" + artikelnr;
				orderArtikelDetails[1] = product;
				orderArtikelDetails[2] = "" + aantal;
				orderArtikelDetails[3] = decimalFormatter.format(artikelprijs);
				orderArtikelDetails[4] = "" + stukgewichtInt;
				orderArtikelDetails[5] = "" + totaalgewicht;
				orderArtikelDetails[6] = decimalFormatter.format(bedragExBtw);
				orderArtikelDetails[7] = decimalFormatter.format(btw9);
				orderArtikelDetails[8] = decimalFormatter.format(btw21);
				orderArtikelDetails[9] = decimalFormatter.format(bedragIncBtw);

				orderArtikelDetailList.add(orderArtikelDetails);
			}

			// totalOrderSpecification --> {artikelenTotaal, totaalBedragExBtw, btw9totaal,
			// btw21totaal, orderGewicht(kg), aantalPaketten, verzendkosten,
			// totaalIncVerzendkosten }
			
			//set aantal paketten default to 1
			int aantalPaketten = 1;
			
			//set verzendkosten default to 0
			double verzendkosten = 0;

			//check whether orderWeight is greather than 20 (kg)
			if (orderGewicht > 20) {
				
				//increase aantalPaketten by the amount of full portions of 20kg weight. 
				aantalPaketten += (int) orderGewicht / 20;
				
				//increase verzendkosten by multiplying aantalPaketten by €12.20 * 1.21 (21% VAT)
				verzendkosten += (aantalPaketten * (12.20 * 1.21));

				//define modules outside full portion of 20kg weight
				int restPakketGewicht = (int) orderGewicht % 20;
				
				//check whether restPakketGewicht greater than 0 and less than 10
				if (restPakketGewicht > 0 && restPakketGewicht < 10) {
					
					//increase verzendkosten by €6.95 * 1.21 (21% VAT)
					verzendkosten += (6.95 * 1.21);
				} else {
					//otherwise in case restGewicht > 10kg increase verzendkosten by €12.20 * 1.21 (21% VAT)
					verzendkosten += (12.20 * 1.21);
				}
			} else {
				
				//otherwise if orderWeight not greather than 20 (kg) increase verzendkosten by €6,95 * 1.21 (21% VAT)
				verzendkosten = (6.95 * 1.21);
			}

			//calculate totaalbedragIncVerzendkosten
			double totaalBedragIncVerzendkosten = totaalBedragExBtw + btw9totaal + btw21totaal + verzendkosten;

			//create String[] object to store the data for the total order specification
			String[] totalOrderSpecification = new String[8];
			totalOrderSpecification[0] = "" + artikelenTotaal;
			totalOrderSpecification[1] = decimalFormatter.format(totaalBedragExBtw);
			totalOrderSpecification[2] = decimalFormatter.format(btw9totaal);
			totalOrderSpecification[3] = decimalFormatter.format(btw21totaal);
			totalOrderSpecification[4] = "" + orderGewicht;
			totalOrderSpecification[5] = "" + aantalPaketten;
			totalOrderSpecification[6] = decimalFormatter.format(verzendkosten);
			totalOrderSpecification[7] = decimalFormatter.format(totaalBedragIncVerzendkosten);

			//set content to "newOrder" en message to "ok"
			contentRoot = "newOrder";
			message = "OK";

			//put attributes 'orderArtikelDetailList' and 'totalOrderSpecification' into request-scope
			session.setAttribute("orderArtikelDetailList", orderArtikelDetailList);
			session.setAttribute("totalOrderSpecification", totalOrderSpecification);

		}

		//set attributes 'disabledButton', 'message' and 'contentRoot'
		request.setAttribute("disabledButton", disabledButton);
		request.setAttribute("message", message);
		request.setAttribute("contentRoot", contentRoot);
		
		//forward request to index.jsp so that request scope can be accessed from within index.jsp.
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
