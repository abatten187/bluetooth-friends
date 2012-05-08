package com.alexanderbatten.bluetoothfriends;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class Settings extends PreferenceActivity {
	private static final String TAG = "PreferenceActivity";
	private static final String OPT_DEVICENAME = "setting_deviceName";
	private static final String OPT_DEVICENAME_DEF = "ForeverAlone";
	private static final String OPT_SCANINTERVAL = "setting_scanInterval";
	private static final String OPT_SCANINTERVAL_DEF = "30000";
	private static final String OPT_BACKGROUNDRUN = "setting_backgroundRun";
	private static final boolean OPT_BACKGROUNDRUN_DEF = true;
	private static final String OPT_VIBRATE = "setting_vibrate";
	private static final boolean OPT_VIBRATE_DEF = false;
	private static final String OPT_NOTIFICATION = "setting_notification";
	private static final boolean OPT_NOTIFICATION_DEF = false;
	
	private static final long MINIMUM_TIME_BETWEEN_ENCOUNTERS = AlarmManager.INTERVAL_HALF_HOUR;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addPreferencesFromResource(R.xml.settings);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		startBluetoothScanAlarmManager(MainMenu.ACTIVE_INSTANCE);
		startServerUploadAlarmManager(MainMenu.ACTIVE_INSTANCE);
	}

	public static void startBluetoothScanAlarmManager(Context context){
		Log.i(TAG, "startBluetoothAlarmManager");
		Calendar btsCal = Calendar.getInstance();
		long btsInterval = Settings.getScanInterval(context);
		btsCal.add(Calendar.SECOND, 5);
		Intent btsIntent = new Intent(context, BluetoothFriendsReceiver.class);
		btsIntent.setAction(BluetoothFriendsReceiver.ACTION_BLUETOOTHSCAN);
		PendingIntent btsSender = PendingIntent.getBroadcast(context, 1871, btsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, btsCal.getTimeInMillis(), btsInterval, btsSender);
	}
	
	public static void stopBluetoothScanAlarmManager(Context context){
		Log.i(TAG, "stopBluetoothAlarmManager");
		Intent btsIntent = new Intent(context, BluetoothFriendsReceiver.class);
		btsIntent.setAction(BluetoothFriendsReceiver.ACTION_BLUETOOTHSCAN);
		PendingIntent btsSender = PendingIntent.getBroadcast(context, 1871, btsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(btsSender);
	}
	
	public static void startServerUploadAlarmManager(Context context){
		/* TODO: Disabled due to new model.
		Log.i(TAG, "startServerUploadAlarmManager");
		Calendar suCal = Calendar.getInstance();
		long suInterval = Settings.getServerUploadInterval(context);
		suCal.add(Calendar.SECOND, 5);
		Intent suIntent = new Intent(context, BluetoothFriendsReceiver.class);
		suIntent.setAction(BluetoothFriendsReceiver.ACTION_SERVERUPLOAD);
		PendingIntent suSender = PendingIntent.getBroadcast(context, 1872, suIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, suCal.getTimeInMillis(), suInterval, suSender);
		*/
	}
	
	public static void stopServerUploadAlarmManager(Context context){
		Log.i(TAG, "stopServerUploadAlarmManager");
		Intent suIntent = new Intent(context, BluetoothFriendsReceiver.class);
		suIntent.setAction(BluetoothFriendsReceiver.ACTION_SERVERUPLOAD);
		PendingIntent suSender = PendingIntent.getBroadcast(context, 1872, suIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(suSender);
	}

	public static String getDeviceName(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context).getString(OPT_DEVICENAME, OPT_DEVICENAME_DEF).replaceAll("[^a-zA-Z0-9]","");
	}

	public static long getScanInterval(Context context){
		return Long.parseLong(PreferenceManager.getDefaultSharedPreferences(context).getString(OPT_SCANINTERVAL, OPT_SCANINTERVAL_DEF));
	}
	
	public static long getServerUploadInterval(Context context){
		return AlarmManager.INTERVAL_HOUR;
	}
	
	public static boolean getBackgroundRun(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_BACKGROUNDRUN, OPT_BACKGROUNDRUN_DEF);
	}
	
	public static boolean getVibrate(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_VIBRATE, OPT_VIBRATE_DEF);
	}
	
	public static boolean getNotification(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_NOTIFICATION, OPT_NOTIFICATION_DEF);
	}
	
	public static long getMinimumTimeBetweenEncounters(Context context){
		return MINIMUM_TIME_BETWEEN_ENCOUNTERS;
	}
}
