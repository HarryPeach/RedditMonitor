
public class Configuration {
	
	private static Configuration configInstance = null;
	
	private String accountUsername;
	private boolean nsfwFilteringEnabled;
	private boolean alertSoundEnabled;
	private double alertSoundVolume;
	
	public boolean isNsfwFilteringEnabled() {
		return nsfwFilteringEnabled;
	}
	public void setNsfwFilteringEnabled(boolean nsfwFilteringEnabled) {
		this.nsfwFilteringEnabled = nsfwFilteringEnabled;
	}
	
	public boolean isAlertSoundEnabled() {
		return alertSoundEnabled;
	}
	public void setAlertSoundEnabled(boolean alertSoundEnabled) {
		this.alertSoundEnabled = alertSoundEnabled;
	}
	
	public double getAlertSoundVolume() {
		return alertSoundVolume;
	}
	public void setAlertSoundVolume(double alertSoundVolume) {
		this.alertSoundVolume = alertSoundVolume;
	}

	public String getAccountUsername() {
		return accountUsername;
	}
	public void setAccountUsername(String accountUsername) {
		this.accountUsername = accountUsername;
	}
	
}
