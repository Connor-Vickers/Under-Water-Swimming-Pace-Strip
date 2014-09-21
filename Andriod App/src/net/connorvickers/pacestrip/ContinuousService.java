//TODO bluethooth to computer display with proccessings
//then relay to ardunio display on strip
package net.connorvickers.pacestrip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class ContinuousService extends Service {
	public final int REFRESHRATEMILL = 400;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startID) {
		return START_STICKY;
	}

	@SuppressWarnings("deprecation")
	public void onCreate() {
		Log.w("test", "service started");

		// Setup notification
		Intent newIntent = new Intent(this, MainActivity.class);
		PendingIntent pIntent = PendingIntent
				.getActivity(this, 0, newIntent, 0);
		Notification.Builder notification = new Notification.Builder(this)
				.setContentTitle("Light Strip is Running")
				.setContentText("Touch to go to")
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentIntent(pIntent)
				//.setAutoCancel(true)
				;

		// notification.setProgress(0, 0, true);//if want status bar

		// start Foreground
		startForeground(1, notification.getNotification());

		new Thread(new Runnable() {
			public void run() {
				// get hash
				SharedPreferences settings = getSharedPreferences(
						getString(R.string.settingsFile), Context.MODE_PRIVATE);
				int targetHash = settings.getInt(
						getString(R.string.deviceHash), -1);
				// TODO error if -1
				// get device
				BluetoothDevice device = null;
				Set<BluetoothDevice> bondedDevices = BluetoothAdapter
						.getDefaultAdapter().getBondedDevices();
				for (BluetoothDevice tmpDevice : bondedDevices) {
					if (tmpDevice.hashCode() == targetHash)// device we want
						device = tmpDevice;
				}

				// TODO if device null

				// Set up variables
				OutputStream outStream = connect(device);
				Strip strip = new Strip(getApplicationContext());
				int mode = settings.getInt(getString(R.string.mode), -1);

				// loop
				while (mode > 0) {

					long startedAt = System.currentTimeMillis();
					try {
						//Trace.beginSection("updating and sending bytes");
						switch (mode) {
						case 1:// Pace
							outStream.write(strip
									.update(getApplicationContext()));
							break;
						case 2:// Solid
							int hexColor = settings.getInt(getString(R.string.solidColor), 0);
							outStream.write(strip
									.getSolid(hexColor));
							break;
						default:
							// TODO error
							break;
						}

					} catch (IOException e) {
					}
					//Trace.endSection();// updating and sending bytes

					//Trace.beginSection("waiting");
					long elasped = System.currentTimeMillis() - startedAt;
					synchronized (this) {
						try {
							wait(Math.max(0, REFRESHRATEMILL - elasped));
						} catch (Exception e) {
						}
					}
					//Trace.endSection();// waiting

					// update mode
					//Trace.beginSection("Getting mode");
					mode = settings.getInt(getString(R.string.mode), -1);
					//Trace.endSection();// getting mode
					Log.w("test", "mode = " + mode);

				}
				// set all leds to off
				try {
					outStream.write(strip.getSolid(0));
				} catch (IOException e) {
				}
				// Stop
				Log.w("test", "destroying");
				stopSelf();
			}
		}).start();
	}

	private OutputStream connect(BluetoothDevice device) {
		// Use a temporary object that is later assigned to mmSocket,
		// because mmSocket is final
		BluetoothSocket mmSocket = null;
		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			// MY_UUID is the app's UUID string, also used by the server code
			// 00001115-0000-1000-8000-00805f9b34fb <- other possiable uuid
			UUID MY_UUID = UUID
					.fromString("00001101-0000-1000-8000-00805f9b34fb");
			mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
		}

		// Cancel discovery because it will slow down the connection
		// mBluetoothAdapter.cancelDiscovery();
		try {
			// Connect the device through the socket. This will block
			// until it succeeds or throws an exception
			mmSocket.connect();
		} catch (IOException connectException) {
			// Unable to connect; close the socket and get out
			try {
				mmSocket.close();
			} catch (IOException closeException) {
			}
		}
		// InputStream mmInStream = null;
		OutputStream mmOutStream = null;
		try {
			// mmInStream = mmSocket.getInputStream();
			mmOutStream = mmSocket.getOutputStream();
		} catch (IOException e) {
		}
		return mmOutStream;
	}

}