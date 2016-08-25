package com.example.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.activity.R;
import com.example.song.Song;

/**
 * *将事件处理的接口框架定义在本dialog中，实现更高的内聚性，并定义各种回调函数将钩子抛给使用本dialog者，让他们处理具体事件。
 * 
 * @author scott
 */
public class SongInfoDialog extends Dialog implements android.view.View.OnClickListener {
	Context context;
	
	Song song;
	
	Button dialogSongInfoExitButton;
	TextView dialogSongInfoTitleTextView;
	TextView dialogSongInfoAuthorTextView;
	TextView dialogSongInfoDurationTextView;
	TextView dialogSongInfoSizeTextView;
	TextView dialogSongInfoAlbumTextView;
	TextView dialogSongInfoFormationTextView;
	TextView dialogSongInfoPathTextView;
	
	private void initView ( ) {
		dialogSongInfoTitleTextView = ( TextView ) findViewById ( R.id.song_info_dialog_title );
		dialogSongInfoTitleTextView.setText ( song.getTitle ( ) );
		dialogSongInfoAuthorTextView = ( TextView ) findViewById ( R.id.song_info_dialog_author );
		dialogSongInfoAuthorTextView.setText ( song.getAuthor ( ) );
		dialogSongInfoDurationTextView = ( TextView ) findViewById ( R.id.song_info_dialog_duration );
		dialogSongInfoDurationTextView.setText ( setDuration2String ( ( int ) song.getDuration ( ) ) );
		dialogSongInfoSizeTextView = ( TextView ) findViewById ( R.id.song_info_dialog_size );
		dialogSongInfoSizeTextView.setText ( setSize2String ( song.getSize ( ) ) );
		dialogSongInfoAlbumTextView = ( TextView ) findViewById ( R.id.song_info_dialog_album );
		dialogSongInfoAlbumTextView.setText ( song.getSongAlbum ( ) );
		dialogSongInfoFormationTextView = ( TextView ) findViewById ( R.id.song_info_dialog_formation );
		dialogSongInfoFormationTextView.setText ( song.getSongName ( ).substring ( song.getSongName ( ).length ( ) - 3 ) );
		dialogSongInfoPathTextView = ( TextView ) findViewById ( R.id.song_info_dialog_path );
		String extPathStr = Environment.getExternalStorageDirectory().toString ( );
		dialogSongInfoPathTextView.setText ( song.getURL ( ).substring ( extPathStr.length ( ) , song.getURL ( ).length ( ) - 4 ) );
		
		dialogSongInfoExitButton = ( Button ) findViewById ( R.id.song_info_dialog_exit );
		dialogSongInfoExitButton.setOnClickListener ( this );
	}
	
	private CharSequence setSize2String ( long size ) {
		String temp;
		float tempSize;
		if ( size < 1024 ) {
			temp = size + ".00 B";
			return temp;
		} else if ( size < 1048576 ) {
			tempSize = ( ( float ) size ) / 1024;
			temp = String.format ( "%.2f" , tempSize ) + " KB";
		} else {
			tempSize = ( ( float ) size ) / 1048576;
			temp = String.format ( "%.2f" , tempSize ) + " MB";
		}
		return temp;
	}
	
	private String setDuration2String ( int duration ) {
		duration /= 1000;
		int second = duration % 60;
		int minute = duration / 60;
		return String.format ( "%02d:%02d" , minute , second );
	}
	
	public SongInfoDialog ( Context context , int theme ) {
		// 将dialog的弹出以及弹入动画写入到一个dialog的属性主题中，让超类去实现dialog的动画
		super ( context , theme );
		this.context = context;
	}
	
	public SongInfoDialog ( Context context , Song song ) {
		this ( context , R.style.MyDialogTheme );
		this.song = song;
	}
	
	@ Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.song_info_dialog );
		initWindow ( );
		initView ( );
	}
	
	private void initWindow ( ) {
		Window window = getWindow ( );
		WindowManager.LayoutParams layoutParams = window.getAttributes ( );
		
		layoutParams.width = ( int ) ( getWidth ( ) * 0.85 );
		layoutParams.height = ( int ) ( getHeight ( ) * 0.65 );
		layoutParams.gravity = Gravity.CENTER;
		
		window.setAttributes ( layoutParams );
	}
	
	private float getWidth ( ) {
		return ( ( WindowManager ) context.getSystemService ( Context.WINDOW_SERVICE ) ).getDefaultDisplay ( ).getWidth ( );
	}
	
	private float getHeight ( ) {
		return ( ( WindowManager ) context.getSystemService ( Context.WINDOW_SERVICE ) ).getDefaultDisplay ( ).getHeight ( );
	}
	
	@ Override
	public void onClick ( View v ) {
		this.dismiss ( );
		
	}
}
