package com.righteousbanana.devicemanager;

import java.io.StringReader;
import java.lang.AutoCloseable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class MySQLAccess {
	
	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private ResultSet resultSet2 = null;
	
	private Connection defaultConnection() {
		Connection connection = null;
		Properties properties = ReadPropertyFile.getProperties();
		
		try {
			connection = DriverManager.getConnection("jdbc:mysql://" + properties.getProperty("db_host") + "/" + properties.getProperty("db_name") + "?user=" + properties.getProperty("db_user") + "&password=" + properties.getProperty("db_pass"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return connection;
	}

	public Device dbFetchDeviceOfID(String did) {
		Device aDevice = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = defaultConnection();
			statement = connect.createStatement();

			String sql = "SELECT * FROM device_list WHERE device_id=?";
			PreparedStatement ps = connect.prepareStatement(sql);
			ps.setString(1, did);
			resultSet = ps.executeQuery();
			
			String sql2 = "SELECT * FROM device_list_activity WHERE device_id=? ORDER BY created_on DESC LIMIT 10";
			PreparedStatement ps2 = connect.prepareStatement(sql2);
			ps2.setString(1, did);
			resultSet2 = ps2.executeQuery();
			
			List<Activity> activities = new ArrayList<Activity>();
			// Iterate over the result if it exists
			while (resultSet2.next()) {
				Activity anActivity = new Activity(resultSet2.getInt("id"), resultSet2.getString("user"), resultSet2.getString("activity"), resultSet2.getString("device_id"), resultSet2.getString("note"), resultSet2.getTimestamp("created_on"));
				activities.add(anActivity);
				System.out.println(anActivity);
			}

			// Iterate over the result if it exists
			while (resultSet.next()) {
				aDevice = new Device(resultSet.getString("device_id"), resultSet.getBoolean("available"), resultSet.getString("type"), resultSet.getString("manufacturer"), 
						resultSet.getString("model"), resultSet.getString("model_version"), resultSet.getString("os_type"), resultSet.getString("os_version"), 
						resultSet.getBoolean("dev_provisioned"), resultSet.getBoolean("jailbroken"), resultSet.getString("location"), resultSet.getString("manager_dept"), 
						resultSet.getString("manager_name"), resultSet.getString("checked_out_to"), resultSet.getTimestamp("checked_out_date"), resultSet.getString("udid"), 
						resultSet.getString("note"), resultSet.getString("carrier"), resultSet.getString("phone_number"), resultSet.getBoolean("recovery_mode_enabled"), 
						resultSet.getInt("checked_out_count"), resultSet.getString("image_path"), 
						resultSet.getTimestamp("created_on"), resultSet.getTimestamp("changed_on"), activities);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return aDevice;
	}
	
	public boolean dbGetDeviceAvailability(String did) {
		boolean status = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = defaultConnection();
			statement = connect.createStatement();

			String sql = "SELECT available FROM device_list WHERE device_id=?";
			PreparedStatement ps = connect.prepareStatement(sql);
			ps.setString(1, did);
			resultSet = ps.executeQuery();

			// Iterate over the result if it exists
			while (resultSet.next()) {
				if (resultSet.getBoolean("available")) {
					status = true;
				} else {
					status = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}

		return status;
	}
	
	public void dbCreateActivityItem(String username, String activity_verb, String device_id) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = defaultConnection();
			statement = connect.createStatement();

			String sql = "INSERT INTO device_list_activity (user, activity, device_id, created_on) VALUES (?, ?, ?, NOW())";
			PreparedStatement ps = connect.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, activity_verb);
			ps.setString(3, device_id);
			ps.executeUpdate();
		} catch (Exception e) {
			System.err.println("Got an exception!");
		    System.err.println(e.getMessage());
		} finally {
			close();
		}
	}
	
	public void dbCreateActivityItem(String username, String activity_verb, String device_id, String note) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = defaultConnection();
			statement = connect.createStatement();

			String sql = "INSERT INTO device_list_activity (user, activity, device_id, note, created_on) VALUES (?, ?, ?, ?, NOW())";
			PreparedStatement ps = connect.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, activity_verb);
			ps.setString(3, device_id);
			ps.setString(4, note);
			ps.executeUpdate();
		} catch (Exception e) {
			System.err.println("Got an exception!");
		    System.err.println(e.getMessage());
		} finally {
			close();
		}
	}

	public void dbSetAvailabilityForDevice(String device_id, String username, boolean availability, boolean increment_count) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = defaultConnection();
			statement = connect.createStatement();

			String sql = "UPDATE device_list SET available=?,checked_out_to=?,checked_out_count=(checked_out_count + ?),checked_out_date=NOW() WHERE device_id=?";
			PreparedStatement ps = connect.prepareStatement(sql);
			ps.setBoolean(1, availability);
			ps.setString(2, username);
			ps.setInt(3, increment_count ? 1 : 0);
			ps.setString(4, device_id);
			ps.executeUpdate();
		} catch (Exception e) {
			System.err.println("Got an exception!");
		    System.err.println(e.getMessage());
		} finally {
			close();
		}
	}
	
