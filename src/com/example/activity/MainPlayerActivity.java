package com.example.activity;

import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fragment.PlayerFramgment;
import com.example.fragment.PlayerFramgment.PlayerShutPlayerServiceCallBacker;
import com.example.fragment.PlayerFramgment.PlayerTabCallBacker;
import com.example.fragment.RapperFramgment;
import com.example.fragment.SongListFramgment;
import com.example.fragment.SongListFramgment.SongListCallBacker;
import com.example.song.Song;
import com.example.song.SongList;

public class MainPlayerActivity extends Activity implements OnClickListener , PlayerShutPlayerServiceCallBacker , PlayerTabCallBacker , SongListCallBacker {
	PlayerFramgment playerFramgment;
	SongListFramgment songListFramgment;
	RapperFramgment rapperFramgment;
	
	TextView palyerTabTextView;
	TextView rapperTabTextView;
	
	FragmentManager fragmentManager;
	
	public static List < Song > songs;
	
	private boolean is2to0 = false;
	
	Intent intentPlayerService;
	Intent intentRapperService;
	
	private static int lastOpenedFragment;
	
	private static boolean isFirstStartAPP = true;
	
	Handler handler = new Handler ( ) {
		public void handleMessage ( Message msg ) {
			if ( msg.what == 0x123 ) {
				songs = SongList.getSongData ( MainPlayerActivity.this );
			}
		}
	};
	
	@ Override
	protected void onCreate ( Bundle savedInstanceState ) {
		super.onCreate ( savedInstanceState );
		setContentView ( R.layout.main_activity );
		initView ( );
		songs = SongList.getSongData ( this );
		transferData ( );
		initPlayStatus ( );
	}
	
	@ Override
	protected void onRestoreInstanceState ( Bundle savedInstanceState ) {
		super.onRestoreInstanceState ( savedInstanceState );
		MainPlayerActivity.lastOpenedFragment = savedInstanceState.getInt ( "lastOpenedFragment" , 0 );
		MainPlayerActivity.isFirstStartAPP = savedInstanceState.getBoolean ( "isFirstStartAPP" , true );
	}
	
	@ Override
	protected void onRestart ( ) {
		super.onRestart ( );
		// initFragment ( );
		// setTabSelected ( lastOpenedFragment , fragmentManager );
	}
	
	@ Override
	protected void onStart ( ) {
		super.onStart ( );
		initFragment ( );
		if ( isFirstStartAPP ) {
			setTabSelected ( 0 , fragmentManager );
		}
	}
	
	@ Override
	protected void onResume ( ) {
		super.onResume ( );
		if ( ! isFirstStartAPP ) {
			setTabSelected ( MainPlayerActivity.lastOpenedFragment , fragmentManager );
		}
		isFirstStartAPP = false;
	}
	
	@ Override
	protected void onSaveInstanceState ( Bundle outState ) {
		super.onSaveInstanceState ( outState );
		outState.putInt ( "lastOpenedFragment" , MainPlayerActivity.lastOpenedFragment );
		outState.putBoolean ( "isFirstStartAPP" , MainPlayerActivity.isFirstStartAPP );
	}
	
	@ Override
	protected void onDestroy ( ) {
		super.onDestroy ( );
	}
	
	private void initPlayStatus ( ) {
		intentPlayerService = new Intent ( MainPlayerActivity.this , com.example.service.MainPlayerService.class );
		intentPlayerService.putExtra ( "type" , - 1 );
		startService ( intentPlayerService );
		intentRapperService = new Intent ( MainPlayerActivity.this , com.example.service.RapperService.class );
		intentRapperService.putExtra ( "type" , - 1 );
		startService ( intentRapperService );
	}
	
	private void transferData ( ) {
		new Thread ( new Runnable ( ) {
			@ Override
			public void run ( ) {
				while ( songs == null ) {
					handler.sendEmptyMessage ( 0x123 );
					try {
						Thread.sleep ( 200 );
					} catch ( InterruptedException e ) {
						e.printStackTrace ( );
					}
					if ( songs != null ) {
						playerFramgment.songs = MainPlayerActivity.songs;
						songListFramgment.songs = MainPlayerActivity.songs;
						
					}
				}
			}
		} ).start ( );
		
	}
	
