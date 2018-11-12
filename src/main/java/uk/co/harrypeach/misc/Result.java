package uk.co.harrypeach.misc;
/**
 * Model for each result in the search, bound to the listview.
 * @author harry
 *
 */
public class Result {

	private String subreddit;
	private String title;
	private String url;
	private String postUrl;
	private String id;
	
	public Result(String subreddit, String title, String url, String postUrl, String id) {
		this.subreddit = subreddit;
		this.title = title;
		this.url = url;
		this.postUrl = postUrl;
		this.id = id;
	}
	
	/**
	 * Default string return method which returns the title
	 */
	public String toString() {
		return title;
	}
	
	/**
	 * Gets the subreddit name
	 * @return subreddit string
	 */
	public String getSubreddit() {
		return subreddit;
	}

	/**
	 * Gets the title of the post
	 * @return title string
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the URL of the post's content
	 * @return URL string
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Gets the URL of the post itself
	 * @return URL string
	 */
	public String getPostUrl() {
		return postUrl;
	}
	
	/**
	 * Gets the ID of the post
	 * @return ID string
	 */
	public String getId() {
		return id;
	}
	
}
