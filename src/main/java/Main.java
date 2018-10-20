import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Core.fxml"));
			Scene scene = new Scene(root, 500, 300);
			primaryStage.setTitle("RedditMonitor");
			primaryStage.setScene(scene);
			
			// Stop users from resizing the UI
			primaryStage.setResizable(false);
			
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
