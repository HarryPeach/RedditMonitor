package uk.co.harrypeach.configuration;

import java.util.List;

public class Configuration {
	
	private String oauthClient;
	private boolean nsfwFilteringEnabled;
	private boolean alertSoundEnabled;
	private double alertSoundVolume;
	private boolean notificationsEnabled;
	private List<String> keywordList;
	private List<String> blacklistedSubreddits;
	
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
	public List<String> getKeywordList() {
		return keywordList;
	}
	public void setKeywordList(List<String> keywordList) {
		this.keywordList = keywordList;
	}
	public boolean isNotificationsEnabled() {
		return notificationsEnabled;
	}
	public void setNotificationsEnabled(boolean notificationsEnabled) {
		this.notificationsEnabled = notificationsEnabled;
	}
	public List<String> getBlacklistedSubreddits() {
		return blacklistedSubreddits;
	}
	public void setBlacklistedSubreddits(List<String> blacklistedSubreddits) {
		this.blacklistedSubreddits = blacklistedSubreddits;
	}
	
}
