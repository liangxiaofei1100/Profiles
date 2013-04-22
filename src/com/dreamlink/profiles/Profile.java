package com.dreamlink.profiles;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;

public class Profile implements Parcelable,Comparable{
	private static final String TAG = "Profile";
	
	/**use this id,you can locate the data that the profile save in db*/
	private int id;
	/**profile name*/
	private String mProfileName = "";
	/**multi media volume*/
	private int mMediaVolume;
	/**call ring volume*/
	private int mRingVolume;
	/**system notification volume*/
	private int mNotificationVolume;
	/**alarm clock volume*/
	private int mAlarmVolume;
	
	/**call ring uri*/
	private Uri mRingOverride = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
	/**system notification uri*/
	private Uri mNotificationOverride = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	/**alarm ringtone uri*/
	private Uri mAlarmOverride = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
	
	/**true:vibrator when incoming tel,false:else*/
	private boolean mRingVibrator = false;
	
	/**true:default profile,false:else*/
	private boolean mDefault = false;
	
	//add a icon for every profile
	/**the drawable id for the icon*/
	private int mIcon;
	
	public static final Parcelable.Creator<Profile> CREATOR = new Parcelable.Creator<Profile>() {
		@Override
		public Profile createFromParcel(Parcel source) {
			return new Profile(source);
		}

		@Override
		public Profile[] newArray(int size) {
			return new Profile[size];
		}
	};
	
	public Profile(String name) {
		this.mProfileName = name;
	}
	
	private Profile(Parcel in){
		readFromParcel(in);
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}

	public String getProfileName() {
		return mProfileName;
	}

	public void setProfileName(String name) {
		this.mProfileName = name;
	}

	public int getMediaVolume() {
		return mMediaVolume;
	}

	public void setMediaVolume(int volume) {
		this.mMediaVolume = volume;
	}

	public int getRingVolume() {
		return mRingVolume;
	}

	public void setRingVolume(int volume) {
		this.mRingVolume = volume;
	}

	public int getNotificationVolume() {
		return mNotificationVolume;
	}

	public void setNotificationVolume(int volume) {
		this.mNotificationVolume = volume;
	}

	public int getAlarmVolume() {
		return mAlarmVolume;
	}

	public void setAlarmVolume(int volume) {
		this.mAlarmVolume = volume;
	}

	public Uri getRingOverride() {
		return mRingOverride;
	}

	public void setRingOverride(Uri ringTone) {
		this.mRingOverride = ringTone;
	}

	public Uri getNotificationOverride() {
		return mNotificationOverride;
	}

	public void setNotificationOverride(Uri notificationTone) {
		this.mNotificationOverride = notificationTone;
	}
	
	public Uri getAlarmOverride(){
		return mAlarmOverride;
	}
	
	public void setAlarmOverride(Uri alarmUri){
		this.mAlarmOverride = alarmUri;
	}

	public boolean getRingVibrator() {
		return mRingVibrator;
	}

	public void setRingVibrator(boolean type) {
		this.mRingVibrator = type;
	}
	
	public boolean isDefault(){
		return mDefault;
	}
	
	public void setDefault(boolean def){
		this.mDefault = def;
	}
	
	public int getIcon(){
		return mIcon;
	}
	
	public void setIcon(int icon){
		this.mIcon = icon;
	}
	
