package com.alexanderbatten.bluetoothfriends;

public class UserScore {
	private String userName;
	private int deviceCount;
	
	public UserScore(String userName, int deviceCount) {
		super();
		this.userName = userName;
		this.deviceCount = deviceCount;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getDeviceCount() {
		return deviceCount;
	}
	public void setDeviceCount(int deviceCount) {
		this.deviceCount = deviceCount;
	}
}