package tests;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import model.Article;
import model.Feed;
import model.Folder;
import derby.DBHandler;
import derby.DBHandlerException;

/**
 * JUnit test case for DBHandlerTest
 */

public class DBHandlerTest extends TestCase {
	static DBHandler dbh;
	Date testDate = new Date(100);
	Date testDate2 = new Date(200);
	Feed testFeed = new Feed("Feed's title", "Feed's description", "http://this-feeds-url.com/over'here", testDate, testDate, testDate);
	Feed testFeed2 = new Feed("Feed's title 2", "Feed's description 2", "http://this-feeds-url.com/over'here2", testDate2, testDate2, testDate2);

	public DBHandlerTest(String name) {
		super(name);
	}
	public static void main(String[] args) {
		junit.textui.TestRunner.run(DBHandlerTest.class);
	}
	
	public static Test suite() {
		return new TestSuite(DBHandlerTest.class);
	}
	
	protected void setUp() {
	}
	
	protected void tearDown() {
	}
	
	public void testStart() throws Throwable {
		dbh = new DBHandler();
		dbh = new DBHandler();
	}
	
	public void testDBHandlerConstructor() {
		assertNotNull("DBHandler should != null after call to constructor", dbh);
		try {
			assertEquals("Size of DB on contruction should be 3 things", 3, dbh.getRoot().size());
			Folder f1 = (Folder) dbh.getRoot().get(0);
			Feed f2 = (Feed) dbh.getRoot().get(1);
			Feed f3 = (Feed) dbh.getRoot().get(2);
			assertEquals("Folder 1 must be subscribed feeds", "Subscribed Feeds", f1.getTitle());
			assertEquals("Folder 2 must be the outbox", "Outbox", f2.getTitle());
			assertEquals("Folder 3 must be the trash", "Trash", f3.getTitle());
		} catch (Exception e){
			e.printStackTrace();
			fail("Encountered error during empty constructor");
		}
	}

	public void testShutdown() {
		try {
			assertTrue("Database should shut down properly and return true when it does so", dbh.shutdown());
		} catch (Throwable e) {
			fail("Database shutdown failed: " + e);
		}

		try {
			dbh.shutdown();
			fail("Shutting down twice should raise DBHandlerException");
		} catch (DBHandlerException e) {
		} catch (Throwable e) {
			fail("Failed with:" + e);
		}
		try {
			dbh = new DBHandler();
		}
		catch (Exception e) {
			fail("Starting up database after shutting it down should work properly.");
		}
	}	
	
	public void testAddFolder() {
		try {
			dbh.resetDB();
			Folder f = new Folder("Test's folder!");
			Folder root = (Folder) dbh.getRoot().get(0);
			Folder g = dbh.addFolder(root, f);
			assertEquals("A newly added folder should have the right title.", f.getTitle(), dbh.getChildrenFolders(g).get(0).getTitle()); 
			assertEquals("A folder added to the root should have the right parent id", root.getId(), g.getId());
			Folder h = dbh.addFolder(g, new Folder("Test's second folder"));
			assertEquals("A folder added to a secondary folder should have the right title", "Test's folder!", dbh.getChildrenFolders(h).get(0).getTitle());
		} catch (Throwable e) {
			fail("Failed with:" + e);
		}

//		try {
//			Folder f = new Folder("Test's folder!");
//			dbh.addFolder(dbh.getRoot().get(2), f);
//			fail("You shouldn't be able to add a folder to the trash.");
//		} catch (DBHandlerException e) {
//		} catch (Throwable e) {
//			fail("Adding folder to trash failed in an unexpected way: " + e);
//		}		
	}

	public void testAddFeed() {
		try {
			Article articleOne = new Article("Test author's name", "Test article's title", "http://articles-url.com/article'surl/", "Test article's summary", testDate);
			testFeed.addArticle(articleOne);
			Folder root = (Folder) dbh.getRoot().get(0);
			Feed b = dbh.addFeed(testFeed, root);
			assertEquals("Feed should be added to the folder it thinks it is", root.getId(), b.getParentId());
		} catch (Exception e) {
			fail("Adding a feed to a folder yields an exception: " + e);
		}

//		try {
//			dbh.addFeed(testFeed2, dbh.getRoot().get(2));
//			fail("Adding a feed to the trash should cause an exception.");
//		} catch (DBHandlerException e) {
//		} catch (Throwable e) {
//			fail("Adding a feed to the trash caused an unexpected exception: " + e);
//		}
	}


