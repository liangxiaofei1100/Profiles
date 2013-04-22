package com.dreamlink.profiles.ui;

import com.dreamlink.profiles.R;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class IconAdapter extends BaseAdapter {
	private int select_pos = 0;//默认选中第一个
	private Context mContext;
	
	public IconAdapter(Context context){
		mContext = context;
	}
	
	public void setPosition(int pos){
		select_pos = pos;
	}
	@Override
	public int getCount() {
		return mThumbIds.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
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
			imageView.setBackgroundColor(mContext.getResources().getColor(R.color.bac_color));
		}else {
			imageView.setBackgroundColor(Color.TRANSPARENT);
		}
        
        imageView.setImageResource(mThumbIds[position]);
        return imageView;
	}
	
	//default icons
	public final Integer[] mThumbIds = {
			R.drawable.biaozhun_light, R.drawable.huiyi_light,
			R.drawable.slient_light, R.drawable.outdoor_light,
			R.drawable.profile_icon_1_light, R.drawable.profile_icon_2_light,
			R.drawable.profile_icon_3_light, R.drawable.profile_icon_4_light
	};
}
