package tests;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import model.Article;
import model.Feed;

/**
 * JUnit test case for FeedTest
 */

public class FeedTest extends TestCase {
	//declare reusable objects to be used across multiple tests
	public FeedTest(String name) {
		super(name);
	}
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(FeedTest.class);
	}
	
	public static Test suite() {
		return new TestSuite(FeedTest.class);
	}

	String title1,description1,url1;
	Date updatedByFeed1,updatedByUser1,publishedDate1;
	String title2,description2,url2;
	Date updatedByFeed2,updatedByUser2,publishedDate2;
	int id2,parentid2,updateInterval2;
	Feed smallTestFeed, largeTestFeed, smallNullFeed, largeNullFeed;
	
	protected void setUp() {
		title1 = "Feed's title";
		description1 = "Feed's summary";
		url1 = "http://feed'surl.com/";
		updatedByFeed1 = Calendar.getInstance().getTime();
		updatedByUser1 = updatedByFeed1;
		publishedDate1 = updatedByFeed1;
		smallTestFeed = new Feed(title1, description1, url1, updatedByFeed1, updatedByUser1, publishedDate1);

		id2 = 121212;
		parentid2 = 1;
		title2 = "second feed title";
		description2 = "second description title";
		url2 = "http://test-url-location.com/";
		updatedByFeed2 = Calendar.getInstance().getTime();
		updatedByUser2 = updatedByFeed2;
		publishedDate2 = updatedByFeed2;
		updateInterval2 = 100;
		largeTestFeed = new Feed(id2, parentid2, title2, description2, url2, updatedByFeed2, updatedByUser2, publishedDate2, updateInterval2);
		
		smallNullFeed = new Feed(null, null, null, null, null, null);
		largeNullFeed = new Feed(0, 0, null, null, null, null, null, null, 0);
	}
	
	protected void tearDown() {
		
	}

	public void testFeedConstructor() {
		assertNotNull("Feed should not be null after construction",largeTestFeed);
		assertNotNull("Feed should not be null after construction",smallTestFeed);		
		assertNotNull("Feed should not be null after construction",largeNullFeed);
		assertNotNull("Feed should not be null after construction",smallNullFeed);
	}
	
	public void testGetters() {
		 
		assertEquals("Small null feed should have the appropriate empty fields",smallNullFeed.getDescription(), "");
		assertEquals("Small null feed should have the appropriate empty fields",smallNullFeed.getTitle(), "");
		assertEquals("Small null feed should have the appropriate empty fields",smallNullFeed.getUrl(), "");
		assertEquals("Small null feed should have the appropriate empty fields",smallNullFeed.getLastUpdatedByFeed(), new Date());
		assertEquals("Small null feed should have the appropriate empty fields",smallNullFeed.getLastUpdatedByUser(), new Date());
		assertEquals("Small null feed should have the appropriate empty fields",smallNullFeed.getPublishedDate(), new Date());
		try {
			smallNullFeed.getId();
			fail("Did not throw expected IllegalAccessError on getting an unset field of a small feed");
		}
		catch (IllegalAccessError e) {}
		try {
			smallNullFeed.getParentId();
			fail("Did not throw expected IllegalAccessError on getting an unset field of a small feed");
		}
		catch (IllegalAccessError e) {}

		try {
			smallNullFeed.getUpdateInterval();
			fail("Did not throw expected IllegalAccessError on getting an unset field of a small feed");
		}
		catch (IllegalAccessError e) {}
		
		assertEquals("Large null feed should have the appropriate empty fields", largeNullFeed.getDescription(), "");
		assertEquals("Large null feed should have the appropriate empty fields", largeNullFeed.getTitle(), "");
		assertEquals("Large null feed should have the appropriate empty fields", largeNullFeed.getUrl(), "");
		assertEquals("Large null feed should have the appropriate empty fields", largeNullFeed.getLastUpdatedByFeed(), new Date());
		assertEquals("Large null feed should have the appropriate empty fields", largeNullFeed.getLastUpdatedByUser(), new Date());
		assertEquals("Large null feed should have the appropriate empty fields", largeNullFeed.getPublishedDate(), new Date());
		try {
			assertEquals("Large null field should have the appropriate zeroed fields", 0, largeNullFeed.getId());
			assertEquals("Large null field should have the appropriate zeroed fields", 0, largeNullFeed.getParentId());
			assertEquals("Large null field should have the appropriate zeroed fields", 0, largeNullFeed.getUpdateInterval());
		}
		catch (Exception e) {
			fail("Encountered exception trying to get zeroed fields of a large null feed :"+e);
		}
		
		assertEquals("Small test article has the appropriate fields", title1, smallTestFeed.getTitle());
		assertEquals("Small test article has the appropriate fields", description1, smallTestFeed.getDescription());
		assertEquals("Small test article has the appropriate fields", url1, smallTestFeed.getUrl());
		assertEquals("Small test article has the appropriate fields", updatedByFeed1, smallNullFeed.getLastUpdatedByFeed());
		assertEquals("Small test article has the appropriate fields", updatedByUser1, smallNullFeed.getLastUpdatedByUser());
		assertEquals("Small test article has the appropriate fields", publishedDate1, smallNullFeed.getPublishedDate());
		try {
			smallTestFeed.getId();
			fail("Did not throw expected IllegalAccessError on getting an unset field of a small feed");
		}
		catch (IllegalAccessError e) {}
		try {
			smallTestFeed.getParentId();
			fail("Did not throw expected IllegalAccessError on getting an unset field of a small feed");
		}
		catch (IllegalAccessError e) {}

		try {
			smallTestFeed.getUpdateInterval();
			fail("Did not throw expected IllegalAccessError on getting an unset field of a small feed");
		}
		catch (IllegalAccessError e) {}
		 
		assertEquals("Large test feed should have the fields it was constructed with", title2, largeTestFeed.getTitle());
		assertEquals("Large test feed should have the fields it was constructed with", description2, largeTestFeed.getDescription());
		assertEquals("Large test feed should have the fields it was constructed with", url2, largeTestFeed.getUrl());
		assertEquals("Large test feed should have the fields it was constructed with", updatedByFeed2, largeTestFeed.getLastUpdatedByFeed());
		assertEquals("Large test feed should have the fields it was constructed with", updatedByUser2, largeTestFeed.getLastUpdatedByUser());
		assertEquals("Large test feed should have the fields it was constructed with", publishedDate2, largeTestFeed.getPublishedDate());
		try {
			assertEquals("Large test feed should have the fields it was constructed with", id2, largeTestFeed.getId());
			assertEquals("Large test feed should have the fields it was constructed with", parentid2, largeTestFeed.getParentId());
			assertEquals("Large test feed should have the fields it was constructed with", updateInterval2, largeTestFeed.getUpdateInterval());
		}
		catch (Exception e) {
			fail("Encountered error while getting fields of a large test feed: "+e);
		}		
	}

	public void testSetTitle() {
		largeTestFeed.setTitle("New title");
		assertEquals("A feed should report it has the correct new title after being set", "New title", largeTestFeed.getTitle());
		largeTestFeed.setTitle(null);
		assertEquals("Setting a title to null should result in its becoming the empty string", "", largeTestFeed.getTitle());
	}
	
	public void testIsSame() {
		assertFalse("Two feeds which do not have the same URL should not report that they are the same", Feed.isSame(largeTestFeed, smallTestFeed));
		assertTrue("Two feeds which both have empty URLs should be considered the same by isSame", Feed.isSame(smallNullFeed, largeNullFeed));
		assertTrue("Two feeds with the same URL should be treated as the same feed", Feed.isSame(largeTestFeed, new Feed(15, 25, "arbitrary title", "nonsense description", url2, Calendar.getInstance().getTime(), Calendar.getInstance().getTime(), Calendar.getInstance().getTime(), 0)));
	}
	
	public void testAdders() {
		assertFalse("A new large feed should not know its children", largeTestFeed.knowsChildren());
		assertFalse("A new small feed should not know its children", smallTestFeed.knowsChildren());
		Article smallArticle = new Article("author", "title", "http://url!", "summary", new Date());
		Article largeArticle = new Article(10, 11, "authorlarge", "titlelarge", "urllarge", "summarylarge", new Date(), false, false, false, 0);
		ArrayList<Article> articles = new ArrayList<Article>();
		articles.add(smallArticle);
		articles.add(largeArticle);
		largeTestFeed.addArticle(smallArticle);
		assertTrue("A large feed with things added to it should know its children", largeTestFeed.knowsChildren());
		try {
			assertEquals("A large feed with one article added should have one child", 1, largeTestFeed.getChildren().size());
		}
		catch (Exception e) { fail("Encountered exception while getting a large feed's children: "+e); }
		largeTestFeed.addArticles(articles);
		try {
			assertEquals("Identical articles should not be added to a feed twice.", 2, largeTestFeed.getChildren().size());
		}
		catch (Exception e) { fail("Encountered exception while getting a large feed's children: "+e); }
		smallTestFeed.addArticles(articles);
		assertTrue("After adding articles, a small feed should know its children", smallTestFeed.knowsChildren());
		try {
			assertEquals("After adding two articles, a small feed should contain two children", 2, smallTestFeed.getChildren().size());
		}
		catch (Exception e) {fail("Encountered exception while getting a small feed's children: "+e); }
		
		try {
			largeTestFeed.addArticle(null);
			fail("Adding null instead of an article should yield a null pointer exception");
		}
		catch (NullPointerException n) {}
		catch (Exception e) { fail("Encountered unexpected exception while adding a null article to a feed: "+e); }

		try {
			largeTestFeed.addArticles(null);
			fail("Adding null instead of a list of articles should yield a null pointer exception");
		}
		catch (NullPointerException n) {}
		catch (Exception e) { fail("Encountered unexpected exception while adding a null list of articles to a feed: "+e); }
	}
	
	public void testRemove() {
		Article smallArticle = new Article("author", "title", "http://url!", "summary", new Date());
		Article largeArticle = new Article(10, 11, "authorlarge", "titlelarge", "urllarge", "summarylarge", new Date(), false, false, false, 0);
		ArrayList<Article> articles = new ArrayList<Article>();
		assertTrue("Adding an article yields true", articles.add(smallArticle));
		assertTrue("Adding an article yields true", articles.add(largeArticle));
		largeTestFeed.addArticles(articles);
		assertTrue("Removing an article yields true", largeTestFeed.removeArticle(smallArticle));
		try {
			assertEquals("After removing an article, feed should have the right number of elements", 1, largeTestFeed.getChildren().size());
			assertEquals("After removing an article, the correct article should remain", largeArticle, largeTestFeed.getChildren().get(0));
		}
		catch (Exception e) {
			fail("Encountered unexpected exception while removing an article from a feed: "+e);
		}
		try {
			assertFalse("Failing to remove a nonexistent article should return false", largeTestFeed.removeArticle(smallArticle));
		}
		catch (Exception e) {
			fail("Encountered unexpected exception when removing a nonexistent article: "+e);
		}
		
		try {
			largeTestFeed.removeArticle(null);
			fail("Removing null in place of an article should yield a null pointer exception");
		}
		catch (NullPointerException n) {}
		catch (Exception e) { fail("Encountered an unexpected exception when trying to remove a null article: "+e); }
		
	}



}