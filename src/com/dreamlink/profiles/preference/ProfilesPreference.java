package com.dreamlink.profiles.preference;

import com.dreamlink.profiles.Profile;
import com.dreamlink.profiles.ProfileManager;
import com.dreamlink.profiles.R;
import com.dreamlink.profiles.ui.ProfileConfigActivity;
import com.dreamlink.profiles.ui.ProfileConfigFragment;
import com.dreamlink.profiles.ui.ProfileListFragment;
import com.dreamlink.profiles.ui.ProfileMuteFragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

/**custom CheckBoxPreference, use it for viewing profiles list*/
public class ProfilesPreference extends CheckBoxPreference {
    private static final String TAG = "ProfilesPreference";
    private static final float DISABLED_ALPHA = 0.4f;
    private final ProfileListFragment mFragment;
    private final Bundle mSettingsBundle;

    /**modify button*/
    private ImageView mProfilesSettingsButton;
    /**view profile name */
    private TextView mTitleText;
    /**@unuse*/
    private TextView mSummaryText;
    private ImageView mImageView;
    private View mProfilesPref;
    
    private ImageView iconView;

    private final OnClickListener mPrefOnclickListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
        	Log.d(TAG, "onClick");
            if (!isEnabled() || isChecked()) {
                return;
            }
            setChecked(true);
            callChangeListener(getKey());
        }
    };
    
    public ProfilesPreference(ProfileListFragment fragment, Bundle settingsBundle) {
        super(fragment.getActivity(), null, R.style.ProfilesPreferenceStyle);
        setLayoutResource(R.layout.preference_profiles);
        setWidgetLayoutResource(R.layout.preference_profiles_widget);
        mFragment = fragment;
        mSettingsBundle = settingsBundle;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        
        mProfilesPref = view.findViewById(R.id.profiles_pref);
        mProfilesPref.setOnClickListener(mPrefOnclickListener);
        
        mProfilesSettingsButton = (ImageView)view.findViewById(R.id.profiles_settings);
        //show the profille name
        mTitleText = (TextView)view.findViewById(android.R.id.title);
        //if you have something to show,you can set here
        mSummaryText = (TextView)view.findViewById(android.R.id.summary);
        mImageView = (ImageView)view.findViewById(android.R.id.icon);

        if (mSettingsBundle != null) {
            mProfilesSettingsButton.setOnClickListener(
                    new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            try {
                                startProfileConfigActivity();
                            } catch (ActivityNotFoundException e) {
                                // If the settings activity does not exist, we can just
                                // do nothing...
                            }
                        }
                    });
            
            mProfilesSettingsButton.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					//on long click show delete menu
					onPopupViewClick(v);
					return true;
				}
			});
            
        }
        if (mSettingsBundle == null) {
            mProfilesSettingsButton.setVisibility(View.GONE);
        } else {
            updatePreferenceViews();
        }
    }
    
    @Override
    public void setIcon(int iconResId) {
    	// TODO Auto-generated method stub
    	super.setIcon(iconResId);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            updatePreferenceViews();
        } else {
            disablePreferenceViews();
        }
    }

    private void disablePreferenceViews() {
        if (mProfilesSettingsButton != null) {
            mProfilesSettingsButton.setEnabled(false);
            mProfilesSettingsButton.setAlpha(DISABLED_ALPHA);
        }
        if (mProfilesPref != null) {
            mProfilesPref.setEnabled(false);
            mProfilesPref.setBackgroundColor(0);
        }
    }

    private void updatePreferenceViews() {
        final boolean checked = isChecked();
        if (mProfilesSettingsButton != null) {
            mProfilesSettingsButton.setEnabled(true);
            mProfilesSettingsButton.setClickable(true);
            mProfilesSettingsButton.setFocusable(true);
        }
        if (mTitleText != null) {
            mTitleText.setEnabled(true);
        }
        if (mSummaryText != null) {
            mSummaryText.setEnabled(checked);
        }
        if (mProfilesPref != null) {
            mProfilesPref.setEnabled(true);
            mProfilesPref.setLongClickable(checked);
            final boolean enabled = isEnabled();
            mProfilesPref.setOnClickListener(enabled ? mPrefOnclickListener : null);
            if (!enabled) {
                mProfilesPref.setBackgroundColor(0);
            }
        }
    }

    private void startProfileConfigActivity() {
    	Profile profile = mSettingsBundle.getParcelable("Profile");
    	Intent intent = new Intent();
    	if (profile.getProfileName().equals(mFragment.getActivity().getResources().getString(R.string.profileNameSlient))) {
    		//静音的话，显示不同的UI
			intent.setClass(mFragment.getActivity(), ProfileMuteFragment.class);
			intent.putExtras(mSettingsBundle);
		}else {
			//open sub fragment
			intent.setClass(mFragment.getActivity(), ProfileConfigActivity.class);
	    	intent.putExtras(mSettingsBundle);
		}
    	mFragment.startActivity(intent);
    	mFragment.getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
    }
    
    //popup menu
    public void onPopupViewClick(View view) {
        PopupMenu popup = new PopupMenu(mFragment.getActivity(), view);
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
            	final Profile profile = mSettingsBundle.getParcelable("Profile");
            	final ProfileManager profileManager = ProfileManager.newInstance(mFragment.getActivity());
            	profileManager.doDeleteProfile(profile);
                return true;
            }
        });
        popup.show();
    }
    
}
