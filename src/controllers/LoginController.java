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
import model.ProceduresProfiel;


@WebServlet(name = "loginController", urlPatterns = { "/login" })
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	HttpSession loginSession;
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
		
		if(request.getParameter("logout") != null && request.getParameter("logout").contentEquals("true")) {
			loggedIn = "false";
			loginSession.setAttribute("loggedIn", loggedIn);
			contentRoot = "loginForm";
			message = "uitloggen succesvol";	

		}
		
		if(loggedIn == null || loggedIn.contentEquals("false")) {
			contentRoot = "loginForm";
			message = "log alstublieft in";
		}
		else {
			contentRoot = "profiel";
			message = "login succesvol";
		}
			
		request.setAttribute("contentRoot", contentRoot);
		request.setAttribute("message", message);
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
	
		loginSession = request.getSession();
		loginSession.setMaxInactiveInterval(300);
		
		String user = request.getParameter("user");
		String pw = request.getParameter("pw");
		
		try {
			List<String>profielDataList = ProceduresProfiel.read(user, pw);
			
			
			if(profielDataList.get(0).contentEquals("NotFound")) 
			{
				errMsg = "De opgegeven combinatie van gebruikersnaam en wachtwoord is onjuist!";
				loggedIn = "false";
				contentRoot = "loginForm";

			}
			else 
			{
				loggedIn = "true";
				message = "login succesvol";
				contentRoot = "profiel";
						
				loginSession.setAttribute("profielDataList", profielDataList);
				String klantnr = profielDataList.get(0);
				List<String[]>customerOrderList = ProceduresOrders.listCustomerOrders(klantnr);
				loginSession.setAttribute("customerOrderList", customerOrderList);
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
