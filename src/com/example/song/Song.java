package com.example.song;

public class Song {
	private String title;
	private String author;
	private String URL;
	private long size;
	private long duration;
	private String songName;
	private String songAlbum;
	
	public String getTitle ( ) {
		return title;
	}
	
	public void setTitle ( String title ) {
		this.title = title;
	}
	
	public String getAuthor ( ) {
		return author;
	}
	
	public void setAuthor ( String author ) {
		this.author = author;
	}
	
	public String getURL ( ) {
		return URL;
	}
	
	public void setURL ( String uRL ) {
		URL = uRL;
	}
	
	public long getSize ( ) {
		return size;
	}
	
	public void setSize ( long size ) {
		this.size = size;
	}
	
	public long getDuration ( ) {
		return duration;
	}
	
	public void setDuration ( long duration ) {
		this.duration = duration;
	}
	
	public String getSongName ( ) {
		return songName;
	}
	
	public void setSongName ( String songName ) {
		this.songName = songName;
	}
	
	public String getSongAlbum ( ) {
		return songAlbum;
	}
	
	public void setSongAlbum ( String songAlbum ) {
		this.songAlbum = songAlbum;
	}
	
}
