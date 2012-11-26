package com.rssoap.actions;

import com.rssoap.control.ControlException;
import com.rssoap.control.Controller;
import com.rssoap.model.Folder;
import com.rssoap.parser.RomeFeedParser;
import com.rssoap.view.NoItemSelectedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
/**
 * Action responsible for importing a list of feeds from a remote OPML file over the internet.
 * @author pgroudas
 *
 */
/*
 * Depends on Controller, Folder, View, RomeFeedParser
 */
public class ImportFromURLAction extends Action implements IRunnableWithProgress{
	private Folder target;
	private List<String> urls;
	private String opmlurl;
	private Controller c;
	/**
	 * Constructs a new ImportFromURLAction.
	 *
	 */
	public ImportFromURLAction(){
		super();
		setText("Import from remote OMPL document");
		setImageDescriptor(ImageDescriptor.createFromFile(ImportFromURLAction.class,"/images/importfromurl.png"));
		setToolTipText("Import a group of feeds from a remote OPML file");
	}
	/**
	 * Invoked when a user tries to Import a list of feeds in OPML form.
	 */
	public void run(){
		c = Controller.getApp();
		//sets the selected folder so we know how much the view needs to refresh
		try{
			target = c.getActiveView().getSelectedFolder();
		}catch(NoItemSelectedException e){
			//if they haven't selected a folder, let them know so they aren't confused why it isn't doing anything
			MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Warning", "Please select a folder to import feeds into");
			return;
		}
		//gets the url
		InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(),"Import from OPML","Enter OPML file URL and Click 'OK'","", null);
		int rc = dlg.open();
		if (rc == Window.OK){
			opmlurl = dlg.getValue();
			try{
				//prepares the statusline to use as the progress monitor
				StatusLineManager s = c.getStatusLine();
				s.setCancelEnabled(true);
				//performs action in progress dialog since it takes awhile
				ModalContext.run(this,true,s.getProgressMonitor(),Display.getCurrent());
				//updates the folder contents
				c.getActiveView().updateFolderContents(target);
				c.setStatus("done");
			}catch(InterruptedException e){
				c.setStatus("Problem opening file: "+e.getMessage());
				c.getActiveView().updateFolderContents(target);
			}catch(InvocationTargetException e){
				c.setStatus("Done");
				c.getActiveView().updateFolderContents(target);
			}
			catch(Exception e){
				//if the tread throws some big time exception just exit gracefully
				c.setStatus("Error importing feeds: "+e);
				c.getActiveView().updateFolderContents(target);
			}
		}
	}
	/**
	 * This method is invoked and paired with a progress dialog to monitor the 
	 * progress of the addition of the feeds.
	 * 
	 * @param monitor
	 * @throws InterruptedException if the method is cancelled.
	 */
	public void run(IProgressMonitor monitor) throws InterruptedException{
		int numFeeds = 0;
		//starts the tast with an allocation of 10000 work
		monitor.beginTask("Importing Feeds",10000);
		monitor.subTask("Getting OPML file");
		try{
			//gets the urls of all the feeds to add
			//System.out.println("Target: "+target);
			urls = RomeFeedParser.ParseOPMLURL(opmlurl);
			//gets numFeeds to know the fraction of work thats done each iteration
			numFeeds = urls.size();
		}catch(Exception e){
			//if the opml parser can't do it...show an error dialog
			throw new InterruptedException("Couldn't open location: "+e.getMessage());
		}
		for(int i=0;i < numFeeds && !monitor.isCanceled();i++){
			//for each feed, show what its subscribing to, increment the progress monitor, and
			//then subscribe.
			monitor.subTask("Subscribing to: "+urls.get(i));
			monitor.worked(10000/numFeeds);
			try{
				c.subscribe(urls.get(i),target);
			}catch(ControlException e){
				//if there is a problem getting one feed, just ignore it and go on.
				continue;
			}
		}
		if (monitor.isCanceled()){
			monitor.done();
			throw new InterruptedException("Cancelled by user");
		}
		monitor.done();
	}
}
