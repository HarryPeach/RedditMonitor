package application;

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
	
	public String getSubreddit() {
		return subreddit;
	}

	public void setSubreddit(String subreddit) {
		this.subreddit = subreddit;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getShortUrl() {
		return shortUrl;
	}

	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}
	
}
