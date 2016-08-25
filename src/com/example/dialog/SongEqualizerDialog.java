package com.example.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.activity.R;
import com.example.service.MainPlayerService;
import com.example.view.MyVerticalSeekBar;
import com.example.view.MyVerticalSeekBar.OnSeekBarChangeListener;

/**
 * *将事件处理的接口框架定义在本dialog中，实现更高的内聚性，并定义各种回调函数将钩子抛给使用本dialog者，让他们处理具体事件。
 * 
 * @author scott
 */
public class SongEqualizerDialog extends Dialog implements android.view.View.OnClickListener {
	Context context;
	
	Button dialogSongEqualizerConfirmButton;
	Button dialogSongEqualizerResetButton;
	TextView [ ] songEqualizerBandUp = new TextView [ 5 ];
	TextView [ ] songEqualizerBandDown = new TextView [ 5 ];
	MyVerticalSeekBar [ ] bandVerticalSeekBar = new MyVerticalSeekBar [ 5 ];
	SeekBar songBassSeekBar;
	
	Equalizer equalizer;
	BassBoost bassBoost;
	short minEqualizer;
	short maxEqualizer;
	
	int bassStrength;
	int band1;
	int band2;
	int band3;
	int band4;
	int band5;
	
	private void initView ( ) {
		songBassSeekBar = ( SeekBar ) findViewById ( R.id.song_bass_dialog_seekbar );
		songBassSeekBar.setMax ( 1000 );
		songBassSeekBar.setProgress ( bassBoost.getRoundedStrength ( ) );
		System.out.println ( " bassBoost.getRoundedStrength ( ) " + bassBoost.getRoundedStrength ( ) );
		songBassSeekBar.setOnSeekBarChangeListener ( new SeekBar.OnSeekBarChangeListener ( ) {
			@ Override
			public void onStopTrackingTouch ( SeekBar seekBar ) {
			}
			
			@ Override
			public void onStartTrackingTouch ( SeekBar seekBar ) {
			}
			
			@ Override
			public void onProgressChanged ( SeekBar seekBar , int progress , boolean fromUser ) {
				bassBoost.setStrength ( ( short ) progress );
			}
		} );
		
		songEqualizerBandUp [ 0 ] = ( TextView ) findViewById ( R.id.song_equalizer_dialog_band1_up );
		songEqualizerBandDown [ 0 ] = ( TextView ) findViewById ( R.id.song_equalizer_dialog_band1_down );
		bandVerticalSeekBar [ 0 ] = ( MyVerticalSeekBar ) findViewById ( R.id.song_equalizer_dialog_band1_seekbar );
		songEqualizerBandUp [ 0 ].setText ( "+" + ( maxEqualizer / 100 ) + " dB" );
		songEqualizerBandDown [ 0 ].setText ( ( equalizer.getCenterFreq ( ( short ) 0 ) / 1000 ) + " HZ" );
		bandVerticalSeekBar [ 0 ].setMax ( maxEqualizer - minEqualizer );
		bandVerticalSeekBar [ 0 ].setProgress ( equalizer.getBandLevel ( ( short ) 0 ) - minEqualizer );
		bandVerticalSeekBar [ 0 ].setOnSeekBarChangeListener ( new OnSeekBarChangeListener ( ) {
			@ Override
			public void onStopTrackingTouch ( MyVerticalSeekBar VerticalSeekBar ) {
			}
			
			@ Override
			public void onStartTrackingTouch ( MyVerticalSeekBar VerticalSeekBar ) {
			}
			
			@ Override
			public void onProgressChanged ( MyVerticalSeekBar VerticalSeekBar , int progress , boolean fromUser ) {
				equalizer.setBandLevel ( ( short ) 0 , ( short ) ( progress + minEqualizer ) );
			}
		} );
		
		songEqualizerBandUp [ 1 ] = ( TextView ) findViewById ( R.id.song_equalizer_dialog_band2_up );
		songEqualizerBandDown [ 1 ] = ( TextView ) findViewById ( R.id.song_equalizer_dialog_band2_down );
		bandVerticalSeekBar [ 1 ] = ( MyVerticalSeekBar ) findViewById ( R.id.song_equalizer_dialog_band2_seekbar );
		songEqualizerBandDown [ 1 ].setText ( ( equalizer.getCenterFreq ( ( short ) 1 ) / 1000 ) + " HZ" );
		songEqualizerBandUp [ 1 ].setText ( "+" + ( maxEqualizer / 100 ) + " dB" );
		bandVerticalSeekBar [ 1 ].setMax ( maxEqualizer - minEqualizer );
		bandVerticalSeekBar [ 1 ].setProgress ( equalizer.getBandLevel ( ( short ) 1 ) - minEqualizer );
		bandVerticalSeekBar [ 1 ].setOnSeekBarChangeListener ( new OnSeekBarChangeListener ( ) {
			@ Override
			public void onStopTrackingTouch ( MyVerticalSeekBar VerticalSeekBar ) {
			}
			
			@ Override
			public void onStartTrackingTouch ( MyVerticalSeekBar VerticalSeekBar ) {
			}
			
			@ Override
			public void onProgressChanged ( MyVerticalSeekBar VerticalSeekBar , int progress , boolean fromUser ) {
				equalizer.setBandLevel ( ( short ) 1 , ( short ) ( progress + minEqualizer ) );
			}
		} );
		
		songEqualizerBandUp [ 2 ] = ( TextView ) findViewById ( R.id.song_equalizer_dialog_band3_up );
		songEqualizerBandDown [ 2 ] = ( TextView ) findViewById ( R.id.song_equalizer_dialog_band3_down );
		bandVerticalSeekBar [ 2 ] = ( MyVerticalSeekBar ) findViewById ( R.id.song_equalizer_dialog_band3_seekbar );
		songEqualizerBandDown [ 2 ].setText ( ( equalizer.getCenterFreq ( ( short ) 2 ) / 1000 ) + " HZ" );
		songEqualizerBandUp [ 2 ].setText ( "+" + ( maxEqualizer / 100 ) + " dB" );
		bandVerticalSeekBar [ 2 ].setMax ( maxEqualizer - minEqualizer );
		bandVerticalSeekBar [ 2 ].setProgress ( equalizer.getBandLevel ( ( short ) 2 ) - minEqualizer );
		bandVerticalSeekBar [ 2 ].setOnSeekBarChangeListener ( new OnSeekBarChangeListener ( ) {
			@ Override
			public void onStopTrackingTouch ( MyVerticalSeekBar VerticalSeekBar ) {
			}
			
			@ Override
			public void onStartTrackingTouch ( MyVerticalSeekBar VerticalSeekBar ) {
			}
			
			@ Override
			public void onProgressChanged ( MyVerticalSeekBar VerticalSeekBar , int progress , boolean fromUser ) {
				equalizer.setBandLevel ( ( short ) 2 , ( short ) ( progress + minEqualizer ) );
			}
		} );
		
		songEqualizerBandUp [ 3 ] = ( TextView ) findViewById ( R.id.song_equalizer_dialog_band4_up );
		songEqualizerBandDown [ 3 ] = ( TextView ) findViewById ( R.id.song_equalizer_dialog_band4_down );
		bandVerticalSeekBar [ 3 ] = ( MyVerticalSeekBar ) findViewById ( R.id.song_equalizer_dialog_band4_seekbar );
		songEqualizerBandDown [ 3 ].setText ( ( equalizer.getCenterFreq ( ( short ) 3 ) / 1000 ) + " HZ" );
		songEqualizerBandUp [ 3 ].setText ( "+" + ( maxEqualizer / 100 ) + " dB" );
		bandVerticalSeekBar [ 3 ].setMax ( maxEqualizer - minEqualizer );
		bandVerticalSeekBar [ 3 ].setProgress ( equalizer.getBandLevel ( ( short ) 3 ) - minEqualizer );
		bandVerticalSeekBar [ 3 ].setOnSeekBarChangeListener ( new OnSeekBarChangeListener ( ) {
			@ Override
			public void onStopTrackingTouch ( MyVerticalSeekBar VerticalSeekBar ) {
			}
			
			@ Override
			public void onStartTrackingTouch ( MyVerticalSeekBar VerticalSeekBar ) {
			}
			
			@ Override
			public void onProgressChanged ( MyVerticalSeekBar VerticalSeekBar , int progress , boolean fromUser ) {
				equalizer.setBandLevel ( ( short ) 3 , ( short ) ( progress + minEqualizer ) );
			}
		} );
		
		songEqualizerBandUp [ 4 ] = ( TextView ) findViewById ( R.id.song_equalizer_dialog_band5_up );
		songEqualizerBandDown [ 4 ] = ( TextView ) findViewById ( R.id.song_equalizer_dialog_band5_down );
		bandVerticalSeekBar [ 4 ] = ( MyVerticalSeekBar ) findViewById ( R.id.song_equalizer_dialog_band5_seekbar );
		songEqualizerBandDown [ 4 ].setText ( ( equalizer.getCenterFreq ( ( short ) 4 ) / 1000 ) + " HZ" );
		songEqualizerBandUp [ 4 ].setText ( "+" + ( maxEqualizer / 100 ) + " dB" );
		bandVerticalSeekBar [ 4 ].setMax ( maxEqualizer - minEqualizer );
		bandVerticalSeekBar [ 4 ].setProgress ( equalizer.getBandLevel ( ( short ) 4 ) - minEqualizer );
		bandVerticalSeekBar [ 4 ].setOnSeekBarChangeListener ( new OnSeekBarChangeListener ( ) {
			@ Override
			public void onStopTrackingTouch ( MyVerticalSeekBar VerticalSeekBar ) {
			}
			
			@ Override
			public void onStartTrackingTouch ( MyVerticalSeekBar VerticalSeekBar ) {
			}
			
			@ Override
			public void onProgressChanged ( MyVerticalSeekBar VerticalSeekBar , int progress , boolean fromUser ) {
				equalizer.setBandLevel ( ( short ) 4 , ( short ) ( progress + minEqualizer ) );
			}
		} );
		
		dialogSongEqualizerConfirmButton = ( Button ) findViewById ( R.id.song_equalizer_dialog_confirm );
		dialogSongEqualizerConfirmButton.setOnClickListener ( this );
		
		dialogSongEqualizerResetButton = ( Button ) findViewById ( R.id.song_equalizer_dialog_reset );
		dialogSongEqualizerResetButton.setOnClickListener ( this );
	}
	
