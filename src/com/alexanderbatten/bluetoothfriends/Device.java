package com.alexanderbatten.bluetoothfriends;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Device {
	private String deviceMacAddress;
	private String deviceName;
	private String deviceCustomName;
	private int deviceClass;
	private List<Encounter> deviceEncounters;
	
	public Device(String deviceMacAddress, String deviceName, String deviceCustomName, int deviceClass, List<Encounter> deviceEncounters) {
		super();
		this.deviceMacAddress = deviceMacAddress;
		this.deviceName = deviceName;
		this.deviceCustomName = deviceCustomName;
		this.deviceClass = deviceClass;
		this.deviceEncounters = deviceEncounters;
	}
	
	public int getEncounterCount(){
		return deviceEncounters.size();
	}
	
	public long getFirstEncounter() {
		sortEncounters();
		if (deviceEncounters.size() > 0) return deviceEncounters.get(deviceEncounters.size()-1).getStarttime().getTime();
		return 0;
	}
	
	//public long getLastEncounter() {
		//sortEncounters();
		//if (deviceEncounters.size() > 0) return deviceEncounters.get(0).getFinishtime().getTime();
		//return 0;
		
	//}
	
	private void sortEncounters(){
		Collections.sort(deviceEncounters, new Comparator<Encounter>() {
			public int compare(Encounter e1, Encounter e2){
				if (e1.getStarttime().getTime() > e2.getStarttime().getTime()) return -1;
				else if (e1.getStarttime().getTime() < e2.getStarttime().getTime()) return 1;
				else return 0;
			}
		});
	}
	
	public String getDeviceClassImage() {
		//Log.v(TAG, "DeviceClass = "+ deviceClass);
		switch (deviceClass){
			//Computers
			case BluetoothClass.Device.COMPUTER_DESKTOP:
			case BluetoothClass.Device.COMPUTER_HANDHELD_PC_PDA:
			case BluetoothClass.Device.COMPUTER_LAPTOP:
			case BluetoothClass.Device.COMPUTER_PALM_SIZE_PC_PDA:
			case BluetoothClass.Device.COMPUTER_SERVER:
			case BluetoothClass.Device.COMPUTER_UNCATEGORIZED:
			case BluetoothClass.Device.COMPUTER_WEARABLE:
			return "images/computer.png";

			//Mobile Phones
			case BluetoothClass.Device.PHONE_CELLULAR:
			case BluetoothClass.Device.PHONE_CORDLESS:
			case BluetoothClass.Device.PHONE_ISDN:
			case BluetoothClass.Device.PHONE_MODEM_OR_GATEWAY:
			case BluetoothClass.Device.PHONE_SMART:
			case BluetoothClass.Device.PHONE_UNCATEGORIZED:
			return "images/mobile.png";
				
			//Audio Devices
			case BluetoothClass.Device.AUDIO_VIDEO_CAMCORDER:
			case BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO:
			case BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE:
			case BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES:
			case BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO:
			case BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER:
			case BluetoothClass.Device.AUDIO_VIDEO_MICROPHONE:
			case BluetoothClass.Device.AUDIO_VIDEO_PORTABLE_AUDIO:
			case BluetoothClass.Device.AUDIO_VIDEO_SET_TOP_BOX:
			case BluetoothClass.Device.AUDIO_VIDEO_UNCATEGORIZED:
				return "images/headphone.png";
		}
			
		return "images/unknown.png"; //display nothing
	}
	
	public CharSequence getCustomOrNameOrMac() {
        if (getDeviceCustomName() != null) return getDeviceCustomName();
        else if (getDeviceName() != null) return getDeviceName();
        else return getDeviceMacAddress();
	}
	
	public Bitmap getIcon(Context context) {
        try {
        	String imgpath = getDeviceClassImage();
        	return BitmapFactory.decodeStream(context.getResources().getAssets().open(imgpath));
        } catch (IOException e) {
        	e.printStackTrace();
        }
		return null;
	}

	public void getFullView(){
		//TODO: Develop function to get full view (bluetooth friend view) and summary view (for list)
	}
	
	public void getSummaryView(){
		//TODO: Develop function to get full view (bluetooth friend view) and summary view (for list)
	}
	
	/* ================= Getters and setters ====================== */
	public String getDeviceMacAddress() {
		return deviceMacAddress;
	}
	public void setDeviceMacAddress(String deviceMacAddress) {
		this.deviceMacAddress = deviceMacAddress;
	}
	public String getDeviceName() {
		if (deviceName == null) return null;
		return deviceName.replaceAll("[^a-zA-Z0-9]","");
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDeviceCustomName() {
		if (deviceCustomName == null) return null;
		return deviceCustomName.replaceAll("[^a-zA-Z0-9]","");
	}
	public void setDeviceCustomName(String deviceCustomName) {
		this.deviceCustomName = deviceCustomName;
	}
	public int getDeviceClass() {
		return deviceClass;
	}
	public void setDeviceClass(int deviceClass) {
		this.deviceClass = deviceClass;
	}
	public List<Encounter> getDeviceEncounters() {
		return deviceEncounters;
	}
	public void setDeviceEncounters(List<Encounter> deviceEncounters) {
		this.deviceEncounters = deviceEncounters;
	}
}
