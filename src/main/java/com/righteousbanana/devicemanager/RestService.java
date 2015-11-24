package com.righteousbanana.devicemanager;

import java.util.*;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class RestService {
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response empty() {
	    return Response.status(200).entity("OK").build();
	}
	
	@Path("/v1/devices")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getDevicesMatch(@DefaultValue("") @QueryParam("query") String query, @DefaultValue("") @QueryParam("available") String available, @DefaultValue("") @QueryParam("dev") String devProvisioned, @DefaultValue("") @QueryParam("checked_out_to") String checkedOutTo) {
		Map<String, String> map = returnQueryAsMap(query, available, devProvisioned, checkedOutTo);
		ReturnStatus status = getDevices(map);
        
        // Build a GenericEntity per http://stackoverflow.com/questions/6081546/jersey-can-produce-listt-but-cannot-response-oklistt-build
        GenericEntity<List<Device>> entity = new GenericEntity<List<Device>>(status.getDeviceList()) {};
        
        return Response.status(status.getCode()).entity(entity).build();
	}

	// Create a new device
	// Requires an Authorization header that includes "<username>:<hashed pw>"
	@Path("/v1/devices")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response postDevice(@Context HttpHeaders headers, Device device) {
		MySQLAccess access = new MySQLAccess();
		ReturnStatus status = access.dbPostDevice(device, headers);

        return Response.status(status.getCode()).entity(status.getMessage()).build();
	}

	@Path("/v1/device/{device_id}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getDevice(@PathParam("device_id") String device_id) {
		ReturnStatus status = getDeviceOfID(device_id);
        
        return Response.status(status.getCode()).entity(status.getDevice()).build();
	}

	// Update an existing device
	// Requires an Authorization header that includes "<username>:<hashed pw>"
	@Path("/v1/device/{device_id}")
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	public Response putDevice(@Context HttpHeaders headers, Device device) {
		MySQLAccess access = new MySQLAccess();
		ReturnStatus status = access.dbPutDevice(device, headers);

        return Response.status(status.getCode()).entity(status.getMessage()).build();
	}
	
	@Path("/v1/device/{device_id}/available")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getDeviceAvailability(@PathParam("device_id") String device_id) {
        ReturnStatus status = getDeviceOfIDAvailability(device_id);
        
        return Response.status(status.getCode()).entity(status.getDevice()).build();
	}

	// Check in a device
	// Requires an Authorization header that includes "<username>:<hashed pw>"
	@Path("/v1/device/{device_id}/checkin")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response postCheckIn(@PathParam("device_id") String device_id, @Context HttpHeaders headers, String xml) {
        MySQLAccess access = new MySQLAccess();
		ReturnStatus status = access.dbPostCheckinForID(device_id, headers, xml);
        
        return Response.status(status.getCode()).entity(status.getMessage()).build();
	}

	// Check out a device
	// Requires an Authorization header that includes "<username>:<hashed pw>"
	@Path("/v1/device/{device_id}/checkout")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response postCheckout(@PathParam("device_id") String device_id, @Context HttpHeaders headers) {
        MySQLAccess access = new MySQLAccess();
		ReturnStatus status = access.dbPostCheckoutForID(device_id, headers);
        
        return Response.status(status.getCode()).entity(status.getMessage()).build();
	}

	private Map<String, String> returnQueryAsMap(String query, String available, String devProvisioned, String checkedOutTo) {
		Map<String, String> map = new HashMap<String, String>();
		
		// Create a map
		if (query != null && !query.isEmpty()) {
			map.put("query", query);
		}
		if (available.equals("true")) {
			map.put("isAvailable", "true");
		}
		else if (available.equals("false")) {
			map.put("isAvailable", "false");
		}
		if (devProvisioned.equals("true")) {
			map.put("isDevProvisioned", "true");
		}
		else if (devProvisioned.equals("false")) {
			map.put("isDevProvisioned", "false");
		}
		if (checkedOutTo != null && !checkedOutTo.isEmpty()) {
			map.put("checkedOutTo", checkedOutTo);
		}
		
		return map;
	}
	
	public ReturnStatus getDeviceOfID(String did) {
		String message;
		int code;
		Device device;
		
		MySQLAccess access = new MySQLAccess();
		Device fetchedDevice = access.dbFetchDeviceOfID(did);
		
		if (fetchedDevice.isValidDevice()) {
			device = fetchedDevice;
			message = "OK";
			code = 200;
		}
		else {
			// Add empty device
			device = new Device();
			message = "No device";
			code = 404;
		}

		return new ReturnStatus(message, code, device);
	}
	
	public ReturnStatus getDeviceOfIDAvailability(String did) {
		String message;
		int code;
		Device device;
		
		MySQLAccess access = new MySQLAccess();
		Device fetchedDevice = access.dbGetDeviceAvailability(did);
		
		if (fetchedDevice.isAvailable() != null) {
			device = fetchedDevice;
			message = "OK";
			code = 200;
		}
		else {
			// Add empty device
			device = new Device();
			message = "No device";
			code = 404;
		}

		return new ReturnStatus(message, code, device);
	}
	
	public ReturnStatus getDevices(Map<String, String> map) {
		String message;
		int code;
		List<Device> devices;
		
		MySQLAccess access = new MySQLAccess();
		devices = access.dbGetDevices(map);
		
		if (devices != null) {
			message = "OK";
			code = 200;
		}
		else {
			devices = new ArrayList<Device>();
			message = "No devices";
			code = 404;
		}

		return new ReturnStatus(message, code, devices);
	}

//	// Check in a device (OLD)
//	// Requires an Authorization header that includes "<username>:<hashed pw>"
//	@Path("/v1/device/{device_id}/checkin")
//	@POST
//	@Consumes(MediaType.APPLICATION_XML)
//	public Response postCheckIn(@PathParam("device_id") String device_id, @Context HttpHeaders headers) {
//		MySQLAccess access = new MySQLAccess();
//		ReturnStatus status = access.dbPostCheckinForID(device_id, headers);
//  
//		return Response.status(status.getCode()).entity(status.getMessage()).build();
//	}
}
