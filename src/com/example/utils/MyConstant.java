package com.example.utils;

public class MyConstant {
	//BR in PlayerFragment 
	public static final String ACTION_BROADCAST_PLAYER_SEEKBAR_UPDATE = "com.example.broadcast.PLAYER_SEEKBAR_UPDATE";
	public static final String ACTION_BROADCAST_PLAYER_LOOPLOGO_UPDATE = "com.example.broadcast.PLAYER_LOOPLOGO_UPDATE";
	public static final String ACTION_BROADCAST_PLAYER_COMPLETE_UPDATE = "com.example.broadcast.PLAYER_COMPLETE_UPDATE";
	public static final String ACTION_BROADCAST_PLAYER_BTN_UPDATE = "com.example.broadcast.PLAYER_BTN_UPDATE";
	
	//BR in RapperFragment 
	public static final String ACTION_BROADCAST_RAPPER_SEEKBAR_UPDATE = "com.example.broadcast.RAPPER_SEEKBAR_UPDATE";
	public static final String ACTION_BROADCAST_RAPPER_LOOPLOGO_UPDATE = "com.example.broadcast.RAPPER_LOOPLOGO_UPDATE";
	public static final String ACTION_BROADCAST_RAPPER_COMPLETE_UPDATE = "com.example.broadcast.RAPPER_COMPLETE_UPDATE";
	public static final String ACTION_BROADCAST_RAPPER_BTN_UPDATE = "com.example.broadcast.RAPPER_BTN_UPDATE";

	//BR in PlayerService 
	public static final String ACTION_BROADCAST_PLAYERSERVICE_PLAYER_STATU_UPDATE = "com.example.broadcast.PLAYERSERVICE_PLAYER_STATU_UPDATE";
	public static final String ACTION_BROADCAST_PLAYERSERVICE_LISTFRAG_FIRST_UPDATE = "com.example.broadcast.PLAYERSERVICE_LISTFRAG_FIRST_UPDATE";
	public static final String ACTION_BROADCAST_PLAYERSERVICE_PLAYERFRAG_REBACK_UPDATE = "com.example.broadcast.PLAYERSERVICE_PLAYERFRAG_REBACK_UPDATE";
	
	//BR in RapperService 
	public static final String ACTION_BROADCAST_RAPPERSERVICE_PLAYER_STATU_UPDATE = "com.example.broadcast.RAPPERSERVICE_PLAYER_STATU_UPDATE";
	public static final String ACTION_BROADCAST_RAPPERSERVICE_RAPPERFRAG_REBACK_UPDATE = "com.example.broadcast.RAPPERSERVICE_RAPPERFRAG_REBACK_UPDATE";
	
	//BR in ListFragment 
	public static final String ACTION_BROADCAST_LIST_BTN_UPDATE = "com.example.broadcast.LIST_BTN_UPDATE";
	public static final String ACTION_BROADCAST_LIST_INFO_UPDATE = "com.example.broadcast.LIST_INFO_UPDATE";
	
	public static final String ACTION_SERVICE_START_UPDATE = "com.example.service.MAIN_PLAYER";
	
	public static final int LOOP_ALL = 0;
	public static final int LOOP_SINGLE = 1;
	public static final int LOOP_SHUFFLE = 2;
	
	public static final String CONNECTION_WORD = " - ";
}
