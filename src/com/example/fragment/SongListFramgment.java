package com.example.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.activity.MainPlayerActivity;
import com.example.activity.R;
import com.example.quickaccessbar.CharacterParser;
import com.example.quickaccessbar.QuickAccessBar;
import com.example.quickaccessbar.QuickAccessBar.OnTouchingLetterChangedListener;
import com.example.quickaccessbar.QuickLetterModel;
import com.example.song.Song;
import com.example.song.SongListAdapter;
import com.example.utils.MyConstant;

public class SongListFramgment extends Fragment implements OnClickListener {
	Context context;
	
	LocalBroadcastManager localBroadcastManager1;
	ListPlayerBtnUpdateBroadCastReceiver listPlayerBtnUpdateBroadCastReceiver;
	ListCompleteUpdateBroadCastReceiver listCompleteUpdateBroadCastReceiver;
	
	View listOperateLayout;
	ListView songList;
	
	Button listBtnPlay;
	Button listBtnPrev;
	Button listBtnNext;
	
	TextView operateListTitleTextView;
	TextView operateListInfoTextView;
	
	ImageView operateIconImageView;
	
	View songListLayout;
	
	SongListAdapter songListAdapter;
	public List < Song > songs;
	
	boolean isViewInited = false;
	
	private int [ ] listPlayBtnIds = new int [ ] {
	                R.drawable.list_play_btn , R.drawable.list_pause_btn };
	
	// 第一个是播放按钮记录，第二个是歌曲id。
	public int [ ] memoryListTempView = new int [ 2 ];
	
	//quickacessbar相关
	private QuickAccessBar quickAccessBar;
	private TextView dialogQuickBar;
	private CharacterParser characterParser;//汉字转换成拼音的类
	private List < QuickLetterModel > quickLetterModelList;
	private String [ ] songsTitles;
	
	public interface SongListCallBacker {
		public void onSongListCallBackerClick ( );
	}
	
	public SongListFramgment ( Context context ) {
		this.context = context;
	}
	
	@ Override
	public void onCreate ( Bundle savedInstanceState ) {
		super.onCreate ( savedInstanceState );
		this.songs = MainPlayerActivity.songs;
		loadSongsTitles ( );
	}
	
	private void loadSongsTitles ( ) {
		songsTitles = new String [ songs.size ( ) ];
		for ( int i = 0 ; i < songsTitles.length ; i ++ ) {
			String title = new String ( );
			title = songs.get ( i ).getTitle ( );
			songsTitles [ i ] = title;
		}
		
	}
	
	@ Override
	public void onAttach ( Activity activity ) {
		super.onAttach ( activity );
		initBroadcastReceiver ( );
	}
	
	private void informSreviceUpdateMe ( ) {
		Intent intentUpdateMe = new Intent ( MyConstant.ACTION_BROADCAST_PLAYERSERVICE_LISTFRAG_FIRST_UPDATE );
		intentUpdateMe.putExtra ( "isFromListFrag" , 0 );
		localBroadcastManager1.sendBroadcast ( intentUpdateMe );
	}
	
	private void changeListItemSelectedView ( ) {
		songListAdapter.notifyDataSetInvalidated ( );
	}
	
	private void relocateListItemPosition ( ) {
		songList.setSelection ( memoryListTempView [ 1 ] - 4 > 0 ? memoryListTempView [ 1 ] - 4 : 0 );
		changeListItemSelectedView ( );
	}
	
	private void restoreView ( ) {
		Song song = songs.get ( memoryListTempView [ 1 ] );
		String title = song.getTitle ( );
		String info = song.getAuthor ( ) + MyConstant.CONNECTION_WORD + song.getSongAlbum ( );
		operateListTitleTextView.setText ( title );
		operateListInfoTextView.setText ( info );
		
		listBtnPlay.setBackground ( getResources ( ).getDrawable ( listPlayBtnIds [ memoryListTempView [ 0 ] ] ) );
//		changeListItemSelectedView();
	}
	
