package tests;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.rssoap.model.Article;

/**
 * JUnit test case for ArticleTest
 */

public class ArticleTest extends TestCase {
	Article largeNullArticle, largeTestArticle, smallNullArticle, smallTestArticle;
	public ArticleTest(String name) {
		super(name);
	}
	public static void main(String[] args) {
		junit.textui.TestRunner.run(ArticleTest.class);
	}
	public static Test suite() {
		return new TestSuite(ArticleTest.class);
	}
	protected void setUp() {
		String author = null;
		String title = null;
		String url = null;
		String summary = null;
		Date pubdate = null;
		smallNullArticle = new Article(author, title, url, summary, pubdate);
		
		author = null;
		title = null;
		url = null;
		summary = null;
		pubdate = null;
		boolean read = false;
		int rating = 0;
		int id = 0;
		int parentid = 0;
		Date pubDate = null;
		boolean trash = false;
		boolean outbox = false;
		largeNullArticle = new Article(id, parentid, author,
				title, url, summary, pubDate, read,
				trash, outbox, rating);
		
		author = "Zachary's Ozer";
		title = "Zozer's";
		url = "http://www.zozer's.com";
		summary = "I'd really like to be done writin' tests";
		pubDate = new Date(new Long("1163980800000"));
		smallTestArticle = new Article(author, title, url, summary, pubDate);
		
		
		author = "Zachary's Ozer";
		title = "Zozer's";
		url = "http://www.zozer's.com";
		summary = "I'd really like to be done writin' tests";
		pubDate = new Date(new Long("1163980800000"));
		read = true;
		rating = 5;
		id = 123456789;
		parentid = 987654321;
		largeTestArticle = new Article(id, parentid, author,
				title, url, summary, pubDate, read,
				trash, outbox, rating);
	}
	protected void tearDown() {
		//clean up after testing (if necessary)
	}
	public void testArticleConstructor() {
		//insert code testing basic functionality
		String author = null;
		String title = null;
		String url = null;
		String summary = null;
		Date pubdate = null;
		Article newArticle = new Article(author, title, url, summary, pubdate);
		assertTrue("An article constructed with all nulls should return empty" +
				"strings and a null date", ((newArticle.getAuthor().equals(""))&&
						(newArticle.getTitle().equals(""))&&
						(newArticle.getUrl().equals(""))&&
						(newArticle.getSummary().equals(""))&&
						(newArticle.getPubDate() == null)&&
						(newArticle.getRead() == false)&&
						(newArticle.getRating() == 0)));
		boolean shouldFail = false;
		try {
			newArticle.getId();
		} catch (IllegalAccessException e){
			shouldFail = true;
		}
		assertTrue("Getting the ID of an article not construced with an ID" +
				"should throw an error",shouldFail);
		
		author = "Zachary's Ozer";
		title = "Zozer's";
		url = "http://www.zozer's.com";
		summary = "I'd really like to be done writin' tests";
		pubdate = new Date(new Long("1163980800000"));
		newArticle = new Article(author, title, url, summary, pubdate);
		assertTrue("An article constructed with values should return objects" +
				"with the same value", ((newArticle.getAuthor().equals(
				"Zachary's Ozer"))&&
				(newArticle.getTitle().equals(
				"Zozer's"))&&
				(newArticle.getUrl().equals(
				"http://www.zozer's.com"))&&
				(newArticle.getSummary().equals(
				"I'd really like to be done writin' tests"))&&
				(newArticle.getPubDate().equals(
						new Date(new Long("1163980800000"))))&&
						(newArticle.getRead() == false)&&
						(newArticle.getRating() == 0)));
		shouldFail = false;
		try {
			newArticle.getId();
		} catch (IllegalAccessException e){
			shouldFail = true;
		}
		assertTrue("Getting the ID of an article not construced with an ID" +
				"should throw an error",shouldFail);
	}
	public void testArticleConstructor2() {
		//insert code testing basic functionality
		String author = null;
		String title = null;
		String url = null;
		String summary = null;
		boolean read = false;
		int rating = 0;
		int id = 0;
		int parentid = 0;
		Date pubDate = null;
		boolean trash = false;
		boolean outbox = false;
		Article newArticle = new Article(id, parentid, author,
				title, url, summary, pubDate, read,
				trash, outbox, rating);

		boolean shouldNotFail = true;
		try {
			assertTrue("An article constructed with all nulls should return empty" +
					"strings and a null date", ((newArticle.getAuthor().equals(""))&&
							(newArticle.getTitle().equals(""))&&
							(newArticle.getUrl().equals(""))&&
							(newArticle.getSummary().equals(""))&&
							(newArticle.getPubDate() == null)&&
							(newArticle.getRead() == false)&&
							(newArticle.getRating() == 0)&&
							(newArticle.getId() == 0)&&
							(newArticle.getParentid() == 0)));
		} catch (IllegalAccessException e) {
			shouldNotFail = false;
		}
		assertTrue("Trying to get the ID of an article instantiated with an ID" +
				"should not throw an error", shouldNotFail);
		
		author = "Zachary's Ozer";
		title = "Zozer's";
		url = "http://www.zozer's.com";
		summary = "I'd really like to be done writin' tests";
		pubDate = new Date(new Long("1163980800000"));
		read = true;
		rating = 5;
		id = 123456789;
		parentid = 987654321;
		newArticle = new Article(id, parentid, author,
				title, url, summary, pubDate, read,
				trash, outbox, rating);

		
		shouldNotFail = true;
		try {
			assertTrue("An article constructed with values should return values", 
					((newArticle.getAuthor().equals(
					"Zachary's Ozer"))&&
					(newArticle.getTitle().equals(
					"Zozer's"))&&
					(newArticle.getUrl().equals(
					"http://www.zozer's.com"))&&
					(newArticle.getSummary().equals(
					"I'd really like to be done writin' tests"))&&
					(newArticle.getPubDate().equals(
							new Date(new Long("1163980800000"))))&&
							(newArticle.getRead() == true)&&
							(newArticle.getRating() == 5)&&
							(newArticle.getId() == 123456789)&&
							(newArticle.getParentid() == 987654321)));
		} catch (IllegalAccessException e) {
			shouldNotFail = false;
		}
		assertTrue("Trying to get the ID of an article instantiated with an ID" +
				"should not throw an error", shouldNotFail);
	}
	
