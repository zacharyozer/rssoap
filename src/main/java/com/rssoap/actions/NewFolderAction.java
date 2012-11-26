package com.rssoap.actions;

import com.rssoap.control.ControlException;
import com.rssoap.control.Controller;
import com.rssoap.model.Folder;
import com.rssoap.view.NoItemSelectedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
/**
 * Action responsible for creating a new folder
 * @author pgroudas
 *
 */
/*
 * Depends on Controller, Folder and View
 */
public class NewFolderAction extends Action {
	/**
	 * Contructs a new NewFolderAction
	 *
	 */
	public NewFolderAction(){
		super();
		setText("Create New Folder");
		setImageDescriptor(ImageDescriptor.createFromFile(NewFolderAction.class,"/images/newfolder.png"));
		setToolTipText("Create a new folder");
	}
	/**
	 * Invoked when a user tries to add a new Folder.  The Folder is added to the currently selected folder, 
	 * or by default to the SubscribedFeeds Folder
	 */
	public void run(){
		//gets the controller
		Controller c = Controller.getApp();
		//gets target folder to put new folder in
		Folder target = null;
		try{
			target = c.getActiveView().getSelectedFolder();
		}catch(NoItemSelectedException e){
			try{
				//if no selected folder, create one in the selected feeds folder
				target = c.getSubscribedFeeds();
			}catch(ControlException ex){
				c.setStatus("Could not create folder, no folder selected");
				return;
			}
		}
		InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),"Create a new folder","Enter folder name and Click 'OK'","", null);
		//shows dialog for user to put in folder name
		int rc = dlg.open();
		String folderName;
		//if user clicked 'ok'
		if (rc == Window.OK){
			folderName = dlg.getValue();
			try{
				//adds new folder
				c.addFolder(target, new Folder(folderName));
				//refresh appropriate part of view
				c.getActiveView().updateFolderContents(target);
			}
			catch(ControlException e){
				c.setStatus("Could not create folder: "+e);
			}
		}
	}
}
