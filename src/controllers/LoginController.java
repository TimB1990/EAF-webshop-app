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

import model.ProceduresProfiel;


@WebServlet(name = "loginController", urlPatterns = { "/login" })
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	String contentRoot;
	String message;
	String errMsg;
	String loggedIn;

    public LoginController() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		loggedIn = (String) request.getSession().getAttribute("loggedIn");
		message = "please login";
		
		if(loggedIn == null || loggedIn.contentEquals("false")) {
			contentRoot = "loginForm";
		}
		else if(loggedIn != null && loggedIn.contentEquals("true")) {
			contentRoot = "profiel";
		}
		
		request.setAttribute("contentRoot", contentRoot);
		request.setAttribute("message", message);
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		HttpSession loginSession = request.getSession(true);
		loginSession.setMaxInactiveInterval(120);
		
		String user = request.getParameter("user");
		String pw = request.getParameter("pw");
		
		try {
			List<String>profielDataList = ProceduresProfiel.read(user, pw);
			
			if(profielDataList.get(0).contentEquals("NotFound")) {
				loggedIn = "false";
				errMsg = "De opgegeven combinatie van gebruikersnaam en wachtwoord is onjuist!";
				contentRoot = "loginForm";
			}
			else {
				
				loggedIn = "true";
				message = "login succesvol";
				contentRoot = "profiel";
				loginSession.setAttribute("profielDataList", profielDataList);
			}
			
		}
		catch (ClassNotFoundException | SQLException e) {
			message = "inladen profiel gegevens mislukt..." + e;			
		}
		
		loginSession.setAttribute("loggedIn", loggedIn);
		request.setAttribute("errMsg", errMsg);
		request.setAttribute("contentRoot", contentRoot);
		request.setAttribute("message", message);
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);

		//doGet(request, response);
	}
}
