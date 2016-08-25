package com.example.quickaccessbar;

public class QuickLetterModel {
	private String name;   //显示的数据
	private String quickLetters;  //显示数据拼音的首字母
	
	public String getName ( ) {
		return name;
	}
	
	public void setName ( String name ) {
		this.name = name;
	}
	
	public String getQuickLetters ( ) {
		return quickLetters;
	}
	
	public void setQuickLetters ( String quickLetters ) {
		this.quickLetters = quickLetters;
	}
}
