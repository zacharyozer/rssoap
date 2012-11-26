package com.rssoap.actions;

import com.rssoap.control.ControlException;
import com.rssoap.control.Controller;
import com.rssoap.model.Feed;
import com.rssoap.parser.RomeFeedParser;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
/**
 * This class is responsible for exporting a user's list of feeds as an opml file.
 * 
 * @author pgroudas
 *
 */
public class ExportFeedListAction extends Action {
	/**
	 * Constructs a new ExportFeedListAction
	 *
	 */
	public ExportFeedListAction(){
		super("Export feeds to OPML");
		setToolTipText("Exports your list of feeds as an OPML file");
		setImageDescriptor(ImageDescriptor.createFromFile(ExportFeedListAction.class, "/images/export.png"));
	}
	/**
	 * Invoked to export the user's feeds as an OPML file.
	 */
	public void run(){
		//gets the controller
		Controller c = Controller.getApp();
		try{
			List<Feed> allFeeds = c.getAllFeeds();
			List<Feed> feeds = new ArrayList<Feed>();
			for (Feed f:allFeeds){
				if (!f.getTitle().equals("outbox")&&!f.getTitle().equals("trash"));
				feeds.add(f);
			}
			String opml = RomeFeedParser.MakeOPMLString(feeds);
			FileDialog dlg = new FileDialog(c.getShell(),SWT.SAVE);
			String outputFilePath = dlg.open();
			//writes the contents to the file specified if a file was chosen.
			if (outputFilePath!=null){
				try{
					//gets string representation of output file
					//writes the file
					File outFile = new File(outputFilePath);
	                FileWriter out = new FileWriter(outFile);
	                out.write(opml);
	                out.close();
				}catch(Exception e){
					//if there is a problem writing the file, just give up
					c.setStatus("Problem writing outbox contents to file");
				}
			}
		}catch(ControlException e){
			
		}
	}
}
