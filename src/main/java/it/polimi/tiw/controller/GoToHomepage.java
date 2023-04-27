package it.polimi.tiw.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.Playlist;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.PlaylistDAO;


/**
 * Servlet implementation class GoToHomepage
 */
@WebServlet("/GoToHomepage")
public class GoToHomepage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;


	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);   					  // 
		if (session == null || session.getAttribute("currentUser") == null) { // controls if user is NOT logged in
			String path = getServletContext().getContextPath();				  //
			response.sendRedirect(path);									  //
		}																	  //
		else {
			PlaylistDAO playlistDAO = new PlaylistDAO(connection);
			List<Playlist> playlists;
			int userId = ((User) session.getAttribute("currentUser")).getIdUser();
			//try {
				String path = "/WEB-INF/homepage.html";
				
				//playlists = playlistDAO.findPlaylistsByIUser(userId);
				//request.setAttribute("playlists", playlists);
				RequestDispatcher dispatcher = request.getRequestDispatcher(path);
				dispatcher.forward(request, response);
			//} catch (SQLException e) {
				//response.sendError(500, "Database access failed");
			//}			
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
