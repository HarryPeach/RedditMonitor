package uk.co.harrypeach.configuration;
import java.io.IOException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

import uk.co.harrypeach.core.Main;

public class ConfigHelper {
	
	private static ConfigHelper configHelperInstance = null;
	private static Configuration configInstance = null;

	private static void initializeConfig() {
		Yaml yaml = new Yaml();
		try(InputStream in = Main.class.getClassLoader().getResource("config.yml").openStream()){
			configInstance = yaml.loadAs(in, Configuration.class);
		} catch (IOException e) {
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
