import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;

public class RedditHelper {

	private UserAgent userAgent;
	private Credentials credentials;
	private NetworkAdapter adapter;
	private RedditClient reddit;
	
	public RedditHelper() {
		userAgent = new UserAgent("bot", "application.redditmonitor", "0.1", "JellyGiant");
		// MyCredentials is a class that contains static constants for sensitive data.
		// It is hidden from the Git repository.
		credentials = Credentials.script(MyCredentials.username, MyCredentials.password,
				MyCredentials.clientId, MyCredentials.secret);
		adapter = new OkHttpNetworkAdapter(userAgent);
		reddit = OAuthHelper.automatic(adapter, credentials);
	}
	
	/**
	 * Gets the instance of the RedditClient
	 * @return instance of RedditClient
	 */
	public RedditClient getRedditClient() {
		return reddit;
	}
	
	/**
	 * Gets the instance of the UserAgent
	 * @return instance of UserAgent
	 */
	public UserAgent getUserAgent() {
		return userAgent;
	}

	/**
	 * Gets the instance of the Credentials
	 * @return instance of Credentials
	 */
	public Credentials getCredentials() {
		return credentials;
	}

	/**
	 * Gets the instance of the NetworkAdapter
	 * @return instance of NetworkAdapter
	 */
	public NetworkAdapter getAdapter() {
		return adapter;
	}
	
}
