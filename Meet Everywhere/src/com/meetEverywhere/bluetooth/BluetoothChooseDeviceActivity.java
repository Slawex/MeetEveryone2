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
import android.widget.ListView;
import android.widget.Toast;

public class BluetoothChooseDeviceActivity extends Activity {

	private BluetoothAdapter bluetoothAdapter;
	private static BroadcastReceiverImpl broadcastReceiver = null;

	private ListView listView;
	// private ArrayAdapter<BluetoothDevice> adapter;
	private BluetoothListAdapter adapter;

	public void addToList(BluetoothConnection connection) {
		adapter.add(connection);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth_choose_layout);

		if (BluetoothDispatcher.getInstance().getBluetoothListAdapter() == null) {
			BluetoothDispatcher.getInstance().setBluetoothListAdapter(
					new BluetoothListAdapter(getApplicationContext(), 0));
		}

		adapter = BluetoothDispatcher.getInstance().getBluetoothListAdapter();

		/*
		 * adapter = new ArrayAdapter<BluetoothDevice>(this,
		 * android.R.layout.simple_list_item_1);
		 */
		listView = (ListView) findViewById(R.id.chatListView);
		listView.setAdapter(adapter);
		getAdapter().notifyDataSetChanged();

		
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BluetoothConnection conn = (BluetoothConnection) listView
						.getItemAtPosition(position);
				BluetoothDispatcher dispatcher = BluetoothDispatcher
						.getInstance();
				BluetoothDevice item = null;
				for (BluetoothDevice dev : dispatcher.getConnections().keySet()) {
					if (dispatcher.getConnections().get(dev).equals(conn)) {
						item = dev;
					}
				}

				Intent i = new Intent(getApplicationContext(),
						BluetoothChat.class);
				i.putExtra("device", item);
				startActivity(i);
			}
		});

		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// SprawdŸ czy bluetooth jest zainstalowany.
		if (bluetoothAdapter == null) {
			Toast.makeText(this, "Brak urz¹dzenia Bluetooth",
					Toast.LENGTH_SHORT).show();
		}
		// Uruchom modu³ Bluetooth.
		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBT, 0xDEADBEEF);
		}
		// Zatrzymaj wyszukiwanie urz¹dzeñ.
		if (bluetoothAdapter.isDiscovering()) {
			bluetoothAdapter.cancelDiscovery();
		}
		// Rozpocznij wyszukiwanie urz¹dzeñ.
		bluetoothAdapter.startDiscovery();

		// let's make a broadcast receiver to register our things

		broadcastReceiver = new BroadcastReceiverImpl(this);
		IntentFilter ifilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(broadcastReceiver, ifilter);
	}

	@Override
	public void onPause() {
		try {
			this.unregisterReceiver(broadcastReceiver);
		} catch (IllegalArgumentException e) {
			// Nie rób nic.
		}
		super.onPause();
	}

	public BluetoothListAdapter getAdapter() {
		return adapter;
	}

}

class BroadcastReceiverImpl extends BroadcastReceiver {

	private final BluetoothChooseDeviceActivity bluetoothChooseDev;

	public BroadcastReceiverImpl(
			BluetoothChooseDeviceActivity bluetoothChooseDev) {
		this.bluetoothChooseDev = bluetoothChooseDev;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		BluetoothDispatcher dispatcher = BluetoothDispatcher.getInstance();
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			// Pobierz object BluetoothDevice.
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			BluetoothConnection connection;
			try {
				if (!BluetoothDispatcher.getInstance().getConnections().keySet()
						.contains(device)) {
					connection = BluetoothDispatcher.getInstance()
							.establishConnection(context, device);
					bluetoothChooseDev.addToList(connection);

				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			bluetoothChooseDev.getAdapter().notifyDataSetChanged();

		}
	}
}
