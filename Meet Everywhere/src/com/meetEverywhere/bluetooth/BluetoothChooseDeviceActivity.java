package com.meetEverywhere.bluetooth;

import com.meetEverywhere.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class BluetoothChooseDeviceActivity extends Activity {

	private BluetoothAdapter bluetoothAdapter;
	private static BroadcastReceiverImpl broadcastReceiver = null;
	
	private ListView listView;
	private ArrayAdapter<BluetoothDevice> adapter;

	public void addToList(BluetoothDevice device){
		adapter.add(device);
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_choose_layout);

		adapter = new ArrayAdapter<BluetoothDevice>(this,
				android.R.layout.simple_list_item_1);
		listView = (ListView) findViewById(R.id.chatListView);
		listView.setAdapter(adapter);
		
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
				BluetoothDevice item =  (BluetoothDevice) listView.getItemAtPosition(position);		               
								
				Intent i = new Intent(getApplicationContext(), BluetoothChat.class);
				i.putExtra("device",item);
				startActivity(i); 				 				
			}
		});
		
	
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		 // SprawdŸ czy bluetooth jest zainstalowany.
		 if (bluetoothAdapter == null){
			 Toast.makeText(this, "Brak urz¹dzenia Bluetooth", Toast.LENGTH_SHORT).show();
		 }
		 // Uruchom modu³ Bluetooth.
		 if (!bluetoothAdapter.isEnabled()){
			 Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			 startActivityForResult(enableBT, 0xDEADBEEF);
		 }
		 // Zatrzymaj wyszukiwanie urz¹dzeñ.
		 if (bluetoothAdapter.isDiscovering()){
			 bluetoothAdapter.cancelDiscovery();
		 }
		 // Rozpocznij wyszukiwanie urz¹dzeñ.
		 bluetoothAdapter.startDiscovery();

		 // let's make a broadcast receiver to register our things		 
		 
		 
		 broadcastReceiver = new BroadcastReceiverImpl(this);
		 //broadcastReceiver.setBluetoothChat(this);
		 IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		 this.registerReceiver(broadcastReceiver, ifilter);	
	}
	
	@Override
	public void onPause(){
		try{
			this.unregisterReceiver(broadcastReceiver);
		}catch(IllegalArgumentException e){
			// Nie rób nic.
		}
		super.onPause();
	}	
}


class BroadcastReceiverImpl extends BroadcastReceiver {

	private final BluetoothChooseDeviceActivity bluetoothChat;
	
	public BroadcastReceiverImpl(BluetoothChooseDeviceActivity bluetoothChat){
		this.bluetoothChat = bluetoothChat;
	}
		
	@Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction(); //may need to chain this to a recognizing function
        if (BluetoothDevice.ACTION_FOUND.equals(action)){
            // Pobierz object BluetoothDevice.
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            bluetoothChat.addToList(device);            
        }
    }
}

