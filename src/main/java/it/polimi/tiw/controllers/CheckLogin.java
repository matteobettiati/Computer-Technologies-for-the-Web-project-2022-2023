package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;

@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;

	public void init() throws ServletException {
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
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new UnavailableException("Couldn't get db connection");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String error = "";

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		if (username == null || password == null) {
			
			error = "Invalid username or password";
			ctx.setVariable("error", error);
			templateEngine.process("/login.html", ctx, response.getWriter());

		} else {

			UserDAO userDAO = new UserDAO(connection);
			try {
				User user = userDAO.checkUser(username, password);
				if (user != null) {
					HttpSession session = request.getSession(true);
					session.setAttribute("currentUser", user);
					session.setAttribute("username", username);
					String path = getServletContext().getContextPath() + "/GoToHomepage";
					response.sendRedirect(path);
				} else {
					error = "Invalid username or password";
					String path = "login.html";
					ctx.setVariable("error", error);
					templateEngine.process(path, ctx, response.getWriter());
					return;
				}
			} catch (SQLException e) {
				response.sendError(500, "Database access failed");
			}
		}
	}
	

	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}

}