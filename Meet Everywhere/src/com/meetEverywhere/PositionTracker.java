package com.meetEverywhere;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

/*
 * Klasa s³u¿¹ca do monitorowania w tle wspó³rzêdnych GPS.
 * 
 * Klasa jest kompletna i przetestowana.
 * Pod koniec projektu trzeba bêdzie wywaliæ powiadomienia w formacie Toast.
 * 
 */
public class PositionTracker extends Service {

	private final long ONE_SECOND_FACTOR = 1000;
	private final long serverUpdatePeriod = 30 * ONE_SECOND_FACTOR;
	private final long minUpdateTime = 5 * ONE_SECOND_FACTOR;
	private final float updateDistance = 10;
	private DAO dao;

	private LocationManager locationManager;
	private Location lastGPSLocation = null;
	private boolean isGPSOn = false;

	private Toast info;

	public PositionTracker() {
		this.dao = new DAO();
	}

	public void setDao(DAO dao) {
		this.dao = dao;
	}

	private LocationListener locationListener = new LocationListener() {

		public void onProviderEnabled(String arg0) {
			isGPSOn = true;
		}

		public void onProviderDisabled(String arg0) {
			isGPSOn = false;
		}

		public void onLocationChanged(Location location) {
			lastGPSLocation = location;
			updateLocationInfoOnServer("GPS handler.");
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// Implementacja nie jest konieczna.
		}

	};

	private Timer updatingTimer;
	private TimerTask updateTask = new TimerTask() {

		@Override
		public void run() {
			updateLocationInfoOnServer("updateTask.");
		}
	};

	@SuppressLint("ShowToast")
	@Override
	public void onCreate() {
		super.onCreate();

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		updatingTimer = new Timer();

		info = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
	}

	@Override
	public void onDestroy() {
		locationManager.removeUpdates(locationListener);
		updatingTimer.cancel();

		super.onDestroy();
	}

	private void handleStartCommand(Intent intent) {
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			isGPSOn = true;
		else
			isGPSOn = false;

		lastGPSLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				minUpdateTime, updateDistance, locationListener);

		updatingTimer.scheduleAtFixedRate(updateTask, 0, serverUpdatePeriod);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		handleStartCommand(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleStartCommand(intent);
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// Implementacja nie jest konieczna.
		return null;
	}

	private void updateLocationInfoOnServer(String sender) {
		double latitude;
		double longtitude;
		if (isGPSOn && lastGPSLocation != null) {

			latitude = lastGPSLocation.getLatitude();
			longtitude = lastGPSLocation.getLongitude();
			showToast("GPS: " + latitude + " x " + longtitude
					+ " wywo³anie: " + sender);
			dao.updateLocationOnServer(latitude, longtitude, true);

		} else {
			Location l = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (l != null) {

				latitude = l.getLatitude();
				longtitude = l.getLongitude();
				showToast("Sieæ: " + latitude + " " + longtitude
						+ " wywo³anie: " + sender);
				dao.updateLocationOnServer(latitude, longtitude, false);

			} else {
				showToast("Aktualna pozycja nieznana. Wywo³anie: " + sender);
			}
		}
	}
	
	public void showToast(String text){
		info.setText(text);
		info.show();
	}
}
