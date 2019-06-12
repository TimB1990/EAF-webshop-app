package controllers;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.ProceduresArtikel;


/**
 * Servlet implementation class AddToBasketController
 */
@WebServlet(name = "/addToCart", urlPatterns = { "/addToCart" })
public class CartController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public CartController() {
        super();
    }

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String message = "product added to cart...";
		String action = request.getParameter("action");
		String paramArtikelnr = request.getParameter("artikelnr");
		
		int itemCount = 0;
		
		HttpSession cartSession = request.getSession(true);
		List<String[]>cartItemList = new ArrayList<String[]>();
		
		if(cartSession.getAttribute("cartItemList") != null) {
			cartItemList = (List<String[]>) cartSession.getAttribute("cartItemList");
		}
		else {
			cartItemList = new ArrayList<String[]>();
		}
		
		if(action != null && action.equals("emptyCart")) {
			for (Iterator<String[]> iter = cartItemList.listIterator(); iter.hasNext(); ) {
				String[] arr = iter.next();
			    iter.remove();
			}
			itemCount = 0;
		}
		
			
		if(action != null && action.equals("newItem")) {
			
			int artikelnr = Integer.parseInt(paramArtikelnr);
			String[]cartItem = new String[5];
		
			try {
				String[]productData = ProceduresArtikel.read(artikelnr);
				
				String artikelnrString = productData[0];
				String productnaam = productData[2];
				String prijs = productData[4];
				int aantal = 1;
				
				Double bedrag = Double.parseDouble(prijs.replace(',', '.')) * aantal;
				Locale currentLocale = Locale.getDefault();
				NumberFormat numberFormatter = NumberFormat.getNumberInstance(currentLocale);
				
				String pricePattern = "#,##0.00";
				DecimalFormat decimalFormatter = (DecimalFormat) numberFormatter;
				decimalFormatter.applyPattern(pricePattern);
				String bedragString = decimalFormatter.format(bedrag);
				
				cartItem[0] = artikelnrString;
				cartItem[1] = productnaam;
				cartItem[2] = "" + prijs;
				cartItem[3] = "" + aantal;
				cartItem[4] = bedragString;
				
				cartItemList.add(cartItem);
				itemCount = cartItemList.size();
	
			}
			catch (ClassNotFoundException | SQLException e) {
				message = "item niet gevonden..." + e;		
			}	
		}
		
		if(paramArtikelnr != null && action != null && action.equals("deleteFromCart")) {
			for (Iterator<String[]> iter = cartItemList.listIterator(); iter.hasNext(); ) {
				String[] item = iter.next();
				if(item[0].equals(paramArtikelnr)) {
					iter.remove();
				}	    
			}
			
			//itemCount should be decreased by 1, therefore top declaration must be itemCount = cartItemList.size()
		}
		
		cartSession.setAttribute("itemCount", itemCount);
		cartSession.setAttribute("cartItemList", cartItemList);
		
		request.setAttribute("message", message);
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
