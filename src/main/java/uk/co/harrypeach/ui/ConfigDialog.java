package uk.co.harrypeach.ui;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;

public class ConfigDialog extends Dialog<String>{
	
	
	public ConfigDialog(String title, String header) {
		setTitle(title);
		setHeaderText(header);
		
		ButtonType okButton = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
	}

}
