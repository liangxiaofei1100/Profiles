package com.dreamlink.profiles.preference;

import com.dreamlink.profiles.Constant;
import com.dreamlink.profiles.R;
import com.dreamlink.profiles.ui.ProfileConfigFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

/**custome preference for volume*/
public class StreamVolumePreference extends Preference implements
        CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private boolean mProtectFromCheckedChange = false;

    final static String TAG = "StreamVolumePreference";
    
    //Steam type
    private int mStreamType = -1;
    
    private String mLabel = null;
    private int mValue = -1;
    
//    private Profile profile;

    private SeekBar mBar;
    
    private Context mContext;
    
    public static SeekBarVolumizer mMediaBarVolumizer;
    public static SeekBarVolumizer mRingBarVolumizer;
//    public static SeekBarVolumizer mNotificationBarVolumizer;
    public static SeekBarVolumizer mAlarmBarVolumizer;
    
    private static int mediaVolume;
    private static int ringVolume;
    private static int notificationVolume;
    private static int alarmVolume;

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public StreamVolumePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    /**
     * @param context
     * @param attrs
     */
    public StreamVolumePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    /**
     * @param context
     */
    public StreamVolumePreference(Context context) {
        super(context);
        mContext = context;
        init();
    }
    
    public void setStreamType(int type){
    	this.mStreamType = type;
    }
    
    public int getStreamType(){
    	return this.mStreamType;
    }

    @Override
    public View getView(View convertView, ViewGroup parent) {
        View view = super.getView(convertView, parent);
        
//        profile = ProfileConfigFragment.mCurrentProfile;
        
        View textLayout = view.findViewById(R.id.text_layout);
        if ((textLayout != null) && textLayout instanceof LinearLayout) {
            textLayout.setOnClickListener(this);
        }

        return view;
    }

    private void init() {
        setLayoutResource(R.layout.preference_streamvolume);
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mProtectFromCheckedChange) {
            return;
        }
    }

    protected Dialog createVolumeDialog() {
    	final AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
    	
    	SharedPreferences sp = mContext.getSharedPreferences(Constant.PROFILE_SHARE, Context.MODE_PRIVATE);
    	final int active_id = sp.getInt(Constant.ENABLE, -1);
    	//save system volume before change
    	mediaVolume  = am.getStreamVolume(AudioManager.STREAM_MUSIC);
    	ringVolume  = am.getStreamVolume(AudioManager.STREAM_RING);
    	notificationVolume  = am.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
    	alarmVolume  = am.getStreamVolume(AudioManager.STREAM_ALARM);
//    	Log.d(TAG, "Debug before:" + "media:" + mediaVolume +"\n"
//    			+ "ring:" + ringVolume + "\n"
//    			+ "notification:" + notificationVolume + "\n"
//    			+ "alarm:" + alarmVolume);
//    	
//    	Log.e(TAG, "1-->" + ProfileConfigFragment.mCurrentProfile.getMediaVolume() + "\n"
//    			+ "2-->" + ProfileConfigFragment.mCurrentProfile.getRingVolume() + "\n"
//    			+ "3-->" + ProfileConfigFragment.mCurrentProfile.getNotificationVolume() + "\n"
//    			+ "4-->" + ProfileConfigFragment.mCurrentProfile.getAlarmVolume());
    	LayoutInflater inflater = LayoutInflater.from(getContext());
    	final View view = inflater.inflate(R.layout.streamvolume_alertdialog, null);
    	
    	//set media seekbar
    	final SeekBar mediaBar = (SeekBar)view.findViewById(R.id.seekbar_media_v);
        mediaBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mediaBar.setProgress(ProfileConfigFragment.mCurrentProfile.getMediaVolume());
        mMediaBarVolumizer = new SeekBarVolumizer(getContext(), mediaBar, AudioManager.STREAM_MUSIC, getMediaVolumeUri(getContext()));
        
        //set ring seekbar
    	final SeekBar ringBar = (SeekBar)view.findViewById(R.id.seekbar_ring_v);
    	ringBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_RING));
    	ringBar.setProgress( ProfileConfigFragment.mCurrentProfile.getRingVolume());
    	mRingBarVolumizer = new SeekBarVolumizer(getContext(), ringBar, AudioManager.STREAM_RING,  ProfileConfigFragment.mCurrentProfile.getRingOverride());
    	
    	//set notification seekbar
