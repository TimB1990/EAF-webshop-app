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
			
			HttpSession session = request.getSession();
			List<String>profielDataList = (List<String>) session.getAttribute("profielDataList");
			int klantnr = Integer.parseInt(profielDataList.get(0));
			int bestelnr = ProceduresOrders.createBestelnr();

			List<String[]>orderArtikelDetailList = (List<String[]>) session.getAttribute("orderArtikelDetailList");
			
			for(int i = 0; i < orderArtikelDetailList.size(); i++) {
				
				int artikelnr = Integer.parseInt(orderArtikelDetailList.get(i)[0]);
				int aantal = Integer.parseInt(orderArtikelDetailList.get(i)[2]);
				ProceduresOrders.insertIntoBesteld(artikelnr, aantal, bestelnr);
			}
			
			ProceduresOrders.insertIntoBestellingen(bestelnr, klantnr);
			message = "Bestelling succesvol";
			success = true;
			request.setAttribute("success", success);
			contentRoot = "orderProcessed";	
		}
		catch (ClassNotFoundException | SQLException e) {
			message = "plaatsen bestelling mislukt..." + e;
			e.printStackTrace();
		}
		
		request.setAttribute("message", message);
		request.setAttribute("contentRoot", contentRoot);
		
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);
		
	}

}
