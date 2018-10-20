import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

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
		if(t.getState().equals(Thread.State.NEW)) {
			t.start();
		}
		threadEnabled = !threadEnabled;

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

	public UpdateList(FXMLCoreController instance) {
		controllerInstance = instance;
	}

	public void run() {
		SubredditReference all = Main.redditHelper.getRedditClient().subreddit("all");
		
		while (true) {
			if(controllerInstance.threadEnabled) {
				try {
					DefaultPaginator<Submission> paginator = all.posts().sorting(SubredditSort.NEW).limit(30).build();
					Listing<Submission> submissions = paginator.next();
					
					for(Submission s : submissions) {
						Result r = new Result(s.getSubreddit(), s.getTitle(), s.getUrl(), s.getId());
						if(!containsResult(resultQueue, r)) {
							addToQueue(r);
							controllerInstance.getPostList().getItems().add(r);
						}
					}				
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean containsResult(Queue<Result> resultQueueIn, Result testResult) {
		for(Result result : resultQueueIn) {
			if(result.getId().equals(testResult.getId())) {
				return true;
			}
		}
		return false;
	}
	
	private void addToQueue(Result r) {
		if(resultQueue.size() >= 100) {
			resultQueue.remove();
			resultQueue.add(r);
		}else {
			resultQueue.add(r);
		}
	}
}