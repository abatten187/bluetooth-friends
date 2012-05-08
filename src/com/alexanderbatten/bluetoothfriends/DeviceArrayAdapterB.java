package com.alexanderbatten.bluetoothfriends;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DeviceArrayAdapterB extends ArrayAdapter<Device> {
	
	private Context context;
	private List<Device> devices;

	private ImageView deviceIcon;
	private TextView deviceName;
	
	public DeviceArrayAdapterB(Context context, int textViewResourceId, List<Device> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.devices = objects;
	}
	
	public int getCount(){
		return this.devices.size();
	}
	
	public Device getItem(int index){
		return this.devices.get(index);
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.devicelist_row, parent, false);
        }
        
        Device device = getItem(position);
        
        deviceIcon = (ImageView) row.findViewById(R.id.devicelistrow_icon);
    	deviceIcon.setImageBitmap(device.getIcon(context));
        
        deviceName = (TextView) row.findViewById(R.id.devicelistrow_name);
        deviceName.setText(device.getCustomOrNameOrMac());
        
        //deviceLastEncounter = (TextView) row.findViewById(R.id.devicelistrow_lastSeen);
        //deviceLastEncounter.setText(device.getLastEncounterToString());
        
        //deviceEncounters = (TextView) row.findViewById(R.id.devicelistrow_encounters);
        //deviceEncounters.setText(Integer.toString(device.getEncounterCount()));
        
		return row;
	}
}
