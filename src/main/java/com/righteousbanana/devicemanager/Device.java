package com.righteousbanana.devicemanager;

import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "device")
public class Device {
	
	private String deviceID;
	private boolean available;
	private String type;
	private String manufacturer;
	private String model;
	private String modelVersion;
	private String osType;
	private String osVersion;
	private boolean devProvisioned;
	private boolean jailbroken;
	private String location;
	private String managerDept;
	private String managerName;
	private String checkedOutTo;
	private Date checkedOutDate;
	private String udid;
	private String note;
	private String carrier;
	private String phoneNumber;
	private boolean recoveryModeEnabled;
	private int checkedOutCount;
	private String imagePath;
	private Date createdOn;
	private Date changedOn;
	private List<Activity> recentActivity;
	
	public Device() {
		
	}
	
	public Device(String deviceID, boolean available, String type, String manufacturer, String model, String modelVersion, String osType, String osVersion, 
			boolean devProvisioned, boolean jailbroken, String location, String managerDept, String managerName, String checkedOutTo, Date checkedOutDate, String udid, String note, String carrier, 
			String phoneNumber, boolean recoveryModeEnabled, int checkedOutCount, String imagePath, Date createdOn, Date changedOn) {
		this.deviceID = deviceID;
		this.available = available;
		this.type = type;
		this.manufacturer = manufacturer;
		this.model = model;
		this.modelVersion = modelVersion;
		this.osType = osType;
		this.osVersion = osVersion;
		this.devProvisioned = devProvisioned;
		this.jailbroken = jailbroken;
		this.location = location;
		this.managerDept = managerDept;
		this.managerName = managerName;
		this.checkedOutTo = checkedOutTo;
		this.checkedOutDate = checkedOutDate;
		this.udid = udid;
		this.note = note;
		this.carrier = carrier;
		this.phoneNumber = phoneNumber;
		this.recoveryModeEnabled = recoveryModeEnabled;
		this.checkedOutCount = checkedOutCount;
		this.imagePath = imagePath;
		this.createdOn = createdOn;
		this.changedOn = changedOn;
		
		this.display();
	}
	
	public Device(String deviceID, boolean available, String type, String manufacturer, String model, String modelVersion, String osType, String osVersion, 
			boolean devProvisioned, boolean jailbroken, String location, String managerDept, String managerName, String checkedOutTo, Date checkedOutDate, String udid, String note, String carrier, 
			String phoneNumber, boolean recoveryModeEnabled, int checkedOutCount, String imagePath, Date createdOn, Date changedOn, List<Activity> recentActivity) {
		this.deviceID = deviceID;
		this.available = available;
		this.type = type;
		this.manufacturer = manufacturer;
		this.model = model;
		this.modelVersion = modelVersion;
		this.osType = osType;
		this.osVersion = osVersion;
		this.devProvisioned = devProvisioned;
		this.jailbroken = jailbroken;
		this.location = location;
		this.managerDept = managerDept;
		this.managerName = managerName;
		this.checkedOutTo = checkedOutTo;
		this.checkedOutDate = checkedOutDate;
		this.udid = udid;
		this.note = note;
		this.carrier = carrier;
		this.phoneNumber = phoneNumber;
		this.recoveryModeEnabled = recoveryModeEnabled;
		this.checkedOutCount = checkedOutCount;
		this.imagePath = imagePath;
		this.createdOn = createdOn;
		this.changedOn = changedOn;
		this.recentActivity = recentActivity;
		
		this.display();
	}
	
//	public void buildFromDBEntry(String deviceID, boolean available, String type, String manufacturer, String model, String modelVersion, String osType, String osVersion, 
//			boolean devProvisioned, boolean jailbroken, String location, String managerDept, String managerName, String checkedOutTo, Date checkedOutDate, String udid, String note, String carrier, 
//			String phoneNumber, boolean recoveryModeEnabled, int checkedOutCount, String imagePath, Date createdOn, Date changedOn, List<String[]> activity) {
//		this.deviceID = deviceID;
//		this.available = available;
//		this.type = type;
//		this.manufacturer = manufacturer;
//		this.model = model;
//		this.modelVersion = modelVersion;
//		this.osType = osType;
//		this.osVersion = osVersion;
//		this.devProvisioned = devProvisioned;
//		this.jailbroken = jailbroken;
//		this.location = location;
//		this.managerDept = managerDept;
//		this.managerName = managerName;
//		this.checkedOutTo = checkedOutTo;
//		this.checkedOutDate = checkedOutDate;
//		this.udid = udid;
//		this.note = note;
//		this.carrier = carrier;
//		this.phoneNumber = phoneNumber;
//		this.recoveryModeEnabled = recoveryModeEnabled;
//		this.checkedOutCount = checkedOutCount;
//		this.imagePath = imagePath;
//		this.createdOn = createdOn;
//		this.changedOn = changedOn;
//		this.activity = activity;
//		
//		this.display();
//	}
	
