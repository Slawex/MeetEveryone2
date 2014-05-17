package com.meetEverywhere;

import java.io.IOException;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class BluetoothChat extends Activity {

	private ListView listView;
	private EditText text;
	private ArrayAdapter<TextMessage> messages;
	private final static BluetoothDispatcher dispatcher = BluetoothDispatcher
			.getInstance();
	private BluetoothDevice actualDevice;
	private BluetoothConnection actualConnection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_chat_layout);
		
		Bundle extras = getIntent().getExtras();
		actualDevice = (BluetoothDevice) extras.get("device");
		text = (EditText) this.findViewById(R.id.text);
		
		messages = dispatcher.getArrayAdapterForDevice(this, actualDevice);
		if(messages == null){
			Toast.makeText(getApplicationContext(), "B³¹d tworzenia po³¹czenia!", Toast.LENGTH_SHORT).show();
			finish();
		}
		listView = (ListView) findViewById(R.id.messagesList);
		listView.setAdapter(messages);
		
		Log.i("WIADOMOSC", "POBRANO ADAPTER");
		actualConnection = dispatcher.getBluetoothConnectionForDevice(actualDevice);

		
		((Button) this.findViewById(R.id.send_button)).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				String messageText = text.getText().toString();
				if(!messageText.equals("")){
					TextMessage message = new TextMessage(messageText, dispatcher.getOwnData().getNickname());
					try {
						actualConnection.addMessage(message);
					} catch (IOException e) {
						Toast.makeText(getBaseContext(), "Wiadomoœæ nie zosta³a wys³ana!", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

	}

	public void sendMessage(View v) {

	}

}
