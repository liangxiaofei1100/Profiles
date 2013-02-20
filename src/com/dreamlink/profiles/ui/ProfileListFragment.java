package com.dreamlink.profiles.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dreamlink.profiles.Constant;
import com.dreamlink.profiles.Profile;
import com.dreamlink.profiles.ProfileManager;
import com.dreamlink.profiles.ProfileService;
import com.dreamlink.profiles.R;
import com.dreamlink.profiles.preference.ProfilesPreference;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * Main Fragment,view Profile list
 * @author yuri
 */
public class ProfileListFragment extends PreferenceFragment implements OnPreferenceChangeListener {
	private static final String TAG = "ProfileFragment";
	
	private ProfileManager mProfileManager;
	
	/**temp save list,mDefaultProfiles:save default mode profiles*/
	public static Map<String, Profile> mDefaultProfiles;
	/**temp save list,mDefaultProfiles:save custom mode profiles*/
	public static Map<String, Profile> mCustomProfiles;
	public static Profile[] defaultProfiles = null;
	public static Profile[] customProfiles = null;
	
	private SharedPreferences sp = null;
	private Context mContext;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.main_list);
		
		mDefaultProfiles = new HashMap<String, Profile>();
		mCustomProfiles = new HashMap<String, Profile>();
		
		mContext = getActivity();
		sp = getActivity().getSharedPreferences(Constant.PROFILE_SHARE, Context.MODE_PRIVATE);
		
		mProfileManager = ProfileManager.newInstance(mContext);
		
