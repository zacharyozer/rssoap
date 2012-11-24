package tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import model.Feed;
import parser.RomeFeedParser;

import com.sun.syndication.io.FeedException;

public class ParserTest extends TestCase {

	public ParserTest(String name) {
		super(name);
	}

	public void testParseFeed() {
		String[] testURLs = { "http://rss.slashdot.org/Slashdot/slashdot.rss",
				"http://wt.mit.edu/rss/news.xml",
				"http://www.nytimes.com/services/xml/rss/nyt/HomePage.xml",
				"http://newsrss.bbc.co.uk/rss/newsonline_world_edition/front_page/rss.xml" };
		for (String testURL : testURLs) {

			try {
				Feed f = RomeFeedParser.ParseURL(testURL);
				assertNotNull("Feed generated from " + testURL
						+ " should not be false", f);
			} catch (MalformedURLException e) {
				fail("Generating feed from " + testURL
						+ " caused an unexpected Malformed URL exception");
			} catch (IOException e) {
				fail("Generating feed from " + testURL
						+ " caused an unexpected IO exception");
			} catch (FeedException e) {
				fail("Generating feed from " + testURL
						+ " caused an unexpected Feed exception");
			}
		}
	}

	public void testMalformedURLs() {
		String[] testURLs = {
				"irq://i-am-making-this-url-scheme-up:q11~\\`rz/", "", null,
				"Slashdot" };
		for (String testURL : testURLs) {
			try {
				RomeFeedParser.ParseURL(testURL);
				fail("Parser fails to parse malformed URL " + testURL);
			} catch (Exception MalformedURLException) {
			}
		}
	}

	public void testOPMLRead() {
		String testURL = "http://share.opml.org/opml/top100.opml";
		try {
			RomeFeedParser.ParseOPMLURL(testURL);
		} catch (Exception e) {
			fail("Parser fails upon parsing the OPML file at " + testURL
					+ " . See error message: " + e);
		}

	}

	public void testMakeOPML() {
		String[] feeds = { 
				"http://wt.mit.edu/rss/news.xml",
				"http://rss.slashdot.org/Slashdot/slashdot.xml" };
		ArrayList<Feed> thefeeds = new ArrayList<Feed>();
		for (String feed : feeds) {
			try {
				Feed f = RomeFeedParser.ParseURL(feed);
				thefeeds.add(f);
			} catch (Exception e) {
				e.printStackTrace();
				throw new NullPointerException();
			}
		}
		String madeOPML = RomeFeedParser.MakeOPMLString(thefeeds);
		try {
			File temp = File.createTempFile("temp", ".opml");
			temp.deleteOnExit();
			String filename = temp.getAbsolutePath();
			BufferedWriter out = new BufferedWriter(new FileWriter(temp));
			out.write(madeOPML);
			out.close();
			List<String> urls = RomeFeedParser.ParseOPMLFile(filename);
			assertEquals("Created OPML should contain exactly two URLs", 2, urls.size());
			assertEquals("Created OPML should have Tech feed as its first URL", "http://wt.mit.edu/rss/news.xml", urls.get(0));
			assertEquals("Created OPML should have Slashdot feed as its second URL", "http://rss.slashdot.org/Slashdot/slashdot.xml", urls.get(1));
		}
		catch (IOException e) { fail("Encountered unusual IO Exception during testing: "+e); }
		catch (Exception e) { fail("Encountered exception while creating and parsing OPML: "+e); }
	}
	
	public void tortureTest() {
		String testURL = "http://share.opml.org/opml/top100.opml";
		try {
			List<String> urls = RomeFeedParser.ParseOPMLURL(testURL);
			for (String url : urls) {
				try {
					Feed f = RomeFeedParser.ParseURL(url);
					RomeFeedParser.PrintRSSFeed(f, "rss_2.0");
				} catch (Exception e) {
					fail("Encountered exception while parsing url " + url
							+ ": " + e);
				}
			}
		} catch (Exception e) {
			fail("Encountered error while parsing OPML at " + testURL + " : e");
		}
	}

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new ParserTest("testParseFeed"));
		suite.addTest(new ParserTest("testMalformedURLs"));
		suite.addTest(new ParserTest("testOPMLRead"));
		suite.addTest(new ParserTest("tortureTest"));
		return suite;
	}

}