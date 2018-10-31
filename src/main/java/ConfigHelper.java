import org.yaml.snakeyaml.Yaml;

public class ConfigHelper {
	
	private static ConfigHelper configHelperInstance = null;

	public static ConfigHelper getInstance() {
		if(configHelperInstance == null)
			configHelperInstance = new ConfigHelper();
		
		return configHelperInstance;
	}
	
}
