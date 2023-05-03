package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SongDAO {
	
	private Connection con;
	
	public SongDAO(Connection con) {
		this.con = con;
	}
	
	public boolean upLoadSong(String title, String albumImagePath, String albumTitle, String author, int userID, int publicationYear, String genre, String fileAudioPath) throws SQLException {
		String query = "INSERT INTO song (title,albumImagePath,albumTitle,author,userID,publicationYear,genre,fileAudioPath) values (?,?,?,?,?,?,?,?)";
		PreparedStatement pStatement = null;
		int code = 0;
		
		try {
			pStatement = con.prepareStatement(query);
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

}
