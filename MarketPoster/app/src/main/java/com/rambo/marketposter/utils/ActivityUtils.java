package com.rambo.marketposter.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ActivityUtils {
	/*
	 * 关闭输入法
	 */
	public static void closeInputService(Activity context) {
		 View view = context.getWindow().peekDecorView();
	        if (view != null) {
	            InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
	            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
	        }
	}

	public static void startActivity(Activity actA,Intent intent){
		actA.startActivity(intent);
		Mylog.d("Activity start   " , actA.getClass().getSimpleName() );
	}
}
