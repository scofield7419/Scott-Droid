package com.example.service;

import java.io.ByteArrayInputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.animation.AnimationUtils;

import com.example.activity.R;
import com.example.fragment.RapperFramgment;
import com.example.lyric.Lyric;
import com.example.lyric.LyricProcessor;
import com.example.utils.MyConstant;

public class RapperService extends Service {
	LocalBroadcastManager localBroadcastManager4;
	RapperStatuUpdateBroadCastReceiver rapperStatuUpdateBroadCastReceiver;
	UpdateRapperFragRebackBroadCastReceiver updateRapperFragRebackBroadCastReceiver;
	
	HeadsetPlugReceiver headsetPlugReceiver;
	
	private AudioManager audioManager;
	
	private MyOnAudioFocusChangeListener myOnAudioFocusChangeListener;
	
	private LyricProcessor mLyricProcessor;
	private List < Lyric > lrcList = new ArrayList < Lyric > ( );
	// 歌词检索值
	private int index = 0;
	
	public static MediaPlayer mediaPlayer;
	
	private static final int ID_LUDA = 1;
	private static final int ID_JERRY = 0;
	
	FileDescriptor [ ] rapperDescriptors = new FileDescriptor [ 2 ];
	InputStream [ ] rapperLyricInputStream = new InputStream [ 2 ];
	AssetFileDescriptor [ ] rapperAssetFileDescriptors = new AssetFileDescriptor [ 2 ];
	String lrcStr[] = new String [ 2 ];
	
	public int currRapperID;
	public int [ ] currRapperProcess = new int [ ] { 0 , 0 };
	public int [ ] currRapperDuration = new int [ ] { 30000 , 30000 };
	public int currLoopStatus;
	
	private boolean isServiceAlive;
	
	private boolean isFinshACall;
	
	// 所有涉及到更改播放状态的地方都要更改此标签
	public boolean isPlaying;
	
	private Thread updatePlayerThread;
	
	public RapperService ( ) {
		super ( );
	}
	
	@ Override
	public IBinder onBind ( Intent intent ) {
		return null;
	}
	
