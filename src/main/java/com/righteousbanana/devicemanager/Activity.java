package com.righteousbanana.devicemanager;

import java.util.Date;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Activity {
	
	@XmlAttribute
	private int id;
	
	@XmlAttribute
	private String user;
	
	@XmlAttribute
	private String type;
	
	@XmlAttribute(name = "device_id")
	private String deviceID;
	
	@XmlAttribute
	private String note;
	
	@XmlAttribute(name = "created_on")
	private Date createdOn;
	
	public Activity(int id, String user, String type, String deviceID, String note, Date createdOn) {
		this.id = id;
		this.user = user;
		this.type = type;
		this.deviceID = deviceID;
		this.note = note;
		this.createdOn = createdOn;
	}
	
	public String toString() {
		return "Activity(ID: " + id + ", User: " + user + ", Type: " + type + ", Device ID: " + deviceID + ", Note: " + note + ", Created: " + createdOn + ")";
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getDeviceID() {
		return deviceID;
	}
	
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		this.note = note;
	}
	
	public Date getCreatedOn() {
		return createdOn;
	}
	
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
}
