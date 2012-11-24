package actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import control.Controller;
/**
 * Action responsible for refreshing the view
 * @author pgroudas
 *
 */
/*
 * Depends on Controller and View
 */
public class RefreshAction extends Action {
	/**
	 * Constructs an refresh action.
	 */
	public RefreshAction(){
		super("Refresh View");
		setImageDescriptor(ImageDescriptor.createFromFile(RefreshAction.class,"/images/refresh.png"));
		setToolTipText("Refresh Current View");
	}
	/**
	 * Invoked to Refresh the current view.
	 */
	public void run(){
		Controller.getApp().getActiveView().update();
	}
}
