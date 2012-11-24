package actions;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import control.ControlException;
import control.Controller;
/**
 * Action responsible for emptying the trash.
 * @author pgroudas
 *
 */
/*
 * Depends on Controller and View
 */
public class EmptyTrashAction extends Action {
	/**
	 * Constructs an EmptyTrashAction
	 */
	public EmptyTrashAction(){
		super("Empty Trash");
		setImageDescriptor(ImageDescriptor.createFromFile(EmptyTrashAction.class,"/images/emptytrash.png"));
		setToolTipText("Empties the trash bin");
	}
	/**
	 * Invoked to empty the trash bin.
	 */
	public void run(){
		//gets the controller
		Controller c = Controller.getApp();
		try{
			//empties the trash bin
			c.emptyTrash();
			//updates the view appropriately
			c.getActiveView().updateTrash();
		}
		catch(ControlException e){
			//if theres some random issue updating the trash, just return
			c.setStatus("Error occured while emptying trash");
		}
	}
}