	private void initView ( ) {
		palyerTabTextView = ( TextView ) findViewById ( R.id.tab_text_palyer );
		rapperTabTextView = ( TextView ) findViewById ( R.id.tab_text_rapper );
		
		palyerTabTextView.setOnClickListener ( this );
		rapperTabTextView.setOnClickListener ( this );
	}
	
	public void setTabSelected ( int index , FragmentManager fragmentManager ) {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction ( );
		removeFragments ( fragmentTransaction );
		switch ( index ) {
			case 0 :// Color.parseColor ( "#757575" )
				palyerTabTextView.setTextColor ( getResources ( ).getColor ( R.color.text_dark ) );
				rapperTabTextView.setTextColor ( getResources ( ).getColor ( R.color.text_white_light ) );
				palyerTabTextView.setClickable ( false );
				rapperTabTextView.setClickable ( true );
				if ( is2to0 ) {
					fragmentTransaction.setCustomAnimations ( R.animator.slide_up_in , R.animator.slide_up_out );
					is2to0 = false;
				}
				fragmentTransaction.replace ( R.id.player_content , playerFramgment );
				// fragmentTransaction.addToBackStack (
				// "playerFramgment" );
				// fragmentTransaction.show ( playerFramgment );
				fragmentTransaction.commit ( );
				lastOpenedFragment = 0;
				break;
			case 1 :
				rapperTabTextView.setTextColor ( Color.parseColor ( "#757575" ) );
				palyerTabTextView.setTextColor ( Color.parseColor ( "#ffffff" ) );
				palyerTabTextView.setClickable ( true );
				rapperTabTextView.setClickable ( false );
				// fragmentTransaction.addToBackStack (
				// "rapperFramgment" );
				fragmentTransaction.replace ( R.id.player_content , rapperFramgment );
				// fragmentTransaction.show ( rapperFramgment );
				fragmentTransaction.commit ( );
				lastOpenedFragment = 1;
				break;
			case 2 :
				palyerTabTextView.setTextColor ( getResources ( ).getColor ( R.color.text_dark ) );
				rapperTabTextView.setTextColor ( getResources ( ).getColor ( R.color.text_white_light ) );
				palyerTabTextView.setClickable ( false );
				rapperTabTextView.setClickable ( true );
				fragmentTransaction.setCustomAnimations ( R.animator.slide_down_in , R.animator.slide_down_out );
				fragmentTransaction.replace ( R.id.player_content , songListFramgment );
				// fragmentTransaction.addToBackStack (
				// "rapperFramgment" );
				// fragmentTransaction.show ( songListFramgment
				// );
				fragmentTransaction.commit ( );
				lastOpenedFragment = 2;
				break;
			default :
				break;
		}
		
	}
	
	private void removeFragments ( FragmentTransaction fragmentTransaction ) {
		if ( playerFramgment.isAdded ( ) ) {
			fragmentTransaction.remove ( playerFramgment );
		}
		if ( rapperFramgment.isAdded ( ) ) {
			fragmentTransaction.remove ( rapperFramgment );
		}
		if ( songListFramgment.isAdded ( ) ) {
			fragmentTransaction.remove ( songListFramgment );
		}
		
	}
	
	private void initFragment ( ) {
		fragmentManager = getFragmentManager ( );
		playerFramgment = new PlayerFramgment ( this );
		songListFramgment = new SongListFramgment ( this );
		rapperFramgment = new RapperFramgment ( this );
	}
	
	@ Override
	public void onClick ( View v ) {
		switch ( v.getId ( ) ) {
			case R.id.tab_text_palyer :
				
				setTabSelected ( 0 , fragmentManager );
				break;
			case R.id.tab_text_rapper :
				
				setTabSelected ( 1 , fragmentManager );
				break;
			default :
				break;
		}
		
	}
	
	@ Override
	public void onPlayerCallBackerClick ( ) {
		setTabSelected ( 2 , fragmentManager );
		
	}
	
	@ Override
	public void onSongListCallBackerClick ( ) {
		is2to0 = true;
		setTabSelected ( 0 , fragmentManager );
		
	}
	
	@ Override
	public void onPlayerShutPlayerServiceCallBackerClick ( ) {
		
		// 销毁音乐播放放在一个按钮事件中！
		stopService ( intentPlayerService );
		stopService ( intentRapperService );
		MainPlayerActivity.this.finish ( );
	}
	
}
