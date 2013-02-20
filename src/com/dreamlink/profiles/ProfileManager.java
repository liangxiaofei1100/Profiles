package com.dreamlink.profiles;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

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
//		Log.d(TAG, "updateProfile.name=" + profile.getProfileName() + "\n"
//				+ "updateProfile.id=" + profile.getId() + "\n"
//				+ "updateProfile.mediavolume = " + profile.getMediaVolume());
		Intent intent = new Intent();
		intent.setClass(mContext, ProfileService.class);
		intent.setAction(Constant.UPDATE_PROFILE);
		
		Bundle bundle = new Bundle();
//		bundle.putParcelable("update_profile", profile);
		bundle.putParcelable("profile", profile);
		
		intent.putExtras(bundle);
		
		mContext.startService(intent);
	}
	
//	public void addNewProfile()
	public void insertProfile(Profile profile){
		Intent intent = new Intent();
		intent.setClass(mContext, ProfileService.class);
		intent.setAction(Constant.INSERT_RECORD);
		
		Bundle bundle = new Bundle();
//		bundle.putParcelable("insert_profile", profile);
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
//		bundle.putParcelable("delete_profile", profile);
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
}
