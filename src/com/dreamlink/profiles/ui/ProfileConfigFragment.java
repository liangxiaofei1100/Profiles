package com.dreamlink.profiles.ui;

import com.dreamlink.profiles.Constant;
import com.dreamlink.profiles.Profile;
import com.dreamlink.profiles.ProfileManager;
import com.dreamlink.profiles.R;
import com.dreamlink.profiles.preference.ProfileNamePreference;
import com.dreamlink.profiles.preference.ProfileRingtonePreference;
import com.dreamlink.profiles.preference.StreamVolumePreference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

/**Edit Page
 * Support items:
 * 1.Profiles Name
 * 2.Volume set:media volume,incoming call ring volume,notification volume,alarm volume
 * 3.vibrator set:vibrator when incoming call
 * 4.Ring set:incoming call ring,notification ring
 * */
public class ProfileConfigFragment extends PreferenceFragment implements OnPreferenceChangeListener, OnPreferenceClickListener{
	private static final String TAG = "ProfileConfigFragment";
	//profile name preference
	private ProfileNamePreference namePreference;
	//profile volume
	private StreamVolumePreference volumeSetPreference;
	//profile vibrator 
	private CheckBoxPreference vibratorPreference;
	//profile ring setting
	private ProfileRingtonePreference ringTonePreference;
	private ProfileRingtonePreference notificationTonePreference;
	private ProfileRingtonePreference alarmTonePreference;
	
	private ProfileManager mProfileManager;
	public static Profile mCurrentProfile;
	
	public ProfileConfigFragment(Profile profile) {
		mCurrentProfile = profile;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.profile_config);
		
		mProfileManager = ProfileManager.newInstance(getActivity());
		
//		ProfileService.setHandler(configHandler);
		
