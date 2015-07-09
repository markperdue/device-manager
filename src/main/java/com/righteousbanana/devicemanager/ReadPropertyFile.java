package com.righteousbanana.devicemanager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ReadPropertyFile {
	
	private final static String CONTEXT_LOOKUP = "java:/comp/env/configurationPath";
	
	public static Properties getPropertiesFromConfigurationPath(String filePath) {
		Properties properties = new Properties();
		InputStream input = null;
		 
		try {
			input = new FileInputStream(filePath);
	 
			// Load a properties file
			properties.load(input);
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return properties;
	}
	
	public static String getConfigurationPathFromContext(String context) {
		String configurationPath = "";
		
		try {
			configurationPath = (String) new InitialContext().lookup(context);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return configurationPath;
	}
	
	public static Properties getProperties() {
		String configurationPath = getConfigurationPathFromContext(CONTEXT_LOOKUP);
		Properties properties = getPropertiesFromConfigurationPath(configurationPath);
		
		return properties;
	}
}
