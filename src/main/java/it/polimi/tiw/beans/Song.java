package it.polimi.tiw.beans;

public class Song {
	
	private int idSong;
	private String title;
	private String imagePath;
	private String albumTitle;
	private String author;
	private int userId;
	private int year;
	private String genre;
	private String fileAudioPath;
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImage() {
		return imagePath;
	}
	public void setImage(String image) {
		this.imagePath = image;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getIDSong() {
		return this.idSong;
	}
	public void setIDSong(int iDSong) {
		this.idSong = iDSong;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getFileAudio() {
		return fileAudioPath;
	}
	public void setFileAudio(String fileAudio) {
		this.fileAudioPath = fileAudio;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getAlbumTitle() {
		return albumTitle;
	}
	public void setAlbumTitle(String albumTitle) {
		this.albumTitle = albumTitle;
	}


}
