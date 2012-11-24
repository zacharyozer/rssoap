package view.newsPaperView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Article;
import model.Feed;
import model.Folder;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import view.NoItemSelectedException;
import view.View;
import view.mailBoxView.util.MailBoxTreeContentProvider;
import view.mailBoxView.util.MailBoxTreeLabelProvider;
import view.newsPaperView.util.PaperLayout;
import actions.DeleteFolderAction;
import actions.EmptyTrashAction;
import actions.ExportOutboxAction;
import actions.FeedPropertiesAction;
import actions.NewArticleAction;
import actions.NewFolderAction;
import actions.SubscribeAction;
import actions.UnSubscribeFeedAction;
import actions.UpdateFeedAction;
import control.ControlException;
import control.Controller;
/**
 * The NewsPaperView is a pluggable view for the RSS Reader that lays out articles to read like 
 * a newspaper
 * @author pgroudas
 *
 */
public class NewsPaperView implements View{
//	private Shell parent;
	private Controller control;
	private TreeViewer treeViewer;
	private Browser browser;
	//some actions
	private SubscribeAction subscribeAction = new SubscribeAction();
	private UnSubscribeFeedAction unSubscribeAction = new UnSubscribeFeedAction();
	private DeleteFolderAction deleteFolderAction = new DeleteFolderAction();
	private EmptyTrashAction emptyTrashAction = new EmptyTrashAction();
	private NewFolderAction newFolderAction = new NewFolderAction();
	private UpdateFeedAction updateFeedAction = new UpdateFeedAction();
	private NewArticleAction newArticleAction = new NewArticleAction();
	private FeedPropertiesAction feedPropertiesAction = new FeedPropertiesAction();
	private ExportOutboxAction exportOutboxAction = new ExportOutboxAction();
	//some random useful things
	private Feed mostRecentFeed;
	/**
	 * Constructs a new MailBoxView.
	 * @param c Controller to be associated with this view.
	 */
	public NewsPaperView(Controller c){
		control = c;
	}
	/**
	 * Creates and returns the composite of the view.  
	 * @return Composite
	 */
	public Composite getComposite(Composite parent){
		//composite is the composite we are building, it gets a formlayout
		Composite composite = new Composite(parent,SWT.NONE);
		FormLayout layout = new FormLayout();
		composite.setLayout(layout);
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		FormData data;
		
		//creates the sash on the right of the tree
		final Sash vertSash = new Sash(composite,SWT.VERTICAL);
		data = new FormData();
		data.top = new FormAttachment(0,0);
		data.bottom = new FormAttachment(100,0);
		data.left = new FormAttachment(25,0);
		vertSash.setLayoutData(data);
		vertSash.addSelectionListener(new SelectionAdapter(){//makes the sashes resizeable
			public void widgetSelected(SelectionEvent event){
				((FormData) vertSash.getLayoutData()).left = new FormAttachment(0,event.x);
				vertSash.getParent().layout();
			}
		});

		//construct tree in left side of composite
		Tree leftTree = new Tree(composite,SWT.MULTI|SWT.BORDER|SWT.V_SCROLL);
		data = new FormData();
		data.bottom = new FormAttachment(100,0);
		data.top = new FormAttachment(0,0);
		data.right = new FormAttachment(vertSash,0);
		data.left = new FormAttachment(0,0);
		leftTree.setLayoutData(data);
		treeViewer = new TreeViewer(leftTree);
		treeViewer.setContentProvider(new MailBoxTreeContentProvider(control));
		treeViewer.setLabelProvider(new MailBoxTreeLabelProvider());
		treeViewer.setInput("root");
		//constructs the web browser
		browser = new Browser(composite, SWT.BORDER);
		data = new FormData();
		data.left = new FormAttachment(vertSash,0);
		data.top = new FormAttachment(0,0);
		data.right = new FormAttachment(100,0);
		data.bottom = new FormAttachment(100,0);
		browser.setLayoutData(data);
		
		//references to the underlying tree object
		final Tree tree = treeViewer.getTree();
		//keylistener to handle deletions
		tree.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) {
				if(e.character==0x7f){
					if (tree.getSelection().length==1&&tree.getSelection()[0].getData() instanceof Folder){
						//if there is one item selected and it is a folder, delete it
						deleteFolderAction.run();
					}else{
						//otherwise delete selected feeds
						unSubscribeAction.run();
					}
				}
			}
			public void keyReleased(KeyEvent e) {
			}
		});
		//does things like update table when items in tree are selected
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event){
				if(event.getSelection().isEmpty()) {
					return;
				}
				if(event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					Object o = selection.getFirstElement();
					if (o instanceof Feed){
						//if the selected item is a feed, shows the articles in the browser
						Feed f = (Feed) o;
						try{
							mostRecentFeed = f;
							List<Article> articles = control.getArticles(f);
							//generates the html and sets it in the browser
							browser.setText(PaperLayout.MakeHTML(PaperLayout.GetNewUnreadArticles(articles)));
						}catch(ControlException e){
							control.setStatus(e.getMessage());
						}
					}else if (o instanceof Folder){
					}
				}
			}
		});
