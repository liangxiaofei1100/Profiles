package com.dreamlink.profiles;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.InputFilter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

/**funtions that used*/
public class ProfileUtil {

	public static Uri ringtone_uri;
	public static Uri notification_uri;
	public static Uri alarm_uri;
	
	public static int px2dip(Context context, float pxValue){ 
	    final float scale = context.getResources().getDisplayMetrics().density; 
	    return (int)(pxValue / scale + 0.5f); 
	}
	
	public static void setEditLengthFilter(final EditText editText, final TextView textView, final int max_length) {
		InputFilter[] filters = new InputFilter[1];
		filters[0] = new InputFilter.LengthFilter(max_length) {
			@Override
			public CharSequence filter(CharSequence source, int start, int end, android.text.Spanned dest, int dstart, int dend) {
				int destLen = getCharacterNum(dest.toString());
				int sourceLen = getCharacterNum(source.toString());
				if (destLen + sourceLen > max_length) {
					// when input char too long,show error msg
					textView.setText(R.string.profile_name_too_long);
					textView.setVisibility(View.VISIBLE);
					return "";
				}
				textView.setVisibility(View.GONE);
				return source;
			}
		};
		editText.setFilters(filters);
	}
	
	// get Character num in string(one chinese char = two english char)
	public static int getCharacterNum(final String content) {
		if (null == content || "".equals(content)) {
			return 0;
		} else {
			return (content.length() + getChineseNum(content));
		}
	}

	// return the chinese char count in string
	public static int getChineseNum(String s) {
		int num = 0;
		char[] myChar = s.toCharArray();
		for (int i = 0; i < myChar.length; i++) {
			if ((char) (byte) myChar[i] != myChar[i]) {
				num++;
			}
		}
		return num;
	}
	
	/**
	 * show IME when need by manual
	 * @param view the view that need show IME
	 * @param hasFoucs show or not
	 * */
	public static void onFocusChange(final View view, boolean hasFocus) {
		final boolean isFocus = hasFocus;
		(new Handler()).postDelayed(new Runnable() {
			public void run() {
				InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				if (isFocus) {
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				} else {
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
			}
		}, 500);
	}
	
}
