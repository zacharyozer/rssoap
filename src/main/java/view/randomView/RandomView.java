package view.randomView;

import java.util.List;

import model.Article;
import model.Feed;
import model.Folder;
import model.Util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import view.NoItemSelectedException;
import view.View;
import control.ControlException;
import control.Controller;
/**
 * Random View is a simple view that lets the user view a random article that is 
 * in one of the feeds that they've subscribed to.
 * 
 * @author pgroudas
 *
 */
public class RandomView implements View {
	private Controller control;
	private Browser browser;
	/**
	 * Constructs a new randomView.
	 * @param c
	 */
	public RandomView (Controller c){
		control = c;
	}
	/**
	 * Generates and returns the composite for this view.
	 * 
	 * @return Composite
	 */
	public Composite getComposite(Composite parent) {
		Composite composite = new Composite(parent,SWT.NONE);
		composite.setLayout(new FormLayout());
		FormData data;
		//creates browser
		browser = new Browser(composite,SWT.BORDER);
		data = new FormData();
		data.bottom = new FormAttachment(100,0);
		data.top = new FormAttachment(20,0);
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		browser.setLayoutData(data);
		//creates button
		Button button = new Button(composite, SWT.PUSH|SWT.CENTER);
		button.setText("Get Random Article");
		button.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				try{
					//gets random article when clicked, and opens url in browser
					Article randomArticle = control.getArticleRandom();
					browser.setText(Util.generateHTML(randomArticle));
				//System.out.println("Button clicked");
				}catch(ControlException ex){
				}
			};
		});
		
		data = new FormData();
		data.height = 25;
		data.width = 200;
		data.left = new FormAttachment(50,-100);
		data.top = new FormAttachment(10,0);
		button.setLayoutData(data);
		return composite;
	}

	/**
	 * Sets the browser's url to the specified url
	 * @param url String url
	 */
	public void setURL(String url){
		browser.setUrl(url);
	}
	/**
	 * Not implemented in this view
	 */
	public Feed getSelectedFeed() throws NoItemSelectedException {
		throw new NoItemSelectedException();
	}
	/**
	 * Not implemented in this view
	 */
	public Article getSelectedArticle() throws NoItemSelectedException {
		throw new NoItemSelectedException();
	}
	/**
	 * Not implemented in this view
	 */
	public Folder getSelectedFolder() throws NoItemSelectedException {
		throw new NoItemSelectedException();
	}
	/**
	 * Not implemented in this view
	 */
	public void update() {
	}
	/**
	 * Not implemented in this view
	 */
	public void updateArticles(Feed f) {
	}
	/**
	 * Not implemented in this view
	 */
	public void updateFolderContents(Folder f) {
	}
	/**
	 * Not implemented in this view
	 */
	public Folder getParentOfSelected() throws NoItemSelectedException{
		throw new NoItemSelectedException();
	}
	/**
	 * Not implemented in this view
	 */
	public void updateArticle(Article a){
		throw new NotImplementedException();
	}
	/**
	 * Not implemented in this view
	 */
	public void updateFeed(Feed f){
		throw new NotImplementedException();
	}
	/**
	 * Not implemented in this view
	 */
	public void updateFolder(Folder f){
		throw new NotImplementedException();
	}
	/**
	 * Not implemented in this view
	 */
	public void updateOutbox(){
	}
	/**
	 * Not implemented in this view
	 */
	public void updateTrash(){
	}
	/**
	 * Not implemented in this view
	 */
	public List<Article> getSelectedArticles() throws NoItemSelectedException{
		throw new NoItemSelectedException();
	}
	/**
	 * Not implemented in this view
	 */
	public List<Feed> getSelectedFeeds() throws NoItemSelectedException{
		throw new NoItemSelectedException();
	}
	/**
	 * Not implemented in this view
	 */
	public List<Folder> getSelectedFolders() throws NoItemSelectedException{
		throw new NoItemSelectedException();
	}
}
