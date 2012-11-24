package actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import control.Controller;
/**
 * Action responsible for creating a new user
 * @author pgroudas
 *
 */
/*
 * Depends on Controller
 */
public class NewUserAction extends Action implements Runnable{
	/**
	 * Constructs a new NewUserAction
	 *
	 */
	public NewUserAction(){
		super("Create a new user");
		setImageDescriptor(ImageDescriptor.createFromFile(NewUserAction.class,"/images/user.png"));
		setToolTipText("Create New Article");
	}
	/**
	 * Invoked to create a new user using a NewUserWizard.
	 */
	public void run(){
		Controller c = Controller.getApp();
		InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),"Create a new user","Enter new username","", null);
		//shows dialog for user to put in username
		int rc = dlg.open();
		String username;
		//if user clicked 'ok'
		if (rc == Window.OK){
			username = dlg.getValue();
			if (username.equals("")){
				//if they entered blank user name, show and error dialog
				MessageDialog.openError(c.getShell(), "Problem", "Please enter a username");
				//makes sure users are in a consistent state
				c.checkUser();
			}
			else if (c.isUser(username)){
				//if there is already a user with that name, show an error dialog
				MessageDialog.openError(c.getShell(), "Problem", "Cannot create a new user with the following username: "
						+username+"\n\n There is already a user with that username");
				c.checkUser();
			}else{
				//if everythings okay create and switch to the new user
				c.createAndSwitchUser(username);
				c.setStatus("Created new user: "+username);
			}
		}else if(rc == Window.CANCEL){
			//just to be sure....
			c.checkUser();
		}
	}
}