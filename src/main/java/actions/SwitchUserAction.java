package actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import control.Controller;
import dialogs.SwitchUserDialog;
/**
 * This class is responsible for opening a switch user dialog to allow
 *  the user to swich user profiles
 * @author pgroudas
 *
 */
/*
 *Depends on Controller and SwitchUserDialog 
 */
public class SwitchUserAction extends Action {
	/**
	 * Constructs a new SwitchUserAction
	 */
	public SwitchUserAction(){
		super("Switch user");
		setImageDescriptor(ImageDescriptor.createFromFile(SwitchUserAction.class,"/images/users.png"));
		setToolTipText("Switch users");
	}
	/**
	 * Invoked to bring up the switch user dialog and allow the user to change the user profile
	 */
	public void run(){
		Controller c = Controller.getApp();
		SwitchUserDialog dlg = new SwitchUserDialog(Display.getCurrent().getActiveShell());
		//shows dialog for user to put in username
		int rc = dlg.open();
		if (rc == Window.OK){
			//if they clicked okay, switch user
			c.switchUser(dlg.getSelectedUser());
		}else{
			//do nothing
		}
	}
}
