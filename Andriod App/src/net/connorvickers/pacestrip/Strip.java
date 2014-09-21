package net.connorvickers.pacestrip;

import java.util.HashMap;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

public class Strip {

	private LED[] strip;
	private float espsilon;
	private float farWall;
	private long time;
	private DBAdapter myDb;
	private int timeBetween;
	private long nextQueueMove;
	private HashMap<Integer, Dot> map;
	private int queueLength;
	private SharedPreferences settings;

	@SuppressLint("UseSparseArrays")
	public Strip(Context appCon) {
		settings = appCon.getSharedPreferences(
				appCon.getString(R.string.settingsFile), Context.MODE_PRIVATE);

		farWall = settings.getFloat(appCon.getString(R.string.locationFarWall),
				-1f);
		timeBetween = settings.getInt(
				appCon.getString(R.string.milliSecBetwSwim), -1);

		int numLEDs = settings.getInt(appCon.getString(R.string.numLEDs), -1);
		float spaceBetween = settings.getFloat(
				appCon.getString(R.string.spaceBetweenLEDs), -1f);
		float firstOffset = settings.getFloat(
				appCon.getString(R.string.distanceWallToFirstLED), -1f);

		espsilon = spaceBetween / 2;
		// TODO exeption if any == -1

		strip = new LED[numLEDs];
		for (int i = 0; i < strip.length; i++) {
			strip[i] = new LED((i * spaceBetween) + firstOffset);
		}

		map = new HashMap<Integer, Dot>();

		queueLength = 0;

		updateTime();
		nextQueueMove = time + timeBetween;

		myDb = new DBAdapter(appCon);
		myDb.open();

	}

	private void updateTime() {
		time = android.os.SystemClock.elapsedRealtime();
	}

	public byte[] update(Context context) {
		//Trace.beginSection("Updating");
		updateLocation();
		updateColor();
		// int size = strip.length * 4;
		//Trace.beginSection("Generating bytes");
		// return bytes
		byte[] ret = new byte[strip.length * 4];
		for (int i = 0; i < strip.length; i++) {
			ret[i * 4] = (byte) i;// location
			int hexColor = context.getResources().getColor(
					(strip[i].getColor()));
			ret[i * 4 + 1] = (byte) ((hexColor & 0xFF0000) >> 16);// R
			ret[i * 4 + 2] = (byte) ((hexColor & 0xFF00) >> 8);// G
			ret[i * 4 + 3] = (byte) (hexColor & 0xFF);// B
		}
		//Trace.endSection();// Generating bytes
		//Trace.endSection();// Updating
		return ret;
	}

	public byte[] getSolid(int hexColor) {
		byte[] ret = new byte[strip.length * 4];
		for (int i = 0; i < strip.length; i++) {
			ret[i * 4] = (byte) i;// location
			//Log.w("name", (context.getString(R.string.solidColor)));
			//Log.w("settings", settings.toString());
			//Log.w("hexcolor", "" + hexColor);
			ret[i * 4 + 1] = (byte) ((hexColor & 0xFF0000) >> 16);// R
			ret[i * 4 + 2] = (byte) ((hexColor & 0xFF00) >> 8);// G
			ret[i * 4 + 3] = (byte) (hexColor & 0xFF);// B
		}
		Log.w("ret", "" + ret[1] + "," + ret[2] + "," + ret[3] + ",");
		return ret;
	}

	private void updateLocation() {
		//Trace.beginSection("Updating location");
		//Trace.beginSection("setup and getting db");
		// calculate delta time and queue
		long lastTime = time;
		updateTime();
		long deltaTime = time - lastTime;
		boolean decreaseQueue = time >= nextQueueMove;// attempt to decrease
														// queue
		boolean queueMoved = false;
		// get dots
		Cursor cursor = myDb.getAllRows();
		cursor.moveToFirst();
		//Trace.endSection();// setup and getting db
		// loop all dots
		while (!cursor.isAfterLast()) {
			//Trace.beginSection("stepping");

			//Trace.beginSection("getting dot");
			int id = cursor.getInt(DBAdapter.COL_ROWID);// get id
			if (!map.containsKey(id)) {// if dot was added add to map
				map.put(id, new Dot(farWall, queueLength));
				queueLength++;
			}
			//Trace.endSection();// getting dot

			//Trace.beginSection("updating location");
			Dot dot = map.get(id);
			float speed = cursor.getFloat(DBAdapter.COL_SPEED);// get speed
			if (dot.updateLocation(speed, deltaTime, decreaseQueue)) {// queue
																		// was
																		// moved
				queueMoved = true;
			}
			//Trace.endSection();// updating location

			/*
			 * Log.w("log", "Speed: " + speed + "newlocation: " + newlocation +
			 * "newDirection: " + newDirection + "farWall: " + farWall +
			 * "deltaTime: " + deltaTime);
			 */

			//Trace.endSection();// stepping
			cursor.moveToNext();
		}

		if (queueMoved) {
			nextQueueMove = time + timeBetween;
			queueLength--;
		}
		//Trace.endSection();// Updating location
	}

	private void updateColor() {
		//Trace.beginSection("updating color");
		for (LED led : strip) { // set all leds to black
			led.setColor(LED.DEFAULTCOLOR);
		}

		Cursor cursor = myDb.getAllRows();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {// for every dot
			int id = cursor.getInt(DBAdapter.COL_ROWID);
			for (LED led : strip) {// for every led
				if (Math.abs(map.get(id).getLocation() - led.getLocation()) < espsilon) {// if
					// close
					// enough
					led.setColor(cursor.getInt(DBAdapter.COL_COLOR));// set
																		// color
					// Log.w("gotColor", " id: " + id + ", dot loc: " +
					// map.get(id).getLocation() + ", led loc: " +
					// led.getLocation() + "eps: " + espsilon);
				}
			}
			cursor.moveToNext();
		}
		////Trace.endSection();// updating color
	}

	public void reset() {
		for (int i = 0; i < strip.length; i++) {
			strip[i].setColor(LED.DEFAULTCOLOR);
		}
	}
}
