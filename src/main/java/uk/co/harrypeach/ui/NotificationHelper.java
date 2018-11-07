package uk.co.harrypeach.ui;

import org.controlsfx.control.Notifications;

import javafx.application.Platform;
import javafx.util.Duration;

public class NotificationHelper {
	
		public static final int DEFAULT_NOTIFICATION_DURATION = 4000;
		public static enum NotificationType { INFO, WARN, ERROR, CONFIRM, DEFAULT }
	
		/**
		 * Creates a notification to be displayed to the user
		 * @param title The title of the notification
		 * @param text The text in the body of the notification
		 * @param duration How long the notification stays on the screen in milliseconds
		 */
		public void createNotification(String title, String text, int duration, NotificationType type) {
			Notifications notification = Notifications.create();
			notification.title(title);
			notification.text(text);
			notification.hideAfter(new Duration(duration));
			switch(type) {
				case INFO:
					Platform.runLater(() -> notification.showInformation());
					break;
				case WARN:
					Platform.runLater(() -> notification.showWarning());
					break;
				case ERROR:
					Platform.runLater(() -> notification.showError());
					break;
				case CONFIRM:
					Platform.runLater(() -> notification.showConfirm());
					break;
				case DEFAULT:
				default:
					Platform.runLater(() -> notification.show());
					break;
			}
		}
		
		public void createNotification(String title, String text, int duration) {
			createNotification(title, text, duration, NotificationType.INFO);
		}
		
		public void createNotificiation(String title, String text, NotificationType type) {
			createNotification(title, text, DEFAULT_NOTIFICATION_DURATION, type);
		}
		
		public void createNotification(String title, String text) {
			createNotification(title, text, DEFAULT_NOTIFICATION_DURATION);
		}
}
