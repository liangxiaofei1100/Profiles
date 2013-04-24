package com.dreamlink.profiles.start;

import com.dreamlink.profiles.ProfileUtil;
import com.dreamlink.profiles.ui.ProfileListActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class StartActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences sp = getSharedPreferences(ProfileUtil.SHARED_NAME, MODE_PRIVATE);
		boolean first_start = sp.getBoolean(ProfileUtil.FIRST_START_FLAG, true);
		Intent intent = new Intent();
		if(first_start){
			intent.setClass(this, AppIntroduce.class);
		}else {
			intent.setClass(this, ProfileListActivity.class);
		}
		
		startActivity(intent);
		this.finish();
	}
}
