package com.rambo.marketposter.activity.Base;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.rambo.marketposter.R;
import com.rambo.marketposter.application.MyApplication;


public class BaseFragment extends Fragment {

	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		String name = this.getActivity().getClass().getSimpleName();
		MyApplication.getAppContext().getRequestQueue().cancelAll(name);
	}


}
