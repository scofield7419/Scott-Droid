package com.example.fragment;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.activity.MainPlayerActivity;
import com.example.activity.R;
import com.example.dialog.MyDialog;
import com.example.dialog.MyDialog.OnMyDialogBtnsClickListener;
import com.example.dialog.SongAboutDialog;
import com.example.dialog.SongEqualizerDialog;
import com.example.dialog.SongInfoDialog;
import com.example.dialog.SongTimerDialog;
import com.example.dialog.SongTimerDialog.SongTimerDialogBtnsClickListener;
import com.example.service.MainPlayerService;
import com.example.song.Song;
import com.example.utils.DebugUtil;
import com.example.utils.MyConstant;
import com.example.view.MyLyricView;
import com.example.view.MyPagerView;
import com.example.view.MyPagerView.OnChildCustomTouchListener;
import com.example.view.MyPagerView.OnScrollToScreenListener;

public class PlayerFramgment extends Fragment implements OnClickListener , OnSeekBarChangeListener , SongTimerDialogBtnsClickListener , OnChildCustomTouchListener , OnScrollToScreenListener {
	View mainPlayer;
	
	Context context;
	
	LocalBroadcastManager localBroadcastManager0;
	ViewSeekBarUpdateBroadCastReceiver viewSeekBarUpdateBroadCastReceiver;
	PlayerCompleteUpdateBroadCastReceiver completeUpdateBroadCastReceiver;
	PlayerBtnUpdateBroadCastReceiver palyerBtnUpdateBroadCastReceiver;
	PlayerViewLoopLogoUpdateBroadCastReceiver viewLoopLogoUpdateBroadCastReceiver;
	
	ImageButton verticalBackButton;
	ImageButton loopButton;
	TextView songTitleTextView;
	TextView songInfoTextView;
	Button prevButton;
	Button playButton;
	Button nextButton;
	SeekBar musicProcess;
	TextView musicProcessCurTime;
	TextView musicProcessDuration;
	
	ImageView playerAlbumArea;
	MyPagerView playerPagerView;
	public static MyLyricView playerLyricView;
	
	private int threeLoopTag;
	private int [ ] loopLogoIds = new int [ ] { R.drawable.loop_btn ,
	                R.drawable.loop_single_btn , R.drawable.shuffle_btn };
	private int [ ] playBtnIds = new int [ ] { R.drawable.play_btn ,
	                R.drawable.pause_btn };
	public List < Song > songs;
	// 第一个是播放按钮记录，第二个是loop状态记忆，第三个是歌曲id。
	private int [ ] memoryTempView = new int [ 3 ];
	
	public interface PlayerTabCallBacker {
		public void onPlayerCallBackerClick ( );
	}
	
	public interface PlayerShutPlayerServiceCallBacker {
		public void onPlayerShutPlayerServiceCallBackerClick ( );
	}
	
	public PlayerFramgment ( Context context ) {
		this.context = context;
	}
	
	@ Override
	public void onAttach ( Activity activity ) {
		super.onAttach ( activity );
		initBroadcastReceivers ( );
		this.songs = MainPlayerActivity.songs;
	}
	
