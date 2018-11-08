package uk.co.harrypeach.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger LOGGER = LoggerFactory.getLogger(LogReaderDialog.class);
	
	public LogReaderDialog() {
		setTitle("Logs");
		setHeaderText("Log viewer");
		logList.setMinWidth(800);
		
		HBox hbox = new HBox();
		HBox.setHgrow(logList, Priority.ALWAYS);
		hbox.getChildren().add(logList);
		
		VBox vbox = new VBox();
		vbox.getChildren().add(hbox);
		vbox.setPadding(new Insets(16));
		
		getDialogPane().setContent(vbox);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String currentFormattedDate = formatter.format(date);
		String logFileName = "log/RedditMonitor." + currentFormattedDate + ".log";
		File f = new File(logFileName);
		if(f.exists() && !f.isDirectory()) {
			try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			    String line;
			    while ((line = br.readLine()) != null) {
			       logList.getItems().add(line);
			    }
			} catch (FileNotFoundException e) {
				LOGGER.error("FileNotFound Exception when accessing log file");
				LOGGER.error(e.getMessage());
				logList.getItems().add("Log could not be loaded");
				e.printStackTrace();
			} catch (IOException e) {
				LOGGER.error("IO Exception when accessing log file");
				LOGGER.error(e.getMessage());
				logList.getItems().add("Log could not be loaded");
				e.printStackTrace();
			}
		}else {
			logList.getItems().add("Log could not be loaded");
			LOGGER.warn("Log file could not be loaded as it does not exist");
		}
		
		ButtonType okButton = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(okButton);
	}

}
