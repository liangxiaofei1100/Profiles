package com.dreamlink.profiles.preference;

import java.lang.reflect.Field;

import com.dreamlink.profiles.Profile;
import com.dreamlink.profiles.ProfileUtil;
import com.dreamlink.profiles.R;
import com.dreamlink.profiles.ui.ProfileConfigFragment;
import com.dreamlink.profiles.ui.ProfileListFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**custom profile name preference*/
public class ProfileNamePreference extends Preference implements
    View.OnClickListener, Preference.OnPreferenceChangeListener {
    private static final String TAG = "ProfileNamePreference";

    private TextView mNameView;

    private String mName;

    /**
     * @param context
     * @param title
     */
    public ProfileNamePreference(Context context, String name) {
        super(context);
        mName = name.toString();
        init();
    }

    /**
     * @param context
     */
    public ProfileNamePreference(Context context) {
        super(context);
        init();
    }

    @Override
    public void onBindView(View view) {
        super.onBindView(view);

        View namePref = view.findViewById(R.id.name_pref);
        if ((namePref != null) && namePref instanceof LinearLayout) {
            namePref.setOnClickListener(this);
        }

        mNameView = (TextView) view.findViewById(R.id.title);

        updatePreferenceViews();
    }

    private void init() {
        setLayoutResource(R.layout.preference_name);
    }

    public void setName(String name) {
        mName = (name.toString());
        updatePreferenceViews();
    }

    public String getName() {
        return(mName.toString());
    }

    private void updatePreferenceViews() {
        if (mNameView != null) {
            mNameView.setText(mName.toString());
        }
    }

    @Override
    public void onClick(android.view.View v) {
        if (v != null) {
        	if (ProfileConfigFragment.mCurrentProfile == null) {
				return;
			}
        	
        	if (ProfileConfigFragment.mCurrentProfile.isDefault()) {
    			//default profile ,can not rename
        		return;
    		}
        	
            Context context = getContext();
            if (context != null) {
            	LayoutInflater inflater = LayoutInflater.from(context);
            	final View view = inflater.inflate(R.layout.profile_dialog, null);
                final EditText entryEdit = (EditText)view.findViewById(R.id.name_edit);
                entryEdit.setText(mName.toString());
                entryEdit.setPadding(34, 16, 34, 16);
                ProfileUtil.onFocusChange(entryEdit, true);
                
                final TextView tipText = (TextView)view.findViewById(R.id.error_tip_text);
                ProfileUtil.setEditLengthFilter(entryEdit, tipText, 20);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.rename);
                builder.setView(view);
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            	String value = entryEdit.getText().toString().trim();
                                
                                if (value.equals("")) {
                					setDialogDismiss(dialog, false);
                					tipText.setVisibility(View.VISIBLE);
                					tipText.setText(R.string.profile_name_cannot_null);
                				}else if (isNameExist(value) && !value.equals(mName)) {
                					//if the name is exist,do not close the alert dialog
                					setDialogDismiss(dialog, false);
                					tipText.setVisibility(View.VISIBLE);
                					tipText.setText(R.string.profile_name_exist);
                				}else {
                					mName = value;
                					 mNameView.setText(mName);
                                     ProfileConfigFragment.mCurrentProfile.setProfileName(mName);
                					setDialogDismiss(dialog, true);
                				}
                                callChangeListener(this);
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setDialogDismiss(dialog, true);
					}
				});
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }
    
    /**set dialog dismiss or not*/
	private void setDialogDismiss(DialogInterface dialog, boolean dismiss){
		try {
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, dismiss);
			dialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**wheather the new add profile is exist?*/
	public boolean isNameExist(String name){
		for(Profile profile:ProfileListFragment.defaultProfiles){
			if (name.equals(profile.getProfileName())) {
				return true;
			}
		}
		
		if (ProfileListFragment.customProfiles != null) {
			for(Profile profile:ProfileListFragment.customProfiles){
				if (name.equals(profile.getProfileName())) {
					return true;
				}
			}
		}
		return false;
	}

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        callChangeListener(preference);
        return false;
    }
}
