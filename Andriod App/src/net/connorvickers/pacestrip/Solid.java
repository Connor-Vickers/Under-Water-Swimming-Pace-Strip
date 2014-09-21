package net.connorvickers.pacestrip;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;

public class Solid extends SherlockFragment {

	private ToggleButton[] buttons;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.solid, container, false);
		buttons = new ToggleButton[7];
		int[] ids = new int[] { R.id.BtnGreen, R.id.BtnBlue, R.id.BtnYellow,
				R.id.BtnWhite, R.id.BtnRed, R.id.BtnMagenta, R.id.BtnCyan };

		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = (ToggleButton) view.findViewById(ids[i]);
		}

		final SharedPreferences settings = getActivity().getSharedPreferences(
				getString(R.string.settingsFile), Context.MODE_PRIVATE);

		for (final ToggleButton button : buttons) {
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					for (ToggleButton button : buttons) {
						if (button != (ToggleButton) v)
							button.setChecked(false);
					}
					Log.w("button back", button.getBackground().toString());
					ColorDrawable color = (ColorDrawable) button
							.getBackground();
					SharedPreferences.Editor editor = settings.edit();
					editor.putInt(getString(R.string.solidColor),
							color.getColor());
					editor.putInt(getString(R.string.mode), 2);
					editor.commit();
					Intent intent = new Intent(getActivity(),// Start service
							ContinuousService.class);
					getActivity().startService(intent);

				}
			});
		}
		Button offBut = (Button) view.findViewById(R.id.BtnOff);
		offBut.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Turn others off
				for (ToggleButton button : buttons) {
					button.setChecked(false);
				}
				// turn service off
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt(getString(R.string.mode), 0);
				editor.commit();
			}
		});
		return view;
	}

	@Override
	public void onResume() {//Preserve state
		int[] ids = new int[] { R.id.BtnGreen, R.id.BtnBlue, R.id.BtnYellow,
				R.id.BtnWhite, R.id.BtnRed, R.id.BtnMagenta, R.id.BtnCyan };

		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = (ToggleButton) getView().findViewById(ids[i]);
		}

		final SharedPreferences settings = getActivity().getSharedPreferences(
				getString(R.string.settingsFile), Context.MODE_PRIVATE);

		int preColor = settings.getInt(getString(R.string.solidColor), 0);
		int mode = settings.getInt(getString(R.string.mode), 0);

		for (ToggleButton button : buttons) {
			ColorDrawable color = (ColorDrawable) button.getBackground();
			if (mode == 2 && preColor == color.getColor()) {
				button.setChecked(true);
			} else {
				button.setChecked(false);
			}
		}
		super.onResume();
	}
}
