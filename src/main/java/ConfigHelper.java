import java.io.IOException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

public class ConfigHelper {
	
	private static ConfigHelper configHelperInstance = null;

	public static ConfigHelper getInstance() {
		if(configHelperInstance == null)
			configHelperInstance = new ConfigHelper();
		
		// TODO: Move this code
		Yaml yaml = new Yaml();
		try(InputStream in = Main.class.getClassLoader().getResource("config.yml").openStream()){
			Configuration config = yaml.loadAs(in, Configuration.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return configHelperInstance;
	}
	
	
	
}
