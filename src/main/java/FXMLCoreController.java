import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.SubredditReference;

public class FXMLCoreController {
	@FXML
	private ListView<Result> postList;
	@FXML
	private Button startButton;
	public boolean threadEnabled = false;
	Thread t;

	@FXML
	protected void handleStartButton(ActionEvent event) {
		if (t.getState().equals(Thread.State.NEW)) {
			t.start();
		}
		threadEnabled = !threadEnabled;
		String buttonText = (startButton.getText().equals("Start"))
				? ("Stop")
				: ("Start");
		startButton.setText(buttonText);
	}

	@FXML
	protected void initialize() {
		t = new Thread(new UpdateList(this));
		t.setDaemon(true);
	}

	@FXML
	public void exitApplication(ActionEvent event) {
		threadEnabled = false;
	}

	public ListView<Result> getPostList() {
		return postList;
	}

	public void shutdown() {
		threadEnabled = false;
	}

}

class UpdateList implements Runnable {
	private FXMLCoreController controllerInstance;
	Queue<Result> resultQueue = new LinkedList<>();
	List<String> stringList = Arrays.asList("steam key", "giving away", "giveaway", "cd key", "steam code", "cd code",
			"spare code", "spare key", "extra key", "extra code", "give away", "the");

	public UpdateList(FXMLCoreController instance) {
		controllerInstance = instance;
	}

	public void run() {
		SubredditReference all = Main.redditHelper.getRedditClient().subreddit("all");

		while (true) {
			if (controllerInstance.threadEnabled) {
				try {
					DefaultPaginator<Submission> paginator = all.posts().sorting(SubredditSort.NEW).limit(30).build();
					Listing<Submission> submissions = paginator.next();

					for (Submission s : submissions) {
						Result r = new Result(s.getSubreddit(), s.getTitle(), s.getUrl(), s.getId());
						if (titleContainsWordList(r.getTitle(), stringList) && !containsResult(resultQueue, r)) {
							addToQueue(r);
							Runnable updater = new Runnable() {
								public void run() {
									controllerInstance.getPostList().getItems().add(r);
								}
							};
							Platform.runLater(updater);
						}
					}
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private boolean titleContainsWordList(String word, List<String> wordList) {
		for(String s : wordList) {
			if(word.toLowerCase().contains(s)) {
				return true;
			}
		}
		return false;
	}

	private boolean containsResult(Queue<Result> resultQueueIn, Result testResult) {
		for (Result result : resultQueueIn) {
			if (result.getId().equals(testResult.getId())) {
				return true;
			}
		}
		return false;
	}

	private void addToQueue(Result r) {
		if (resultQueue.size() >= 100) {
			resultQueue.remove();
			resultQueue.add(r);
		} else {
			resultQueue.add(r);
		}
		Runnable updater = new Runnable() {
			public void run() {
				controllerInstance.getPostList().scrollTo(controllerInstance.getPostList().getItems().size());
			}
		};
		Platform.runLater(updater);
	}
}