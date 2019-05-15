package com.example.codeonandroid.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.codeonandroid.R;
import com.example.codeonandroid.view.SoftKeyboard;
import com.example.codeonandroid.widget.ShaderEditor;


public class EditorFragment extends Fragment {
	public static final String TAG = "EditorFragment";

	private ScrollView scrollView;
	private ShaderEditor shaderEditor;
	private int yOffset;

	@Override
	public View onCreateView(
			LayoutInflater inflater,
			ViewGroup container,
			Bundle state) {
		View view = inflater.inflate(
				R.layout.fragment_editor,
				container,
				false);

		scrollView = view.findViewById(R.id.scroll_view);
		shaderEditor = view.findViewById(R.id.editor);

		Activity activity = getActivity();
		try {
			shaderEditor.setOnTextChangedListener(
					(ShaderEditor.OnTextChangedListener) activity);
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() +
					" must implement " +
					"ShaderEditor.OnTextChangedListener");
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	public boolean hasErrorLine() {
		return shaderEditor.hasErrorLine();
	}

	public void clearError() {
		shaderEditor.setErrorLine(0);
	}

	public void updateHighlighting() {
		shaderEditor.updateHighlighting();
	}



	public boolean isModified() {
		return shaderEditor.isModified();
	}

	public String getText() {
		return shaderEditor.getCleanText();
	}

	public void setText(String text) {
		clearError();
		shaderEditor.setTextHighlighted(text);
	}

	public void insertTab() {
		shaderEditor.insertTab();
	}

	public void addUniform(String name) {
		shaderEditor.addUniform(name);
	}

	public boolean isCodeVisible() {
		return scrollView.getVisibility() == View.VISIBLE;
	}

	public boolean toggleCode() {
		boolean visible = isCodeVisible();

		scrollView.setVisibility(visible ? View.GONE : View.VISIBLE);

		if (visible) {
			SoftKeyboard.hide(getActivity(), shaderEditor);
		}

		return visible;
	}



	private int getYOffset(Activity activity) {
		if (yOffset == 0) {
			float dp = getResources().getDisplayMetrics().density;

			try {
				ActionBar actionBar = ((AppCompatActivity) activity)
						.getSupportActionBar();

				if (actionBar != null) {
					yOffset = actionBar.getHeight();
				}
			} catch (ClassCastException e) {
				yOffset = Math.round(48f * dp);
			}

			yOffset += Math.round(16f * dp);
		}

		return yOffset;
	}
}