	public void testGetFeedsandGetArticlesbyFeed() {
		try {
			dbh.resetDB();
			Folder root = (Folder) dbh.getRoot().get(0);
			Article articleOne = new Article("Test author's name", "Test article's title", "http://articles-url.com/article'surl/", "Test article's summary", testDate);
			testFeed.addArticle(articleOne);
			Feed f = dbh.addFeed(testFeed, root);
			//assertEquals("Feed with no articles should have zero articles!", 0, dbh.getArticlesbyFeed(f).size());
			//dbh.updateFeed(f);
			List<Article> articleList = dbh.getArticlesbyFeed(f);
			assertEquals("Feed with one article added should have exactly one", 1, articleList.size());
			assertEquals("Feed with one article added has correct title for that article", "Test article's title", articleList.get(0).getTitle());

			Article articleTwo = new Article("Test author's name", "Test article's second title", "http://articles-url.com/article2'surl/", "Test article's second summary", testDate2);
			f.addArticle(articleTwo);
			dbh.updateFeed(f);
			assertEquals("Adding two articles to a feed should yield a list of two articles in that feed", 2, dbh.getArticlesbyFeed(f).size()); 
			articleList = dbh.getArticlesbyFeed(f);
			testFeed2.addArticle(articleTwo);
		}
		catch (Exception e)
		{
			fail("Encountered exception while getting articles by feed: " +e);
		}

		try {
			Feed f = new Feed("Fictional feed", "does not exist", "http://dev-null.net/", testDate, testDate, testDate);
			dbh.getArticlesbyFeed(f);
			fail("Should raise an error when getting articles by a feed that does not exist.");
		} catch (DBHandlerException e) {}
		catch (IllegalAccessError e) {}
		catch (Throwable e) {
			fail("Getting articles by a nonexistent feed failed in an unexpected way: " + e);
		}
		
		try {
			Feed f = testFeed2;
			Feed g = new Feed(f.getTitle(),f.getDescription(),f.getUrl(), f.getLastUpdatedByFeed(), f.getLastUpdatedByUser(), f.getPublishedDate());
			dbh.getArticlesbyFeed(g);
			fail("Should not successfully get articles by feed of an artificial feed (has no id)");
		}
		catch (DBHandlerException e) {}
		catch (IllegalAccessError e) {}
		catch (Exception e) {fail("Encountered an unexpected exception when getting articles by an artificial feed (has no ID, not from database): "+e); }
	}
	
	

	public void testGetArticlesbySearch() {
			try {
				assertFalse("Searching among test articles by author should yield some articles", dbh.searchArticles(true, false, false, "author").isEmpty());				
				assertFalse("Searching among test articles by title should yield some articles", dbh.searchArticles(false, true, false, "title").isEmpty());				
				assertFalse("Searching among test articles by summary should yield some articles", dbh.searchArticles(false, false, true, "summary").isEmpty());				

				assertTrue("Searching among test articles by author should yield no articles", dbh.searchArticles(false, true, true, "author").isEmpty());				
				assertTrue("Searching among test articles by title should yield no articles", dbh.searchArticles(true, false, true, "title").isEmpty());				
				assertTrue("Searching among test articles by summary should yield no articles", dbh.searchArticles(true, true, false, "summary").isEmpty());				
			}
			catch (DBHandlerException e) {
				fail("Encountered exception while searching among test articles: "+e);
			}
		
			try {
				dbh.searchArticles(true, true, true, null);
				fail("Should throw an error when searching for a null constraint");
			} catch (DBHandlerException e) {
			} catch (Throwable e) {
				fail("Failed with unexpected exception when searching with null constraint: " + e);
			}		
	}
	
