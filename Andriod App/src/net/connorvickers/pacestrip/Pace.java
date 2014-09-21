package net.connorvickers.pacestrip;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;

public class Pace extends SherlockFragment {

	private static final float defaultSpeed = 75f;

	protected static final float STEPVALUE = 1;
	int[] colorIDs = { R.color.Blue, R.color.Cyan, R.color.Green,
			R.color.Magenta, R.color.Red, R.color.White, R.color.Yellow, };
	private ListView listview;
	int nextColor;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.pace, container, false);
		listview = (ListView) view.findViewById(R.id.pacelistview);
		populateListViewFromDB();
		nextColor = 0;

		final SharedPreferences settings = getActivity().getSharedPreferences(
				getString(R.string.settingsFile), Context.MODE_PRIVATE);

		Button addBtn = (Button) view.findViewById(R.id.Add_Btn);
		addBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).myDb.
				// float speed, int direction, int color, float location, int
				// queue
						insertRow(defaultSpeed, colorIDs[nextColor]);
				nextColor = (nextColor + 1) % 7;
				populateListViewFromDB();// refresh
			}
		});

		final ToggleButton startStop = (ToggleButton) view
				.findViewById(R.id.startStop);
		
		startStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = settings.edit();
				if (startStop.isChecked()) {// start service
					Log.w("test", "starting service");
					editor.putInt(getString(R.string.mode), 1);
					editor.commit();
					Intent intent = new Intent(getActivity(),
							ContinuousService.class);
					getActivity().startService(intent);
				} else {// stop service
					Log.w("test", "stopping service");
					editor.putInt(getString(R.string.mode), 0);
					editor.commit();
				}
			}
		});

		return view;
	}
	
	@Override
	public void onResume() {
		final SharedPreferences settings = getActivity().getSharedPreferences(
				getString(R.string.settingsFile), Context.MODE_PRIVATE);
		
		 ToggleButton startStop = (ToggleButton) getView()
				.findViewById(R.id.startStop);
		//preserve state
		if (settings.getInt(getString(R.string.mode), -1) == 1) {
			startStop.setChecked(true);
		}else{
			startStop.setChecked(false);
		}
		Log.w("reloading pace", "mode =" + settings.getInt(getString(R.string.mode), -1));
		super.onResume();
	}

	@SuppressWarnings("deprecation")
	private void populateListViewFromDB() {
		Cursor cursor = ((MainActivity) getActivity()).myDb.getAllRows();

		// Allow activity to manage lifetime of the cursor.
		// DEPRECATED! Runs on the UI thread, OK for small/short queries.
		getActivity().startManagingCursor(cursor);

		listview.setAdapter(new CustomAdapter(getActivity(), cursor, false));
	}

	class CustomAdapter extends CursorAdapter {// adapter to convert cursor from
												// DB to list View

		private LayoutInflater inflater;

		public CustomAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return inflater.inflate(R.layout.dot, parent, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			// Set Delete Button
			Button delButton = (Button) view.findViewById(R.id.Del_Btn);
			delButton.setTag(cursor.getLong(DBAdapter.COL_ROWID));// set tag as
																	// id so can
																	// access
																	// later
			delButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Long id = Long.parseLong(((Button) v).getTag().toString());// get
																				// id
					((MainActivity) getActivity()).myDb.deleteRow(id);// delete
					populateListViewFromDB();// refresh
				}
			});

			// Set Color
			View color = view.findViewById(R.id.Color);
			color.setBackgroundResource(cursor.getInt(DBAdapter.COL_COLOR));

			// Set Plus Button
			Button plusButton = (Button) view.findViewById(R.id.Plus_Btn);
			plusButton.setTag(cursor.getLong(DBAdapter.COL_ROWID));// set tag as
																	// id so can
																	// access
																	// later
			plusButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Long id = Long.parseLong(((Button) v).getTag().toString());// get
																				// id
					float speed = ((MainActivity) getActivity()).myDb
							.getSpeed(id);
					speed += STEPVALUE;
					((MainActivity) getActivity()).myDb.setSpeed(id, speed);
					populateListViewFromDB();// refresh
				}
			});

			// Set Minus Button
			Button minusButton = (Button) view.findViewById(R.id.Minus_Btn);
			minusButton.setTag(cursor.getLong(DBAdapter.COL_ROWID));// set tag
																	// as id so
																	// can
																	// access
																	// later
			minusButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Long id = Long.parseLong(((Button) v).getTag().toString());// get
																				// id
					float speed = ((MainActivity) getActivity()).myDb
							.getSpeed(id);
					speed -= STEPVALUE;
					((MainActivity) getActivity()).myDb.setSpeed(id, speed);
					populateListViewFromDB();// refresh
				}
			});

			// Set Speed
			TextView speed = (TextView) view.findViewById(R.id.Speed);
			float speedValue = (Float) (cursor.getFloat(DBAdapter.COL_SPEED));
			speed.setText(((Float) speedValue).toString());
		}
	}
}