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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.stage.FileChooser;
import uk.co.harrypeach.core.Main;
import uk.co.harrypeach.core.UpdaterThread;
import uk.co.harrypeach.misc.RedditHelper;
import uk.co.harrypeach.misc.Result;

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
	 * 
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
		if (selectedFile != null) {
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
				LOGGER.error("File not found when trying to export to CSV: " + e);
			} catch (IOException e) {
				LOGGER.error("IO Exception when trying to export to CSV: " + e);
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
		postList.getItems().add(new Result("test", "This is a test post", "https://reddit.com/",
				"/r/test/comments/t35t", "t35t"));
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
		t = new Thread(new UpdaterThread(this));
		t.setDaemon(true);

		// Handle selection of ListView items
		postList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Result>() {
			@Override
			public void changed(ObservableValue<? extends Result> observable, Result oldValue, Result newValue) {

				// Set the title hyperlink text and URL
				String titleText = newValue.getTitle();
				String titleUrl =  newValue.getFullPostUrl();
				titleHyperlink.setText(titleText.substring(0, Math.min(titleText.length(), MAX_LABEL_CHARS)));
				titleHyperlink.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						openUrlInBrowser(titleUrl);
					}
				});

				// Set the subreddit hyperlink text and URL
				String subredditText = newValue.getSubreddit();
				String subredditUrl = newValue.getFullSubreddit();
				subredditHyperlink
						.setText(subredditText.substring(0, Math.min(subredditText.length(), MAX_LABEL_CHARS)));
				subredditHyperlink.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						openUrlInBrowser(subredditUrl);
					}
				});

				// Set the URL hyperlink text and URL
				String urlText = newValue.getUrl();
				urlHyperlink.setText(urlText.substring(0, Math.min(urlText.length(), MAX_LABEL_CHARS)));
				urlHyperlink.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						openUrlInBrowser(urlText);
					}
				});
			}
		});

		// Open the post's url in the browser when it is double clicked.
		postList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent click) {

				if (click.getClickCount() == 2) {
					openUrlInBrowser(postList.getSelectionModel().getSelectedItem().getUrl());
				}
			}
		});

		// Open a context menu upon right-clicking items in the list
		postList.setCellFactory(lv -> {
			ListCell< Result > cell = new ListCell<Result>(){
				@Override
	            protected void updateItem(Result item, boolean empty) {
	                super.updateItem(item, empty);
	                if (item != null) {
	                    setText(item.getTitle());
	                } else {
	                    setText("");
	                }
	            }
			};
			ContextMenu contextMenu = new ContextMenu();

			MenuItem openPermalink = new MenuItem();
			openPermalink.textProperty().bind(Bindings.format("Open Permalink"));
			openPermalink.setOnAction(event -> {
				openUrlInBrowser(cell.getItem().getFullPostUrl());
			});

			MenuItem openSubreddit = new MenuItem();
			openSubreddit.textProperty().bind(Bindings.format("Open Subreddit"));
			openSubreddit.setOnAction(event -> {
				openUrlInBrowser(cell.getItem().getFullSubreddit());
			});

			MenuItem openURL = new MenuItem();
			openURL.textProperty().bind(Bindings.format("Open URL"));
			openURL.setOnAction(event -> {
				openUrlInBrowser(cell.getItem().getUrl());
			});

			MenuItem deleteItem = new MenuItem();
			deleteItem.textProperty().bind(Bindings.format("Delete item"));
			deleteItem.setOnAction(event -> postList.getItems().remove(cell.getItem()));
			contextMenu.getItems().addAll(openPermalink, openSubreddit, openURL, deleteItem);

			cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
				if (isNowEmpty) {
					cell.setContextMenu(null);
				} else {
					cell.setContextMenu(contextMenu);
				}
			});

			return cell;
		});

	}
	
	/**
	 * Opens a specific URL in the users browser
	 */
	private void openUrlInBrowser(String url) {
		try {
			LOGGER.debug("Attempting to open selected URL in the users browser");
			Desktop.getDesktop().browse(new URI(url));
		} catch (IOException | URISyntaxException e1) {
			LOGGER.warn("Exception opening URL in browser: " + e1);
		}
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
	 * Restarts / recreates the update thread
	 */
	public void restartThread() {
		LOGGER.debug("Restarting the update thread");
		disableThread();
		t = new Thread(new UpdaterThread(this));
		t.setDaemon(true);
		enableThread();
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