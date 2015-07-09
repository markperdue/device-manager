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
	public String empty() {
		System.out.println("Here we go!!");
		Properties properties = ReadPropertyFile.getProperties();
		
		System.out.println(properties.getProperty("db_host"));
		System.out.println(properties.getProperty("db_port"));
		System.out.println(properties.getProperty("db_name"));
	    return "Done";
	}
	
	@Path("/v1/device/{device_id}/checkin2")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public Response postCheckIn2(@PathParam("device_id") String device_id, @Context HttpHeaders headers, String xml) {
        MySQLAccess access = new MySQLAccess();
		ReturnStatus status = access.dbPostCheckinForID(device_id, headers, xml);
        
        return Response.status(status.getCode()).entity(status.getMessage()).build();
	}

	@Path("/v1/devices")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getDevicesMatch(@DefaultValue("") @QueryParam("query") String query, @DefaultValue("") @QueryParam("available") String available, @DefaultValue("") @QueryParam("dev") String devProvisioned, @DefaultValue("") @QueryParam("checked_out_to") String checkedOutTo) {
		Map<String, String> map = returnQueryAsMap(query, available, devProvisioned, checkedOutTo);
        MySQLAccess access = new MySQLAccess();
        ReturnStatus status = access.dbGetDevices(map);
        
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

	@Path("/v1/device/{device_id}")
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Device getDevice(@PathParam("device_id") String device_id) {
		Device device = null;
        MySQLAccess access = new MySQLAccess();
        try {
			device = access.dbFetchDeviceOfID(device_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return device;
	}

	// Check in a device
	// Requires an Authorization header that includes "<username>:<hashed pw>"
	@Path("/v1/device/{device_id}/checkin")
	@POST
	@Consumes(MediaType.APPLICATION_XML)
//	public Response postCheckIn(@PathParam("device_id") String device_id, @Context HttpHeaders headers) {
//        MySQLAccess access = new MySQLAccess();
//		ReturnStatus status = access.dbPostCheckinForID(device_id, headers);
//        
//        return Response.status(status.getCode()).entity(status.getMessage()).build();
//	}
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

	@Path("/v1/device/{device_id}/available")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public boolean getDeviceAvailability(@PathParam("device_id") String device_id) {
        MySQLAccess access = new MySQLAccess();
        boolean status = false;
        try {
			status = access.dbGetDeviceAvailability(device_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return status;
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
}
