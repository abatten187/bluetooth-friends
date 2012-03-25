package com.alexanderbatten.bluetoothfriends;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;

public class BluetoothService extends Service {
	public static final String TAG = "BluetoothService";
	
	private Context context;
	private DBAdapter db;
	public static PowerManager.WakeLock wakeLock;
	private BluetoothAdapter bluetoothAdapter;
	private Vibrator vibrator;
	
	@Override
	public IBinder onBind(Intent arg0) {
		Log.i(TAG, "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
		context = this;
		
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
        // Register the BroadcastReceiver
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
        
        //Initialise Vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
        db = new DBAdapter(context);
        db.open();
        
        Log.v(TAG, "Bluetooth State: '"+ bluetoothAdapter.getState() +"'");
        Log.v(TAG, "Discovery state: "+ bluetoothAdapter.startDiscovery());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
		
		if(wakeLock != null){
			if(wakeLock.isHeld()) {
				Log.v(TAG, "Releasing BluetoothService WakeLock");
				wakeLock.release();
			}
			wakeLock = null;
		}
		
		this.unregisterReceiver(mReceiver);
		db.close();
	}
    
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice seenDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); 
                Device device = db.getDevice(seenDevice.getAddress());
                
                
                if (device == null){
                	//Add New Device
                	BluetoothClass seenClass = seenDevice.getBluetoothClass();
                	
                	try{
	                	db.addDeviceToDB(new Device(seenDevice.getAddress(), seenDevice.getName(), null, seenClass.getDeviceClass(), null));
	                	device = db.getDevice(seenDevice.getAddress());
                	} catch (Exception e){
                		Log.e(TAG, "Error Add device to DB: "+ e.getMessage());
                	}
                } else {
                	//Device already exists, update details if any are wrong
                	
                	try {
	                	if(!(seenDevice.getName().replaceAll("[^a-zA-Z0-9]","").equals(device.getDeviceName()) && seenDevice.getBluetoothClass().getDeviceClass() == device.getDeviceClass()))
	                		db.updateDevice(device.getDeviceMacAddress(), seenDevice.getName(), seenDevice.getBluetoothClass().getDeviceClass());
                	} catch (Exception e){
                		Log.e(TAG, "Error Update device: "+ e.getMessage());
                	}
                }
                      
                try {
	                //boolean result = db.addEncounter(new Encounter(device.getDeviceMacAddress(), new Date().getTime(), lastLoc.getLatitude(), lastLoc.getLongitude(), lastLoc.getAccuracy()));
	                if (db.handleEncoutner(device, context)){
	                	if (Settings.getVibrate(context)) vibrator.vibrate(100);
	                	if (Settings.getNotification(context)){
		                	NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		                	
		                	int icon = R.drawable.notification_bluetoothfriends;
		                	CharSequence tickerText = "New Encounter with "+ device.getCustomOrNameOrMac();
		                	long when = System.currentTimeMillis();
		                	CharSequence contentTitle = "New Encounter";
		                	CharSequence contentText = device.getCustomOrNameOrMac();
		                	
		                	Intent notificationIntent = new Intent(context, ViewDevice.class);
		                	notificationIntent.putExtra("deviceMac", device.getDeviceMacAddress());
		                	PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		                	
		                	Notification notification = new Notification(icon, tickerText, when);
		                	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		                	
		                	notificationManager.notify(187, notification);
	                	}
	                }
                } catch (Exception e) {
                	Log.e(TAG, "Error add encounter: "+ e.getMessage());
                }
            }
            
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            	Log.i(TAG, "DISCOVERY FINISHED!");
            	stopSelf();
            }
            
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
            	Log.i(TAG, "DISCOVERY started");
            }
        }
    };
}
