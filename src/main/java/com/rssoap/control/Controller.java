package com.rssoap.control;

import com.rssoap.actions.*;
import com.rssoap.derby.DBHandler;
import com.rssoap.derby.DBHandlerException;
import com.rssoap.model.Article;
import com.rssoap.model.Feed;
import com.rssoap.model.Folder;
import com.rssoap.parser.RomeFeedParser;
import com.rssoap.view.View;
import com.rssoap.view.fightView.FightView;
import com.rssoap.view.mailBoxView.MailBoxView;
import com.rssoap.view.newsPaperView.NewsPaperView;
import com.rssoap.view.randomView.RandomView;
import com.rssoap.view.timelineView.TimelineView;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * The Controller class is responsible for manipulating the underlying model. 
 * User interaction of the database is delegated to public methods of the Controller. 
 * 
 * @author pgroudas
 *
 */
public class Controller extends ApplicationWindow {
	//the Currently running instance of controller
	private static Controller APP;
	//DBHandler...this is what does all the work
	private DBHandler dbh;
	//currently active view
	private View activeView;
	//all the views available
	private List<View> allViews;
	//Actions for menu, toolbar, and such
	private List<ChangeViewAction> changeViewActions;
	private Composite mostRecent;
	private SubscribeAction subscribeAction;
	private UnSubscribeFeedAction unSubscribeAction;
	private ExitAction exitAction;
	private UpdateFeedAction updateFeedAction;
	private UpdateAllFeeds updateAllFeeds;
	private AddToOutbox addToOutbox;
	private TrashAction trashAction;
	private DeleteFolderAction deleteFolderAction;
	private EmptyTrashAction emptyTrashAction;
	private ImportFromFileAction importFromFileAction;
	private ImportFromURLAction importFromURLAction;
	private NewFolderAction newFolderAction;
	private RefreshAction refreshAction;
	private ExportOutboxAction exportOutboxAction;
	private NewArticleAction newArticleAction;
	private NewUserAction newUserAction;
	private SwitchUserAction switchUserAction;
	private DeleteUserAction deleteUserAction;
	private AboutAction aboutAction;
	private ResetAction resetAction;
	private ExportFeedListAction exportFeedListAction;
	/**
	 * Constructs a new Controller with the specified DBHandler.
	 * 
	 * @param dbh DBHandler
	 */
	public Controller(DBHandler dbh){
		super(null);
		//sets internal fields
		APP = this;
		this.dbh = dbh;
		subscribeAction = new SubscribeAction();
		unSubscribeAction = new UnSubscribeFeedAction();
		exitAction = new ExitAction();
		updateFeedAction = new UpdateFeedAction();
		updateAllFeeds = new UpdateAllFeeds();
		addToOutbox = new AddToOutbox();
		trashAction = new TrashAction();
		deleteFolderAction = new DeleteFolderAction();
		emptyTrashAction = new EmptyTrashAction();
		importFromFileAction = new ImportFromFileAction();
		importFromURLAction = new ImportFromURLAction();
		newFolderAction = new NewFolderAction();
		refreshAction = new RefreshAction();
		exportOutboxAction = new ExportOutboxAction();
		newArticleAction = new NewArticleAction();
		newUserAction = new NewUserAction();
		switchUserAction = new SwitchUserAction();
		deleteUserAction = new DeleteUserAction();
		aboutAction = new AboutAction();
		resetAction = new ResetAction();
		exportFeedListAction = new ExportFeedListAction();
		//sets up the views
		setupViews();
		//calls createMenuManager() to construct Menu
		addMenuBar();
		//calls createToolBarManager() to construct Toolbar
		addToolBar(SWT.SHADOW_OUT);
		//calls createStatusLineManager() to construct the status line
		addStatusLine();
		//createContents is also implicitly to construct contents
	}
	/**
	 * Sets up the views.  If a new view is to be added, this is where it would go.
	 *
	 */
	private void setupViews(){
		/*
		 * THIS IS WHAT TO EDIT IF YOU WANT TO ADD A VIEW.
		 * 
		 * To add a new view, just instantiate one, add it to the collection of 
		 * views (allViews), and then create a new ChangeViewAction and add that action
		 * to the collection of changeViewActions.  Note that if no views are created and added
		 * in this method the software will fail to run when it tries to set the active view.
		 */
		
		
		//sets up collections of Views, and collections of actions that switch to those views
		allViews = new ArrayList<View>();
		changeViewActions = new ArrayList<ChangeViewAction>();
		
		//creating the Views
		View mailView = new MailBoxView(this);
		View newsPaperView = new NewsPaperView(this);
		View timelineView = new TimelineView(this);
		View fightView = new FightView(this);
		View randomView = new RandomView(this);
		//adding the views
		allViews.add(mailView);
		allViews.add(newsPaperView);
		allViews.add(timelineView);
		allViews.add(fightView);
		allViews.add(randomView);
		//creating and adding the actions
		changeViewActions.add(new ChangeViewAction(mailView,"Mail View"));
		changeViewActions.add(new ChangeViewAction(newsPaperView, "Newspaper View"));
		changeViewActions.add(new ChangeViewAction(timelineView, "Timeline View"));
		changeViewActions.add(new ChangeViewAction(fightView, "Fight View"));
		changeViewActions.add(new ChangeViewAction(randomView,"Random View"));
		//sets the active view according to what the database says the user last used
		try{
			activeView = allViews.get(dbh.getCurrentUserView());
		}catch(DBHandlerException e){
			//if there is a problem, then use the default view
			activeView = allViews.get(0);
		}
	}
	/**
	 * Constructs the Menu for the Controller.
	 * 
	 * @return MenuManager 
	 */
	protected MenuManager createMenuManager(){
		/*
		 * This method is very straightforward, it just takes all the actions that are stored 
		 * as local variable and adds them to the menumanager.  If the Menu layout is to be changed, 
		 * this is where to do it.
		 */
		//creates menuManager
		MenuManager menuManager = new MenuManager();
		
		//creates file menu
		MenuManager fileMenu = new MenuManager("&File");
		menuManager.add(fileMenu);

		//creates import sub menu as the first item under file
		MenuManager importSubMenu = new MenuManager("Import");
		importSubMenu.add(importFromFileAction);
		importSubMenu.add(importFromURLAction);
		fileMenu.add(importSubMenu);
		//creates the export sub menu
		MenuManager exportSubMenu = new MenuManager("Export");
		exportSubMenu.add(exportOutboxAction);
		exportSubMenu.add(exportFeedListAction);
		fileMenu.add(exportSubMenu);
		fileMenu.add(new Separator());
		fileMenu.add(resetAction);
		fileMenu.add(new Separator());
		fileMenu.add(exitAction); 
		
		//creates the feed menu
		MenuManager feedMenu = new MenuManager("F&eed");
		menuManager.add(feedMenu);
		feedMenu.add(subscribeAction);
		feedMenu.add(unSubscribeAction);
		feedMenu.add(updateFeedAction);
		feedMenu.add(updateAllFeeds);
		
		//creates the view menu
		MenuManager viewMenu = new MenuManager("&View");
		menuManager.add(viewMenu);
		for (ChangeViewAction a:changeViewActions){
			viewMenu.add(a);
		}
		
		//creates the user menu
		MenuManager userMenu = new MenuManager("&User");
		menuManager.add(userMenu);
		userMenu.add(newUserAction);
		userMenu.add(switchUserAction);
		userMenu.add(deleteUserAction);
		
		//creates the help menu
		MenuManager helpMenu = new MenuManager("&Help");
		menuManager.add(helpMenu);
		helpMenu.add(aboutAction);
		
		return menuManager;
	}
	/**
	 * Constructs the toolbar for the Controller.
	 * 
	 * @return ToolBarManager
	 */
	protected ToolBarManager createToolBarManager(int style){
		//create the manager 
		ToolBarManager manager = new ToolBarManager(style);
		//addActions
		manager.add(subscribeAction);
		manager.add(unSubscribeAction);
		manager.add(new Separator());
		manager.add(updateFeedAction);
		manager.add(updateAllFeeds);
		manager.add(refreshAction);
		manager.add(new Separator());
		manager.add(newFolderAction);
		manager.add(deleteFolderAction);
		manager.add(new Separator());
		manager.add(newArticleAction);
		manager.add(addToOutbox);
		manager.add(trashAction);
		manager.add(new Separator());
		manager.add(emptyTrashAction);
		return manager;
	}
	/**
	 * Constructs the StatusLineManager for the Controller.  Invoked by the 
	 * 'addStatusLine()' method call during construction.
	 * 
	 * @return StatusLineManager
	 */
	