	public void testIsSame() {
		//insert code testing basic functionality
		//Test for two null articles, constructor 1
		String author = null;
		String title = null;
		String url = null;
		String summary = null;
		Date pubdate = null;
		Article newArticle1 = new Article(author, title, url, summary, pubdate);
		Article newArticle2 = new Article(author, title, url, summary, pubdate);
		assertTrue("Two null articles should be the same.", newArticle1.isSame(newArticle2));
		assertTrue("Two null articles should be the same.", newArticle2.isSame(newArticle1));
		assertTrue("A null article should be the same as itself.", newArticle1.isSame(newArticle1));
		assertTrue("A null article should be the same as itself.", newArticle2.isSame(newArticle2));
		
		//Test for two articles, constructor 1
		author = "Zachary's Ozer";
		title = "Zozer's";
		url = "http://www.zozer's.com";
		summary = "I'd really like to be done writin' tests";
		pubdate = new Date(new Long("1163980800000"));
		newArticle1 = new Article(author, title, url, summary, pubdate);
		assertFalse("Two different articles should not be the same.", newArticle1.isSame(newArticle2));
		assertFalse("Two different articles should not be the same.", newArticle2.isSame(newArticle1));
		newArticle2 = new Article(author, title, url, summary, pubdate);
		assertTrue("Two articles should be the same.", newArticle1.isSame(newArticle2));
		assertTrue("Two articles should be the same.", newArticle2.isSame(newArticle1));
		assertTrue("An article should be the same as itself.", newArticle1.isSame(newArticle1));
		assertTrue("An  article should be the same as itself.", newArticle2.isSame(newArticle2));
		
		//Test for two null articles, constructor 2
		author = null;
		title = null;
		url = null;
		summary = null;
		pubdate = null;
		boolean read = false;
		int rating = 0;
		int id = 0;
		int parentid = 0;
		Date pubDate = null;
		boolean trash = true;
		boolean outbox = true;
		newArticle1 = new Article(id, parentid, author,
				title, url, summary, pubDate, read,
				trash, outbox, rating);
		newArticle2 = new Article(id, parentid, author,
				title, url, summary, pubDate, read,
				trash, outbox, rating);
		assertTrue("Two null articles should be the same.", newArticle1.isSame(newArticle2));
		assertTrue("Two null articles should be the same.", newArticle2.isSame(newArticle1));
		assertTrue("A null article should be the same as itself.", newArticle1.isSame(newArticle1));
		assertTrue("A null article should be the same as itself.", newArticle2.isSame(newArticle2));
		
		//Test for two articles, constructor 2
		author = "Zachary's Ozer";
		title = "Zozer's";
		url = "http://www.zozer's.com";
		summary = "I'd really like to be done writin' tests";
		pubdate = new Date(new Long("1163980800000"));
		read = true;
		rating = 1;
		id = 123456789;
		parentid =987654321;
		trash = false;
		outbox = false;
		newArticle1 = new Article(id, parentid, author,
				title, url, summary, pubDate, read,
				trash, outbox, rating);
		
		assertFalse("Two different articles should not be the same.", newArticle1.isSame(newArticle2));
		assertFalse("Two different articles should not be the same.", newArticle2.isSame(newArticle1));
		newArticle2 = new Article(id, parentid, author,
				title, url, summary, pubDate, read,
				trash, outbox, rating);
		
		assertTrue("Two articles should be the same.", newArticle1.isSame(newArticle2));
		assertTrue("Two articles should be the same.", newArticle2.isSame(newArticle1));
		assertTrue("An article should be the same as itself.", newArticle1.isSame(newArticle1));
		assertTrue("An article should be the same as itself.", newArticle2.isSame(newArticle2));
	}

