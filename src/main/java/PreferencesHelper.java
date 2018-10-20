import java.util.prefs.Preferences;

public class PreferencesHelper {

	private Preferences preferences;

	public PreferencesHelper() {
		preferences = Preferences.userRoot().node(this.getClass().getName());
//		preferences.putBoolean("FIRST_RUN", true);
		
		if (preferences.getBoolean("FIRST_RUN", true)) {
			System.out.println("Running preference reset");
			resetPreferences();
			// Make sure the reset code does not run again
			preferences.putBoolean("FIRST_RUN", false);
		}

	}

	/**
	 * Resets the program preferences
	 */
	public void resetPreferences() {
		// Alert sound volume level
		preferences.putDouble("ALERT_VOLUME", 0.2);

		// Whether or not to play the alert sound
		preferences.putBoolean("PLAY_ALERT", true);
	}
	
	/**
	 * Returns the preferences object
	 * @return The preferences object
	 */
	public Preferences getPreferences() {
		return preferences;
	}

}
