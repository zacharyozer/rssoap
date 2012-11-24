package tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import model.Article;
import model.Feed;
import model.Folder;
import control.ControlException;
import control.Controller;
import derby.DBHandler;
import derby.DBHandlerException;

public class ControllerTest extends TestCase {
	Controller controller;
	Feed feed;
	public ControllerTest(String name){
		super(name);
	}
	public void setup() {
		try{
			controller = new Controller(new DBHandler());
		}catch(DBHandlerException e){
			fail("controller wasn't instantiated");
			//something went very wrong
		}
	}
	public void testGetApp(){
		setup();
		assertTrue("getApp() should return currently running instance of controller",controller==Controller.getApp());
	}
	
	public void testAddStuff(){
		setup();
		List<Object> folders = new ArrayList<Object>(); // instantiating to keep Eclipse from being sad; this does nothing 
		Folder root;
		try {
			folders = controller.getRoot();
		}
		catch (ControlException e) {
			fail("Error getting root: "+e);
		}
		
		for (Object o : folders) {
			if (o instanceof Folder) {
				root = (Folder) o;
				try {
					feed = controller.subscribe("http://wt.mit.edu/rss/news.xml", root);
				}
				catch (ControlException e) {
					fail("Encountered error while trying to subscribe to a feed in the main folder: "+e);
				}
			}
		}
		
	}
	
	public void testGetStuff(){
		setup();
		//gets the root folders
		try{
			List<Object> rootFolders = controller.getRoot();
			for (Object o : rootFolders){
				if (o instanceof Folder) {
					Folder f = (Folder) o;
					for (Folder child:controller.getChildrenFolders(f)){
						assertTrue("Folder's child's parent should be folder",controller.getParent(child).getId()==f.getId()); }
					for (Feed child:controller.getChildrenFeeds(f)){
						assertTrue("Folder's child's parent should be folder",controller.getParent(child).getId()==f.getId());
					}
				}
				else if (o instanceof Feed) {
					Feed f = (Feed) o;
					assertTrue("Root folder should contain only two feeds: outbox and trash", (f.getTitle().equals("Trash") || f.getTitle().equals("Outbox")));  
				}
				else {
					fail("Root folder should only contain feeds and folders");
				}
			}
			//tests that getArticle random returns articles
			for(int i=0;i<50;i++){
				assertTrue("getRandomArticle should return articles",controller.getArticleRandom() instanceof Article);
			}
			//tests that every articles parent is its appropriate feed.
			for(Feed feed:controller.getAllFeeds()){
				for (Article a:controller.getArticles(feed)){
					assertTrue("article's parent should be appropriate feed",feed.getId()==controller.getParent(a).getId());
				}
			}
			
		}catch(ControlException e){
			fail("assertion failed: "+ e);
		}

	}

	public void testUnsubscribe(){
		setup();
		try {
			List<Feed> feeds = controller.getAllFeeds();
			for (Feed feed : feeds) {
				if (feed.getUrl().equals("http://wt.mit.edu/rss/news.xml"))
					controller.unSubscribe(feed);
			}
		}
		catch (ControlException e) {
			fail("Controller failed to unsubscribe from a feed :"+e);
		}		
	}	
	
	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new ControllerTest("testGetApp"));
		suite.addTest(new ControllerTest("testAddStuff"));
		suite.addTest(new ControllerTest("testGetStuff"));
		suite.addTest(new ControllerTest("testUnsubscribe"));
		return suite;
	}
	
}
