package controllers;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.ProceduresArtikel;

@WebServlet(name = "/addToCart", urlPatterns = { "/addToCart" })
public class CartController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public CartController() {
        super();
    }

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String action = request.getParameter("action");
		String paramArtikelnr = request.getParameter("artikelnr");
		String totaalString = "";
		
		int aantal = 1;
		int itemCount = 0;
		double totaal = 0;
		
		/*define the way doubles should be represented*/
		Locale currentLocale = Locale.getDefault();
		NumberFormat numberFormatter = NumberFormat.getNumberInstance(currentLocale);
		DecimalFormat decimalFormatter = (DecimalFormat) numberFormatter;
		String pricePattern = "#,##0.00";
		decimalFormatter.applyPattern(pricePattern);
		
		/*create cartSession*/
		HttpSession cartSession = request.getSession(true);
		List<String[]>cartItemList = new ArrayList<String[]>();
		
		if(cartSession.getAttribute("cartItemList") != null) {
			cartItemList = (List<String[]>) cartSession.getAttribute("cartItemList");
			itemCount = cartItemList.size();
		}
		else {
			cartItemList = new ArrayList<String[]>();
		}
		
		if(action != null && action.equals("emptyCart")) {
			for (Iterator<String[]> iter = cartItemList.listIterator(); iter.hasNext(); ) {
				@SuppressWarnings("unused")
				String[] arr = iter.next();
			    iter.remove();
			}
			itemCount = 0;
		}
		
			
		if(action != null && action.equals("newItem")) {
			
			int artikelnr = Integer.parseInt(paramArtikelnr);
			String[]cartItem = new String[7];
			//int aantal = 1;
			Boolean update = false;

			/*check if paramArtikelnr exists in cartItemList*/
			for (int i = 0; i<cartItemList.size(); i++) {
				if(cartItemList.get(i)[0].equals(paramArtikelnr)) {
					
					/*update aantal*/
					aantal = Integer.parseInt(cartItemList.get(i)[3]) + 1;
					cartItemList.get(i)[3] = "" + aantal;
					
					/*parse double bedrag */
					Double updatedBedrag = Double.parseDouble(cartItemList.get(i)[2].replace(',', '.')) * aantal;
					String updatedBedragString = decimalFormatter.format(updatedBedrag);
					
					/*update bedrag*/
					cartItemList.get(i)[4] = updatedBedragString;
					
					/*set updateBoolean to true*/
					update = true;			
				}
			}
			
			if(!update) {
				try {
					
					String[]productData = ProceduresArtikel.read(artikelnr);
					
					String artikelnrString = productData[0];
					String productnaam = productData[2];
					String prijs = productData[4];
					String gewicht = productData[5];
					String btw = productData[6];
					
					Double bedrag = Double.parseDouble(prijs.replace(',', '.')) * aantal;
					String bedragString = decimalFormatter.format(bedrag);
					
					cartItem[0] = artikelnrString;
					cartItem[1] = productnaam;
					cartItem[2] = gewicht;
					cartItem[3] = btw;
					cartItem[4] = "" + prijs;
					cartItem[5] = "" + aantal;
					cartItem[6] = bedragString;
					
					cartItemList.add(cartItem);
					itemCount = cartItemList.size();
		
				}
				catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();		
				}	
				
			}
		}
		
		if(paramArtikelnr != null && action != null) {
			
			//must be placed inside the for loop so other condition, that aantal must be bigger than 1
			
			for (int i = 0; i<cartItemList.size(); i++) {
				if(cartItemList.get(i)[0].equals(paramArtikelnr)) {
					
					aantal = Integer.parseInt(cartItemList.get(i)[5]);
				
					/*update aantal*/
					if(action.contentEquals("increase")) {
						aantal = Integer.parseInt(cartItemList.get(i)[5]) + 1;
					}
					
					if(action.contentEquals("decrease")) {
						if(aantal > 1) {
							aantal = Integer.parseInt(cartItemList.get(i)[5]) - 1;
						}	
					}

					cartItemList.get(i)[5] = "" + aantal;
					
					/*parse double bedrag */
					Double updatedBedrag = Double.parseDouble(cartItemList.get(i)[4].replace(',', '.')) * aantal;
					String updatedBedragString = decimalFormatter.format(updatedBedrag);
					
					/*update bedrag*/
					cartItemList.get(i)[6] = updatedBedragString;
							
				}
			}
		}
		
		if(paramArtikelnr != null && action != null && action.equals("deleteFromCart")) {
			for (Iterator<String[]> iter = cartItemList.listIterator(); iter.hasNext(); ) {
				String[] item = iter.next();
				if(item[0].equals(paramArtikelnr)) {
					iter.remove();
					itemCount--;
				}	    
			}

		}
		
		for(int i = 0; i<cartItemList.size(); i++) {
			totaal += Double.parseDouble(cartItemList.get(i)[6].replace(',', '.'));
		}
		
		
		totaalString = decimalFormatter.format(totaal);

		cartSession.setAttribute("itemCount", itemCount);
		cartSession.setAttribute("totaal", totaalString);
		cartSession.setAttribute("cartItemList", cartItemList);
		
		/*redirect user to previous-page*/
		response.sendRedirect(request.getHeader("referer"));
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
