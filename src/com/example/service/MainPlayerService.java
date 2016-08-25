package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.animation.AnimationUtils;

import com.example.activity.R;
import com.example.fragment.PlayerFramgment;
import com.example.lyric.Lyric;
import com.example.lyric.LyricProcessor;
import com.example.song.Song;
import com.example.song.SongList;
import com.example.utils.MyConstant;

public class MainPlayerService extends Service {
	LocalBroadcastManager localBroadcastManager2;
	PlayerStatuUpdateBroadCastReceiver playerStatuUpdateBroadCastReceiver;
	UpdateListFragFirstViewBroadCastReceiver updateListFragFirstViewBroadCastReceiver;
	UpdatePlayerFragRebackBroadCastReceiver updatePlayerFragRebackBroadCastReceiver;
	
	HeadsetPlugReceiver headsetPlugReceiver;
	
	private AudioManager audioManager;
	
	private MyOnAudioFocusChangeListener myOnAudioFocusChangeListener;
	
	public List < Song > songs;
	private LyricProcessor mLyricProcessor;
	private List < Lyric > lrcList = new ArrayList < Lyric > ( );
	// 歌词检索值
	private int index = 0;
	
	public static MediaPlayer mediaPlayer;
	public static Equalizer equalizer;
	public static BassBoost bassBoost;
	
	int currSongPositionInList;
	public int lastPlayerPositoinInList;
	public int lastPlayerProcess;
	public int currSongLoopProcess;
	public int loopStatus;
	
	private boolean isServiceAlive;
	
	private boolean isFinshACall;
	
	// 所有涉及到更改播放状态的地方都要更改此标签，便于给rapper做处理。
	public boolean isPlaying;
	
	private Thread updatePlayerThread;
	
	public MainPlayerService ( ) {
		super ( );
	}
	
	@ Override
	public IBinder onBind ( Intent intent ) {
		return null;
	}
	
	@ Override
	public void onCreate ( ) {
		super.onCreate ( );
		if ( this.songs == null ) {
			songs = SongList.getSongData ( MainPlayerService.this );
		}
		createMediaSetting ( );
		initBroadcastReceiver ( );
		restoreLastData ( );
		sendStartBroad ( );
		
		//下面两组标记如果顺序弄错了，必报错
		isFinshACall = false;
		isServiceAlive = true;
		initPlayerSeekBarThread ( );
		initPhoneListener ( );
		initSystemAudioListener ( );
	}
	
	private void createMediaSetting ( ) {
		mediaPlayer = new MediaPlayer ( );
		equalizer = new Equalizer ( 0 , mediaPlayer.getAudioSessionId ( ) );
//		if ( equalizer.getEnabled ( ) ) {
		equalizer.setEnabled ( true );
//		}
		bassBoost = new BassBoost ( 0 , mediaPlayer.getAudioSessionId ( ) );
//		if ( bassBoost.getEnabled ( ) ) {
		bassBoost.setEnabled ( true );
//		}
		//MEIZU MX5   有低音增强功能； 小米1 没有
//		System.out.println ("bassBoost.getEnabled"+bassBoost.getEnabled ( ));
	}
	
	private void initSystemAudioListener ( ) {
		audioManager = ( AudioManager ) getApplicationContext ( ).getSystemService ( Context.AUDIO_SERVICE );
		myOnAudioFocusChangeListener = new MyOnAudioFocusChangeListener ( );
	}
	
	private void initPhoneListener ( ) {
		// 添加来电监听事件
		TelephonyManager telManager = ( TelephonyManager ) getSystemService ( Context.TELEPHONY_SERVICE ); // 获取系统服务
		telManager.listen ( new MyPhoneStateListener ( ) , PhoneStateListener.LISTEN_CALL_STATE );
		
	}
	
