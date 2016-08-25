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
public class SongAboutDialog extends Dialog implements android.view.View.OnClickListener {
	Context context;
	
	Button dialogSongAboutExitButton;
	
	private void initView ( ) {
		dialogSongAboutExitButton = ( Button ) findViewById ( R.id.song_about_dialog_exit );
		dialogSongAboutExitButton.setOnClickListener ( this );
	}
	
	public SongAboutDialog ( Context context , int theme ) {
		// 将dialog的弹出以及弹入动画写入到一个dialog的属性主题中，让超类去实现dialog的动画
		super ( context , theme );
		this.context = context;
	}
	
	public SongAboutDialog ( Context context) {
		this ( context , R.style.MyDialogTheme );
	}
	
	@ Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.song_about_dialog );
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
