package com.dreamlink.profiles.ui;

import com.dreamlink.profiles.Profile;
import com.dreamlink.profiles.ProfileManager;
import com.dreamlink.profiles.R;
import com.dreamlink.profiles.preference.ProfileNamePreference;

import android.app.ActionBar;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class ProfileMuteFragment extends PreferenceActivity implements OnPreferenceClickListener{
	private static final String TAG = "ProfileMuteFragment";
	
	private Profile mCurrentProfile;
	
	//profile name preference
	private ProfileNamePreference namePreference;
	private CheckBoxPreference mVibratorPreference;
	
	private ProfileManager mProfileManager;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		addPreferencesFromResource(R.xml.profile_mute);
		
		// click the title back
		final ActionBar bar = getActionBar();
		int flags = ActionBar.DISPLAY_HOME_AS_UP;
		int change = bar.getDisplayOptions() ^ flags;
		bar.setDisplayOptions(change, flags);
		
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mCurrentProfile = bundle.getParcelable("Profile");
		}
		
		mProfileManager = ProfileManager.newInstance(this);
		
		//add the gennel setcion
		PreferenceScreen prefset = getPreferenceScreen();
		
		PreferenceGroup generalPref = (PreferenceGroup) prefset.findPreference("profile_name_section");
		if (generalPref != null) {
			generalPref.removeAll();

			// profile.getProfileName
			namePreference = new ProfileNamePreference(this, mCurrentProfile.getProfileName());
			generalPref.addPreference(namePreference);
		}
		
		mVibratorPreference = (CheckBoxPreference) findPreference("vibrate_mute");
		mVibratorPreference.setChecked(mCurrentProfile.getRingVibrator());
		mVibratorPreference.setOnPreferenceClickListener(this);
	}
	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference == mVibratorPreference) {
			mCurrentProfile.setRingVibrator(mVibratorPreference.isChecked());
		}
		return false;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//save here
		if (mCurrentProfile != null) {
			mProfileManager.updateProfile(mCurrentProfile);
		}
	}
}
