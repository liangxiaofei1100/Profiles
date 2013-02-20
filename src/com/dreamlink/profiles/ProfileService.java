package com.dreamlink.profiles;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.dreamlink.profiles.data.ProfilesMetaData;
import com.dreamlink.profiles.ui.ProfileConfigFragment;
import com.dreamlink.profiles.ui.ProfileListFragment;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**Service*/
public class ProfileService extends Service{
	private static final String TAG = "ProfileService";
	
	private Context mContext;
	
	/**define a Profile,the current active profile*/
	public static  Profile mActiveProfile;
	
	private static Handler mHandler = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d(TAG, "onCreateService");
		mContext = getApplicationContext();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			stopSelf();
			return START_NOT_STICKY;
		}
		
		Profile profile = null;
		Bundle args = intent.getExtras();
		if (args != null) {
			profile = args.getParcelable("profile");
		}
		
		String action = intent.getAction();
		if (action.equals(Constant.INIT_ACTION)) {
			Thread t = new Thread(){
				@Override
				public void run() {
					try {
						//delete data
						getContentResolver().delete(ProfilesMetaData.Profiles.CONTENT_URI, null, null);
						
						//clear list
						ProfileListFragment.mDefaultProfiles.clear();
						ProfileListFragment.mCustomProfiles.clear();
						
						//delete sharedPreference
						SharedPreferences sp = getSharedPreferences(Constant.PROFILE_SHARE, Context.MODE_PRIVATE);
						Editor editor = sp.edit();
						editor.remove(Constant.ENABLE);
						editor.commit();
						
						loadFromFile();
						mHandler.sendMessage(mHandler.obtainMessage(ProfileListFragment.MSG_INIT_OVER));
					} catch (XmlPullParserException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			t.start();
		}else if (action.equals(Constant.GET_PROFILES)) {
			getProfiles();
			mHandler.sendMessage(mHandler.obtainMessage(ProfileListFragment.MSG_GET_PROFILES));
		}else if (action.equals(Constant.SET_ACTIVE_PROFILE)) {
			setActiveProfile(profile);
			mHandler.sendMessage(mHandler.obtainMessage(ProfileListFragment.MSG_SET_ACTIVE_PROFILE));
		}else if (action.equals(Constant.QUERY_RECORD)) {
			queryRecord();
			mHandler.sendMessage(mHandler.obtainMessage(ProfileListFragment.MSG_QUERY_OVER));
		}else if (action.equals(Constant.UPDATE_PROFILE)) {
			updateProfiles(profile);
		}else if (action.equals(Constant.INSERT_RECORD)) {
			insertRecord(profile);
		}else if (action.equals(Constant.DELETE_RECORD)) {
			deleteProfile(profile);
			mHandler.sendMessage(mHandler.obtainMessage(ProfileListFragment.MSG_DELETE_OVER));
		}
		return START_NOT_STICKY;
	}
	
	public void loadFromFile() throws XmlPullParserException, IOException {
		XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
		XmlPullParser xpp = xppf.newPullParser();

		InputStreamReader isr = new InputStreamReader(getResources().openRawResource(R.raw.profile_default));

		xpp.setInput(isr);
		loadXml(xpp);
		isr.close();

	}
	
	public void loadXml(XmlPullParser xpp) throws XmlPullParserException, IOException{
		int event = xpp.next();
		
		while (event != XmlPullParser.END_TAG || !"profiles".equals(xpp.getName())) {
			if (event == XmlPullParser.START_TAG) {
				String name = xpp.getName();
//				Log.d(TAG, "name=" + xpp.getName());
				if (name.equals("profile")) {
					Profile profile = Profile.fromXml(xpp,mContext);
					addProfileInternel(profile);
					insertRecord(profile);
				} else {
					Log.e(TAG, "error=" + name);
				}
			}
			event = xpp.next();
		}
	}
	
	private void addProfileInternel(Profile profile){
		Log.v(TAG, "addProfileInternel");
		if(profile.isDefault()){
			//id is the key of profile
			ProfileListFragment.mDefaultProfiles.put(profile.getId() + "", profile);
		}else {
			ProfileListFragment.mCustomProfiles.put(profile.getId() + "", profile);
		}
		
//		Log.d(TAG,"=="+ ProfileFragment.mProfiles.size());
	}
	
	/**get current all profiles*/
	public void getProfiles(){
		Log.d(TAG, "getProfiles.customsize=" + ProfileListFragment.mCustomProfiles.size()
				+ "getProfiles.default=" + ProfileListFragment.mDefaultProfiles.size());
		ProfileListFragment.defaultProfiles = ProfileListFragment.mDefaultProfiles.
				values().toArray(new Profile[ProfileListFragment.mDefaultProfiles.size()]);
		
		if (ProfileListFragment.mCustomProfiles.size() > 0) {
			ProfileListFragment.customProfiles = ProfileListFragment.mCustomProfiles.
					values().toArray(new Profile[ProfileListFragment.mCustomProfiles.size()]);
			Arrays.sort(ProfileListFragment.customProfiles);
		}else {
			ProfileListFragment.customProfiles = null;
		}
		
		Arrays.sort(ProfileListFragment.defaultProfiles);
	}
	
	/**Update profiles*/
	public void updateProfiles(Profile profile) {
//		Log.v(TAG, "Test name= " + profile.getProfileName() + "id=" + profile.getId());
		SharedPreferences sp = mContext.getSharedPreferences(Constant.PROFILE_SHARE, Context.MODE_PRIVATE);
		int active_id = sp.getInt(Constant.ENABLE, -1);
		
		Profile old = null;
		
		//default profile
		if (profile.isDefault()) {
			//the profile before update
			old = ProfileListFragment.mDefaultProfiles.get(profile.getId() + "");
			if (old != null) {
				//add new profile to mProfiles
				ProfileListFragment.mDefaultProfiles.put(profile.getId() + "", profile);
			}else {
				Log.e(TAG, "old profile is null1");
			}
		}else {
			old = ProfileListFragment.mCustomProfiles.get(profile.getId() + "");
			if (old != null) {
				ProfileListFragment.mCustomProfiles.put(profile.getId() + "", profile);
			}else {
				Log.e(TAG, "old profile is null2");
			}
		}
		
		//save the data to db
		updateRecord(profile);
		
		//if the profile is active profile
		if (profile.getId() == active_id) {
			//do apply
			setActiveProfile(profile);
		}
	}
	
	/**delete the custom profile*/
	public void deleteProfile(Profile profile){
//		Log.v(TAG, "deleteProfile-->" + profile.getProfileName());
		ProfileListFragment.mCustomProfiles.remove(profile.getId() + "");
//		Log.v(TAG, "after delete-->" + ProfileListFragment.mCustomProfiles.size());
		if (ProfileListFragment.mCustomProfiles.size() <= 0) {
			ProfileListFragment.customProfiles = null;
		}
		deleteRecord(profile);
	}
	
	public boolean setActiveProfile(Profile newActiveProfile){
		Log.d(TAG, "setActiveProfile-->" + newActiveProfile.getProfileName());
		//use new profile for active profile
		newActiveProfile.doSelect(mContext);
		return true;
	}
	
	/**insert a profile into db*/
	private Uri insertRecord(Profile profile) {
		ContentValues values = new ContentValues();
		values.put(ProfilesMetaData.Profiles.NAME, profile.getProfileName());
		values.put(ProfilesMetaData.Profiles.MEDIA_VOLUME, profile.getMediaVolume());
		values.put(ProfilesMetaData.Profiles.RINGTONE_VOLUME, profile.getRingVolume());
		values.put(ProfilesMetaData.Profiles.NOTIFICATION_VOLUME, profile.getNotificationVolume());
		values.put(ProfilesMetaData.Profiles.AlARM_VOLUME, profile.getAlarmVolume());
		values.put(ProfilesMetaData.Profiles.RING_VIBRATION, profile.getRingVibrator());
		values.put(ProfilesMetaData.Profiles.RINGTONE, profile.getRingOverride().toString());
		values.put(ProfilesMetaData.Profiles.NOTIFICATION_TONE, profile.getNotificationOverride().toString());
		values.put(ProfilesMetaData.Profiles.ALARM_TONE, profile.getAlarmOverride().toString());
		values.put(ProfilesMetaData.Profiles.DEFAULT, profile.isDefault());
		values.put(ProfilesMetaData.Profiles.ICON_ID, profile.getIcon());
		return getContentResolver().insert(ProfilesMetaData.Profiles.CONTENT_URI,
				values);
	}
	
	/**query all profiles*/
	private void queryRecord(){
		ProfileListFragment.mDefaultProfiles.clear();
		ProfileListFragment.mCustomProfiles.clear();
		
		Cursor cur = null;
		try {
			cur = getContentResolver().query(ProfilesMetaData.Profiles.CONTENT_URI, Constant.columns, null, null, null);
			if (cur.moveToFirst()) {
				int id;
				String profileName = null;
				Profile profile;
				do{
					//query all profile and return 
					profileName = cur.getString(cur.getColumnIndex(ProfilesMetaData.Profiles.NAME));
					profile = new Profile(profileName);//
					
					profile.setId(cur.getInt(cur.getColumnIndex(ProfilesMetaData.Profiles._ID)));
//					Log.d(TAG, "query.id=" + cur.getInt(cur.getColumnIndex(ProfilesMetaData.Profiles._ID)));
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
					
					addProfileInternel(profile);
				}while(cur.moveToNext());
			}
		} catch (Exception e) {
			Log.e(TAG, "SQL Error:" + e.toString());
		}finally{
			if(cur != null){
				cur.close();
			}
		}
		
	}
	
	/**update */
	public void updateRecord(Profile profile){
		ContentValues values = new ContentValues();
		values.put(ProfilesMetaData.Profiles.NAME, profile.getProfileName());
		values.put(ProfilesMetaData.Profiles.MEDIA_VOLUME, profile.getMediaVolume());
		values.put(ProfilesMetaData.Profiles.RINGTONE_VOLUME, profile.getRingVolume());
		values.put(ProfilesMetaData.Profiles.NOTIFICATION_VOLUME, profile.getNotificationVolume());
		values.put(ProfilesMetaData.Profiles.AlARM_VOLUME, profile.getAlarmVolume());
		values.put(ProfilesMetaData.Profiles.RING_VIBRATION, profile.getRingVibrator());
		values.put(ProfilesMetaData.Profiles.RINGTONE, profile.getRingOverride().toString());
		values.put(ProfilesMetaData.Profiles.NOTIFICATION_TONE, profile.getNotificationOverride().toString());
		values.put(ProfilesMetaData.Profiles.ALARM_TONE, profile.getAlarmOverride().toString());

//		Log.d(TAG, "profie.name=" + profile.getProfileName());
		
		Uri uri = Uri.parse(ProfilesMetaData.Profiles.CONTENT_URI + "/" + profile.getId());
		getContentResolver().update(uri, values, null, null);
	}
	
	public void deleteRecord(Profile profile){
		Uri uri = Uri.parse(ProfilesMetaData.Profiles.CONTENT_URI + "/" + profile.getId());
		getContentResolver().delete(uri, null, null);
	}
	
	public static void setHandler(Handler handler){
		mHandler = handler;
	}

}
