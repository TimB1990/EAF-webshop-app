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


@WebServlet(name = "loginController", urlPatterns = { "/profiel" })
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//get session-object from request
		loginSession = request.getSession();

		//get attribute 'loggedIn' from sessionscope
		loggedIn = (String) request.getSession().getAttribute("loggedIn");

		//check whether loggedIn is null or loggedIn equals false.
		if (loggedIn == null || loggedIn.contentEquals("false")) {
			
			//set content to 'loginForm' and set message to 'log alstublieft in'
			contentRoot = "loginForm";
			message = "log alstublieft in";
			
		} else {
			
			//otherwise set content to 'profiel' and set message to 'login succesvol'
			contentRoot = "profiel";
			message = "login succesvol";

		}

		//check if parameter 'logout' is not null and parameter 'logout' has value "true'.
		if (request.getParameter("logout") != null && request.getParameter("logout").contentEquals("true")) {
			
			//set value of loggedIn to 'false'
			loggedIn = "false";
			
			//reset 'loggedIn' session-attribute.
			loginSession.setAttribute("loggedIn", loggedIn);
			
			//create String-array object to put "guest" as 
			String[]credentials = {"guest","guest"};
			loginSession.setAttribute("credentials",credentials);
		
			//set contentRoot to 'loginForm' and message to 'uitloggen succesvol'
			contentRoot = "loginForm";
			message = "uitloggen succesvol";

		}

		//set attributes 'contentRoot' and 'message' into request-scope.
		request.setAttribute("contentRoot", contentRoot);
		request.setAttribute("message", message);
		
		//forward request to index.jsp so that request scope can be accessed from within index.jsp.
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		loginSession = request.getSession();
		loginSession.setMaxInactiveInterval(300);

		String user = request.getParameter("user");
		String pw = request.getParameter("pw");

		try {
			List<String> profielDataList = ProceduresProfiel.read(request, user, pw);

			if (profielDataList.get(0).contentEquals("NotFound")) {
				errMsg = "De opgegeven combinatie van gebruikersnaam en wachtwoord is onjuist!";
				loggedIn = "false";
				contentRoot = "loginForm";

			} else {
				loggedIn = "true";
				message = "login succesvol";
				contentRoot = "profiel";
				
				String[]credentials = {user,pw};
				loginSession.setAttribute("credentials",credentials);
				loginSession.setAttribute("profielDataList", profielDataList);
				String klantnr = profielDataList.get(0);
				
				List<String[]> customerOrderList = ProceduresOrders.listCustomerOrders(request,klantnr);
				loginSession.setAttribute("customerOrderList", customerOrderList);
			}

		} catch (ClassNotFoundException | SQLException e) {
			message = "inladen profiel gegevens mislukt..." + e;
		}

		if (request.getHeader("referer").contentEquals("http://localhost:8080/EAFwebshop/newOrder")) {
			contentRoot = "newOrder";
		}

		loginSession.setAttribute("loggedIn", loggedIn);
		request.setAttribute("errMsg", errMsg);
		request.setAttribute("contentRoot", contentRoot);
		request.setAttribute("message", message);
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);

		// doGet(request, response);
	}
}
