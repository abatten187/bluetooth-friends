package com.alexanderbatten.bluetoothfriends;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ViewDevice extends ListActivity {
	private static final String TAG = "ViewDevice";
	
	private List<Encounter> allEncounters;
	private TextView tvDeviceName;
	private TextView tvRelationshipStatus;
	private ProgressBar pbRelationshipProgress;
	private TextView tvRelationshipLeft;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.device_view);
		
		tvDeviceName = (TextView) this.findViewById(R.id.deviceview_devicename);
		tvRelationshipStatus = (TextView) this.findViewById(R.id.deviceview_relationshipstatus);
		pbRelationshipProgress = (ProgressBar) this.findViewById(R.id.deviceview_relationshipprogress);
		tvRelationshipLeft = (TextView) this.findViewById(R.id.deviceview_relationshipleft);
		
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		DBAdapter db = new DBAdapter(this);
		db.open();
		Device device = db.getDevice(extras.getString("deviceMac"));
		List<Encounter> encounters = db.getEncounters(device);
		long totalEncounterDuration = db.getTotalEncounterDuration(device);
		db.close();
		
		tvDeviceName.setText(device.getCustomOrNameOrMac());
		getRelationshipStatus(totalEncounterDuration);
		
		
        this.setTitle(this.getString(R.string.app_name) +" > View Device");
        EncounterArrayAdapter adapter = new EncounterArrayAdapter(getApplicationContext(), R.layout.encounterlist_row, encounters);
        this.setListAdapter(adapter);
	}
	
	private void getRelationshipStatus(long duration){
		final long LEVEL_NOBODY = 0;
		final long LEVEL_ACQUAINTANCE = 3600000;
		final long LEVEL_CASUALFRIEND = 7200000;
		final long LEVEL_BESTFRIEND = 14400000;
		final long LEVEL_BESTFRIENDSFOREVER = 28800000;
		pbRelationshipProgress.setMax(100);
		
		if (duration > LEVEL_NOBODY && duration <= LEVEL_ACQUAINTANCE) {
			tvRelationshipStatus.setText("Acquaintance");
			pbRelationshipProgress.setProgress((int)((double)(duration-LEVEL_NOBODY)/(LEVEL_ACQUAINTANCE-LEVEL_NOBODY)*100));
			long left = (LEVEL_ACQUAINTANCE - duration) / 60000;
			tvRelationshipLeft.setText(left +" mins until next relationship level");
		}
		else if (duration > LEVEL_ACQUAINTANCE && duration <= LEVEL_CASUALFRIEND) {
			tvRelationshipStatus.setText("Acquaintance");
			pbRelationshipProgress.setProgress((int)((double)(duration-LEVEL_ACQUAINTANCE)/(LEVEL_CASUALFRIEND-LEVEL_ACQUAINTANCE)*100));	
			long left = (LEVEL_CASUALFRIEND - duration) / 60000;
			tvRelationshipLeft.setText(left +" mins until next relationship level");
		}
		else if	(duration > LEVEL_CASUALFRIEND && duration <= LEVEL_BESTFRIEND) {
			tvRelationshipStatus.setText("Casual Friend");
			pbRelationshipProgress.setProgress((int)((double)(duration-LEVEL_CASUALFRIEND)/(LEVEL_BESTFRIEND-LEVEL_CASUALFRIEND)*100));	
			long left = (LEVEL_BESTFRIEND - duration) / 60000;
			tvRelationshipLeft.setText(left +" mins until next relationship level");
		}
		else if	(duration > LEVEL_BESTFRIEND && duration <= LEVEL_BESTFRIENDSFOREVER) {
			tvRelationshipStatus.setText("Best Friend");
			pbRelationshipProgress.setProgress((int)((double)(duration-LEVEL_BESTFRIEND)/(LEVEL_BESTFRIENDSFOREVER-LEVEL_BESTFRIEND)*100));	
			long left = (LEVEL_BESTFRIENDSFOREVER - duration) / 60000;
			tvRelationshipLeft.setText(left +" mins until next relationship level");
		}
		else if	(duration > LEVEL_BESTFRIENDSFOREVER) {
			tvRelationshipStatus.setText("Best Friends Forever");
			pbRelationshipProgress.setProgress(100);	
			tvRelationshipLeft.setText("");
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Encounter encounter = allEncounters.get(position);
		String stringLoc = "geo:"+ encounter.getLatitude() +", "+ encounter.getLongitude() +"?z=20";
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(stringLoc));
		this.startActivity(intent);
	}
}
