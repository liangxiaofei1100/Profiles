package com.dreamlink.profiles.preference;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * Turns a {@link SeekBar} into a volume control.
 */
public class SeekBarVolumizer implements OnSeekBarChangeListener, Runnable {
	private static final String TAG = "SeekBarVolumizer";
	
	private Context mContext;
	private Handler mHandler = new Handler();
	
	private AudioManager mAudioManager;
	private Ringtone mRingtone;
	private int mStreamType;
	
	private int mLastProgress = -1;
    private SeekBar mSeekBar;
    private int mVolumeBeforeMute = -1;
    private int mOriginalStreamVolume;
    
    private ContentObserver mVolumeObserver = new ContentObserver(mHandler) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (mSeekBar != null && mAudioManager != null) {
                int volume = mAudioManager.getStreamVolume(mStreamType);
                mSeekBar.setProgress(volume);
            }
        }
    };
    
    public SeekBarVolumizer(Context context, SeekBar seekBar, int streamType){
    	this(context, seekBar, streamType, null);
    }
    
    public SeekBarVolumizer(Context context, SeekBar seekBar, int streamType, Uri defaultUri){
    	mContext = context;
    	mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    	mSeekBar = seekBar;
    	mStreamType = streamType;
    	
    	initSeekBar(seekBar, defaultUri);
    }
    
    private void initSeekBar(SeekBar seekBar,Uri defaultUri){
    	seekBar.setMax(mAudioManager.getStreamMaxVolume(mStreamType));
//    	mOriginalStreamVolume = mAudioManager.getStreamVolume(mStreamType);
//    	seekBar.setProgress(mOriginalStreamVolume);
    	seekBar.setOnSeekBarChangeListener(this);
    	
//    	mContext.getContentResolver().registerContentObserver(
//                System.getUriFor(System.VOLUME_SETTINGS[mStreamType]),
//                false, mVolumeObserver);
    	
    	if (defaultUri == null) {
            if (mStreamType == AudioManager.STREAM_RING) {
                defaultUri = Settings.System.DEFAULT_RINGTONE_URI;
            } else if (mStreamType == AudioManager.STREAM_NOTIFICATION) {
                defaultUri = Settings.System.DEFAULT_NOTIFICATION_URI;
            } else {
                defaultUri = Settings.System.DEFAULT_ALARM_ALERT_URI;
            }
        }
    	
    	mRingtone = RingtoneManager.getRingtone(mContext, defaultUri);
    	
    	 if (mRingtone != null) {
             mRingtone.setStreamType(mStreamType);
         }
    }
    
    public void stop() {
        stopSample();
//        mContext.getContentResolver().unregisterContentObserver(mVolumeObserver);
        mSeekBar.setOnSeekBarChangeListener(null);
    }
    
    public void revertVolume() {
        mAudioManager.setStreamVolume(mStreamType, mOriginalStreamVolume, 0);
    }
//
	@Override
	public void run() {
		mAudioManager.setStreamVolume(mStreamType, mLastProgress, 0);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		Log.d(TAG, "onProgressChanged");
		if (!fromUser) {
            return;
        }

        postSetVolume(progress);
	}
	
	void postSetVolume(int progress) {
        // Do the volume changing separately to give responsive UI
        mLastProgress = progress;
        mHandler.removeCallbacks(this);
        mHandler.post(this);
    }

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		if (!isSamplePlaying()) {
            startSample();
        }
	}
	
	public boolean isSamplePlaying() {
        return mRingtone != null && mRingtone.isPlaying();
    }
	
	public void startSample() {
        onSampleStarting(this);
        if (mRingtone != null) {
            mRingtone.play();
        }
    }

    public void stopSample() {
        if (mRingtone != null) {
            mRingtone.stop();
        }
    }
    
    public SeekBar getSeekBar() {
        return mSeekBar;
    }

    public void changeVolumeBy(int amount) {
        mSeekBar.incrementProgressBy(amount);
        if (!isSamplePlaying()) {
            startSample();
        }
        postSetVolume(mSeekBar.getProgress());
        mVolumeBeforeMute = -1;
    }

    public void muteVolume() {
        if (mVolumeBeforeMute != -1) {
            mSeekBar.setProgress(mVolumeBeforeMute);
            startSample();
            postSetVolume(mVolumeBeforeMute);
            mVolumeBeforeMute = -1;
        } else {
            mVolumeBeforeMute = mSeekBar.getProgress();
            mSeekBar.setProgress(0);
            stopSample();
            postSetVolume(0);
        }
    }
    
    public void onSampleStarting(SeekBarVolumizer volumizer){
    	if (StreamVolumePreference.mMediaBarVolumizer != null && 
    			volumizer != StreamVolumePreference.mMediaBarVolumizer) {
    		StreamVolumePreference.mMediaBarVolumizer.stopSample();
		}
    	
    	if (StreamVolumePreference.mRingBarVolumizer != null && 
    			volumizer != StreamVolumePreference.mRingBarVolumizer) {
    		StreamVolumePreference.mRingBarVolumizer.stopSample();
		}
    	
    	if (StreamVolumePreference.mNotificationBarVolumizer != null && 
    			volumizer != StreamVolumePreference.mNotificationBarVolumizer) {
    		StreamVolumePreference.mNotificationBarVolumizer.stopSample();
		}
    	
    	if (StreamVolumePreference.mAlarmBarVolumizer != null && 
    			volumizer != StreamVolumePreference.mAlarmBarVolumizer) {
    		StreamVolumePreference.mAlarmBarVolumizer.stopSample();
		}
    }

//    public void onSaveInstanceState(VolumeStore volumeStore) {
//        if (mLastProgress >= 0) {
//            volumeStore.volume = mLastProgress;
//            volumeStore.originalVolume = mOriginalStreamVolume;
//        }
//    }

//    public void onRestoreInstanceState(VolumeStore volumeStore) {
//        if (volumeStore.volume != -1) {
//            mOriginalStreamVolume = volumeStore.originalVolume;
//            mLastProgress = volumeStore.volume;
//            postSetVolume(mLastProgress);
//        }
//    }

}
