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
       

    public SingleArtikelController() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String artikelnr = "";
		String message = "ok";
		
		try {
			String uri = request.getRequestURI();
			System.out.println("uri: " + uri);
			Pattern pattern = Pattern.compile("[0-9]+$");
			Matcher matcher = pattern.matcher(uri);
			if(matcher.find()) {
				artikelnr = matcher.group();
				String[]artikelProps = ProceduresArtikel.read(Integer.parseInt(artikelnr));
				request.setAttribute("artikelProps", artikelProps);
				
			}		
		} catch (ClassNotFoundException | SQLException e) {
			message = "inladen data single-artikel mislukt..." + e;			
		}
		
		String contentRoot = "single-artikel";
		request.setAttribute("message", message);
		request.setAttribute("contentRoot", contentRoot);
		
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
