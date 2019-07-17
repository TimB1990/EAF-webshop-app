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

	// only for displaying new order specification, not inserting...
	private static final long serialVersionUID = 1L;
	private static Locale currentLocale = Locale.getDefault();
	private static NumberFormat numberFormatter = NumberFormat.getNumberInstance(currentLocale);
	private static String pricePattern = "#,##0.00";
	private static DecimalFormat decimalFormatter = (DecimalFormat) numberFormatter;

	HttpSession session;
	String contentRoot;
	String message;
	boolean goAhead;

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
			
			// retrieve attribute cartItemList from sessionScope, item = {artikelnr,
			// productnaam, gewicht, btw, artikelprijs, aantal, bedrag}
			List<String[]> cartItemList = (List<String[]>) session.getAttribute("cartItemList");
			// initialize variables for totalOrderSpecification that need to be incremented
			// during loop over cartItemList
			int artikelenTotaal = 0;
			double btw9totaal = 0.0, btw21totaal = 0.0, totaalBedragExBtw = 0;
			double orderGewicht = 0.0;

			// create orderArtikelDetails-list from cartItemList, item = {artikelnr,
			// artikelprijs, aantal, stukgewicht, totaalgewicht, bedragExBtw, btw9, btw21,
			// bedragIncBtw}
			List<String[]> orderArtikelDetailList = new ArrayList<String[]>();

			for (int i = 0; i < cartItemList.size(); i++) {

				// populate orderArtikelDetails- list by using data from cartItemList
				int artikelnr = Integer.parseInt(cartItemList.get(i)[0]);
				String product = cartItemList.get(i)[1];

				int aantal = Integer.parseInt(cartItemList.get(i)[5]);
				artikelenTotaal += aantal;

				double stukgewicht = Double.parseDouble(cartItemList.get(i)[2]);
				int stukgewichtInt = (int) stukgewicht;
				int btw = Integer.parseInt(cartItemList.get(i)[3]);
				double btw9 = 0, btw21 = 0;

				double totaalgewicht = (stukgewicht * aantal) / 1000;
				orderGewicht += totaalgewicht;

				double artikelprijs = Double.parseDouble(cartItemList.get(i)[4].replace(',', '.'));

				double bedragIncBtw = aantal * artikelprijs;

				if (btw == 9) {
					btw9 += bedragIncBtw * 0.09;
					btw9totaal += btw9;
				} else {
					btw21 += bedragIncBtw * 0.21;
					btw21totaal += btw21;
				}

				double bedragExBtw = bedragIncBtw - btw9 - btw21;
				totaalBedragExBtw += bedragExBtw;

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
			int aantalPaketten = 1;
			double verzendkosten = 0;

			if (orderGewicht > 20) {
				aantalPaketten += (int) orderGewicht / 20;
				verzendkosten += (aantalPaketten * (12.20 * 1.21));

				int restPakketGewicht = (int) orderGewicht % 20;
				if (restPakketGewicht > 0 && restPakketGewicht < 10) {
					verzendkosten += (6.95 * 1.21);
				} else {
					verzendkosten += (12.20 * 1.21);
				}
			} else {
				verzendkosten = (6.95 * 1.21);
			}

			double totaalBedragIncVerzendkosten = totaalBedragExBtw + btw9totaal + btw21totaal + verzendkosten;

			String[] totalOrderSpecification = new String[8];
			totalOrderSpecification[0] = "" + artikelenTotaal;
			totalOrderSpecification[1] = decimalFormatter.format(totaalBedragExBtw);
			totalOrderSpecification[2] = decimalFormatter.format(btw9totaal);
			totalOrderSpecification[3] = decimalFormatter.format(btw21totaal);
			totalOrderSpecification[4] = "" + orderGewicht;
			totalOrderSpecification[5] = "" + aantalPaketten;
			totalOrderSpecification[6] = decimalFormatter.format(verzendkosten);
			totalOrderSpecification[7] = decimalFormatter.format(totaalBedragIncVerzendkosten);

			contentRoot = "newOrder";
			message = "OK";

			session.setAttribute("orderArtikelDetailList", orderArtikelDetailList);
			session.setAttribute("totalOrderSpecification", totalOrderSpecification);

		}

		request.setAttribute("message", message);
		request.setAttribute("contentRoot", contentRoot);
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