		//set show menu
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		MenuItem menuItem = menu.add(0, Constant.MENU_DELETE, 1, R.string.delete).setIcon(R.drawable.ic_menu_delete_holo_dark);
		menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM |
                MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case Constant.MENU_DELETE:
			if (isCanDelete()) {
				//do delete this profile
				new AlertDialog.Builder(getActivity()).setTitle(R.string.delete_profile_confirm)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(getResources().getString(R.string.delete_profile_confirm_msg) + "\"" + mCurrentProfile.getProfileName() + "\"?")
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mProfileManager.deleteProfile(mCurrentProfile);
							mCurrentProfile = null;
							getActivity().finish();
						}
					})
					.setNegativeButton(android.R.string.cancel, null)
					.create().show();
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume() {
		super.onResume();
//		ProfileService.setHandler(configHandler);
		
		refreshList();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//save here
		if (mCurrentProfile != null) {
			mProfileManager.updateProfile(mCurrentProfile);
		}
	}
	
	public void refreshList(){
		PreferenceScreen prefset = getPreferenceScreen();
		
		//add the gennel setcion
		PreferenceGroup generalPref = (PreferenceGroup) prefset.findPreference("profile_general_section");
		if (generalPref != null) {
			generalPref.removeAll();
			
			//profile.getProfileName
			namePreference = new ProfileNamePreference(getActivity(), mCurrentProfile.getProfileName());
			namePreference.setOnPreferenceChangeListener(this);
			generalPref.addPreference(namePreference);
		}
		
		//add volume setcion
		PreferenceGroup volumePref = (PreferenceGroup) prefset.findPreference("profile_volume_section");
		if (volumePref != null) {
			volumePref.removeAll();
			
			//profile volume
			volumeSetPreference = new StreamVolumePreference(getActivity());
			volumeSetPreference.setOnPreferenceChangeListener(this);
			volumeSetPreference.setTitle(R.string.volume_setting_section);
			volumePref.addPreference(volumeSetPreference);
		}
		
		//add incalling ring vibrator
		PreferenceGroup vibratorPref = (PreferenceGroup)findPreference("vibrator_section");
		if (vibratorPref != null) {
			vibratorPref.removeAll();
			
			vibratorPreference = (CheckBoxPreference)findPreference("vibrate_when_ringing");
			vibratorPreference.setChecked(mCurrentProfile.getRingVibrator());
			vibratorPreference.setOnPreferenceClickListener(this);
		}
		
		//add ring setting
		PreferenceGroup ringPref = (PreferenceGroup) prefset.findPreference("ring_selection");
		if (ringPref != null) {
			ringPref.removeAll();
			
			ringTonePreference = (ProfileRingtonePreference) findPreference("ringtone");
			ringTonePreference.setShowSilent(false);
			ringTonePreference.setOnPreferenceChangeListener(this);
			if (mCurrentProfile.getRingOverride() != null) {
				updateRingToneNames(mCurrentProfile.getRingOverride(), RingtoneManager.TYPE_RINGTONE);
			}
			
			notificationTonePreference = (ProfileRingtonePreference) findPreference("notificationtone");
			ringTonePreference.setShowSilent(false);
			notificationTonePreference.setOnPreferenceChangeListener(this);
			if (mCurrentProfile.getNotificationOverride() != null) {
				updateRingToneNames(mCurrentProfile.getNotificationOverride(), RingtoneManager.TYPE_NOTIFICATION);
			}
			
			alarmTonePreference = (ProfileRingtonePreference) findPreference("alarmtone");
			alarmTonePreference.setShowSilent(false);
			alarmTonePreference.setOnPreferenceChangeListener(this);
			if (mCurrentProfile.getAlarmOverride() != null) {
				updateRingToneNames(mCurrentProfile.getAlarmOverride(), RingtoneManager.TYPE_ALARM);
			}
			
		}
		
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		if (preference == ringTonePreference) {
			Uri uri = Uri.parse((String)newValue);
			mCurrentProfile.setRingOverride(uri);
			updateRingToneNames(uri, RingtoneManager.TYPE_RINGTONE);
		}else if (preference == notificationTonePreference) {
			Uri uri = Uri.parse((String)newValue);
			mCurrentProfile.setNotificationOverride(uri);
			updateRingToneNames(uri, RingtoneManager.TYPE_NOTIFICATION);
		}else if (preference == alarmTonePreference) {
			Uri uri = Uri.parse((String)newValue);
			mCurrentProfile.setAlarmOverride(uri);
			updateRingToneNames(uri, RingtoneManager.TYPE_ALARM);
		}else if (preference == namePreference) {
//			mCurrentProfile.setProfileName();
		}
		
		return false;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		if (preference == vibratorPreference) {
			mCurrentProfile.setRingVibrator(vibratorPreference.isChecked());
		}
		return false;
	}
	
	public void updateRingToneNames(Uri uri, int type) {
		Context context = getActivity();
		if (context == null)
			return;
		CharSequence summary = context.getString(R.string.ringtone_unkown);
		// Is it a silent ringtone?
		if (uri == null) {
			summary = context.getString(R.string.ringtone_slient);
		} else {
			// Fetch the ringtone title from the media provider
			try {
				Cursor cursor = context.getContentResolver().query(uri, 
						new String[] { MediaStore.Audio.Media.TITLE }, null, null,null);
				if (cursor != null) {
					if (cursor.moveToFirst()) {
						summary = cursor.getString(0);
					}
					cursor.close();
				}
			} catch (SQLiteException sqle) {
				// Unknown title for the ringtone
			}
		}
		
		if (RingtoneManager.TYPE_RINGTONE == type) {
			ringTonePreference.setSummary(summary);
		}else if (RingtoneManager.TYPE_NOTIFICATION == type) {
			notificationTonePreference.setSummary(summary);
		}else if (RingtoneManager.TYPE_ALARM == type) {
			alarmTonePreference.setSummary(summary);
		}
	}
	
	private boolean isCanDelete(){
		SharedPreferences sp = getActivity().getSharedPreferences(Constant.PROFILE_SHARE, Context.MODE_PRIVATE);
//		String activeName = sp.getString(Constant.ENABLE, null);
		int active_id = sp.getInt(Constant.ENABLE, -1);
		
		if(mCurrentProfile.isDefault()){//default profile cannot delete
			//default profile cannot delete
			Toast.makeText(getActivity(), R.string.delete_toast_default, Toast.LENGTH_LONG).show();
			return false;
		}else if (active_id == mCurrentProfile.getId()) {
			//activing profile cannot delete
			Toast.makeText(getActivity(), R.string.delete_toast_activing, Toast.LENGTH_LONG).show();
			return false;
		}else {
			return true;
		}
	}
	
	/**@unuse*/
	public static final int CMSG_DELETED = 0x00;
	private final Handler configHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CMSG_DELETED:
//				Intent intent = new Intent(getActivity(), ProfileListActivity.class);
//				startActivity(intent);
//				getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
}
