package uk.co.harrypeach.ui;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ConfigDialog extends Dialog<String>{
	
	
	public ConfigDialog() {
		setTitle("Configuration");
		setHeaderText("Configuration Manager");
		
		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("icon.png")));
		
		ButtonType okButton = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
	}

}
