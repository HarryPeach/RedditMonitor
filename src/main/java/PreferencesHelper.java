import org.yaml.snakeyaml.Yaml;

public class PreferencesHelper {
	
	private static PreferencesHelper preferencesHelperInstance = null;

	public static PreferencesHelper getInstance() {
		if(preferencesHelperInstance == null)
			preferencesHelperInstance = new PreferencesHelper();
		
		return preferencesHelperInstance;
	}
	
}
