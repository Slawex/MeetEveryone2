package com.meetEverywhere;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Klasa stanowi Data Access Object do komunikacji z serwerem.
 * 
 */
public class DAO {


	/*
	 * private static int timeout = 5000;
	 * 
	 * public void connectGet(String method, String params){ try { URL url = new
	 * URL("http://10.0.2.2:8080/" + method + "?" + params); HttpURLConnection
	 * connection = (HttpURLConnection)url.openConnection();
	 * connection.setConnectTimeout(timeout);
	 * connection.setReadTimeout(timeout); connection.setRequestMethod("GET");
	 * 
	 * int code = connection.getResponseCode(); if(code == 200){ InputStream
	 * inStream = connection.getInputStream(); if(inStream != null){
	 * BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
	 * String line; while((line = in.readLine()) != null)
	 * System.out.println(line);
	 * 
	 * connection.disconnect(); } else { System.out.println("Error for " +
	 * method + ", params " + params + ": connection stream is null");
	 * connection.disconnect(); } } else { System.out.println("Error for " +
	 * method + ", params " + params + ": response code: " + code);
	 * connection.disconnect(); } } catch (Exception e) {
	 * System.out.println("Error for " + method + ", params " + params + ": " +
	 * e); } }
	 */

	public synchronized void updateLocationOnServer(double latitude,
			double longtitude, boolean isPositionFromGPS) {
		// TODO
	}
	
	public synchronized void updateMyHashtags(List<String> hashtags){
		// TODO
	}
	
	public List<ServUser> getUsersFromServer() {
		List<ServUser> list = new ArrayList<ServUser>();
		
		Random random = new Random();
		int count = random.nextInt(5)+1;
		
		for(int i=0;i<count;i++){
			String nick="marek"+random.nextInt(100);
			String desc=" jestem marek ";
			List<Tag> tags = new ArrayList<Tag>();
			int perc = random.nextInt(100);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			//Bitmap bitmap = BitmapFactory.decodeFile("/ic_launcher-web.png", options);
			Bitmap bitmap = null;
			
			ServUser user = new ServUser(nick,perc,bitmap,desc,tags);
			list.add(user);
		}
		
		
		
		return list;
	}
	
}
