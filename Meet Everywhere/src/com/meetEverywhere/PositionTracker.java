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

public class PositionTracker extends Service {
    
	private final long serverUpdatePeriod = 30 * 1000;
	private final long minUpdateTime = 5 * 1000;
	private final float minUpdateDistance = 50;
	
	private LocationManager locationManager;
    private Location lastGPSLocation = null;
    private boolean isGPSOn = false;
    
    private Toast info;
    
    private LocationListener locationListener = new LocationListener() {
        
        //@Override
        public void onProviderEnabled(String arg0) { 
        	isGPSOn = true;
        }
        
        //@Override
        public void onProviderDisabled(String arg0) {
        	isGPSOn = false;
        }
        
        //@Override
        public void onLocationChanged(Location location) {
        	lastGPSLocation = location;
        	updateLocationInfoOnServer();
        }

        //@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
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
        
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        updatingTimer = new Timer();
        
        info = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
    }

    @Override
    public void onDestroy() {
    	locationManager.removeUpdates(locationListener);
        updatingTimer.cancel();
        
        super.onDestroy();
    }
    
    private void handleStartCommand(Intent intent){
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        	isGPSOn = true;
        else
        	isGPSOn = false;
        
        lastGPSLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minUpdateTime, minUpdateDistance, locationListener);
                
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
		// TODO Auto-generated method stub
		return null;
	}

	private void updateLocationInfoOnServer(){
		if(isGPSOn && lastGPSLocation != null){
			info.setText("GPS " + lastGPSLocation.getLatitude() + " " + lastGPSLocation.getLongitude());
			info.show();
		} else{
			Location l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if(l != null){
				info.setText("Network " + l.getLatitude() + " " + l.getLongitude());
				info.show();
			} else {
				info.setText("No location info");
				info.show();
			}
		}		
	}
	
}
