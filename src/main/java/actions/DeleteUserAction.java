package actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import control.ControlException;
import control.Controller;
import dialogs.DeleteUserDialog;
/**
 * This class is responsible for deleting a user.
 * @author pgroudas
 *
 */
public class DeleteUserAction extends Action {
	/**
	 * Constructs a new SwitchUserAction
	 *
	 */
	/*
	 * Depends on Controller and DeleteUserDialog
	 */
	public DeleteUserAction(){
		super("Delete user");
		setImageDescriptor(ImageDescriptor.createFromFile(DeleteUserAction.class,"/images/deleteuser.png"));
		setToolTipText("Delete a user");
	}
	/**
	 * Invoked to bring up a dialog to delete a user
	 */
	public void run(){
		//gets the controller
		Controller c = Controller.getApp();
		DeleteUserDialog dlg = new DeleteUserDialog(Display.getCurrent().getActiveShell());
		//shows dialog for user to put in username
		int rc = dlg.open();
		if (rc == Window.OK){
			try {
				//try to delete selected user
				c.deleteUser(dlg.getSelectedUser());
			} catch (ControlException e){
				//catches error if they try to delete the current user, show an error dialog and try again
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Sorry", "You can't delete the active user.");
				run();
			}
		}else{
			//if canceled then just quit.
		}
	}
}