//		ProfileService.setHandler(mHandler);
		
		setHasOptionsMenu(true);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		
		ProfileService.setHandler(mHandler);
		
		mProfileManager.queryRecord();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem resetItem = menu.add(0, Constant.MENU_RESET, 1, R.string.reset).setIcon(R.drawable.ic_settings_backup);
    	resetItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM |
                MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    	
    	MenuItem addItem = menu.add(0, Constant.MENU_ADD, 1, R.string.add).setIcon(R.drawable.ic_menu_add);
    	addItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM |
                MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Constant.MENU_ADD:
			// add a new profile
			createAddProfileDialog();
			break;
		case Constant.MENU_RESET:
			// do reset operator
			new AlertDialog.Builder(mContext)
				.setTitle(R.string.reset_profile_confirm)
				.setMessage(R.string.reset_profile_confirm_msg)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mProfileManager.initList();
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.create().show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**create add new profile dialog*/
	private int select_position = 0;//默认选中第一个
	public void createAddProfileDialog(){
		select_position = 0;
		LayoutInflater factory = LayoutInflater.from(mContext);
		final View addProfileView = factory.inflate(R.layout.profile_dialog, null);
		final EditText editText = (EditText) addProfileView.findViewById(R.id.name_edit);
		final TextView textView = (TextView) addProfileView.findViewById(R.id.error_tip_text);
		
		//情景模式图标选择view
		final GridView gridView = (GridView)addProfileView.findViewById(R.id.icon_view);
		final IconAdapter adapter = new IconAdapter();
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				adapter.setPosition(position);
				select_position = position;
				adapter.notifyDataSetChanged();
			}
		});
		
		final Builder alertDialog = new AlertDialog.Builder(mContext);
		alertDialog.setTitle(R.string.add_profile_title);
		alertDialog.setView(addProfileView);
		alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				/* User clicked OK so do some stuff */
				String name = editText.getText().toString().trim();
				if (name.equals("")) {
					setDialogDismiss(dialog, false);
					textView.setVisibility(View.VISIBLE);
					textView.setText(R.string.profile_name_cannot_null);
				}else if (isNameExist(name)) {
					//if the name is exist,do not close the alert dialog
					setDialogDismiss(dialog, false);
					textView.setVisibility(View.VISIBLE);
					textView.setText(R.string.profile_name_exist);
				}else {
					addNewProfile(name,adapter.mThumbIds[select_position]);
					setDialogDismiss(dialog, true);
				}
			}
		});
		alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				setDialogDismiss(dialog, true);
			}
		});
		alertDialog.create();
		alertDialog.show();
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
		for(Profile profile:defaultProfiles){
			if (name.equals(profile.getProfileName())) {
				return true;
			}
		}
		
		if (customProfiles != null) {
			for(Profile profile:customProfiles){
				if (name.equals(profile.getProfileName())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**add new profile*/
	public void addNewProfile(String name, int icon_id){
		Profile profile = new Profile(name);
		//set default config
		profile.setMediaVolume(Constant.MEDIA_VOLUME);
		profile.setRingVolume(Constant.RING_VOLUME);
		profile.setNotificationVolume(Constant.NOTIFICATION_VOLUME);
		profile.setAlarmVolume(Constant.ALARM_VOLUME);
		profile.setRingVibrator(Constant.RING_VIBRATOR);
//		profile.setRingOverride(Constant.RINGTONE);
//		profile.setNotificationOverride(Constant.NOTIFICATIONTONE);
		profile.setRingOverride(mProfileManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
		profile.setNotificationOverride(mProfileManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		profile.setAlarmOverride(mProfileManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
		profile.setDefault(Constant.DEFAULT);
		profile.setIcon(icon_id);
		
		//put into lists
		mCustomProfiles.put(profile.getId() + "", profile);
		
		//save to db
		mProfileManager.insertProfile(profile);
		
		mProfileManager.queryRecord();
//		//start sub fragment
//		Bundle bundle = new Bundle();
//    	Intent intent = new Intent(mContext,ProfileConfigActivity.class);
//    	bundle.putParcelable("Profile", profile);
//    	intent.putExtras(bundle);
//    	
//    	mContext.startActivity(intent);
//    	getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	/**update list*/
	public void refreshList() {
		ProfilesPreference ppref;
		// String enable_name = sp.getString(Constant.ENABLE, null);
		int active_id = sp.getInt(Constant.ENABLE, -1);
//		System.out.println("active_id = " + active_id);

		// show default mode profiles in main list
		PreferenceGroup defaultGroup = (PreferenceGroup) findPreference("default_profiles");
		if (defaultGroup != null) {
			defaultGroup.removeAll();

			for (Profile profile : defaultProfiles) {
				Bundle args = new Bundle();
				args.putParcelable("Profile", profile);

				// use custom preference
				ppref = new ProfilesPreference(ProfileListFragment.this, args);
				ppref.setKey(profile.getId() + "");// use profile id for key
				ppref.setTitle(profile.getProfileName());
				ppref.setPersistent(false);
				ppref.setOnPreferenceChangeListener(this);
				ppref.setSelectable(true);
				ppref.setEnabled(true);
				ppref.setIcon(profile.getIcon());
				//
				if (active_id == -1) {
					String defaultProfile = getResources().getString(R.string.profileNameDeafult);
					if (profile.getProfileName().equals(defaultProfile)) {
						ppref.setChecked(true);

						// set the profile active
						Editor editor = sp.edit();
						editor.putInt(Constant.ENABLE, profile.getId());
						editor.commit();

						// do apply
						mProfileManager.setActiveProfille(profile, mContext);
					}
				} else if (active_id == profile.getId()) {
					ppref.setChecked(true);
				}

				defaultGroup.addPreference(ppref);
			}
		} else {
			Log.e(TAG, "default is null");
		}

		// //show default mode profiles in main list
		PreferenceGroup customGroup = (PreferenceGroup) findPreference("custom_profiles");
		if (customGroup != null) {
			customGroup.removeAll();

			if (customProfiles != null) {
				customGroup.setTitle(R.string.custom_profiles);

				for (Profile profile : customProfiles) {
					Bundle args = new Bundle();
					args.putParcelable("Profile", profile);

					// use custom preference
					ppref = new ProfilesPreference(ProfileListFragment.this, args);
					ppref.setKey(profile.getId() + "");// use profile name for
														// key
					ppref.setTitle(profile.getProfileName());
					ppref.setPersistent(false);
					ppref.setOnPreferenceChangeListener(this);
					ppref.setSelectable(true);
					ppref.setEnabled(true);
					ppref.setIcon(profile.getIcon());

					if (active_id == profile.getId()) {
						ppref.setChecked(true);
					}

					customGroup.addPreference(ppref);
				}
			} else {
				// if custom profiles is null ,invisible customGroup
				customGroup.setTitle("");
			}
		} else {
			Log.e(TAG, "custom is null");
		}
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (newValue instanceof String) {
			//save the selected profile name into sharedpreference enable
			Editor editor = sp.edit();
			Profile profile = null;
			if (mDefaultProfiles.get(newValue) != null) {
				profile = mDefaultProfiles.get(newValue);
			}else if (mCustomProfiles.get(newValue) != null) {
				profile = mCustomProfiles.get(newValue);
			}else {
				Log.e(TAG, "error in onPreferenceChange");
				return false;
			}
			
			editor.putInt(Constant.ENABLE, profile.getId());
			editor.commit();
			
			mProfileManager.setActiveProfille(profile, mContext);
		}
		return true;
	}
	
	public static final int MSG_INIT_OVER = 0x00;
	public static final int MSG_GET_PROFILES = 0x01;
	public static final int MSG_UPDATE_PROFILE = 0x02;
	public static final int MSG_SET_ACTIVE_PROFILE = 0x03;
	public static final int MSG_QUERY_OVER = 0x04;
	public static final int MSG_UPDATE_OVER = 0x05;
	public static final int MSG_DELETE_OVER = 0x06;
	private final Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_INIT_OVER:
				mProfileManager.queryRecord();
				break;
				
			case MSG_DELETE_OVER:
				mProfileManager.getProfiles();
				break;
				
			case MSG_GET_PROFILES:
			case MSG_SET_ACTIVE_PROFILE:
				refreshList();
				break;
			case MSG_QUERY_OVER:
				if (mDefaultProfiles.size() > 0) {
					mProfileManager.getProfiles();
				}else {
					mProfileManager.initList();
				}
				break;
			default:
				break;
			}
		}
	};
	
	public class IconAdapter extends BaseAdapter{
		private int select_pos = 0;//默认选中第一个
		
		public void setPosition(int pos){
			select_pos = pos;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mThumbIds.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(90, 90));
                imageView.setAdjustViewBounds(false);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }
            
            if (position == select_pos) {
				imageView.setBackgroundColor(getResources().getColor(R.color.bac_color));
			}else {
				imageView.setBackgroundColor(Color.TRANSPARENT);
			}
            
            imageView.setImageResource(mThumbIds[position]);
            return imageView;
		}
		
		public final Integer[] mThumbIds = {
				R.drawable.biaozhun, R.drawable.huiyi,
				R.drawable.slient, R.drawable.out,
				R.drawable.profile_icon_1, R.drawable.profile_icon_2,
				R.drawable.profile_icon_3, R.drawable.profile_icon_4
		};
	}

}
