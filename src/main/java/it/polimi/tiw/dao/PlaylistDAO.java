package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.*;

public class PlaylistDAO {
	
	private Connection con;
	
	public PlaylistDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<Playlist> getPlaylistsByUser (int userID) throws SQLException {
		List<Playlist> playlists = new ArrayList<>();
		String query = "SELECT * FROM playlist WHERE userID = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, userID);
			result = pstatement.executeQuery();
			while (result.next()) {
				Playlist playlist = new Playlist();
				playlist.setIdPlaylist(result.getInt("ID"));
				playlist.setTitle(result.getString("title"));
				playlist.setCreationDate(result.getDate("creationDate"));
				playlist.setIdUser(result.getInt("userID"));
				playlists.add(playlist);
			}
			
			
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new SQLException(e);

		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
			
		}
		return playlists;
		
	}

}
