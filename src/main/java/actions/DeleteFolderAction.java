package actions;

import model.Folder;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import view.NoItemSelectedException;
import control.ControlException;
import control.Controller;
/**
 * Action responsible for deleting a folder.
 * @author pgroudas
 *
 */
public class DeleteFolderAction extends Action {
	/**
	 * Constructs an DeleteFolderAction
	 */
	/*
	 * This class depends on Controller, Folder, and View
	 */
	public DeleteFolderAction(){
		super("Delete Folder");
		setImageDescriptor(ImageDescriptor.createFromFile(DeleteFolderAction.class,"/images/deletefolder.png"));
		setToolTipText("Deletes the selected folder");
	}
	/**
	 * Invoked to Delete the selected Folder
	 */
	public void run(){
		//gets the controller
		Controller c = Controller.getApp();
		try{
			//gets the selected folder.
			Folder selected = c.getActiveView().getSelectedFolder();
			//if the folder is the top-level folder, then it can't be deleted.
			if (selected.getId()==c.getSubscribedFeeds().getId()){
				c.setStatus("Cannnot delete top-level folder");
				return;
			}
			//gets the parent of the selected item.
			Folder parent = c.getActiveView().getParentOfSelected();
			//deletes the selected article
			c.deleteFolder(selected);
			//updates view
			c.getActiveView().updateFolderContents(parent);
		}
		catch(NoItemSelectedException e){
			//no selected folder, just inform user and return
			c.setStatus("No Folder Selected");
			return;
		}
		catch(ControlException e){
			//some type of problem with the database...just return
			c.setStatus("Could not delete folder due to an internal error");
			return;
		}
	}
}