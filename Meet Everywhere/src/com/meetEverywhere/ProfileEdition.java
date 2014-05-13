package com.meetEverywhere;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
//import android.provider.MediaStore;


public class ProfileEdition extends Activity {

	private static final int USER_IMAGE_REQUEST_CODE = 1000;
	
	private SharedPreferences userSettings;
	private EditText descriptionEditor;
	private ImageView userImage;
	private String imageUri;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_edition_layout);
        
        userSettings = getSharedPreferences(SharedPreferencesKeys.preferencesName, Activity.MODE_PRIVATE);
        String description = userSettings.getString(SharedPreferencesKeys.userDescription, null);
        
        descriptionEditor = (EditText) findViewById(R.id.userDescriptionEdition);
        if(description != null)
        	descriptionEditor.setText(description);
        
        userImage = (ImageView)findViewById(R.id.userImage);
        //userImage.setImageResource(R.drawable.ic_launcher);
        
        userImage.setOnClickListener(new OnClickListener() {
	        //@Override
	        public void onClick(View arg0) {
	        	Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
	        	photoPickerIntent.setType("image/*");
	        	startActivityForResult(photoPickerIntent, USER_IMAGE_REQUEST_CODE);
	        	
	        	//startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), USER_IMAGE_REQUEST_CODE);
	        }
	    });
        
        ((Button)findViewById(R.id.saveUserSettingsButton)).setOnClickListener(new OnClickListener() {
	        //@Override
	        public void onClick(View arg0) {
	        	SharedPreferences.Editor settingsEditor = userSettings.edit();
	        	settingsEditor.putString(SharedPreferencesKeys.userDescription, descriptionEditor.getText().toString());
	        	settingsEditor.putString(SharedPreferencesKeys.userImage, imageUri);
	        	settingsEditor.commit();
	        	
	        	if(!userSettings.getBoolean(SharedPreferencesKeys.initialization, false))
	        		startActivity(new Intent(ProfileEdition.this, InitialSettings.class));
	        	
	            finish();
	        }
	    });
        
    }
    
    @Override
    protected final void onActivityResult(final int requestCode, final int resultCode, final Intent i) {
    	super.onActivityResult(requestCode, resultCode, i);
    	if(resultCode == RESULT_OK) {
    		switch(requestCode) {
    	    	case USER_IMAGE_REQUEST_CODE:
    	    		Uri uri = i.getData();
    	    		imageUri = uri.toString();
    	    		new ImageLoader().execute(uri);
    	    		break;
    		}
    	}
    }
    
    private class ImageLoader extends AsyncTask<Uri, Object, Bitmap>{

		@Override
		protected Bitmap doInBackground(Uri... params) {
			try {
				return BitmapFactory.decodeStream(getContentResolver().openInputStream(params[0]));
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

