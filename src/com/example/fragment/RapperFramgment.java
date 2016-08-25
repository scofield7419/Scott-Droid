package com.example.fragment;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.activity.R;
import com.example.fragment.PlayerFramgment.PlayerTabCallBacker;
import com.example.service.RapperService;
import com.example.utils.DebugUtil;
import com.example.utils.MyConstant;
import com.example.view.MyLyricView;
import com.example.view.MyPagerView;
import com.example.view.MyPagerView.OnScrollToScreenListener;

public class RapperFramgment extends Fragment implements OnClickListener , OnSeekBarChangeListener , OnScrollToScreenListener {
	Context context;
	
	View mainRapper;
	
	LocalBroadcastManager localBroadcastManager3;
	RapperViewSeekBarUpdateBroadCastReceiver rapperViewSeekBarUpdateBroadCastReceiver;
	RapperCompleteUpdateBroadCastReceiver rapperCompleteUpdateBroadCastReceiver;
	RapperBtnUpdateBroadCastReceiver rapperBtnUpdateBroadCastReceiver;
	RapperViewLoopLogoUpdateBroadCastReceiver rapperViewLoopLogoUpdateBroadCastReceiver;
	
	ImageButton rapperLoopButton;
	ImageView [ ] rappperCDImageView = new ImageView [ 2 ];
	Button rapperPlayButton;
	SeekBar rapperProcess;
	TextView rapperMusicProcessCurTime;
	TextView rapperMusicProcessDuration;
	
	MyPagerView rapperPagerView;
	public static MyLyricView playerJerryLyricView;
	public static MyLyricView playerLudaLyricView;
	
	private int [ ] rapperLoopLogoIds = new int [ ] {
	                R.drawable.loop_single_btn , R.drawable.once_btn };
	private int [ ] rapperPlayBtnIds = new int [ ] { R.drawable.play_btn ,
	                R.drawable.pause_btn };
	
	private int [ ][ ] CDIDs = new int [ ] [ ] {
	                { R.drawable.jerry_cd1 , R.drawable.jerry_cd2 } ,
	                { R.drawable.luda_cd1 , R.drawable.luda_cd2 } };
	
	private static final int ID_LUDA = 1;
	private static final int ID_JERRY = 0;
	
	// 第一个是播放按钮记录，第二个是loop状态记忆，第三个是cursongID。
	private int [ ] memoryTempView = new int [ 3 ];
	
	private int threeLoopTag;
	
	private boolean isCDMove = false;
	
	private boolean isRapperFragAlive = false;
	
	private int tempCDIndex = 0;
	
	public RapperFramgment ( Context context ) {
		this.context = context;
	}
	
	@ Override
	public void onAttach ( Activity activity ) {
		super.onAttach ( activity );
		initBroadcastReceivers ( );
	}
	
