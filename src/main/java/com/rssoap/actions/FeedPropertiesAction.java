package com.rssoap.actions;

import com.rssoap.control.ControlException;
import com.rssoap.control.Controller;
import com.rssoap.dialogs.FeedPropertiesDialog;
import com.rssoap.model.Feed;
import com.rssoap.view.NoItemSelectedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
/**
 * This class is responsible for opening the properties dialog for a feed to allow 
 * the user to change a feed's title and update interval.
 * @author pgroudas
 *
 */
/*
 * Depends on Controller, View, Feed and FeedPropertiesDialog
 */
public class FeedPropertiesAction extends Action {
	/**
	 * Constructs and new FeedPropertiesAction
	 *
	 */
	public FeedPropertiesAction(){
		super("Properties");
		setImageDescriptor(ImageDescriptor.createFromFile(FeedPropertiesAction.class,"/images/properties.png"));
	}
	/**
	 * Invoked to open our FeedPropertiesDialog and perform the appropriate changes to the properties of 
	 * a feed.
	 */
	public void run(){
		//gets the controller
		Controller c = Controller.getApp();
		try{
			//gets the selected feed
			Feed f = c.getActiveView().getSelectedFeed();
			//opens dialog
			FeedPropertiesDialog dlg= new FeedPropertiesDialog(Display.getCurrent().getActiveShell(),f);
			int rc = dlg.open();
			if (rc == Window.OK){
				//if they closed the dialog with okay, then change updateinterval and title appropriately
				int interval = Integer.parseInt(dlg.getUpdateInterval());
				String title = dlg.getTitle();
				if (interval < 1 || interval > 1349){
					//if they changed the update interval inappropriately, let them know and rerun.
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Problem", "The update interval must be an integer between 1 and 1440");
					//c.setStatus("Enter an integer number of minutes between 1 and 1440");
					run();
					return;
				}else{
					//sets the title if its been changed
					if (!title.equals(f.getTitle())){
						c.renameFeed(f, title);	
						f.setTitle(title);
					}
					//changes the interval if its been changed
					if (interval!=f.getUpdateInterval()){
						c.setFeedUpdateInterval(f, interval);
						c.getActiveView().updateFeed(f);
						c.setStatus("Update interval changed to "+interval+" for Feed: "+f.getTitle());
					}
				}
			}
		}catch(NoItemSelectedException e){
			c.setStatus("No feed selected");
			return;
		}catch(NumberFormatException e){
			c.setStatus("Couldn't parse value, Enter an integer number of minutes between 1 and 1440");
			run();
			return;
		}catch(ControlException e){
			c.setStatus(e.getMessage());
		}
	}
			
}
