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
		
		//create list of string-arrays to represent cartitem list.
		List<String[]>cartItemList = new ArrayList<String[]>();
		
		//check if the cartItemList attribute is present in sessionscope
		if(cartSession.getAttribute("cartItemList") != null) {
			
			//retrieve cartItemList from sessionscope
			cartItemList = (List<String[]>) cartSession.getAttribute("cartItemList");
			
			//set itemCount equal to the size of cartItemList
			itemCount = cartItemList.size();
		}
		else {
			//other set cartItemList to new list of string arrays.
			cartItemList = new ArrayList<String[]>();
		}
		
		//check is parameter 'action' has value 'emptyCart'
		if(action != null && action.equals("emptyCart")) {
			
			//us a Iterator to iterate over whole cartItemList
			for (Iterator<String[]> iter = cartItemList.listIterator(); iter.hasNext(); ) {
				@SuppressWarnings("unused")
				String[] arr = iter.next();
				
				//remove list index
			    iter.remove();
			}
			
			//set itemcount to 0
			itemCount = 0;
		}
		
		//check whether action parameter has value 'newItem'	
		if(action != null && action.equals("newItem")) {
			
			//set artikelnr from given artikelnr parameter.
			int artikelnr = Integer.parseInt(paramArtikelnr);
			String[]cartItem = new String[7];
			
			//int aantal = 1;
			Boolean update = false;

			/*check if paramArtikelnr exists in cartItemList*/
			for (int i = 0; i<cartItemList.size(); i++) {
				if(cartItemList.get(i)[0].equals(paramArtikelnr)) {
					
					/*update aantal*/
					aantal = Integer.parseInt(cartItemList.get(i)[5]) + 1;
					cartItemList.get(i)[5] = "" + aantal;
					
					/*parse double bedrag */
					Double updatedBedrag = Double.parseDouble(cartItemList.get(i)[4].replace(',', '.')) * aantal;
					String updatedBedragString = decimalFormatter.format(updatedBedrag);
					
					/*update bedrag*/
					cartItemList.get(i)[6] = updatedBedragString;
					
					/*set updateBoolean to true*/
					update = true;			
				}
			}
			
			//if updateBoolean is false
			if(!update) {
				try {
					
					//get data from artikelnr via database
					String[]productData = ProceduresArtikel.read(request,artikelnr);
					
					String artikelnrString = productData[0];
					String productnaam = productData[2];
					String prijs = productData[4];
					String gewicht = productData[5];
					String btw = productData[6];
					
					Double bedrag = Double.parseDouble(prijs.replace(',', '.')) * aantal;
					String bedragString = decimalFormatter.format(bedrag);
					
					//put data into cartItem array
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
					//if an error occurs, print error.
					e.printStackTrace();		
				}	
				
			}
		}
		
		//if given parameter for artikelnr is not null and parameter action is not null.
		if(paramArtikelnr != null && action != null) {
			
			//loop over cartItemList
			for (int i = 0; i<cartItemList.size(); i++) {
				
				//check if the first array-index of current list-index equals the given parameter for artikelnr
				if(cartItemList.get(i)[0].equals(paramArtikelnr)) {
					
					//get amount of current product on cartItemList. 
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

					//set 5th array index of current list-index to empty string + new aantal/
					cartItemList.get(i)[5] = "" + aantal;
					
					/*get and parse double bedrag */
					Double updatedBedrag = Double.parseDouble(cartItemList.get(i)[4].replace(',', '.')) * aantal;
					String updatedBedragString = decimalFormatter.format(updatedBedrag);
					
					/*update bedrag*/
					cartItemList.get(i)[6] = updatedBedragString; 
							
				}
				
			}
		}
		
		//check if parameters 'paramArtikelNr' and 'deleteFromCart' are given from index.jsp
		if(paramArtikelnr != null && action != null && action.equals("deleteFromCart")) {
			
			//iterate over cartItemList
			for (Iterator<String[]> iter = cartItemList.listIterator(); iter.hasNext(); ) {
				
				//set string[] object item to next iteration of cartItemList
				String[] item = iter.next();
				
				//check if first arrayindex of item equals value of given artikelnr (paramArtikelnr)
				if(item[0].equals(paramArtikelnr)) {
				
					//remove the object (string[]) from cartItemList beeing iterated. 
					iter.remove();
					
					//decrease itemcount by one
					itemCount--;
				}	    
			}

		}
		
		//loop over cartItemList
		for(int i = 0; i<cartItemList.size(); i++) {
			
			//increase totaal by the 6th array-index of current list-index and make sure ',' is replaced by '.' so String can be parsed to Double.
			totaal += Double.parseDouble(cartItemList.get(i)[6].replace(',', '.'));
		}
		
		//create string-object to represent 'totaal' which is given a Double.
		totaalString = decimalFormatter.format(totaal);

		//set attributes 'itemCount', 'totaal' and 'cartItemList' to sessionScope 
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
