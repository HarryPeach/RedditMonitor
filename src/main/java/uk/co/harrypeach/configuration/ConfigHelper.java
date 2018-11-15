package uk.co.harrypeach.configuration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class ConfigHelper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigHelper.class);
	private static final File PRODUCTION_CONFIG_LOCATION = new File("../config/config.yml");
	private static final File DEBUG_CONFIG_LOCATION = new File("/src/dist/config/config.yml");
	private static ConfigHelper configHelperInstance = null;
	private static Configuration configInstance = null;
	private static String configToUse = "";

	private static void initializeConfig() {
		Yaml yaml = new Yaml();
		LOGGER.debug("Opening config resource");
		
		// Check for the production config and fallback to debug otherwise
		LOGGER.debug("Checking for production configuration file");
		if(PRODUCTION_CONFIG_LOCATION.exists() && !PRODUCTION_CONFIG_LOCATION.isDirectory()) {
			configToUse = PRODUCTION_CONFIG_LOCATION.getPath();
		}else {
			LOGGER.debug("Production configuration not found, falling back to debug configuration location");
			configToUse = DEBUG_CONFIG_LOCATION.getPath();
		}
		
		try(InputStream in = new FileInputStream(configToUse)){
			LOGGER.debug("Loading config resource into configuration class");
			configInstance = yaml.loadAs(in, Configuration.class);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static ConfigHelper getInstance() {
		if(configHelperInstance == null)
			configHelperInstance = new ConfigHelper();
		if(configInstance == null)
			initializeConfig();
			
		
		return configHelperInstance;
	}
	
	public static String getConfigLocation() throws Exception {
		if(configToUse != "") {
			return configToUse;
		}else {
			throw(new FileNotFoundException("The config file was not set when access was attempted."));
		}
	}
	
	public Configuration getConfigInstance() {
		if(configInstance == null)
			initializeConfig();
		
		return configInstance;
	}
	
}
