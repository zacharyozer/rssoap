package com.rssoap.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.FileInputStream;
/**
 * About Dialog class shows an about dialog.
 * @author zozer
 *
 */
public class AboutDialog extends TitleAreaDialog {
	/**
	 * Constructs a new AboutDialog
	 * @param parent Shell
	 */
	public AboutDialog(Shell parent){
		super(parent);
	}
	/**
	 * Creates the control for this dialog
	 * @param parent Composite
	 * @return Control
	 */
	protected Control createContents(Composite parent){
		Control contents = super.createContents(parent);
		setMessage("We got motherfuckin snakes!");
		setTitle("About RSS on a plane");
		try{
			setTitleImage(new Image(null, new FileInputStream("images/rsoapsmall.png")));
		}catch(Exception e){
		}
		return contents;
	}
	/**
	 * Constructs the dialog portion of the dialog
	 * @param parent Composite
	 * @return Control
	 */
	protected Control createDialogArea(Composite parent){
		Composite composite = (Composite) super.createDialogArea(parent);
		//uses a gridLayout because dialogs can't use a form layout for some reason
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		composite.setLayout(layout);
		GridData data;
		//label for the image
		CLabel pic = new CLabel(composite, SWT.CENTER);
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		try{
			pic.setImage(new Image(null, new FileInputStream("images/rss.png")));
		}catch(Exception e){
		}
		pic.setLayoutData(data);
		//create some fonts
		Font titleFont = new Font(Display.getCurrent(), "", 12, 1);
		Font versionFont = new Font(Display.getCurrent(), "", 8, 1);
		Font quoteFont = new Font(Display.getCurrent(), "", 10, 2);
		Font normalFont = new Font(Display.getCurrent(), "", 10, 0);
		//create a label
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		Label info = new Label(composite, SWT.WRAP);
		info.setText("RSS on a Plane");
		info.setFont(titleFont);
		info.setLayoutData(data);
		//create another label
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		Label info2 = new Label(composite, SWT.WRAP);
		info2.setText("Version 1.70");
		info2.setFont(versionFont);
		info2.setLayoutData(data);
		//and yet a third
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		Label info3 = new Label(composite, SWT.WRAP);
		info3.setText("\"I have had it with these motherfucking snakes on this motherfucking plane\"");
		info3.setFont(quoteFont);
		info3.setLayoutData(data);
		//but we keep going..
		data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		Label info4 = new Label(composite, SWT.WRAP);
		info4.setBounds(0, 0, 100, 100);
		info4.setText("\n(c) 2006 Paul Groudas, Zachary Ozer, \nand Michael McGraw-Herdeg. All rights \nreserved. Rome and Derby used under \nlicense from the Apache Foundation. \nIcons modified under license from \nGNOME.");
		info4.setFont(normalFont);
		info4.setLayoutData(data);
		//done!
		return composite;
	}
	/**
	 * Creates the buttons 
	 * @param parent Composite
	 */
	protected void createButtonsForButtonBar(Composite parent){
		createButton(parent, IDialogConstants.OK_ID,IDialogConstants.OK_LABEL,true);
	}
}
