package com.dreamlink.profiles.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dreamlink.profiles.Constant;
import com.dreamlink.profiles.Profile;
import com.dreamlink.profiles.R;
import com.dreamlink.profiles.data.ProfilesMetaData;
import com.dreamlink.profiles.ui.ProfileListActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

public class ProfilesWidgetActivity extends Activity implements OnClickListener,
		OnItemClickListener {
	private static final String TAG = "DialogForAppWidget";
	private GridView gv;
	private View view;
	private ImageView mSettingView;
	private ArrayList<Profile> pList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.widget_dialog);
		
		pList = new ArrayList<Profile>();
		
		gv = (GridView) findViewById(R.id.gridview);
		gv.setOnItemClickListener(this);
		
		view = findViewById(R.id.settingClick);
		view.setOnClickListener(this);
		
//		mSettingView = (ImageView) findViewById(R.id.setting_button);
//		mSettingView.setOnClickListener(this);
		
		queryRecord();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		gv.setAdapter(createDataAdapter());
	}

	@Override
	public void finish() {
		super.finish();

	}

	public void onClick(View v) {
		if (v.getId() == R.id.settingClick) {
			Intent intent = new Intent();
			intent.setClass(this, ProfileListActivity.class);
			this.startActivity(intent);
			finish();
		}

	}

	private SimpleAdapter createDataAdapter() {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(int i=0; i < pList.size() ; i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", pList.get(i).getIcon());
			map.put("name", pList.get(i).getProfileName());
			list.add(map);
		}
		SimpleAdapter sa = new SimpleAdapter(this, list, R.layout.itemlayout,
				new String[] { "image", "name" }, new int[] { R.id.imageView1,
						R.id.textView1 });
		return sa;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		Profile p = pList.get(position);
		
		doSelect(p);
		
		Intent intent = new Intent(Constant.ACTION_APP_WIDGET_UPDATE);
		Bundle bundle = new Bundle();
		bundle.putParcelable("profile", p);
		intent.putExtras(bundle);
		sendBroadcast(intent);
		finish();
	}
	
	private void queryRecord(){
		pList.clear();
		
		Cursor cur = null;
		try {
			cur = getContentResolver().query(ProfilesMetaData.Profiles.CONTENT_URI, Constant.columns, null, null, null);
			if (cur.moveToFirst()) {
				String profileName = null;
				Profile profile;
				do{
					//query all profile and return 
					profileName = cur.getString(cur.getColumnIndex(ProfilesMetaData.Profiles.NAME));
					profile = new Profile(profileName);//
					
					profile.setId(cur.getInt(cur.getColumnIndex(ProfilesMetaData.Profiles._ID)));
					profile.setMediaVolume(cur.getInt(cur.getColumnIndex(ProfilesMetaData.Profiles.MEDIA_VOLUME)));
					profile.setRingVolume(cur.getInt(cur.getColumnIndex(ProfilesMetaData.Profiles.RINGTONE_VOLUME)));
					profile.setNotificationVolume(cur.getInt(cur.getColumnIndex(ProfilesMetaData.Profiles.NOTIFICATION_VOLUME)));
					profile.setAlarmVolume(cur.getInt(cur.getColumnIndex(ProfilesMetaData.Profiles.AlARM_VOLUME)));
					profile.setRingOverride(Uri.parse(cur.getString(cur.getColumnIndex(ProfilesMetaData.Profiles.RINGTONE))));
					profile.setNotificationOverride(Uri.parse(cur.getString(cur.getColumnIndex(ProfilesMetaData.Profiles.NOTIFICATION_TONE))));
					profile.setAlarmOverride(Uri.parse(cur.getString(cur.getColumnIndex(ProfilesMetaData.Profiles.ALARM_TONE))));
					profile.setRingVibrator((cur.getInt(cur.getColumnIndex(ProfilesMetaData.Profiles.RING_VIBRATION))) ==1 ? true : false);
					profile.setDefault((cur.getInt(cur.getColumnIndex(ProfilesMetaData.Profiles.DEFAULT))) == 1 ? true :false);
					profile.setIcon(cur.getInt(cur.getColumnIndex(ProfilesMetaData.Profiles.ICON_ID)));
					
					pList.add(profile);
				}while(cur.moveToNext());
			}else {
				Log.e(TAG, "Erroor");
			}
		} catch (Exception e) {
			Log.e(TAG, "SQL Error:" + e.toString());
		}finally{
			if(cur != null){
				cur.close();
			}
		}
		
	}
	
	private void doSelect(Profile profile){
		SharedPreferences sp = getSharedPreferences(Constant.PROFILE_SHARE, MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt(Constant.ENABLE, profile.getId());
		editor.putString(Constant.ACTIVE_NAME, profile.getProfileName());
		editor.putInt(Constant.ACTIVE_ICON_ID, profile.getIcon());
		editor.commit();
		
		profile.doSelect(ProfilesWidgetActivity.this);
	}
}
