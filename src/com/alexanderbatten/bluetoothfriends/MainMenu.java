package com.alexanderbatten.bluetoothfriends;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainMenu extends Activity implements OnClickListener {
	private final String TAG = "MainMenu";
	
	public static Context ACTIVE_INSTANCE;
	public static String version;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);
        Log.i(TAG, "onCreate");
        
        ACTIVE_INSTANCE = this;
        
        //Set up buttons
        View buttonViewAll = this.findViewById(R.id.mainmenu_button_viewall);
        buttonViewAll.setOnClickListener(this);
        View buttonSettings = this.findViewById(R.id.mainmenu_button_settings);
        buttonSettings.setOnClickListener(this);
        //View buttonUpload = this.findViewById(R.id.mainmenu_button_upload);
        //buttonUpload.setOnClickListener(this);
        View buttonLeaderboard = this.findViewById(R.id.mainmenu_button_leaderboard);
        buttonLeaderboard.setOnClickListener(this);
        //View buttonQuit = this.findViewById(R.id.mainmenu_button_quit);
        //buttonQuit.setOnClickListener(this);
        //View buttonOn = this.findViewById(R.id.mainmenu_button_on);
        //buttonOn.setOnClickListener(this);
        //View buttonOff = this.findViewById(R.id.mainmenu_button_off);
        //buttonOff.setOnClickListener(this);
        
        //Start up AlarmManagers
		Settings.startBluetoothScanAlarmManager(this);
		Settings.startServerUploadAlarmManager(this);
		
		//Display Version info
		PackageManager manager = this.getPackageManager();
		TextView tvVersion = (TextView) this.findViewById(R.id.mainmenu_version);
		try {
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			version = "v"+ info.versionName;
		} catch (Exception e){
			Log.w(TAG, "Error version");
		}
		
		tvVersion.setText(version);
    }

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
		//Start up ServerService
		//Intent serverService = new Intent(this, ServerService.class);
		//this.startService(serverService);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "onDestroy");
		
		//Determine if user wishes to have service in background
		if(!Settings.getBackgroundRun(this)){
			Settings.stopBluetoothScanAlarmManager(this);
			Settings.stopServerUploadAlarmManager(this);
		}
	}

	public void onClick(View v) {
		Log.i(TAG, "onClick");
		
		switch (v.getId()){
			case R.id.mainmenu_button_viewall:
				Log.v(TAG, "ViewAll button clicked");
				this.startActivity(new Intent(this, ViewAllDevices.class));
				break;
			case R.id.mainmenu_button_settings:
				Log.v(TAG, "Settings button clicked");
				this.startActivity(new Intent(this, Settings.class));
				break;
			//case R.id.mainmenu_button_upload:
				//Log.v(TAG, "Upload button clicked");
				//new UploadTest(this).execute();
				//break;
			case R.id.mainmenu_button_leaderboard:
				Log.v(TAG, "Leaderboard button clicked");
				this.startActivity(new Intent(this, OnlineLeaderboard.class));
				break;
			//case R.id.mainmenu_button_quit:
				//Log.v(TAG, "Quit button clicked");
				//this.finish();
				//break;
			//case R.id.mainmenu_button_on:
				//Log.v(TAG, "on button clicked");
				//break;
			//case R.id.mainmenu_button_off:
				//Log.v(TAG, "off button clicked");
				//break;
			
		}
	}
	
	
}