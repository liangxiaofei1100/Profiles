package com.dreamlink.profiles.preference;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.RingtonePreference;
import android.util.Log;

/**custom profile ringtone preference*/
public class ProfileRingtonePreference extends RingtonePreference {
	private static final String TAG = "ProfileRingtonePreference";
    private Uri mRingtone;

    public ProfileRingtonePreference(Context context, Uri uri) {
		super(context);
		this.mRingtone = uri;
	}

	@Override
    protected void onPrepareRingtonePickerIntent(Intent ringtonePickerIntent) {
        super.onPrepareRingtonePickerIntent(ringtonePickerIntent);

        /*
         * Since this preference is for choosing the default ringtone, it
         * doesn't make sense to show a 'Default' item.
         */
        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
    }

    public void setRingtone(Uri uri) {
        mRingtone = uri;
    }

    @Override
    protected Uri onRestoreRingtone() {
    	//显示打开对话框后，哪个应该被选中(Open dialog box is displayed, which should be selected)
//    	return ProfileConfigFragment.mCurrentProfile.getRingOverride();
//    	return mRingtone;
        if (mRingtone == null) {
        	Log.e(TAG, "mRingtone is null");
            return super.onRestoreRingtone();
        } else {
            return mRingtone;
        }
    }
}
