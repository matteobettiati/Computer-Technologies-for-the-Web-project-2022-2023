package it.polimi.tiw.controller.beans;

public class Songs {
	
	private String title;
	private String image;
	private String author;
	private int IDTitle;
	private int year;
	private String genre;
	private String fileAudio;
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getIDTitle() {
		return IDTitle;
	}
	public void setIDTitle(int iDTitle) {
		IDTitle = iDTitle;
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
		return fileAudio;
	}
	public void setFileAudio(String fileAudio) {
		this.fileAudio = fileAudio;
	}


}