	@ Override
	public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate ( savedInstanceState );
		isRapperFragAlive = true;
	}
	
	@ Override
	public void onStart ( ) {
		// 每次当fragment创建并显示时，让rapperservice 来更新frag的ui为当前播放状态
		informSreviceUpdateMe ( );
		startCDMoveThread ( );
		super.onStart ( );
	}
	
	@ Override
	public void onResume ( ) {
		super.onResume ( );
	}
	
	@ Override
	public void onDestroy ( ) {
		super.onDestroy ( );
		isRapperFragAlive = false;
	}
	
	private void informSreviceUpdateMe ( ) {
		Intent intentUpdateMe = new Intent ( MyConstant.ACTION_BROADCAST_RAPPERSERVICE_RAPPERFRAG_REBACK_UPDATE );
		intentUpdateMe.putExtra ( "isFromRapperFrag" , 0 );
		localBroadcastManager3.sendBroadcast ( intentUpdateMe );
	}
	
	private void initBroadcastReceivers ( ) {
		localBroadcastManager3 = LocalBroadcastManager.getInstance ( context );
		rapperViewSeekBarUpdateBroadCastReceiver = new RapperViewSeekBarUpdateBroadCastReceiver ( );
		IntentFilter filter1 = new IntentFilter ( );
		filter1.addAction ( MyConstant.ACTION_BROADCAST_RAPPER_SEEKBAR_UPDATE );
		localBroadcastManager3.registerReceiver ( rapperViewSeekBarUpdateBroadCastReceiver , filter1 );
		
		rapperCompleteUpdateBroadCastReceiver = new RapperCompleteUpdateBroadCastReceiver ( );
		IntentFilter filter2 = new IntentFilter ( );
		filter2.addAction ( MyConstant.ACTION_BROADCAST_RAPPER_COMPLETE_UPDATE );
		localBroadcastManager3.registerReceiver ( rapperCompleteUpdateBroadCastReceiver , filter2 );
		
		rapperBtnUpdateBroadCastReceiver = new RapperBtnUpdateBroadCastReceiver ( );
		IntentFilter filter3 = new IntentFilter ( );
		filter3.addAction ( MyConstant.ACTION_BROADCAST_RAPPER_BTN_UPDATE );
		localBroadcastManager3.registerReceiver ( rapperBtnUpdateBroadCastReceiver , filter3 );
		
		rapperViewLoopLogoUpdateBroadCastReceiver = new RapperViewLoopLogoUpdateBroadCastReceiver ( );
		IntentFilter filter4 = new IntentFilter ( );
		filter4.addAction ( MyConstant.ACTION_BROADCAST_RAPPER_LOOPLOGO_UPDATE );
		localBroadcastManager3.registerReceiver ( rapperViewLoopLogoUpdateBroadCastReceiver , filter4 );
		
	}
	
	@ Override
	public View onCreateView ( LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState ) {
		mainRapper = inflater.inflate ( R.layout.rapper_frag , container , false );
		initView ( mainRapper );
		return mainRapper;
	}
	
	private void initView ( View rapper ) {
		rapperLoopButton = ( ImageButton ) rapper.findViewById ( R.id.rapper_loop_btn );
		rapperLoopButton.setOnClickListener ( this );
		
		rapperPlayButton = ( Button ) rapper.findViewById ( R.id.rapper_btn_play );
		rapperPlayButton.setOnClickListener ( this );
		
		rapperProcess = ( SeekBar ) rapper.findViewById ( R.id.rapper_music_process );
		rapperProcess.setOnSeekBarChangeListener ( this );
		
		rappperCDImageView [ ID_JERRY ] = ( ImageView ) rapper.findViewById ( R.id.rappper_jerry_cd );
		rappperCDImageView [ ID_LUDA ] = ( ImageView ) rapper.findViewById ( R.id.rappper_luda_cd );
		
		rapperMusicProcessCurTime = ( TextView ) rapper.findViewById ( R.id.rapper_music_process_currtime );
		rapperMusicProcessDuration = ( TextView ) rapper.findViewById ( R.id.rapper_music_process_duration );
		
		playerJerryLyricView = ( MyLyricView ) rapper.findViewById ( R.id.rapper_lyric_jerry );
		playerLudaLyricView = ( MyLyricView ) rapper.findViewById ( R.id.rapper_lyric_luda );
		
		rapperPagerView = ( MyPagerView ) rapper.findViewById ( R.id.rapper_lyric_pager );
		// playerPagerView.setOnChildCustomTouchListener ( this );
		rapperPagerView.setOnScrollToScreenListener ( this );
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
		Intent intent = new Intent ( MyConstant.ACTION_BROADCAST_RAPPERSERVICE_PLAYER_STATU_UPDATE );
		intent.putExtra ( "playerProcess" , playerProcess );
		localBroadcastManager3.sendBroadcast ( intent );
	}
	
	private String setDuration2String ( int duration ) {
		duration /= 1000;
		int second = duration % 60;
		int minute = duration / 60;
		return String.format ( "%02d:%02d" , minute , second );
	}
	
	public class RapperViewSeekBarUpdateBroadCastReceiver extends BroadcastReceiver {
		@ Override
		public void onReceive ( Context context , Intent intent ) {
			
			int positionProcess = intent.getIntExtra ( "positionProcess" , 0 );
			int duration = intent.getIntExtra ( "duration" , 0 );
			int curPosition = positionProcess * 100 / duration;
			rapperProcess.setProgress ( curPosition );
			rapperMusicProcessCurTime.setText ( setDuration2String ( positionProcess ) );
			rapperMusicProcessDuration.setText ( setDuration2String ( duration ) );
			
//			 更新播放进度的同时更新歌词滚动
			if ( memoryTempView [ 2 ] == ID_LUDA ) {
				playerLudaLyricView.invalidate ( );
			} else if ( memoryTempView [ 2 ] == ID_JERRY ) {
				playerJerryLyricView.invalidate ( );
			}
		}
	}
	
	public class RapperViewLoopLogoUpdateBroadCastReceiver extends BroadcastReceiver {
		@ Override
		public void onReceive ( Context context , Intent intent ) {
			if ( ! RapperFramgment.this.isAdded ( ) ) {
				return;
			}
			int loopLogo = intent.getIntExtra ( "loopLogo" , 0 );
			rapperLoopButton.setImageDrawable ( getResources ( ).getDrawable ( rapperLoopLogoIds [ loopLogo ] ) );
			threeLoopTag = loopLogo;
			memoryTempView [ 1 ] = loopLogo;
		}
	}
	
	public class RapperBtnUpdateBroadCastReceiver extends BroadcastReceiver {
		
		@ Override
		public void onReceive ( Context context , Intent intent ) {
			if ( ! RapperFramgment.this.isAdded ( ) ) {
				return;
			}
			int btnTag = intent.getIntExtra ( "playBtnTag" , 0 );
			rapperPlayButton.setBackground ( getResources ( ).getDrawable ( rapperPlayBtnIds [ btnTag ] ) );
			memoryTempView [ 0 ] = btnTag;
		}
	}
	
	private void startCDMoveThread ( ) {
		// TODO Auto-generated method stub
		new Thread ( new Runnable ( ) {
			@ Override
			public void run ( ) {
				while ( isRapperFragAlive ) {
					if ( RapperService.mediaPlayer.isPlaying ( ) ) {
						startSpinChange ( );
					} else {
						stopSpinChange ( );
					}
				}
			}
		} ).start ( );
	}
	
	private void stopSpinChange ( ) {
		if ( ! isCDMove ) {
			return;
		}
		spinCDHandler.sendEmptyMessage ( 0x125 );
		isCDMove = false;
	}
	
	private void startSpinChange ( ) {
		isCDMove = true;
		//顺时针旋转两周
		spinCDHandler.sendEmptyMessage ( 0x124 );
		if ( ! RapperService.mediaPlayer.isPlaying ( ) ) {
			return;
		}
		//等顺时针旋转两周后开始逆时针旋转
		try {
			Thread.sleep ( 7990 );
		} catch ( InterruptedException e ) {
			e.printStackTrace ( );
		}
		if ( ! RapperService.mediaPlayer.isPlaying ( ) ) {
			return;
		}
		new Timer ( ).schedule ( new TimerTask ( ) {
			public void run ( ) {
				spinCDHandler.sendEmptyMessage ( 0x123 );
			}
		} , 0 );
		if ( ! RapperService.mediaPlayer.isPlaying ( ) ) {
			return;
		}
		//等逆时针旋转两周后再进行下一轮的旋转判断
		try {
			Thread.sleep ( 7970 );
		} catch ( InterruptedException e ) {
			e.printStackTrace ( );
		}
	}
	
	Animation spinCDEastern;
	Animation spinCDClockwise;
	
	Handler spinCDHandler = new Handler ( ) {
		public void handleMessage ( Message msg ) {
			if ( msg.what == 0x123 ) {//逆时针旋转两周
				rappperCDImageView [ memoryTempView [ 2 ] ].setImageResource ( CDIDs [ memoryTempView [ 2 ] ] [ ( tempCDIndex ++ ) % CDIDs.length ] );
				spinCDEastern = AnimationUtils.loadAnimation ( context , R.anim.spin_cd_eastern );
				spinCDEastern.setFillAfter ( false );
				rappperCDImageView [ memoryTempView [ 2 ] ].startAnimation ( spinCDEastern );
//				DebugUtil.toa ( context , "0x123 逆时针旋转 : tempCDIndex :  " + ( ( tempCDIndex ) % CDIDs.length ) );
			} else if ( msg.what == 0x124 ) {//顺时针旋转两周
				rappperCDImageView [ memoryTempView [ 2 ] ].setImageResource ( CDIDs [ memoryTempView [ 2 ] ] [ ( tempCDIndex ++ ) % CDIDs.length ] );
				spinCDClockwise = AnimationUtils.loadAnimation ( context , R.anim.spin_cd_clockwise );
				spinCDClockwise.setFillAfter ( false );
				rappperCDImageView [ memoryTempView [ 2 ] ].startAnimation ( spinCDClockwise );
//				DebugUtil.toa ( context , "0x124 顺时针旋转 : tempCDIndex :  " + ( ( tempCDIndex ) % CDIDs.length ) );
			} else if ( msg.what == 0x125 ) {
				rappperCDImageView [ memoryTempView [ 2 ] ].clearAnimation ( );
				rappperCDImageView [ memoryTempView [ 2 ] ].setImageResource ( CDIDs [ memoryTempView [ 2 ] ] [ ( tempCDIndex - 1 ) % CDIDs.length ] );
			}
		}
	};
	
	/**
	 * 本广播接收器只有在rapperfrag每此初始加载时才会通知此广播来更新currRapperID，
	 * 并进行rapperPagerView的选择。
	 * 
	 * @author scott
	 * 
	 */
	public class RapperCompleteUpdateBroadCastReceiver extends BroadcastReceiver {
		
		@ Override
		public void onReceive ( Context context , Intent intent ) {
			if ( ! RapperFramgment.this.isAdded ( ) ) {
				return;
			}
			memoryTempView [ 2 ] = intent.getIntExtra ( "currRapperID" , 0 );
			//只有在初始加载时才会通知此广播来更新currRapperID，并进行rapperPagerView的选择。
			rapperPagerView.setToScreen ( memoryTempView [ 2 ] , false );
		}
		
	}
	
	@ Override
	public void onClick ( View v ) {
		switch ( v.getId ( ) ) {
			case R.id.vertical_back_btn :
				if ( getActivity ( ) instanceof PlayerTabCallBacker ) {
					( ( PlayerTabCallBacker ) getActivity ( ) ).onPlayerCallBackerClick ( );
				}
				break;
			case R.id.rapper_loop_btn :
				changeLoopMode ( );
				break;
			case R.id.rapper_btn_play :
				changePlayStatu ( );
				break;
			
			default :
				break;
		}
	}
	
	private void changePlayStatu ( ) {
		Intent intent = new Intent ( context , com.example.service.RapperService.class );
		intent.putExtra ( "type" , 1 );
		context.startService ( intent );
		
	}
	
	private void changeLoopMode ( ) {
		int loopOperation = changeLoopLogo ( );
		Intent intent = new Intent ( context , com.example.service.RapperService.class );
		intent.putExtra ( "type" , 0 );
		intent.putExtra ( "loopOperation" , loopOperation );
		context.startService ( intent );
		
	}
	
	private int changeLoopLogo ( ) {
		int tempThreeLoopTag = ( ++ threeLoopTag ) % rapperLoopLogoIds.length;
		rapperLoopButton.setImageDrawable ( getResources ( ).getDrawable ( rapperLoopLogoIds [ tempThreeLoopTag ] ) );
		return tempThreeLoopTag;
	}
	
	@ Override
	public void operation ( int currentScreen , int screenCount ) {
		// TODO Auto-generated method stub
		if ( currentScreen == 1 ) {
			playerJerryLyricView.setAnimation ( AnimationUtils.loadAnimation ( context , R.anim.slide_to_album ) );
		} else {
			playerLudaLyricView.setAnimation ( AnimationUtils.loadAnimation ( context , R.anim.slide_to_lyric ) );
		}
		if ( memoryTempView [ 2 ] != currentScreen ) {
			changeScreenPlayStatu ( );
			
		}
		memoryTempView [ 2 ] = currentScreen;
	}
	
	/**
	 * 当rapper屏幕滑动到另一个页面时，让按钮根据实时的播放状态而改变。
	 */
	private void changeScreenPlayStatu ( ) {
		Intent intent = new Intent ( context , com.example.service.RapperService.class );
		intent.putExtra ( "type" , 2 );
		context.startService ( intent );
		
	}
}