	@ Override
	public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate ( savedInstanceState );
	}
	
	@ Override
	public void onDetach ( ) {
		super.onDetach ( );
	}
	
	private void initBroadcastReceivers ( ) {
		localBroadcastManager0 = LocalBroadcastManager.getInstance ( context );
		viewSeekBarUpdateBroadCastReceiver = new ViewSeekBarUpdateBroadCastReceiver ( );
		IntentFilter filter1 = new IntentFilter ( );
		filter1.addAction ( MyConstant.ACTION_BROADCAST_PLAYER_SEEKBAR_UPDATE );
		localBroadcastManager0.registerReceiver ( viewSeekBarUpdateBroadCastReceiver , filter1 );
		
		completeUpdateBroadCastReceiver = new PlayerCompleteUpdateBroadCastReceiver ( );
		IntentFilter filter2 = new IntentFilter ( );
		filter2.addAction ( MyConstant.ACTION_BROADCAST_PLAYER_COMPLETE_UPDATE );
		localBroadcastManager0.registerReceiver ( completeUpdateBroadCastReceiver , filter2 );
		
		palyerBtnUpdateBroadCastReceiver = new PlayerBtnUpdateBroadCastReceiver ( );
		IntentFilter filter3 = new IntentFilter ( );
		filter3.addAction ( MyConstant.ACTION_BROADCAST_PLAYER_BTN_UPDATE );
		localBroadcastManager0.registerReceiver ( palyerBtnUpdateBroadCastReceiver , filter3 );
		
		viewLoopLogoUpdateBroadCastReceiver = new PlayerViewLoopLogoUpdateBroadCastReceiver ( );
		IntentFilter filter4 = new IntentFilter ( );
		filter4.addAction ( MyConstant.ACTION_BROADCAST_PLAYER_LOOPLOGO_UPDATE );
		localBroadcastManager0.registerReceiver ( viewLoopLogoUpdateBroadCastReceiver , filter4 );
	}
	
	@ Override
	public View onCreateView ( LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState ) {
		mainPlayer = inflater.inflate ( R.layout.main_player_frag , container , false );
		initView ( mainPlayer );
		return mainPlayer;
	}
	
	@ Override
	public void onStart ( ) {
		super.onStart ( );
		informSreviceUpdateMe ( );
	}
	
	@ Override
	public void onResume ( ) {
		super.onResume ( );
	}
	
	private void informSreviceUpdateMe ( ) {
		Intent intentUpdateMe = new Intent ( MyConstant.ACTION_BROADCAST_PLAYERSERVICE_PLAYERFRAG_REBACK_UPDATE );
		intentUpdateMe.putExtra ( "isFromPlayerFrag" , 0 );
		localBroadcastManager0.sendBroadcast ( intentUpdateMe );
	}
	
	private void initView ( View mainPlayer ) {
		verticalBackButton = ( ImageButton ) mainPlayer.findViewById ( R.id.vertical_back_btn );
		loopButton = ( ImageButton ) mainPlayer.findViewById ( R.id.loop_btn );
		verticalBackButton.setOnClickListener ( this );
		loopButton.setOnClickListener ( this );
		
		songTitleTextView = ( TextView ) mainPlayer.findViewById ( R.id.song_title );
		songInfoTextView = ( TextView ) mainPlayer.findViewById ( R.id.song_info );
		
		playButton = ( Button ) mainPlayer.findViewById ( R.id.btn_play );
		prevButton = ( Button ) mainPlayer.findViewById ( R.id.btn_prev );
		nextButton = ( Button ) mainPlayer.findViewById ( R.id.btn_next );
		playButton.setOnClickListener ( this );
		prevButton.setOnClickListener ( this );
		nextButton.setOnClickListener ( this );
		
		musicProcess = ( SeekBar ) mainPlayer.findViewById ( R.id.music_process );
		musicProcess.setOnSeekBarChangeListener ( this );
		
		musicProcessCurTime = ( TextView ) mainPlayer.findViewById ( R.id.music_process_currtime );
		musicProcessDuration = ( TextView ) mainPlayer.findViewById ( R.id.music_process_duration );
		
		playerLyricView = ( MyLyricView ) mainPlayer.findViewById ( R.id.lyric_area );
		playerAlbumArea = ( ImageView ) mainPlayer.findViewById ( R.id.album_area );
		
		playerPagerView = ( MyPagerView ) mainPlayer.findViewById ( R.id.player_lyric_pager );
		playerPagerView.setOnChildCustomTouchListener ( this );
		playerPagerView.setOnScrollToScreenListener ( this );
	}
	
	private void changePlayStatu ( ) {
		Intent intent = new Intent ( context , com.example.service.MainPlayerService.class );
		intent.putExtra ( "type" , 1 );
		intent.putExtra ( "playOperation" , 0 );
		context.startService ( intent );
	}
	
	private void changeLoopMode ( ) {
		int loopOperation = changeLoopLogo ( );
		Intent intent = new Intent ( context , com.example.service.MainPlayerService.class );
		intent.putExtra ( "type" , 0 );
		intent.putExtra ( "loopOperation" , loopOperation );
		context.startService ( intent );
	}
	
	private int changeLoopLogo ( ) {
		int tempThreeLoopTag = ( ++ threeLoopTag ) % loopLogoIds.length;
		loopButton.setImageDrawable ( getResources ( ).getDrawable ( loopLogoIds [ tempThreeLoopTag ] ) );
		return tempThreeLoopTag;
	}
	
	private void playPrev ( ) {
		Intent intent = new Intent ( context , com.example.service.MainPlayerService.class );
		intent.putExtra ( "type" , 1 );
		intent.putExtra ( "playOperation" , 1 );
		context.startService ( intent );
	}
	
	private void playNext ( ) {
		Intent intent = new Intent ( context , com.example.service.MainPlayerService.class );
		intent.putExtra ( "type" , 1 );
		intent.putExtra ( "playOperation" , 2 );
		context.startService ( intent );
	}
	
	@ Override
	public void onProgressChanged ( SeekBar seekBar , int progress , boolean fromUser ) {
	}
	
	@ Override
	public void onStartTrackingTouch ( SeekBar seekBar ) {
	}
	
	@ Override
	public void onStopTrackingTouch ( SeekBar seekBar ) {
		int playerProcess = seekBar.getProgress ( );
		Intent intent = new Intent ( MyConstant.ACTION_BROADCAST_PLAYERSERVICE_PLAYER_STATU_UPDATE );
		intent.putExtra ( "playerProcess" , playerProcess );
		localBroadcastManager0.sendBroadcast ( intent );
	}
	
	private String setDuration2String ( int duration ) {
		duration /= 1000;
		int second = duration % 60;
		int minute = duration / 60;
		return String.format ( "%02d:%02d" , minute , second );
	}
	
	public class ViewSeekBarUpdateBroadCastReceiver extends BroadcastReceiver {
		@ Override
		public void onReceive ( Context context , Intent intent ) {
			int positionProcess = intent.getIntExtra ( "positionProcess" , 0 );
			int duration = intent.getIntExtra ( "duration" , 0 );
			int curPosition = positionProcess * 100 / duration;
			musicProcess.setProgress ( curPosition );
			musicProcessCurTime.setText ( setDuration2String ( positionProcess ) );
			musicProcessDuration.setText ( setDuration2String ( duration ) );
			// 更新播放进度的同时更新歌词滚动
			playerLyricView.invalidate ( );
		}
	}
	
	public class PlayerViewLoopLogoUpdateBroadCastReceiver extends BroadcastReceiver {
		@ Override
		public void onReceive ( Context context , Intent intent ) {
			if ( ! PlayerFramgment.this.isAdded ( ) ) {
				return;
			}
			int loopLogo = intent.getIntExtra ( "loopLogo" , 0 );
			loopButton.setImageDrawable ( getResources ( ).getDrawable ( loopLogoIds [ loopLogo ] ) );
			threeLoopTag = loopLogo;
			memoryTempView [ 1 ] = loopLogo;
		}
	}
	
	public class PlayerCompleteUpdateBroadCastReceiver extends BroadcastReceiver {
		
		@ Override
		public void onReceive ( Context context , Intent intent ) {
			if ( ! PlayerFramgment.this.isAdded ( ) ) {
				return;
			}
			int songId = intent.getIntExtra ( "songId" , 0 );
			Song song = songs.get ( songId );
			String title = song.getTitle ( );
			String info = song.getAuthor ( ) + MyConstant.CONNECTION_WORD + song.getSongAlbum ( );
			songTitleTextView.setText ( title );
			songInfoTextView.setText ( info );
			memoryTempView [ 2 ] = songId;
		}
		
	}
	
	public class PlayerBtnUpdateBroadCastReceiver extends BroadcastReceiver {
		
		@ Override
		public void onReceive ( Context context , Intent intent ) {
			if ( ! PlayerFramgment.this.isAdded ( ) ) {
				return;
			}
			int btnTag = intent.getIntExtra ( "playBtnTag" , 0 );
			playButton.setBackground ( getResources ( ).getDrawable ( playBtnIds [ btnTag ] ) );
			memoryTempView [ 0 ] = btnTag;
		}
		
	}
	
	@ Override
	public void operation ( MotionEvent event ) {
		showMenuDialog ( );
	}
	
	@ Override
	public void onClick ( View v ) {
		switch ( v.getId ( ) ) {
			case R.id.vertical_back_btn :
				if ( getActivity ( ) instanceof PlayerTabCallBacker ) {
					( ( PlayerTabCallBacker ) getActivity ( ) ).onPlayerCallBackerClick ( );
				}
				break;
			case R.id.loop_btn :
				changeLoopMode ( );
				break;
			case R.id.btn_play :
				changePlayStatu ( );
				break;
			case R.id.btn_next :
				playNext ( );
				break;
			case R.id.btn_prev :
				playPrev ( );
				break;
			
			default :
				break;
		}
	}
	
	private void showMenuDialog ( ) {
		final MyDialog myDialog = new MyDialog ( context , R.style.MyDialogTheme );
		myDialog.show ( );
		myDialog.setOnMyDialogBtnsClickListener ( new OnMyDialogBtnsClickListener ( ) {
			@ Override
			public void onMyDialogBtnsClickListener ( View v ) {
				// TODO Auto-generated method stub
				switch ( v.getId ( ) ) {
					case R.id.dialog_info :
						showDialogSongInfo ( );
						break;
					case R.id.dialog_edit :
						DebugUtil.toa ( context , "dialog_edit功能完善中~" );
						break;
					case R.id.dialog_ringbell :
						DebugUtil.toa ( context , "dialog_ringbell功能完善中~" );
						break;
					case R.id.dialog_equalizer :
						showDialogSongEqualizer ( );
						break;
					case R.id.dialog_timer :
						showDialogSongTimer ( );
						break;
					case R.id.dialog_about :
						showDialogAbout ( );
						break;
					case R.id.dialog_exit :
						// 在shut down按钮事件中关闭程序，执行回调
						if ( getActivity ( ) instanceof PlayerShutPlayerServiceCallBacker ) {
							( ( PlayerShutPlayerServiceCallBacker ) getActivity ( ) ).onPlayerShutPlayerServiceCallBackerClick ( );
						}
						break;
				}
				myDialog.dismiss ( );
			}
			
		} );
	}
	
	private void showDialogAbout ( ) {
		SongAboutDialog songAboutDialog = new SongAboutDialog ( context );
		songAboutDialog.show ( );
	}
	
	private void showDialogSongTimer ( ) {
		SongTimerDialog songTimerDialog = new SongTimerDialog ( context );
		songTimerDialog.show ( );
		songTimerDialog.setOnSongTimerDialogBtnsClickListener ( this );
	}
	
	private void showDialogSongEqualizer ( ) {
		SongEqualizerDialog songEqualizerDialog = new SongEqualizerDialog ( context );
		songEqualizerDialog.show ( );
	}
	
	private void showDialogSongInfo ( ) {
		Song curSong = songs.get ( memoryTempView [ 2 ] );
		SongInfoDialog songInfoDialog = new SongInfoDialog ( context , curSong );
		songInfoDialog.show ( );
	}
	
	@ Override
	public void operation ( int currentScreen , int screenCount ) {
		if ( currentScreen == 1 ) {
			this.playerAlbumArea.setAnimation ( AnimationUtils.loadAnimation ( context , R.anim.slide_to_album ) );
		} else {
			playerLyricView.setAnimation ( AnimationUtils.loadAnimation ( context , R.anim.slide_to_lyric ) );
		}
	}
	
	@ Override
	public void onSongTimerDialogBtnsClickListener ( int time ) {
		int millisecon = time * 60 * 1000;
		new Timer ( ).schedule ( new TimerTask ( ) {
			public void run ( ) {
				if ( ! MainPlayerService.mediaPlayer.isPlaying ( ) ) {
					return;
				}
				Intent intent = new Intent ( context , com.example.service.MainPlayerService.class );
				intent.putExtra ( "type" , 1 );
				intent.putExtra ( "playOperation" , 0 );
				context.startService ( intent );
			}
		} , millisecon );
	}
	
}