//	public List<Device> dbFetchDevicesMatchingParameters(Map<String, String> map) throws Exception {
//		List<Device> devices = new ArrayList<Device>();
//		
//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			connect = defaultConnection();
//			statement = connect.createStatement();
//			
//			String sql = "SELECT * FROM device_list WHERE ( IF(LENGTH(?) > 0, type LIKE ?, 1) "
//					+ "AND IF((? = 'true' OR ? = 'false'), available=?, 1) "
//					+ "AND IF(LENGTH(?) > 0, manufacturer LIKE ?, 1) "
//					+ "AND IF(LENGTH(?) > 0, model LIKE ?, 1) "
//					+ "AND IF(LENGTH(?) > 0, model_version LIKE ?, 1) "
//					+ ")";
//			PreparedStatement ps = connect.prepareStatement(sql);
//			ps.setString(1, map.get("type"));
//			ps.setString(2, "%" + map.get("type") + "%");
//			ps.setString(3, map.get("available"));
//			ps.setString(4, map.get("available"));
//			//ps.setString(5, map.get("available").equals("true") ? "1" : "0");
//			ps.setString(5, (map.get("available") != null && map.get("available").equals("true")) ? "1" : "0");
//			ps.setString(6, map.get("manufacturer"));
//			ps.setString(7, "%" + map.get("manufacturer") + "%");
//			ps.setString(8, map.get("model"));
//			ps.setString(9, "%" + map.get("model") + "%");
//			ps.setString(10, map.get("model_version"));
//			ps.setString(11, "%" + map.get("model_version") + "%");
//			resultSet = ps.executeQuery();
//
//			// Iterate over the result if it exists
//			while (resultSet.next()) {
//				Device temp = new Device();
//				temp.buildFromDBEntry(resultSet.getString("device_id"), resultSet.getBoolean("available"), resultSet.getString("type"), resultSet.getString("manufacturer"), 
//						resultSet.getString("model"), resultSet.getString("model_version"), resultSet.getString("os_type"), resultSet.getString("os_version"), 
//						resultSet.getBoolean("dev_provisioned"), resultSet.getBoolean("jailbroken"), resultSet.getString("location"), resultSet.getString("manager_dept"), 
//						resultSet.getString("manager_name"), resultSet.getString("checked_out_to"), resultSet.getTimestamp("checked_out_date"), resultSet.getString("udid"), 
//						resultSet.getString("note"), resultSet.getString("carrier"), resultSet.getString("phone_number"), resultSet.getBoolean("recovery_mode_enabled"), 
//						resultSet.getInt("checked_out_count"), resultSet.getString("image_path"), 
//						resultSet.getTimestamp("created_on"), resultSet.getTimestamp("changed_on"));
//				devices.add(temp);
//			}
//		} catch (Exception e) {
//			throw e;
//		} finally {
//			close();
//		}
//
//		return devices;
//	}

