package com.rssoap.actions;

import com.rssoap.control.ControlException;
import com.rssoap.control.Controller;
import com.rssoap.model.Feed;
import com.rssoap.view.NoItemSelectedException;
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
 * UpdateFeedAction is responsible for updating the selected feed.
 * @author pgroudas
 *
 */
/*
 * Depends on Controller, Feed and View
 */
public class UpdateFeedAction extends Action implements IRunnableWithProgress{
	Controller c;
	List<Feed> selected;
	/**
	 * Constructs an UpdateFeedAction
	 */
	public UpdateFeedAction(){
		super("Update Feed");
		setImageDescriptor(ImageDescriptor.createFromFile(UpdateFeedAction.class,"/images/update.png"));
		setToolTipText("Update feed");
	}
	/**
	 * Invoked to Update the selected feed.
	 */
	public void run(){
		//gets the controller
		c = Controller.getApp();
		//gets the selected feed, and updates in in another thread with the
		//statusbar as the progress monitor
		try{
			selected = Controller.getApp().getActiveView().getSelectedFeeds();
			//prepares the progress monitor
			StatusLineManager s = c.getStatusLine();
			s.setCancelEnabled(true);
			//performs action in another thread since it takes awhile
			ModalContext.run(this,true,s.getProgressMonitor(),Display.getCurrent());
			//updates view
			c.getActiveView().update();
			return;
		}
		catch(NoItemSelectedException e){
			c.setStatus("No Feed Selected");
			return;
		}catch(InterruptedException e){
			c.setStatus("Problem updating feed, please try again");
			return;
		}catch(InvocationTargetException e){
			return;
		}
	}
	/**
	 * Invoked to update the selected feed while reporting to the specified 
	 * progress monitor
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		//gets controller
		Controller c = Controller.getApp();
		//starts task
		monitor.beginTask("Updating feed...", 100000);
		int increment = 100000/selected.size();
		//updates feed
		for (Feed f:selected){
			try{
				monitor.subTask(f.getTitle());
				c.updateFeed(f);
				//System.out.println("feed updated");
				monitor.worked(increment);
			}
			//if theres a problem, report status and quit
			catch(ControlException e){
				c.setStatus("problem updating feeds");
				continue;
			}
		}
		monitor.done();
		return;
	}
}
