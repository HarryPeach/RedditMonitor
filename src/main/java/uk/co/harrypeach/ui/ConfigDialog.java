package uk.co.harrypeach.ui;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class ConfigDialog extends Dialog<String>{
	
	
	public ConfigDialog() {
		setTitle("Configuration");
		setHeaderText("Configuration Manager");
		
		ButtonType okButton = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
	}

}
