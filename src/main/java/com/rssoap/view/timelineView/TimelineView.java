package com.rssoap.view.timelineView;

import com.rssoap.control.Controller;
import com.rssoap.model.Article;
import com.rssoap.model.ArticleComparator;
import com.rssoap.model.Feed;
import com.rssoap.model.Folder;
import com.rssoap.view.NoItemSelectedException;
import com.rssoap.view.View;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.util.Collections;
import java.util.List;
/**
 * Timeline View is an implementation of the View interface that allows the user to perform a search, and organizes 
 * as a timeline
 * @author pgroudas
 *
 */
public class TimelineView implements View {
	private Browser browser;
	private Controller control;
	private List<Article> articles;
	private Slider mySlider;
	private Text searchText;
	private Label label;
	public TimelineView(Controller c){
		control = c;
	}
	public Composite getComposite(Composite parent) {
		//creates the composite to eventually return
		Composite composite = new Composite(parent,SWT.NONE);
		//sets its layout
		FormLayout layout = new FormLayout();
		FormData data;
		layout.spacing = 5;
		composite.setLayout(layout);
		//Let's create some controls!
		//search text
		searchText = new Text(composite, SWT.SINGLE|SWT.BORDER);
		data = new FormData();
		data.top = new FormAttachment(0,0);
		data.left = new FormAttachment(50,-125);
		data.width = 150;
		data.height = 24;
		searchText.setLayoutData(data);
		//search button
		final Button searchButton = new Button(composite, SWT.PUSH);
		searchButton.setText("Search");
		try{
			searchButton.setImage(new Image(null, new FileInputStream("images/search.png")));
		}catch(Exception e){
			
		}
		searchText.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) {
				if(e.keyCode==13){
					//if user hits enter, performs search
					processSearch();
				}
			}
			public void keyReleased(KeyEvent e) {
			};
		});
		data = new FormData();
		data.top = new FormAttachment(0,0);
		data.left = new FormAttachment(searchText,0);
		data.width = 100;
		data.height = 30;
		searchButton.setLayoutData(data);
		//slider
		final Slider slider = new Slider(composite, SWT.HORIZONTAL);
		this.mySlider = slider;
		data = new FormData();
		data.left = new FormAttachment(12,0);
		data.right = new FormAttachment(88,0);
		data.top = new FormAttachment(searchText, 5);
		slider.setLayoutData(data);
		//slider is disabled at start since there are no search results to navigate
		slider.setEnabled(false);
		//adds a selection listener so processSearch() is called onclick
		searchButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				processSearch();
			}
		});
		//Adds some data
		control.orderedSearch("");
		//Label
		label = new Label(composite,SWT.CENTER);
		label.setText("Enter text and click 'Search'");
		data = new FormData();
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(slider,5);
		label.setLayoutData(data);

		//browser
		browser = new Browser(composite,SWT.BORDER);
		data = new FormData();
		data.left = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(label,5);
		data.bottom = new FormAttachment(100,0);
		browser.setLayoutData(data);
		//sets homepage
		browser.setUrl("http://www.rssoap.com");
		
		//add listener to open web page when user lets go of the slider
		slider.addMouseListener(new MouseListener(){
			public void mouseDoubleClick(MouseEvent e) {};
			public void mouseDown(MouseEvent e) {};
			public void mouseUp(MouseEvent e) {
				setURL(articles.get(slider.getSelection()).getUrl());
			};
		});
		//adds a listener to adjust the label
		slider.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event event) {
		    	  Article a = articles.get(slider.getSelection());
		    	  label.setText(a.getTitle()+"  "+DateFormat.getDateTimeInstance(
				            DateFormat.MEDIUM, DateFormat.SHORT).format(a.getPubDate()));
//		    	 
//		    	  System.out.println("Selection: "+mySlider.getSelection());
//		    	  System.out.println("Min: "+mySlider.getMinimum());
//		    	  System.out.println("max: "+mySlider.getMaximum());
//		    	  System.out.println("num articles: "+articles.size());
		    	  
		      }
		});
		return composite;
	}
	/**
	 * Ensures that there is text before search is performed, then performs 
	 * search and sets up the slider to behave properly
	 *
	 */
	private void processSearch(){
		String constraint = searchText.getText();
		if (constraint.length()>0){
			articles = control.searchArticles(true, true, true, constraint);
			Collections.sort(articles, new ArticleComparator());
			if (articles.size()>0)
				setupSlider();
			else
				label.setText("No results, try again");
		}
	}
	/**
	 * Sets min and max value of slider so there are the right number of locations 
	 * for each article.  Disables slider if there were no search results
	 *
	 */
	private void setupSlider(){
		int size = articles.size();
/*		for(Article a:articles){
		//System.out.println(a.getTitle());
		}*/
		if (size>=1){
			
			mySlider.setEnabled(true);
			mySlider.setValues(0, 0, size, 1, 1, 5);
			Article a = articles.get(mySlider.getSelection());
	    	 label.setText(a.getTitle()+"  "+DateFormat.getDateTimeInstance(
			            DateFormat.MEDIUM, DateFormat.SHORT).format(a.getPubDate()));
		}
		if (size<1){
			label.setText("No Results");
			//if there were no results, disable slider
			mySlider.setEnabled(false);
		}
	}
	/**
	 * Sets the url of the browser
	 * @param url String url
	 */
	public void setURL(String url) {
		browser.setUrl(url);
	}
	/**
	 * Not implemented for this view
	 */
	public Feed getSelectedFeed() throws NoItemSelectedException {
		throw new NoItemSelectedException();
	}
	/**
	 * Not implemented for this view
	 */
	public Article getSelectedArticle() throws NoItemSelectedException {
		throw new NoItemSelectedException();
	}
	/**
	 * Not implemented for this view
	 */
	public List<Article> getSelectedArticles() throws NoItemSelectedException {
		throw new NoItemSelectedException();
	}
	/**
	 * Not implemented for this view
	 */
	public List<Feed> getSelectedFeeds() throws NoItemSelectedException {
		throw new NoItemSelectedException();
	}
	/**
	 * Not implemented for this view
	 */
	public List<Folder> getSelectedFolders() throws NoItemSelectedException {
		throw new NoItemSelectedException();
	}
	/**
	 * Not implemented for this view
	 */
	public Folder getSelectedFolder() throws NoItemSelectedException {
		throw new NoItemSelectedException();
	}
	/**
	 * Not implemented for this view
	 */
	public Folder getParentOfSelected() throws NoItemSelectedException {
		throw new NoItemSelectedException();
	}
	/**
	 * Not implemented for this view
	 */
	public void updateFolderContents(Folder f) {

	}
	/**
	 * Not implemented for this view
	 */
	public void update() {

	}
	/**
	 * Not implemented for this view
	 */
	public void updateArticles(Feed f) {

	}
	/**
	 * Not implemented for this view
	 */
	public void updateArticle(Article a) {

	}
	/**
	 * Not implemented for this view
	 */
	public void updateFeed(Feed f) {

	}
	/**
	 * Not implemented for this view
	 */
	public void updateOutbox() {

	}
	/**
	 * Not implemented for this view
	 */
	public void updateTrash() {

	}
}
