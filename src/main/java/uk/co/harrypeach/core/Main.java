package uk.co.harrypeach.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import uk.co.harrypeach.configuration.ConfigHelper;

public class Main extends Application {

	public static ConfigHelper config;

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	public static final String VERSION = "0.5";
	private static Stage primaryStageInstance;

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("core.fxml"));
			primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResource("icon.png").toExternalForm()));
			Scene scene = new Scene(root, 500, 300);
			primaryStage.setTitle("Reddit Monitor " + VERSION);
			primaryStage.setScene(scene);

			primaryStageInstance = primaryStage;
			
			LOGGER.debug("Showing primary stage");
			primaryStage.show();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		config = ConfigHelper.getInstance();

		launch(args);
	}
	
	/**
	 * Returns the current primary stage
	 * @return The current primary stage
	 */
	public static Stage getPrimaryStage() {
		return primaryStageInstance;
	}
	
	/**
	 * Brings the primary stage to the front of the users screen and focuses it
	 */
	public static void popupPrimaryStage() {
		getPrimaryStage().setAlwaysOnTop(true);
		getPrimaryStage().requestFocus();
		getPrimaryStage().setAlwaysOnTop(false);
	}
}
