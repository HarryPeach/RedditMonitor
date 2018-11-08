package uk.co.harrypeach.ui;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LogReaderDialog extends Dialog<String>{
	
	private ListView<String> logList = new ListView<>();
	
	public LogReaderDialog() {
		setTitle("Logs");
		setHeaderText("Log viewer");
		
		HBox hbox = new HBox();
		HBox.setHgrow(logList, Priority.ALWAYS);
		hbox.getChildren().add(logList);
		
		VBox vbox = new VBox();
		vbox.getChildren().add(hbox);
		vbox.setPadding(new Insets(16));
		
		getDialogPane().setContent(vbox);
		
		ButtonType okButton = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
	}

}