	public void testGetters() {
		assertEquals("Small article should have correct author", "Zachary's Ozer", smallTestArticle.getAuthor());
		assertEquals("Small article should have zero parentid by default", 0, smallTestArticle.getParentid());
		assertEquals("Small article should have correct published date", new Date(new Long("1163980800000")), smallTestArticle.getPubDate());
		assertEquals("Small article should have default rating", 0, smallTestArticle.getRating());
		assertEquals("Small article should have correct summary", "I'd really like to be done writin' tests", smallTestArticle.getSummary());
		assertEquals("Small article should have correct title", "Zozer's", smallTestArticle.getTitle());
		assertEquals("Small article should have correct URL", "http://www.zozer's.com", smallTestArticle.getUrl());
		try {
			smallTestArticle.getId();
			fail("A 'small' constructed article should throw an exception when getting its id");
		}
		catch (IllegalAccessException e) {}
		catch (Exception e) { fail("Encountered unexpected exception when testing getId on a small article: "+e); }
		assertEquals("Small article should have default read status", false, smallTestArticle.getRead());
		assertEquals("Small article should have default trash status", false, smallTestArticle.isInTrash());
		assertEquals("Small article should have default outbox status", false, smallTestArticle.isInOutbox());

		
		assertEquals("Small null article should have correct author", "", smallNullArticle.getAuthor());
		assertEquals("Small null article should have zero parentid by default", 0, smallNullArticle.getParentid());
		assertEquals("Small null article should have correct published date", null, smallNullArticle.getPubDate());
		assertEquals("Small null article should have default rating", 0, smallNullArticle.getRating());
		assertEquals("Small null article should have correct summary", "", smallNullArticle.getSummary());
		assertEquals("Small null article should have correct title", "", smallNullArticle.getTitle());
		assertEquals("Small null article should have correct URL", "", smallNullArticle.getUrl());
		try {
			smallNullArticle.getId();
			fail("A 'small' constructed article should throw an exception when getting its id");
		}
		catch (IllegalAccessException  e) {}
		catch (Exception e) { fail("Encountered unexpected exception when testing getId on a small null article: "+e); }
		assertEquals("Small null article should have default read status", false, smallNullArticle.getRead());
		assertEquals("Small null article should have default trash status", false, smallNullArticle.isInTrash());
		assertEquals("Small null article should have default outbox status", false, smallNullArticle.isInOutbox());

		assertEquals("Large article should have correct author", "Zachary's Ozer", largeTestArticle.getAuthor());
		try { assertEquals("Large article should have correct id", 123456789, largeTestArticle.getId()); }
			catch (Exception e) { fail("Encountered exception getting id of a large, fully constructed article: "+e); }
		assertEquals("Large article should have correct parentid", 987654321, largeTestArticle.getParentid());
		assertEquals("Large article should have correct published date", new Date(new Long("1163980800000")), largeTestArticle.getPubDate());
		assertEquals("Large article should have correct rating", 5, largeTestArticle.getRating());
		assertEquals("Large article should have correct summary", "I'd really like to be done writin' tests", largeTestArticle.getSummary());
		assertEquals("Large article should have correct title", "Zozer's", largeTestArticle.getTitle());
		assertEquals("Large article should have correct URL", "http://www.zozer's.com", largeTestArticle.getUrl());
		assertEquals("Large article should have correct read status", true, largeTestArticle.getRead());
		assertEquals("Large article should have correct trash status", false, largeTestArticle.isInTrash());
		assertEquals("Large article should have correct outbox status", false, largeTestArticle.isInOutbox());

		assertEquals("Large null article should have correct author", "", largeNullArticle.getAuthor());
		try { assertEquals("Large null article should have correct id", 0, largeNullArticle.getId()); }
			catch (Exception e) { fail("Encountered exception getting id of a large, null article: "+e); }
		assertEquals("Large null article should have correct parentid", 0, largeNullArticle.getParentid());
		assertEquals("Large null article should have correct published date", null, largeNullArticle.getPubDate());
		assertEquals("Large null article should have correct rating", 0, largeNullArticle.getRating());
		assertEquals("Large null article should have correct summary", "", largeNullArticle.getSummary());
		assertEquals("Large null article should have correct title", "", largeNullArticle.getTitle());
		assertEquals("Large null article should have correct URL", "", largeNullArticle.getUrl());
		assertEquals("Large null article should have correct read status", false, largeNullArticle.getRead());
		assertEquals("Large null article should have correct trash status", false, largeNullArticle.isInTrash());
		assertEquals("Large null article should have correct outbox status", false, largeNullArticle.isInOutbox());
	}

	
	public void testSetters() {
		smallTestArticle.setInOutbox(true);
		largeTestArticle.setInOutbox(true);
		largeNullArticle.setInOutbox(true);
		smallNullArticle.setInOutbox(true);
		assertTrue("Setting something to be in outbox should yield a correct result",(true == smallNullArticle.isInOutbox()) && (smallNullArticle.isInOutbox() == largeNullArticle.isInOutbox() == smallTestArticle.isInOutbox() == largeTestArticle.isInOutbox()));
		smallTestArticle.setInOutbox(false);
		largeTestArticle.setInOutbox(false);
		largeNullArticle.setInOutbox(false);
		smallNullArticle.setInOutbox(false);
		assertTrue("Setting something to be outside outbox should yield a correct result",(false == smallNullArticle.isInOutbox()) && (smallNullArticle.isInOutbox() == largeNullArticle.isInOutbox() == smallTestArticle.isInOutbox() == largeTestArticle.isInOutbox()));

		smallTestArticle.setInTrash(true);
		largeTestArticle.setInTrash(true);
		largeNullArticle.setInTrash(true);
		smallNullArticle.setInTrash(true);
		assertTrue("Setting something to be in trash should yield a correct result",(true == smallNullArticle.isInTrash()) && (smallNullArticle.isInTrash() == largeNullArticle.isInTrash() == smallTestArticle.isInTrash() == largeTestArticle.isInTrash()));
		smallTestArticle.setInTrash(false);
		largeTestArticle.setInTrash(false);
		largeNullArticle.setInTrash(false);
		smallNullArticle.setInTrash(false);
		assertTrue("Setting something to be outside trash should yield a correct result",(false == smallNullArticle.isInTrash()) && (smallNullArticle.isInTrash() == largeNullArticle.isInTrash() == smallTestArticle.isInTrash() == largeTestArticle.isInTrash()));
	
		smallTestArticle.markRead(true);
		largeTestArticle.markRead(true);
		largeNullArticle.markRead(true);
		smallNullArticle.markRead(true);
		assertTrue("Setting something to be read should yield a correct result",(true == smallNullArticle.getRead()) && (smallNullArticle.getRead() == largeNullArticle.getRead() == smallTestArticle.getRead() == largeTestArticle.getRead()));
		smallTestArticle.markRead(false);
		largeTestArticle.markRead(false);
		largeNullArticle.markRead(false);
		smallNullArticle.markRead(false);
		assertTrue("Setting something to be unread should yield a correct result",(false == smallNullArticle.getRead()) && (smallNullArticle.getRead() == largeNullArticle.getRead() == smallTestArticle.getRead() == largeTestArticle.getRead()));
	}
	
	
	
	
}