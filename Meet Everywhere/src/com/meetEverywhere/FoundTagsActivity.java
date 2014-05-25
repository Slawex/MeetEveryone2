package com.meetEverywhere;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class FoundTagsActivity extends Activity{
	
	private UsersListRefresher refresher;
	private List<ServUser> users = new ArrayList<ServUser>();
	private ListView usersListView;
	private ListAdapter listAdapter;
	private Handler handler;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.found_tags_activity_layout);
		
		usersListView = (ListView) findViewById(R.id.foundUsersList);
		
		handler = new Handler(){
			  @Override
			  public void handleMessage(Message msg) {
				  loadAdapterWithNewList(users);
			  }
			};

		loadAdapterWithNewList(users);
		refresher = new UsersListRefresher();
		refresher.start();
		
		Intent intent = getIntent();
		List<String> tags = intent.getStringArrayListExtra("tags");
	}

	public void back(View view) {
		finish();
	}
	
	
	public void loadAdapterWithNewList(List<ServUser> list) {
		listAdapter = new MyUsersListAdapter(this, R.layout.found_tags_content_info,
				(ArrayList<ServUser>) list);
		usersListView.setAdapter(listAdapter);

	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		refresher.stopThread();
	}
	
	
	public class UsersListRefresher extends Thread{
		private boolean shouldRun = true;
		private DAO dao;
		
		public UsersListRefresher(){
			dao = new DAO();
		}
		
		@Override
		public void run(){
			while(shouldRun){
				users = dao.getUsersFromServer();
				Message msg = handler.obtainMessage();
				handler.sendMessage(msg);
				
				try {
					Thread.currentThread();
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void stopThread(){
			shouldRun=false;
		}

	}
}