//	public List<Device> dbFetchAllDevicesAsAList() throws Exception {
//		List<Device> devices = new ArrayList<Device>();
//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			connect = defaultConnection();
//			statement = connect.createStatement();
//
//			resultSet = statement.executeQuery("SELECT * FROM device_list");
//
//			// Iterate over the result if it exists
//			while (resultSet.next()) {
//				Device temp = new Device();
//				temp.buildFromDBEntry(resultSet.getString("device_id"), resultSet.getBoolean("available"), resultSet.getString("type"), resultSet.getString("manufacturer"), 
//						resultSet.getString("model"), resultSet.getString("model_version"), resultSet.getString("os_type"), resultSet.getString("os_version"), 
//						resultSet.getBoolean("dev_provisioned"), resultSet.getBoolean("jailbroken"), resultSet.getString("location"), resultSet.getString("manager_dept"), 
//						resultSet.getString("manager_name"), resultSet.getString("checked_out_to"), resultSet.getTimestamp("checked_out_date"), resultSet.getString("udid"), 
//						resultSet.getString("note"), resultSet.getString("carrier"), resultSet.getString("phone_number"), resultSet.getBoolean("recovery_mode_enabled"), 
//						resultSet.getInt("checked_out_count"), resultSet.getString("image_path"), 
//						resultSet.getTimestamp("created_on"), resultSet.getTimestamp("changed_on"));
//				devices.add(temp);
//			}
//		} catch (Exception e) {
//			throw e;
//		} finally {
//			close();
//		}
//
//		return devices;
//	}
	
	public ReturnStatus dbPostDevice(Device device, HttpHeaders headers) {
		String message;
		int code;
		
		// Verify that the user is authenticated
		if (isAuthenticated(headers)) {
		
			// Verify that the user can check out the device
			if (isAuthorizedForAction(device.getDeviceID(), "create", headers)) {
				
				// Verify that the device does not already exist
				if (!isKnownDevice(device.getDeviceID())) {
					
					// Verify that the device is valid
					if (device.isValidDevice()) {
						try {
							Class.forName("com.mysql.jdbc.Driver");
							connect = defaultConnection();
							statement = connect.createStatement();

							String sql = "INSERT INTO device_list (device_id, available, type, manufacturer, model, model_version, os_type, os_version, dev_provisioned, jailbroken, location, manager_dept, manager_name, udid, note, carrier, phone_number, recovery_mode_enabled, image_path, created_on) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
							
							PreparedStatement ps = connect.prepareStatement(sql);
							ps.setString(1, device.getDeviceID());
							ps.setBoolean(2, true);
							ps.setString(3, device.getType());
							ps.setString(4, device.getManufacturer());
							ps.setString(5, device.getModel());
							ps.setString(6, device.getModelVersion());
							ps.setString(7, device.getOsType());
							ps.setString(8, device.getOsVersion());
							ps.setBoolean(9, device.isDevProvisioned());
							ps.setBoolean(10, device.isJailbroken());
							ps.setString(11, device.getLocation());
							ps.setString(12, device.getManagerDept());
							ps.setString(13, device.getManagerName());
							ps.setString(14, device.getUdid());
							ps.setString(15, device.getNote());
							ps.setString(16, device.getCarrier());
							ps.setString(17, device.getPhoneNumber());
							ps.setBoolean(18, device.isRecoveryModeEnabled());
							ps.setString(19, device.getImagePath());
							ps.executeUpdate();
							
							// Create a new activity entry for this device
							dbCreateActivityItem(getUsernameFromAuthorizationHeader(headers), "created", device.getDeviceID());

							message = "Success";
							code = 201;
						} catch (Exception e) {
							System.err.println("Got an exception!");
						    System.err.println(e.getMessage());

						    message = "Internal Server Error";
						    code = 500;
						} finally {
							close();
						}
					} else {
						message = "Device is not valid";
						code = 400;
					}
				} else {
					message = "Device is already known";
					code = 400;
				}
			} else {
				message = "Not authorized for this action";
				code = 403;
			}
		} else {
			message = "Bad credentials";
		    code = 401;
		}
		
		
		return new ReturnStatus(message, code);
	}

	public ReturnStatus dbPutDevice(Device device, HttpHeaders headers) {
		String message;
		int code;
		
		// Verify that the user is authenticated
		if (isAuthenticated(headers)) {
		
			// Verify that the user can check out the device
			if (isAuthorizedForAction(device.getDeviceID(), "edit", headers)) {
				
				// Verify that the device does not already exist
				if (isKnownDevice(device.getDeviceID())) {
					
					// Verify that the device is valid
					if (device.isValidDevice()) {
						try {
							Class.forName("com.mysql.jdbc.Driver");
							connect = defaultConnection();
							statement = connect.createStatement();

							String sql = "UPDATE device_list "
									+ "SET type=?, manufacturer=?, model=?, model_version=?, os_type=?, os_version=?, dev_provisioned=?, jailbroken=?, location=?, manager_dept=?, manager_name=?, udid=?, note=?, carrier=?, phone_number=?, recovery_mode_enabled=?, image_path=?, changed_on=NOW() "
									+ "WHERE device_id=?";
							
							PreparedStatement ps = connect.prepareStatement(sql);
							ps.setString(1, device.getType());
							ps.setString(2, device.getManufacturer());
							ps.setString(3, device.getModel());
							ps.setString(4, device.getModelVersion());
							ps.setString(5, device.getOsType());
							ps.setString(6, device.getOsVersion());
							ps.setBoolean(7, device.isDevProvisioned());
							ps.setBoolean(8, device.isJailbroken());
							ps.setString(9, device.getLocation());
							ps.setString(10, device.getManagerDept());
							ps.setString(11, device.getManagerName());
							ps.setString(12, device.getUdid());
							ps.setString(13, device.getNote());
							ps.setString(14, device.getCarrier());
							ps.setString(15, device.getPhoneNumber());
							ps.setBoolean(16, device.isRecoveryModeEnabled());
							ps.setString(17, device.getImagePath());
							ps.setString(18, device.getDeviceID());
							ps.executeUpdate();
							
							// Create a new activity entry for this device
							dbCreateActivityItem(getUsernameFromAuthorizationHeader(headers), "edited", device.getDeviceID());

							message = "Success";
							code = 201;
						} catch (Exception e) {
							System.err.println("Got an exception!");
						    System.err.println(e.getMessage());

						    message = "Internal Server Error";
						    code = 500;
						} finally {
							close();
						}
					} else {
						message = "Device is not valid";
						code = 400;
					}
				} else {
					message = "Device is not known";
					code = 400;
				}
			} else {
				message = "Not authorized for this action";
				code = 403;
			}
		} else {
			message = "Bad credentials";
		    code = 401;
		}
		
		
		return new ReturnStatus(message, code);
	}

