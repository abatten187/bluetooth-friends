package com.alexanderbatten.bluetoothfriends;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings.Secure;
import android.util.Log;

public class ServerService extends Service {
	public static final String TAG = "ServerService";
	
	private Context context;
	public static PowerManager.WakeLock wakeLock;
	
	private String android_id;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
		context = this;
		
		android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		
		DBAdapter db = new DBAdapter(context);
		db.open(); 

		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 20000);
		
		HttpResponse response;
		JSONObject json = new JSONObject();
		try {
			URI url = new URI("http","alexanderbatten.com","/sandbox/bluetoothfriends/uploadTest.php","deviceID="+ android_id,null);
			
			HttpPost post = new HttpPost(url);
			
			//SETTINGS
			JSONObject json_settings = new JSONObject();
			json_settings.put("deviceName", Settings.getDeviceName(context));
			json_settings.put("version", MainMenu.version);
			json_settings.put("scanInterval", Settings.getScanInterval(context));
			if (Settings.getBackgroundRun(context)) json_settings.put("runInBackground", 1); else json_settings.put("runInBackground", 0);
			if (Settings.getVibrate(context)) json_settings.put("vibrateOnEncounter", 1); else json_settings.put("vibrateOnEncounter", 0);
			json.put("settings", json_settings);
			Log.v(TAG, json_settings.toString());
			
			//DEVICES
			int deviceCount = 0;
			JSONObject json_devices = new JSONObject();
			List<Device> devices = db.getAllNotUploadedDevices();
			Log.v(TAG, devices.size() +" devices pending to be uploaded..");
			for(Device device: devices){
				JSONObject json_obj = new JSONObject();
				json_obj.put("mac", device.getDeviceMacAddress());
				json_obj.put("name", device.getDeviceName());
				json_obj.put("customName", device.getDeviceCustomName());
				json_obj.put("class", device.getDeviceClass());
				json_devices.put(Integer.toString(deviceCount++), json_obj);
				
				if (deviceCount >= 500) break;
			}
			json.put("devices", json_devices);
			Log.v(TAG, json_devices.toString());
			
			//ENCOUNTERS
			int encounterCount = 0;
			JSONObject json_encounters = new JSONObject();
			List<Encounter> encounters = db.getAllNotUploadedEncounters();
			Log.v(TAG, encounters.size() +" encounters pending to be uploaded..");
			for(Encounter encounter: encounters){
				JSONObject json_obj = new JSONObject();
				json_obj.put("mac", encounter.getMac());
				json_obj.put("starttime", Long.toString(encounter.getStarttime().getTime()));
				json_obj.put("finishtime", Long.toString(encounter.getFinishtime().getTime()));
				json_obj.put("latitude", encounter.getLatitude());
				json_obj.put("longitude", encounter.getLongitude());
				json_obj.put("accuracy", encounter.getAccuracy());
				json_encounters.put(Integer.toString(encounterCount++), json_obj);
				
				if(encounterCount >= 500) break;
			}
			json.put("encounters", json_encounters);
			Log.v(TAG, json_encounters.toString());
			
			StringEntity se = new StringEntity(json.toString());
			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			post.setEntity(se);
			
			if (deviceCount + encounterCount >= 0){ //TODO: think of smarter way to save bandwidth
				Log.v(TAG, "Uploading data.. "+ json.toString());
				Log.v(TAG, "URL: '"+ url +"'");
				response = client.execute(post);
			} else {
				Log.w(TAG, "Nothing to upload!");
				response = null;
			}
			
			if (response != null){
				InputStream in = response.getEntity().getContent();
				String a = convertStreamToString(in);
				Log.v(TAG, "Server response.. "+ a);
				
				JSONObject json_response = new JSONObject(a); 
				
				//Extract successful devices
				if (!json_response.getString("devices").equals("[]")){
    				Log.v(TAG, "Extracting successful devices..");
    				JSONObject json_response_devices = new JSONObject(json_response.getString("devices"));
    				Iterator<?> iDevices = json_response_devices.keys();
    				while(iDevices.hasNext()){
    					String deviceMac = (String) iDevices.next();
    					String deviceResponse = json_response_devices.getString(deviceMac);
    					if (deviceResponse.equals("ok")) {
    						db.setDeviceUploaded(db.getDevice(deviceMac), true);
    					} else {
    						db.setDeviceUploaded(db.getDevice(deviceMac), false);
    						Log.w(TAG, deviceResponse);
    					}
    				}
				}
				
				//Extract successful encounters
				if (!json_response.getString("encounters").equals("[]")){
    				Log.v(TAG, "Extracting successful encounters..");
    				JSONArray json_response_encounters = json_response.getJSONArray("encounters");
    				for(int iEncounters = 0; iEncounters < json_response_encounters.length(); iEncounters++){
    					JSONObject encounter = json_response_encounters.getJSONObject(iEncounters);
    					
    					String encounterMac = encounter.getString("mac");
    					long encounterDateTime = encounter.getLong("dateTime");
    					String encounterStatus = encounter.getString("status");
    					
    					if (encounterStatus.equals("ok")) {
    						db.setEncounterUploaded(db.getEncounter(encounterMac, encounterDateTime), true);
    					} else {
    						db.setEncounterUploaded(db.getEncounter(encounterMac, encounterDateTime), false);
    						Log.w(TAG, encounterStatus);
    					}
    				}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Looper.loop();
		db.close();
		Log.i(TAG, "InitiateUpload - complete!");
		this.stopSelf();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
		
		if(wakeLock != null){
			if(wakeLock.isHeld()) {
				Log.v(TAG, "Releasing ServerService WakeLock");
				wakeLock.release();
			}
			wakeLock = null;
		}
	}
    
        
	private String convertStreamToString(InputStream is) {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
