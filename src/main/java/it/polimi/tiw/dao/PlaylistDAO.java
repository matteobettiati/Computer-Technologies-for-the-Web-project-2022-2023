package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.*;

public class PlaylistDAO {

	private Connection connection;

	public PlaylistDAO(Connection connection) {
		this.connection = connection;
	}

	public List<Playlist> getPlaylistsByUser(int userID) throws SQLException {
		List<Playlist> playlists = new ArrayList<>();
		String query = "SELECT * FROM playlist WHERE userID = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;

		try {
			pstatement = connection.prepareStatement(query);
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

	public boolean getPlaylistByID(int playlistID) throws SQLException {
		String query = "SELECT * FROM playlist WHERE ID = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;

		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, playlistID);
			result = pstatement.executeQuery();
			if(result != null) {
				return true;
			}
			else {
				return false;
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
	}
	
	public String getPlaylistTitle(int playlistId) throws SQLException{
		String query = "SELECT * FROM playlist WHERE ID = ?";
		String result = "";
		ResultSet resultSet = null;
		PreparedStatement pStatement = null;
		
		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1 , playlistId);
			
			resultSet = pStatement.executeQuery();
			
			if(resultSet.next()) {
				result = resultSet.getString("title");
			}
		}catch(SQLException e) {
			throw new SQLException();
		}finally{
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
	
	public boolean createPlaylist(String title, Date creationDate, int userId) throws SQLException {
		String query = "INSERT INTO playlist (title , creationDate , userID) VALUES (? , ? , ?)";
		int code = 0;
		PreparedStatement pStatement = null;

		if (findPlaylistByTitle(title, userId) == true)
			return false;

		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setString(1, title);
			pStatement.setDate(2, creationDate);
			pStatement.setInt(3, userId);
			code = pStatement.executeUpdate();
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
		return (code > 0);
	}

	public boolean findPlaylistByTitle(String title, int userId) throws SQLException {

		String query = "SELECT * FROM playlist WHERE title = ? AND userID = ?";
		boolean result = false;
		ResultSet resultSet = null;
		PreparedStatement pStatement = null;

		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setString(1, title);
			pStatement.setInt(2, userId);
			resultSet = pStatement.executeQuery();

			if (resultSet.next())
				result = true;

		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pStatement != null) {
					pStatement.close();
				}
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
		return result;
	}

	public boolean findPlaylistById(int playlistId, int userId) throws SQLException {

		String query = "SELECT * FROM playlist WHERE ID = ? AND userID = ?";
		boolean result = false;
		ResultSet resultSet = null;
		PreparedStatement pStatement = null;

		try {
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, playlistId);
			pStatement.setInt(2, userId);
			resultSet = pStatement.executeQuery();

			if (resultSet.next())
				result = true;

		} catch (SQLException e) {
			throw new SQLException();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				if (pStatement != null) {
					pStatement.close();
				}
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
		return result;
	}
	
	public int getLastID() throws SQLException {
		String query = "SELECT MAX(ID) as playlistID FROM playlist";
		PreparedStatement pStatement = null;
		ResultSet resultSet = null;
		int lastID = 0;

		try {
			pStatement = connection.prepareStatement(query);
			resultSet = pStatement.executeQuery();
			if (resultSet.next()) {
				lastID = resultSet.getInt("playlistID");
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
		return lastID;
	}

	public boolean relateSong(int songID, int playlistID) throws SQLException {
		String checkquery = "SELECT * FROM contains where playlistID = ? AND songID = ?";
		String query = "INSERT INTO contains (playlistID,songID) values (?,?)";
		PreparedStatement pStatement = null;
		ResultSet resultSet = null;
		int code = 0;

		try {

			pStatement = connection.prepareStatement(checkquery);
			pStatement.setInt(1, playlistID);
			pStatement.setInt(2, songID);
			resultSet = pStatement.executeQuery();
			if (resultSet.next()) {
			    return false;
			}
			pStatement.close();
			
			pStatement = connection.prepareStatement(query);
			pStatement.setInt(1, playlistID);
			pStatement.setInt(2, songID);
			code = pStatement.executeUpdate();


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
		return code > 0;
	}

}
