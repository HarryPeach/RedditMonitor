package uk.co.harrypeach.ui;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.stage.FileChooser;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.SubredditReference;
import uk.co.harrypeach.core.Main;
import uk.co.harrypeach.misc.RedditHelper;
import uk.co.harrypeach.misc.Result;
import uk.co.harrypeach.ui.NotificationHelper.NotificationType;

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
	private Hyperlink titleHyperlink;
	@FXML
	private Hyperlink subredditHyperlink;
	@FXML
	private Hyperlink urlHyperlink;
	@FXML
	private AnchorPane anchorPane;

	private static final int MAX_LABEL_CHARS = 60;
	private static final Logger LOGGER = LoggerFactory.getLogger(FXMLCoreController.class);
	NotificationHelper notifHelp = new NotificationHelper();

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
		LOGGER.debug("Start/stop button clicked by user");
		if (threadEnabled) {
			if (Main.config.getConfigInstance().isNotificationsEnabled())
				notifHelp.createNotification("Reddit Monitor", "User stopped update thread");
			disableThread();
		} else {
			if (Main.config.getConfigInstance().isNotificationsEnabled())
				notifHelp.createNotification("Reddit Monitor", "User started update thread");
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
		LOGGER.debug("Showing alert dialog");
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About");
		alert.setHeaderText("About Reddit Monitor");
		alert.setContentText("Version " + Main.VERSION + " - build " + System.getProperty("githash") + "\n"
				+ "Copyright (C) Harry Peach\n" + "Licensed under the GNU GPL v3\n" + "https://github.com/HarryPeach");
		alert.showAndWait();
	}

	/**
	 * Exits the program entirely
	 * 
	 * @param event
	 */
	@FXML
	protected void handleExitButton(ActionEvent event) {
		LOGGER.info("Exiting program because user pressed exit button");
		disableThread();
		System.exit(0);
	}

	/**
	 * Opens the config manager dialog
	 * 
	 * @param event
	 */
	@FXML
	protected void handleConfigButton(ActionEvent event) {
		LOGGER.debug("Showing config dialog");
		ConfigDialog configDialog = new ConfigDialog();
		configDialog.show();
	}

	/**
	 * Opens the Logs dialog
	 * 
	 * @param event
	 */
	@FXML
	protected void handleLogsButton(ActionEvent event) {
		LOGGER.debug("Showing logs dialog");
		LogReaderDialog logReaderDialog = new LogReaderDialog();
		logReaderDialog.show();
	}

	/**
	 * Exports the items within the list to a CSV file
	 * @param event
	 */
	@FXML
	protected void handleCsvExport(ActionEvent event) {
		LOGGER.debug("Opening file dialog");

		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter fileExtensions = new FileChooser.ExtensionFilter("CSV Files", "*.csv");

		fileChooser.getExtensionFilters().add(fileExtensions);
		fileChooser.setTitle("Export to CSV");
		File selectedFile = fileChooser.showSaveDialog(postList.getScene().getWindow());
		if(selectedFile != null) {
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(selectedFile);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
				bw.write("Title,Subreddit,Url");
				bw.newLine();
				for (Result p : postList.getItems()) {
					bw.write(p.getTitle() + "," + p.getSubreddit() + "," + p.getUrl());
					bw.newLine();
				}
				bw.close();
			} catch (FileNotFoundException e) {
				LOGGER.error("File not found when trying to export to CSV");
				e.printStackTrace();
			} catch (IOException e) {
				LOGGER.error("IO Exception when trying to export to CSV");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Debug function to add a dummy post to the list
	 * 
	 * @param event
	 */
	@FXML
	protected void handleDebugAddItem(ActionEvent event) {
		LOGGER.debug("Adding dummy item to the post list");
		postList.getItems().add(new Result("/r/test", "This is a test post", "https://reddit.com/", "https://reddit.com/r/test/comments/t35t", "t35t"));
		playAlert();
	}

	/**
	 * Debug function to remove all items from the list
	 * 
	 * @param event
	 */
	@FXML
	protected void handleDebugClearItems(ActionEvent event) {
		LOGGER.debug("Clearing all items in the post list");
		postList.getItems().clear();
	}

	/**
	 * Debug function to create a dummy notification
	 * 
	 * @param event
	 */
	@FXML
	protected void handleDebugCreateNotification(ActionEvent event) {
		LOGGER.debug("Creating dummy notification");
		notifHelp.createNotification("Reddit Monitor - DEBUG",
				"This is a dummy notification that deliberately has a very long notification content body.");
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
				
				// Set the title hyperlink text and URL
				String titleText = newValue.getTitle();
				String titleUrl = "https://reddit.com" + newValue.getPostUrl();
				titleHyperlink.setText(titleText.substring(0, Math.min(titleText.length(), MAX_LABEL_CHARS)));
				titleHyperlink.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						try {
							LOGGER.debug("Attempting to open selected URL in the users browser");
							Desktop.getDesktop().browse(new URI(titleUrl));
						} catch (IOException | URISyntaxException e1) {
							LOGGER.warn(e1.getMessage());
							e1.printStackTrace();
						}
					}
				});

				// Set the subreddit hyperlink text and URL
				String subredditText = newValue.getSubreddit();
				String subredditUrl = "https://reddit.com/r/" + newValue.getSubreddit();
				subredditHyperlink.setText(subredditText.substring(0, Math.min(subredditText.length(), MAX_LABEL_CHARS)));
				subredditHyperlink.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						try {
							LOGGER.debug("Attempting to open selected URL in the users browser");
							Desktop.getDesktop().browse(new URI(subredditUrl));
						} catch (IOException | URISyntaxException e1) {
							LOGGER.warn(e1.getMessage());
							e1.printStackTrace();
						}
					}
				});

				// Set the URL hyperlink text and URL
				String urlText = newValue.getUrl();
				urlHyperlink.setText(urlText.substring(0, Math.min(urlText.length(), MAX_LABEL_CHARS)));
				urlHyperlink.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						try {
							LOGGER.debug("Attempting to open selected URL in the users browser");
							Desktop.getDesktop().browse(new URI(urlText));
						} catch (IOException | URISyntaxException e1) {
							LOGGER.warn(e1.getMessage());
							e1.printStackTrace();
						}
					}
				});
			}
		});

		// Open the post's url in the browser when it is double clicked.
		postList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {

				if (click.getClickCount() == 2) {
					try {
						LOGGER.debug("Attempting to open selected URL in the users browser");
						Desktop.getDesktop().browse(new URI(postList.getSelectionModel().getSelectedItem().getUrl()));
					} catch (IOException | URISyntaxException e1) {
						LOGGER.warn(e1.getMessage());
						e1.printStackTrace();
					}
				}
			}
		});

	}

	/**
	 * Enables the thread and updates UI
	 */
	public void enableThread() {
		LOGGER.trace("Enabling update thread");
		if (t.getState().equals(Thread.State.NEW) || t.getState().equals(Thread.State.TERMINATED)) {
			LOGGER.debug("Starting thread as it was either NEW or TERMINATED");
			t.start();
		}
		startButton.setText("Stop");
		threadEnabled = true;
	}

	/**
	 * Disables the thread and updates UI
	 */
	public void disableThread() {
		LOGGER.trace("Disabling update thread");
		startButton.setText("Start");
		threadEnabled = false;
	}

	/**
	 * Plays an alert sound to notify the user that a match has been found
	 */
	public void playAlert() {
		if (!Main.config.getConfigInstance().isAlertSoundEnabled()) {
			LOGGER.debug("Attempted to play alert, but it was disabled by the PLAY_ALERT preference");
			return;
		}

		// Create a new audio clip from resources and play it to alert the user
		LOGGER.debug("Playing alert clip");
		alert = new AudioClip(getClass().getClassLoader().getResource("alert.wav").toExternalForm());
		alert.setVolume(Main.config.getConfigInstance().getAlertSoundVolume());
		alert.play();
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

/**
 * The updater thread which continiously checks for new posts that match the keywords and that are not blacklisted or already included
 * @author Harry Peach
 */
class UpdateList implements Runnable {
	private FXMLCoreController controllerInstance;
	private static final int MAX_RESULT_QUEUE_SIZE = 100;
	private static final int SUBMISSION_LIMIT = 50;
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateList.class);
	private static final NotificationHelper notifHelp = new NotificationHelper();
	Queue<Result> resultQueue = new LinkedList<>();

	// The list of strings that are searched for within a posts title
	List<String> stringList = Main.config.getConfigInstance().getKeywordList();

	public UpdateList(FXMLCoreController instance) {
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

							// NSFW Filtering
							if (Main.config.getConfigInstance().isNsfwFilteringEnabled() && s.isNsfw()) {
								LOGGER.info("A post matched the criteria, but was blocked as it was marked NSFW");
								return;
							}

							// Subreddit blacklist filtering
							if (Main.config.getConfigInstance().getBlacklistedSubreddits()
									.contains(s.getSubreddit().toLowerCase())) {
								LOGGER.info(
										"A post matched the criteria, but was blocked as its subreddit is blacklisted");
								return;
							}

							// Add item to the postlist and notify the user
							addToQueue(r);
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
					e.printStackTrace();
					LOGGER.warn(e.getMessage());
					controllerInstance.disableThread();
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