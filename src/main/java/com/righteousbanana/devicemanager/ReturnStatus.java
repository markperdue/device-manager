package com.righteousbanana.devicemanager;

import java.util.List;

public class ReturnStatus {
	
	private final String message;
	private final int code;
	private List<Device> deviceList;
	
	public ReturnStatus(String message, int code) {
		this.message = message;
		this.code = code;
	}
	
	public ReturnStatus(String message, int code, List<Device> deviceList) {
		this.message = message;
		this.code = code;
		this.deviceList = deviceList;
	}
	
	public String getMessage() { return message; }
	public int getCode() { return code; }
	public List<Device> getDeviceList() { return deviceList; }
}
