package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.PlaylistDAO;

@WebServlet("/AddSong")
public class AddSong extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
	public void init() {
		try {
			//Initializing the connection
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			
			Class.forName(driver);
			connection = DriverManager.getConnection(url , user , password);
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void doPost(HttpServletRequest request , HttpServletResponse response)throws ServletException,IOException{
		String playlistId = request.getParameter("playlistId");
		String songId = request.getParameter("song");
		String error = "";
		int pId = -1;
		int sId = -1;
		
		HttpSession s = request.getSession();
		
	    User user = (User) s.getAttribute("currentUser");
		
		if (s.isNew() || user == null) {
			response.sendRedirect("/SongsPlaylist/login.html");
			return;
		}
		
	    
		if(playlistId == null || playlistId.isEmpty() || songId == null || songId.isEmpty()) {
			error += "Missing parameter;";
		}
		
		if(error.equals("")) {
			try {
				
				//Check if the playlistId and songId are numbers
				pId = Integer.parseInt(playlistId);
				sId = Integer.parseInt(songId);
				

			}catch(NumberFormatException e) {
				error += "Playlist not defined;";
			}
		}
		
		// case of error
		if(!error.equals("")) {
			s.setAttribute("errorFromAddSong", error);
			String path = getServletContext().getContextPath() + "/GoToPlaylistPage?playlistId=" + playlistId + "&section=0";

			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(path);
			dispatcher.forward(request,response);
		}
		
		//The user can add the song at the playList
		
		//To add the song in the playList
		PlaylistDAO pDao = new PlaylistDAO(connection);
		
		try {
			boolean result = pDao.relateSong(sId, pId);
			
			if(result == true) {
				String path = getServletContext().getContextPath() + ("/GoToPlaylistPage?playlistId=" + playlistId + "&section=0");
				response.sendRedirect(path);
			}
			else {
				error += "An arror occurred with the db, retry later;";
				s.setAttribute("errorFromAddSong", error);
				//Forward to GoToPlaylistPage
				String path = getServletContext().getContextPath() + "/GoToPlaylistPage?playlistId=" + playlistId + "&section=0";
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(path);
				dispatcher.forward(request,response);	
			}
		}catch(SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An arror occurred with the db, retry later");
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