	@ Override
	public View onCreateView ( LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState ) {
		songListLayout = inflater.inflate ( R.layout.song_list_frag , container , false );
		listOperateLayout = songListLayout.findViewById ( R.id.list_opera_layout );
		listOperateLayout.setOnClickListener ( this );
		initView ( songListLayout );
		return songListLayout;
	}
	
	private void initView ( View songListLayout ) {
		songList = ( ListView ) songListLayout.findViewById ( R.id.song_list );
		listBtnNext = ( Button ) songListLayout.findViewById ( R.id.list_btn_next );
		listBtnPlay = ( Button ) songListLayout.findViewById ( R.id.list_btn_play );
		listBtnPrev = ( Button ) songListLayout.findViewById ( R.id.list_btn_prev );
		listBtnNext.setOnClickListener ( this );
		listBtnPlay.setOnClickListener ( this );
		listBtnPrev.setOnClickListener ( this );
		
		operateIconImageView = ( ImageView ) songListLayout.findViewById ( R.id.operate_list_song_icon );
		operateIconImageView.setOnClickListener ( this );
		
		operateListInfoTextView = ( TextView ) songListLayout.findViewById ( R.id.operate_list_song_info );
		operateListTitleTextView = ( TextView ) songListLayout.findViewById ( R.id.operate_list_song_title );
		if ( songs == null ) {
			Toast.makeText ( context , "系统正在加载歌单，请您先到别处逛逛吧~" , Toast.LENGTH_SHORT ).show ( );
			return;
		}
		
		characterParser = CharacterParser.getInstance ( );
		quickAccessBar = ( QuickAccessBar ) songListLayout.findViewById ( R.id.song_list_quick_access_bar );
		dialogQuickBar = ( TextView ) songListLayout.findViewById ( R.id.song_list_dialog_quick_bar );
		quickAccessBar.setTextView ( dialogQuickBar );
		
		quickLetterModelList = filledData ( songsTitles );
		songListAdapter = new SongListAdapter ( songs , context , this , quickLetterModelList );
		songList.setOnItemClickListener ( new OnItemClickListener ( ) {
			@ Override
			public void onItemClick ( AdapterView < ? > parent , View view , int position , long id ) {
				Intent intent = new Intent ( context , com.example.service.MainPlayerService.class );
				intent.putExtra ( "type" , 2 );
				intent.putExtra ( "songListPosition" , position );
				context.startService ( intent );
			}
		} );
		//设置右侧触摸监听
		quickAccessBar.setOnTouchingLetterChangedListener ( new OnTouchingLetterChangedListener ( ) {
			
			@ Override
			public void onTouchingLetterChanged ( String s ) {
				//该字母首次出现的位置
				int position = songListAdapter.getPositionForSection ( s.charAt ( 0 ) );
				if ( position != - 1 ) {
					songList.setSelection ( position );
				}
			}
		} );
		
		songList.setAdapter ( songListAdapter );
	}
	
	private List < QuickLetterModel > filledData ( String [ ] date ) {
		List < QuickLetterModel > mSortList = new ArrayList < QuickLetterModel > ( );
		
		for ( int i = 0 ; i < date.length ; i ++ ) {
			QuickLetterModel sortModel = new QuickLetterModel ( );
			sortModel.setName ( date [ i ] );
			//汉字转换成拼音
			String pinyin = characterParser.getSelling ( date [ i ] );
			String sortString = pinyin.substring ( 0 , 1 ).toUpperCase ( );
			
			// 正则表达式，判断首字母是否是英文字母
			if ( sortString.matches ( "[A-Z]" ) ) {
				sortModel.setQuickLetters ( sortString.toUpperCase ( ) );
			} else {
				sortModel.setQuickLetters ( "#" );
			}
			
			mSortList.add ( sortModel );
		}
		return mSortList;
	}
	
