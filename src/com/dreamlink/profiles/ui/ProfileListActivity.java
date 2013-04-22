package com.dreamlink.profiles.ui;

import com.dreamlink.profiles.Constant;
import com.dreamlink.profiles.ProfileUtil;
import com.dreamlink.profiles.R;

import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;

/**
 *  Main launch
 * @author yuri
 */
public class ProfileListActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        init();
        
        getFragmentManager().beginTransaction().replace(android.R.id.content, new ProfileListFragment()).commit();
    }
    
    private void init(){
    	//get uri and save
		ProfileUtil.ringtone_uri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
		ProfileUtil.notification_uri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION);
		ProfileUtil.alarm_uri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);
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
		menu.add(0, Constant.MENU_ABOUT, 2, R.string.about);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case Constant.MENU_ABOUT:
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
    
}
