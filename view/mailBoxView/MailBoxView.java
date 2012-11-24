package view.mailBoxView;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Article;
import model.Feed;
import model.Folder;
import model.Util;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import view.NoItemSelectedException;
import view.View;
import view.mailBoxView.util.ArticleViewerSorter;
import view.mailBoxView.util.ColumnConstants;
import view.mailBoxView.util.MailBoxTableContentProvider;
import view.mailBoxView.util.MailBoxTableLabelProvider;
import view.mailBoxView.util.MailBoxTreeContentProvider;
import view.mailBoxView.util.MailBoxTreeLabelProvider;
import actions.AddToOutbox;
import actions.DeleteFolderAction;
import actions.DeliciousAction;
import actions.DiggAction;
import actions.EmptyTrashAction;
import actions.ExportOutboxAction;
import actions.FeedPropertiesAction;
import actions.MarkNewAction;
import actions.MarkReadAction;
import actions.NewArticleAction;
import actions.NewFolderAction;
import actions.SubscribeAction;
import actions.TrashAction;
import actions.UnSubscribeFeedAction;
import actions.UnTrashAction;
import actions.UpdateFeedAction;
import control.ControlException;
import control.Controller;
/**
 * The MailBoxView is a pluggable view for the RSS Reader that is visually similar
 *  to an e-mail client.
 * @author pgroudas
 *
 */
