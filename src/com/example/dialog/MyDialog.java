package com.example.dialog;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.activity.R;

/**
 * *将事件处理的接口框架定义在本dialog中，实现更高的内聚性，并定义各种回调函数将钩子抛给使用本dialog者，让他们处理具体事件。
 * 
 * @author scott
 */
public class MyDialog extends Dialog implements android.view.View.OnClickListener , OnSeekBarChangeListener {
	OnMyDialogBtnsClickListener onMyDialogBtnsClickListener;
	Context context;
	
	DialogVolumeBroadcastReceiver dialogVolumeBroadcastReceiver;
	
	AudioManager audioManager;
	int currVolume;
	int maxVolume;
	
	SeekBar dialogVolumSeekBar;
	ImageButton dialogInfoButton;
	ImageButton dialogEqualizerButton;
	ImageButton dialogEditButton;
	ImageButton dialogRingbellButton;
	ImageButton dialogAboutButton;
	ImageButton dialogTimerButton;
	TextView dialogExitTextView;
	
	public MyDialog ( Context context , int theme ) {
		// 将dialog的弹出以及弹入动画写入到一个dialog的属性主题中，让超类去实现dialog的动画
		super ( context , R.style.Theme_MyDialog_From_Bottom );
		this.context = context;
	}
	
	@ Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.my_dialog );
		initWindow ( );
		initSystemAudio ( );
		initView ( );
		initBroadcast ( );
	}
	
	private void initBroadcast ( ) {
		dialogVolumeBroadcastReceiver = new DialogVolumeBroadcastReceiver ( );
		IntentFilter filter = new IntentFilter ( );
		filter.addAction ( "android.media.VOLUME_CHANGED_ACTION" );
		context.registerReceiver ( dialogVolumeBroadcastReceiver , filter );
	}
	
	private void initSystemAudio ( ) {
		audioManager = ( AudioManager ) context.getSystemService ( Context.AUDIO_SERVICE );
		currVolume = audioManager.getStreamVolume ( AudioManager.STREAM_MUSIC );
		maxVolume = audioManager.getStreamMaxVolume ( AudioManager.STREAM_MUSIC );
	}
	
	private void initWindow ( ) {
		Window window = getWindow ( );
		WindowManager.LayoutParams layoutParams = window.getAttributes ( );
		
		layoutParams.width = ( int ) ( getWidth ( ) );
		layoutParams.height = ( int ) ( getHeight ( ) * 0.54 );
		layoutParams.gravity = Gravity.BOTTOM;
		
		window.setAttributes ( layoutParams );
	}
	
	private void initView ( ) {
		dialogVolumSeekBar = ( SeekBar ) findViewById ( R.id.dialog_volum );
		dialogVolumSeekBar.setProgress ( currVolume * 100 / maxVolume );
		
		dialogInfoButton = ( ImageButton ) findViewById ( R.id.dialog_info );
		dialogEqualizerButton = ( ImageButton ) findViewById ( R.id.dialog_equalizer );
		dialogEditButton = ( ImageButton ) findViewById ( R.id.dialog_edit );
		dialogRingbellButton = ( ImageButton ) findViewById ( R.id.dialog_ringbell );
		dialogAboutButton = ( ImageButton ) findViewById ( R.id.dialog_about );
		dialogTimerButton = ( ImageButton ) findViewById ( R.id.dialog_timer );
		dialogExitTextView = ( TextView ) findViewById ( R.id.dialog_exit );
		dialogEqualizerButton.setOnClickListener ( this );
		dialogInfoButton.setOnClickListener ( this );
		dialogEditButton.setOnClickListener ( this );
		dialogRingbellButton.setOnClickListener ( this );
		dialogExitTextView.setOnClickListener ( this );
		dialogAboutButton.setOnClickListener ( this );
		dialogTimerButton.setOnClickListener ( this );
		dialogVolumSeekBar.setOnSeekBarChangeListener ( this );
	}
	
	private float getWidth ( ) {
		return ( ( WindowManager ) context.getSystemService ( Context.WINDOW_SERVICE ) ).getDefaultDisplay ( ).getWidth ( );
	}
	
	private float getHeight ( ) {
		return ( ( WindowManager ) context.getSystemService ( Context.WINDOW_SERVICE ) ).getDefaultDisplay ( ).getHeight ( );
	}
	
	public void setOnMyDialogBtnsClickListener ( OnMyDialogBtnsClickListener onMyDialogBtnsClickListener ) {
		this.onMyDialogBtnsClickListener = onMyDialogBtnsClickListener;
	}
	
	public interface OnMyDialogBtnsClickListener {
		public void onMyDialogBtnsClickListener ( View v );
	}
	
	@ Override
	public void onClick ( View v ) {
		onMyDialogBtnsClickListener.onMyDialogBtnsClickListener ( v );
	}
	
	@ Override
	public void onProgressChanged ( SeekBar seekBar , int progress , boolean fromUser ) {
		int currVo = ( progress * maxVolume ) / 100;
		audioManager.setStreamVolume ( AudioManager.STREAM_MUSIC , currVo , 0 );
	}
	
	@ Override
	public void onStartTrackingTouch ( SeekBar seekBar ) {
	}
	
	@ Override
	public void onStopTrackingTouch ( SeekBar seekBar ) {
	}
	
	public class DialogVolumeBroadcastReceiver extends BroadcastReceiver {
		@ Override
		public void onReceive ( Context context , Intent intent ) {
			if ( intent.getAction ( ).equals ( "android.media.VOLUME_CHANGED_ACTION" ) ) {
				int currVo = audioManager.getStreamVolume ( AudioManager.STREAM_MUSIC );// 当前的媒体音量
				dialogVolumSeekBar.setProgress ( currVo * 100 / maxVolume );
			}
		}
		
	}
}
