package com.dreamlink.profiles.preference;

import com.dreamlink.profiles.Profile;
import com.dreamlink.profiles.R;
import com.dreamlink.profiles.ui.ProfileConfigActivity;
import com.dreamlink.profiles.ui.ProfileListFragment;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

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
        
//        iconView = (ImageView) view.findViewById(R.id.profile_icon);
//        for(Profile profile : ProfileListFragment.defaultProfiles){
//    		iconView.setImageResource(profile.getIcon());
//    	}
//        for (int i = 0; i < ProfileListFragment.customProfiles.length; i++) {
//			System.out.println("0000000000");
//		}
        
        mProfilesSettingsButton = (ImageView)view.findViewById(R.id.profiles_settings);
        mTitleText = (TextView)view.findViewById(android.R.id.title);
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
    	//open sub fragment
    	Intent intent = new Intent(mFragment.getActivity(),ProfileConfigActivity.class);
    	intent.putExtras(mSettingsBundle);
    	
    	mFragment.startActivity(intent);
    	mFragment.getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
    }
    
}
