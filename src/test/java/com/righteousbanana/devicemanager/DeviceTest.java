package com.righteousbanana.devicemanager;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DeviceTest {
	
	private Device deviceTest;

	@Before
	public void setUp() throws Exception {
		deviceTest = new Device();
	}

	@Test
	public void testSetModel() {
		deviceTest.setModel("iPhone");
		assertEquals("iPhone must be returned", "iPhone", deviceTest.getModel());
	}
//
//	
//	@Test
//	public void testModelVersion() {
//		assertEquals("5S must be returned", "5S", deviceTest.getModelVersion());
//	}
//	
//	@Test
//	public void testApplePhone() {
//		ApplePhone testApplePhone = new ApplePhone("QA001", "5S");
//		assertEquals("5S must be returned", "5S", testApplePhone.getModelVersion());
//		testApplePhone = null;
//	}
//	
//	@Test
//	public void testApplePhoneManufacturer() {
//		ApplePhone testApplePhone = new ApplePhone("QA001", "5S");
//		assertEquals("Apple must be returned", "Apple", testApplePhone.getManufacturer());
//		testApplePhone = null;
//	}
//	
	@After
	public void tearDown() throws Exception {
		deviceTest = null;
	}
}
