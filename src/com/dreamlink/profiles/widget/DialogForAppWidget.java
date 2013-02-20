package com.dreamlink.profiles.widget;

import java.util.ArrayList;

import com.dreamlink.profiles.Profile;
import com.dreamlink.profiles.R;
import com.dreamlink.profiles.ui.ProfileListFragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DialogForAppWidget extends Activity implements OnClickListener {
	private LinearLayout paraentLayout;
	private LinearLayout layout;
	private int num;
	private Profile[] itemArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dialogact);
		paraentLayout = (LinearLayout) findViewById(R.id.linearLayout);
		itemArray = ProfileListFragment.defaultProfiles;

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		int i = 0;
		for (Profile p : ProfileListFragment.defaultProfiles) {
			if (layout != null) {
				if (num < 2) {
					layout.addView(createNewView(p));
					num++;
				} else {
					paraentLayout.addView(layout);
					num = 0;
					layout = new LinearLayout(this);
					layout.setLayoutParams(params);
					layout.addView(createNewView(p));
					num++;
				}
			} else {
				num = 0;
				layout = new LinearLayout(this);
				layout.addView(createNewView(p));
				num++;
			}
		}
		if (ProfileListFragment.customProfiles != null) {
			for (Profile p : ProfileListFragment.customProfiles) {
				if (layout != null) {
					if (num < 2) {
						layout.addView(createNewView(p));
						num++;
					} else {
						paraentLayout.addView(layout);
						num = 0;
						layout = new LinearLayout(this);
						layout.setLayoutParams(params);
						layout.addView(createNewView(p));
						num++;
					}
				} else {
					num = 0;
					layout = new LinearLayout(this);
					layout.addView(createNewView(p));
					num++;
				}
			}
		}
		paraentLayout.addView(layout);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		/*
		 * AppWidgetManager manager = AppWidgetManager.getInstance(this);
		 * ComponentName name = new ComponentName(this, MyWight.class);
		 */
		// Intent it = new Intent("com.wd.appWidgetUpdate");
		// it.putExtra("arbiter_liu", flag);
		// this.sendBroadcast(it);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		Profile p = (Profile) v.getTag();
		p.doSelect(this);
		Intent it = new Intent("com.dreamlink.appWidgetUpdate");
		it.putExtra("arbiter_liu_name", p.getProfileName());
		it.putExtra("arbiter_liu_icon", p.getIcon());
		this.sendBroadcast(it);
		finish();
	}

	private View createNewView(Profile p) {
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setLayoutParams(new LayoutParams(100, 100));
		ImageView view = new ImageView(this);
		view.setImageDrawable(this.getResources().getDrawable(p.getIcon()));
		view.setLayoutParams(params);
		TextView tv = new TextView(this);
		tv.setText(p.getProfileName());
		tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		tv.setGravity(Gravity.CENTER);
		linearLayout.addView(view);
		linearLayout.addView(tv);
		linearLayout.setTag(p);
		linearLayout.setOnClickListener(this);
		return linearLayout;

	}
}
