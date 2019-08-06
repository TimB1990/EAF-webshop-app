package controllers;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.ProceduresContactformulier;


@WebServlet(name = "ContactformController", urlPatterns = { "/contact" })
public class ContactformController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String contentRoot;
	String message;
	
	//set required time interval to 2 minutes (120.000 ms).
	private static final long REQUIRED_TIME_INTERVAL_MS = 120000;
	
	//the time the last request was made
	long lastRequestTimeLong;
	
	//the timedifference between last request time and current time.
	long requestsTimeDifference;
	
	//boolean that desides if contactform-data should be inserted into the database.
	boolean insertIntoDB;
       
    public ContactformController() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//set content to 'contact' and message to 'invullen contactformulier'.
		contentRoot = "contact";
		message = "invullen contactformulier";
		
		//set attributes 'contentRoot' and 'message' into request-scope
		request.setAttribute("contentRoot", contentRoot);
		request.setAttribute("message",message);
		
		//forward request to index.jsp so that request scope can be accessed from within index.jsp.
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//create session object to obtain lastRequestTimeMillis
		HttpSession session = request.getSession();
		
		//initialize parameters to retrieve
		String naam = request.getParameter("naam");
		String email = request.getParameter("email");
		String emailConfirm = request.getParameter("emailConfirm");
		String onderwerp = request.getParameter("onderwerp");
		String bericht = request.getParameter("bericht");
		
		//initialize attributes to be set
		String contactformErrMsg = "";
		String confirmMsg = "";
		String confirmationNotAllowed = "";
		
		//define errMsg as a result of 'validateInputContactform'
		contactformErrMsg = validateInputContactform(naam, email, emailConfirm, onderwerp, bericht);
		
		//check is error message is empty
		if(contactformErrMsg.contentEquals("")) 
		{
			//check if a last request time is not present, meaning no previous requests have been made.
			if(session.getAttribute("lastRequestTimeMillis") == null) {

				//set requests time difference to required time interval of 2 minutes (120.000 ms)
				requestsTimeDifference = REQUIRED_TIME_INTERVAL_MS;
				
				//allow insert into database
				insertIntoDB = true;
			}
			else {
				
				//calculate time difference by substracting current time - last request time
				requestsTimeDifference = System.currentTimeMillis() - (long) session.getAttribute("lastRequestTimeMillis");
				System.out.println("current ms: " + System.currentTimeMillis());
				System.out.println("last request ms: " + (long) session.getAttribute("lastRequestTimeMillis"));
				System.out.println("timedifference: " + requestsTimeDifference);
				
				//check if time difference is less than required time interval
				if(requestsTimeDifference < REQUIRED_TIME_INTERVAL_MS) {
					
					//do not allow insert into database
					insertIntoDB = false;
				}
				else {
					
					//otherwise if time difference is equal to or greather than required time interval, allow insert into database.
					insertIntoDB = true;
				}
			}
			
			//if insert into database is allowed
			if(insertIntoDB) {
				try 
				{
					//insert data into database
					ProceduresContactformulier.insertIntoContactform(request, naam, email, onderwerp, bericht);
					
					//set last request time to current time, and store it as an attribute in sessionscope.
					long lastRequestTimeMillis = System.currentTimeMillis();
					session.setAttribute("lastRequestTimeMillis",lastRequestTimeMillis);
					
					//set message, confirmation message, and content
					message = "Inzenden formulier succesvol";
					confirmMsg = "Bedankt voor het inzenden van het contactformulier, wij proberen uw vraag binnen 1 a 2 werkdagen te beantwoorden";
					contentRoot = "contactConfirmation";
				} 
				catch (ClassNotFoundException | SQLException e) 
				{
					//In case of an exception, show message and display error.
					message = "Verzenden contactformulier mislukt..." + e;
				}
			}
			else {
				//set message that says how many seconds the user should wait before another contact request can be made.
				confirmationNotAllowed = "U kunt pas over " + ((REQUIRED_TIME_INTERVAL_MS/1000) - (requestsTimeDifference /1000)) + " seconden het contact formulier inzenden!";
			}
			
		}

		//set request attributes for index.jsp
		request.setAttribute("message", message);
		request.setAttribute("contactformErrMsg", contactformErrMsg);
		request.setAttribute("confirmationNotAllowed", confirmationNotAllowed);
		request.setAttribute("contentRoot", contentRoot);
		request.setAttribute("confirmMsg", confirmMsg);
		
		System.out.println("all request attributes are defined");
		
		//forward request to /index.jsp
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);
		System.out.println("the request was beeing forwarded");

	}
	
	//input validation method for contactform
	private String validateInputContactform(String naam, String email, String emailConfirm, String onderwerp, String bericht) {
    	String errMsg = "";
    	
    	//if given naam parameter is not between 1 and 30 characters, show error message.
    	if(!(naam.length() > 1 && naam.length() < 30)) {
    		errMsg += "Uw naam moet tussen de 1 en 30 characters lang zijn!";
    		return errMsg;
    	}
    	
    	// if given username/email doesn't match regex for email, show error message.
    	if(!(email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"))) {
    		errMsg = "Het opgegeven email adres is ongeldig!";
    		return errMsg;
    	}
    	
    	// if emailConfirm parameter does not match given email parameter, show error message.
    	if(!(emailConfirm.contentEquals(email))) {
    		errMsg = "De door U opgegeven email adressen zijn niet hetzelfde!";
    		return errMsg;
    	}
    	
    	//if given onderwerp attribute is not between 3 and 60 characters, show error message.
    	if(!(onderwerp.length() > 3 && onderwerp.length() < 60)) {
    		errMsg = "Het onderwerp moet tussen de 3 en 60 characters lang zijn!";
    		return errMsg;
    	}
    	
    	//if given naam onderwerp is not between 3 and 60 characters, show error message.
    	if(!(bericht.length() > 10 && bericht.length() < 400)) {
    		errMsg = "Het bericht moet tussen de 10 en 400 characters lang zijn!";
    		return errMsg;
    	}
    	
    	return errMsg;
    }

}