//		/edits the right click menu to add appropriate actions
		final MenuManager treeMenuManager = new MenuManager();
		treeMenuManager.addMenuListener(new IMenuListener(){
			public void menuAboutToShow(IMenuManager arg0) {
				//first remove all actions, then add appropriate ones based on context
				treeMenuManager.removeAll();
				if(treeViewer.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
					if (selection.size()==1){
						Object o = selection.getFirstElement();
						//of the object is a folder add the following actions to the menu
						if (o instanceof Folder){
							Folder f = (Folder)o;
							try{
								treeMenuManager.add(subscribeAction);
								treeMenuManager.add(new Separator());
								treeMenuManager.add(newFolderAction);
								//as long as its not the top folder we let the user delete it
								if (f.getId()!=control.getSubscribedFeeds().getId())
									treeMenuManager.add(deleteFolderAction);
							}catch(ControlException e){
								control.setStatus("Problem fetching top level folder");
							}
						}
						//if its a feed we want different actions
						if(o instanceof Feed){
							Feed f = (Feed) o;
							try{
								//special actions for the trash and outbox
								if (f.getId() == control.getTrash().getId()){
									treeMenuManager.add(emptyTrashAction);
								}else if (f.getId()==control.getOutbox().getId()){
									treeMenuManager.add(newArticleAction);
									treeMenuManager.add(exportOutboxAction);
								}
								else{
									//actions for normal feeds
									treeMenuManager.add(updateFeedAction);
									treeMenuManager.add(unSubscribeAction);
									treeMenuManager.add(feedPropertiesAction);
								}
							}catch(ControlException e){
								control.setStatus("Problem fetching top level folder");
							}
						}
					}
					//if there are multiple selections, do different things
					else if(selection.size()>1){
						boolean allFeeds = true;
						int trashId, outBoxId;
						//gets the trashid and boxid once so we don't need to query the database every iteration
						//to check if the feed is trash or outbox
						try{
							 trashId = control.getTrash().getId();
							 outBoxId = control.getOutbox().getId();
						}catch(ControlException e){
							trashId = 0;
							outBoxId = 0;
						}
						//gets iterator over selection
						Iterator iter = selection.iterator();
						//iterates through selection to check if the selection is only feeds
						while(iter.hasNext()){
							Object o = iter.next();
							if (!(o instanceof Feed)){
								allFeeds=false;
								break;
							}
							if (((Feed)o).getId() ==trashId||((Feed)o).getId() ==outBoxId){
								allFeeds = false;
								break;
							}
						}
						if (allFeeds){
							//if the only selected items are feeds, then let them update all the selected feeds or unsubscribe
							treeMenuManager.add(updateFeedAction);
							treeMenuManager.add(unSubscribeAction);
						}
					}
				}
			}
		});
		//set the menu
		tree.setMenu(treeMenuManager.createContextMenu(tree));
		return composite;
	}
	
