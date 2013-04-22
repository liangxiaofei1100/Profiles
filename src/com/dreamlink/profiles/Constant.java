package com.dreamlink.profiles;

import com.dreamlink.profiles.data.ProfilesMetaData;

import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;

/**define constant here*/
public class Constant {
	/**set debug mode*/
	public static final boolean DEBUG = true;
	
	/**profile default config*/
	public static final int MEDIA_VOLUME = 7;//max is 15
	public static final int RING_VOLUME = 3;//max is 7
	public static final int NOTIFICATION_VOLUME = 3;//max is 7
	public static final int ALARM_VOLUME = 3;//max is 7
	public static final boolean RING_VIBRATOR = false;
	public static final Uri RINGTONE = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
	public static final Uri NOTIFICATIONTONE =RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	public static final Uri ALARMTONE = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
	public static final boolean DEFAULT = false;//default profile or not
	
	/**database columns*/
	public static final String columns[] = new String[] { 
			ProfilesMetaData.Profiles._ID, 
			ProfilesMetaData.Profiles.NAME, 
			ProfilesMetaData.Profiles.DEFAULT,
			ProfilesMetaData.Profiles.MEDIA_VOLUME,
			ProfilesMetaData.Profiles.RINGTONE_VOLUME,
			ProfilesMetaData.Profiles.NOTIFICATION_VOLUME,
			ProfilesMetaData.Profiles.AlARM_VOLUME,
			ProfilesMetaData.Profiles.RING_VIBRATION,
			ProfilesMetaData.Profiles.RINGTONE,
			ProfilesMetaData.Profiles.NOTIFICATION_TONE,
			ProfilesMetaData.Profiles.ALARM_TONE,
			ProfilesMetaData.Profiles.ICON_ID
	};
	
	/**service action*/
	public static final String INIT_ACTION = "com.dreamlink.profiles.INIT_ACTION";
	public static final String GET_PROFILES = "com.dreamlink.profiles.GET_PROFILES";
	public static final String UPDATE_PROFILE = "com.dreamlink.profiles.UPDATE_PROFILE";
	public static final String SET_ACTIVE_PROFILE = "com.dreamlink.profiles.SET_ACTIVE_PROFILE";
	public static final String QUERY_RECORD = "com.dreamlink.profiles.QUERY_RECORD";
	public static final String UPDATE_RECORD = "com.dreamlink.profiles.UPDATE_RECORD";
	public static final String INSERT_RECORD = "com.dreamlink.profiles.INSERT_RECORD";
	public static final String DELETE_RECORD = "com.dreamlink.profiles.DELETE_RECORD";
	
	/***/
	public static final String VIBRATE_WHEN_RINGING = "vibrate_when_ringing";
	
	/**use SharedPreference to save the active profile name*/
	public static final String PROFILE_SHARE = "profile_share";
	/**which profile is enable/active. Type:int*/
	public static final String ENABLE= "enable";
	/**save active profile name. Type:String*/
	public static final String ACTIVE_NAME = "active_name";
	/**save active profile icon. Type:int*/
	public static final String ACTIVE_ICON_ID = "active_icon_id";
	/**save the default ringtone uri. Type:String*/
	public static final String DEFAULT_RINGTONE_URI = "ringtone";
	/**save the default notification uri. Type:String*/
	public static final String DEFAULT_NOTIFICATION_URI = "notification";
	/**save the default alarm uri. Type:String*/
	public static final String DEFAULT_ALARM_URI = "alarm";
	
	/**default config doc*/
	public static final String PROFILE_DEFAULT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/profile_default.xml";
	
	/**menu*/
	public static final int MENU_ADD = 0x00;
	public static final int MENU_DELETE = 0x01;
	public static final int MENU_RESET = 0x02;
	public static final int MENU_ABOUT = 0x03;
	
	/**broadcast action*/
	public static final String ACTION_APP_WIDGET_UPDATE = "com.dreamlink.profiles.appWidgetUpdate";
	
}
