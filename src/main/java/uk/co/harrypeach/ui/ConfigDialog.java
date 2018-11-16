package uk.co.harrypeach.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uk.co.harrypeach.core.Main;

public class ConfigDialog extends Dialog<String> {

	private CheckBox nsfwFilteringCheckbox = new CheckBox("Filter NSFW posts");
	private CheckBox alertSoundCheckbox = new CheckBox("Play alert sounds");
	private CheckBox notificationsCheckbox = new CheckBox("Enable pop-up notifications");

	private GridPane volumeGrid = new GridPane();
	private Separator volumeSeparator = new Separator();
	private Label volumeSliderInfoLabel = new Label("Alert volume");
	private Slider volumeSlider = new Slider(0, 1, 0.2);
	private Label volumeSliderLabel = new Label(Double.toString(volumeSlider.getValue()));

	private Label infoLabel = new Label("More advanced options are available in your config.yml");

	public ConfigDialog() {
		setTitle("Configuration");
		setHeaderText("Configuration Manager");

		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("icon.png")));

		// Checkbox settings
		nsfwFilteringCheckbox.setTooltip(new Tooltip("Whether or not to filter NSFW posts"));
		alertSoundCheckbox
				.setTooltip(new Tooltip("Whether or not to play an alert sound when a matching post is found"));
		notificationsCheckbox.setTooltip(new Tooltip("Whether or not to display pop-up notifications"));

		// Volume settings
		volumeSeparator.setPadding(new Insets(10, 0, 10, 0));
		GridPane.setConstraints(volumeSliderInfoLabel, 0, 0);
		GridPane.setConstraints(volumeSlider, 0, 1);
		GridPane.setConstraints(volumeSliderLabel, 1, 1);
		volumeGrid.getChildren().addAll(volumeSliderInfoLabel, volumeSlider, volumeSliderLabel);
		volumeSlider.setShowTickLabels(true);
		volumeSlider.setTooltip(new Tooltip("The volume at which the alert sound should play"));
		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				volumeSliderLabel.setText(String.format("%.2f", new_val));
			}
		});

		VBox vbox = new VBox();
		vbox.getChildren().addAll(nsfwFilteringCheckbox, alertSoundCheckbox, notificationsCheckbox, volumeSeparator,
				volumeGrid, infoLabel);

		loadUiFromConfig();
		getDialogPane().setContent(vbox);

		ButtonType okButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
	}

	/**
	 * Loads values from the config file and updates the UI to match
	 */
	private void loadUiFromConfig() {
		nsfwFilteringCheckbox.setSelected(Main.config.getConfigInstance().isNsfwFilteringEnabled());
		alertSoundCheckbox.setSelected(Main.config.getConfigInstance().isAlertSoundEnabled());
		notificationsCheckbox.setSelected(Main.config.getConfigInstance().isNotificationsEnabled());
		volumeSlider.setValue(Main.config.getConfigInstance().getAlertSoundVolume());
	}

}