	private class MyPhoneStateListener extends PhoneStateListener {
		@ Override
		public void onCallStateChanged ( int state , String incomingNumber ) {
//			System.out.println (state);
			switch ( state ) {
				case TelephonyManager.CALL_STATE_IDLE : // 挂机状态
					if ( ( ! MainPlayerService.mediaPlayer.isPlaying ( ) ) && isFinshACall ) {
						Intent intent = new Intent ( MainPlayerService.this , com.example.service.MainPlayerService.class );
						intent.putExtra ( "type" , 1 );
						intent.putExtra ( "playOperation" , 0 );
						startService ( intent );
						isFinshACall = false;
					}
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK : // 通话状态
				case TelephonyManager.CALL_STATE_RINGING : // 响铃状态
					if ( MainPlayerService.mediaPlayer.isPlaying ( ) ) {
						Intent intent2 = new Intent ( MainPlayerService.this , com.example.service.MainPlayerService.class );
						intent2.putExtra ( "type" , 1 );
						intent2.putExtra ( "playOperation" , 0 );
						startService ( intent2 );
						isFinshACall = true;
					}
					break;
				default :
					break;
			}
		}
	}
	
	private void restoreLastData ( ) {
		restoreSongPref ( );
		restoreMediaPref ( );
	}
	
	private void restoreMediaPref ( ) {
		SharedPreferences sharedPreferences = getSharedPreferences ( "lastMediaEqualizerStaus" , Context.MODE_PRIVATE );
		int minEqu = equalizer.getBandLevelRange ( ) [ 0 ];
		int bandGap = equalizer.getBandLevelRange ( ) [ 1 ] - equalizer.getBandLevelRange ( ) [ 0 ];
		
		int band1 = sharedPreferences.getInt ( "band1" , ( int ) ( minEqu + bandGap * 0.95 ) );
		equalizer.setBandLevel ( ( short ) 0 , ( short ) band1 );
		
		int band2 = sharedPreferences.getInt ( "band2" , ( int ) ( minEqu + bandGap * 0.85 ) );
		equalizer.setBandLevel ( ( short ) 1 , ( short ) band2 );
		
		int band3 = sharedPreferences.getInt ( "band3" , ( int ) ( minEqu + bandGap * 0.75 ) );
		equalizer.setBandLevel ( ( short ) 2 , ( short ) band3 );
		
		int band4 = sharedPreferences.getInt ( "band4" , ( int ) ( minEqu + bandGap * 0.85 ) );
		equalizer.setBandLevel ( ( short ) 3 , ( short ) band4 );
		
		int band5 = sharedPreferences.getInt ( "band5" , ( int ) ( minEqu + bandGap * 0.95 ) );
		equalizer.setBandLevel ( ( short ) 4 , ( short ) band5 );
		
		int bassTmp = 300;
		int bassStrength = sharedPreferences.getInt ( "bassBoost" , bassTmp );
		bassBoost.setStrength ( ( short ) bassStrength );
	}
	
	private void restoreSongPref ( ) {
		SharedPreferences sharedPreferences = getSharedPreferences ( "lastPlayStaus" , Service.MODE_PRIVATE );
		if ( sharedPreferences.contains ( "lastPlayerProcess" ) ) {
			this.lastPlayerProcess = sharedPreferences.getInt ( "lastPlayerProcess" , 0 );
			this.currSongLoopProcess = this.lastPlayerProcess;
		}
		if ( sharedPreferences.contains ( "lastPlayerPositoinInList" ) ) {
			this.currSongPositionInList = sharedPreferences.getInt ( "lastPlayerPositoinInList" , 0 );
		}
		if ( sharedPreferences.contains ( "lastLoopState" ) ) {
			this.loopStatus = sharedPreferences.getInt ( "lastLoopState" , 0 );
		}
		if ( sharedPreferences.contains ( "lastisPlaying" ) ) {
			this.isPlaying = sharedPreferences.getBoolean ( "lastisPlaying" , false );
		}
	}
	
