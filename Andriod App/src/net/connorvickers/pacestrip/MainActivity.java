package net.connorvickers.pacestrip;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MainActivity extends SherlockFragmentActivity {

	Fragment Solid;
	public DBAdapter myDb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// initalize actionbar
		ActionBar actionbar = getSupportActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionbar.setDisplayShowTitleEnabled(false);
		actionbar.setDisplayShowHomeEnabled(false);
		ActionBar.Tab Frag1Tab = actionbar.newTab().setText("Pace");
		ActionBar.Tab Frag2Tab = actionbar.newTab().setText("Solid");
		ActionBar.Tab Frag3Tab = actionbar.newTab().setText("Settings");
		// initalize tabs
		Fragment Pace = new Pace();
		Solid = new Solid();
		Fragment Settings = new Settings();
		Frag1Tab.setTabListener(new MyTabsListener(Pace));
		Frag2Tab.setTabListener(new MyTabsListener(Solid));
		Frag3Tab.setTabListener(new MyTabsListener(Settings));
		actionbar.addTab(Frag1Tab);
		actionbar.addTab(Frag2Tab);
		actionbar.addTab(Frag3Tab);
		// initalize database
		myDb = new DBAdapter(this);
		myDb.open();
		// initalize key value set
		SharedPreferences settings = getSharedPreferences(// need this.get ?
				getString(R.string.settingsFile), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();

		editor.putFloat(getString(R.string.spaceBetweenLEDs), 15);
		editor.putFloat(getString(R.string.distanceWallToFirstLED), 7.5f);
		editor.putFloat(getString(R.string.locationFarWall), 900);
		editor.putInt(getString(R.string.numLEDs), 60);
		editor.putInt(getString(R.string.milliSecBetwSwim), 5000);
		//editor.putInt(getString(R.string.mode), 0);// 0 = off 1 = pace 2 = solid
		//editor.putInt(getString(R.string.solidColor), 0);
		editor.commit();
		Log.w("test", "hi");

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		myDb.close();
	}

	class MyTabsListener implements ActionBar.TabListener {
		public Fragment fragment;

		public MyTabsListener(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.replace(R.id.fragment_container, fragment);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {

		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {

		}
	}
}
