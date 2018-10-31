
public class Configuration {
	
	private boolean nsfwFilteringEnabled;
	private boolean alertSoundEnabled;
	private int alertSoundVolume;
	
	public boolean isnsfwFilteringEnabled() {
		return nsfwFilteringEnabled;
	}
	public void setFilterNSFW(boolean nsfwFilteringEnabled) {
		this.nsfwFilteringEnabled = nsfwFilteringEnabled;
	}
	
	public boolean isalertSoundEnabled() {
		return alertSoundEnabled;
	}
	public void setalertSoundEnabled(boolean alertSoundEnabled) {
		this.alertSoundEnabled = alertSoundEnabled;
	}
	
	public int getAlertSoundVolume() {
		return alertSoundVolume;
	}
	public void setAlertSoundVolume(int alertSoundVolume) {
		this.alertSoundVolume = alertSoundVolume;
	}
	
}
