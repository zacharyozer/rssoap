package com.rssoap.dialogs;

import com.rssoap.control.Controller;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
/**
 * Dialog to allow the user to switch the active user profile
 * @author pgroudas
 *
 */
public class SwitchUserDialog extends IconAndMessageDialog {
    String user = "Default User";
    private Image image;
    /**
     * SwitchUserDialog constructor
     * @param parentShell the parent shell
     */
	public SwitchUserDialog(Shell parentShell) {
        super(parentShell);
        try{
        	image =new Image(null, new FileInputStream("images/green/Users.png"));
        }catch(FileNotFoundException e){
        }
        message = "Select which user you would like to switch to and click 'OK'";
    }
	/**
	 * gets the Image for this dialog
	 * @return Image
	 */
	@Override
	protected Image getImage() {
		return image;
	}
	/**
	 * Closes the dialog box.
	 */
	public boolean close(){
		if (image != null) image.dispose();
		return super.close();
	}
    /**
     * Creates the dialog area for this dialog
     * @param Composite
     * @return Control
     */
	protected Control createDialogArea(Composite parent) {
        createMessageArea(parent);
    	Composite composite = new Composite(parent,SWT.NONE);
        //use a gridlayout
    	GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        composite.setLayout(layout);                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
        data.grabExcessHorizontalSpace = true;
        data.minimumWidth = 200;
        //create a combobox
        final Combo userCombo = new Combo(composite,SWT.DROP_DOWN);
        userCombo.setLayoutData(data);
        //get a list of users and adds them to the combo as long as they aren't "Default User"
        ArrayList<String> users = new ArrayList<String>(Controller.getApp().getAllUsers());
        for (String s: users){
        	if (!s.equals("Default User")) userCombo.add(s);
        }
        user = userCombo.getItem(0);
        userCombo.select(0);
        //adds listener to set the user field
        userCombo.addSelectionListener(new SelectionListener(){
        	public void widgetDefaultSelected(SelectionEvent e) {
        		user = userCombo.getItem(userCombo.getSelectionIndex());
        	};
        	public void widgetSelected(SelectionEvent e) {
        		user = userCombo.getItem(userCombo.getSelectionIndex());
        	};
        });
        return composite;
    }
	/**
	 * Creates the buttons
	 * @param parent Composite
	 */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID,
            IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
            IDialogConstants.CANCEL_LABEL, false);
    }
    /**
     * gets the Initial size
     * @return Point
     */
    protected Point getInitialSize() {
        return new Point(500, 200);
    }
    /**
     * Returns the selected user
     * @return String username
     */
    public String getSelectedUser(){
    	return user;
    }
}