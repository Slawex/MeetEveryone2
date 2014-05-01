package com.meetEverywhere;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class InitialSettings extends Activity {

	private SharedPreferences userSettings;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_settings_layout);
        
        ((Button)findViewById(R.id.saveSettings)).setOnClickListener(new OnClickListener() {
	        //@Override
	        public void onClick(View arg0) {
	        	userSettings = getSharedPreferences(SharedPreferencesKeys.preferencesName, Activity.MODE_PRIVATE);
	        	SharedPreferences.Editor settingsEditor = userSettings.edit();
	        	settingsEditor.putBoolean(SharedPreferencesKeys.initialization, true);
	        	settingsEditor.commit();
	            finish();
	        }
	    });
    }
}
