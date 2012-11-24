package actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import control.Controller;
import dialogs.NewArticleWizard;
/**
 * Action responsible for creating a new article
 * @author pgroudas
 *
 */
/*
 * depends on Controller, View and NewArticleWizard
 */
public class NewArticleAction extends Action {
	/**
	 * Constructs a new NewArticleAction
	 *
	 */
	public NewArticleAction(){
		super("Create a new Article");
		setImageDescriptor(ImageDescriptor.createFromFile(NewArticleAction.class,"/images/newarticle.png"));
		setToolTipText("Create New Article");
	}
	/**
	 * Invoked to create a new article using a NewArticleWizard.
	 */
	public void run(){
		Controller c = Controller.getApp();
		//opens wizard
		WizardDialog dlg = new WizardDialog(Display.getCurrent().getActiveShell(),new NewArticleWizard());
		dlg.open();
		//updates view
		c.getActiveView().updateOutbox();
	}
	
}
