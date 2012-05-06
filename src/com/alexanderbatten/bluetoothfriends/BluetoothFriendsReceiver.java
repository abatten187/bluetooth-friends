package com.alexanderbatten.bluetoothfriends;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class BluetoothFriendsReceiver extends BroadcastReceiver {
	private static final String TAG = "BluetoothFriendsReceiver";
	public static final String ACTION_BLUETOOTHSCAN = "BLUETOOTHFRIENDS_ACTION_BLUETOOTHSCAN";
	public static final String ACTION_SERVERUPLOAD = "BLUETOOTHFRIENDS_ACTION_SERVERUPLOAD";
		
	//public static PowerManager.WakeLock wakeLock = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "onReceive - "+ intent.getAction());
		String action = intent.getAction();
		//TESTING EGIT
		//PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		//wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		//wakeLock.setReferenceCounted(false);
		
		//Log.i(TAG, "isHeld = "+ wakeLock.isHeld());
		
		if (action.equals(ACTION_BLUETOOTHSCAN)){
			context.startService(new Intent(context, BluetoothService.class));
			if(BluetoothService.wakeLock == null){
				PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				BluetoothService.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, BluetoothService.TAG);
			}
			if(!BluetoothService.wakeLock.isHeld()) BluetoothService.wakeLock.acquire(12000);
		} else if (action.equals(Intent.ACTION_BOOT_COMPLETED)){
			if (Settings.getBackgroundRun(context)){
				Settings.startBluetoothScanAlarmManager(context);
				Settings.startServerUploadAlarmManager(context);
			}
		} else if (action.equals(ACTION_SERVERUPLOAD)) {
			context.startService(new Intent(context, ServerService.class));
			if(ServerService.wakeLock == null){
				PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				ServerService.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ServerService.TAG);
			}
			if(!ServerService.wakeLock.isHeld()) ServerService.wakeLock.acquire(20000);
		} else {
			Log.e(TAG, "Unkown action: "+ action);
		}		
	}	
}
