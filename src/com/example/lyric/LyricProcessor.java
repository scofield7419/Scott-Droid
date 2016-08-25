package com.example.lyric;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LyricProcessor {
	private List < Lyric > lrcList; // List集合存放歌词内容对象
	private Lyric mLrc; // 声明一个歌词内容对象
	
	/**
	 * 无参构造函数用来实例化对象
	 */
	public LyricProcessor ( ) {
		mLrc = new Lyric ( );
		lrcList = new ArrayList < Lyric > ( );
	}
	
	/**
	 * 读取歌词
	 * 
	 * @param path
	 * @return
	 */
	public String readLRCFromSDPath ( String path ) {
		// 定义一个StringBuilder对象，用来存放歌词内容
		StringBuilder stringBuilder = new StringBuilder ( );
		// 此处理应用正则表达式解决通配问题
		String tmpPath = null;
		String exten = path.substring ( path.length ( ) - 4 , path.length ( ) );
		if ( exten.equals ( ".mp3" ) ) {
			tmpPath = path.replace ( ".mp3" , ".lrc" );
		} else if ( exten.equals ( ".m4a" ) ) {
			tmpPath = path.replace ( ".m4a" , ".lrc" );
		} else if ( exten.equals ( ".ape" ) ) {
			tmpPath = path.replace ( ".ape" , ".lrc" );
		} else if ( exten.equals ( ".wma" ) ) {
			tmpPath = path.replace ( ".wma" , ".lrc" );
		} else if ( exten.equals ( ".wax" ) ) {
			tmpPath = path.replace ( ".wax" , ".lrc" );
		}
		File f = new File ( tmpPath );
		try {
			// 创建一个文件输入流对象
			FileInputStream fis = new FileInputStream ( f );
			InputStreamReader isr = new InputStreamReader ( fis , "utf-8" );
			BufferedReader br = new BufferedReader ( isr );
			String s = "";
			while ( ( s = br.readLine ( ) ) != null ) {
				// 替换字符
				s = s.replace ( "[" , "" );
				s = s.replace ( "]" , "@" );
				
				// 分离“@”字符
				String splitLrcData[] = s.split ( "@" );
				if ( splitLrcData.length > 1 ) {
					mLrc.setLrcStr ( splitLrcData [ 1 ] );
					
					// 处理歌词取得歌曲的时间
					int lrcTime = time2Str ( splitLrcData [ 0 ] );
					
					mLrc.setLrcTime ( lrcTime );
					
					// 添加进列表数组
					lrcList.add ( mLrc );
					
					// 新创建歌词内容对象
					mLrc = new Lyric ( );
				}
			}
			fis.close ( );
			br.close ( );
		} catch ( FileNotFoundException e ) {
			e.printStackTrace ( );
			stringBuilder.append ( "木有歌词文件..." );
		} catch ( IOException e ) {
			e.printStackTrace ( );
			stringBuilder.append ( "木有读取到歌词！" );
		}
		// System.out.println (
		// "in lrcprocessor  ：stringBuilder.toString()"+stringBuilder.toString()
		// );
		return stringBuilder.toString ( );
	}
	
	/**
	 * 解析歌词时间 歌词内容格式如下： [00:02.32]陈奕迅 [00:03.43]好久不见 [00:05.22]歌词制作 王涛
	 * 
	 * @param timeStr
	 * @return
	 */
	public int time2Str ( String timeStr ) {
		timeStr = timeStr.replace ( ":" , "." );
		timeStr = timeStr.replace ( "." , "@" );
		// 将时间分隔成字符串数组
		String[] timeData = timeStr.split ( "@" ); 
		System.out.println ( "timeData[0] == 00 :" + (timeData [ 0 ].equals ( "00" ) ));
		int currentTime = 0;
		try {
			// 分离出分、秒并转换为整型
			int minute = Integer.parseInt (timeData[0] );
			int second = Integer.parseInt ( timeData [ 1 ] );
			int millisecond = Integer.parseInt (timeData [ 2 ] );
			// 计算上一行与下一行的时间转换为毫秒数
			currentTime = ( minute * 60 + second ) * 1000 + millisecond * 10;
	                
                } catch ( Exception e ) {
                	e.printStackTrace ( );
                	return 0;
                }
		
		return currentTime;
	}
	
	public List < Lyric > getLrcList ( ) {
		return lrcList;
	}
	
	/**
	 * 接受一个文件输入流对象
	 * 
	 * @param is
	 * @return
	 */
	public String readLRCFromLrcInputStream ( ByteArrayInputStream is ) {
		// 定义一个StringBuilder对象，用来存放歌词内容
		StringBuilder stringBuilder = new StringBuilder ( );
		try {
			InputStreamReader isr = new InputStreamReader ( is , "UTF-8" );
			BufferedReader br = new BufferedReader ( isr );
			String s = "";
			while ( ( s = br.readLine ( ) ) != null ) {
				// 替换字符
				s = s.replace ( "[" , "" );
				s = s.replace ( "]" , "@" );
				
				// 分离“@”字符
				String splitLrcData[] = s.split ( "@" );
				if ( splitLrcData.length > 1 ) {
					mLrc.setLrcStr ( splitLrcData [ 1 ] );
					
					// 处理歌词取得歌曲的时间
					int lrcTime = time2Str ( splitLrcData [ 0 ] );
					
					mLrc.setLrcTime ( lrcTime );
					
					// 添加进列表数组
					lrcList.add ( mLrc );
					
					// 新创建歌词内容对象
					mLrc = new Lyric ( );
				}
			}
		} catch ( FileNotFoundException e ) {
			System.out.println ( "readLRCFromLrcInputStream  is is bad " );
			e.printStackTrace ( );
			stringBuilder.append ( "木有歌词文件..." );
		} catch ( IOException e ) {
			e.printStackTrace ( );
			stringBuilder.append ( "木有读取到歌词！" );
		}
		
		return stringBuilder.toString ( );
	}
}