	@ Override
	public void onCreate ( ) {
		super.onCreate ( );
		loadRapperSong ( );
		loadRapperLyric ( );
		mediaPlayer = new MediaPlayer ( );
		initBroadcastReceiver ( );
//		initLrc ( );
		isServiceAlive = true;
		isFinshACall = false;
		initPlayerSeekBarThread ( );
		initPhoneListener ( );
		initSystemAudioListener ( );
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
			switch ( state ) {
				case TelephonyManager.CALL_STATE_IDLE : // 挂机状态
					if ( ( ! RapperService.mediaPlayer.isPlaying ( ) ) && isFinshACall ) {
						Intent intent = new Intent ( RapperService.this , com.example.service.RapperService.class );
						intent.putExtra ( "type" , 1 );
						startService ( intent );
//						System.out.println ("挂机状态is coming");
						isFinshACall = false;
					}
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK : // 通话状态
				case TelephonyManager.CALL_STATE_RINGING : // 响铃状态
					if ( RapperService.mediaPlayer.isPlaying ( ) ) {
						Intent intent2 = new Intent ( RapperService.this , com.example.service.RapperService.class );
						intent2.putExtra ( "type" , 1 );
						startService ( intent2 );
						isFinshACall = true;
					}
					
					break;
				default :
					break;
			}
		}
	}
	
	private void loadRapperLyric ( ) {
		try {
			int size = 0;
			//将歌词文件读取出来作为字符串保留，避免了每次重复读取的失败！
			this.rapperLyricInputStream [ ID_JERRY ] = getAssets ( ).open ( "jerry_lyric.lrc" );//jerry_lyric
			size = this.rapperLyricInputStream [ ID_JERRY ].available ( );
			byte [ ] bufferJerry = new byte [ size ];
			this.rapperLyricInputStream [ ID_JERRY ].read ( bufferJerry );
			this.rapperLyricInputStream [ ID_JERRY ].close ( );
			lrcStr [ ID_JERRY ] = new String ( bufferJerry , "UTF-8" );
			
			this.rapperLyricInputStream [ ID_LUDA ] = getAssets ( ).open ( "luda_lyric.lrc" );
			size = this.rapperLyricInputStream [ ID_LUDA ].available ( );
			byte [ ] bufferLuda = new byte [ size ];
			this.rapperLyricInputStream [ ID_LUDA ].read ( bufferLuda );
			this.rapperLyricInputStream [ ID_LUDA ].close ( );
			lrcStr [ ID_LUDA ] = new String ( bufferLuda , "UTF-8" );
			
		} catch ( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace ( );
//			System.out.println ( "loadRapperLyric? false" );
		}
	}
	
	private void loadRapperSong ( ) {
		try {
			this.rapperAssetFileDescriptors [ ID_JERRY ] = getAssets ( ).openFd ( "jerry_rap.mp3" );
			this.rapperDescriptors [ ID_JERRY ] = this.rapperAssetFileDescriptors [ ID_JERRY ].getFileDescriptor ( );
			this.rapperAssetFileDescriptors [ ID_LUDA ] = getAssets ( ).openFd ( "luda_rap.mp3" );
			this.rapperDescriptors [ ID_LUDA ] = this.rapperAssetFileDescriptors [ ID_LUDA ].getFileDescriptor ( );
		} catch ( IOException e ) {
			e.printStackTrace ( );
			//			DebugUtil.toa ( RapperService.this , "loadRapperSong? false"  );
		}
		
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
					// 把looplogo传给rapper
					Intent intentLoopLogo = new Intent ( MyConstant.ACTION_BROADCAST_RAPPER_LOOPLOGO_UPDATE );
					intentLoopLogo.putExtra ( "loopLogo" , currLoopStatus );
					localBroadcastManager4.sendBroadcast ( intentLoopLogo );
					if ( ( mediaPlayer != null ) && ( mediaPlayer.isPlaying ( ) ) ) {
						// 把实时播放进度发送给rapper
						Intent intentSeekBar = new Intent ( MyConstant.ACTION_BROADCAST_RAPPER_SEEKBAR_UPDATE );
						int positionProcess = mediaPlayer.getCurrentPosition ( );
						int duration = mediaPlayer.getDuration ( );
						currRapperProcess [ currRapperID ] = positionProcess;
						currRapperDuration [ currRapperID ] = duration;
						intentSeekBar.putExtra ( "positionProcess" , positionProcess );
						intentSeekBar.putExtra ( "duration" , duration );
						localBroadcastManager4.sendBroadcast ( intentSeekBar );
						// 实时传送歌词的同步信息
						if ( currRapperID == ID_LUDA ) {
							RapperFramgment.playerLudaLyricView.setIndex ( lrcIndex ( ) );
						} else if ( currRapperID == ID_JERRY ) {
							RapperFramgment.playerJerryLyricView.setIndex ( lrcIndex ( ) );
						}
//						System.out.println ( "run update seekbar? true" );
					}
//					System.out.println ( "run update mediaPlayer == null ? " + ( mediaPlayer == null ) );
				}
			}
		} );
		updatePlayerThread.start ( );
