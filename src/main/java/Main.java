import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {
	
	public static RedditHelper redditHelper;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Core.fxml"));
			Scene scene = new Scene(root, 500, 300);
			primaryStage.setTitle("RedditMonitor");
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
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
