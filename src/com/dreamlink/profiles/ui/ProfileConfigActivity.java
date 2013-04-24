package com.dreamlink.profiles.ui;

import com.dreamlink.profiles.Profile;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

public class ProfileConfigActivity extends Activity {
	private static final String TAG = "ProfileConfigActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// click the title back
		final ActionBar bar = getActionBar();
		int flags = ActionBar.DISPLAY_HOME_AS_UP;
		int change = bar.getDisplayOptions() ^ flags;
		bar.setDisplayOptions(change, flags);
				
		Bundle bundle = getIntent().getExtras();
		
		Profile profile = null;
		if (bundle != null) {
    		profile = (Profile)bundle.getParcelable("Profile");
		}else {
			//do nothing
		}
		
		//open ProfileConfigFragment
		getFragmentManager().beginTransaction().replace(android.R.id.content, new ProfileConfigFragment(profile))
		.commit();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
