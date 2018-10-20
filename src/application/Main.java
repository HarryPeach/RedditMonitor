package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;

public class Main extends Application {
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
		UserAgent userAgent = new UserAgent("bot", "application.redditmonitor", "0.1", "JellyGiant");
		// MyCredentials is a class that contains static constants for sensitive data.
		// It is hidden from the git repository.
		Credentials credentials = Credentials.script(MyCredentials.username, MyCredentials.password,
				MyCredentials.clientId, MyCredentials.secret);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
