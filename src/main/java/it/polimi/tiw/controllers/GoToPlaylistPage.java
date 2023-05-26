
package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;


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

@WebServlet("/GoToPlaylistPage")
public class GoToPlaylistPage extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
	private static final int SONGS_PER_BLOCK = 5;

	public void init() {
		ServletContext context = getServletContext();

		// Initializing the template engine
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");

		try {

			// Initializing the connection
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
		// TODO Auto-generated method stub
		String playlistId = request.getParameter("playlistId");
		String block = request.getParameter("block");
		String error = "";
		String error1 = "";
		int playlistIdParsed = -1;
		int blockParsed = 0;
		
		HttpSession s = request.getSession();
		User user = (User) s.getAttribute("currentUser");
		
		if (s.isNew() || user == null) {
			response.sendRedirect("/login.html");
			return;
		}
		
		if(playlistId == null || playlistId.isEmpty())
			error += "Playlist not defined;";
		
		if(block == null || block.isEmpty()) {
			block = "0";
		}
		
		if(error.equals("")) {
			//Create the DAO to check if the playList id belongs to the user 
			PlaylistDAO playlistDAO = new PlaylistDAO(connection);
			
			try {
				
				//Check if the playlistId is a number
				playlistIdParsed = Integer.parseInt(playlistId);
				
				//Check if section is a number
				blockParsed = Integer.parseInt(block);
				
				//Check if the playList exists
				if(!playlistDAO.findPlaylistById(playlistIdParsed, user.getIdUser()))
					error += "PlayList doesn't exist";
				
				if(blockParsed < 0)
					blockParsed = 0;
			}catch(NumberFormatException e) {
				error += "Playlist e/o section not defined;";
			}catch(SQLException e) {
				error += "Impossible comunicate with the data base;";
			}
		}
		
		// playlist not found
		if(!error.equals("")){
			request.getSession().setAttribute("errorFromGoToPlaylist", error);
			String path = "/GoToHomepage";

			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(path);
			dispatcher.forward(request,response);
			return;
		}
		
	
		SongDAO songDao = new SongDAO(connection);		
		//To take the title of the playList
		PlaylistDAO playlistDao = new PlaylistDAO(connection);
				
		//Take the titles and the image paths
		try {
					
			ArrayList<Song> songsInBlock = songDao.getSongsInBlock(playlistIdParsed, blockParsed);
			ArrayList<Song> songsNotInPlaylist = songDao.getSongsNotInPlaylist(playlistIdParsed , user.getIdUser());
			
			String title = playlistDao.getPlaylistTitle(playlistIdParsed);
					
			boolean next = false;
			boolean previous = false;
			
			if(blockParsed > 0)
				previous = true;
			
			if(!songDao.getSongsInBlock(playlistIdParsed, blockParsed + 1).isEmpty())
				next = true;
			
			
			ArrayList<Song> songsPerBlock = new ArrayList<Song>();
			
			if(songsInBlock.size() > 0) {
				for(int i = 0; i < songsInBlock.size(); i++){
					Song song = songsInBlock.get(i);
					songsPerBlock.add(song);
				}	
			}
			
			Playlist p = new Playlist();
			p.setIdPlaylist(playlistIdParsed);
			p.setTitle(title);
			
			String path = "/WEB-INF/playlistPage.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request , response , servletContext , request.getLocale());
			
			if(s.getAttribute("errorFromAddSong") != null) {
				error += (String) s.getAttribute("errorFromAddSong");
			}
			//Take the error in case of forward from GoToPlayerPage
			else if(s.getAttribute("errorFromGoToPlayer") != null) {
				error1 += (String) s.getAttribute("errorFromGoToPlayer");
			}
			
			ctx.setVariable("user" , user);
			
			ctx.setVariable("songsNotInPlaylist", songsNotInPlaylist);
			ctx.setVariable("songsPerBlock", songsPerBlock);
			
			ctx.setVariable("playlist", p);
			ctx.setVariable("block", block);
			
			ctx.setVariable("next", next);
			ctx.setVariable("previous", previous);
			
			ctx.setVariable("errorFromAddSong", error);
			ctx.setVariable("errorFromGoToPlayer", error1);
			templateEngine.process(path , ctx , response.getWriter());
			s.removeAttribute("errorFromAddSong");
			s.removeAttribute("errorFromGoToPlayer");
		

		}catch(SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An arror occurred with the db, retry later");
		}
		
		
		
		
		
	}
	
	
	
}
