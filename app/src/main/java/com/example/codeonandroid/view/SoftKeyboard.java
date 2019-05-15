package com.example.codeonandroid.view;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class SoftKeyboard {
	public static void hide(Context context, View view) {
		if (context != null && view != null) {
			((InputMethodManager) context.getSystemService(
					Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
							view.getWindowToken(),
							0);
		}
	}
}
