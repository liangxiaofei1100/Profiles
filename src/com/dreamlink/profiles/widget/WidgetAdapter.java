package com.dreamlink.profiles.widget;

import java.util.ArrayList;
import java.util.Map;

import com.dreamlink.profiles.Profile;
import com.dreamlink.profiles.R;
import com.dreamlink.profiles.data.ProfilesMetaData.Profiles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WidgetAdapter extends BaseAdapter {
	private LayoutInflater mInflater = null;
//	private ArrayList<Map<String, Object>> mList = new ArrayList<Map<String,Object>>();
	private ArrayList<Profile> mList = new ArrayList<Profile>();
	
	private int mSelected_id = -1;
	
	public WidgetAdapter(Context context, ArrayList<Profile> data, int selected_id){
		mInflater = LayoutInflater.from(context);
		
		mList = data;
		
		mSelected_id = selected_id;
		System.out.println("size=" + mList.size());
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class WidgetHolder{
		ImageView iconView;
		TextView nameView;
		View seletedView;
	}
	@Override
	public View getView(int position, View converView, ViewGroup parent) {
		View view = converView;
		WidgetHolder holder = null;
		
		if (view == null) {
			view = mInflater.inflate(R.layout.itemlayout, parent, false);
			holder = new WidgetHolder();
			
			holder.iconView = (ImageView) view.findViewById(R.id.imageView1);
			holder.nameView = (TextView) view.findViewById(R.id.textView1);
			holder.seletedView = view.findViewById(R.id.selected_view);
			
			view.setTag(holder);
		}else {
			holder = (WidgetHolder) view.getTag();
		}
		holder.iconView.setImageResource(mList.get(position).getIcon());
		holder.nameView.setText(mList.get(position).getProfileName());
		
		if (mList.get(position).getId() == mSelected_id ) {
			holder.seletedView.setVisibility(View.VISIBLE);
		}else {
			holder.seletedView.setVisibility(View.INVISIBLE);
		}
		return view;
	}

}
