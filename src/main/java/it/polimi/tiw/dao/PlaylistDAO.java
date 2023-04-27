package it.polimi.tiw.dao;

import java.sql.Connection;

public class PlaylistDAO {
	private Connection connection;
	
	public PlaylistDAO(Connection connection) {
		this.connection = connection;
	}

}
