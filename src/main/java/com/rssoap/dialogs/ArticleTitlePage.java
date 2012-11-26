package com.rssoap.dialogs;

import com.rssoap.actions.NewArticleAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
/**
 * First page of the create new article wizard.
 * @author pgroudas
 *
 */
public class ArticleTitlePage extends WizardPage {
	private Text titleText,authorText;
	private String title = "";
	private String author = "";
	/**
	 * Constructs a new ArticleTitlePage
	 *
	 */
	public ArticleTitlePage(){
		super("Create a New Article", "Create a New Article",ImageDescriptor.createFromFile(NewArticleAction.class,"/images/newarticle.png"));
		setDescription("Enter the title and author of the new article");
		setPageComplete(false);
	}
	/**
	 * Gets the String that the user input for the title.
	 * @return String
	 */
	public String getTitle(){
		return title;
	}
	/**
	 * Gets the String that the user input for the author.
	 * @return String
	 */
	public String getAuthor(){
		return author;
	}
	/**
	 * Creates the control for this page of the wizard.
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FormLayout());
		
		//creates a label for the title field
		CLabel titleLabel = new CLabel(composite,SWT.LEFT);
		titleLabel.setText("Title: ");
		FormData data = new FormData();
		data.left = new FormAttachment(0,5);
		data.right = new FormAttachment(33,0);
		data.top = new FormAttachment(0,20);
		titleLabel.setLayoutData(data);
		//creates the title input field
		titleText = new Text(composite,SWT.BORDER|SWT.SINGLE);
		data = new FormData();
		data.left = new FormAttachment(titleLabel,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(0,20);
		titleText.setLayoutData(data);
		//adds a modify listener to know when its valid to go to the next page
		titleText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				title = titleText.getText();
				setPageComplete(title.length() > 0 && author.length() > 0);
			}
		});
		//creates a label for the author field
		CLabel authorLabel = new CLabel(composite,SWT.LEFT);
		authorLabel.setText("Author: ");
		data = new FormData();
		data.top = new FormAttachment(titleLabel, 10);
		data.left = new FormAttachment(0,5);
		data.right = new FormAttachment(33,0);
		authorLabel.setLayoutData(data);
		//creates an author input field
		authorText = new Text(composite,SWT.BORDER|SWT.SINGLE);
		data = new FormData();
		data.top = new FormAttachment(titleText,10);
		data.left = new FormAttachment(authorLabel,0);
		data.right = new FormAttachment(100,0);
		authorText.setLayoutData(data);
		//adds a listener to enable the ability to go to the next page
		authorText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				author = authorText.getText();
				setPageComplete(title.length() > 0 && author.length() > 0);
			}
		});
		
		setControl(composite);
	}

}
