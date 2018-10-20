package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	public static RedditHelper redditHelper;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Core.fxml"));
			Scene scene = new Scene(root, 500, 500);
			primaryStage.setTitle("RedditMonitor");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		redditHelper = new RedditHelper();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
