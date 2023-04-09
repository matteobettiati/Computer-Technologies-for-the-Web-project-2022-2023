package it.polimi.tiw.playlist;

import java.beans.Statement;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.interfaces.RSAKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
   
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		try {
			//connection with mySQL
			Class.forName("com.mysql.cj.jdbc.Driver");
			
			// URL
			// database used
			// user ID
			// password
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/users?serverTimezone=UTC","root","BluBanan69!");
			
			java.sql.Statement stm = con.createStatement();
			String query = "select * from saved_users where username='"+username+"' and password='"+password+"'";
			ResultSet res = stm.executeQuery(query);
			
			if(res.next()) 
				response.sendRedirect("homepage.html");	
			else {
				response.sendRedirect("login.html");
				//out.println("wrong username or password");
				
			}
			con.close();
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException{
				
			}

}
