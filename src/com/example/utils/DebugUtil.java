package com.example.utils;

import android.content.Context;
import android.widget.Toast;

public class DebugUtil {
	public static void toa ( Context context , String str ) {
		Toast.makeText ( context , str , Toast.LENGTH_SHORT ).show ( );
	}
}
