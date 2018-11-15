package uk.co.harrypeach.ui;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uk.co.harrypeach.core.Main;

public class ConfigDialog extends Dialog<String>{
	
	private CheckBox nsfwFilteringCheckbox = new CheckBox("Filter NSFW posts");
	
	public ConfigDialog() {
		setTitle("Configuration");
		setHeaderText("Configuration Manager");
		
		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("icon.png")));
		
		nsfwFilteringCheckbox.setTooltip(new Tooltip("Whether or not to filter NSFW posts"));
		
		VBox vbox = new VBox();
		vbox.getChildren().addAll(nsfwFilteringCheckbox);
		
		loadUiFromConfig();
		getDialogPane().setContent(vbox);
		
		ButtonType okButton = new ButtonType("Done", ButtonBar.ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
	}
	
	/**
	 * Loads values from the config file and updates the UI to match
	 */
	private void loadUiFromConfig() {
		nsfwFilteringCheckbox.setSelected(Main.config.getConfigInstance().isNsfwFilteringEnabled());
	}

}
