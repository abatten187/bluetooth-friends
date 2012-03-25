package com.alexanderbatten.bluetoothfriends;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ViewAllDevices extends ListActivity {
	private static final String TAG = "ViewAllDevices";
	
	private List<Device> allDevices;
	private DBAdapter db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.devicelist_view);
	}

	@Override
	protected void onResume() {
		super.onResume();
		db = new DBAdapter(this);
        db.open();
        
        //allDevices = db.getAllDevicesOrderByDuration();
        //allDevices = db.getAllDevicesOrderByLastSeen();
        allDevices = db.getAllDevices();
        
        
        this.setTitle(this.getString(R.string.app_name) +" > View All Devices ("+ allDevices.size() +")");
        DeviceArrayAdapter adapter = new DeviceArrayAdapter(getApplicationContext(), R.layout.devicelist_row, allDevices);
        this.setListAdapter(adapter);
        this.registerForContextMenu(this.getListView());
	}

	@Override
	protected void onPause() {
		super.onPause();
		db.close();
		db = null;
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
    	
    	menu.setHeaderTitle(allDevices.get(info.position).getCustomOrNameOrMac());
    	menu.add(Menu.NONE, R.string.devicelist_context_view, Menu.FIRST, R.string.devicelist_context_view);
    	menu.add(Menu.NONE, R.string.devicelist_context_favourite, Menu.FIRST+1, R.string.devicelist_context_favourite);
    	menu.add(Menu.NONE, R.string.devicelist_context_delete, Menu.FIRST+2, R.string.devicelist_context_delete);
    }
	
	public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	
    	switch(item.getItemId()) {
    	case  R.string.devicelist_context_view:
    		Log.v(TAG, "Context - View: pos "+ info.position);
    		viewDevice(info.position);
    		return true;
    	case  R.string.devicelist_context_favourite:
    		Log.v(TAG, "Context - Favourite: pos "+ info.position);
    		Toast.makeText(this, "Work in progress..", Toast.LENGTH_SHORT).show();
    		return true;
    	case  R.string.devicelist_context_delete:
    		Log.v(TAG, "Context - Delete: pos "+ info.position);
    		Toast.makeText(this, "Work in progress..", Toast.LENGTH_SHORT).show();
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage("Are you sure you want to delete this device?");
    		builder.setPositiveButton("Yes", dialogClickListener);
    		builder.setNegativeButton("No", null);
    		builder.show();
    		return true;
    	default:
    		return super.onContextItemSelected(item);
    	}
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.i(TAG, "onListItemClick - pos "+ position);
		viewDevice(position);
	}
	
	private void viewDevice(int position){
		Intent intent = new Intent(this, ViewDevice.class);
		intent.putExtra("deviceMac", allDevices.get(position).getDeviceMacAddress());
		this.startActivity(intent);		
	}
	
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			if(which == DialogInterface.BUTTON_POSITIVE){
				
			}
		}
	};
	
	private class DeviceArrayAdapter extends ArrayAdapter<Device> {
		private static final String TAG = "DeviceArrayAdapter";
		
		private Context context;
		private List<Device> devices;

		private ImageView deviceIcon;
		private TextView deviceName;
		private TextView deviceRelationshipStatus;
		private ProgressBar deviceRelationshipProgress;
		
		private TextView deviceLastEncounter;
		private TextView deviceEncounters;
		
		public DeviceArrayAdapter(Context context, int textViewResourceId, List<Device> objects) {
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
	        
	        deviceRelationshipStatus = (TextView) row.findViewById(R.id.devicelistrow_relationshipStatus);
	        deviceRelationshipStatus.setText("change");
	        
	        deviceRelationshipProgress = (ProgressBar) row.findViewById(R.id.devicelistrow_relationshipProgress);
	        deviceRelationshipProgress.setMax(100);
	        deviceRelationshipProgress.setProgress(db.getRelationshipProgress(device));
	        
	        if(db.isEncountering(device)) {
	        	row.setBackgroundResource(R.color.encountering);
	        } else {
	        	row.setBackgroundResource(R.color.black);
	        }
	        
	        //deviceLastEncounter = (TextView) row.findViewById(R.id.devicelistrow_lastSeen);
	        //deviceLastEncounter.setText(db.getLastEncounterToString(device));

	        //Date now = new Date();
	        //if(new Date().getTime() < db.getLastEncounter(device).getFinishtime().getTime() + 360000){
	        	//row.setBackgroundResource(R.color.background);
	        //}
	        
			return row;
		}
	}
	
}
