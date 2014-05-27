package com.meetEverywhere.bluetooth;

import java.util.LinkedHashMap;
import java.util.List;

import com.meetEverywhere.R;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BluetoothListAdapter extends ArrayAdapter<BluetoothConnection> {

	private final Context context;
	private final LinkedHashMap<BluetoothDevice, BluetoothConnection> connections;

	public BluetoothListAdapter(Context context, int resource) {
		super(context, R.layout.bluetooth_device_list_element);
		this.context = context;
		this.connections = BluetoothDispatcher.getInstance()
				.getConnections();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
			if(position >= connections.size()){
				return null;
			}
	    	LayoutInflater inflater = (LayoutInflater) context
	            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View row = inflater.inflate(1 /* R.layout.bluetooth_device_list_element*/, parent, false);
	        TextView textView = (TextView) row.findViewById(1/*R.id.bluetooth_username*/);
	        ImageView imageView = (ImageView) row.findViewById(1/*R.id.bluetooth_thumb*/);
	                                                                                                                    
	        textView.setText(getNickname(position));
	        // Change the icon for Windows and iPhone
	        String s = getNickname(position);
	        
/*  		if (s.startsWith("something") || s.startsWith("something")
	            || s.startsWith("something")) {
	          imageView.setImageResource(R.drawable.no);
	        } else {
	          imageView.setImageResource(R.drawable.ok);
	        }
*/
	        
	        return row;
	        
	        
	}

	public String getNickname(int position) {
/*		
		return BluetoothDispatcher.getInstance().getConnections().get(((BluetoothDevice) connections.keySet().toArray()[position]))
				.getUser().getNickname();
*/
		return connections
				.get(((BluetoothDevice) connections.keySet().toArray()[position]))
				.getUser().getNickname();

	}

}