	public void testGetArticleRandom() {
		try {
			Article a = dbh.getArticleRandom();
			assertNotNull("Article retrieved at random should not be null", a);
			assertFalse("Article retrieved at random should not be marked as read", a.getRead());
			assertFalse("Article retrieved at random should not be in the Trash", dbh.getTrashArticles().contains(a));
			boolean seen = false;
			List<Feed> listFeeds = dbh.getFeedsAll();
			try {
				for (Feed feed : listFeeds) {
				//System.out.println(feed.getTitle());
					List<Article> listArticles = dbh.getArticlesbyFeed(feed);
					for (Article article : listArticles) {
						if (a.isSame(article)) 
							seen = true;
					}
				}
			}
			catch (Exception e)
			{
				fail("Encountered error checking on getArticleRandom :" +e);
			}
			assertTrue("Article retrieved at random should exist somewhere in the database", seen);
		}
		catch (Exception e)
		{ fail("Encountered an exception during the course of getting a random article: "+e); }
	}
	
	public void testGetRoot() {
		try {
			List<Object> root = dbh.getRoot();
			Folder f1 = (Folder) root.get(0);
			Feed f2 = (Feed) root.get(1);
			Feed f3 = (Feed) root.get(2);
			
			assertEquals("Root should contain three things. And the number shall be three.",3,root.size());
			assertEquals("Root's first item should be Subscribed Feeds", "Subscribed Feeds", f1.getTitle());
			assertEquals("Root's second item should be Outbox", "Outbox", f2.getTitle());
			assertEquals("Root's third item should be Trash", "Trash", f3.getTitle());
		}
		catch (Exception e) {
			fail("Encountered exception getting root: "+e);
		}
	}
	
	public void testGetChildrenFeeds() {
			try {
				Folder root = (Folder) dbh.getRoot().get(0);
				List<Feed> feeds = dbh.getChildrenFeeds(root);
				assertNotNull("List of children feeds should not be null", feeds);
			}
			catch (Exception e)
			{
				fail("Failed to get children feeds of a folder: "+e);
			}

			try {
				dbh.getChildrenFeeds(new Folder("This folder does not exist"));
				fail("getChildrenFeeds should fail when trying to get children of a nonexistent folder");
			}
			catch (DBHandlerException e) {}
			catch (IllegalAccessError e) {}
			catch (Exception e) { fail("Encountered unexpected exception when getting children feeds of a folder that does not exist: "+e); }
		}

	public void testGetChildrenFolders() {
		try {
			Folder root = (Folder) dbh.getRoot().get(0);
			List<Folder> folders = dbh.getChildrenFolders(root);
			assertNotNull("List of children folders should not be null", folders);
		}
		catch (Exception e)
		{
			fail("Failed to get children folders of a folder: "+e);
		}

		try {
			dbh.getChildrenFolders(new Folder("This folder does not exist"));
			fail("getChildrenFolders should fail when trying to get children of a nonexistent folder");
		}
		catch (DBHandlerException e) {}
		catch (IllegalAccessError e) {}
		catch (Exception e) { fail("Encountered unexpected exception when getting children folders of a folder that does not exist: "+e); }

	}
	
	public void testGetParents() {
		try {
			Folder root = (Folder) dbh.getRoot().get(0);
			List<Feed> feeds = dbh.getChildrenFeeds(root);
			List<Article> articles = new ArrayList<Article>();
			for (Feed feed : feeds) {
				assertNotNull("Parent of a feed in the database should not be null", dbh.getParent(feed));
				for (Article article : dbh.getArticlesbyFeed(feed)) {
					articles.add(article);
					assertEquals("All articles which are the children of a feed should have that feed as their parent", dbh.getParent(article).getId(), feed.getId());
				}
			List<Folder> folders = dbh.getChildrenFolders(root);
			for (Folder folder : folders) {
				assertEquals("All folders which are the children of a folder should have that folder as their getParent parent", dbh.getParent(folder).getId(), root.getId());
			}

			}
		} catch (Exception e) 
		{ fail("Getting parents threw an unexpected exception: " +e);
		}
	}


public void testTrash() {
	try {
		dbh.resetDB();
		assertEquals("A database with no trash articles should return an empty list when asked for them", 0, dbh.getTrashArticles().size());
		Article articleOne = new Article("Test author's name", "Test article's title", "http://articles-url.com/article'surl/", "Test article's summary", testDate);
		testFeed.addArticle(articleOne);
		Folder root = (Folder) dbh.getRoot().get(0);
		testFeed = dbh.addFeed(testFeed, root);
		articleOne = dbh.getArticlesbyFeed(testFeed).get(0);
		
		dbh.setTrash(articleOne);
		assertEquals("A database with one trash article should correctly report it has one article", 1, dbh.getTrashArticles().size());
		assertEquals("A trashed item should appear in getTrashArticles but not in its feed", 0, dbh.getArticlesbyFeed(testFeed).size());
		dbh.setUnTrash(articleOne);
		assertEquals("After untrashing, the size of the trash should decrease", 0, dbh.getTrashArticles().size());
		assertEquals("After untrashing, an untrashed article should reppear in its feed", 1, dbh.getArticlesbyFeed(testFeed).size());
		dbh.setTrash(articleOne);
		dbh.setDelete(articleOne);
		assertEquals("After deletion, an article does not appear in trash", 0, dbh.getTrashArticles().size());
		dbh.setUnTrash(articleOne);
		assertEquals("After deletion, an article does not appear in original feed", 0, dbh.getArticlesbyFeed(testFeed).size());
	}
	catch (Exception e) {
		fail("Encountered exception while testing trash abilities: "+e);
	}
}
	
