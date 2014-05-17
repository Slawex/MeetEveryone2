package com.meetEverywhere;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class BluetoothDispatcher {

	private static BluetoothDispatcher instance;
	private Handler handler;
	private Context tempContextHolder;
	private BluetoothSocket tempSocketHolder;
	private final String ownUUID = "00001101-0000-1000-8000-00805F9B34FB";
	private final HashMap<BluetoothDevice, BluetoothConnection> connections;
	private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();
	private User ownData;

	private BluetoothDispatcher() {
		connections = new HashMap<BluetoothDevice, BluetoothConnection>();

		List<String> list = new ArrayList<String>();
		list.add("pi³ka no¿na");
		list.add("strzelectwo");
		ownData = new User("marek" + (new Random().nextInt(100000)), list);
	}

	public static BluetoothDispatcher getInstance() {
		if (instance == null) {
			instance = new BluetoothDispatcher();
		}
		return instance;
	}

	public ArrayAdapter<TextMessage> getArrayAdapterForDevice(Context context,
			BluetoothDevice device) {
		if (!connections.containsKey(device)) {
			try {
				establishConnection(context, device);
			} catch (IOException e) {
				return null;
			} catch (ClassNotFoundException e) {
				return null;
			} catch (InterruptedException e) {
				return null;
			}
		}
		return connections.get(device).getMessagesAdapter();
	}

	public BluetoothConnection getBluetoothConnectionForDevice(
			BluetoothDevice device) {
		return connections.get(device);
	}

	public void establishConnection(Context context, BluetoothDevice device) throws IOException, ClassNotFoundException,
			InterruptedException {

		BluetoothSocket socket = tempSocketHolder;
		tempSocketHolder = null;
		
		if (socket == null) {
			socket = device.createInsecureRfcommSocketToServiceRecord(UUID
					.fromString(ownUUID));
			bluetoothAdapter.cancelDiscovery();
			socket.connect();
		}
		BluetoothConnection connection = new BluetoothConnection(context,
				socket);

		Toast toast = Toast.makeText(context, "Nawi¹zano po³¹czenie z: "
				+ connection.getUser().getNickname(), Toast.LENGTH_LONG);
		toast.show();


		addConnection(context, device, connection);	
	}

	public void addConnection(Context context, BluetoothDevice device,
			BluetoothConnection connection) {
		connections.put(device, connection);
	}

	public void deleteConnection(BluetoothConnection connection) {
		connections.remove(connection.getBluetoothSocket().getRemoteDevice());
	}

	public User getOwnData() {
		return ownData;
	}

	public void setOwnNickname(User ownData) {
		this.ownData = ownData;
	}

	public String getUUID() {
		return ownUUID;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public Handler getHandler() {
		return handler;
	}

	public BluetoothSocket getTempSocketHolder() {
		return tempSocketHolder;
	}

	public void setTempSocketHolder(BluetoothSocket tempSocketHolder) {
		this.tempSocketHolder = tempSocketHolder;
	}

	public Context getTempContextHolder() {
		return tempContextHolder;
	}

	public void setTempContextHolder(Context tempContextHolder) {
		this.tempContextHolder = tempContextHolder;
	}

}
