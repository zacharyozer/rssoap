package com.rssoap.actions;

import com.rssoap.control.ControlException;
import com.rssoap.control.Controller;
import com.rssoap.model.Folder;
import com.rssoap.parser.RomeFeedParser;
import com.rssoap.view.NoItemSelectedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
/**
 * Action responsible for importing a list of feeds from a local OPML file.
 * @author pgroudas
 *
 */
/*
 * Depends on Controller, Folder, View and RomeFeedParser
 */
public class ImportFromFileAction extends Action implements IRunnableWithProgress{
	private Folder target;
	private List<String> urls;
	private String opmlFilePath;
	private Controller c;
	/**
	 * Contructs a new ImportFromURLAction
	 *
	 */
	public ImportFromFileAction(){
		super();
		setText("Import from local OPML file");
		setImageDescriptor(ImageDescriptor.createFromFile(ImportFromFileAction.class,"/images/importfromfile.png"));
		setToolTipText("Import a group of feed from a OMPL file on your local disk");
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
			//since importing could be a lot of feeds, we don't default it to the top level folder like
			//subscribe
			MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Warning", "Please select a folder to import feeds into");
			//c.setStatus("Could not import feeds, no folder selected");
			return;
		}
		//gets the file path
		FileDialog dlg = new FileDialog(Display.getCurrent().getActiveShell(),SWT.OPEN);
		String[] OPMLExtensions = { "*.opml", "*.*" };
		String[] OPMLExtensionNames = { "OPML Files (*.opml)", "All Files (*.*)" };
		dlg.setFilterExtensions(OPMLExtensions);
		dlg.setFilterNames(OPMLExtensionNames);
		opmlFilePath = dlg.open();
		if (opmlFilePath!=null){
			//if they selected a file
			try{
				StatusLineManager s = c.getStatusLine();
				//System.out.println(s.isCancelEnabled());
				s.setCancelEnabled(true);
				//performs action in progress dialog since it takes awhile
				ModalContext.run(this,true,s.getProgressMonitor(),Display.getCurrent());
				//when its done it updates the view
				//note:  the previous line should run the updating process in another thread, but this
				//still updates the view when its done even though the previous method shouldn't block.
				//oh well, its not critical and it works this way.
				c.getActiveView().updateFolderContents(target);
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
	 * This method is invoked and paired with a progress monitor to monitor 
	 * progress of the addition of the feeds.
	 * 
	 * @param monitor ProgressMonitor
	 * @throws InterruptedException if the method is cancelled.
	 */
	public void run(IProgressMonitor monitor)throws InterruptedException{
		int numFeeds = 0;
		//starts the tast with an allocation of 10000 work
		monitor.beginTask("Importing Feeds",10000);
		monitor.subTask("Getting OPML file");
		try{
			//gets the urls of all the feeds to add
			//System.out.println("Target: "+target);
			urls = RomeFeedParser.ParseOPMLFile(opmlFilePath);
			//gets numFeeds to know the fraction of work thats done each iteration
			numFeeds = urls.size();
		}catch(Exception e){
			//if the opml parser can't do it...show an error dialog
			monitor.done();
			throw new InterruptedException("Couldn't open file: "+e);
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

