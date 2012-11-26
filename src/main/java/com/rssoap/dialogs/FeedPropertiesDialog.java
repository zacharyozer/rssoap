package com.rssoap.dialogs;

import com.rssoap.model.Feed;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
/**
 * Dialog to display and edit feed properties
 * @author pgroudas
 *
 */
public class FeedPropertiesDialog extends TitleAreaDialog {
    Feed feed;
    String updateInterval;
    String title;
    private Image image;
    
    /**
     * FeedPropertiesDialog constructor
     * @param parentShell the parent shell
     */
    
	public FeedPropertiesDialog(Shell parentShell,Feed f) {
        super(parentShell);
        this.feed = f;
        updateInterval = Integer.toString(feed.getUpdateInterval());
        try{
        	image =new Image(null, new FileInputStream("images/properties.png"));
        }catch(FileNotFoundException e){
        	image = null;
        }
    }
	/**
	 * Sets up particular attributes of the dialog window, such as title image, message, etc.
	 * @param parent Composite
	 * @return Control
	 */
	protected Control createContents(Composite parent){
		Control contents = super.createContents(parent);
		setMessage("Change the update interval to change how frequently this feed is updated");
		setTitle("Feed Properties");
		setTitleImage(image);
		return contents;
	}
	/**
	 * Sets up the dialog area
	 * @param parent Composite
	 * @return Control
	 */
	protected Control createDialogArea(Composite parent){
		Composite composite = (Composite) super.createDialogArea(parent);
		Composite control = new Composite(composite,SWT.NONE);
		//gridlayout with 3 equally wide columns
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		layout.verticalSpacing = 20;
		control.setLayout(layout);
		GridData data;
		//create the title label
		Label titleLbl = new Label(control,SWT.NONE);
		titleLbl.setText("Title: ");
		data = new GridData(SWT.BEGINNING);
		data.horizontalSpan = 1;
		titleLbl.setLayoutData(data);
		//create the title text field
		final Text titleText = new Text(control, SWT.SINGLE|SWT.BORDER);
		titleText.setText(feed.getTitle());
		data = new GridData(SWT.BEGINNING);
		data.horizontalSpan = 2;
		data.grabExcessHorizontalSpace = true;
		data.widthHint = 250;
		titleText.setLayoutData(data);
		//create the url label
		Label urlLbl = new Label(control, SWT.NONE);
		urlLbl.setText("URL: "+feed.getUrl());
		data = new GridData(SWT.BEGINNING);
		data.grabExcessHorizontalSpace = true;
		data.horizontalSpan = 3;
		urlLbl.setLayoutData(data);
		//create the update interval label
		Label updateLabel = new Label(control,SWT.NONE);
		updateLabel.setText("Update Interval: ");
		data = new GridData(SWT.BEGINNING);
		data.horizontalSpan = 1;
		updateLabel.setLayoutData(data);
		//update interval text
		final Text intervalText = new Text(control, SWT.SINGLE|SWT.BORDER);
		intervalText.setText(Integer.toString(feed.getUpdateInterval()));
		data = new GridData(SWT.BEGINNING);
		data.horizontalSpan = 2;
		data.grabExcessHorizontalSpace = true;
		data.widthHint = 250;
		intervalText.setLayoutData(data);
		//adds a listener to set the title field 
		titleText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				title = titleText.getText();
			}
		});
		//adds a listener to set the update interval field
		intervalText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				updateInterval = intervalText.getText();
			}
		});
		return control;
	}
	public String getUpdateInterval() {
		return updateInterval;
	}
	public String getTitle(){
		return title;
	}
}