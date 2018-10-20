package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class FXMLCoreController {
    @FXML private ListView<String> postList;
    
    @FXML protected void handleSampleButton(ActionEvent event) {
    	postList.setVisible(false);
    }

}