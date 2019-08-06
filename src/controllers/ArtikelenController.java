package controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.ProceduresArtikel;
import model.ProceduresCategorie;

@WebServlet(name = "/categorie", urlPatterns = { "/categorie/*" })
public class ArtikelenController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

    public ArtikelenController() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String categoryName = "";

		System.out.println("categorie: " + categoryName);
		
		String message = "ok";
		
		try {
			String uri = request.getRequestURI();
			System.out.println("uri: " + uri);
			Pattern pattern = Pattern.compile("[a-zA-Z]+$");
			Matcher matcher = pattern.matcher(uri);
			if(matcher.find()) {
				categoryName = matcher.group();
				String[]categorieProps = ProceduresCategorie.read(request,categoryName);
				List<String[]>artikelList = ProceduresArtikel.list(request, categoryName);
				request.setAttribute("categorieProps", categorieProps);
				request.setAttribute("artikelList", artikelList);
				
			}		
		} catch (ClassNotFoundException | SQLException e) {
			message = "inladen data artikel-list mislukt..." + e;			
		}
		
		String contentRoot = "artikelen";
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