//		System.out.println ( "run == null ? " + ( updatePlayerThread.isAlive ( ) ) );
	}
	
	@ Override
	public void onDestroy ( ) {
		isServiceAlive = false;
		if ( mediaPlayer != null ) {
			mediaPlayer.stop ( );
			mediaPlayer.release ( );
			mediaPlayer = null;
		}
		unregisterReceiver ( headsetPlugReceiver );
		audioManager.abandonAudioFocus ( myOnAudioFocusChangeListener );
	}
	
	private void initBroadcastReceiver ( ) {
		localBroadcastManager4 = LocalBroadcastManager.getInstance ( this );
		rapperStatuUpdateBroadCastReceiver = new RapperStatuUpdateBroadCastReceiver ( );
		IntentFilter filter1 = new IntentFilter ( );
		filter1.addAction ( MyConstant.ACTION_BROADCAST_RAPPERSERVICE_PLAYER_STATU_UPDATE );
		localBroadcastManager4.registerReceiver ( rapperStatuUpdateBroadCastReceiver , filter1 );
		
		updateRapperFragRebackBroadCastReceiver = new UpdateRapperFragRebackBroadCastReceiver ( );
		IntentFilter filter2 = new IntentFilter ( );
		filter2.addAction ( MyConstant.ACTION_BROADCAST_RAPPERSERVICE_RAPPERFRAG_REBACK_UPDATE );
		localBroadcastManager4.registerReceiver ( updateRapperFragRebackBroadCastReceiver , filter2 );
		
		//监听系统耳机插入拔出情况,此BR是全局的，需要在destroy时注销注册
		headsetPlugReceiver = new HeadsetPlugReceiver ( );
		IntentFilter filter3 = new IntentFilter ( );
		filter3.addAction ( AudioManager.ACTION_AUDIO_BECOMING_NOISY );
		registerReceiver ( headsetPlugReceiver , filter3 );
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
				if ( RapperService.mediaPlayer.isPlaying ( ) ) {
					Intent intent2 = new Intent ( RapperService.this , com.example.service.RapperService.class );
					intent2.putExtra ( "type" , 1 );
					startService ( intent2 );
				}
			}
		}
		
	}
	
	@ Override
	public int onStartCommand ( Intent intent , int flags , int startId ) {
		//		DebugUtil.toa ( RapperService.this , "onStartCommand is start sevice? " );
		// 第一次在activity中启动service时，必须让type不为这里的三种值，否则总是会有错误!
		if ( intent == null ) {
			return super.onStartCommand ( intent , flags , startId );
		}
		int type = intent.getIntExtra ( "type" , 0 );
		switch ( type ) {
			case 0 :
				int loopOperation = intent.getIntExtra ( "loopOperation" , 0 );
				this.currLoopStatus = loopOperation;
				break;
			case 1 :
				if ( mediaPlayer.isPlaying ( ) ) {
					// 对currSongLoopProcess进行赋值只有三处：初试从sharedpref获得，播放时实时从run中获得，暂停前赋值
					this.currRapperProcess [ currRapperID ] = mediaPlayer.getCurrentPosition ( );
					mediaPlayer.pause ( );
					this.isPlaying = false;
					
					// 改变player的UI为播放
					Intent intentPlay = new Intent ( MyConstant.ACTION_BROADCAST_RAPPER_BTN_UPDATE );
					intentPlay.putExtra ( "playBtnTag" , 0 );
					localBroadcastManager4.sendBroadcast ( intentPlay );
//					sendStopCDMovementBR();
				} else {
					if(playASong ( this.currRapperID , this.currRapperProcess [ currRapperID ] )){
						// 改变player的UI为暂停
						Intent intentPause = new Intent ( MyConstant.ACTION_BROADCAST_RAPPER_BTN_UPDATE );
						intentPause.putExtra ( "playBtnTag" , 1 );
						localBroadcastManager4.sendBroadcast ( intentPause );
						// 给player发送songid
						Intent intentPlayerText = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_COMPLETE_UPDATE );
						intentPlayerText.putExtra ( "currRapperID" , this.currRapperID );
						localBroadcastManager4.sendBroadcast ( intentPlayerText );
						
					}
					
				}
				break;
			case 2 :
				if ( mediaPlayer.isPlaying ( ) ) {
					// 对currSongLoopProcess进行赋值只有2处：播放时实时从run中获得，暂停前赋值
					this.currRapperProcess [ currRapperID ] = mediaPlayer.getCurrentPosition ( );
					mediaPlayer.pause ( );
					this.isPlaying = false;
					
					// 改变player的UI为播放
					Intent intentPlay = new Intent ( MyConstant.ACTION_BROADCAST_RAPPER_BTN_UPDATE );
					intentPlay.putExtra ( "playBtnTag" , 0 );
					localBroadcastManager4.sendBroadcast ( intentPlay );
				}
				currRapperID = currRapperID == 1 ? 0 : 1;
				break;
			default :
				break;
		}
		return super.onStartCommand ( intent , flags , startId );
	}
	
	private boolean playASong ( int rapperID , int surrRapperLoopPosition ) {
		
		// 把各项参数恢复到初始状态
		mediaPlayer.reset ( );
		mediaPlayer.setAudioStreamType ( AudioManager.STREAM_MUSIC );
		try {
			mediaPlayer.setDataSource ( rapperDescriptors [ rapperID ] , rapperAssetFileDescriptors [ rapperID ].getStartOffset ( ) , rapperAssetFileDescriptors [ rapperID ].getLength ( ) );
			// 进行缓冲
			mediaPlayer.prepare ( );
		} catch ( Exception e ) {
			e.printStackTrace ( );
		}
		
		int result = audioManager.requestAudioFocus ( myOnAudioFocusChangeListener , AudioManager.STREAM_MUSIC , AudioManager.AUDIOFOCUS_GAIN );
		
		if ( result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED ) {
			this.isPlaying = true;
			this.currRapperID = rapperID;
			initLrc ( );
			mediaPlayer.setOnPreparedListener ( new PreparedListener ( surrRapperLoopPosition ) );
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
//		loadRapperSong ( );
		mLyricProcessor = new LyricProcessor ( );
		// 读取歌词文件
		ByteArrayInputStream in = new ByteArrayInputStream ( lrcStr [ currRapperID ].getBytes ( ) );
		mLyricProcessor.readLRCFromLrcInputStream ( in );
//		System.out.println (lrcStr [ currRapperID ]);
		// 传回处理后的歌词文件
		lrcList = mLyricProcessor.getLrcList ( );
		if ( currRapperID == ID_LUDA ) {
			RapperFramgment.playerLudaLyricView.setmLrcList ( lrcList );
//			 切换带动画显示歌词
			RapperFramgment.playerLudaLyricView.setAnimation ( AnimationUtils.loadAnimation ( this , R.anim.reloard_lyric ) );
		} else if ( currRapperID == ID_JERRY ) {
//			System.out.println ("is playerJerryLyricView null ? " + (RapperFramgment.playerJerryLyricView == null));
			RapperFramgment.playerJerryLyricView.setmLrcList ( lrcList );
			// 切换带动画显示歌词
			RapperFramgment.playerJerryLyricView.setAnimation ( AnimationUtils.loadAnimation ( this , R.anim.reloard_lyric ) );
		}
	}
	
	/**
	 * 根据时间获取歌词显示的索引值
	 */
	public int lrcIndex ( ) {
		int duration = currRapperDuration [ currRapperID ];
		if ( currRapperProcess [ currRapperID ] < duration ) {
			for ( int i = 0 ; i < lrcList.size ( ) ; i ++ ) {
				if ( i < lrcList.size ( ) - 1 ) {
					if ( currRapperProcess [ currRapperID ] < lrcList.get ( i ).getLrcTime ( ) && i == 0 ) {
						index = i;
					}
					if ( currRapperProcess [ currRapperID ] > lrcList.get ( i ).getLrcTime ( ) && currRapperProcess [ currRapperID ] < lrcList.get ( i + 1 ).getLrcTime ( ) ) {
						index = i;
					}
				}
				if ( i == lrcList.size ( ) - 1 && currRapperProcess [ currRapperID ] > lrcList.get ( i ).getLrcTime ( ) ) {
					index = i;
				}
			}
		}
		return index;
	}
	
	private final class CompletionListener implements OnCompletionListener {
		
		@ Override
		public void onCompletion ( MediaPlayer mp ) {
			int currIDTemp = generateLoopNext ( currRapperID , currLoopStatus );
			if ( currIDTemp == - 1 ) {
				// 改变player的UI为播放
				Intent intentPause = new Intent ( MyConstant.ACTION_BROADCAST_RAPPER_BTN_UPDATE );
				intentPause.putExtra ( "playBtnTag" , 0 );
				localBroadcastManager4.sendBroadcast ( intentPause );
				
				// 把实时播放进度发送给rapper
				Intent intentSeekBar = new Intent ( MyConstant.ACTION_BROADCAST_RAPPER_SEEKBAR_UPDATE );
				currRapperProcess [ currRapperID ] = 0;
				intentSeekBar.putExtra ( "positionProcess" , 0 );
				intentSeekBar.putExtra ( "duration" , 30000 );
				localBroadcastManager4.sendBroadcast ( intentSeekBar );
				
//				sendStopCDMovementBR();
				initLrc ( );
				return;
			}
			if(playASong ( currIDTemp , 0 )){
				
				// 改变player的UI为暂停
				Intent intentPause = new Intent ( MyConstant.ACTION_BROADCAST_RAPPER_BTN_UPDATE );
				intentPause.putExtra ( "playBtnTag" , 1 );
				localBroadcastManager4.sendBroadcast ( intentPause );
				// 给player发送songid
				Intent intentPlayerText = new Intent ( MyConstant.ACTION_BROADCAST_PLAYER_COMPLETE_UPDATE );
				intentPlayerText.putExtra ( "currRapperID" , currRapperID );
				localBroadcastManager4.sendBroadcast ( intentPlayerText );
			}
		}
		
	}
	
	private int generateLoopNext ( int songListPosition , int loopStatus ) {
		if ( loopStatus == 0 ) {
			return songListPosition;
		} else {
			return - 1;
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
//			DebugUtil.toa ( RapperService.this , "is playing? " + mediaPlayer.isPlaying ( ) );
		}
	}
	
	public class RapperStatuUpdateBroadCastReceiver extends BroadcastReceiver {
		
		@ Override
		public void onReceive ( Context context , Intent intent ) {
			if ( mediaPlayer == null ) {
				return;
			}
			int playerProcess = intent.getIntExtra ( "playerProcess" , 0 );
			playASong ( currRapperID , playerProcess * mediaPlayer.getDuration ( ) / 100 );
			// 改变rapper的UI为暂停
			Intent intentPause = new Intent ( MyConstant.ACTION_BROADCAST_RAPPER_BTN_UPDATE );
			intentPause.putExtra ( "playBtnTag" , 1 );
			localBroadcastManager4.sendBroadcast ( intentPause );
		}
	}
	
	public class UpdateRapperFragRebackBroadCastReceiver extends BroadcastReceiver {
		
		@ Override
		public void onReceive ( Context context , Intent intent ) {
			if ( intent.getIntExtra ( "isFromRapperFrag" , - 1 ) != 0 ) {
				return;
			}
			// 改变rapper的UI为现在的播放情况
			Intent intentPause = new Intent ( MyConstant.ACTION_BROADCAST_RAPPER_BTN_UPDATE );
			intentPause.putExtra ( "playBtnTag" , ( ( mediaPlayer == null ) || ( ! mediaPlayer.isPlaying ( ) ) ? 0 : 1 ) );
			localBroadcastManager4.sendBroadcast ( intentPause );
			// 给rapper发送currRapperID
			Intent intentPlayerText = new Intent ( MyConstant.ACTION_BROADCAST_RAPPER_COMPLETE_UPDATE );
			intentPlayerText.putExtra ( "currRapperID" , currRapperID );
			localBroadcastManager4.sendBroadcast ( intentPlayerText );
			// 把loopstatus发送给rapper
			Intent intentLoopStatus = new Intent ( MyConstant.ACTION_BROADCAST_RAPPER_LOOPLOGO_UPDATE );
			intentLoopStatus.putExtra ( "loopLogo" , currLoopStatus );
			localBroadcastManager4.sendBroadcast ( intentLoopStatus );
			// 把实时播放进度发送给rapper
			Intent intentSeekBar = new Intent ( MyConstant.ACTION_BROADCAST_RAPPER_SEEKBAR_UPDATE );
			int positionProcess = 0;
			int duration = 0;
			positionProcess = currRapperProcess [ currRapperID ];
			duration = currRapperDuration [ currRapperID ];
			intentSeekBar.putExtra ( "positionProcess" , positionProcess );
			intentSeekBar.putExtra ( "duration" , duration );
			localBroadcastManager4.sendBroadcast ( intentSeekBar );
			initLrc ( );
		}
	}
	
	private class MyOnAudioFocusChangeListener implements OnAudioFocusChangeListener {
		@ Override
		public void onAudioFocusChange ( int focusChange ) {
			if ( RapperService.mediaPlayer.isPlaying ( ) ) {
				Intent intent2 = new Intent ( RapperService.this , com.example.service.RapperService.class );
				intent2.putExtra ( "type" , 1 );
				startService ( intent2 );
			}
		}
	}
}
