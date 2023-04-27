package it.polimi.tiw.beans;

import java.sql.Date;

public class Playlist {
	private int IdPlaylist;
	private String title;
	private Date creationDate;
	private int IdUser;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public int getIdUser() {
		return IdUser;
	}
	public void setIdUser(int idUser) {
		IdUser = idUser;
	}
	public int getIdPlaylist() {
		return IdPlaylist;
	}
	public void setIdPlaylist(int idPlaylist) {
		IdPlaylist = idPlaylist;
	}
}
