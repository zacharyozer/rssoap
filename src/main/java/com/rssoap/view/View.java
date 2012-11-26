package com.rssoap.view;

import com.rssoap.model.Article;
import com.rssoap.model.Feed;
import com.rssoap.model.Folder;
import org.eclipse.swt.widgets.Composite;

import java.util.List;


/**
 * The view interface specifies what methods a view must implement to properly interact
 *  with a controller.
 * @author pgroudas
 *
 */
public interface View {
	
	/**
	 * getComposite returns a Composite instance that is displayed by the controller.
	 * @param parent Composite 
	 * @return Composite 
	 */
	public Composite getComposite(Composite parent);
	/**
	 * gets the currently selected feed in the view.
	 * @return Feed selected feed.
	 * @throws NoItemSelectedException If no feed is currently selected.
	 */
	public Feed getSelectedFeed() throws NoItemSelectedException;
	/**
	 * gets the currently selected article in the view.
	 * @return Article selected article.
	 * @throws NoItemSelectedException If no article is currently selected.
	 */
	public Article getSelectedArticle() throws NoItemSelectedException;
	/**
	 * Gets the selected articles
	 * @return List of Articles selected articles
	 * @throws NoItemSelectedException if no articles are selected
	 */
	public List<Article> getSelectedArticles() throws NoItemSelectedException;
	/**
	 * Gets the selected feeds
	 * @return List of feeds selected feeds
	 * @throws NoItemSelectedException if no Feeds are selected
	 */
	public List<Feed> getSelectedFeeds() throws NoItemSelectedException;
	/**
	 * Gets the selected folders
	 * @return List of Folders selected folders
	 * @throws NoItemSelectedException
	 */
	public List<Folder> getSelectedFolders() throws NoItemSelectedException;
	
	/**
	 * gets the currently selected folder in the view.
	 * @return Folder selected folder.
	 * @throws NoItemSelectedException If no folder is currently selected.
	 */
	public Folder getSelectedFolder() throws NoItemSelectedException;
	/**
	 * gets the parent folder of the selected item
	 * @return
	 * @throws NoItemSelectedException
	 */
	public Folder getParentOfSelected() throws NoItemSelectedException;
	/**
	 * updates the specified folder's contents
	 * @param f Folder whose contents will be updated
	 */
	public void updateFolderContents(Folder f);
	/**
	 * The update method is called when the Controller wants the view to refresh.
	 *
	 */
	public void update();
	/**
	 * Updates the articles of the specified feed.
	 * @param f
	 */
	public void updateArticles(Feed f);
	/**
	 * updates the view of a particular article
	 * @param a
	 */
	public void updateArticle(Article a);
	/**
	 * updates the view of a particular feed
	 * @param f
	 */
	public void updateFeed(Feed f);
	/**
	 * updates the view of the outbox
	 *
	 */
	public void updateOutbox();
	/**
	 * updates the view of the trash
	 *
	 */
	public void updateTrash();
	/**
	 * Sets the url of a view's browser, if it has one.
	 * @param url
	 */
	public void setURL(String url);
}
