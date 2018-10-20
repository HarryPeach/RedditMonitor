/**
 * Model for each result in the search, bound to the listview.
 * @author harry
 *
 */
public class Result {

	private String subreddit;
	private String title;
	private String url;
	private String shortUrl;
	
	public Result(String subreddit, String title, String url, String shortUrl) {
		this.subreddit = subreddit;
		this.title = title;
		this.url = url;
		this.shortUrl = shortUrl;
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
	 * Gets the URL of the post
	 * @return URL string
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Gets the short URL of the post
	 * @return short URL string
	 */
	public String getShortUrl() {
		return shortUrl;
	}
	
}
