package com.alexanderbatten.bluetoothfriends;

import java.util.Date;

public class Encounter {
	private String mac;
	private Date starttime;
	private Date finishtime;
	private double latitude;
	private double longitude;
	private float accuracy;
	
	public Encounter(String mac, Date starttime, Date finishtime, double latitude, double longitude, float accuracy) {
		super();
		this.mac = mac;
		this.starttime = starttime;
		this.finishtime = finishtime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.accuracy = accuracy;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getFinishtime() {
		return finishtime;
	}

	public void setFinishtime(Date finishtime) {
		this.finishtime = finishtime;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
}
