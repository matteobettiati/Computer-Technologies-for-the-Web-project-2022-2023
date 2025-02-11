
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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Playlist;
import it.polimi.tiw.beans.Song;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.PlaylistDAO;
import it.polimi.tiw.dao.SongDAO;

@WebServlet("/GoToPlayerPage")
public class GoToPlayerPage extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	
	public void init() {
		ServletContext context = getServletContext();
		
		//Initializing the template engine
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		
		try {
			
			//Initializing the connection
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
	
	public void doGet(HttpServletRequest request , HttpServletResponse response)throws ServletException,IOException{
		//Take the song id
		String songId = request.getParameter("songId");
		String playlistId = request.getParameter("playlistId");
		String block = request.getParameter("block");
		String error = "";
		int songIdParsed = -1;
		int playlistIdParsed = -1;
		int blockParsed = -1;
		
		//I should do some controls about the user session
		HttpSession s = request.getSession();
		//Take the user
	    User user = (User) s.getAttribute("currentUser");
		
		if (s.isNew() || user == null) {
			response.sendRedirect("/login.html");
			return;
		}
		
		//Check if songId is valid
		if(songId.isEmpty() || songId == null)
			error += "Song not defined;";
		//Check if playlistId is valid
		if(playlistId.isBlank() || playlistId == null)
			error += "Playlist not defined;";
		
		//Check the follow only if the id is valid
		if(error.equals("")) { 
			try {
				//Create DAO to check if the song id and the playList id belong to the user
				SongDAO sDao = new SongDAO(connection);
				PlaylistDAO pDao = new PlaylistDAO(connection);
				
				//Check if the songId and the playlistId are numbers
				songIdParsed = Integer.parseInt(songId);
				playlistIdParsed = Integer.parseInt(playlistId);
				
				//set block only if section is valid -> otherwise the next controller know what to do 
				if(block != null && !block.isEmpty())
					blockParsed = Integer.parseInt(block);
				
				//Check if the player has this song --> Check if the song exists
				if(!sDao.findSongByUserId(songIdParsed, user.getIdUser())) {
					error += "Song doesn't exist";
				}
				//Check if the player has this playList
				if(!pDao.findPlaylistById(playlistIdParsed, user.getIdUser())) {
					error += "Playlist doesn't exist;";
				}
			}catch(NumberFormatException e) {
				error += "Request with bad format;";
			}catch(SQLException e) {
				error += "Impossible comunicate with the data base;";
			}
		}
		
		//if an error occurred
		if(!error.equals("")){
			s.setAttribute("errorFromGoToPlayer", error);
			String path = "/GoToPlaylistPage";

			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(path);
			dispatcher.forward(request,response);
			return;
		}
				
		//To take song and playList details 
		SongDAO sDao = new SongDAO(connection); 
		PlaylistDAO pDao = new PlaylistDAO(connection);
		
		try {
			Song song = sDao.getSongInfo(songIdParsed);
			Playlist playlist = new Playlist();
			playlist.setIdPlaylist(playlistIdParsed);
			playlist.setIdUser(user.getIdUser());
			playlist.setTitle(pDao.getPlaylistTitle(playlistIdParsed));
			
			String path = "/WEB-INF/playerPage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request , response , servletContext , request.getLocale());
			ctx.setVariable("song", song);
			ctx.setVariable("playlist", playlist);
			//block is the section of the playList where there is the song
			ctx.setVariable("block", blockParsed);
			templateEngine.process(path , ctx , response.getWriter());
			
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
