package it.polimi.tiw.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

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

import it.polimi.tiw.beans.Playlist;
import it.polimi.tiw.beans.Songs;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.PlaylistDAO;
import it.polimi.tiw.dao.SongDAO;

@WebServlet("/GoToHomepage")
public class GoToHomepage extends HttpServlet {
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

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String error = "";
		String error1 = "";
		String message = "";
		HttpSession session = request.getSession(false); //
		if (session == null || session.getAttribute("currentUser") == null) { // controls if user is NOT logged in
			String path = getServletContext().getContextPath(); //
			response.sendRedirect(path); //
		} //
		else {
			PlaylistDAO playlistDAO = new PlaylistDAO(connection);
			SongDAO songDAO = new SongDAO(connection);
			List<Playlist> playlists = null;
			List<Songs> songs = null;
			int userId = ((User) session.getAttribute("currentUser")).getIdUser();
			try {
				
				playlists = playlistDAO.getPlaylistsByUser(userId);
				songs = songDAO.getSongsByUser(userId);
				
				
			} catch (SQLException e) {
				response.sendError(500, "Database access failed");
			}
					
			
			String username = request.getParameter("username");
			String path = "/WEB-INF/homepage.html";
			
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			
			if(((String) request.getAttribute("error")) != null) 
				error = (String) request.getAttribute("error");
			else if(((String) request.getAttribute("error1")) != null) 
				error1 = (String) request.getAttribute("error1");
			else if(((String) request.getAttribute("message")) != null)
				message = (String) request.getAttribute("message");
			
			try {
				playlists = playlistDAO.findPlaylist(userId);
			}catch(SQLException e) {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database extraction");
			}
			
			ctx.setVariable("playlists" , playlists);
			ctx.setVariable("errorMsg", error);
			ctx.setVariable("errorMsg1", error1);
			
			ctx.setVariable("message",message);
			
			ctx.setVariable("playlists", playlists);
			ctx.setVariable("songsInThedb", songs);
			ctx.setVariable("username", username);
			templateEngine.process(path, ctx, response.getWriter());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