	/**get default config from xml*/
	public static Profile fromXml(XmlPullParser xpp,Context context) throws XmlPullParserException, IOException{
		String value = xpp.getAttributeValue(null,"nameres");
		if(Constant.DEBUG) Log.d(TAG, "value=" + value);
		int profileNameResId = -1;
		String profileName = null;
		if (value != null) {
			profileNameResId = context.getResources().getIdentifier(value, "string", "com.dreamlink.profiles");
			if (profileNameResId > 0) {
				profileName = context.getResources().getString(profileNameResId);
			}
		}
		
		if (profileName == null) {
			profileName = "DEFAULT";
		}
		if(Constant.DEBUG) Log.d(TAG, "profileName=" + profileName);
		
		Profile profile = new Profile(profileName);
		int event = xpp.next();
		while(event != XmlPullParser.END_TAG){
			if (event == XmlPullParser.START_TAG) {
				String name = xpp.getName();
				if(Constant.DEBUG) Log.d(TAG, "name=" + name);
				if (name.equals("media_volume")) {
					profile.setMediaVolume(Integer.valueOf(xpp.nextText()));
				}else if (name.equals("ring_volume")) {
					profile.setRingVolume(Integer.valueOf(xpp.nextText()));
				}else if (name.equals("notification_volume")) {
					profile.setNotificationVolume(Integer.valueOf(xpp.nextText()));
				}else if (name.equals("clock_volume")) {
					profile.setAlarmVolume(Integer.valueOf(xpp.nextText()));
				}else if (name.equals("ringtone")) {
					//ringtone use system default uri
//					profile.setRingOverride(Uri.parse(xpp.nextText()));
					if (ProfileUtil.ringtone_uri != null) {
						profile.setRingOverride(ProfileUtil.ringtone_uri);
						xpp.nextText();
					}else {
						profile.setRingOverride(Uri.parse(xpp.nextText()));
					}
				}else if (name.equals("notificationtone")) {
//					profile.setNotificationOverride(Uri.parse(xpp.nextText()));
					if (ProfileUtil.notification_uri != null) {
						profile.setNotificationOverride(ProfileUtil.notification_uri);
						xpp.nextText();
					}else {
						profile.setNotificationOverride(Uri.parse(xpp.nextText()));
					}
				}else if (name.equals("alarmtone")) {
//					profile.setAlarmOverride(Uri.parse(xpp.nextText()));
					if (ProfileUtil.alarm_uri != null) {
						profile.setAlarmOverride(ProfileUtil.alarm_uri);
						xpp.nextText();
					}else {
						profile.setAlarmOverride(Uri.parse(xpp.nextText()));
					}
				}else if (name.equals("ringvibrator")) {
					profile.setRingVibrator(xpp.nextText().equals("0")? false : true);
				}else if (name.equals("default")) {
					profile.setDefault(xpp.nextText().equals("1") ? true : false);
				}else if (name.equals("icon")) {
					String icon_value = xpp.getAttributeValue(null,"nameres");
					int icon_id = -1;
					if (value != null) {
						icon_id = context.getResources().getIdentifier(icon_value, "drawable", "com.dreamlink.profiles");
						profile.setIcon(icon_id);
					}
				}
			}
			event = xpp.next();
		}
		return profile;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(mProfileName);
		dest.writeInt(mMediaVolume);
		dest.writeInt(mRingVolume);
		dest.writeInt(mNotificationVolume);
		dest.writeInt(mAlarmVolume);
		
		dest.writeParcelable(mRingOverride, flags);
		dest.writeParcelable(mNotificationOverride, flags);
		dest.writeParcelable(mAlarmOverride, flags);
		
		dest.writeInt(mRingVibrator? 1 : 0);
		
		dest.writeInt(mDefault ? 1 : 0);
		
		dest.writeInt(mIcon);
	}
	
	public void readFromParcel(Parcel in){
		id = in.readInt();
		mProfileName = in.readString();
		
		mMediaVolume = in.readInt();
		mRingVolume = in.readInt();
		mNotificationVolume = in.readInt();
		mAlarmVolume = in.readInt();
		
		mRingOverride = in.readParcelable(null);
		mNotificationOverride = in.readParcelable(null);
		mAlarmOverride = in.readParcelable(null);
		
		int i = in.readInt();
		if (i == 1) {
			mRingVibrator = true;
		}else if (i == 0) {
			mRingVibrator = false;
		}
		
		mDefault = in.readInt() == 1 ? true : false;
		
		mIcon = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	/** sort profile name*/
	@Override
	public int compareTo(Object another) {
		Profile tmp = (Profile) another;
        if (mProfileName.compareTo(tmp.mProfileName) < 0) {
            return -1;
        } else if (mProfileName.compareTo(tmp.mProfileName) > 0) {
            return 1;
        }
        return 0;
	}
	
	/**
	 * select this profil,let configs apply to system
	 * */
	public void doSelect(Context context){
		//apply to system
		if(Constant.DEBUG) Log.d(TAG, "doSelect=" + mProfileName);
		
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		if (mProfileName.equals(context.getResources().getString(R.string.profileNameSlient))) {
			//静音模式
			if (mRingVibrator) {
				//振动
				vibrate(am);
			}else {
				silent(am);
			}
			return;
		}
		
		//set steam volume
		am.setStreamVolume(AudioManager.STREAM_MUSIC, mMediaVolume, 0);
		am.setStreamVolume(AudioManager.STREAM_RING, mRingVolume, 0);
		am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, mNotificationVolume, 0);
		am.setStreamVolume(AudioManager.STREAM_ALARM, mAlarmVolume, 0);
		
		//set vibrate
		Settings.System.putInt(context.getContentResolver(),Constant.VIBRATE_WHEN_RINGING,
                mRingVibrator ? 1 : 0);
		
		//set ring tone
		RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, mRingOverride);
		RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, mNotificationOverride);
		RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM, mAlarmOverride);
		
	}
	
	// 震动
	protected void vibrate(AudioManager audio) {
		audio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
	}

	// 静音
	protected void silent(AudioManager audio) {
		audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
		audio.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
	}
	
}
