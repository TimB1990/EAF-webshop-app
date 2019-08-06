package controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.ProceduresArtikel;

@WebServlet(name = "/artikel", urlPatterns = { "/artikel/*" })
public class SingleArtikelController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	String message;
	String contentRoot;
       

    public SingleArtikelController() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		message = "ok";
		String artikelnr = "";
		
		try {
			//get the request uri
			String uri = request.getRequestURI();
			System.out.println("uri: " + uri);
			
			//define regex pattern to filter out last digits from URI respresenting an artikelnr.
			Pattern pattern = Pattern.compile("[0-9]+$");
			Matcher matcher = pattern.matcher(uri);
			
			//if last digits that should represent the artikelnr can be found
			if(matcher.find()) {
				artikelnr = matcher.group();
				
				//create string[] object containing the properties of that specific artikel
				String[]artikelProps = ProceduresArtikel.read(request,Integer.parseInt(artikelnr));
				
				//set artikel properties as an attribute for the request scope. 
				request.setAttribute("artikelProps", artikelProps);
				
			}		
		} catch (ClassNotFoundException | SQLException e) {
			
			//In case of an exception, show message and display error.
			message = "inladen data single-artikel mislukt..." + e;			
		}
		
		//set content indicator to be used for index.jsp to display right content
		contentRoot = "single-artikel";
		
		//set message and content as attributes for request-scope.
		request.setAttribute("message", message);
		request.setAttribute("contentRoot", contentRoot);
		
		//forward request to index.jsp so that request scope can be accessed from within index.jsp.
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
