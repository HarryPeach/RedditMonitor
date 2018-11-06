package uk.co.harrypeach.configuration;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import uk.co.harrypeach.core.Main;
import uk.co.harrypeach.ui.FXMLCoreController;

public class ConfigHelper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigHelper.class);
	private static ConfigHelper configHelperInstance = null;
	private static Configuration configInstance = null;

	private static void initializeConfig() {
		Yaml yaml = new Yaml();
		LOGGER.debug("Opening config resource");
		try(InputStream in = Main.class.getClassLoader().getResource("config.yml").openStream()){
			LOGGER.debug("Loading config resource into configuration class");
			configInstance = yaml.loadAs(in, Configuration.class);
		} catch (IOException e) {
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
	
	public Configuration getConfigInstance() {
		if(configInstance == null)
			initializeConfig();
		
		return configInstance;
	}
	
}
