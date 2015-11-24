### device-manager


A java webapp that provides the backend to `device-genie`



## Minimum Requirements

1. Java application server (ex. Tomcat)
2. MySQL 5.6



## Getting Started

1. Build the project with ant
2. Copy [project home]/build/dist/resources/device-manager.properties and [project home]/build/dist/resources/device-manager.xml to your Tomcat configuration directory under [tomcat home]/conf/[engine-name]/[server-name]/
	(If Tomcat is installed to /Library/Tomcat/ this is likely /Library/Tomcat/conf/Catalina/localhost/)
3. Edit the file [tomcat home]/conf/[engine-name]/[server-name]/device-manager.xml and change the configurationPath value attribute to the path for the device-manager.properties file
4. Edit the file [tomcat home]/conf/[engine-name]/[server-name]/device-manager.properties and change values to reflect your database settings
5. Deploy [project home]/build/dist/device-manager.war to your java application server



## License

DeviceManager is released under the [MIT License](http://www.opensource.org/licenses/MIT).