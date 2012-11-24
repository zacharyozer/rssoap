package actions;

import java.util.List;

import model.Feed;
import model.Folder;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import view.NoItemSelectedException;
import control.ControlException;
import control.Controller;
/**
 * UnSubscribeFeedAction is responsible for unsubscribing from the selected feed.
 * @author pgroudas
 *
 */
/*
 * Depends on Controller, Feed and View
 */
public class UnSubscribeFeedAction extends Action {
	/**
	 * Constructs an UnSubscribeFeedAction
	 */
	public UnSubscribeFeedAction(){
		super("Unsubscribe from Feed");
		setImageDescriptor(ImageDescriptor.createFromFile(UnSubscribeFeedAction.class,"/images/unsubscribe.png"));
		setToolTipText("Unsubscribe from feed");
	}
	/**
	 * Invoked to Unsubscribe from the selected feed.
	 */
	public void run(){
		//gets the controller
		Controller c = Controller.getApp();
		try{
			//gets the selected feeds
			List<Feed> selected = c.getActiveView().getSelectedFeeds();
			//checks to see if selected feed is not outbox or trash
			int outboxID = c.getOutbox().getId();
			int trashID = c.getTrash().getId();
			for(Feed f:selected){
				if (f.getId()==outboxID||f.getId()==trashID){
					//if the user is trying to unsubscribe from outbox or trash
					c.setStatus("Cannot unsubscribe from top level items");
					continue;
				}else{
					//otherwise unsubscribe
					c.unSubscribe(f);
				}
			}
			if (selected.size()==1){
				//if it was only one feed, then update the view from its parent folder
				Folder parent = c.getActiveView().getParentOfSelected();
				c.getActiveView().updateFolderContents(parent);
			}else{
				//otherwise update the whoe view
				c.getActiveView().update();
			}
			//updates trash
			c.getActiveView().updateTrash();
		}
		catch(NoItemSelectedException e){
			//if no feed selected, then return with a message
			c.setStatus("No Feed Selected");
			return;
		}
		catch(ControlException e){
			//if something just broke, then return with error message
			c.setStatus(e.getMessage());
			return;
		}
	}
}