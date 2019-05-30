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
	
	private static final long serialVersionUID = 1L;
	   
    public StartupController() {
        super();
        // TODO Auto-generated constructor stub
    }
    

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		if(session.getAttribute("categorieList")!= null) {
			session.removeAttribute("categorieList");
		}

		String message = "ok";	
		try {
			List<String[]>categorieList = ProceduresCategorie.listCategorie();
			List<String[]>berichtList = ProceduresBericht.listBericht();
			request.setAttribute("berichtList", berichtList);
			session.setAttribute("categorieList", categorieList);
		} catch (ClassNotFoundException | SQLException e) {
			message = "inladen data list mislukt..." + e;
			
		}
		
		String contentRoot = "berichten";
		
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
