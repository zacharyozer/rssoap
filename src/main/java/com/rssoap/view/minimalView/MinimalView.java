package com.rssoap.view.minimalView;

import com.rssoap.control.Controller;
import com.rssoap.model.Article;
import com.rssoap.model.Feed;
import com.rssoap.model.Folder;
import com.rssoap.view.NoItemSelectedException;
import com.rssoap.view.View;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class MinimalView implements View{
	/*private TableViewer tableViewer;
	private Text text;
	private TreeViewer treeViewer;*/
	private Controller control;
	public MinimalView (Controller c){
		control = c;
	}
	
	public Composite getComposite(Composite parent){
		Composite composite = new Composite(parent,SWT.NONE);
		/*TableLayout layout = new TableLayout();*/
		Text t = new Text(composite, 0);
		t.setText("This is a slightly less minimal view.");
		t.setBounds(0, 0, 500, 500);
		return composite;
		}
	
	public void update(){
	}

	public void updateArticles(Feed f){
	}
	public Feed getSelectedFeed() throws NoItemSelectedException{
		throw new NoItemSelectedException();
	}
	public Article getSelectedArticle() throws NoItemSelectedException{
		throw new NoItemSelectedException();
	}
	public Folder getSelectedFolder() throws NoItemSelectedException{
		throw new NoItemSelectedException();
	}
	
	public void updateFolderContents(Folder f) {
	}
	public Folder getParentOfSelected() throws NoItemSelectedException{
		throw new NoItemSelectedException();
	}
	public void updateArticle(Article a){
		throw new NotImplementedException();
	}
	public void updateFeed(Feed f){
		throw new NotImplementedException();
	}
	public void updateFolder(Folder f){
		throw new NotImplementedException();
	}
	public void setURL(String url){
		
	}
	public void updateOutbox(){
		
	}
	public void updateTrash(){
		
	}
	public List<Article> getSelectedArticles() throws NoItemSelectedException{
		throw new NoItemSelectedException();
	}
	public List<Feed> getSelectedFeeds() throws NoItemSelectedException{
		throw new NoItemSelectedException();
	}
	public List<Folder> getSelectedFolders() throws NoItemSelectedException{
		throw new NoItemSelectedException();
	}
}
