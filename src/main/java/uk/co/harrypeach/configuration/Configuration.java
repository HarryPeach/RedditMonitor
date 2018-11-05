package uk.co.harrypeach.configuration;

public class Configuration {
	
	private String oauthClient;
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
	public String getOauthClient() {
		return oauthClient;
	}
	public void setOauthClient(String oauthClient) {
		this.oauthClient = oauthClient;
	}
	
}