	public void testMetaData() {
		try {
			dbh.resetDB();	
			Folder root = (Folder) dbh.getRoot().get(0);
			Article testArticle = new Article("author", "title", "url", "summary", Calendar.getInstance().getTime());
			testFeed.addArticle(testArticle);
			Feed f = dbh.addFeed(testFeed, root);
			Article a = dbh.getArticlesbyFeed(f).get(0);
			dbh.markRead(a);
			assertTrue("Marking an article as read should make it read", dbh.getArticlesbyFeed(f).get(0).getRead());
			dbh.markUnread(a);
			assertFalse("Marking an article as unread should make it unread", dbh.getArticlesbyFeed(f).get(0).getRead());
		}
		catch (Exception e) {
			fail("Encountered exception while testing metadata operations: "+e);
		}
		
	}
	
	public void testMoveOperations() {
		try {
			dbh.resetDB();
			Folder root = (Folder) dbh.getRoot().get(0);
			Article testArticle = new Article("author", "title", "url", "summary", Calendar.getInstance().getTime());
			testFeed.addArticle(testArticle);
			testFeed = dbh.addFeed(testFeed, root);
			dbh.addFolder(root, new Folder("Test folder one"));
			Folder added = dbh.getChildrenFolders(root).get(0);
			assertEquals("An added folder should have the correct name", "Test folder one", added.getTitle());
			dbh.addFolder(added, new Folder("Test folder two"));
			assertEquals("Folders should nest properly", "Test folder one", dbh.getParent(dbh.getChildrenFolders(added).get(0)).getTitle());
			dbh.moveFolder(dbh.getChildrenFolders(added).get(0), root);
			assertEquals("After moving a folder, it should exist in the new target place", 2, dbh.getChildrenFolders(root).size());
			assertEquals("After moving a folder, it should not exist in the old source place", 0, dbh.getChildrenFolders(added).size());
		}
		catch (Exception e)
		{
			fail("Encountered exception while testing move operations: "+e);
		}
		
		try {
			Folder root = (Folder) dbh.getRoot().get(0);
			Folder one = dbh.getChildrenFolders(root).get(0);
			try {
				dbh.moveFolder(one, new Folder("I don't actually exist"));
				dbh.moveFolder(new Folder("I don't exist"), one);
				fail("Did not encounter an error moving a folder to a place that does not exist");
				}
			catch (DBHandlerException e) {}
			catch (IllegalAccessError e) {}
			catch (Exception e) { fail("Encountered unexpected exception when moving a folder to a place that doesn't exist"); }
			try {
				dbh.moveFolder(null, one);
				dbh.moveFolder(one, null);
				dbh.moveFolder(null, null);
				fail("Encountered no errors moving folders that don't exist");
			}
			catch (NullPointerException n) {}
			catch (DBHandlerException e) {}
			catch (Exception e) {
				fail("Encountered unexpected exception while testing move operations on folders: "+e);
			}

			Folder folderone = dbh.getChildrenFolders(root).get(0);
			int feedid = testFeed.getId();
			dbh.moveFeed(testFeed,dbh.getChildrenFolders(root).get(0));
			assertEquals("After moving a feed, it should appear in the new folder", 1, dbh.getChildrenFeeds(folderone).size());
			assertEquals("After moving a feed, it should have the same id in its new location", feedid, dbh.getChildrenFeeds(folderone).get(0).getId());
			assertEquals("After moving a feed, it should no longer appear in the original location", 0, dbh.getChildrenFeeds(root).size());
			try {
				dbh.moveFeed(testFeed, null);
				dbh.moveFeed(null, root);
				dbh.moveFeed(null, null);
				fail("moveFeed should not successfully move null feeds or into null folders");
			}
			catch (NullPointerException n) {}
			catch (DBHandlerException e) {}
			catch (Exception e) {
				fail("Encountered unexpected exception during null tests with moveFeed: "+e);
			}
			
		}
		catch (DBHandlerException e)
		{
			fail("Encountered unexpected exception: "+e);
		}
	}

