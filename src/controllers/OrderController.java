package controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ProceduresOrders;

@WebServlet(name = "order", urlPatterns = { "/order/*"})
public class OrderController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	

    public OrderController() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		List<String[]>orderArtikelList = new ArrayList<String[]>();
		String message = "OK";
		try{
			String uri = request.getRequestURI();
			Pattern pattern = Pattern.compile("[0-9]+$");
			Matcher matcher = pattern.matcher(uri);
			if(matcher.find()) {
				String bestelnr = matcher.group();
				orderArtikelList = ProceduresOrders.readOrder(bestelnr);
				request.setAttribute("bestelnr", bestelnr);
				request.setAttribute("orderArtikelList", orderArtikelList);
				List<String>costSpecificationList = ProceduresOrders.getCostSpecification(orderArtikelList);
				request.setAttribute("costSpecificationList", costSpecificationList);
					
			}		
		}
		catch (ClassNotFoundException | SQLException e) {
			message = "inladen data artikel-list mislukt..." + e;			
		}
		
		
		String contentRoot = "order";
		request.setAttribute("message", message);
		request.setAttribute("contentRoot", contentRoot);
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		//TODO
	}
}
