package actions;

import java.lang.reflect.InvocationTargetException;

import model.Folder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import view.NoItemSelectedException;
import control.ControlException;
import control.Controller;

/**
 * A SubscibeAction is invoked when the user tries to Subscribe to a new feed.
 * @author pgroudas
 *
 */
/*
 * Depends on Controller and Folder
 */
public class SubscribeAction extends Action implements IRunnableWithProgress{
	private String url = "";
	private Folder target = null;
	
	/**
	 * Contructs a new SubscribeAction
	 *
	 */
	public SubscribeAction(){
		super();
		setText("Subscribe to New Feed");
		setAccelerator(SWT.MOD1+'+');
		setImageDescriptor(ImageDescriptor.createFromFile(SubscribeAction.class,"/images/subscribe.png"));
		setToolTipText("Subscribe to a new feed in the currently selected folder");
	}
	/**
	 * Invoked when a user tries to Subscribe to a new feed.
	 */
	public void run(){
		//gets the controller
		Controller c = Controller.getApp();
		target = null;
		//gets the target folder to put the new feed 
		try{
			target = c.getActiveView().getSelectedFolder();
			//System.out.println(target);
		}catch(NoItemSelectedException e){
			//if no folder selected, put new feed in the subscribed feeds folder
			try{
				target = c.getSubscribedFeeds();
			}catch(ControlException ex){
				//if theres any kind of problem with getting the target, just return
				c.setStatus("Could not subscribe to feed, no folder selected");
				return;
			}
		}
		//uses imput dialog to get url of feed
		InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),"Subscribe to New Feed","Enter Feed URL and Click 'OK'","", null);
		int rc = dlg.open();
		//if user closed dialog with 'okay' button
		if (rc == Window.OK){
			//gets url of feed
			url = dlg.getValue();
			try{
				//prepares statusline as progress monitor
				StatusLineManager s = c.getStatusLine();
				s.setCancelEnabled(true);
				//performs action in progress dialog since it takes awhile
				ModalContext.run(this,true,s.getProgressMonitor(),Display.getCurrent());
				//tries to subscribe in another thread
				//updates the folder contents
				c.getActiveView().updateFolderContents(target);
				c.setStatus("done");
				return;
			}catch(InterruptedException e){
				//if couldn't subscribe, set message and then run again
				MessageDialog.openError(Display.getCurrent().getActiveShell(),"Error","The feed couldn't be subscribed to for the following reason: "+e.getMessage());
				//c.setStatus(e.getMessage());
				run(url);
			}catch(InvocationTargetException e){
				return;
			}
		}
	}
	/**
	 * Invoked when a user tries to Subscribe to a new feed.
	 */
	public void run(String oldurl){
		//gets the controller
		Controller c = Controller.getApp();
		target = null;
		//gets the target folder to put the new feed 
		try{
			target = c.getActiveView().getSelectedFolder();
		}catch(NoItemSelectedException e){
			c.setStatus("Could not subscribe to feed, no folder selected");
			return;
		}
		//uses imput dialog to get url of feed
		InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),"Subscribe to New Feed","Enter Feed URL and Click 'OK'",oldurl, null);
		int rc = dlg.open();
		//if user closed dialog with 'okay' button
		if (rc == Window.OK){
			//gets url of feed
			url = dlg.getValue();
			try{
				//tries to subscribe in another thread
				ModalContext.run(this,true,c.getStatusLine().getProgressMonitor(),Display.getCurrent());
//				updates the folder contents
				c.getActiveView().updateFolderContents(target);		
				c.setStatus("done");
				return;
			}catch(InterruptedException e){
				//if couldn't subscribe, show error dialog and run again
				MessageDialog.openError(Display.getCurrent().getActiveShell(),"Error","The feed couldn't be subscribed to for the following reason: "+e.getMessage());
				//c.setStatus(e.getMessage());
				run(url);
			}catch(InvocationTargetException e){
				return;
			}
		}
	}
	/**
	 * Invoked to subscribe to a feed in another thread
	 */
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		//gets controller
		Controller c = Controller.getApp();
		//starts task
		monitor.beginTask("Subscribing to feed...", 100);
		Thread.sleep(200);
		monitor.worked(33);
		try{
			c.subscribe(url,target);
		}
		catch(ControlException e){
			//if theres a problem throw an exception
			monitor.done();
			throw new InterruptedException(e.getMessage());
		}
		monitor.worked(33);
		Thread.sleep(200);
		monitor.worked(33);
		monitor.done();
		return;
	}
	
}
