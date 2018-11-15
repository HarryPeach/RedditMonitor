package uk.co.harrypeach.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.SubredditReference;
import uk.co.harrypeach.misc.RedditHelper;
import uk.co.harrypeach.misc.Result;
import uk.co.harrypeach.ui.FXMLCoreController;
import uk.co.harrypeach.ui.NotificationHelper;
import uk.co.harrypeach.ui.NotificationHelper.NotificationType;

/**
 * The updater thread which continiously checks for new posts that match the
 * keywords and that are not blacklisted or already included
 * 
 * @author Harry Peach
 */
public class UpdaterThread implements Runnable {
	private FXMLCoreController controllerInstance;
	private static final int MAX_RESULT_QUEUE_SIZE = 100;
	private static final int SUBMISSION_LIMIT = 50;
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdaterThread.class);
	private static final NotificationHelper notifHelp = new NotificationHelper();
	Queue<Result> resultQueue = new LinkedList<>();

	// The list of strings that are searched for within a posts title
	List<String> stringList = Main.config.getConfigInstance().getKeywordList();

	public UpdaterThread(FXMLCoreController instance) {
		controllerInstance = instance;
	}

	public void run() {
		if (controllerInstance.redditHelper == null) {
			LOGGER.trace("Controller instance was null, so it was instantiated");
			controllerInstance.redditHelper = new RedditHelper();
		}
		SubredditReference all = controllerInstance.redditHelper.getRedditClient().subreddit("all");

		try {
			while (true) {
				if (controllerInstance.threadEnabled) {
					DefaultPaginator<Submission> paginator = all.posts().sorting(SubredditSort.NEW)
							.limit(SUBMISSION_LIMIT).build();
					Listing<Submission> submissions = paginator.next();
					
					for (Submission s : submissions) {
						Result r = new Result(s.getSubreddit(), s.getTitle(), s.getUrl(), s.getPermalink(), s.getId());
						// Checks whether the submission title contains a keyword, and whether it is
						// already in the result queue
						if (titleContainsWordList(r.getTitle(), stringList) && !containsResult(resultQueue, r)) {
							LOGGER.info(String.format("Post matched - Title: %s, Subreddit: %s, URL: %s", r.getTitle(),
									r.getSubreddit(), r.getUrl()));

							addToQueue(r);

							// NSFW Filtering
							if (Main.config.getConfigInstance().isNsfwFilteringEnabled() && s.isNsfw()) {
								LOGGER.info("A post matched the criteria, but was blocked as it was marked NSFW");
								continue;
							}

							// Subreddit blacklist filtering
							if (Main.config.getConfigInstance().getBlacklistedSubreddits()
									.contains(s.getSubreddit().toLowerCase())) {
								LOGGER.info(
										"A post matched the criteria, but was blocked as its subreddit is blacklisted");
								continue;
							}

							// Keyword blacklist filtering
							for (String blacklistedKeyword : Main.config.getConfigInstance().getBlacklistedKeywords()) {
								if (r.getTitle().contains(blacklistedKeyword)) {
									LOGGER.info(
											"A post matched the criteria, but was blocked as it contained a blacklisted keyword");
									continue;
								}
							}

							// Add item to the postlist and notify the user
							Runnable updater = new Runnable() {
								public void run() {
									controllerInstance.playAlert();
									LOGGER.trace("Adding item to postList");
									controllerInstance.getPostList().getItems().add(r);
									if (Main.config.getConfigInstance().isNotificationsEnabled()) {
										notifHelp.createNotification("Reddit Monitor - Match found", r.getTitle());
									}
								}
							};
							Platform.runLater(updater);
						}
					}
					Thread.sleep(Main.config.getConfigInstance().getUpdateDelay());
				} else {
					Thread.sleep(Main.config.getConfigInstance().getUpdateDelay());
				}
			}
		} catch (Exception e) {
			Runnable updater = new Runnable() {
				public void run() {
					LOGGER.error("Exception when updating postList: " + e);
					controllerInstance.restartThread();
					if (Main.config.getConfigInstance().isNotificationsEnabled())
						notifHelp.createNotification("Reddit Monitor",
								"An error was encountered while running the program",
								NotificationHelper.DEFAULT_NOTIFICATION_DURATION, NotificationType.ERROR);
				}
			};
			Platform.runLater(updater);
		}
	}

	/**
	 * Checks whether a string contains another string from a list of words
	 * 
	 * @param word     The string to search
	 * @param wordList The list of strings to compare
	 * @return Whether an String item contains a string within a list
	 */
	private boolean titleContainsWordList(String word, List<String> wordList) {
		for (String s : wordList) {
			if (word.toLowerCase().contains(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether a result is in the result queue
	 * 
	 * @param resultQueueIn The result queue to search
	 * @param testResult    The result to search for
	 * @return Whether a result is in the result queue
	 */
	private boolean containsResult(Queue<Result> resultQueueIn, Result testResult) {
		for (Result result : resultQueueIn) {
			if (result.getId().equals(testResult.getId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds items to the result queue and pops the bottom item if the queue gets
	 * full
	 * 
	 * @param r Item to be added to the queue
	 */
	private void addToQueue(Result r) {
		if (resultQueue.size() >= MAX_RESULT_QUEUE_SIZE) {
			resultQueue.remove();
			resultQueue.add(r);
		} else {
			resultQueue.add(r);
		}
		Runnable updater = new Runnable() {
			public void run() {
				controllerInstance.getPostList().scrollTo(controllerInstance.getPostList().getItems().size());
			}
		};
		Platform.runLater(updater);
	}
}