package actions;


import org.eclipse.jface.action.Action;

import view.View;
import control.Controller;
/**
 * Action responsible for Changing the view.
 * 
 * @author pgroudas
 *
 */
/*
 * Depends on View, Controller
 */
public class ChangeViewAction extends Action {
	private View view;
	/**
	 * Constructs a ChangeViewAction that sets the View to the specified View
	 * @param v View to be changed to
	 * @param description String description
	 */
	public ChangeViewAction(View v, String description){
		super(description);
		view = v;
	}
	/**
	 * Invoked to change the view 
	 */
	public void run(){
		Controller.getApp().setActiveView(view);
	}
}
