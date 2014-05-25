package com.meetEverywhere.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class BluetoothService extends Service implements Runnable {

	// private Toast toast;
	private Handler handler;

	/*
	 * public BluetoothService() { handler = new Handler(); }
	 */

	@SuppressLint("ShowToast")
	@Override
	public void onCreate() {
		super.onCreate();
		handler = new Handler();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// Implementacja nie jest konieczna.
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		onStart(intent, startId);
		return START_STICKY;
	}

	@Override
	public void onStart(Intent intent, int startId) {

		BluetoothAdapter bluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		// SprawdŸ czy bluetooth jest zainstalowany.
		if (bluetoothAdapter == null) {
			showToast("Brak urz¹dzenia Bluetooth!");
			return;
		}
		// Uruchom modu³ Bluetooth.
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
		}

		// Zatrzymaj wyszukiwanie urz¹dzeñ.
		if (bluetoothAdapter.isDiscovering()) {
			bluetoothAdapter.cancelDiscovery();
		}

		// Uruchom w¹tek us³ugi, który akceptuje po³¹czenia.
		(new Thread(this)).start();

	}

	public void run() {
		while (true) {
			showToast("Us³uga Bluetooth uruchomiona.");
			BluetoothDispatcher dispatcher = BluetoothDispatcher.getInstance();
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			BluetoothServerSocket serverSocket = null;
			BluetoothSocket socket = null;
			try {
				serverSocket = adapter
						.listenUsingInsecureRfcommWithServiceRecord(
								"MeetEverywhere",
								UUID.fromString(dispatcher.getUUID()));

				while ((socket = serverSocket.accept()) != null) {

					final BluetoothSocket tempSocket = socket;
					final BluetoothDispatcher tempDispatcher = dispatcher;
					handler.post(new Runnable() {
						public void run() {
							BluetoothConnection connection;
							try {
								connection = new BluetoothConnection(
										getApplicationContext(), tempSocket);
								tempDispatcher.addConnection(null,
										tempSocket.getRemoteDevice(),
										connection);
								if (tempDispatcher.getBluetoothListAdapter() == null) {
									tempDispatcher
											.setBluetoothListAdapter(new BluetoothListAdapter(
													getApplicationContext(), 0));
								}
								tempDispatcher.getBluetoothListAdapter().add(
										connection);
								showToast("Nawi¹zano po³¹czenie z: "
										+ connection.getUser().getNickname());
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					});

				}

			} catch (IOException e) {
				showToast("Us³uga Bluetooth zatrzymana, poczekaj na wznowienie!");
			} finally {
				try {
					if (serverSocket != null) {
						serverSocket.close();
					}
					if (socket != null) {
						socket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				showToast("B³¹d podczas wznawiania us³ugi Bluetooth!");
			}
		}

	}

	public void showToast(final String text) {
		handler.post(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), text,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

}