public class MailBoxView implements View{
	//private Shell parent;
	private Controller control;
	private TreeViewer treeViewer;
	private TableViewer tableViewer;
	private Browser browser;
	//some actions
	private SubscribeAction subscribeAction = new SubscribeAction();
	private UnSubscribeFeedAction unSubscribeAction = new UnSubscribeFeedAction();
	private DeleteFolderAction deleteFolderAction = new DeleteFolderAction();
	private EmptyTrashAction emptyTrashAction = new EmptyTrashAction();
	private NewFolderAction newFolderAction = new NewFolderAction();
	private UpdateFeedAction updateFeedAction = new UpdateFeedAction();
	private MarkReadAction markReadAction = new MarkReadAction();
	private MarkNewAction markNewAction = new MarkNewAction();
	private AddToOutbox addToOutbox = new AddToOutbox();
	private TrashAction trashAction = new TrashAction();
	private UnTrashAction unTrashAction = new UnTrashAction();
	private DiggAction diggAction = new DiggAction();
	private DeliciousAction deliciousAction = new DeliciousAction();
	private NewArticleAction newArticleAction = new NewArticleAction();
	private FeedPropertiesAction feedPropertiesAction = new FeedPropertiesAction();
	private ExportOutboxAction exportOutboxAction = new ExportOutboxAction();
	//some random useful things
	private Feed mostRecentFeed; //the most recent selected feed
	/**
	 * Constructs a new MailBoxView.
	 * @param c Controller to be associated with this view.
	 */
	public MailBoxView(Controller c){
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
		
		//construct the horizontal sash
		final Sash horiSash = new Sash(composite,SWT.HORIZONTAL);
		data = new FormData();
		data.left = new FormAttachment(vertSash,0);
		data.top = new FormAttachment(35,0);
		data.right = new FormAttachment(100,0);
		horiSash.setLayoutData(data);
		horiSash.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event){//makes the sashes resizeable
				((FormData) horiSash.getLayoutData()).top = new FormAttachment(0,event.y);
				horiSash.getParent().layout();
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
		
		//construct the table 
		tableViewer= new TableViewer(composite, SWT.FULL_SELECTION|SWT.MULTI);
		tableViewer.setContentProvider(new MailBoxTableContentProvider());
		tableViewer.setLabelProvider(new MailBoxTableLabelProvider());
		tableViewer.setSorter(new ArticleViewerSorter());
		
		//creates search composite
		Composite search = new Composite(composite, SWT.NONE);
		{	//content check button
			final Button content = new Button(search,SWT.CHECK);
			content.setSelection(true);
			content.setText("Content");
			data = new FormData();
			data.width = 80;
			data.left = new FormAttachment(0,0);
			data.top = new FormAttachment(0,0);
			data.bottom = new FormAttachment(100,0);
			content.setLayoutData(data);
			//title check button
			final Button title = new Button(search,SWT.CHECK);
			title.setText("Title");
			data = new FormData();
			data.left = new FormAttachment(content,0);
			data.width = 60;
			data.top = new FormAttachment(0,0);
			data.bottom = new FormAttachment(100,0);
			title.setLayoutData(data);
			//author check button
			final Button author = new Button(search,SWT.CHECK);
			author.setText("Author");
			data = new FormData();
			data.left = new FormAttachment(title,0);
			data.top = new FormAttachment(0,0);
			data.bottom = new FormAttachment(100,0);
			data.width = 80;
			author.setLayoutData(data);
			//search all feeds check button
			final Button allFeeds = new Button(search,SWT.CHECK);
			allFeeds.setText("All Feeds");
			data = new FormData();
			data.left = new FormAttachment(author,0);
			data.top = new FormAttachment(0,0);
			data.bottom = new FormAttachment(100,0);
			data.width = 80;
			allFeeds.setLayoutData(data);
			//search label
			CLabel searchLabel = new CLabel(search, SWT.BOLD);
			searchLabel.setText("Search");
			try{
				searchLabel.setImage(new Image(null, new FileInputStream("images/search.png")));
			}catch(Exception e){
			}
			data = new FormData();
			data.left = new FormAttachment(allFeeds,5);
			data.top = new FormAttachment(0,0);
			data.bottom = new FormAttachment(100,0);
			searchLabel.setLayoutData(data);
			//search text field
			final Text searchText = new Text(search,SWT.SINGLE|SWT.BORDER);
			search.setLayout(new FormLayout());
			data = new FormData();
			data.right = new FormAttachment(100,0);
			data.left = new FormAttachment(searchLabel,5);
			data.top = new FormAttachment(0,0);
			data.bottom = new FormAttachment(100,0);
			searchText.setLayoutData(data);
			//adds a listener to perform search
			searchText.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					//whenever the text is modified, perform the next search.  asyncExec is used so the GUI doesn't wait
					Display.getCurrent().asyncExec(
						new Runnable(){
							public void run() {
								if (allFeeds.getSelection()){
									//if all feeds is checked then search all feeds and set the table to the resulting articles
									List<Article> arts = control.searchArticles(author.getSelection(), title.getSelection(), content.getSelection(), searchText.getText());
									tableViewer.setInput(arts);
								}else{
									//otherwise just search feed
									try{
										List<Article> arts = control.searchArticles(author.getSelection(), title.getSelection(), content.getSelection(), getSelectedFeed(),searchText.getText());
										tableViewer.setInput(arts);
									}catch(NoItemSelectedException e){
										tableViewer.setInput(new ArrayList<Article>());
									}
								}
							}
						}
					);
				}
			});
		}
		
		//makes the form data for the search composite
		data = new FormData();
		data.left = new FormAttachment(vertSash,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(0,0);
		data.height = 24;
		search.setLayoutData(data);
		
		//constructs the web browser
		browser = new Browser(composite, SWT.BORDER);
		data = new FormData();
		data.left = new FormAttachment(vertSash,0);
		data.top = new FormAttachment(horiSash,0);
		data.right = new FormAttachment(100,0);
		data.bottom = new FormAttachment(100,0);
		browser.setLayoutData(data);
		
		//references to the underlying tree and table objects
		final Table table = tableViewer.getTable();
		final Tree tree = treeViewer.getTree();
		
		//make adjustments to the table
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		data = new FormData();
		data.left = new FormAttachment(vertSash,0);
		data.right = new FormAttachment(100,0);
		data.top = new FormAttachment(search,0);
		data.bottom = new FormAttachment(horiSash,0);
		table.setLayoutData(data);
		
		//creates the columns, with selection listeners to sort them
		TableColumn tc = new TableColumn(table,SWT.LEFT,ColumnConstants.READ_STATUS);
		tc.setText("Read");
		tc.setWidth(50);
		tc.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				((ArticleViewerSorter) tableViewer.getSorter())
					.doSort(ColumnConstants.READ_STATUS);
				tableViewer.refresh();
			}
		});
		
		tc = new TableColumn(table,SWT.LEFT,ColumnConstants.DATE);
		tc.setText("Date");
		tc.setWidth(100);
		tc.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				((ArticleViewerSorter) tableViewer.getSorter())
					.doSort(ColumnConstants.DATE);
				tableViewer.refresh();
			}
		});
		
		tc = new TableColumn(table,SWT.LEFT,ColumnConstants.TITLE);
		tc.setText("Title");
		tc.setWidth(100);
		tc.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				((ArticleViewerSorter) tableViewer.getSorter())
					.doSort(ColumnConstants.TITLE);
				tableViewer.refresh();
			}
		});
		
		tc = new TableColumn(table,SWT.LEFT,ColumnConstants.AUTHOR);
		tc.setText("Author");
		tc.setWidth(50);
		tc.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				((ArticleViewerSorter) tableViewer.getSorter())
				.doSort(ColumnConstants.AUTHOR);
				tableViewer.refresh();
			}
		});

		//adds a listener to pack columns of table appropriately
		tree.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {
				for (TableColumn tc:table.getColumns()){
					tc.pack();
				}
			}
			public void widgetSelected(SelectionEvent e) {
				for (TableColumn tc:table.getColumns()){
					tc.pack();
				}
			}
		});

		//when an article is selected its summary is shown in the browser window.
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event){
				if(event.getSelection().isEmpty()) {
					return;
				}
				if(event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					Object o = selection.getFirstElement();
					if (o instanceof Article){
						//when article is clicked
						//changes browswer text to summary
						Article a = (Article) o;
						browser.setText(Util.generateHTML(a));
						try{
							//set the article as read
							control.markRead(a);
							a.markRead(true);
							//update the view
							updateArticle(a);
							Feed feed = getSelectedFeed();
							//update the feed's view to decrement the number of new articles if appropriate
							updateFeed(feed);
							//treeViewer.refresh(feed);
						}catch(ControlException e){
						}catch(NoItemSelectedException e){
						}
					}
				}
			}
		});
		//KeyListeners to handle deletions
		table.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) {
				//if delete key is pressed, trash selected articles
				if(e.keyCode==0x7f){
					trashAction.run();
				}
			}
			public void keyReleased(KeyEvent e) {
			}
		});
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
						//if the selected item is a feed, update the table
						Feed f = (Feed) o;
						try{
							mostRecentFeed = f;
							List<Article> articles = control.getArticles(f);
							//special actions for the trash and outbox
							if (f.getId() == control.getTrash().getId()){
								//if its trash, make the read column small
								tableViewer.setInput(articles);
								TableColumn tc =tableViewer.getTable().getColumn(0); 
								tc.setText("");
								tc.setWidth(0);
								tc.setResizable(false);
							}else if (f.getId()==control.getOutbox().getId()){
								//if its outbox, make the read column small
								tableViewer.setInput(articles);
								tableViewer.setInput(articles);
								TableColumn tc =tableViewer.getTable().getColumn(0); 
								tc.setText("");
								tc.setWidth(0);
								tc.setResizable(false);
							}
							else{
								//else make the columns normal sized
								tableViewer.setInput(articles);
								TableColumn tc =tableViewer.getTable().getColumn(0); 
								tc.setText("Read");
								tc.setWidth(50);
								tc.setResizable(true);
								tableViewer.setInput(articles);
							}
							tableViewer.setInput(articles);
						}catch(ControlException e){
							control.setStatus(e.getMessage());
						}
					}else if (o instanceof Folder){
						//do nothing if its a folder
					}
				}
			}
		});
		
		///edits the right click menu to add appropriate actions
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
		
		//makes the context menu for the table
		final MenuManager tableMenuManager = new MenuManager();
		tableMenuManager.addMenuListener(new IMenuListener(){
			public void menuAboutToShow(IMenuManager arg0) {
				//remove all actions
				tableMenuManager.removeAll();
				if(tableViewer.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
					if (selection.size()==1){
						Object o = selection.getFirstElement();
						if (o instanceof Article){
							//set actions based on if article is in trash, outbox, or normal feed
							Article a = (Article) o;
							try{
								if (getSelectedFeed().getId()==control.getTrash().getId()){
									tableMenuManager.add(unTrashAction);
								}
								else if (getSelectedFeed().getId()==control.getOutbox().getId()){
									 tableMenuManager.add(trashAction);
								}else{
									if (a.getRead())
										tableMenuManager.add(markNewAction);
									else tableMenuManager.add(markReadAction);
									tableMenuManager.add(trashAction);
									tableMenuManager.add(addToOutbox);
								}
							}catch(ControlException e){
							}catch(NoItemSelectedException e){
							}
							tableMenuManager.add(diggAction);
							tableMenuManager.add(deliciousAction);
						}
					}
					//different actions if multiple selected.  
					else if (selection.size()>1){
						try{
							//different actions for articles from trash, outbox, and normal feeds
							if (getSelectedFeed().getId()==control.getTrash().getId()){
								tableMenuManager.add(unTrashAction);
							}
							else if (getSelectedFeed().getId()==control.getOutbox().getId()){
								tableMenuManager.add(trashAction);
							}else{
								tableMenuManager.add(markNewAction);
								tableMenuManager.add(markReadAction);
								tableMenuManager.add(trashAction);
								tableMenuManager.add(addToOutbox);
							}
						}catch(ControlException e){
						}catch(NoItemSelectedException e){
						}
					}
				}
			}
		});
		//sets the menu
		table.setMenu(tableMenuManager.createContextMenu(table));
		return composite;
	}
	//methods to implement view interface
	
	/**
	 * The update method is called when the Controller wants the view to refresh.  
	 * Refreshes the current view entirely;
	 *
	 */
	public void update(){
		updateTable(); //update table
		treeViewer.refresh(); //update tree
	}
	/**
	 * Updates the table
	 */
	private void updateTable(){
		try{
			List<Article> articles = control.getArticles(getSelectedFeed());//get appropriate articles
			tableViewer.setInput(articles); //update table
			tableViewer.refresh();
		}catch(Exception e){
			tableViewer.setInput(new ArrayList<Article>());
		}
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
	 * gets the currently selected article in the view.
	 * @return Article selected article.
	 * @throws NoItemSelectedException If no article is currently selected.
	 */
	public Article getSelectedArticle() throws NoItemSelectedException{
		if(tableViewer.getSelection() instanceof IStructuredSelection) {
	           IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
	           Object o = selection.getFirstElement();
	           if (o instanceof Article){
	        	   return (Article) o;
	           }
	       }
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
	 * Returns a list of all selected articles
	 * @return List of articles that are selected
	 * @throws NoItemSelectedException if no articles are selected
	 */
	public List<Article> getSelectedArticles() throws NoItemSelectedException{
		if(tableViewer.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
			List<Article> arts = new ArrayList<Article>();
			Iterator iter = selection.iterator();
			//iterates through selection and adds articles to return list
			while(iter.hasNext()){
				Object o = iter.next();
				if (o instanceof Article){
					arts.add((Article) o);
				}
			}
			//if any articles were selected, then return, otherwise, throw exception
			if (arts.size()>0)
				return arts;
		}
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
	 * Updates the view of the articles of the specified feed.
	 * @param f Feed
	 */
	public void updateArticles(Feed f){
		try{
			tableViewer.setInput(control.getArticles(f));
			tableViewer.refresh();
		}catch(ControlException e){
		}
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
	 * Updates the view of a particular article in the table
	 * @param a Article
	 */
	public void updateArticle(Article a){
		tableViewer.update(a, null);
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
