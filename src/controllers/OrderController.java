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
	
	String contentRoot, message;
	

    public OrderController() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		List<String[]>orderArtikelList = new ArrayList<String[]>();
		message = "OK";
		try{
			
			//get the request uri
			String uri = request.getRequestURI();
			
			//define regex pattern to filter out last digits from URI respresenting an artikelnr.
			Pattern pattern = Pattern.compile("[0-9]+$");
			Matcher matcher = pattern.matcher(uri);
			
			//if last digits that should represent the bestelnr can be found
			if(matcher.find()) {
				String bestelnr = matcher.group();
				
				//define orderArtikelList containing the articles of requested order and its properties
				orderArtikelList = ProceduresOrders.readOrder(request,bestelnr);
				
				//set request attributes bestelnr, and orderArtikelList
				request.setAttribute("bestelnr", bestelnr);
				request.setAttribute("orderArtikelList", orderArtikelList);
				
				//Create list of strings that represents order specification details (costSpecificationList) by parsing the orderArtikelList defined previously.
				List<String>costSpecificationList = ProceduresOrders.getCostSpecification(orderArtikelList);
				
				//set attribute 'costSpecificationList'
				request.setAttribute("costSpecificationList", costSpecificationList);
					
			}		
		}
		catch (ClassNotFoundException | SQLException e) {
			
			//In case of an exception, show message and display error.
			message = "inladen data artikel-list mislukt..." + e;			
		}
		
		//set content to "order"
		contentRoot = "order";
		
		//set attributes 'message' and 'contentRoot' into request-scope
		request.setAttribute("message", message);
		request.setAttribute("contentRoot", contentRoot);
		
		//forward request to index.jsp so that request scope can be accessed from within index.jsp.
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);
	}

}
