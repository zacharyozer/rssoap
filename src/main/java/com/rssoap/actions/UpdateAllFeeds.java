package com.rssoap.actions;

import com.rssoap.control.ControlException;
import com.rssoap.control.Controller;
import com.rssoap.model.Feed;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
/**
 * This class is responsible for updating all feeds the user is subscribed to
 * @author pgroudas
 *
 */
/*
 * Depends on Controller and Feed 
 */
public class UpdateAllFeeds extends Action implements IRunnableWithProgress{
	private List<Feed> feeds;
	/**
	 * Constructs a new UpdateAllFeeds (action)
	 *
	 */
	public UpdateAllFeeds(){
		super("Update all feeds");
		setImageDescriptor(ImageDescriptor.createFromFile(UpdateAllFeeds.class,"/images/updateall.png"));
		setToolTipText("Updates all of your feeds immediately");
	}
	/**
	 * Invoked to update all the users feeds.
	 */
	public void run(){
		//gets the controller
		Controller c = Controller.getApp();
		try{
			//gets the feeds
			feeds = c.getAllFeeds();
			//sets up the progress monitor
			StatusLineManager s = c.getStatusLine();
			s.setCancelEnabled(true);
			//performs action in progress dialog since it takes awhile
			ModalContext.run(this,true,s.getProgressMonitor(),Display.getCurrent());
		}catch(ControlException e){
			//do nothing
		}catch(InterruptedException e){
			c.setStatus("Cancelled by user");
		}catch(InvocationTargetException e){
			c.setStatus("Done");
		}
	}
	/**
	 * Invoked to update all feeds while reporting to the specified 
	 * progress monitor
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		//gets controller
		Controller c = Controller.getApp();
		//starts task
		monitor.beginTask("Updating feed...", 100000);
		int numFeeds = feeds.size();
		//figures out appropriate increment
		int increment = 100000/numFeeds;
		for(Feed f:feeds){
			if(monitor.isCanceled()){
				//if process is ever cancelled, then quit
				break;
			}
			monitor.subTask(f.getTitle());
			try{
				//update the feed
				c.updateFeed(f);
			}catch(ControlException e){
				//if there is an individual problem updating a feed, just ignore it
				monitor.worked(increment);
				continue;
			}
			//increment the monitor
			monitor.worked(increment);
		}
		//cleanup and return
		monitor.done();
	}
}
