package controllers;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Utility.DBconnect;

@WebServlet(name = "/register", urlPatterns = { "/register" })
public class RegistrationController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String contentRoot;
	String message;
	String registrationErrMsg;
	boolean createUser;
       

    public RegistrationController() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//if a request to this java servlet have been made by clicking a link in index.jsp, set attributes message to OK and content to registrationForm.
		message = "OK";
		contentRoot = "registrationForm";
		
		//set message and content as attributes for request-scope.
		request.setAttribute("contentRoot", contentRoot);
		request.setAttribute("message",message);
		
		//forward request to index.jsp so that request scope can be accessed from within index.jsp.
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);

	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//retrieve all given parameters from the registration form.
		String email = request.getParameter("email");
		String pwd = request.getParameter("pwd");
		String pwdConfirm = request.getParameter("pwdConfirm");
		String voornaam = request.getParameter("voornaam");
		String achternaam = request.getParameter("achternaam");
		String gender = request.getParameter("gender");
		String straat = request.getParameter("straat");
		String huisnr = request.getParameter("huisnr");
		String pc = request.getParameter("pc");
		String wp = request.getParameter("wp");
		
		//call the validateInputRegistration-function that displays the right error message whenever the validation fails due to any cause. 
		registrationErrMsg = validateInputRegistrationform(email, pwd, pwdConfirm, voornaam, achternaam, gender, straat, huisnr, pc, wp);
		
		//check whether given email is unique
		try 
		{
			//create a connection to the database and parse in the request-object.
			Connection conn = DBconnect.getConnection(request);
			
			//create a callable statement that refers to check_existing_email mysql (stored) procedure. 
			CallableStatement stmt = conn.prepareCall("{call check_existing_email(?)}");
			
			//parse in the email address for the '?' postition
			stmt.setString(1, email);
			
			//create resultset by calling executeQuery();
			ResultSet rs = stmt.executeQuery();
			
			//check whether resultset containts data.
			if(rs.next()) {
				
				//when resultset contains data, don't allow a new user to be created.
				createUser = false;
				
				//set the error message to following...
				registrationErrMsg = "Het opgegeven email adres is al geregistreerd!";
			}
			else {
				//ohterwise allow to create new user. 
				createUser = true;
			}
			
			conn.close();
				
		}
		catch (ClassNotFoundException | SQLException e) 
		{
			//In case of an exception, show message and display error.
			message = "Kan email niet valideren..." + e;
		}
		
		//check if a new user is allowed to be created. 
		if(createUser) {
			try 
			{
				//make a connection to the database and parse in request-object
				Connection conn = DBconnect.getConnection(request);
				
				//create a callable statement that refers to create_klant mysql (stored) procedure. 
				CallableStatement stmt = conn.prepareCall("{call create_klant(?,?,?,?,?,?,?,?,?)}");
				
				//parse in parameters in sequence of the '?' characters
				stmt.setString(1,email);
				stmt.setString(2, pwd);
				stmt.setString(3,voornaam);
				stmt.setString(4,achternaam);
				stmt.setString(5,gender);
				stmt.setString(6,straat);
				stmt.setString(7,huisnr);
				stmt.setString(8, pc);
				stmt.setString(9,wp);
				
				//execute statement (no resultset needs to be returned)
				stmt.executeUpdate();
				conn.close();
				
				//set attributes message and content.
				message = "gebruiker succesvol geregistreerd!";
				contentRoot = "registrationConfirmed";
			}
			catch (ClassNotFoundException | SQLException e) 
			{
				//In case of an exception, show message and display error.
				message = "kan gebruiker niet registreren" + e;
			}
		}
		
		//set attributes registrationErrmsg, contentRoot and message in request-scope.
		request.setAttribute("registrationErrMsg", registrationErrMsg);
		request.setAttribute("contentRoot", contentRoot);
		request.setAttribute("message", message);
		
		//forward request to index.jsp so that request scope can be accessed from within index.jsp.
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		rd.forward(request, response);	
	}

	
	//method to perform input validation
	private String validateInputRegistrationform(
			String email, 
			String pwd, 
			String pwdConfirm, 
			String voornaam, 
			String achternaam, 
			String gender,
			String straat,
			String huisnr,
			String pc,
			String wp) {
		
    	String errMsg = "";
    	
    	// if given username/email doesn't match regex for email, show error message.
    	if(!(email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"))) {
    		errMsg = "Het opgegeven email adres is ongeldig!";
    		return errMsg;
    	}
    	
    	// if length of given password greather than 64 characters, show error message.
    	if(pwd.length() > 64) {
    		errMsg = "Uw wachtwoord mag niet meer dan 64 characters lang zijn!";
    		return errMsg;
    	}
    	
    	//if given password doesn't match regex that indicates strong password, show error message. Notice this section can be commented out anyway.
    	if(!(pwd.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"))){
    		errMsg = "Uw wachtwoord is niet sterk genoeg, zorg dat uw wachtwoord tenministe één nummer, één kleine letter, één hoofdletter, één speciaal character en "
    				+ "geen spaties bevat en tenminste 8 characters lang is...";
    		return errMsg;
    	}
    	
    	//if the given password as confirmation does not equal given password, show error message.
    	if(!(pwdConfirm.contentEquals(pwd))) {
    		errMsg = "De opgegeven wachtwoorden zijn niet hetzelfde!";
    		return errMsg;
    	}
    	
    	//if voornaam does not have a length between 1 and 30 characters, show error message.
    	if(!(voornaam.length() > 1 && voornaam.length() < 30)) {
    		errMsg = "Uw voornaam moet tussen de 1 en 30 characters lang zijn!";
    		return errMsg;
    	}
    	
    	//if achternaam does not have a length between 1 and 30 characters, show error message.
    	if(!(achternaam.length() > 1 && achternaam.length() < 30)) {
    		errMsg = "Uw achternaam moet tussen de 1 en 30 characters lang zijn!";
    		return errMsg;
    	}
    	
    	//if straat does not have a length between 1 and 30 characters, show error message.
    	if(!(straat.length() > 2 && straat.length() < 30)) {
    		errMsg = "De opgegeven straatnaam moet tussen de 2 en 30 characters lang zijn!";
    		return errMsg;
    	}
    	
    	//if huisnr does not not start with a number, ends with a number or ends with a alphabetical character, or is larger than 4 characters,show error message
    	if(!(huisnr.matches("((^[0-9]{1,3}$)|(^[0-9]{1,3}[A-Za-z]$))"))) {
    		errMsg = "Het opgegeven huisnummer mag alleen een combinatie van 1 of meerdere getallen of 1 of meerdere getallen gevolgt door een letter, en niet langer dan 4 characters zijn!";
    		return errMsg;
    	}
    	
    	//if zip-code (postcode) doesn't match 4 digits and two alphabetical characters, show error message.
    	if(!(pc.matches("[0-9]{4}[A-Za-z]{2}"))) {
    		errMsg = "Het systeem accepteert alleen Nederlandse postcodes, 4 cijfers gevolgt door 2 letters!";
    		return errMsg;
    	}
    	
    	//if city (woonplaats) does not have a length between 2 and 30 characters, show error message.
    	if(!(wp.length() > 2 && wp.length()<30)) {
    		errMsg = "De opgegeven woonplaats moet tussen de 2 en 30 characters lang zijn!";
    		return errMsg;
    	}
    		
    	return errMsg;
    }
	
	

}
