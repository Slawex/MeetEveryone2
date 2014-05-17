package com.meetEverywhere;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.view.ViewDebug.FlagToString;
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
		// Sprawd� czy bluetooth jest zainstalowany.
		if (bluetoothAdapter == null) {
			showToast("Brak urz�dzenia Bluetooth!");
			return;
		}
		// Uruchom modu� Bluetooth.
		if (!bluetoothAdapter.isEnabled()) {
			bluetoothAdapter.enable();
		}

		// Zatrzymaj wyszukiwanie urz�dze�.
		if (bluetoothAdapter.isDiscovering()) {
			bluetoothAdapter.cancelDiscovery();
		}

		// Uruchom w�tek us�ugi, kt�ry akceptuje po��czenia.
		(new Thread(this)).start();

	}

	public void run() {
		while (true) {
			showToast("Us�uga Bluetooth uruchomiona.");
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
/*
					BluetoothConnection connection = new BluetoothConnection(this, socket);

					dispatcher.addConnection(getApplicationContext(),
							socket.getRemoteDevice(), connection);

					showToast("Nawi�zano po��czenie z: "
							+ connection.getUser().getNickname());
*/
					Intent i = new Intent(getBaseContext(), BluetoothChat.class);
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.putExtra("device", socket.getRemoteDevice());
					dispatcher.setTempSocketHolder(socket);
					getApplication().startActivity(i);

				}

			} catch (IOException e) {
				showToast("Us�uga Bluetooth zatrzymana, poczekaj na wznowienie!");
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
				showToast("B��d podczas wznawiania us�ugi Bluetooth!");
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
