package com.dreamlink.profiles.widget;

import com.dreamlink.profiles.Constant;
import com.dreamlink.profiles.Profile;
import com.dreamlink.profiles.R;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

public class ProfilesWidget extends AppWidgetProvider {
	private static final String TAG = "ProfilesWidget";
	private SharedPreferences sp;

	@SuppressLint("NewApi")
	@Override
	public void onAppWidgetOptionsChanged(Context context,
			AppWidgetManager appWidgetManager, int appWidgetId,
			Bundle newOptions) {
		// TODO Auto-generated method stub
		super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,
				newOptions);
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
	}

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Profile profile = null;
		
		if (Constant.ACTION_APP_WIDGET_UPDATE.equals(intent.getAction())) {
			if(Constant.DEBUG) Log.d(TAG, "onReceiver update");
			RemoteViews rv = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);
			
			profile = (Profile) intent.getExtras().get("profile");
			rv.setImageViewResource(R.id.update, profile.getIcon());
			rv.setTextViewText(R.id.name, profile.getProfileName());
			
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			ComponentName name = new ComponentName(context,
					ProfilesWidget.class);
			manager.updateAppWidget(name, rv);
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		if(Constant.DEBUG) Log.d(TAG, "onUpdate");
		sp = context.getSharedPreferences(Constant.PROFILE_SHARE, Context.MODE_PRIVATE);
		String active_name = sp.getString(Constant.ACTIVE_NAME, "ÏµÍ³Ä¬ÈÏ");
		int active_icon_id = sp.getInt(Constant.ACTIVE_ICON_ID, R.drawable.biaozhun_light);
		RemoteViews rv = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		
		rv.setImageViewResource(R.id.update, active_icon_id);
		rv.setTextViewText(R.id.name, active_name);
		Intent intent = new Intent(context, ProfilesWidgetActivity.class);
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		rv.setOnClickPendingIntent(R.id.updateUI, pi);
		appWidgetManager.updateAppWidget(appWidgetIds, rv);

	}

}
