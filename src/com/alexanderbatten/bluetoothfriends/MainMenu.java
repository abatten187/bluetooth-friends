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

public class MainMenu extends Activity
{
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
	
	public void onViewAllClick(View v)
	{
		Log.v(TAG, "ViewAll button clicked");
		startActivity(new Intent(this, ViewAllDevices.class));
	}
	
	public void onUploadClick(View v)
	{
		//Log.v(TAG, "Upload button clicked");
		//new UploadTest(this).execute();
	}
	
	public void onLeaderboardClick(View v)
	{
		Log.v(TAG, "Leaderboard button clicked");
		this.startActivity(new Intent(this, OnlineLeaderboard.class));
	}
	
	public void onSettingsClick(View v)
	{
		Log.v(TAG, "Settings button clicked");
		this.startActivity(new Intent(this, Settings.class));
	}
	
	public void onQuitClick(View v)
	{
		//Log.v(TAG, "Quit button clicked");
		//this.finish();
	}
	
	public void onOnClick(View v)
	{
		Log.v(TAG, "on button clicked");
	}	
	
	public void onOffClick(View v)
	{
		Log.v(TAG, "on button clicked");
	}
}