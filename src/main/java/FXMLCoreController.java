import java.util.Iterator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.SubredditReference;


public class FXMLCoreController {
	@FXML
	private ListView<Result> postList;
	@FXML
	private Button startButton;
	Thread t;

	@FXML
	protected void handleStartButton(ActionEvent event) {
		if (!t.getState().equals(Thread.State.RUNNABLE)) {
			t.start();
			startButton.setText("Stop");
		} else {
			t.interrupt();
			startButton.setText("Start");
		}

	}
	
	@FXML
	protected void initialize() {
		t = new Thread(new UpdateList(this));
	}

	public ListView<Result> getPostList() {
		return postList;
	}

}

class UpdateList implements Runnable {
	private FXMLCoreController controllerInstance;

	public UpdateList(FXMLCoreController instance) {
		controllerInstance = instance;
	}

	public void run() {
		SubredditReference all = Main.redditHelper.getRedditClient().subreddit("all");
		while (true) {
			DefaultPaginator<Submission> paginator = all.posts().sorting(SubredditSort.NEW).limit(30).build();
			Listing<Submission> submissions = paginator.next();
			
			for(Submission s : submissions) {
				System.out.println(s.getTitle());
			}

			controllerInstance.getPostList().getItems().add(new Result("Subreddit", "Title", "URL", "shortURL"));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}