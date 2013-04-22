package com.dreamlink.profiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class ProfileManager {
	private static final String TAG = "ProfileManager";
	private static ProfileManager mProfileManager;
	private static Context mContext;
	
	public static ProfileManager newInstance(Context context){
		if (mProfileManager == null) {
			mProfileManager = new ProfileManager();
		}
		
		mContext = context;
		
		return mProfileManager;
	}
	
	private ProfileManager(){
	}
	
	public Uri getDefaultUri(int streamType){
		return RingtoneManager.getActualDefaultRingtoneUri(mContext, streamType);
	}
	
	public void initList(){
		startServiceByAction(Constant.INIT_ACTION);
	}
	
	public void getProfiles(){
		startServiceByAction(Constant.GET_PROFILES);
	}
	
	public void queryRecord(){
		startServiceByAction(Constant.QUERY_RECORD);
	}
	
	public void updateProfile(Profile profile){
		Intent intent = new Intent();
		intent.setClass(mContext, ProfileService.class);
		intent.setAction(Constant.UPDATE_PROFILE);
		
		Bundle bundle = new Bundle();
		bundle.putParcelable("profile", profile);
		
		intent.putExtras(bundle);
		
		mContext.startService(intent);
	}
	
	public void insertProfile(Profile profile){
		Intent intent = new Intent();
		intent.setClass(mContext, ProfileService.class);
		intent.setAction(Constant.INSERT_RECORD);
		
		Bundle bundle = new Bundle();
		bundle.putParcelable("profile", profile);
		
		intent.putExtras(bundle);
		
		mContext.startService(intent);
	}
	
	/**delete a profile*/
	public void deleteProfile(Profile profile){
		Intent intent = new Intent();
		intent.setClass(mContext, ProfileService.class);
		intent.setAction(Constant.DELETE_RECORD);
		
		Bundle bundle = new Bundle();
		bundle.putParcelable("profile", profile);
		
		intent.putExtras(bundle);
		
		mContext.startService(intent);
	}
	
	public void setActiveProfille(Profile profile, Context context){
		Intent intent = new Intent();
		intent.setClass(context, ProfileService.class);
		intent.setAction(Constant.SET_ACTIVE_PROFILE);
		
		Bundle bundle = new Bundle();
		bundle.putParcelable("profile", profile);
		
		intent.putExtras(bundle);
		
		context.startService(intent);
	}
	
	
	private static void startServiceByAction(String action){
		Intent intent = new Intent(mContext,ProfileService.class);
		intent.setAction(action);
		mContext.startService(intent);
	}
	
	//do delete a profile 
	public void doDeleteProfile(final Profile profile){
//		final int ret = -1;
		//first, you need judge is can delete for this profile
		if (isCanDelete(profile)) {
			new AlertDialog.Builder(mContext).setTitle(R.string.delete_profile_confirm)
				.setIcon(R.drawable.alert_light)
				.setMessage(mContext.getResources().getString(R.string.delete_profile_confirm_msg) + "\"" + profile.getProfileName() + "\"?")
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mProfileManager.deleteProfile(profile);
					}
				})
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//do nothing
					}
				})
				.create().show();
		}
	}
	
	public boolean isCanDelete(Profile profile){
		SharedPreferences sp = mContext.getSharedPreferences(Constant.PROFILE_SHARE, Context.MODE_PRIVATE);
//		String activeName = sp.getString(Constant.ENABLE, null);
		int active_id = sp.getInt(Constant.ENABLE, -1);
		
		if(profile.isDefault()){//default profile cannot delete
			//default profile cannot delete
			Toast.makeText(mContext, R.string.delete_toast_default, Toast.LENGTH_LONG).show();
			return false;
		}else if (active_id == profile.getId()) {
			//activing profile cannot delete
			Toast.makeText(mContext, R.string.delete_toast_activing, Toast.LENGTH_LONG).show();
			return false;
		}else {
			return true;
		}
	}
	
	public void stopService(){
		Intent intent = new Intent(mContext,ProfileService.class);
		mContext.stopService(intent);
	}
	
	public Cursor getUriCursor(Uri uri){
		if (uri == null) {
			Log.e(TAG, "Uri is null");
			return null;
		}else {
			try {
				Cursor cursor = mContext.getContentResolver().query(uri, 
						new String[] { MediaStore.Audio.Media.TITLE }, null, null,null);
				if (cursor != null && cursor.getCount() > 0) {
					return cursor;
				}else {
					return null;
				}
			} catch (Exception e) {
				return null;
			}
		}
	}
}