//methods to implement view interface
	
	/**
	 * The update method is called when the Controller wants the view to refresh.
	 *
	 */
	public void update(){
		treeViewer.refresh();
	}
	/**
	 * Gets the currently selected feed in the view.  If no feed is selected, gets the most recent 
	 * feed that was selected.  If no feed has ever been selected, then throw an exception.
	 * @return Feed selected feed.
	 * @throws NoItemSelectedException If no feed is currently selected.
	 */
	public Feed getSelectedFeed() throws NoItemSelectedException{
		if(treeViewer.getSelection() instanceof IStructuredSelection) {
	           IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
	           Object o = selection.getFirstElement();
	           if (o instanceof Feed){
	        	   return (Feed) o;
	           }else if (mostRecentFeed!=null){
	        	   return mostRecentFeed;
	           }
	       }
		throw new NoItemSelectedException("No Feed Selected");
	}
	/**
	 * gets the currently selected article in the view. Not implemented for this view.
	 * @return Article selected article.
	 * @throws NoItemSelectedException always
	 */
	public Article getSelectedArticle() throws NoItemSelectedException{
		throw new NoItemSelectedException("No Article Selected");
	}
	/**
	 * Gets the currently selected folder in the view.  If no folder is selected, 
	 * gets the folder that is the parent of the first selected item.  If nothing is selected,
	 * then throws an exception.
	 * @return Folder selected folder.
	 * @throws NoItemSelectedException If no folder is currently selected.
	 */
	public Folder getSelectedFolder() throws NoItemSelectedException{
		if(treeViewer.getSelection() instanceof IStructuredSelection) {
		//System.out.println("selected item is istructuredselection");
			IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
			Object o = selection.getFirstElement();
			if (o instanceof Folder){
				//if selection is a folder, just return it
				Folder f = (Folder) o;
				return f;
			}
			if(o instanceof Feed){
				Feed f = (Feed) o;
				//if its a feed thats not the trash/outbox, then return its parent
				try{
					if (f.getId()!=control.getTrash().getId()&&f.getId()!=control.getOutbox().getId())				//FOLDER MAGIC
						return (Folder) treeViewer.getTree().getSelection()[0].getParentItem().getData();
				}
				catch(ControlException e){
				}
			}
		}
		throw new NoItemSelectedException("No Folder Selected");
	}
	/**
	 * Gets all selected articles.  Not implemented for this view.  Always throws 
	 * NoItemSelectedException
	 */
	public List<Article> getSelectedArticles() throws NoItemSelectedException{
		throw new NoItemSelectedException("No Article Selected");
	}
	/**
	 * Returns a list of the selected feeds
	 * @return List of feeds that are selected
	 * @throws NoItemSelectedException if no feeds are selected
	 */
	public List<Feed> getSelectedFeeds() throws NoItemSelectedException{
			if(treeViewer.getSelection() instanceof IStructuredSelection) {
		           IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
		           List<Feed> feeds = new ArrayList<Feed>();
		           Iterator iter = selection.iterator();
		           //iterates through selection and gets feeds
		           while (iter.hasNext()){
		        	   Object o = iter.next();
		        	   if (o instanceof Feed){
			        	   feeds.add((Feed)o);
			           }
		           }if (feeds.size()>0)
		        	   return feeds;
		       }
			//throw exception if there were no feeds
			throw new NoItemSelectedException("No Feed Selected");
	}
	/**
	 * Gets selected folders.  Not implemented in this view
	 */
	public List<Folder> getSelectedFolders() throws NoItemSelectedException{
		throw new NoItemSelectedException();
	}
	/**
	 * gets the parent folder of the selected item in the tree.
	 * @return Folder
	 * @throws NoItemSelectedException if no item in the tree is selected, of if the selected item 
	 * has no parent (Subcribed Feeds folder, outbox or trash)
	 */
	public Folder getParentOfSelected() throws NoItemSelectedException{
		if(treeViewer.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection)treeViewer.getSelection();
			Object o = selection.getFirstElement();
			//if the item selected in the tree is a folder, then traverses the tree to find its parent folder
			//throws an exception if the item is a root item
			if (o instanceof Folder){
				Folder f = (Folder) o;
				try{
					if (f.getId()!=control.getSubscribedFeeds().getId())
						return (Folder) treeViewer.getTree().getSelection()[0].getParentItem().getData();
					else throw new NoItemSelectedException("Top level folder has no parent");
				}catch(ControlException e){
					throw new NoItemSelectedException("No Item Selected");
				}
			}
			//if the item selected in the tree is a feed, then traverses the tree to
			//find its parent, but throws an exception if its a top-level item(outbox or trash).
			if(o instanceof Feed){
				Feed f = (Feed) o;
				try{
					if (f.getId()!=control.getTrash().getId()&&f.getId()!=control.getOutbox().getId())
						return (Folder) treeViewer.getTree().getSelection()[0].getParentItem().getData();
					else throw new NoItemSelectedException("Top level feed has no parent");
				}catch(ControlException e){
					throw new NoItemSelectedException("No Item Selected");
				}
			}
		}
		throw new NoItemSelectedException("No Item Selected");
	}
	/**
	 * Updates the view of the articles of the specified feed.  Not implemented in 
	 * this view.
	 * @param f Feed
	 */
	public void updateArticles(Feed f){
	}
	/**
	 * Updates the view of the children of the specified folder.
	 * @param f Folder parent
	 */
	public void updateFolderContents(Folder f){
		treeViewer.refresh(f);
	}
	/**
	 * Updates the view of the outbox in the tree
	 */
	public void updateOutbox(){
		treeViewer.refresh(((TreeItem)treeViewer.getTree().getItem(1)).getData());
	}
	/**
	 * Updates the view of the trash in the tree
	 */
	public void updateTrash(){
		treeViewer.refresh(((TreeItem)treeViewer.getTree().getItem(2)).getData());
		try{
			Feed trash = control.getTrash();
			if (mostRecentFeed!=null&&mostRecentFeed.getId()==trash.getId()){
				updateArticles(trash);
			}
		}catch(ControlException e){
		}
	}
	/**
	 * Updates the view of a particular article.  Not implemented in this view.  
	 * @param a Article
	 */
	public void updateArticle(Article a){
	}
	/**
	 * updates the view of a feed in the tree.
	 * @param f Feed
	 */
	public void updateFeed(Feed f){
		treeViewer.refresh(f);
	}
	/**
	 * Sets the url of the browser.
	 * @param url String
	 */
	public void setURL(String url){
		browser.setUrl(url);
	}
}