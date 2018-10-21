import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.media.AudioClip;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.SubredditReference;
import java.util.logging.Logger;

/**
 * The controller for the primary stage
 * 
 * @author harry
 *
 */
public class FXMLCoreController {
	@FXML
	private ListView<Result> postList;
	@FXML
	private Button startButton;
	@FXML
	private Label titleLabel;
	@FXML
	private Label subredditLabel;
	@FXML
	private Hyperlink urlLabel;

	private static final int MAX_LABEL_CHARS = 60;
	private static final Logger LOGGER = Logger.getLogger(FXMLCoreController.class.getName());

	public boolean threadEnabled = false;
	public RedditHelper redditHelper;
	Thread t;
	AudioClip alert;

	/**
	 * Handles what should happen when the start button is pressed on the primary
	 * stage
	 * 
	 * @param event
	 */
	@FXML
	protected void handleStartButton(ActionEvent event) {
		LOGGER.finest("Start/stop button clicked by user");
		if (threadEnabled) {
			disableThread();
		} else {
			enableThread();
		}
	}

	/**
	 * Displays an 'About Dialog' when the about button is clicked
	 * 
	 * @param event
	 */
	@FXML
	protected void handleAboutButton(ActionEvent event) {
		LOGGER.finest("Showing alert dialog");
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setHeaderText("About Reddit Monitor");
		alert.setContentText("Copyright (C) Harry Peach\n"
				+ "Licensed under the GNU GPL v3\n"
				+ "https://github.com/HarryPeach");

		alert.showAndWait();
	}
	
	/**
	 * Exits the program entirely
	 * @param event
	 */
	@FXML
	protected void handleExitButton(ActionEvent event) {
		LOGGER.fine("Exiting program because user pressed exit button");
		disableThread();
		System.exit(0);
	}

	/**
	 * Called when the stage is initialised
	 */
	@FXML
	protected void initialize() {
		// Instantiate the Updater thread and make it a daemon so that it exits with the
		// application
		t = new Thread(new UpdateList(this));
		t.setDaemon(true);

		// Handle selection of ListView items
		postList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Result>() {
			@Override
			public void changed(ObservableValue<? extends Result> observable, Result oldValue, Result newValue) {
				String titleText = newValue.getTitle();
				titleLabel.setText(titleText.substring(0, Math.min(titleText.length(), MAX_LABEL_CHARS)));

				String subredditText = newValue.getSubreddit();
				subredditLabel.setText(subredditText.substring(0, Math.min(subredditText.length(), MAX_LABEL_CHARS)));

				String urlText = newValue.getUrl();
				urlLabel.setText(urlText.substring(0, Math.min(urlText.length(), MAX_LABEL_CHARS)));
				urlLabel.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						try {
							LOGGER.fine("Attempting to open selected URL in the users browser");
							Desktop.getDesktop().browse(new URI(urlText));
						} catch (IOException | URISyntaxException e1) {
							LOGGER.warning(e1.getMessage());
							e1.printStackTrace();
						}
					}
				});
			}
		});

	}

	/**
	 * Enables the thread and updates UI
	 */
	public void enableThread() {
		if (t.getState().equals(Thread.State.NEW) || t.getState().equals(Thread.State.TERMINATED)) {
			LOGGER.fine("Starting thread as it was either NEW or TERMINATED");
			t.start();
		}
		startButton.setText("Stop");
		threadEnabled = true;
	}

	/**
	 * Disables the thread and updates UI
	 */
	public void disableThread() {
		startButton.setText("Start");
		threadEnabled = false;
	}

	/**
	 * Plays an alert sound to notify the user that a match has been found
	 */
	public void playAlert() {
		if (!Main.preferences.getPreferences().getBoolean("PLAY_ALERT", true)) {
			LOGGER.finest("Attempted to play alert, but it was disabled by the PLAY_ALERT preference");
			return;
		}

		// Create a new audio clip from resources and play it to alert the user
		alert = new AudioClip(this.getClass().getResource("alert.wav").toExternalForm());
		alert.setVolume(Main.preferences.getPreferences().getDouble("ALERT_VOLUME", 0.2));
	}

	/**
	 * Whenever the application is exited, stop the UpdateList thread
	 * 
	 * @param event
	 */
	@FXML
	public void exitApplication(ActionEvent event) {
		shutdown();
	}

	/**
	 * Returns the ListView that holds all of the posts
	 * 
	 * @return ListView of posts
	 */
	public ListView<Result> getPostList() {
		return postList;
	}

	/**
	 * Can be called to stop the UpdateList thread
	 */
	public void shutdown() {
		threadEnabled = false;
	}

}

class UpdateList implements Runnable {
	private FXMLCoreController controllerInstance;
	private static final int MAX_RESULT_QUEUE_SIZE = 100;
	private static final int SUBMISSION_LIMIT = 50;
	private static final Logger LOGGER = Logger.getLogger(UpdateList.class.getName());
	Queue<Result> resultQueue = new LinkedList<>();

	// The list of strings that are searched for within a posts title
	List<String> stringList = Arrays.asList("steam key", "giving away", "giveaway", "cd key", "steam code", "cd code",
			"spare code", "spare key", "extra key", "extra code", "give away");

	public UpdateList(FXMLCoreController instance) {
		controllerInstance = instance;
	}

	public void run() {
		if (controllerInstance.redditHelper == null) {
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
						Result r = new Result(s.getSubreddit(), s.getTitle(), s.getUrl(), s.getId());
						// Checks whether the submission title contains a keyword, and whether it is
						// already in the result queue
						if (titleContainsWordList(r.getTitle(), stringList) && !containsResult(resultQueue, r)) {
							addToQueue(r);
							Runnable updater = new Runnable() {
								public void run() {
									controllerInstance.playAlert();
									controllerInstance.getPostList().getItems().add(r);
								}
							};
							Platform.runLater(updater);
						}
					}
					Thread.sleep(1000);
				} else {
					Thread.sleep(1000);
				}
			}
		} catch (Exception e) {
			Runnable updater = new Runnable() {
				public void run() {
					e.printStackTrace();
					LOGGER.warning(e.getMessage());
					controllerInstance.disableThread();
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