package com.example.lyric;
/**
 * 一个lyric对象就是一个歌词文件中的一行。
 * @author scott
 *
 */
public class Lyric {
	
	private String lrcStr;	// 歌词内容
	private int lrcTime;	// 歌词当前时间
	
	public String getLrcStr ( ) {
		return lrcStr;
	}
	
	public void setLrcStr ( String lrcStr ) {
		this.lrcStr = lrcStr;
	}
	
	public int getLrcTime ( ) {
		return lrcTime;
	}
	
	public void setLrcTime ( int lrcTime ) {
		this.lrcTime = lrcTime;
	}
	
}
