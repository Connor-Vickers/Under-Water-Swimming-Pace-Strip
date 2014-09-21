package net.connorvickers.pacestrip;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class BluethoothSetup extends Activity {
	BluetoothAdapter mBluetoothAdapter;
	ArrayList<BluetoothDevice> pairedDevices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences settings = this.getSharedPreferences(
				getString(R.string.settingsFile), Context.MODE_PRIVATE);
		if(settings.getInt(getString(R.string.mode), -1) > 0){
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.bluetoothsetup);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// TODO Device does not support Bluetooth
		}

		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 0);// TODO if says no
		}

		pairedDevices = new ArrayList<BluetoothDevice>();
		pairedDevices.addAll(BluetoothAdapter.getDefaultAdapter()
				.getBondedDevices());
		ArrayAdapter<BluetoothDevice> mArrayAdapter = new BluetoothDeviceAdapter(
				this, pairedDevices);
		ListView listview = (ListView) findViewById(R.id.bluetoothListView);
		listview.setAdapter(mArrayAdapter);
	}

	private void start(int deviceHash) {
		// TODO if device valid
		// store device id in settings
		SharedPreferences settings = this.getSharedPreferences(
				getString(R.string.settingsFile), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(getString(R.string.deviceHash), deviceHash);
		editor.commit();
		// start main activity
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);

	}

	private class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice> {
		public BluetoothDeviceAdapter(Context context,
				ArrayList<BluetoothDevice> devices) {
			super(context, R.layout.bluetoothdevice, devices);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			BluetoothDevice device = getItem(position);
			View view = LayoutInflater.from(getContext()).inflate(
					R.layout.bluetoothdevice, null);
			TextView Name = (TextView) view.findViewById(R.id.Device_Name);
			Name.setText(device.getName() + "\t" + device.hashCode());
			view.setTag(device.hashCode());
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					start(((Integer) v.getTag()).intValue());
				}
			});
			return view;
		}
	}
}
