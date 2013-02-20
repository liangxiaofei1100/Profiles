package com.dreamlink.profiles.ui;

import com.dreamlink.profiles.Constant;
import com.dreamlink.profiles.R;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 *  Main launch
 * @author yuri
 */
public class ProfileListActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new ProfileListFragment()).commit();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	if (KeyEvent.KEYCODE_BACK == keyCode) {
    		ProfileListActivity.this.finish();
		}
    	return super.onKeyDown(keyCode, event);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, Constant.MENU_ABOUT, 1, R.string.about);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case Constant.MENU_ABOUT:
			new AlertDialog.Builder(ProfileListActivity.this)
				.setTitle(R.string.about)
				.setMessage(R.string.about_statement)
				.setPositiveButton(android.R.string.ok, null)
				.create().show();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
    
}