	public SongEqualizerDialog ( Context context , int theme ) {
		// 将dialog的弹出以及弹入动画写入到一个dialog的属性主题中，让超类去实现dialog的动画
		super ( context , theme );
		this.context = context;
	}
	
	public SongEqualizerDialog ( Context context ) {
		this ( context , R.style.MyDialogTheme );
		equalizer = MainPlayerService.equalizer;
		bassBoost = MainPlayerService.bassBoost;
	}
	
	@ Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.song_equalizer_dialog );
		initWindow ( );
		setEQ ( );
		initView ( );
	}
	
	private void setEQ ( ) {
		( ( Activity ) context ).setVolumeControlStream ( AudioManager.STREAM_MUSIC );
		minEqualizer = equalizer.getBandLevelRange ( ) [ 0 ];
		maxEqualizer = equalizer.getBandLevelRange ( ) [ 1 ];
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
		
		switch ( v.getId ( ) ) {
			case R.id.song_equalizer_dialog_confirm :
				dismiss ( );
				break;
			
			case R.id.song_equalizer_dialog_reset :
				resetSettings ( );
				break;
			
			default :
				break;
		}
		
	}
	
	private void resetSettings ( ) {
		songBassSeekBar.setProgress ( 500 );
		bassBoost.setStrength ( ( short ) 500 );
		
		int halfEQ = minEqualizer + maxEqualizer >> 1;
		bandVerticalSeekBar [ 0 ].setProgress ( halfEQ - minEqualizer );
		equalizer.setBandLevel ( ( short ) 0 , ( short ) ( halfEQ ) );
		
		bandVerticalSeekBar [ 1 ].setProgress ( halfEQ - minEqualizer );
		equalizer.setBandLevel ( ( short ) 1 , ( short ) ( halfEQ ) );
		
		bandVerticalSeekBar [ 2 ].setProgress ( halfEQ - minEqualizer );
		equalizer.setBandLevel ( ( short ) 2 , ( short ) ( halfEQ ) );
		
		bandVerticalSeekBar [ 3 ].setProgress ( halfEQ - minEqualizer );
		equalizer.setBandLevel ( ( short ) 3 , ( short ) ( halfEQ ) );
		
		bandVerticalSeekBar [ 4 ].setProgress ( halfEQ - minEqualizer );
		equalizer.setBandLevel ( ( short ) 4 , ( short ) ( halfEQ ) );
		
	}
	
	@ Override
	protected void onStop ( ) {
		super.onStop ( );
		storeEqualizerSettings ( );
	}
	
	private void storeEqualizerSettings ( ) {
		bassStrength = bassBoost.getRoundedStrength ( );
		band1 = equalizer.getBandLevel ( ( short ) 0 );
		band2 = equalizer.getBandLevel ( ( short ) 1 );
		band3 = equalizer.getBandLevel ( ( short ) 2 );
		band4 = equalizer.getBandLevel ( ( short ) 3 );
		band5 = equalizer.getBandLevel ( ( short ) 4 );
		SharedPreferences.Editor editor = context.getSharedPreferences ( "lastMediaEqualizerStaus" , Context.MODE_PRIVATE ).edit ( );
		editor.putInt ( "band1" , band1 );
		System.out.println ( "storeEqualizerSettings  : " + band1 );
		editor.putInt ( "band2" , band2 );
		editor.putInt ( "band3" , band3 );
		editor.putInt ( "band4" , band4 );
		editor.putInt ( "band5" , band5 );
		editor.putInt ( "bassBoost" , bassStrength );
		System.out.println ( " bassStrength " + bassStrength );
		editor.commit ( );
	}
}
