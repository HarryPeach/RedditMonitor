package uk.co.harrypeach.core;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uk.co.harrypeach.configuration.ConfigHelper;

public class Main extends Application {

	public static ConfigHelper config;

	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	public static final String VERSION = "0.1";

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Core.fxml"));
			Scene scene = new Scene(root, 500, 300);
			primaryStage.setTitle("RedditMonitor");
			primaryStage.setScene(scene);

			// Stop users from resizing the UI
			primaryStage.setResizable(false);
			
			config = ConfigHelper.getInstance();
			
			LOGGER.log(Level.FINEST, "Showing primary stage");
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// Set Logger defaults
		// TODO: Change this so that it is set through VM args instead. See:
		// https://stackoverflow.com/questions/6307648/change-global-setting-for-logger-instances/6307666#6307666
		Logger rootLogger = LogManager.getLogManager().getLogger("");
		rootLogger.setLevel(Level.FINE);
		for (Handler h : rootLogger.getHandlers()) {
			h.setLevel(Level.FINE);
		}

		launch(args);
	}
}