	public void display() {
		System.out.println(this);
	}

	public void displayPretty() {
		System.out.println("\nDevice Details");
		System.out.println("Device ID: " + this.deviceID);
		System.out.println("Available: " + (this.available ? "Yes" : "No"));
		System.out.println("Type: " + this.type);
		System.out.println("Device: " + this.manufacturer + " " + this.model + " " + this.modelVersion);
	}
	
	public String toString() {
		return "Device(Device ID: " + deviceID + ", Available: " + available + ", Device: " + manufacturer + " " + model + " " + modelVersion + " (" + type + ")" + ")";
	}

	@XmlElement(name = "checked_out_to")
	public String getCheckedOutTo() {
		return checkedOutTo;
	}

	public void setCheckedOutTo(String checkedOutTo) {
		this.checkedOutTo = checkedOutTo;
	}
	
	@XmlElement(name = "device_id")
	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@XmlElement(name = "model_version")
	public String getModelVersion() {
		return modelVersion;
	}

	public void setModelVersion(String modelVersion) {
		this.modelVersion = modelVersion;
	}

	@XmlElement(name = "os_type")
	public String getOsType() {
		return osType;
	}


	public void setOsType(String osType) {
		this.osType = osType;
	}

	@XmlElement(name = "os_version")
	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	@XmlElement(name = "dev_provisioned")
	public boolean isDevProvisioned() {
		return devProvisioned;
	}

	public void setDevProvisioned(boolean devProvisioned) {
		this.devProvisioned = devProvisioned;
	}

	public boolean isJailbroken() {
		return jailbroken;
	}

	public void setJailbroken(boolean jailbroken) {
		this.jailbroken = jailbroken;
	}

	@XmlElement(name = "manager_dept")
	public String getManagerDept() {
		return managerDept;
	}

	public void setManagerDept(String managerDept) {
		this.managerDept = managerDept;
	}

	@XmlElement(name = "manager_name")
	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	@XmlElement(name = "checked_out_date")
	public Date getCheckedOutDate() {
		return checkedOutDate;
	}

	public void setCheckedOutDate(Date checkedOutDate) {
		this.checkedOutDate = checkedOutDate;
	}

	@XmlElementWrapper(name="recent_activity")
    @XmlElement(name="activity")
	public List<Activity> getRecentActivity() {
		return recentActivity;
	}

	public void setRecentActivity(List<Activity> recentActivity) {
		this.recentActivity = recentActivity;
	}

	@XmlElement(name = "recovery_mode_enabled")
	public boolean isRecoveryModeEnabled() {
		return recoveryModeEnabled;
	}

	public void setRecoveryModeEnabled(boolean recoveryModeEnabled) {
		this.recoveryModeEnabled = recoveryModeEnabled;
	}

	@XmlElement(name = "checked_out_count")
	public int getCheckedOutCount() {
		return checkedOutCount;
	}

	public void setCheckedOutCount(int checkedOutCount) {
		this.checkedOutCount = checkedOutCount;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	@XmlElement(name = "phone_number")
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@XmlElement(name = "image_path")
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	@XmlElement(name = "created_on")
	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@XmlElement(name = "changed_on")
	public Date getChangedOn() {
		return changedOn;
	}

	public void setChangedOn(Date changedOn) {
		this.changedOn = changedOn;
	}

	// Verify that a device has at least the following set:
	// device_id, type, manufacturer, model, model_version, os_type, os_version, location
	public boolean isValidDevice() {
		if (this.getDeviceID() != null && !this.getDeviceID().isEmpty()) {
			if (this.getType() != null && !this.getType().isEmpty()) {
				if (this.getManufacturer() != null && !this.getManufacturer().isEmpty()) {
					if (this.getModel() != null && !this.getModel().isEmpty()) {
						if (this.getModelVersion() != null && !this.getModelVersion().isEmpty()) {
							if (this.getOsType() != null && !this.getOsType().isEmpty()) {
								if (this.getOsVersion() != null && !this.getOsVersion().isEmpty()) {
									if (this.getLocation() != null && !this.getLocation().isEmpty()) {
										return true;
									} else {
										return false;
									}
								} else {
									return false;
								}
							} else {
								return false;
							}
						} else {
							return false;
						}
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
