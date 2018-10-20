package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class FXMLCoreController {
    @FXML private ListView<Result> postList;
    
    @FXML protected void handleSampleButton(ActionEvent event) {
    	postList.getItems().add(new Result("Subreddit", "Title", "URL", "shortURL"));
    }

}