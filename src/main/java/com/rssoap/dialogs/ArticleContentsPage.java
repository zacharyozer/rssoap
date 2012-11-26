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
 * Second and last page of the create new article wizard.
 * @author pgroudas
 *
 */
public class ArticleContentsPage extends WizardPage {

	private Text urlText, summaryText;
	private String url = "";
	private String summary = "";
	/**
	 * Constructs a new ArticleContentsPage
	 *
	 */
	public ArticleContentsPage() {
		super("Create a New Article", "Create a New Article",ImageDescriptor.createFromFile(NewArticleAction.class,"/images/newarticle.png"));
		setDescription("Enter the url and summary of the new article");
		setPageComplete(false);
	}
	/**
	 * Gets the String that the user input for the url.
	 * @return String
	 */
	public String getUrl(){
		return url;
	}
	/**
	 * Gets the String that the user input for the summary.
	 * @return String
	 */
	public String getSummary(){
		return summary;
	}
	/**
	 * Creates the control for this page of the wizard.
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FormLayout());
		//creates a label for the url field
		CLabel urlLabel = new CLabel(composite,SWT.LEFT);
		urlLabel.setText("URL: ");
		FormData data = new FormData();
		data.left = new FormAttachment(0,5);
		data.right = new FormAttachment(33,0);
		data.top = new FormAttachment(0,20);
		urlLabel.setLayoutData(data);
		//creates an input field to enter the url
		urlText = new Text(composite,SWT.BORDER|SWT.SINGLE);
		urlText.setText("http://");
		data = new FormData();
		data.left = new FormAttachment(urlLabel,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(0,20);
		urlText.setLayoutData(data);
		//adds a modify listener to know when its valid to go to the next page
		urlText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				url = urlText.getText();
				setPageComplete(summary.length() > 0 && url.length() > 0);
			}
		});
		//adds a label for the summary input field
		CLabel summaryLabel = new CLabel(composite,SWT.LEFT);
		summaryLabel.setText("Summary: ");
		data = new FormData();
		data.top = new FormAttachment(urlLabel, 10);
		data.left = new FormAttachment(0,5);
		data.right = new FormAttachment(100,0);
		summaryLabel.setLayoutData(data);
		//adds an input field to add the summary
		summaryText = new Text(composite,SWT.BORDER|SWT.MULTI|SWT.V_SCROLL);
		data = new FormData();
		data.top = new FormAttachment(summaryLabel,10);
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.bottom = new FormAttachment(100,-5);
		summaryText.setLayoutData(data);
		//adds a modify listener to know when its valid to go to the next page
		summaryText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				summary = summaryText.getText();
				setPageComplete(url.length() > 0 && summary.length() > 0);
			}
		});
		
		setControl(composite);
	}
}
