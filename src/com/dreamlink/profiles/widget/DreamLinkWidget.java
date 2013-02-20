package com.dreamlink.profiles.widget;

import com.dreamlink.profiles.Constant;
import com.dreamlink.profiles.R;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.RemoteViews;

public class DreamLinkWidget extends AppWidgetProvider {
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
		RemoteViews rv = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		rv.setImageViewResource(R.id.update,
				intent.getIntExtra("arbiter_liu_icon", 0));
		rv.setTextViewText(R.id.name, intent.getStringExtra("arbiter_liu_name"));
		// rv.setImageViewResource(R.id.update, 1);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		ComponentName name = new ComponentName(context, DreamLinkWidget.class);
		manager.updateAppWidget(name, rv);
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		RemoteViews rv = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);
		rv.setImageViewResource(R.id.update, R.drawable.alarm_volume);
		Intent intent = new Intent(context, DialogForAppWidget.class);
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		rv.setOnClickPendingIntent(R.id.updateUI, pi);
		appWidgetManager.updateAppWidget(appWidgetIds, rv);

	}

}
