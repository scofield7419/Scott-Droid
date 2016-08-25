package com.example.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.activity.R;
import com.example.lyric.Lyric;

public class MyLyricView extends TextView {
	private float width; // 歌词视图宽度
	private float height; // 歌词视图高度
	private Paint currentPaint; // 当前画笔对象
	private Paint notCurrentPaint; // 非当前画笔对象
	private Paint errorPaint; // 发生错误报告的画笔对象
	private float textHeightShow; // 文本高度
	private float textSizeShow; // 文本大小
	private float textSizeUnshow; // 文本大小
	private int index = 0; // list集合下标
	
	private List < Lyric > mLrcList = new ArrayList < Lyric > ( );
	
	// 实时更新songs的lyricList
	public void setmLrcList ( List < Lyric > mLrcList ) {
		this.mLrcList = mLrcList;
	}
	
	public MyLyricView ( Context context ) {
		super ( context );
	}
	
	public MyLyricView ( Context context , AttributeSet attrs , int defStyle ) {
		super ( context , attrs , defStyle );
		init ( attrs );
	}
	
	public MyLyricView ( Context context , AttributeSet attrs ) {
		super ( context , attrs );
		init ( attrs );
	}
	
	private void init ( AttributeSet attrs ) {
		TypedArray textDimeAttrs = getContext ( ).obtainStyledAttributes ( attrs , R.styleable.MyLyricView );
		
		textSizeShow = textDimeAttrs.getDimension ( R.styleable.MyLyricView_textSizeShow , 60 );
		textSizeUnshow = textDimeAttrs.getDimension ( R.styleable.MyLyricView_textSizeUnshow , 50 );
		textHeightShow = textDimeAttrs.getDimension ( R.styleable.MyLyricView_textHeightShow , 90 );
		
		setFocusable ( true ); // 设置可对焦
		
		// 高亮部分
		currentPaint = new Paint ( );
		currentPaint.setAntiAlias ( true ); // 设置抗锯齿，让文字美观饱满
		currentPaint.setTextAlign ( Paint.Align.CENTER );// 设置文本对齐方式
		
		// 非高亮部分
		notCurrentPaint = new Paint ( );
		notCurrentPaint.setAntiAlias ( true );
		notCurrentPaint.setTextAlign ( Paint.Align.CENTER );
		
		// 报错部分
		errorPaint = new Paint ( );
		errorPaint.setAntiAlias ( true );
		errorPaint.setTextAlign ( Paint.Align.CENTER );
	}
	
	/**
	 * 绘画歌词
	 */
	@ Override
	protected void onDraw ( Canvas canvas ) {
		super.onDraw ( canvas );
		if ( canvas == null ) {
			return;
		}
		
		currentPaint.setColor ( getResources ( ).getColor ( R.color.text_lyric_show ) );
		currentPaint.setTextSize ( textSizeShow );
		currentPaint.setTypeface ( Typeface.SANS_SERIF );
		
		notCurrentPaint.setColor ( getResources ( ).getColor ( R.color.text_lyric_unshow ) );
		notCurrentPaint.setTextSize ( textSizeUnshow );
		notCurrentPaint.setTypeface ( Typeface.SANS_SERIF );
		
		try {
			setText ( "" );
			canvas.drawText ( mLrcList.get ( index ).getLrcStr ( ) , width / 2 , height / 2 , currentPaint );
			
			float tempY = height / 2;
			// 画出本句之前的句子
			for ( int i = index - 1 ; i >= 0 ; i -- ) {
				// 向上推移
				tempY = tempY - textHeightShow;
				canvas.drawText ( mLrcList.get ( i ).getLrcStr ( ) , width / 2 , tempY , notCurrentPaint );
			}
			tempY = height / 2;
			// 画出本句之后的句子
			for ( int i = index + 1 ; i < mLrcList.size ( ) ; i ++ ) {
				// 往下推移
				tempY = tempY + textHeightShow;
				canvas.drawText ( mLrcList.get ( i ).getLrcStr ( ) , width / 2 , tempY , notCurrentPaint );
			}
		} catch ( Exception e ) {
			errorPaint.setColor ( getResources ( ).getColor ( R.color.text_lyric_show ) );
			errorPaint.setTextSize ( textSizeUnshow );
			errorPaint.setTypeface ( Typeface.SANS_SERIF );
			canvas.drawText ( "oops~ cannot find the lyric." , width / 2 , height / 2 , errorPaint );
//			errorPaint.setTextSize ( textSizeUnshow );
//			canvas.drawText ( "try to make sure the lyric exists in the same file." , width / 2 ,textHeightShow+ height / 2 , errorPaint );
			
		}
		// System.out.println ( "in mylrcview  ：onDraw" );
	}
	
	@ Override
	protected void onMeasure ( int widthMeasureSpec , int heightMeasureSpec ) {
		int widthMode = MeasureSpec.getMode ( widthMeasureSpec );
		int widthSize = MeasureSpec.getSize ( widthMeasureSpec );
		int heightMode = MeasureSpec.getMode ( heightMeasureSpec );
		int heightSize = MeasureSpec.getSize ( heightMeasureSpec );
		int width = 0;
		int height = 0;
		if ( widthMode == MeasureSpec.EXACTLY ) {
			// width = widthSize;
			width = ( int ) ( getPaddingLeft ( ) + widthSize + getPaddingRight ( ) );
		} else {
			// mPaint.setTextSize ( mTitleTextSize );
			// mPaint.getTextBounds ( mTitle , 0 , mTitle.length ( )
			// , mBounds );
			// float textWidth = mBounds.width ( );
			// int desired = ( int ) ( getPaddingLeft ( ) +
			// textWidth + getPaddingRight ( ) );
			// width = desired;
			width = 200;
		}
		
		if ( heightMode == MeasureSpec.EXACTLY ) {
			// height =heightSize ;
			height = ( int ) ( getPaddingTop ( ) + heightSize + getPaddingBottom ( ) );
			// height = desired;
		} else {
			// mPaint.setTextSize ( mTitleTextSize );
			// mPaint.getTextBounds ( mTitle , 0 , mTitle.length ( )
			// , mBounds );
			// float textHeight = mBounds.height ( );
			// int desired = ( int ) ( getPaddingTop ( ) +
			// textHeight + getPaddingBottom ( ) );
			// height = desired;
			height = 400;
		}
		
		setMeasuredDimension ( width , height );
	}
	
	/**
	 * 当view大小改变的时候调用的方法
	 */
	@ Override
	protected void onSizeChanged ( int w , int h , int oldw , int oldh ) {
		super.onSizeChanged ( w , h , oldw , oldh );
		this.width = w;
		this.height = h;
	}
	
	public void setIndex ( int index ) {
		this.index = index;
	}
	
}
