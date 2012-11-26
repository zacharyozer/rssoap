package com.rssoap.actions;

import com.rssoap.dialogs.AboutDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
/**
 * This class is responsible for invoking the about dialog.
 * @author pgroudas
 *
 */
/*
 * This class depends on AboutDialog
 */
public class AboutAction extends Action {
	public AboutAction(){
		super("&About RSS on a Plane");
		setImageDescriptor(ImageDescriptor.createFromFile(AboutAction.class,"/images/rsoap.png"));
	}
	public void run(){
		new AboutDialog(Display.getCurrent().getActiveShell()).open();
	}
}
