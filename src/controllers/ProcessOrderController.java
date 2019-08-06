package controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.ProceduresOrders;

@WebServlet(name = "ProcessOrderController", urlPatterns = {"/processOrder"})
public class ProcessOrderController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String message;
	Boolean success;
	String contentRoot;
       
    public ProcessOrderController() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}


	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try {
			
			//get the session object from request.
			HttpSession session = request.getSession();
			
			//get attribute profielDataList from sessionScope.
			List<String>profielDataList = (List<String>) session.getAttribute("profielDataList");
			
			//get stored klantnr from the first index of profielDataList
			int klantnr = Integer.parseInt(profielDataList.get(0));
			
			//get a new bestelnr by calling createBestelnr-method parsing in the request object. 
			int bestelnr = ProceduresOrders.createBestelnr(request);

			//get list-object of string arrays containing the details of the articles beeing ordered from sessionScope.
			List<String[]>orderArtikelDetailList = (List<String[]>) session.getAttribute("orderArtikelDetailList");
			
			//do 'for' loop over orderArtikelDetailList
			for(int i = 0; i < orderArtikelDetailList.size(); i++) {
				
				//retrieve artikelnr from first arrayindex of current list-index beeing looped over. 
				int artikelnr = Integer.parseInt(orderArtikelDetailList.get(i)[0]);
				
				//retrieve aantal (amount of the product ordered) from third arrayindex of current list-index beeing looped over.
				int aantal = Integer.parseInt(orderArtikelDetailList.get(i)[2]);
				
				//insert artikelnr, aantal and bestelnr in table 'besteld' containing each artikelnr beeing ordered and the amount of that article. 
				ProceduresOrders.insertIntoBesteld(request,artikelnr, aantal, bestelnr);
			}
			
			//call insertIntoBestelling procedure parsing in request-object, bestelnr and klantnr, to insert order into database.
			ProceduresOrders.insertIntoBestellingen(request,bestelnr, klantnr);
			
			//set message to 'bestelling succesvol' and set boolean success to true
			message = "Bestelling succesvol";
			success = true;
			
			//set success as request attribute
			request.setAttribute("success", success);
			
			//define content as 'orderProcessed'. 
			contentRoot = "orderProcessed";	
		}
		catch (ClassNotFoundException | SQLException e) {
			
			//In case of an exception, show message and display error.
			message = "plaatsen bestelling mislukt..." + e;
			e.printStackTrace();
		}
		
		//set attributes contentRoot and message.
		request.setAttribute("message", message);
		request.setAttribute("contentRoot", contentRoot);
		
		//forward request to index.jsp so that request scope can be accessed from within index.jsp.
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);
		
	}

}