	protected StatusLineManager createStatusLineManager(){
	    StatusLineManager slm = new StatusLineManager();
	    slm.setCancelEnabled(true);
	    return slm;
	}
	/**
	 * Sets the active view to the specified view.  This is invoked by ChangeViewActions to switch to a 
	 * particular view
	 * 
	 * @param newView View to be displayed
	 */
	public void setActiveView(View newView){
		//sets the active view
		activeView = newView;
		//disposes the current view composite
		mostRecent.dispose();
		//creates the new view composite
		createContents(this.getShell());
		//calls layout() to refresh the view
		mostRecent.getParent().layout();
	}
	/**
	 * Returns the currently active View.
	 * 
	 * @return View that is currently displayed.
	 */
	public View getActiveView(){
		return activeView;
	}
	/**
	 * Returns the currently running Instance of Controller.
	 * 
	 * @return The displayed instance of Controller.
	 */
	public static Controller getApp(){
		return APP;
	}
	/**
	 * Creates the contents for the Controller, based on the active view.
	 * 
	 * @return Control
	 * 
	 */
	protected Control createContents(Composite parent) {
		Composite composite = activeView.getComposite(parent);
		mostRecent = composite;
		return composite;
	}
	/**
	 * Configures the shell of the Controller object.
	 */
	protected void configureShell(Shell shell){
		super.configureShell(shell);
		shell.setText("RSS on a plane");
		//Sets the size of the window
		shell.setSize(1000,750);
		try{
			shell.setImage(new Image(null, new FileInputStream("images/rss.png")));
		}catch(Exception e){
			//oh well, no cool image for us
		}
	}
	/**
	 * Runs the Application
	 */
	public void run() {
	    // Don't return from open() until window closes
	    setBlockOnOpen(true);
	    // Open the main window
	    open();
	    // Dispose the display
	    Display.getCurrent().dispose();
	}
	/**
	 * Performs maintenance tasks and closes application.
	 * @return boolean
	 */
	public boolean close(){
		//cancel's any processes that are reporting to the status bar
		getStatusLine().getProgressMonitor().setCanceled(true);
		try{
			//records what view the user is using
			int currentView = allViews.indexOf(activeView);
			dbh.setCurrentUserView(currentView);
			//tells the DBHandler to shutdown
			dbh.shutdown();
		}catch(DBHandlerException e){
			//database hasn't shut down properly....toobad
			MessageDialog.openError(Display.getCurrent().getActiveShell(),"Error",e.toString());
		}
		//closes
		return super.close();
	}
	/**
	 * The Application Entry Point
	 * 
	 * @param args Command Line Arguments
	 */
	public static void main(String[] args) {
		try{
			//Constructs a new Controller
			DBHandler dbh = new DBHandler();
			Controller controller = new Controller(dbh);
			//starts a feedchecker thread to start periodically checking 
			//what feeds needs to get updated.  This thread initially waits 10 seconds
			new FeedChecker().start();
			//starts a thread that will wait 3.5 seconds for the application to open and then
			//check to make the user status.  If there is no user then it prompts for the user.
			new Thread(){
				public void run() {
					try{
						while(Controller.getApp().getShell()==null){
							Thread.sleep(10);
						}
						Controller.getApp().checkUser();
					}
					catch(InterruptedException e){
						Controller.getApp().checkUser();
					}
				};
			}.start();
			//starts the application
			controller.run();
		}catch(Exception e){
			//Something very Bad happened
			e.printStackTrace();
		}
	}
	/*
	 * METHODS TO DISPATCH TO DBHANDLER
	 * 
	 * In general, these methods merely dispatch to the appropriate method calls 
	 * of the internal DBHandler instance.  If there is a problem, these methods catch the 
	 * exception and then throw a new one with some additional information.
	 */
	/**
	 * Updates all feeds that are scheduled to be updated based on their update interval.
	 *
	 */
	public void updateFeeds(){
		try{
			//gets feeds to update
			List<Feed> feeds = dbh.getFeedstoUpdate();
			for (Feed f:feeds){
				updateFeed(f);
				//updates them
			}
		}catch(DBHandlerException e){
			//don't indicate anything, since this runs in the background
		}catch(ControlException e){
			//don't indicate anything, since this runs in the background
		}
	}
	/**
	 * Sets a feeds update interval.  i must be between 1 and 1440 (the number of minutes in the day).  
	 * If not, this method throws and exception.   
	 * @param f Feed who's interval is to be changed
	 * @param i Integer representing the interval of minutes between updates
	 * @throws ControlException If there is an internal error or if the interval passed in is invalid.
	 */
	public void setFeedUpdateInterval(Feed f,int i)throws ControlException{
		try{
			//checks if interval is valid before we try to set it.
			if (i<1||i>1440)
				throw new ControlException("Cannot set update interval to "+i+", update intervals must be between 1 and 1440");
			dbh.setFeedUpdateInterval(f, i);
		}catch(DBHandlerException e){
			throw new ControlException("Problem changing feed's update interval: "+e.getMessage());
		}
	}
	/**
	 * Gets the Parent Folder of a Folder.
	 * 
	 * @param f Folder to get parent of.
	 * @return Folder that is parent of f.
	 * @throws ControlException if the Feed has no parent.
	 */
	public Folder getParent(Folder f) throws ControlException{
		try{
			return dbh.getParent(f);
		}catch(Exception e){
			throw new ControlException("Problem getting parent Folder: "+e);
		}
	}
	/**
	 * Gets the Parent Folder of a Feed.
	 * @param f Feed to get parent of.
	 * @return Folder that is parent of f.
	 * @throws ControlException if the Feed has no parent.
	 */
	public Folder getParent(Feed f) throws ControlException{
		try{
			return dbh.getParent(f);
		}catch(Exception e){
			throw new ControlException("Problem getting parent Folder: "+e);
		}
	}
	/**
	 * Gets the Parent Feed of an article.
	 * @param f Feed to get parent of.
	 * @return Folder that is parent of f.
	 * @throws ControlException if the Feed has no parent.
	 */
	public Feed getParent(Article a) throws ControlException{
		try{
			return dbh.getParent(a);
		}catch(Exception e){
			throw new ControlException("Problem getting parent Feed: "+e);
		}
	}
	/**
	 * Returns a list of top-level Objects.  This should return a list of a folder 
	 * and two feeds, representign the SubscribedFeeds folder, outbox feed, and trash bin, respectively
	 * 
	 * @return List of top level folders.
	 * @throws ControlException if Internal database has been corrupted. 
	 */
	public List<Object> getRoot() throws ControlException{
		try{
			return dbh.getRoot();
		}
		catch (DBHandlerException e){
			throw new ControlException("Problem getting Root Folders: "+e);
		}
	}
	/**
	 * Gets the children folders of a folder.
	 * 
	 * @param f Folder parent
	 * @return List of folders contained by the parent folder.
	 * @throws ControlException if there is a problem with the internal data structures 
	 * or if the argument folder is not represented in the internal data structures.
	 */
	public List<Folder> getChildrenFolders(Folder f) throws ControlException{
		try{
			return dbh.getChildrenFolders(f);
		}
		catch (DBHandlerException e){
			throw new ControlException("Problem getting Children Folders: "+e);
		}
	}
	/**
	 * Gets the children feeds of a folder.
	 * 
	 * @param f Folder parent
	 * @return List of feeds contained by the parent folder.
	 * @throws ControlException if there is a problem with the internal data structures 
	 * or if the argument feed is not represented in the internal data structures.
	 */
	public List<Feed> getChildrenFeeds(Folder f) throws ControlException{
		
		try{
			return dbh.getChildrenFeeds(f);
		}
		catch (DBHandlerException e){
			throw new ControlException("Problem getting Children Feeds: "+e);
		}
	}
	/**
	 * Gets a random, unread article. Only active, non-deleted articles are returned.
	 * @return An article
	 * @throws ControlException If there is a problem getting an article from
	 * the database.
	 */
	public Article getArticleRandom() throws ControlException{
		try{
			return dbh.getArticleRandom();
		}
		catch (DBHandlerException e){
			throw new ControlException("Problem getting a random article: "+e);
		}
	}
	/**
	 * Gets the List of Articles associated with a feed. 
	 * @param f Feed that contains articles
	 * @return List of Articles in the Feed.
	 * @throws ControlException If the feed is not already represented in the internal
	 * data structures or if there a problem with the internal data structures.
	 */
	public List<Article> getArticles(Feed f) throws ControlException{
		try{
			//if its the trash feed, then gets the trash articles.
			if (f.getId()==getTrash().getId())
				return dbh.getTrashArticles();
			else
				return dbh.getArticlesbyFeed(f);	
		}
		catch (DBHandlerException e){
			throw new ControlException("Problem getting articles from feed: "+e);
		}
	}
	/**
	 * Gets all feeds.
	 * @return List of all feeds.
	 * @throws ControlException if there is an internal error.
	 */
	public List<Feed> getAllFeeds() throws ControlException{
		try{
			return dbh.getFeedsAll();
		}catch(DBHandlerException e){
			throw new ControlException("Problem getting feeds: "+e);
		}
	}
	/**
	 * Gets the Subscribed feeds folder
	 * 
	 * @return Folder SubscribedFeeds Folder
	 * @throws ControlException if there is a problem with the internal data structures.
	 */
	public Folder getSubscribedFeeds() throws ControlException{
		try{
			return dbh.getSubscribedFeeds();
		}
		catch (DBHandlerException e){
			throw new ControlException("Problem fetching subscribed feeds: "+e);
		}
	}
	/**
	 * Gets the Trash Feed
	 * 
	 * @return Feed that is the Trash bin
	 * @throws ControlException if there is a problem with the internal data structures.
	 */
	public Feed getTrash() throws ControlException{
		try{
			return dbh.getTrash();
		}
		catch (DBHandlerException e){
			throw new ControlException("Problem fetching trash: "+e);
		}
	}
	/**
	 * Gets the Outbox Feed
	 * 
	 * @return Feed Outbox feed
	 * @throws ControlException if there is a problem with the internal data structures.
	 */
	public Feed getOutbox() throws ControlException{
		try{
			return dbh.getOutbox();
		}
		catch (DBHandlerException e){
			throw new ControlException("Problem fetching outbox: "+e);
		}
	}
	/**
	 * Permanently empties the trash bin.
	 * 
	 * @throws ControlException if there is a problem with the internal data structures.
	 */
	public void emptyTrash() throws ControlException{
		try{
			dbh.emptyTrash();
		}catch(DBHandlerException e){
			throw new ControlException("Problem emptying trash: "+e);
		}
	}
	/**
	 * Marks the specified article as read.
	 * 
	 * @param a Article to be marked as read
	 * @return Article that represents the input article but has been marked as read.
	 * @throws ControlException If the Article is not already represented in the internal
	 * data structures or if there a problem with the internal data structures.
	 */
	public Article markRead(Article a) throws ControlException{
		try{
			return dbh.markRead(a);
		}
		catch (DBHandlerException e){
			throw new ControlException("Problem setting article's read flag: "+e);
		}
	}
	/**
	 * Marks the specified article as new.
	 * 
	 * @param a Article
	 * @return Article that represents the input article but has been marked as new.
	 * @throws ControlException If the Article is not already represented in the internal
	 * data structures or if there a problem with the internal data structures.
	 */
	public Article markUnread(Article a) throws ControlException{
		try{
			return dbh.markUnread(a);
		}
		catch (DBHandlerException e){
			throw new ControlException("Problem setting article's read flag: "+e);
		}
	}
//These methods are valid, but commented out since we didn't use these features so their effect on the 
//database is untested
//	/**
//	 * Sets the articles rating as the specified integer.
//	 * 
//	 * @param a Article
//	 * @param i	int Rating
//	 * @return Article that represents the input article but has its rating set to i.
//	 * @throws ControlException If the Article is not already represented in the internal
//	 * data structures or if there a problem with the internal data structures.
//	 */
//	public Article setRating(Article a, int i) throws ControlException{
//		try{
//			return dbh.setRating(a,i);
//		}
//		catch (DBHandlerException e){
//			throw new ControlException("Problem setting article's rating: "+e);		
//		}
//	}
//	/**
//	 * Sets the number read time of article to the specified number of seconds.
//	 * 
//	 * @param a Article
//	 * @param time Number of seconds
//	 * @return Article that represents the input article but has its read time set appropriately.
//	 * @throws ControlException If the Article is not already represented in the internal
//	 * data structures or if there a problem with the internal data structures.
//	 */
//	public Article setReadTime(Article a, int time) throws ControlException{
//		try{
//			return dbh.setReadTime(a,time);
//		}
//		catch (DBHandlerException e){
//			throw new ControlException("Problem setting article's read time: "+e);
//		}
//	}
	/**
	 * Puts the specified article in the trash bin.
	 * 
	 * @param a Article to be put in the trash
	 * @return Article that represents the input article but has its trash status set appropriately.
	 * @throws ControlException If the Article is not already represented in the internal
	 * data structures or if there a problem with the internal data structures.
	 */
	public Article setTrash(Article a) throws ControlException{
		try{
			return dbh.setTrash(a);
		}
		catch (DBHandlerException e){
			throw new ControlException("Problem putting article in trash: "+e);
		}
	}
	/**
	 * Takes the specified article in the trash bin and restores it to its original location.
	 * 
	 * @param a Article to be restored from the trash
	 * @return Article that represents the input article but has its trash status set appropriately.
	 * @throws ControlException If the Article is not already represented in the internal
	 * data structures or if there a problem with the internal data structures.
	 */
	public Article setUnTrash(Article a) throws ControlException{
		try{
			return dbh.setUnTrash(a);
		}
		catch (DBHandlerException e){
			throw new ControlException("Problem removing article from trash: "+e);
		}
	}
	/**
	 * Deletes the specified article.
	 * @param a Article to be deleted
	 * @throws ControlException if there is an internal error.  
	 */
	public void setDelete(Article a) throws ControlException{
		try{
			dbh.setDelete(a);
		}
		catch (DBHandlerException e){
			throw new ControlException("Problem deleting article: "+e.getMessage());
		}
	}
	/**
	 * Subscribes to the feed at the specified url.
	 * 
	 * @param url of rss feed
	 * @return Feed object
	 * @throws ControlException If url is malformed, there is a problem with the internet
	 *  connection, or if there is a problem with the internal data structures.
	 */
	public Feed subscribe(String url) throws ControlException{
		try{
			//generates a feed from the url
			Feed parsedFeed = RomeFeedParser.ParseURL(url);
			//adds that feed to the database
			Feed f = dbh.addFeed(parsedFeed, null);
			return f;
		}
		catch(Exception e){
			throw new ControlException("Problem subscribing to feed: "+e.getMessage());
		}
	}
	/**
	 * Subscribes to a new feed, and places that feed into the specified directory.
	 * @param url String url of rss feed.
	 * @param destination Folder where feed should be placed
	 * @return Feed object
	 * @throws ControlException If url is malformed, there is a problem with the internet
	 *  connection, or if there is a problem with the internal data structures.
	 */
	public Feed subscribe(String url, Folder destination) throws ControlException{
		try{
			Feed parsedFeed = RomeFeedParser.ParseURL(url);
			Feed f = dbh.addFeed(parsedFeed, destination);
			return f;
		}
		catch(Exception e){
			throw new ControlException("Problem subscribing to feed: "+e);
		}
		
	}
	/**
	 * Unsubscribes from specified feed.
	 * @param f Feed to be unsubscribed from.
	 * @return True
	 * @throws ControlException if Feed specified is not represented in the internal data
	 * structures (i.e. it is not subscribed to), or if the feed cannot be unsubscribed from.  
	 * For instance, if the feed is the outbox feed or trash bin.
	 */
	public boolean unSubscribe(Feed f) throws ControlException{
		try{
			//checks if feed is outbox or trash, throws exception if so
			if (f.getId()==getOutbox().getId())
				throw new ControlException("Cannot unsubscribe from outbox");
			if (f.getId()==getTrash().getId())
				throw new ControlException("Cannot unsubscribe from Trash");
			return dbh.unSubscribe(f);
		}catch(DBHandlerException e){
			throw new ControlException("Failed to Unsubscribe from Feed: "+e);
		}
	}
	/**
	 * Deletes specifed folder and all of its contents.
	 * 
	 * @param f Folder to be deleted
	 * @return True
	 * @throws ControlException If folder is not represented in the internal data
	 * structures, or if the folder is the subscribedFeeds folder.
	 */
	public boolean deleteFolder(Folder f) throws ControlException{
		try{
			if(f.getId()!=getSubscribedFeeds().getId())
				return dbh.deleteFolder(f);
			else throw new ControlException("Cannot delete Subscribed Feeds folder");
		}
		catch(DBHandlerException e){
			throw new ControlException("Failed to Delete Folder: "+e.getMessage());
		}
	}
	/**
	 * Refreshes the specified feed by comparing to the current rss feed at the
	 * feed object's url.
	 * @param f Feed to be updated
	 * @return Feed object that has been updated appropriately.
	 * @throws ControlException If there is an error updating the feed, or if the 
	 * feed is not already represented in the internal data structures.
	 */
	public Feed updateFeed(Feed f) throws ControlException{
		try{
			String url = f.getUrl();
			Feed updated = RomeFeedParser.ParseURL(url);
			return dbh.updateFeed(updated);
		}catch(DBHandlerException e){
			throw new ControlException("Failed to Update Feed: "+e.getMessage());
		}catch(Exception e){
			throw new ControlException("Failed to Update Feed: "+e.getMessage());
		}
	}
	/**
	 * Adds a new folder.  A folder cannot be added to a parent folder that has a child 
	 * the same name.
	 * @param parent Folder that will contain the added folder
	 * @param child Folder that is to be added
	 * @return Folder that represents the newly added folder
	 * @throws ControlException if the folder couldn't be added.
	 */
	public Folder addFolder(Folder parent, Folder child) throws ControlException{
		try{
			return dbh.addFolder(parent, child);
		}catch(DBHandlerException e){
			throw new ControlException("Problem adding folder: "+e.getMessage());
		}
	}
	/**
	 * Gets the number of new articles in the selected feed. 
	 * @param feed Selected feed
	 * @return int Count of new articles in the selected feed.
	 * @throws ControlException if there is an internal error or the feed is not currently
	 * subscribed to.
	 */
	public int getNumberOfNewArticles(Feed feed)throws ControlException{
		try{
			return dbh.getNumberOfNewArticles(feed);
		}catch(DBHandlerException e){
			throw new ControlException("Problem retrieving number of new articles: "+e);
		}
	}
	/**
	 * Gets the number of articles that are found by a search based on the passed in 
	 * constraing
	 * @param constraint String constraint
	 * @return int Count of articles that are returned by a search
	 * @throws ControlException If there is an internal error
	 */
	public int getNumberOfArticles(String constraint)throws ControlException{
		try{
			return dbh.getNumberArticles(constraint);
		}catch(DBHandlerException e){
			throw new ControlException("Problem getting number of articles");
		}
	}
	/**
	 * Moves Folder into destination folder.
	 * 
	 * @param source Folder to be moved
	 * @param destination Folder that source folder will be moved into.
	 * @return true
	 * @throws ControlException If either folder argument is not represented in the
	 * internal data structures.
	 */
	public boolean move(Folder source, Folder destination) throws ControlException{
		try{
			return dbh.moveFolder(source,destination);
		}catch(DBHandlerException e){
			throw new ControlException("Problem moving folder: "+e.getMessage());
		}
	}
	/**
	 * Moves Feed into destination folder.
	 * 
	 * @param source Feed to be moved
	 * @param destination Folder that source Feed will be moved into.
	 * @return true
	 * @throws ControlException If either argument is not represented in the internal data
	 * structures.
	 */
	public boolean move(Feed source, Folder destination) throws ControlException{
		try{
			return dbh.moveFeed(source,destination);
		}catch(DBHandlerException e){
			throw new ControlException("Problem moving feed: "+e.getMessage());
		}
	}
	/**
	 * Copies the specified article to the outbox.
	 * @param source Article to be moved
	 * @throws ControlException if there is an internal error, or if the article is not already
	 *  represented in the internal data structures.
	 */
	public void moveToOutbox(Article source) throws ControlException{
		try{
			dbh.copyArticle(source, dbh.getOutbox());
		}catch(DBHandlerException e){
			throw new ControlException("Couldn't move to outbox: "+e.getMessage());
		}
	}
	/**
	 * Returns the StatusLineManager of the Controller window.  This can be used by 
	 * actions to utilize the status line as a progress monitor.
	 * @return StatusLineManager
	 */
	public StatusLineManager getStatusLine(){
		return getStatusLineManager();
	}
	/**
	 * Searches for the articles based on the specified input values.  
	 * Searches all articles from all feeds.  Searches for the 
	 * string passed in as constraint in all fields who's corresponding input is true.
	 * For example: searchArticles(true,false,true, "Jim bob") finds all articles that contain
	 * the string "Jim bob" in either their title or summary. 
	 *
	 * @param byAuthor
	 * @param byTitle
	 * @param byContent
	 * @param constraint
	 * @return List<Article> articles that match constraints.
	 */
	public List<Article> searchArticles(boolean byAuthor, 
			boolean byTitle, 
			boolean byContent, 
			String constraint){
		try{
			return dbh.searchArticles(byAuthor, byTitle, byContent, constraint);
		}catch(DBHandlerException e){
			return new ArrayList<Article>();
		}
		
	}
	/**
	 * Searches for the articles based on the specified input values.  
	 * Searches only articles in the selected feed.  Searches for the 
	 * string passed in as constraint in all fields who's corresponding input is true.
	 * For example: searchArticles(true,false,true, myFeed, "Jim bob") finds all articles in myFeed that contain
	 * the string "Jim bob" in either their title or summary. 
	 *
	 * @param byAuthor
	 * @param byTitle
	 * @param byContent
	 * @param feed
	 * @param constraint
	 * @return List<Article> articles that match constraints.
	 */
	public List<Article> searchArticles(boolean byAuthor, 
			boolean byTitle, 
			boolean byContent, 
			Feed feed,
			String constraint){
		try{
			return dbh.searchArticles(byAuthor, byTitle, byContent,feed, constraint);
		}catch(DBHandlerException e){
			return new ArrayList<Article>();
		}
	}
	/**
	 * Checks to make sure that there is a valid user profile.  If there is no valid user profile 
	 * then prompts the user to create one.  Note that this method does nothing to guarantee that the 
	 * user actually does create a new user profile.
	 *
	 */
	public void checkUser(){
		try{
			//if the current user is "Default User" it means that its the first time the program has been opened 
			//or it somehow got to an invalid state.
			if ("Default User".equals(dbh.getCurrentUser())){
				Controller.getApp().getShell().getDisplay().asyncExec(new NewUserAction());
			}
		}catch(DBHandlerException e){
			//do nothing
		}
	}
	/**
	 * Tries to creates a new user profile based on the input username, and switches to that userprofile. 
	 * There are no guarantees that this method will succeed. 
	 * @param username String Username
	 */
	public void createAndSwitchUser(String username){
		try{
			//adds a new user with no password
			dbh.addUser(username,"");
			//switch to the new user
			switchUser(username);
		}catch(DBHandlerException e){
			setStatus("Failed to create new user: "+username);
		}
	}
	/**
	 * Checks to see if there is a user profile for the specified user name.
	 * @param username String Username
	 * @return true if there is a user profile with that username, false otherwise.
	 */
	public boolean isUser(String username){
		try{
			//if dbh.getUserID(username) returns a number greater than 0, then a user exists
			int id = dbh.getUserID(username);
			if (id > 0)
				return true;
			else return false;
		}
		catch(DBHandlerException e){
			return false;
		}
	}
	/**
	 * Tries to switch to the specified user profile.  If there is no such profile, then nothing happens.
	 * @param username String username
	 */
	public void switchUser(String username){
		try{
			//saves the view info for the current user
			int currentView = allViews.indexOf(activeView);
			dbh.setCurrentUserView(currentView);
			//switches to the new user
			dbh.setCurrentUser(username,"");
			//switchs to the new user's stored view
			currentView = dbh.getCurrentUserView();
			setActiveView(allViews.get(currentView));
			activeView.update();
			//reports
			setStatus("Changed to user: "+username);
		}catch(DBHandlerException e){
			setStatus("Failed to switch to user: "+username);
		}
	}
	/**
	 * Tries to delete the specified user profile.  If there is no user profile with that username, then 
	 * this method merely returns.  If the username specifies the current user, then this method throws 
	 * a ControlException
	 * @param username String username
	 * @throws ControlException If username is the current user
	 */
	public void deleteUser(String username) throws ControlException{
		try{
			if (!dbh.getCurrentUser().equals(username)){
				dbh.deleteUser(username, "");
			}else{
				throw new ControlException("Cannot delete active user");
			}
		}catch(DBHandlerException e){
			setStatus("Failed to delete user: "+username);
		}
	}
	/**
	 * Returns a list of all usernames
	 * 
	 * @return List of usernames
	 */
	public List<String> getAllUsers(){
		try{
			return dbh.getAllUsers();
		}catch(DBHandlerException e){
			return new ArrayList<String>();
		}
	}
	/**
	 * Performs a search based on the specified string and returns a list of Articles ordered by their date.
	 * 
	 * @param string String constraint
	 * @return List of Articles ordered by date
	 */
	public List<Article> orderedSearch(String string){
		try{
			return dbh.searchArticlesOrdered(true, true, true, string);
		}catch(DBHandlerException e){
			return new ArrayList<Article>();
		}
	}
	/**
	 * Tries to rename the specified feed.
	 * @param f Feed to be renamed
	 * @param newName String new name
	 */
	public void renameFeed(Feed f,String newName){
		try{
			dbh.setFeedTitle(f, newName);
		}catch(DBHandlerException e){
			setStatus("Failed to rename feed "+f + ": "+e);
		}
	}
	/**
	 * Resets the underlying database.  This will delete all user data and thus is only included as a way to reset the software to 
	 * an initial state and recover if the database gets corrupted.
	 * @throws ControlException If there is a internal error.  If that happens, there is a serious problem.
	 */
	public void reset() throws ControlException{
		try{
			//resets the database
			dbh.resetDB();
			//checks the user 
			checkUser();
		}catch(DBHandlerException e){
			throw new ControlException("Problem trying to reset database...you might consider re-downloading the software");
		}
	}
}
