package dialogs;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

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

import control.Controller;
 /**
  * A dialog that allows the user to select a user profile for deletion
  * @author pgroudas
  *
  */
public class DeleteUserDialog extends IconAndMessageDialog {
    String user = "Default User";
    private Image image;
    
    /**
     * DeleteUserDialog constructor
     * @param parentShell the parent shell
     */
	public DeleteUserDialog(Shell parentShell) {
        super(parentShell);
        
        try{
        	image =new Image(null, new FileInputStream("images/green/Users.png"));
        }catch(FileNotFoundException e){
        }
        message = "Select which user you would like to delete and click 'OK'";
    }
	/**
	 * Gets the image for this dialog
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
	 * Constructs the dialog area for this dialog
	 * @param parent Composite
	 * @return Control
	 */
    protected Control createDialogArea(Composite parent) {
        createMessageArea(parent);
    	Composite composite = new Composite(parent,SWT.NONE);
        //use a grid layout
    	GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        composite.setLayout(layout);                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
        //make a combo box to hold the usernames
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_BEGINNING;
        data.grabExcessHorizontalSpace = true;
        data.minimumWidth = 200;
        final Combo userCombo = new Combo(composite,SWT.DROP_DOWN);
        userCombo.setLayoutData(data);
        //gets the user names
        ArrayList<String> users = new ArrayList<String>(Controller.getApp().getAllUsers());
        //adds them to the combo
        for (String s: users){
        	if (!s.equals("Default User")) userCombo.add(s);
        }
        user = userCombo.getItem(0);
        userCombo.select(0);
        //add listener to set user value
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
     * Gets the initial size
     * @return Point
     */
    protected Point getInitialSize() {
        return new Point(500, 200);
    }
    /**
     * Returns what user was selected
     * @return String username
     */
    public String getSelectedUser(){
    //System.out.println(user);
    	return user;
    }
}