	@ Override
	public void onStart ( ) {
		super.onStart ( );
		informSreviceUpdateMe ( );
//		relocateListItemPosition ( );
	}
	
	@ Override
	public void onResume ( ) {
		restoreView ( );
		relocateListItemPosition ( );
		super.onResume ( );
	}
	
	private void initBroadcastReceiver ( ) {
		localBroadcastManager1 = LocalBroadcastManager.getInstance ( context );
		listPlayerBtnUpdateBroadCastReceiver = new ListPlayerBtnUpdateBroadCastReceiver ( );
		IntentFilter filter1 = new IntentFilter ( );
		filter1.addAction ( MyConstant.ACTION_BROADCAST_LIST_BTN_UPDATE );
		localBroadcastManager1.registerReceiver ( listPlayerBtnUpdateBroadCastReceiver , filter1 );
		
		listCompleteUpdateBroadCastReceiver = new ListCompleteUpdateBroadCastReceiver ( );
		IntentFilter filter2 = new IntentFilter ( );
		filter2.addAction ( MyConstant.ACTION_BROADCAST_LIST_INFO_UPDATE );
		localBroadcastManager1.registerReceiver ( listCompleteUpdateBroadCastReceiver , filter2 );
		
	}
	
	@ Override
	public void onClick ( View v ) {
		switch ( v.getId ( ) ) {
			case R.id.list_opera_layout :
				if ( getActivity ( ) instanceof SongListCallBacker ) {
					( ( SongListCallBacker ) getActivity ( ) ).onSongListCallBackerClick ( );
				}
				break;
			case R.id.list_btn_play :
				changePlayStatu ( );
				break;
			case R.id.list_btn_next :
				playNext ( );
				break;
			case R.id.list_btn_prev :
				playPrev ( );
				break;
			
			default :
				break;
		}
		
	}
	
	private void playNext ( ) {
		Intent intent = new Intent ( context , com.example.service.MainPlayerService.class );
		intent.putExtra ( "type" , 1 );
		intent.putExtra ( "playOperation" , 2 );
		context.startService ( intent );
		
	}
	
	private void playPrev ( ) {
		Intent intent = new Intent ( context , com.example.service.MainPlayerService.class );
		intent.putExtra ( "type" , 1 );
		intent.putExtra ( "playOperation" , 1 );
		context.startService ( intent );
		
	}
	
	private void changePlayStatu ( ) {
		Intent intent = new Intent ( context , com.example.service.MainPlayerService.class );
		intent.putExtra ( "type" , 1 );
		intent.putExtra ( "playOperation" , 0 );
		context.startService ( intent );
		
	}
	
	public class ListPlayerBtnUpdateBroadCastReceiver extends BroadcastReceiver {
		
		@ Override
		public void onReceive ( Context context , Intent intent ) {
			if ( ! SongListFramgment.this.isAdded ( ) ) {
				return;
			}
			int listBtnTag = intent.getIntExtra ( "listBtnTag" , 0 );
			listBtnPlay.setBackground ( getResources ( ).getDrawable ( listPlayBtnIds [ listBtnTag ] ) );
			memoryListTempView [ 0 ] = listBtnTag;
		}
	}
	
	public class ListCompleteUpdateBroadCastReceiver extends BroadcastReceiver {
		
		@ Override
		public void onReceive ( Context context , Intent intent ) {
			if ( ! SongListFramgment.this.isAdded ( ) ) {
				return;
			}
			int songId = intent.getIntExtra ( "songId" , 0 );
			int isStartFirstTime = intent.getIntExtra ( "isStartFirstTime" , - 1 );
			memoryListTempView [ 1 ] = songId;
			restoreView ( );
			if ( isStartFirstTime == 0 ) {
				relocateListItemPosition ( );
			}
			changeListItemSelectedView ( );
		}
		
	}
}
