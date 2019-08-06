package controllers;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.*;
import java.util.List;

import model.ProceduresBericht;
import model.ProceduresCategorie;

@WebServlet(name = "/index", urlPatterns = { "/index" })
public class StartupController extends HttpServlet {
	
	String message;
	String contentRoot;
	
	private static final long serialVersionUID = 1L;
	   
    public StartupController() {
        super();
    }
    

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//at startup make sure that categorieList stays updated...
		HttpSession session = request.getSession(true);
		if(session.getAttribute("categorieList")!= null) {
			session.removeAttribute("categorieList");
		}

		message = "ok";	
		
		try {
			
			//list categories in string[] list object to be shown in headermenu
			List<String[]>categorieList = ProceduresCategorie.listCategorie(request);
			
			//list berichten in string[] list object to display homepage messages 
			List<String[]>berichtList = ProceduresBericht.listBericht(request);
			
			//set lists as attributes for session and request-scope.
			request.setAttribute("berichtList", berichtList);
			session.setAttribute("categorieList", categorieList);
			
		} catch (ClassNotFoundException | SQLException e) {
			
			//In case of an exception, show message and display error
			message = "inladen data list mislukt..." + e;
			
		}
		
		//set content indicator to be used for index.jsp to display right content
		contentRoot = "berichten";
		
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