	public void testCopyArticle() {
		try {
			dbh.resetDB();
			Folder root = (Folder) dbh.getRoot().get(0);
			Article testArticle = new Article("author", "title", "url", "summary", Calendar.getInstance().getTime());
			testFeed.addArticle(testArticle);
			testFeed = dbh.addFeed(testFeed, root);
			Article testArticle2 = new Article("Testing", "Test", "Test", "test", Calendar.getInstance().getTime());
			testFeed2.addArticle(testArticle2);
			testFeed2 = dbh.addFeed(testFeed2, root);
			
			dbh.copyArticle(testArticle, testFeed2);
			assertEquals("After copying an article, it should exist in the origin place", 1,dbh.getArticlesbyFeed(testFeed).size());
			assertEquals("After copying an article, it should exist in the destination", 2,dbh.getArticlesbyFeed(testFeed2).size());
			assertFalse("A copied article should have a different id in the source as in the destination", (dbh.getArticlesbyFeed(testFeed2).get(1).getId() == dbh.getArticlesbyFeed(testFeed).get(0).getId()));
					
		}
		catch (Exception e)
		{
			fail("Encountered exception testing article copying: "+e);
		}
		
		
	}

	public void testDeleteOperations() {
		try {
			dbh.resetDB();
			Folder root = (Folder) dbh.getRoot().get(0);
			Folder folderone = new Folder("Title");
			dbh.addFolder(root, folderone);
			folderone = dbh.getChildrenFolders(root).get(0);

			Article testArticle = new Article("author", "title", "url", "summary", Calendar.getInstance().getTime());
			testFeed.addArticle(testArticle);
			testFeed = dbh.addFeed(testFeed, folderone);
			Article testArticle2 = new Article("Testing", "Test", "Test", "test", Calendar.getInstance().getTime());
			testFeed2.addArticle(testArticle2);
			testFeed2 = dbh.addFeed(testFeed2, folderone);
			
			dbh.unSubscribe(testFeed2);
			assertEquals("After unsubscribing from a feed, it should no exist as a child of its parent folder", 1, dbh.getChildrenFeeds(folderone).size());
			try {
				dbh.getArticlesbyFeed(testFeed2);
				fail("After unsubscribing from a feed, it should not be possible to get its articles");
			}
			catch (DBHandlerException e) {}
			catch (Exception e) {
				fail("Encountered unexpected exception while getting articles from an unsubscribed feed: "+e);
			}
			dbh.deleteFolder(folderone);
			
			assertEquals("After deleting a folder, its feeds should be gone", 2, dbh.getFeedsAll().size()); // (Only trash and outbox remain.)
			try {
				dbh.getArticlesbyFeed(testFeed);
				dbh.getArticleRandom();
				fail("After deleting a folder that indirectly contains all articles in the database, should not be able to look up a feed's articles or get a random article");  
			}
			catch (DBHandlerException e) {}
			catch (Exception e) {
				fail("Encountered unexpected exception looking up articles after deleting a folder: "+e);
			}
		}
		catch (Exception e)
		{
			fail("Encountered unexpected exception while testing delete operations: "+e);
		}
	}
}