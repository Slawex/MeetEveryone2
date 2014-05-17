package com.meetEverywhere;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class BluetoothConnection implements Runnable {

	private boolean keepConnectionActive;
	private final BluetoothDispatcher dispatcher = BluetoothDispatcher
			.getInstance();
	private final ArrayAdapter<TextMessage> messagesAdapter;
	private final BluetoothSocket bluetoothSocket;
	private final ObjectInputStream inputStream;
	private final ObjectOutputStream outputStream;
	private User user;
	private Handler handler;
	
	public BluetoothConnection(Context context, BluetoothSocket socket)
			throws IOException, ClassNotFoundException, InterruptedException {
		messagesAdapter = new ArrayAdapter<TextMessage>(context,
				android.R.layout.simple_list_item_1);
		bluetoothSocket = socket;

		outputStream = new ObjectOutputStream(socket.getOutputStream());
		outputStream.flush();
		Thread.sleep(100);

		inputStream = new ObjectInputStream(socket.getInputStream());

		outputStream.writeObject(BluetoothDispatcher.getInstance()
				.getOwnData());
		user = (User) inputStream.readObject();
		keepConnectionActive = true;
		handler = dispatcher.getHandler();		
		(new Thread(this)).start();
	}

	public BluetoothSocket getBluetoothSocket() {
		return bluetoothSocket;
	}

	public User getUser() {
		return user;
	}

	public ObjectOutputStream getOutputStream() {
		return outputStream;
	}

	public ObjectInputStream getInputStream() {
		return inputStream;
	}

	public ArrayAdapter<TextMessage> getMessagesAdapter() {
		return messagesAdapter;
	}

	public void run() {
		Object receivedObject;
		while (keepConnectionActive) {
			try {
				while ((receivedObject = inputStream.readObject()) != null) {
					if(receivedObject instanceof TextMessage){
						addMessage((TextMessage) receivedObject);
					}
				}
			} catch (OptionalDataException e) {
				keepConnectionActive = false;
				dispatcher.deleteConnection(this);
			} catch (ClassNotFoundException e) {
				keepConnectionActive = false;
				dispatcher.deleteConnection(this);
			} catch (IOException e) {
				keepConnectionActive = false;
				dispatcher.deleteConnection(this);
			}
		}
					
		handler.post(new Runnable() {
			public void run() {
				Toast.makeText(dispatcher.getTempContextHolder(),
						"Utracono po³¹czenie z: " + user.getNickname(),
						Toast.LENGTH_SHORT).show();
			}
		});
		
	}
	
	public void addMessage(final TextMessage message) throws IOException{
		if(message.isLocal()){
			outputStream.writeObject(message);
		}
	
		handler.post(new Runnable() {
			public void run() {
				messagesAdapter.add(message);
			}
		});	

		
	}

}
