package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Song;

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
	
	public List<Song> getSongsByUser(int userID) throws SQLException {
		List<Song> songs = new ArrayList<>();
		String query = "SELECT title FROM song WHERE userID = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;

		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, userID);
			result = pstatement.executeQuery();
			while (result.next()) {
				Song song = new Song();
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
	
	public ArrayList<Song> getSongsInPlaylist(int playlistId) throws SQLException{
		String query = "SELECT song.* FROM song WHERE song.ID IN (SELECT contains.songID FROM contains WHERE contains.playlistID = ?) ORDER BY song.publicationYear DESC";
		PreparedStatement pStatement = null;
		ResultSet resultSet = null;
		ArrayList<Song> songs = new ArrayList<Song>();
		
		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, playlistId);
			
			resultSet = pStatement.executeQuery();
			
			while(resultSet.next()) {
				Song song = new Song();
				
				//Read the image from the data base
				song.setIDSong(resultSet.getInt("song.ID"));
				song.setTitle(resultSet.getString("song.title"));
				song.setImage(resultSet.getString("song.albumImagePath"));//Set the name of the image file
				songs.add(song);
			}
		}catch(SQLException e) {
			throw new SQLException();
		}finally {
			try {
				if(resultSet != null) {
					resultSet.close();
				}
			}catch(Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if(pStatement != null) {
					pStatement.close();
				}
			}catch(Exception e2) {
				throw new SQLException(e2);
			}
		}
		return songs;
	}
	
	public ArrayList<Song> getSongsNotInPlaylist(int playlistId , int userId) throws SQLException{
		String query = "SELECT * FROM song WHERE userID = ? AND ID NOT IN ("
				+ "SELECT songID FROM contains WHERE playlistID = ?)";
		ResultSet resultSet = null;
		PreparedStatement pStatement = null;
		ArrayList<Song> songs = new ArrayList<Song>();
		
		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, userId);
			pStatement.setInt(2, playlistId);
			
			resultSet = pStatement.executeQuery();
			
			while(resultSet.next()) {
				Song song = new Song();
				song.setIDSong(resultSet.getInt("ID"));
				song.setTitle(resultSet.getString("Title"));
				songs.add(song);
			}
		}catch(SQLException e) {
			throw new SQLException();
		}finally {
			try {
				if(resultSet != null) {
					resultSet.close();
				}
			}catch(Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if(pStatement != null) {
					pStatement.close();
				}
			}catch(Exception e2) {
				throw new SQLException(e2);
			}
		}
		return songs;
	}
	
	public boolean findSongByUserId(String audioPath , int userId) throws SQLException{
		String query = "SELECT * FROM song WHERE fileAudioPath = ? AND IdUser = ?";
		boolean result = false;
		PreparedStatement pStatement = null;
		ResultSet resultSet = null;
		
		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setString(1, audioPath);
			pStatement.setInt(2, userId);
			
			resultSet = pStatement.executeQuery();
			
			if(resultSet.next())
				result = true;
			
		}catch(SQLException e) {
			throw new SQLException();
		}finally {
			try {
				if(resultSet != null) {
					resultSet.close();
				}
			}catch(Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if(pStatement != null) {
					pStatement.close();
				}
			}catch(Exception e2) {
				throw new SQLException(e2);
			}
		}	
		return result;
	}
}
