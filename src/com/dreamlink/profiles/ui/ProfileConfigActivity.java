package com.dreamlink.profiles.ui;

import com.dreamlink.profiles.Profile;
import com.dreamlink.profiles.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class ProfileConfigActivity extends Activity {
	private static final String TAG = "ProfileConfigActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_LEFT_ICON);
//		setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.biaozhun);
		Bundle bundle = getIntent().getExtras();
		
		Profile profile = null;
		if (bundle != null) {
    		profile = (Profile)bundle.getParcelable("Profile");
		}else {
			//do nothing
		}
		
		//open ProfileConfigFragment
		getFragmentManager().beginTransaction().replace(android.R.id.content, new ProfileConfigFragment(profile)).commit();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (KeyEvent.KEYCODE_BACK == keyCode) {
		}
		return super.onKeyDown(keyCode, event);
	}
}
