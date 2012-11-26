package com.rssoap.actions;

import com.rssoap.control.ControlException;
import com.rssoap.control.Controller;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
/**
 * This class is responsible for resetting the program if for some reason the database has gotten 
 * corrupted and the software is not working properly
 * @author pgroudas
 *
 */
/*
 * Depends on Controller
 */
public class ResetAction extends Action {
	/**
	 * Constructs a new ResetAction
	 *
	 */
	public ResetAction(){
		super("Delete all user data");
		setImageDescriptor(ImageDescriptor.createFromFile(ResetAction.class,"/images/agent.png"));
		setToolTipText("Reset the database");
	}
	/**
	 * Invoked to reset the underlying database.  This will destroy all user data, and thus should only 
	 * be used if for some reason the database has become corrupted and the software is no longer usable.
	 */
	public void run(){
		//shows a confirm dialog to inform the user that they probably shouldn't do this.
		boolean sure = MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Warning", "Are you sure you want to this? " +
				" All user data will be lost.  ");
		if (sure){
			try{
				//resets
				Controller.getApp().reset();
			}catch(ControlException e){
				//if theres a probleming reseting the program closes
				MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Error", "Something very bad has happened, and this software needs to quit.  " +
						"One might call this a 'Fatal Error'");
				Controller.getApp().close();
			}
		}
	}
}
