package uk.co.harrypeach.configuration;

import java.util.List;

public class Configuration {
	
	private String oauthClient;
	private boolean nsfwFilteringEnabled;
	private boolean alertSoundEnabled;
	private double alertSoundVolume;
	private int updateDelay;
	private boolean notificationsEnabled;
	private List<String> keywordList;
	private List<String> blacklistedSubreddits;
	
	/**
	 * Whether NSFW filtering is enabled or not
	 * @return
	 */
	public boolean isNsfwFilteringEnabled() {
		return nsfwFilteringEnabled;
	}
	public void setNsfwFilteringEnabled(boolean nsfwFilteringEnabled) {
		this.nsfwFilteringEnabled = nsfwFilteringEnabled;
	}
	
	/**
	 * Whether the notification alert sound should be played
	 * @return
	 */
	public boolean isAlertSoundEnabled() {
		return alertSoundEnabled;
	}
	public void setAlertSoundEnabled(boolean alertSoundEnabled) {
		this.alertSoundEnabled = alertSoundEnabled;
	}
	
	/**
	 * What the current alert sound volume is
	 * @return
	 */
	public double getAlertSoundVolume() {
		return alertSoundVolume;
	}
	public void setAlertSoundVolume(double alertSoundVolume) {
		this.alertSoundVolume = alertSoundVolume;
	}
	
	/**
	 * The OAuth string to identify the client
	 * @return
	 */
	public String getOauthClient() {
		return oauthClient;
	}
	public void setOauthClient(String oauthClient) {
		this.oauthClient = oauthClient;
	}
	
	/**
	 * The keywords that posts are compared to in order to determine a match
	 * @return
	 */
	public List<String> getKeywordList() {
		return keywordList;
	}
	public void setKeywordList(List<String> keywordList) {
		this.keywordList = keywordList;
	}
	
	/**
	 * Whether pop-up notifications are enabled
	 * @return
	 */
	public boolean isNotificationsEnabled() {
		return notificationsEnabled;
	}
	public void setNotificationsEnabled(boolean notificationsEnabled) {
		this.notificationsEnabled = notificationsEnabled;
	}
	
	/**
	 * Return the list of blacklisted subreddits
	 * @return
	 */
	public List<String> getBlacklistedSubreddits() {
		return blacklistedSubreddits;
	}
	public void setBlacklistedSubreddits(List<String> blacklistedSubreddits) {
		this.blacklistedSubreddits = blacklistedSubreddits;
	}
	
	/**
	 * Return the delay between each check for new posts
	 * @return
	 */
	public int getUpdateDelay() {
		return updateDelay;
	}
	public void setUpdateDelay(int updateDelay) {
		this.updateDelay = updateDelay;
	}
	
}
