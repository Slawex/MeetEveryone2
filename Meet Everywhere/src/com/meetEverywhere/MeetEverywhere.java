package com.meetEverywhere;

import java.io.FileNotFoundException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import com.google.android.gcm.GCMRegistrar;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MeetEverywhere extends Activity {

	//sprawdzaæ mo¿liwoœæ po³¹czenia z internetem i dostêp do GPSa oraz istnienia konta google
	//dodaæ przy pierwszej konfiguracji zapytanie czy numer telefonu jest poprawny (zaznaczyæ, ¿e podanie nieprawdziwego numeru spowoduje, ¿e nikt znajomy go niw wykryje) 
	//nastepnie poprawic ten system tak zeby autoryzacja byla przez sms
	
	private final String gcmId = "719842226591";
	private final String userName = "meuser";
	private final String password = "mepassword";
	
	private SharedPreferences userSettings;
	private ImageView userImage;
	
	private void registerGCM(){
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
		  GCMRegistrar.register(this, gcmId);
		} else {
		  System.out.println("Already registered, id: " + regId);
		  new InternetHelper().execute("gcmID", "id=1&gcmID=" + regId);
		}
	}
	
	private boolean isTrackingServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    String className = PositionTracker.class.getCanonicalName();
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (className.equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        
        TelephonyManager tMgr =(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        System.out.println(tMgr.getLine1Number());
        
		Authenticator.setDefault (new Authenticator() {
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication (userName, password.toCharArray());
		    }
		});
        
        registerGCM();
        
        ((Button)findViewById(R.id.minimizeButton)).setOnClickListener(new OnClickListener() {
	        //@Override
	        public void onClick(View arg0) {
	            finish();
	        }
	    });
        
        ((Button)findViewById(R.id.closeButton)).setOnClickListener(new OnClickListener() {
	        //@Override
	        public void onClick(View arg0) {
	            stopService(new Intent(MeetEverywhere.this, PositionTracker.class));
	            finish();
	        }
	    });
        
        OnClickListener userSettingsOnClickListener = new OnClickListener() {
	        //@Override
	        public void onClick(View arg0) {
	        	startActivity(new Intent(MeetEverywhere.this, ProfileEdition.class));
	        }
        };
        
        userImage = (ImageView)findViewById(R.id.userImage);
        userImage.setOnClickListener(userSettingsOnClickListener);
        ((TextView)findViewById(R.id.userDescription)).setOnClickListener(userSettingsOnClickListener);
        
        ((Button)findViewById(R.id.tagListsEdit)).setOnClickListener(new OnClickListener() {
	        //@Override
	        public void onClick(View arg0) {
	        	startActivity(new Intent(MeetEverywhere.this, TagsEdition.class));
	        }
	    });
        
        ((Button)findViewById(R.id.searchTagListsEdit)).setOnClickListener(new OnClickListener() {
	        //@Override
	        public void onClick(View arg0) {
	        	startActivity(new Intent(MeetEverywhere.this, SearchTagsEdition.class));
	        }
	    });
        
        ((Button)findViewById(R.id.friendsListEdit)).setOnClickListener(new OnClickListener() {
	        //@Override
	        public void onClick(View arg0) {
	        	startActivity(new Intent(MeetEverywhere.this, FriendsEdition.class));
	        }
	    });
        
        userSettings = getSharedPreferences(SharedPreferencesKeys.preferencesName, Activity.MODE_PRIVATE);
        boolean initialized = userSettings.getBoolean(SharedPreferencesKeys.initialization, false);
        
        if(!initialized)
        	startActivity(new Intent(MeetEverywhere.this, ProfileEdition.class));
        
        if(!isTrackingServiceRunning())
        	startService(new Intent(MeetEverywhere.this, PositionTracker.class));
        
        new InternetHelper().execute("userPosition", "id=1&x=0&y=0");
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        String description = userSettings.getString(SharedPreferencesKeys.userDescription, null);
        if(description != null)
        	((TextView)findViewById(R.id.userDescription)).setText(description);
        
        String image = userSettings.getString(SharedPreferencesKeys.userImage, null);
        if(image != null)
        	new ImageLoader().execute(image);
    }
    
    private class ImageLoader extends AsyncTask<String, Object, Bitmap>{

		@Override
		protected Bitmap doInBackground(String... params) {
			try {
				return BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.parse(params[0])));
			} catch (FileNotFoundException e) {
				return null;
			}
		}
    	
		@Override
		protected void onPostExecute(Bitmap param){
			//userImage.setImageURI(params[0]);
			if(param != null)
				userImage.setImageBitmap(param);
		}
    }
}
