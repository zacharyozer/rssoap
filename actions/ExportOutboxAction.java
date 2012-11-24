package actions;

import java.io.File;
import java.io.FileWriter;

import model.Feed;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import parser.RomeFeedParser;
import control.ControlException;
import control.Controller;
/**
 * The ExportOutboxAction class is responsible for exporting the users outbox to 
 * an rss feed.
 * 
 * @author pgroudas
 *
 */
public class ExportOutboxAction extends Action {
	/**
	 * Constructs an ExportOutboxAction
	 *
	 */
	/*
	 * Depends on Controller, Feed, and RomeFeedParser
	 */
	public ExportOutboxAction(){
		super("Export outbox to file");
		setImageDescriptor(ImageDescriptor.createFromFile(ExportOutboxAction.class,"/images/exportoutbox.png"));
		setToolTipText("Exports outbox as an RSS 2.0 file");
	}
	/**
	 * Invoked to export the outbox to a file.
	 */
	public void run(){
		//gets the controller
		Controller c = Controller.getApp();
		//gets the outbox
		Feed outbox = null;
		try{
			outbox = c.getOutbox();
		}catch(ControlException e){
			c.setStatus("Problem fetching contents of outbox");
			return;
		}
		//gets the file path using a file dialog.
		FileDialog dlg = new FileDialog(Display.getCurrent().getActiveShell(),SWT.SAVE);
		String outputFilePath = dlg.open();
		//writes the contents to the file specified if a file was chosen.
		if (outputFilePath!=null){
			try{
				//gets string representation of output file
				String output = RomeFeedParser.MakeRSSString(outbox,"rss_2.0");
				//writes the file
				File outFile = new File(outputFilePath);
                FileWriter out = new FileWriter(outFile);
                out.write(output);
                out.close();
			}catch(Exception e){
				//if there is a problem writing the file, just give up
				c.setStatus("Problem writing outbox contents to file");
			}
		}
	}
}