//	public ReturnStatus dbPostDevice(Device device, HttpHeaders headers) {
//		String message;
//		int code;
//		
//		// Verify that the user is authenticated
//		if (isAuthenticated(headers)) {
//		
//			// Verify that the user can check out the device
//			if (isAuthorizedForAction(device.getDevice_id(), "create", headers)) {
//				
//				// Verify that the device does not already exist
//				if (!isKnownDevice(device.getDevice_id())) {
//					
//					// Verify that the device is valid
//					if (device.isValidDevice()) {
//						try {
//							Class.forName("com.mysql.jdbc.Driver");
//							connect = defaultConnection();
//							statement = connect.createStatement();
//
//							String sql = "INSERT INTO device_list (device_id, available, type, manufacturer, model, model_version, os_type, os_version, dev_provisioned, jailbroken, location, manager_dept, manager_name, udid, note, carrier, phone_number, recovery_mode_enabled, image_path, created_on) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";
//							
//							PreparedStatement ps = connect.prepareStatement(sql);
//							ps.setString(1, device.getDevice_id());
//							ps.setBoolean(2, true);
//							ps.setString(3, device.getType());
//							ps.setString(4, device.getManufacturer());
//							ps.setString(5, device.getModel());
//							ps.setString(6, device.getModel_version());
//							ps.setString(7, device.getOs_type());
//							ps.setString(8, device.getOs_version());
//							ps.setBoolean(9, device.isDev_provisioned());
//							ps.setBoolean(10, device.isJailbroken());
//							ps.setString(11, device.getLocation());
//							ps.setString(12, device.getManager_dept());
//							ps.setString(13, device.getManager_name());
//							ps.setString(14, device.getUdid());
//							ps.setString(15, device.getNote());
//							ps.setString(16, device.getCarrier());
//							ps.setString(17, device.getPhone_number());
//							ps.setBoolean(18, device.isRecovery_mode_enabled());
//							ps.setString(19, device.getImage_path());
//							ps.executeUpdate();
//							
//							// Create a new activity entry for this device
//							dbCreateActivityItem(getUsernameFromAuthorizationHeader(headers), "created", device.getDevice_id());
//
//							message = "Success";
//							code = 201;
//						} catch (Exception e) {
//							System.err.println("Got an exception!");
//						    System.err.println(e.getMessage());
//
//						    message = "Internal Server Error";
//						    code = 500;
//						} finally {
//							close();
//						}
//					} else {
//						message = "Device is not valid";
//						code = 400;
//					}
//				} else {
//					message = "Device is already known";
//					code = 400;
//				}
//			} else {
//				message = "Not authorized for this action";
//				code = 403;
//			}
//		} else {
//			message = "Bad credentials";
//		    code = 401;
//		}
//		
//		
//		return new ReturnStatus(message, code);
//	}
//
//	public ReturnStatus dbPutDevice(Device device, HttpHeaders headers) {
//		String message;
//		int code;
//		
//		// Verify that the user is authenticated
//		if (isAuthenticated(headers)) {
//		
//			// Verify that the user can check out the device
//			if (isAuthorizedForAction(device.getDevice_id(), "edit", headers)) {
//				
//				// Verify that the device does not already exist
//				if (isKnownDevice(device.getDevice_id())) {
//					
//					// Verify that the device is valid
//					if (device.isValidDevice()) {
//						try {
//							Class.forName("com.mysql.jdbc.Driver");
//							connect = defaultConnection();
//							statement = connect.createStatement();
//
//							String sql = "UPDATE device_list "
//									+ "SET type=?, manufacturer=?, model=?, model_version=?, os_type=?, os_version=?, dev_provisioned=?, jailbroken=?, location=?, manager_dept=?, manager_name=?, udid=?, note=?, carrier=?, phone_number=?, recovery_mode_enabled=?, image_path=?, changed_on=NOW() "
//									+ "WHERE device_id=?";
//							
//							PreparedStatement ps = connect.prepareStatement(sql);
//							ps.setString(1, device.getType());
//							ps.setString(2, device.getManufacturer());
//							ps.setString(3, device.getModel());
//							ps.setString(4, device.getModel_version());
//							ps.setString(5, device.getOs_type());
//							ps.setString(6, device.getOs_version());
//							ps.setBoolean(7, device.isDev_provisioned());
//							ps.setBoolean(8, device.isJailbroken());
//							ps.setString(9, device.getLocation());
//							ps.setString(10, device.getManager_dept());
//							ps.setString(11, device.getManager_name());
//							ps.setString(12, device.getUdid());
//							ps.setString(13, device.getNote());
//							ps.setString(14, device.getCarrier());
//							ps.setString(15, device.getPhone_number());
//							ps.setBoolean(16, device.isRecovery_mode_enabled());
//							ps.setString(17, device.getImage_path());
//							ps.setString(18, device.getDevice_id());
//							ps.executeUpdate();
//							
//							// Create a new activity entry for this device
//							dbCreateActivityItem(getUsernameFromAuthorizationHeader(headers), "edited", device.getDevice_id());
//
//							message = "Success";
//							code = 201;
//						} catch (Exception e) {
//							System.err.println("Got an exception!");
//						    System.err.println(e.getMessage());
//
//						    message = "Internal Server Error";
//						    code = 500;
//						} finally {
//							close();
//						}
//					} else {
//						message = "Device is not valid";
//						code = 400;
//					}
//				} else {
//					message = "Device is not known";
//					code = 400;
//				}
//			} else {
//				message = "Not authorized for this action";
//				code = 403;
//			}
//		} else {
//			message = "Bad credentials";
//		    code = 401;
//		}
//		
//		
//		return new ReturnStatus(message, code);
//	}
	
	public ReturnStatus dbGetDevices(Map<String, String> map) {
		String message;
		int code;
		
		List<Device> devices = new ArrayList<Device>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = defaultConnection();
			statement = connect.createStatement();
			
			String sql = "SELECT * FROM device_list WHERE ( "
					+ "IF(LENGTH(?) > 0, MATCH(device_id, type, manufacturer, model, os_type, location, carrier, model_version, os_version) AGAINST(? IN BOOLEAN MODE), 1) "
					+ "AND IF((? = 'true'), available=1, 1) "
					+ "AND IF((? = 'false'), available=0, 1) "
					+ "AND IF((? = 'true'), dev_provisioned=1, 1) "
					+ "AND IF((? = 'false'), dev_provisioned=0, 1) "
					+ "AND IF(LENGTH(?) > 0, checked_out_to=?, 1) "
					+ ")";
			PreparedStatement ps = connect.prepareStatement(sql);
			
			String queryString = "";
			if (map.get("query") != null) {
				queryString = "+" + map.get("query").replaceAll(" ", " +");
			}
			
			System.out.println("[dbGetDevices] - Query is '" + queryString + "', isAvailable is '" + map.get("isAvailable") + "', isDevProvisioned is '" + map.get("isDevProvisioned") + "'");
			ps.setString(1, queryString);
			ps.setString(2, queryString);
			ps.setString(3, map.get("isAvailable"));
			ps.setString(4, map.get("isAvailable"));
			ps.setString(5, map.get("isDevProvisioned"));
			ps.setString(6, map.get("isDevProvisioned"));
			ps.setString(7, map.get("checkedOutTo"));
			ps.setString(8, map.get("checkedOutTo"));
			resultSet = ps.executeQuery();

			// Iterate over the result if it exists
			while (resultSet.next()) {
				Device temp = new Device(resultSet.getString("device_id"), resultSet.getBoolean("available"), resultSet.getString("type"), resultSet.getString("manufacturer"), 
						resultSet.getString("model"), resultSet.getString("model_version"), resultSet.getString("os_type"), resultSet.getString("os_version"), 
						resultSet.getBoolean("dev_provisioned"), resultSet.getBoolean("jailbroken"), resultSet.getString("location"), resultSet.getString("manager_dept"), 
						resultSet.getString("manager_name"), resultSet.getString("checked_out_to"), resultSet.getTimestamp("checked_out_date"), resultSet.getString("udid"), 
						resultSet.getString("note"), resultSet.getString("carrier"), resultSet.getString("phone_number"), resultSet.getBoolean("recovery_mode_enabled"), 
						resultSet.getInt("checked_out_count"), resultSet.getString("image_path"), 
						resultSet.getTimestamp("created_on"), resultSet.getTimestamp("changed_on"));
//				temp.buildFromDBEntry(resultSet.getString("device_id"), resultSet.getBoolean("available"), resultSet.getString("type"), resultSet.getString("manufacturer"), 
//						resultSet.getString("model"), resultSet.getString("model_version"), resultSet.getString("os_type"), resultSet.getString("os_version"), 
//						resultSet.getBoolean("dev_provisioned"), resultSet.getBoolean("jailbroken"), resultSet.getString("location"), resultSet.getString("manager_dept"), 
//						resultSet.getString("manager_name"), resultSet.getString("checked_out_to"), resultSet.getTimestamp("checked_out_date"), resultSet.getString("udid"), 
//						resultSet.getString("note"), resultSet.getString("carrier"), resultSet.getString("phone_number"), resultSet.getBoolean("recovery_mode_enabled"), 
//						resultSet.getInt("checked_out_count"), resultSet.getString("image_path"), 
//						resultSet.getTimestamp("created_on"), resultSet.getTimestamp("changed_on"));
				devices.add(temp);
			}
			
			message = "Success";
			code = 200;
		} catch (Exception e) {
			System.err.println("Got an exception!");
		    System.err.println(e.getMessage());

		    message = "Internal Server Error";
		    code = 500;
		} finally {
			close();
		}

		return new ReturnStatus(message, code, devices);
	}
	
	public ReturnStatus dbPostCheckinForID(String device_id, HttpHeaders headers) {
		String message;
		int code;

		// Verify that the user is authenicated
		if (isAuthenticated(headers)) {
		
			// Verify that the user can check in the device
			if (isAuthorizedForAction(device_id, "checkin", headers)) {
				
				// Verify that the device exists
				if (isKnownDevice(device_id)) {
					
					// Verify that the device is available
					if (!isAvailableDevice(device_id)) {
						// Checkout the device
						
						// Set Availability to false
						dbSetAvailabilityForDevice(device_id, "", true, false);
						
						// Create a new activity entry for this device
						dbCreateActivityItem(getUsernameFromAuthorizationHeader(headers), "checked in", device_id);

						message = "Success";
						code = 200;
					} else {
						message = "Device is not checked out";
						code = 404;
					}
				} else {
					message = "Device is not known";
					code = 404;
				}
			} else {
				message = "Not authorized for this action - Device is not checked out to you";
				code = 403;
			}
		} else {
			message = "Bad credentials";
		    code = 401;
		}

		return new ReturnStatus(message, code);
	}
	
	public ReturnStatus dbPostCheckinForID(String device_id, HttpHeaders headers, String xml) {
		String message;
		int code;
		String note = "";

		// Verify that the user is authenicated
		if (isAuthenticated(headers)) {
		
			// Verify that the user can check in the device
			if (isAuthorizedForAction(device_id, "checkin", headers)) {
				
				// Verify that the device exists
				if (isKnownDevice(device_id)) {
					
					// Verify that the device is available
					if (!isAvailableDevice(device_id)) {
						// Checkout the device
						
						// Add note if it exists
						if (xml != null && xml.length() > 0) {
						    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
						    DocumentBuilder builder;
						    try {
						        builder = factory.newDocumentBuilder();
						        Document document = builder.parse( new InputSource( new StringReader( xml ) ) );
						        if (document.getElementsByTagName("message").getLength() > 0) {
						            XPath xPath = XPathFactory.newInstance().newXPath();
						            Node node = (Node) xPath.evaluate("/message", document, XPathConstants.NODE);
						            note = node.getTextContent();
						            System.out.println("note is " + note);
						        }
						    } catch (Exception e) {
						        e.printStackTrace();
						    }
						}
						
						
						// Set Availability to false
						dbSetAvailabilityForDevice(device_id, "", true, false);
						
						// Create a new activity entry for this device
						dbCreateActivityItem(getUsernameFromAuthorizationHeader(headers), "checked in", device_id, note);

						message = "Success";
						code = 200;
					} else {
						message = "Device is not checked out";
						code = 404;
					}
				} else {
					message = "Device is not known";
					code = 404;
				}
			} else {
				message = "Not authorized for this action - Device is not checked out to you";
				code = 403;
			}
		} else {
			message = "Bad credentials";
		    code = 401;
		}

		return new ReturnStatus(message, code);
	}
	
	public ReturnStatus dbPostCheckoutForID(String device_id, HttpHeaders headers) {
		String message;
		int code;
		
		// Verify that the user is authenicated
		if (isAuthenticated(headers)) {
		
			// Verify that the user can check out the device
			if (isAuthorizedForAction(device_id, "checkout", headers)) {
				
				// Verify that the device exists
				if (isKnownDevice(device_id)) {
					
					// Verify that the device is available
					if (isAvailableDevice(device_id)) {
						// Checkout the device
						
						// Set Availability to false
						dbSetAvailabilityForDevice(device_id, getUsernameFromAuthorizationHeader(headers), false, true);
						
						// Create a new activity entry for this device
						dbCreateActivityItem(getUsernameFromAuthorizationHeader(headers), "checked out", device_id);
						
						message = "Success";
						code = 200;
					} else {
						message = "Device is not available";
						code = 404;
					}
				} else {
					message = "Device is not known";
					code = 404;
				}
			} else {
				message = "Not authorized for this action";
				code = 403;
			}
		} else {
			message = "Bad credentials";
		    code = 401;
		}

		return new ReturnStatus(message, code);
	}
	
	public boolean isAuthenticated(HttpHeaders headers) {
		boolean authenticated = false;
		
		List<String> header = headers.getRequestHeader("authorization");
	    if (header != null) {
	        String authorization = header.get(0);
			System.out.println("[isAuthenticated] authorization is " + authorization);
			String[] auth_header = authorization.split(":");
			String header_hash_pw = "";
			String header_user_name = "";
			String db_hash_pw = "";
			
			// Verify that the auth header string array contains two elements (username and hashed password)
			if (auth_header.length == 2) {
				if (auth_header[0] != null && !auth_header[0].isEmpty()) {
					header_user_name = auth_header[0];

					db_hash_pw = getHashedPasswordForUsername(header_user_name);
				}
				if (auth_header[1] != null && !auth_header[1].isEmpty()) {
					header_hash_pw = auth_header[1];
				}
				
				if (!db_hash_pw.isEmpty() && !header_hash_pw.isEmpty() && db_hash_pw.equals(header_hash_pw)) {
					System.out.println("[isAuthenticated] Credentials are valid for user '" + header_user_name + "'");
					authenticated = true;
				} else {
					authenticated = false;
				}
			} else {
				authenticated = false;
			}
	    } else {
	    	authenticated = false;
	    }
	    
		return authenticated;
	}
	
	// Determine if a user is authorized to do an action
	public boolean isAuthorizedForAction(String device_id, String action, HttpHeaders headers) {
		boolean authorized = false;
		
		// Deal with "checkout" case
		if (action.equalsIgnoreCase("checkout")) {
			authorized = true;
		}
		
		// Deal with "checkin" case.
		// User attempting this must match the user that has the device checked out
		else if (action.equalsIgnoreCase("checkin")) {
			
			final String usernameInRequest = this.getUsernameFromAuthorizationHeader(headers);
			final String usernameThatCheckedOutDevice = this.getUsernameThatCheckedOutDevice(device_id);
//			System.out.println("[isAuthorizedForAction] - usernameInRequest is: " + usernameInRequest);
//			System.out.println("[isAuthorizedForAction] - usernameThatCheckedOutDevice is: " + usernameThatCheckedOutDevice);
			
			if (usernameInRequest.equals(usernameThatCheckedOutDevice)) {
				authorized = true;
			} else {
				authorized = false;
			}
		}
		
		// Deal with "create" case
		else if (action.equalsIgnoreCase("create")) {
			authorized = true;
		}
		
		// Deal with "edit" case
		else if (action.equalsIgnoreCase("edit")) {
			authorized = true;
		}
		
		// Deal with "delete" case
		else if (action.equalsIgnoreCase("delete")) {
			authorized = false;
		} 
		
		// Do not authorize unknown actions
		else {
			authorized = false;
		}
		
		return authorized;
	}
	
	public boolean isAvailableDevice(String device_id) {
		boolean availability = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = defaultConnection();
			statement = connect.createStatement();

			String sql = "SELECT available FROM device_list WHERE device_id=?";
			PreparedStatement ps = connect.prepareStatement(sql);
			ps.setString(1, device_id);
			resultSet = ps.executeQuery();

			// Iterate over the result if it exists
			while (resultSet.next()) {
				if (resultSet.getBoolean("available")) {
					availability = true;
				} else {
					availability = false;
				}
			}
		} catch (Exception e) {
			System.err.println("Got an exception!");
		    System.err.println(e.getMessage());
			availability = false;
		} finally {
			close();
		}

		return availability;
	}
	
	public boolean isKnownDevice(String device_id) {
		boolean knownDevice = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = defaultConnection();
			statement = connect.createStatement();

			String sql = "SELECT EXISTS(SELECT 1 FROM device_list WHERE device_id=?)";
			PreparedStatement ps = connect.prepareStatement(sql);
			ps.setString(1, device_id);
			resultSet = ps.executeQuery();

			// Iterate over the result if it exists
			while (resultSet.next()) {
				if (resultSet.getBoolean(1)) {
					knownDevice = true;
				} else {
					knownDevice = false;
				}
			}
		} catch (Exception e) {
			System.err.println("Got an exception!");
		    System.err.println(e.getMessage());
		    knownDevice = false;
		} finally {
			close();
		}

		return knownDevice;
	}
	
	public String getHashedPasswordForUsername(String user_name) {
		String hash_pw = "";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = defaultConnection();
			statement = connect.createStatement();

			String sql = "SELECT password FROM uc_users WHERE user_name=? LIMIT 1";
			PreparedStatement ps = connect.prepareStatement(sql);
			ps.setString(1, user_name);
			resultSet = ps.executeQuery();

			// Iterate over the result if it exists
			while (resultSet.next()) {
				if (resultSet.getString("password") != null && !resultSet.getString("password").isEmpty()) {
					hash_pw = resultSet.getString("password");
				} else {
					hash_pw = "";
				}
			}
		} catch (Exception e) {
			System.err.println("Got an exception!");
		    System.err.println(e.getMessage());
		    hash_pw = "";
		} finally {
			close();
		}
		
		return hash_pw;
	}
	
	private String getUsernameFromAuthorizationHeader(HttpHeaders headers) {
		String username = "";
		
		List<String> header = headers.getRequestHeader("authorization");
	    if (header != null) {
	        String authorization = header.get(0);
			String[] auth_header = authorization.split(":");
			
			// Verify that the auth header string array contains two elements (username and hashed password)
			if (auth_header.length == 2) {
				if (auth_header[0] != null && !auth_header[0].isEmpty()) {
					username = auth_header[0];
				}
			}
	    }
		
		return username;
	}
	
	private String getUsernameThatCheckedOutDevice(String device_id) {
		String username = "";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = defaultConnection();
			statement = connect.createStatement();

			String sql = "SELECT checked_out_to FROM device_list WHERE device_id=?;";
			PreparedStatement ps = connect.prepareStatement(sql);
			ps.setString(1, device_id);
			resultSet = ps.executeQuery();

			// Iterate over the result if it exists
			while (resultSet.next()) {
				if (resultSet.getString("checked_out_to") != null && !resultSet.getString("checked_out_to").isEmpty()) {
					username = resultSet.getString("checked_out_to");
				} else {
					username = "";
				}
			}
		} catch (Exception e) {
			System.err.println("Got an exception!");
		    System.err.println(e.getMessage());
		    username = "";
		} finally {
			close();
		}
		
		return username;
	}

	// Close the open connections
	private void close() {
		System.out.println("Closing the connections...");
		close(resultSet);
		close(statement);
		close(connect);
	}

	private void close(AutoCloseable c) {
		try {
			if (c != null) {
				c.close();
			}
		} catch (Exception e) {
			System.out.println("What is going on here?");
		}
	}
}
