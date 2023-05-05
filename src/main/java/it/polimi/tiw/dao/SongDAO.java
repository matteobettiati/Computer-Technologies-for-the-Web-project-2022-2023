package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Songs;

public class SongDAO {
	
	private Connection connection;
	
	public SongDAO(Connection connection) {
		this.connection = connection;
	}
	
	public boolean upLoadSong(String title, String albumImagePath, String albumTitle, String author, int userID, int publicationYear, String genre, String fileAudioPath) throws SQLException {
		String query = "INSERT INTO song (title,albumImagePath,albumTitle,author,userID,publicationYear,genre,fileAudioPath) values (?,?,?,?,?,?,?,?)";
		PreparedStatement pStatement = null;
		int code = 0;
		
		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setString(1, title);
			pStatement.setString(2, albumImagePath);
			pStatement.setString(3, albumTitle);
			pStatement.setString(4, author);
			pStatement.setInt(5, userID);
			pStatement.setInt(6, publicationYear);
			pStatement.setString(7, genre);
			pStatement.setString(8, fileAudioPath);

			
			
			code = pStatement.executeUpdate();
		}catch(SQLException e) {
			throw e;
		}finally {
			try {
				if (pStatement != null) {
					pStatement.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
		}
		return (code > 0); 
	}
	
	public int getSongID(String song) throws SQLException {
		String query = "SELECT ID FROM song WHERE title = ?";
		PreparedStatement pStatement = null;
		ResultSet resultSet = null;
		int songID = 0;

		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setString(1,song);
			
			resultSet = pStatement.executeQuery();
			if (resultSet.next()) {
				songID = resultSet.getInt("ID");
			}
		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			try {
				if (pStatement != null) {
					pStatement.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
		}
		return songID;
	}
	
	public List<Songs> getSongsByUser(int userID) throws SQLException {
		List<Songs> songs = new ArrayList<>();
		String query = "SELECT title FROM song WHERE userID = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;

		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, userID);
			result = pstatement.executeQuery();
			while (result.next()) {
				Songs song = new Songs();
				song.setTitle(result.getString("title"));
				songs.add(song);
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
		return songs;

	}
}
