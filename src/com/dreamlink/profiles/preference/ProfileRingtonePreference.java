package com.dreamlink.profiles.preference;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.RingtonePreference;
import android.util.AttributeSet;

/**custom profile ringtone preference*/
public class ProfileRingtonePreference extends RingtonePreference {
    private static final String TAG = "ProfileRingtonePreference";

    public ProfileRingtonePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
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

    private Uri mRingtone;

    void setRingtone(Uri uri) {
        mRingtone = uri;
    }

    @Override
    protected Uri onRestoreRingtone() {
        if (mRingtone == null) {
            return super.onRestoreRingtone();
        } else {
            return mRingtone;
        }
    }
}
