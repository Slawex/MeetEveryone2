package com.meetEverywhere;

import android.content.Context;
import android.content.Intent;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		System.out.println("onError");
	}

	@Override
	protected void onMessage(Context arg0, Intent intent) {
		// TODO Auto-generated method stub
		System.out.println("onMessage");
		System.out.println(intent.getStringExtra("key1"));
	}

	@Override
	protected void onRegistered(Context arg0, String regId) {
		// TODO Auto-generated method stub
		System.out.println("onRegistered:" + regId);
		new InternetHelper().execute("gcmID", "id=1&gcmID=" + regId);
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		System.out.println("onUnregistered");
	}



}
