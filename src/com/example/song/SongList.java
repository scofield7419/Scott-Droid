package com.example.song;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class SongList {
	public static List<Song> getSongData(Context context){
		List<Song> songList = new ArrayList<Song>();
		ContentResolver mediaContentResolver = context.getContentResolver ( );
		if(mediaContentResolver != null){
			Cursor cursor = mediaContentResolver.query ( MediaStore.Audio.Media.EXTERNAL_CONTENT_URI , null , null
					, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
			if(cursor == null){
				return null;
			}
			if(cursor.moveToFirst ( )){
				do{
					Song song = new Song ( );
					String title = cursor.getString ( cursor.getColumnIndex ( MediaStore.Audio.Media.TITLE ) );
					String author = cursor.getString ( cursor.getColumnIndex ( MediaStore.Audio.Media.ARTIST ) );
					if(author.equals ( "<unknown>" )){
						author = "未知歌手";
					}
					String album = cursor.getString ( cursor.getColumnIndex ( MediaStore.Audio.Media.ALBUM ) );
					if(album.equals ( "<unknown>" )){
						album = "未知专辑";
					}
					long size = cursor.getLong ( cursor.getColumnIndex ( MediaStore.Audio.Media.SIZE ) );
					long duration = cursor.getLong ( cursor.getColumnIndex ( MediaStore.Audio.Media.DURATION ) );
					String URL = cursor.getString ( cursor.getColumnIndex ( MediaStore.Audio.Media.DATA ) );
					String songName = cursor.getString ( cursor.getColumnIndex ( MediaStore.Audio.Media.DISPLAY_NAME ) );
					String exten = songName.substring ( songName.length ( ) -3 ,songName.length ( ));
					if(exten.equals ( "mp3" ) || exten.equals ( "m4a" ) || exten.equals ( "wma" )||exten.equals ( "wax" )||exten.equals ( "ape" )){
						song.setTitle ( title );
						song.setAuthor ( author );
						song.setSongAlbum ( album );
						song.setSize ( size );
						song.setDuration ( duration );
						song.setURL ( URL );
						song.setSongName ( songName );
						songList.add ( song );
					}
				}while(cursor.moveToNext ( ));
			}
		}
		return songList;
	}
}
