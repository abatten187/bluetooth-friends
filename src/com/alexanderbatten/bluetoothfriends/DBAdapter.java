package com.alexanderbatten.bluetoothfriends;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class DBAdapter {
	private static final String TAG = "DBAdapter";

	private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context context;
	
	private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "data.sqlite";
    
    private static final String TABLE_DEVICES = "devices";
    private static final String TABLE_ENCOUNTERS = "encounters";
    
    public static final String KEY_DEVICES_MAC = "macAddress";
    public static final String KEY_DEVICES_NAME = "name";
    public static final String KEY_DEVICES_CUSTOMNAME = "customName";
    public static final String KEY_DEVICES_CLASS = "class";
    public static final String KEY_DEVICES_UPLOADED = "uploaded";
	
    public static final String KEY_ENCOUNTERS_DEVICEMAC = "macAddress";
    public static final String KEY_ENCOUNTERS_STARTTIME = "startTime";
    public static final String KEY_ENCOUNTERS_FINISHTIME = "finishTime";
    public static final String KEY_ENCOUNTERS_LOCATIONLAT = "latitude";
    public static final String KEY_ENCOUNTERS_LOCATIONLON = "longitude";
    public static final String KEY_ENCOUNTERS_LOCATIONACC = "accuracy";
    public static final String KEY_ENCOUNTERS_UPLOADED = "uploaded";
	
    private static final String CREATE_TABLE_DEVICES = "CREATE TABLE IF NOT EXISTS "+ TABLE_DEVICES +" ("+ 
																	KEY_DEVICES_MAC +" TEXT,"+ 
																	KEY_DEVICES_NAME +" TEXT,"+
																	KEY_DEVICES_CUSTOMNAME +" TEXT,"+
																	KEY_DEVICES_CLASS +" INTEGER,"+
																	KEY_DEVICES_UPLOADED +" INTEGER," +
																	"PRIMARY KEY ("+ KEY_DEVICES_MAC +" ASC));";
    
	private static final String CREATE_TABLE_ENCOUNTERS = "CREATE TABLE IF NOT EXISTS "+ TABLE_ENCOUNTERS +" ("+
																	KEY_ENCOUNTERS_DEVICEMAC +" TEXT, "+
																	KEY_ENCOUNTERS_STARTTIME +" TEXT, "+
																	KEY_ENCOUNTERS_FINISHTIME +" TEXT, "+
																	KEY_ENCOUNTERS_LOCATIONLAT +" TEXT, "+
																	KEY_ENCOUNTERS_LOCATIONLON +" TEXT, "+
																	KEY_ENCOUNTERS_LOCATIONACC +" TEXT,"+
																	KEY_ENCOUNTERS_UPLOADED +" INTEGER,"+
																	"PRIMARY KEY ("+ KEY_ENCOUNTERS_DEVICEMAC +" ASC, "+ KEY_ENCOUNTERS_STARTTIME +" ASC));";
	
    private static class DatabaseHelper extends SQLiteOpenHelper {
	    DatabaseHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
	    
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "onCreate");
	        db.execSQL(CREATE_TABLE_DEVICES);
	        db.execSQL(CREATE_TABLE_ENCOUNTERS);
	        
	        //db.execSQL("CREATE INDEX INDEX_"+ KEY_DEVICES_MAC +" ON "+ TABLE_DEVICES +"("+ KEY_DEVICES_MAC +")");
	       // db.execSQL("CREATE INDEX INDEX_"+ KEY_ENCOUNTERS_DEVICEMAC +" ON "+ TABLE_ENCOUNTERS +"("+ KEY_ENCOUNTERS_DEVICEMAC +")");
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i(TAG, "onUpgrade");
	        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_DEVICES);
	        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_ENCOUNTERS);
	        
	        onCreate(db);
		}
    }

    public DBAdapter(Context context) {
        this.context = context;
    }
    
    public DBAdapter open() throws SQLException {
    	//Log.i(TAG, "Open Database");
        mDbHelper = new DatabaseHelper(context);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
    	//Log.i(TAG, "Close Database");
        mDbHelper.close();
    } 
  
    public Device getDevice(String macAddress){
    	//Log.i(TAG, "getDevice");
    	Device returnDevice = null;
    	
    	Cursor c = mDb.query(TABLE_DEVICES, new String[]{KEY_DEVICES_MAC, KEY_DEVICES_NAME, KEY_DEVICES_CUSTOMNAME, KEY_DEVICES_CLASS}, 
    						KEY_DEVICES_MAC +" = ?", 
    						new String[]{macAddress}, null, null, null);
    	
    	if(c != null){
    		//If the device exists
    		if(c.moveToFirst()){
    			returnDevice = new Device(c.getString(0), c.getString(1), c.getString(2), c.getInt(3), null);
    		}
    	}
    		
    	c.close();
    	return returnDevice;
    }
    
    /**
     * Retrieves all the encounters for a particular deviceID
     * @param deviceID Integer
     * @return encounters List
     */
    public List<Encounter> getEncounters(Device device){
    	//Log.i(TAG, "getEncounters");
    	List<Encounter> returnEncounters = new ArrayList<Encounter>();
    	
    	Cursor c = mDb.query(TABLE_ENCOUNTERS, new String[]{KEY_ENCOUNTERS_DEVICEMAC, KEY_ENCOUNTERS_STARTTIME, KEY_ENCOUNTERS_FINISHTIME, KEY_ENCOUNTERS_LOCATIONLAT, KEY_ENCOUNTERS_LOCATIONLON, KEY_ENCOUNTERS_LOCATIONACC}, 
    			KEY_ENCOUNTERS_DEVICEMAC +" = ?", 
    			new String[]{device.getDeviceMacAddress()}, null, null, KEY_ENCOUNTERS_STARTTIME +" DESC");
    	
    	if(c != null){
    		while(c.moveToNext()){
    			returnEncounters.add(new Encounter(c.getString(0), new Date(c.getLong(1)), new Date(c.getLong(2)), c.getDouble(3), c.getDouble(4), c.getFloat(5)));
    		}
    	}
    	
    	c.close();
    	return returnEncounters;
    }
    
    public Encounter getFirstEncounter(Device device){
    	Encounter output = null;
    	
    	Cursor c = mDb.query(TABLE_ENCOUNTERS, new String[]{KEY_ENCOUNTERS_STARTTIME, KEY_ENCOUNTERS_FINISHTIME, KEY_ENCOUNTERS_LOCATIONLAT, KEY_ENCOUNTERS_LOCATIONLON, KEY_ENCOUNTERS_LOCATIONACC}, KEY_ENCOUNTERS_DEVICEMAC +" = ?", new String[]{device.getDeviceMacAddress()}, null, null, KEY_ENCOUNTERS_STARTTIME +" ASC");
    	if(c != null){
    		if(c.moveToFirst())	{
    			output = new Encounter(device.getDeviceMacAddress(), new Date(c.getLong(0)), new Date(c.getLong(1)), c.getDouble(2), c.getDouble(3), c.getFloat(4));
    		}
    	}
    	c.close();
    	return output;
    }
    
    public Encounter getLastEncounter(Device device){
    	Encounter output = null;
    	
    	Cursor c = mDb.query(TABLE_ENCOUNTERS, new String[]{KEY_ENCOUNTERS_STARTTIME, KEY_ENCOUNTERS_FINISHTIME, KEY_ENCOUNTERS_LOCATIONLAT, KEY_ENCOUNTERS_LOCATIONLON, KEY_ENCOUNTERS_LOCATIONACC}, KEY_ENCOUNTERS_DEVICEMAC +" = ?", new String[]{device.getDeviceMacAddress()}, null, null, KEY_ENCOUNTERS_STARTTIME +" DESC");
    	if(c != null){
    		if(c.moveToFirst())	{
    			output = new Encounter(device.getDeviceMacAddress(), new Date(c.getLong(0)), new Date(c.getLong(1)), c.getDouble(2), c.getDouble(3), c.getFloat(4));
    		}
    	}
    	c.close();
    	return output;
    }

    /*
	public List<Device> getAllDevices() {
		List<Device> output = new ArrayList<Device>();
		
		Cursor c = mDb.query(TABLE_DEVICES, new String[]{KEY_DEVICES_MAC, KEY_DEVICES_NAME, KEY_DEVICES_CUSTOMNAME, KEY_DEVICES_CLASS}, 
				null, null, null, null, null);
		
		if(c != null){
			while(c.moveToNext()){
				Device tempDevice = new Device(c.getString(0), c.getString(1), c.getString(2), c.getInt(3), null);
				tempDevice.setDeviceEncounters(getEncounters(tempDevice));
				output.add(tempDevice);
			}
		}
		
		Collections.sort(output, new Comparator<Device>() {
			public int compare(Device d1, Device d2){
				if (d1.getEncounterCount() > d2.getEncounterCount()) return -1;
				else if (d1.getEncounterCount() < d2.getEncounterCount()) return 1;
				else {
					if (d1.getLastEncounter() > d2.getLastEncounter()) return -1;
					else if (d1.getLastEncounter() < d2.getLastEncounter()) return 1;
					else return 0;
				}
			}
		});
		
		c.close();
		return output;
	}*/
    
	public List<Device> getAllDevices() {
		List<Device> output = getAllDevicesOrderByDuration();
		Date now = new Date();
		Log.d(TAG, KEY_ENCOUNTERS_FINISHTIME +" > "+ now.getTime() +" - "+ Settings.getScanInterval(context));
		Cursor c = mDb.query(TABLE_ENCOUNTERS, new String[]{KEY_ENCOUNTERS_DEVICEMAC}, KEY_ENCOUNTERS_FINISHTIME +" > "+ now.getTime() +" - "+ Settings.getScanInterval(context) , null, null, null, null);
		
		if(c != null){
			while(c.moveToNext()){
				Device tempDevice = getDevice(c.getString(0));
				output.remove(tempDevice);
				output.add(0, tempDevice);
				Log.d(TAG, "current"+ c.getString(0));
			}
		}
		
		return output;
	}
	
	public List<Device> getAllDevicesOrderByDuration() {
		List<Device> output = new ArrayList<Device>();
		Cursor c = mDb.rawQuery("SELECT "+ KEY_ENCOUNTERS_DEVICEMAC +", duration FROM (SELECT "+ KEY_ENCOUNTERS_DEVICEMAC +", "+ KEY_ENCOUNTERS_FINISHTIME +"-"+ KEY_ENCOUNTERS_STARTTIME +" as duration FROM "+ TABLE_ENCOUNTERS +") GROUP BY "+ KEY_ENCOUNTERS_DEVICEMAC +" ORDER BY sum(duration) desc;", null);
		
		if(c != null){
			while(c.moveToNext()){
				String mac = c.getString(0);
				
				long duration = c.getLong(1);
				if (duration > 300000){
					Device device = getDevice(mac);
					output.add(device);
				}
			}
		}
		
		c.close();
		return output;
	}

	/*  TODO: REDUNDANT CODE??
	public boolean addEncounter(Encounter encounter) {
		//Log.i(TAG, "addEncounter");
		Device device = this.getDevice(encounter.getEncounterMac());
		
		if(encounter.getEncounterDateTime() - device.getLastEncounter() > ENCOUNTER_BUFFER_DIFFERENCE) {
			Log.v(TAG, device.getDeviceMacAddress() +" - Inserting new encounter.");
			addEncounterToDB(encounter);
			return true;
		} else {
			Log.v(TAG, device.getDeviceMacAddress() +" - Already encountered within buffer time.");
			return false;
		}
	}*/
	
	public boolean handleEncoutner(Device device, Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Location lastLoc = locationManager.getLastKnownLocation("passive");
		Encounter lastEncounter = getLastEncounter(device);
		Date now = new Date();
		
		if (lastEncounter == null || lastEncounter.getFinishtime().getTime() + Settings.getMinimumTimeBetweenEncounters(context) < now.getTime()){
			Log.v(TAG, "Beginning a new encounter with "+ device.getCustomOrNameOrMac());
			addEncounterToDB(new Encounter(device.getDeviceMacAddress(), now, now, lastLoc.getLatitude(), lastLoc.getLongitude(), lastLoc.getAccuracy()));
			return true;
		} else {
			Log.v(TAG, "Continuing encounter with "+ device.getCustomOrNameOrMac() +" ("+ (now.getTime() - lastEncounter.getStarttime().getTime())/1000  +")");
			updateEncounterToDB(new Encounter(lastEncounter.getMac(), lastEncounter.getStarttime(), now, lastLoc.getLatitude(), lastLoc.getLongitude(), lastLoc.getAccuracy()));
			return false;
		}
	}	

	public long addDeviceToDB(Device device) {
		//Log.i(TAG, "addDeviceToDB");
		ContentValues values = new ContentValues();
        values.put(KEY_DEVICES_MAC, device.getDeviceMacAddress());
        values.put(KEY_DEVICES_NAME, device.getDeviceName());
        values.put(KEY_DEVICES_CUSTOMNAME, device.getDeviceCustomName());
        values.put(KEY_DEVICES_CLASS, device.getDeviceClass());
        values.put(KEY_DEVICES_UPLOADED, false);
		return mDb.insert(TABLE_DEVICES, null, values);
	}

	public long addEncounterToDB(Encounter encounter) {
		//Log.i(TAG, "addEncountertoDB");
		ContentValues values = new ContentValues();
        values.put(KEY_ENCOUNTERS_DEVICEMAC, encounter.getMac());
        values.put(KEY_ENCOUNTERS_STARTTIME, encounter.getStarttime().getTime());
        values.put(KEY_ENCOUNTERS_FINISHTIME, encounter.getFinishtime().getTime());
        values.put(KEY_ENCOUNTERS_LOCATIONLAT, encounter.getLatitude());
        values.put(KEY_ENCOUNTERS_LOCATIONLON, encounter.getLongitude());
        values.put(KEY_ENCOUNTERS_LOCATIONACC, encounter.getAccuracy());
        values.put(KEY_ENCOUNTERS_UPLOADED, false);
        return mDb.insert(TABLE_ENCOUNTERS, null, values);
	}
	
	private long updateEncounterToDB(Encounter encounter) {
		//Log.i(TAG, "updateEncounterToDB");
		ContentValues values = new ContentValues();
		values.put(KEY_ENCOUNTERS_FINISHTIME, encounter.getFinishtime().getTime());
		values.put(KEY_ENCOUNTERS_LOCATIONLAT, encounter.getLatitude());
		values.put(KEY_ENCOUNTERS_LOCATIONLON, encounter.getLongitude());
		values.put(KEY_ENCOUNTERS_LOCATIONACC, encounter.getAccuracy());
		return mDb.update(TABLE_ENCOUNTERS, values, KEY_ENCOUNTERS_DEVICEMAC +" = ? AND "+ KEY_ENCOUNTERS_STARTTIME +" = ?" , new String[]{encounter.getMac(), Long.toString(encounter.getStarttime().getTime())});
	}

	public long updateDevice(String deviceMac, String deviceName, int deviceClass) {
		Log.i(TAG, "updateDevice '"+ deviceName +"'");
		ContentValues values = new ContentValues();
		values.put(KEY_DEVICES_NAME, deviceName);
		values.put(KEY_DEVICES_CLASS, deviceClass);
		values.put(KEY_DEVICES_UPLOADED, false);
		return mDb.update(TABLE_DEVICES, values, KEY_DEVICES_MAC +" = ?", new String[]{deviceMac});
	}

	public List<Device> getAllNotUploadedDevices() {
		List<Device> output = new ArrayList<Device>();
		
		Cursor c = mDb.query(TABLE_DEVICES, new String[]{KEY_DEVICES_MAC, KEY_DEVICES_NAME, KEY_DEVICES_CUSTOMNAME, KEY_DEVICES_CLASS}, 
				KEY_DEVICES_UPLOADED +" = 0", null, null, null, null);
		
		if(c != null){
			while(c.moveToNext()){
				output.add(new Device(c.getString(0), c.getString(1), c.getString(2), c.getInt(3), null));
			}
		}
		
		c.close();
		return output;
	}

	public long setDeviceUploaded(Device device, boolean b) {
		ContentValues values = new ContentValues();
		if (b) values.put(KEY_DEVICES_UPLOADED, 1);
		else values.put(KEY_DEVICES_UPLOADED, 0);
		return mDb.update(TABLE_DEVICES, values, KEY_DEVICES_MAC +" = ?", new String[]{device.getDeviceMacAddress()});
	}
	
	public void refreshCustomNames(){
		String val = null;
		ContentValues values = new ContentValues();
		values.put(KEY_DEVICES_CUSTOMNAME, val);
		mDb.update(TABLE_DEVICES, values, null, null);
	}
	
	public void refreshNames(){
		String val = null;
		ContentValues values = new ContentValues();
		values.put(KEY_DEVICES_NAME, val);
		mDb.update(TABLE_DEVICES, values, KEY_DEVICES_NAME +" = ''", null);
	}

	public List<Encounter> getAllNotUploadedEncounters() {
		List<Encounter> output = new ArrayList<Encounter>();
		
		Cursor c = mDb.query(TABLE_ENCOUNTERS, new String[]{KEY_ENCOUNTERS_DEVICEMAC, KEY_ENCOUNTERS_STARTTIME, KEY_ENCOUNTERS_FINISHTIME, KEY_ENCOUNTERS_LOCATIONLAT, KEY_ENCOUNTERS_LOCATIONLON, KEY_ENCOUNTERS_LOCATIONACC}, 
				KEY_ENCOUNTERS_UPLOADED +" = 0", null, null, null, null);
		
		if(c != null){
			while(c.moveToNext()){
				output.add(new Encounter(c.getString(0), new Date(c.getLong(1)), new Date(c.getLong(2)), c.getDouble(3), c.getDouble(4), c.getFloat(5)));
			}
		}
		
		c.close();
		return output;
	}

	public long setEncounterUploaded(Encounter encounter, boolean b) {
		ContentValues values = new ContentValues();
		if (b) values.put(KEY_ENCOUNTERS_UPLOADED, 1);
		else values.put(KEY_ENCOUNTERS_UPLOADED, 0);
		return mDb.update(TABLE_ENCOUNTERS, values, KEY_ENCOUNTERS_DEVICEMAC +" = ? AND "+ KEY_ENCOUNTERS_STARTTIME +" = ?", new String[]{encounter.getMac(), Long.toString(encounter.getStarttime().getTime())});
	}

	public Encounter getEncounter(String encounterMac, long encounterDateTime) {
    	//Log.i(TAG, "getEncounter");
    	Encounter returnEncounter = null;
    	
    	Cursor c = mDb.query(TABLE_ENCOUNTERS, new String[]{KEY_ENCOUNTERS_DEVICEMAC, KEY_ENCOUNTERS_STARTTIME, KEY_ENCOUNTERS_FINISHTIME, KEY_ENCOUNTERS_LOCATIONLAT, KEY_ENCOUNTERS_LOCATIONLON, KEY_ENCOUNTERS_LOCATIONACC, KEY_ENCOUNTERS_UPLOADED}, 
    						KEY_ENCOUNTERS_DEVICEMAC +" = ? AND "+ KEY_ENCOUNTERS_STARTTIME +" = ?", 
    						new String[]{encounterMac, Long.toString(encounterDateTime)}, null, null, null);
    	
    	if(c != null){
    		//If the encounter exists
    		if(c.moveToFirst()){
    			returnEncounter = new Encounter(c.getString(0), new Date(c.getLong(1)), new Date(c.getLong(2)), c.getDouble(3), c.getDouble(4), c.getFloat(5));
    		}
    	}
    		
    	c.close();
    	return returnEncounter;
	}

	public CharSequence getLastEncounterToString(Device device) {
		Encounter encounter = getLastEncounter(device);
		
        long now = new Date().getTime();
        long lastEncounter = encounter.getFinishtime().getTime();
        long diff = now - lastEncounter;
        long diffSec = diff / (1000);
        long diffMin = diff / (1000 * 60);
        long diffHour = diff / (1000 * 60 * 60);
        long diffDay = diff / (1000 * 60 * 60 * 24);
        if (diffDay > 28) 			return "long time";
        else if (diffDay >   1) 	return diffDay +" days ago";
        else if (diffDay ==  1) 	return diffDay +" day ago";
        else if (diffHour >  1) 	return diffHour +" hours ago";
        else if (diffHour == 1) 	return diffHour +" hour ago";
        else if (diffMin >   1) 	return diffMin +" minutes ago";
        else if (diffMin ==  1) 	return diffMin +" minute ago";
        else if (diffSec >   0) 	return "Now";
		return "unknown";
	}

	public long getTotalEncounterDuration(Device device) {
		long output = 0;
		Cursor c = mDb.rawQuery("SELECT sum(duration) FROM (SELECT "+ KEY_ENCOUNTERS_FINISHTIME +"-"+ KEY_ENCOUNTERS_STARTTIME +" as duration FROM "+ TABLE_ENCOUNTERS +" WHERE "+ KEY_ENCOUNTERS_DEVICEMAC +" = ?);", new String[]{device.getDeviceMacAddress()});
		
		if(c != null){
			if(c.moveToNext()){
				output = c.getLong(0);
			}
		}
		
		c.close();
		return output;
	}

	public int getRelationshipProgress(Device device) {
		long duration = getTotalEncounterDuration(device);
		return (int) (duration/300000);
	}

	public boolean isEncountering(Device d) {
		Encounter e = getLastEncounter(d);
		Date lastSeen = e.getFinishtime();
		Date now = new Date();
		long diff = now.getTime() - lastSeen.getTime();
		
		if(diff > Settings.getScanInterval(context) * 2) return false;
		return true;
	}
}
