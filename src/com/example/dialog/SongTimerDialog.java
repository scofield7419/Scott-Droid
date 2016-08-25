package com.example.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

import com.example.activity.R;

/**
 * *将事件处理的接口框架定义在本dialog中，实现更高的内聚性，并定义各种回调函数将钩子抛给使用本dialog者，让他们处理具体事件。
 * 
 * @author scott
 */
public class SongTimerDialog extends Dialog implements android.view.View.OnClickListener {
	Context context;
	
	SongTimerDialogBtnsClickListener songTimerDialogBtnsClickListener;
	
	Button songTimerDialogConfirmButton;
	NumberPicker songTimerDialogPickerNumberPicker;
	
	int time;
	
	private void initView ( ) {
		songTimerDialogConfirmButton = ( Button ) findViewById ( R.id.song_timer_dialog_confirm );
		songTimerDialogConfirmButton.setOnClickListener ( this );
		
		songTimerDialogPickerNumberPicker = ( NumberPicker ) findViewById ( R.id.song_timer_dialog_picker );
		songTimerDialogPickerNumberPicker.setMinValue ( 1 );
		songTimerDialogPickerNumberPicker.setMaxValue ( 60 );
		songTimerDialogPickerNumberPicker.setValue ( 15 );
		songTimerDialogPickerNumberPicker.setOnValueChangedListener ( new OnValueChangeListener ( ) {
			@ Override
			public void onValueChange ( NumberPicker picker , int oldVal , int newVal ) {
				time = newVal;
			}
		} );
	}
	
	public SongTimerDialog ( Context context , int theme ) {
		// 将dialog的弹出以及弹入动画写入到一个dialog的属性主题中，让超类去实现dialog的动画
		super ( context , theme );
		this.context = context;
	}
	
	public SongTimerDialog ( Context context ) {
		this ( context , R.style.TimerDialogTheme );
	}
	
	@ Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.song_timer_dialog );
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
		songTimerDialogBtnsClickListener.onSongTimerDialogBtnsClickListener ( this.time );
		this.dismiss ( );
	}
	
	public void setOnSongTimerDialogBtnsClickListener ( SongTimerDialogBtnsClickListener songTimerDialogBtnsClickListener ) {
		this.songTimerDialogBtnsClickListener = songTimerDialogBtnsClickListener;
	}
	
	public interface SongTimerDialogBtnsClickListener {
		public void onSongTimerDialogBtnsClickListener ( int val );
	}
}
