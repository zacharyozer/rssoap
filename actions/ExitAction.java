package actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;

import control.Controller;
/**
 * ExitAction is the action responsible for closing the Application.
 * @author pgroudas
 */
/*
 * Depends on Controller
 */
public class ExitAction extends Action {
	/**
	 * Constructs an Exit Action
	 */
	public ExitAction(){
		super("E&xit");
		setAccelerator(SWT.MOD1+'q');
		setImageDescriptor(ImageDescriptor.createFromFile(ExitAction.class,"/images/exit.png"));
		setToolTipText("Exits the Program");
	}
	/**
	 * Invoked to Close the application
	 */
	public void run(){
		//closes the application
		Controller.getApp().close();
	}
}
