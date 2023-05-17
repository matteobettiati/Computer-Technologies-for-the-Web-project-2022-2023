package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.dao.UserDAO;

@WebServlet("/CheckSignUp")
public class CheckSignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;


	public void init() {
		ServletContext context = getServletContext();

		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");

		try {
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("user");
		String password = request.getParameter("password");

		String error = "";
		boolean added = false;

		// check if the parameters are not empty or null
		if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
			error += "Missing parameters;";
			String path = "signupPage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorFriomSignup", error);
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}

		// Check if the password contain at least one number and one special character
		// and if it has a size bigger than 4
		if (username.equals(password))
			error += "password non puo essere uguale al nickname!";

		// Check if the userName is too long
		if (username.length() > 45)
			error += "UserName too long;";

		// Check if the password is too long
		if (password.length() > 45)
			error += "Password too long;";

		if (!error.equals("")) {
			String path = "signupPage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorFromSignup", error);
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}

		UserDAO userDao = new UserDAO(connection);

		try {
			added = userDao.addUser(username, password);

			if (added == true) {
				// Redirect to the login page
				String path = getServletContext().getContextPath() + "/login.html";
				response.sendRedirect(path);
			} else {
				String path = "signupPage.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				error += "Username utilizzato da qualcun'altro";
				ctx.setVariable("errorFromSignup", error);
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Issue with DB");
			return;
		}
	}

	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