//    	final SeekBar notificationBar = (SeekBar)view.findViewById(R.id.seekbar_notification_v);
//    	notificationBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION));
//    	notificationBar.setProgress( ProfileConfigFragment.mCurrentProfile.getNotificationVolume());
//    	mNotificationBarVolumizer = new SeekBarVolumizer(getContext(), notificationBar, AudioManager.STREAM_NOTIFICATION,  ProfileConfigFragment.mCurrentProfile.getNotificationOverride());
    	
    	//set alarm seekbar
    	final SeekBar alarmBar = (SeekBar)view.findViewById(R.id.seekbar_alarm_v);
    	alarmBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_ALARM));
    	alarmBar.setProgress( ProfileConfigFragment.mCurrentProfile.getAlarmVolume());
    	mAlarmBarVolumizer = new SeekBarVolumizer(getContext(), alarmBar, AudioManager.STREAM_ALARM);
    	
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.volume_setting_section);
        builder.setView(view);
        
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            	int mediaValue = mediaBar.getProgress();
            	int ringValue = ringBar.getProgress();
//            	int notificationValue = notificationBar.getProgress();
            	int alarmValue = alarmBar.getProgress();
            	
            	//
//            	Log.d(TAG, "Debug after:" + "media:" + am.getStreamVolume(AudioManager.STREAM_MUSIC) +"\n"
//            			+ "ring:" + am.getStreamVolume(AudioManager.STREAM_RING) + "\n"
//            			+ "notification:" + am.getStreamVolume(AudioManager.STREAM_NOTIFICATION) + "\n"
//            			+ "alarm:" + am.getStreamVolume(AudioManager.STREAM_ALARM));
            	//judge is active?
            	if (active_id != ProfileConfigFragment.mCurrentProfile.getId()) {
            		Log.d(TAG, "It is not active");
            		am.setStreamVolume(AudioManager.STREAM_MUSIC, mediaVolume, 0);
            		am.setStreamVolume(AudioManager.STREAM_RING, ringVolume, 0);
            		am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, notificationVolume, 0);
            		am.setStreamVolume(AudioManager.STREAM_ALARM, alarmVolume, 0);
				}
            	
//            	Log.e(TAG, "1-->" + mediaValue + "\n"
//            			+ "2-->" + ringValue + "\n"
//            			+ "3-->" + notificationValue + "\n"
//            			+ "4-->" + alarmValue);
                //save
            	ProfileConfigFragment.mCurrentProfile.setMediaVolume(mediaValue);
            	ProfileConfigFragment.mCurrentProfile.setRingVolume(ringValue);
            	ProfileConfigFragment.mCurrentProfile.setNotificationVolume(ringValue);
            	ProfileConfigFragment.mCurrentProfile.setAlarmVolume(alarmValue);
				
				releaseVolumizer();
            }
        });
//        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				releaseVolumizer();
//			}
//		});
        return builder.create();
    }

    @Override
    public void onClick(android.view.View v) {
        if ((v != null) && (R.id.text_layout == v.getId())) {
            createVolumeDialog().show();
        }
    }
    
    private Uri getMediaVolumeUri(Context context){
    	return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
    			+ context.getPackageName()
    			+ "/"
    			+ R.raw.media_volume);
    }
    
    /**release volumizer res*/
    public void releaseVolumizer(){
    	if (mMediaBarVolumizer != null) {
			mMediaBarVolumizer.stop();
			mMediaBarVolumizer = null;
		}
    	
    	if (mRingBarVolumizer != null) {
    		mRingBarVolumizer.stop();
    		mRingBarVolumizer = null;
		}
    	
//    	if (mNotificationBarVolumizer != null) {
//    		mNotificationBarVolumizer.stop();
//    		mNotificationBarVolumizer = null;
//		}
    	
    	if (mAlarmBarVolumizer != null) {
    		mAlarmBarVolumizer.stop();
    		mAlarmBarVolumizer = null;
		}
    }
    
}
