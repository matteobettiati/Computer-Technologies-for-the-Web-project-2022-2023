package it.polimi.tiw.controllers;

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
import it.polimi.tiw.beans.Song;
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
		String errorFromCreatePlaylist = "";
		String errorUploadingSong = "";
		String errorFromGoToPlaylist= "";
		String message = "";
		
		HttpSession s = request.getSession(false); //
		if (s == null || s.getAttribute("currentUser") == null) { // controls if user is NOT logged in
			String path = getServletContext().getContextPath();
			response.sendRedirect(path);
		} else {
			PlaylistDAO playlistDAO = new PlaylistDAO(connection);
			SongDAO songDAO = new SongDAO(connection);
			List<Playlist> playlists = null;
			List<Song> songs = null;
			int userId = ((User) s.getAttribute("currentUser")).getIdUser();
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

			
			if (((String) request.getSession().getAttribute("errorFromCreatePlaylist")) != null)
				errorFromCreatePlaylist = (String) request.getSession().getAttribute("errorFromCreatePlaylist");
			if (((String) request.getSession().getAttribute("errorUploadingSong")) != null)
				errorUploadingSong = (String) request.getSession().getAttribute("errorUploadingSong");
			if (((String) request.getSession().getAttribute("errorFromGoToPlaylist")) != null)
				errorFromGoToPlaylist = (String) request.getSession().getAttribute("errorFromGoToPlaylist");

			ctx.setVariable("playlists", playlists);
			ctx.setVariable("playlists", playlists);
			ctx.setVariable("songsInThedb", songs);
			ctx.setVariable("username", username);
			
			ctx.setVariable("errorFromCreatePlaylist", errorFromCreatePlaylist);
			ctx.setVariable("errorUploadingSong", errorUploadingSong);
			ctx.setVariable("errorFromGoToPlaylist", errorFromGoToPlaylist);
			
			templateEngine.process(path, ctx, response.getWriter());
			
			s.removeAttribute("error");
			s.removeAttribute("errorUploadingSong");
			s.removeAttribute("errorFromGoToPlaylist");
			s.removeAttribute("errorFromCreatePlaylist");
			
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