	private void sendStartBroad ( ) {
		// 改变player的UI为播放
		Intent intentPause = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_BTN_UPDATE );
		intentPause.putExtra ( "playBtnTag" , 0 );
		localBroadcastManager2.sendBroadcast ( intentPause );
		
		// 给player发送songid
		Intent intentPlayerText = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_COMPLETE_UPDATE );
		intentPlayerText.putExtra ( "songId" , this.currSongPositionInList );
		localBroadcastManager2.sendBroadcast ( intentPlayerText );
		
		// 把实时播放进度发送给player
		Intent intentCurProcess = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_SEEKBAR_UPDATE );
		intentCurProcess.putExtra ( "positionProcess" , this.lastPlayerProcess );
		intentCurProcess.putExtra ( "duration" , ( int ) songs.get ( this.currSongPositionInList ).getDuration ( ) );
		localBroadcastManager2.sendBroadcast ( intentCurProcess );
		
		// 把loopstatus发送给player
		Intent intentLoopStatus = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_LOOPLOGO_UPDATE );
		intentLoopStatus.putExtra ( "loopLogo" , loopStatus );
		localBroadcastManager2.sendBroadcast ( intentLoopStatus );
	}
	
	private void initPlayerSeekBarThread ( ) {
		updatePlayerThread = new Thread ( new Runnable ( ) {
			@ Override
			public void run ( ) {
				while ( isServiceAlive ) {
					try {
						Thread.sleep ( 300 );
					} catch ( InterruptedException e ) {
						e.printStackTrace ( );
					}
					// 把looplogo传给player
					Intent intentLoopLogo = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_LOOPLOGO_UPDATE );
					intentLoopLogo.putExtra ( "loopLogo" , loopStatus );
					localBroadcastManager2.sendBroadcast ( intentLoopLogo );
					if ( ( mediaPlayer != null ) && ( mediaPlayer.isPlaying ( ) ) ) {
						// 把实时播放进度发送给player
						Intent intentSeekBar = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_SEEKBAR_UPDATE );
						int positionProcess = mediaPlayer.getCurrentPosition ( );
						currSongLoopProcess = positionProcess;
						int duration = mediaPlayer.getDuration ( );
						intentSeekBar.putExtra ( "positionProcess" , positionProcess );
						intentSeekBar.putExtra ( "duration" , duration );
						localBroadcastManager2.sendBroadcast ( intentSeekBar );
						// 实时传送歌词的同步信息
						PlayerFramgment.playerLyricView.setIndex ( lrcIndex ( ) );
					}
				}
				
			}
		} );
		updatePlayerThread.start ( );
	}
	
	@ Override
	public void onDestroy ( ) {
		isServiceAlive = false;
		if ( mediaPlayer != null ) {
			mediaPlayer.stop ( );
			mediaPlayer.release ( );
			mediaPlayer = null;
		}
		this.lastPlayerProcess = this.currSongLoopProcess;
		this.lastPlayerPositoinInList = this.currSongPositionInList;
		SharedPreferences.Editor editor = getSharedPreferences ( "lastPlayStaus" , Service.MODE_PRIVATE ).edit ( );
		editor.putInt ( "lastPlayerProcess" , this.lastPlayerProcess );
		editor.putInt ( "lastLoopState" , this.loopStatus );
		editor.putBoolean ( "lastisPlaying" , this.isPlaying );
		editor.putInt ( "lastPlayerPositoinInList" , this.lastPlayerPositoinInList );
		editor.commit ( );
		unregisterReceiver ( headsetPlugReceiver );
		audioManager.abandonAudioFocus ( myOnAudioFocusChangeListener );
	}
	
	private void initBroadcastReceiver ( ) {
		localBroadcastManager2 = LocalBroadcastManager.getInstance ( this );
		playerStatuUpdateBroadCastReceiver = new PlayerStatuUpdateBroadCastReceiver ( );
		IntentFilter filter = new IntentFilter ( );
		filter.addAction ( MyConstant.ACTION_BROADCAST_PLAYERSERVICE_PLAYER_STATU_UPDATE );
		localBroadcastManager2.registerReceiver ( playerStatuUpdateBroadCastReceiver , filter );
		
		updateListFragFirstViewBroadCastReceiver = new UpdateListFragFirstViewBroadCastReceiver ( );
		IntentFilter filter2 = new IntentFilter ( );
		filter2.addAction ( MyConstant.ACTION_BROADCAST_PLAYERSERVICE_LISTFRAG_FIRST_UPDATE );
		localBroadcastManager2.registerReceiver ( updateListFragFirstViewBroadCastReceiver , filter2 );
		
		updatePlayerFragRebackBroadCastReceiver = new UpdatePlayerFragRebackBroadCastReceiver ( );
		IntentFilter filter3 = new IntentFilter ( );
		filter3.addAction ( MyConstant.ACTION_BROADCAST_PLAYERSERVICE_PLAYERFRAG_REBACK_UPDATE );
		localBroadcastManager2.registerReceiver ( updatePlayerFragRebackBroadCastReceiver , filter3 );
		
		//监听系统耳机插入拔出情况,此BR是全局的，需要在destroy时注销注册
		headsetPlugReceiver = new HeadsetPlugReceiver ( );
		IntentFilter filter4 = new IntentFilter ( );
		filter4.addAction ( AudioManager.ACTION_AUDIO_BECOMING_NOISY );
		registerReceiver ( headsetPlugReceiver , filter4 );
	}
	
	/**
	 * Audio输出通道切换 从硬件层面来看，直接监听耳机拔出事件不难，耳机的拔出和插入，会引起手机电平的变化，然后触发什么什么中断
	 * 监听Android的系统广播AudioManager.ACTION_AUDIO_BECOMING_NOISY，
	 * 但是这个广播只是针对有线耳机
	 * 
	 * @author scott
	 * 
	 */
	public class HeadsetPlugReceiver extends BroadcastReceiver {
		
		@ Override
		public void onReceive ( Context context , Intent intent ) {
			String action = intent.getAction ( );
			if ( AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals ( action ) ) {
				if ( MainPlayerService.mediaPlayer.isPlaying ( ) ) {
					Intent intent1 = new Intent ( MainPlayerService.this , com.example.service.MainPlayerService.class );
					intent1.putExtra ( "type" , 1 );
					intent1.putExtra ( "playOperation" , 0 );
					startService ( intent1 );
				}
			}
		}
		
	}
	
	@ Override
	public int onStartCommand ( Intent intent , int flags , int startId ) {
		// 第一次在activity中启动service时，必须让type不为这里的三种值，否则总是会有错误!
		if ( intent == null ) {
			return super.onStartCommand ( intent , flags , startId );
		}
		int type = intent.getIntExtra ( "type" , 0 );
		switch ( type ) {
			case 0 :
				int loopOperation = intent.getIntExtra ( "loopOperation" , 0 );
				this.loopStatus = loopOperation;
				break;
			case 1 :
				int playOperation = intent.getIntExtra ( "playOperation" , 0 );
				if ( playOperation == 0 ) {
					if ( mediaPlayer.isPlaying ( ) ) {
						// 对currSongLoopProcess进行赋值只有三处：初试从sharedpref获得，播放时实时从run中获得，暂停前赋值
						this.currSongLoopProcess = mediaPlayer.getCurrentPosition ( );
						mediaPlayer.pause ( );
						this.isPlaying = false;
						
						// 改变player的UI为播放
						Intent intentPlay = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_BTN_UPDATE );
						intentPlay.putExtra ( "playBtnTag" , 0 );
						localBroadcastManager2.sendBroadcast ( intentPlay );
						
						// 改变list的UI为播放
						Intent intentListPlay = new Intent ( MyConstant.ACTION_BROADCAST_LIST_BTN_UPDATE );
						intentListPlay.putExtra ( "listBtnTag" , 0 );
						localBroadcastManager2.sendBroadcast ( intentListPlay );
						
					} else {
						if ( ! playASong ( this.currSongPositionInList , this.currSongLoopProcess ) ) {
							break;
						}
						// 改变player的UI为暂停
						Intent intentPause = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_BTN_UPDATE );
						intentPause.putExtra ( "playBtnTag" , 1 );
						localBroadcastManager2.sendBroadcast ( intentPause );
						// 给player发送songid
						Intent intentPlayerText = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_COMPLETE_UPDATE );
						intentPlayerText.putExtra ( "songId" , this.currSongPositionInList );
						localBroadcastManager2.sendBroadcast ( intentPlayerText );
						
						// 改变list的UI为暂停
						Intent intentListPause = new Intent ( MyConstant.ACTION_BROADCAST_LIST_BTN_UPDATE );
						intentListPause.putExtra ( "listBtnTag" , 1 );
						localBroadcastManager2.sendBroadcast ( intentListPause );
						// 给list发送songid
						Intent intentListPlayerText = new Intent ( MyConstant.ACTION_BROADCAST_LIST_INFO_UPDATE );
						intentListPlayerText.putExtra ( "songId" , this.currSongPositionInList );
						localBroadcastManager2.sendBroadcast ( intentListPlayerText );
						
					}
				} else if ( playOperation == 1 ) {// 上一曲
					int songListPosition = generateLoopPrev ( this.currSongPositionInList , this.loopStatus );
					if ( songListPosition < 0 ) {
						songListPosition = 0;
					}
					if ( ! playASong ( songListPosition , 0 ) ) {
						break;
					}
					// 改变player的UI为暂停
					Intent intentPause = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_BTN_UPDATE );
					intentPause.putExtra ( "playBtnTag" , 1 );
					localBroadcastManager2.sendBroadcast ( intentPause );
					// 改变list的UI为暂停
					Intent intentListPause = new Intent ( MyConstant.ACTION_BROADCAST_LIST_BTN_UPDATE );
					intentListPause.putExtra ( "listBtnTag" , 1 );
					localBroadcastManager2.sendBroadcast ( intentListPause );
					// 给player发送songid
					Intent intentPlayerText = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_COMPLETE_UPDATE );
					intentPlayerText.putExtra ( "songId" , songListPosition );
					localBroadcastManager2.sendBroadcast ( intentPlayerText );
					// 给list发送songid
					Intent intentListPlayerText = new Intent ( MyConstant.ACTION_BROADCAST_LIST_INFO_UPDATE );
					intentListPlayerText.putExtra ( "songId" , this.currSongPositionInList );
					localBroadcastManager2.sendBroadcast ( intentListPlayerText );
				} else if ( playOperation == 2 ) {// 下一曲
					int songListPosition = generateLoopNext ( this.currSongPositionInList , this.loopStatus );
					if ( songListPosition > songs.size ( ) - 1 ) {
						songListPosition = songs.size ( ) - 1;
					}
					if ( ! playASong ( songListPosition , 0 ) ) {
						break;
					}
					// 改变player的UI为暂停
					Intent intentPause = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_BTN_UPDATE );
					intentPause.putExtra ( "playBtnTag" , 1 );
					localBroadcastManager2.sendBroadcast ( intentPause );
					// 改变list的UI为暂停
					Intent intentListPause = new Intent ( MyConstant.ACTION_BROADCAST_LIST_BTN_UPDATE );
					intentListPause.putExtra ( "listBtnTag" , 1 );
					localBroadcastManager2.sendBroadcast ( intentListPause );
					// 给list发送songid
					Intent intentListPlayerText = new Intent ( MyConstant.ACTION_BROADCAST_LIST_INFO_UPDATE );
					intentListPlayerText.putExtra ( "songId" , this.currSongPositionInList );
					localBroadcastManager2.sendBroadcast ( intentListPlayerText );
					// 给player发送songid
					Intent intentPlayerText = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_COMPLETE_UPDATE );
					intentPlayerText.putExtra ( "songId" , songListPosition );
					localBroadcastManager2.sendBroadcast ( intentPlayerText );
				}
				break;
			case 2 :
				// 根据listview的item点击而去播放
				int songListPositionTemp = intent.getIntExtra ( "songListPosition" , 0 );
				if ( songListPositionTemp == currSongPositionInList ) {
					break;
				}
				if ( ! playASong ( songListPositionTemp , 0 ) ) {
					break;
				}
				// 改变player的UI为暂停
				Intent intentPause = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_BTN_UPDATE );
				intentPause.putExtra ( "playBtnTag" , 1 );
				localBroadcastManager2.sendBroadcast ( intentPause );
				// 给player发送songid
				Intent intentPlayerText = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_COMPLETE_UPDATE );
				intentPlayerText.putExtra ( "songId" , currSongPositionInList );
				localBroadcastManager2.sendBroadcast ( intentPlayerText );
				// 改变list的UI为暂停
				Intent intentListPause = new Intent ( MyConstant.ACTION_BROADCAST_LIST_BTN_UPDATE );
				intentListPause.putExtra ( "listBtnTag" , 1 );
				localBroadcastManager2.sendBroadcast ( intentListPause );
				// 给list发送songid
				Intent intentListPlayerText = new Intent ( MyConstant.ACTION_BROADCAST_LIST_INFO_UPDATE );
				intentListPlayerText.putExtra ( "songId" , currSongPositionInList );
				localBroadcastManager2.sendBroadcast ( intentListPlayerText );
				break;
			default :
				break;
		}
		return super.onStartCommand ( intent , flags , startId );
	}
	
	private int generateLoopPrev ( int songListPosition , int loopStatus ) {
		switch ( loopStatus ) {
			case 0 :
				songListPosition -- ;
				break;
			
			case 1 :
				break;
			
			case 2 :
				songListPosition = generateRamdomNumber ( );
				break;
			
			default :
				break;
		}
		return songListPosition;
	}
	
	private int generateRamdomNumber ( ) {
		// 3级随机数生成,需要改进
		Random randomCache1 = new Random ( );
		int temp = randomCache1.nextInt ( songs.size ( ) );
		Random randomCache2 = new Random ( temp );
		temp = randomCache2.nextInt ( songs.size ( ) );
		Random randomCache3 = new Random ( temp );
		temp = randomCache3.nextInt ( songs.size ( ) );
		return temp;
	}
	
	private int generateLoopNext ( int songListPosition , int loopStatus ) {
		switch ( loopStatus ) {
			case 0 :
				songListPosition ++ ;
				break;
			
			case 1 :
				break;
			
			case 2 :
				songListPosition = generateRamdomNumber ( );
				break;
			
			default :
				break;
		}
		return songListPosition;
	}
	
	private boolean playASong ( int songListPosition , int surrSongLoopPosition ) {
		Song song = songs.get ( songListPosition );
		// 把各项参数恢复到初始状态
		mediaPlayer.reset ( );
		mediaPlayer.setAudioStreamType ( AudioManager.STREAM_MUSIC );
		try {
			mediaPlayer.setDataSource ( song.getURL ( ) );
			// 进行缓冲
			mediaPlayer.prepare ( );
		} catch ( Exception e ) {
			e.printStackTrace ( );
		}
		
		int result = audioManager.requestAudioFocus ( myOnAudioFocusChangeListener , AudioManager.STREAM_MUSIC , AudioManager.AUDIOFOCUS_GAIN );
		
		if ( result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED ) {
			this.isPlaying = true;
			this.currSongPositionInList = songListPosition;
			initLrc ( );
			mediaPlayer.setOnPreparedListener ( new PreparedListener ( surrSongLoopPosition ) );
			mediaPlayer.setOnCompletionListener ( new CompletionListener ( ) );
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 初始化歌词配置
	 */
	public void initLrc ( ) {
		mLyricProcessor = new LyricProcessor ( );
		// 读取歌词文件
		mLyricProcessor.readLRCFromSDPath ( songs.get ( currSongPositionInList ).getURL ( ) );
		// 传回处理后的歌词文件
		lrcList = mLyricProcessor.getLrcList ( );
		PlayerFramgment.playerLyricView.setmLrcList ( lrcList );
		// 切换带动画显示歌词
		PlayerFramgment.playerLyricView.setAnimation ( AnimationUtils.loadAnimation ( this , R.anim.reloard_lyric ) );
	}
	
	/**
	 * 根据时间获取歌词显示的索引值
	 */
	public int lrcIndex ( ) {
		int duration = ( int ) songs.get ( currSongPositionInList ).getDuration ( );
		if ( currSongLoopProcess < duration ) {
			for ( int i = 0 ; i < lrcList.size ( ) ; i ++ ) {
				if ( i < lrcList.size ( ) - 1 ) {
					if ( currSongLoopProcess < lrcList.get ( i ).getLrcTime ( ) && i == 0 ) {
						index = i;
					}
					if ( currSongLoopProcess > lrcList.get ( i ).getLrcTime ( ) && currSongLoopProcess < lrcList.get ( i + 1 ).getLrcTime ( ) ) {
						index = i;
					}
				}
				if ( i == lrcList.size ( ) - 1 && currSongLoopProcess > lrcList.get ( i ).getLrcTime ( ) ) {
					index = i;
				}
			}
		}
		return index;
	}
	
	private final class CompletionListener implements OnCompletionListener {
		
		@ Override
		public void onCompletion ( MediaPlayer mp ) {
			int songListPosition = generateLoopNext ( currSongPositionInList , loopStatus );
			if ( songListPosition > songs.size ( ) - 1 ) {
				songListPosition = songs.size ( ) - 1;
			}
			if ( ! playASong ( songListPosition , 0 ) ) {
				return;
			}
			// 改变player的UI为暂停
			Intent intentPause = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_BTN_UPDATE );
			intentPause.putExtra ( "playBtnTag" , 1 );
			localBroadcastManager2.sendBroadcast ( intentPause );
			// 给player发送songid
			Intent intentPlayerText = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_COMPLETE_UPDATE );
			intentPlayerText.putExtra ( "songId" , currSongPositionInList );
			localBroadcastManager2.sendBroadcast ( intentPlayerText );
			// 改变list的UI为暂停
			Intent intentListPause = new Intent ( MyConstant.ACTION_BROADCAST_LIST_BTN_UPDATE );
			intentListPause.putExtra ( "listBtnTag" , 1 );
			localBroadcastManager2.sendBroadcast ( intentListPause );
			// 给list发送songid
			Intent intentListPlayerText = new Intent ( MyConstant.ACTION_BROADCAST_LIST_INFO_UPDATE );
			intentListPlayerText.putExtra ( "songId" , currSongPositionInList );
			localBroadcastManager2.sendBroadcast ( intentListPlayerText );
		}
		
	}
	
	private final class PreparedListener implements OnPreparedListener {
		private int surrSongLoopPosition;
		
		public PreparedListener ( int surrSongLoopPosition ) {
			this.surrSongLoopPosition = surrSongLoopPosition;
		}
		
		@ Override
		public void onPrepared ( MediaPlayer mp ) {
			mediaPlayer.start ( );
			mediaPlayer.seekTo ( surrSongLoopPosition );
		}
	}
	
	public class PlayerStatuUpdateBroadCastReceiver extends BroadcastReceiver {
		
		@ Override
		public void onReceive ( Context context , Intent intent ) {
			if ( mediaPlayer == null ) {
				return;
			}
			int playerProcess = intent.getIntExtra ( "playerProcess" , 0 );
			int songListPosition = generateLoopNext ( MainPlayerService.this.currSongPositionInList , 1 );
			playASong ( songListPosition , playerProcess * mediaPlayer.getDuration ( ) / 100 );
			// 改变player的UI为暂停
			Intent intentPause = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_BTN_UPDATE );
			intentPause.putExtra ( "playBtnTag" , 1 );
			localBroadcastManager2.sendBroadcast ( intentPause );
			// 改变list的UI为暂停
			Intent intentListPause = new Intent ( MyConstant.ACTION_BROADCAST_LIST_BTN_UPDATE );
			intentListPause.putExtra ( "listBtnTag" , 1 );
			localBroadcastManager2.sendBroadcast ( intentListPause );
		}
	}
	
	public class UpdateListFragFirstViewBroadCastReceiver extends BroadcastReceiver {
		
		@ Override
		public void onReceive ( Context context , Intent intent ) {
			if ( intent.getIntExtra ( "isFromListFrag" , - 1 ) != 0 ) {
				return;
			}
			// 改变list的UI为播放
			Intent intentListPause = new Intent ( MyConstant.ACTION_BROADCAST_LIST_BTN_UPDATE );
			intentListPause.putExtra ( "listBtnTag" , ( ( mediaPlayer == null ) || ( ! mediaPlayer.isPlaying ( ) ) ? 0 : 1 ) );
			localBroadcastManager2.sendBroadcast ( intentListPause );
			// 给list发送songid
			Intent intentListPlayerText = new Intent ( MyConstant.ACTION_BROADCAST_LIST_INFO_UPDATE );
			intentListPlayerText.putExtra ( "songId" , currSongPositionInList );
			intentListPlayerText.putExtra ( "isStartFirstTime" , 0 );
			localBroadcastManager2.sendBroadcast ( intentListPlayerText );
		}
	}
	
	public class UpdatePlayerFragRebackBroadCastReceiver extends BroadcastReceiver {
		
		@ Override
		public void onReceive ( Context context , Intent intent ) {
			if ( intent.getIntExtra ( "isFromPlayerFrag" , - 1 ) != 0 ) {
				return;
			}
			// 改变player的UI为现在播放情况
			Intent intentPause = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_BTN_UPDATE );
			intentPause.putExtra ( "playBtnTag" , ( ( mediaPlayer == null ) || ( ! mediaPlayer.isPlaying ( ) ) ? 0 : 1 ) );
			localBroadcastManager2.sendBroadcast ( intentPause );
			// 给player发送songid
			Intent intentPlayerText = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_COMPLETE_UPDATE );
			intentPlayerText.putExtra ( "songId" , currSongPositionInList );
			localBroadcastManager2.sendBroadcast ( intentPlayerText );
			// 把loopstatus发送给player
			Intent intentLoopStatus = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_LOOPLOGO_UPDATE );
			intentLoopStatus.putExtra ( "loopLogo" , loopStatus );
			localBroadcastManager2.sendBroadcast ( intentLoopStatus );
			// 把实时播放进度发送给player
			Intent intentSeekBar = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_SEEKBAR_UPDATE );
			int positionProcess = 0;
			int duration = 0;
			positionProcess = currSongLoopProcess;
			duration = ( int ) songs.get ( currSongPositionInList ).getDuration ( );
			intentSeekBar.putExtra ( "positionProcess" , positionProcess );
			intentSeekBar.putExtra ( "duration" , duration );
			localBroadcastManager2.sendBroadcast ( intentSeekBar );
			initLrc ( );
		}
	}
	
	private class MyOnAudioFocusChangeListener implements OnAudioFocusChangeListener {
		@ Override
		public void onAudioFocusChange ( int focusChange ) {
			if ( MainPlayerService.mediaPlayer.isPlaying ( ) ) {
				Intent intent1 = new Intent ( MainPlayerService.this , com.example.service.MainPlayerService.class );
				intent1.putExtra ( "type" , 1 );
				intent1.putExtra ( "playOperation" , 0 );
				startService ( intent1 );
			}
		}
	}
}
