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
 * Prawdopodobnie nie wymaga poprawek poza dodaniem wywolania 
 * update'u pozycji na serwerze (przez DAO).
 * */
public class PositionTracker extends Service {

	private final long serverUpdatePeriod = 30 * 1000;
	private final long minUpdateTime = 5 * 1000;
	private final float minUpdateDistance = 50;
	private final DAO dao;

	private LocationManager locationManager;
	private Location lastGPSLocation = null;
	private boolean isGPSOn = false;

	private Toast info;

	public PositionTracker(DAO dao){
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
			updateLocationInfoOnServer();
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			// Implementacja nie jest konieczna.
		}

	};

	private Timer updatingTimer;
	private TimerTask infoTask = new TimerTask() {
		
		@Override
		public void run() {
			updateLocationInfoOnServer();
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
				minUpdateTime, minUpdateDistance, locationListener);

		updatingTimer.scheduleAtFixedRate(infoTask, 0, serverUpdatePeriod);
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

	private void updateLocationInfoOnServer() {
		double latitude;
		double longtitude;
		if (isGPSOn && lastGPSLocation != null) {
			latitude = lastGPSLocation.getLatitude();
			longtitude = lastGPSLocation.getLongitude();
			info.setText("GPS " + latitude + " x "
					+ longtitude);
			info.show();
			dao.updateLocationOnServer(latitude, longtitude);			
		} else {
			Location l = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (l != null) {
				latitude = l.getLatitude();
				longtitude = l.getLongitude();
				info.setText("Network " + latitude + " "
						+ longtitude);
				info.show();
				dao.updateLocationOnServer(latitude, longtitude);
			} else {
				info.setText("No location info");
				info.show();
			}
		}
	}

}
