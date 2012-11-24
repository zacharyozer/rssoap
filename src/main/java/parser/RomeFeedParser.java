package parser;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.Article;
import model.Feed;

import com.sun.syndication.feed.opml.Opml;
import com.sun.syndication.feed.opml.Outline;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.WireFeedInput;
import com.sun.syndication.io.XmlReader;

import control.ControlException;
import control.Controller;

public class RomeFeedParser {

	public static void main(String[] args) {
		try {
			List<String> urls = ParseOPMLURL("http://web.mit.edu/pgroudas/www/");
			for ( String url : urls) {
			//System.out.println(url);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

//	This internal method uses Java and Rome elements to take in a 
//	URL (as a string) and output the URL's contents (as a Rome feed). 
//	Does the dirty work of URL fetching, using a set user agent string,
//	because Rome's Fetcher module is incapable of correctly setting
//	its user agent string (Java appears to override it).

	private static SyndFeed getURL(String url) throws MalformedURLException,
	FeedException, IOException {
		String HTTP_USER_AGENT = "RSS on a Plane!";
		URL feedURL = new URL(url);
		URLConnection connection = feedURL.openConnection();
		connection.setReadTimeout(0); // milliseconds
		connection.addRequestProperty("User-Agent", HTTP_USER_AGENT);
		SyndFeedInput input = new SyndFeedInput();
		return input.build(new XmlReader(connection));		
	}


//	This commented method, while functional, is not currently implemented.
//	Users shouldn't be able to subscribe to feeds stored locally; that
//	kind of defeats the point of the Internet.

//	public static Feed ParseFile(String inputFilename) throws MalformedURLException, IOException, FeedException {
//	SyndFeedInput input = new SyndFeedInput();
//	SyndFeed feed = input.build(new File(inputFilename));
//	if (feed.getLink() == null) 
//	feed.setLink(inputFilename);
//	return ParseFeed(feed);
//	}


	/**
	 * The method ParseURL creates and returns a new feed object instantiated
	 * with article objects for each of the entries contained within the feed
	 * located at the supplied input feed URL.
	 * 
	 * @param inputFeedURL
	 *            URL (as string) where feed is located.
	 * @return New feed object, containing articles for each entry in the feed.
	 * @throws MalformedURLException
	 *             If input URL is malformed (not a valid URL), throws this
	 *             exception.
	 * @throws IOException
	 *             If the parser has difficulty downloading the feed file,
	 *             throws this exception. Note that this could happen for a
	 *             variety of reasons: no Net connection, Web site doesn't
	 *             exist, file doesn't exist on site, your router ate some
	 *             packets...
	 * @throws FeedException
	 *             If Rome can't build a feed from the given RSS feed, this
	 *             exception will be thrown.
	 */
	public static Feed ParseURL(String inputFeedURL)
	throws MalformedURLException, IOException, FeedException {
		SyndFeed feed = getURL(inputFeedURL);
		feed.setLink(inputFeedURL);
		return ParseFeed(feed);
	}


	/**
	 * Internal method: takes as input a feed to be parsed and outputs
	 * the Feed object (constructed with no id) containing all the useful 
	 * information in the Rome SyndFeed object passed to it.
	 * 
	 * @param feed
	 * @return
	 * @throws FeedException
	 */
	private static Feed ParseFeed(SyndFeed feed) throws FeedException
	{
		String feedTitle = feed.getTitle();
		if (feedTitle == null) {
			feedTitle = "";	}
		String feedDesc = feed.getDescription();
		if (feedDesc == null) {	feedDesc = ""; }
		String feedLink = feed.getLink();
		if (feedLink == null) {	throw new FeedException("The feed passed to the parser has no feed link."); }

		Date feedLastPublished = feed.getPublishedDate();
		// HACK ALERT: Some valid RSS feeds have no published date. They
		// instead define a lastBuildDate, which Rome happily discards. 
		// When a feed's published date is null, we return the current date&time.
		// Known limitation of Rome. :( --mherdeg
		if (feedLastPublished == null) {
			feedLastPublished = Calendar.getInstance().getTime(); }
		Date feedLastFeedUpdate = feedLastPublished;
		Date feedLastUserUpdate = Calendar.getInstance().getTime();
		Feed returnable = new Feed(feedTitle, feedDesc, feedLink,
				feedLastFeedUpdate, feedLastUserUpdate, feedLastPublished);

		List feedEntries = feed.getEntries();
		for (Object tempentry : feedEntries) {
			SyndEntry entry = (SyndEntry) tempentry;
			String articleAuthor = entry.getAuthor();
			if (articleAuthor == null) {
				articleAuthor = "";
			}
			String articleTitle = entry.getTitle();
			if (articleTitle == null) {
				articleTitle = "";
			} // um, this is bad
			String articleLink = entry.getLink();
			if (articleLink == null) {
				articleLink = "";
			} // um, this is bad
			SyndContent articleContent = entry.getDescription();
			String articleSummary;
			if (articleContent == null) {
				articleSummary = ""; }
			else {
				articleSummary = articleContent.getValue(); }
			if (articleSummary == null) {
				articleSummary = ""; }
			Date articlePublishDate = entry.getPublishedDate();
			if (articlePublishDate == null) {
				articlePublishDate = Calendar.getInstance().getTime();
			} // this is hackish, as above

			Article article = new Article(articleAuthor, articleTitle,
					articleLink, articleSummary, articlePublishDate);
			returnable.addArticle(article);
		}
		
		// Below is a quick fix for the "cannot add feed with no articles" behavior. Leaving uncommented for now.
		Article test = new Article("Test author", "Test title", "Test link", "Test summary", Calendar.getInstance().getTime());
		returnable.addArticle(test);
		returnable.removeArticle(test);
		return returnable;


	}

	/**
	 * "Exports" (prints to stdout) a feed object in a user-supplied format.
	 * This means that the feed will be printed out with as much metadata
	 * as this parser takes in when creating Feed objects. The feed will
	 * contain all the articles with their available Rome-compatible metadata
	 * in their exported XML file.
	 * 
	 * @param f	The Feed object to be printed to stdout.
	 * @param outputType	The type of output feed desired. Rome supports RSS 0.9, 0.91, 0.92, 0.93, 0.94, 1.0, 2.0, and Atom 0.3 and 1.0.
	 */

	public static void PrintRSSFeed(Feed f, String outputType) throws IllegalAccessException, FeedException, IOException {
		String[] elts = { "rss_0.9", "rss_0.91", "rss_0.92", "rss_0.93", "rss_0.94", "rss_1.0", "rss_2.0", "atom_0.3", "atom_1.0" };
		for (String type : elts) {
			if (outputType.equals(type)) {
				SyndFeedOutput output = new SyndFeedOutput();
				SyndFeed feed = new SyndFeedImpl();
				feed.setFeedType(outputType);
				feed.setPublishedDate(f.getPublishedDate());
				feed.setLink(f.getUrl());
				feed.setUri(f.getUrl());
				feed.setTitle(f.getTitle());
				feed.setDescription(f.getDescription());
				List<Article> feedArticles;
				List<SyndEntry> feedArticlesOut = new ArrayList<SyndEntry>();
				try {
					feedArticles = f.getChildren();
					for (Article article : feedArticles) {
						SyndEntry entry = new SyndEntryImpl();
						entry.setAuthor(article.getAuthor());
						entry.setTitle(article.getTitle());
						entry.setLink(article.getUrl());
						entry.setPublishedDate(article.getPubDate());
						SyndContent articleContent = new SyndContentImpl();
						articleContent.setType("text/plain");
						articleContent.setValue(article.getSummary());
						entry.setDescription(articleContent);
						feedArticlesOut.add(entry);
					}
					feed.setEntries(feedArticlesOut);

					StringWriter writer = new StringWriter();
					output.output(feed, writer);
				//System.out.println(writer.toString());
					return;
				}
				catch (IllegalAccessException e)
				{ throw new IllegalAccessException("Encountered an error printing RSS feed while getting its children: " + e); }
				catch (FeedException e)
				{ throw new FeedException("Encountered an error writing feed to output: " + e); }
				catch (IOException e)
				{ throw new IOException("Encountered an error writing feed to output: " + e); }
			}
		}
		throw new IllegalArgumentException("The output feed type you requested, " + outputType + ", is not available. ");
	}
	
	

	/**
	 * Creates a syndication-feed string in the requested output format from a
	 * given feed. Valid output formats are rss_0.9, rss_0.91, rss_0.92, rss_0.93, 
	 * rss_0.94, rss_1.0, rss_2.0, atom_0.3, or atom_1.0. 
	 * If you're not sure, choose RSS 2.0.
	 * 
	 * @param f	The feed object which is to be created into a feed string
	 * @param outputType	Output format (a string with a limited set of options dictated by the Rome parser used.)
	 * @return	A string containing the 
	 * @throws IllegalAccessException	Thrown if an RSS feed's children cannot be gotten 
	 * @throws FeedException	Thrown if Rome could not generate output string from these feeds.
	 */
	public static String MakeRSSString(Feed f, String outputType) throws IllegalAccessException, FeedException, IOException {
		Controller c = Controller.getApp();
		String[] elts = { "rss_0.9", "rss_0.91", "rss_0.92", "rss_0.93", "rss_0.94", "rss_1.0", "rss_2.0", "atom_0.3", "atom_1.0" };
		for (String type : elts) {
			if (outputType.equals(type)) {
				SyndFeedOutput output = new SyndFeedOutput();
				SyndFeed feed = new SyndFeedImpl();
				feed.setFeedType(outputType);
				feed.setPublishedDate(f.getPublishedDate());
				feed.setLink(f.getUrl());
				feed.setUri(f.getUrl());
				feed.setTitle(f.getTitle());
				feed.setDescription(f.getDescription());
				List<Article> feedArticles;
				List<SyndEntry> feedArticlesOut = new ArrayList<SyndEntry>();
				try {
					feedArticles = c.getArticles(f);
					for (Article article : feedArticles) {
						SyndEntry entry = new SyndEntryImpl();
						entry.setAuthor(article.getAuthor());
						entry.setTitle(article.getTitle());
						entry.setLink(article.getUrl());
						entry.setPublishedDate(article.getPubDate());
						SyndContent articleContent = new SyndContentImpl();
						articleContent.setType("text/plain");
						articleContent.setValue(article.getSummary());
						entry.setDescription(articleContent);
						feedArticlesOut.add(entry);
					}
					feed.setEntries(feedArticlesOut);

					StringWriter writer = new StringWriter();
					output.output(feed, writer);
					
					return writer.toString();
				}
				catch (ControlException e)
				{ throw new IllegalAccessException("Encountered an error printing RSS feed while getting its children: " + e); }
				catch (FeedException e)
				{ throw new FeedException("Encountered an error writing feed to output: " + e); }
			}
		}
		throw new IllegalArgumentException("The output feed type you requested, " + outputType + ", is not available. ");
	}

	/**
	 * Given a list of feeds, creates a OPML outline that represents those feeds.
	 * This is returned as a single long string (which could be, for instance,
	 * saved to file).
	 * 
	 * The generated OPML (1.0) is roughly standards-compliant; feedvalidator.org
	 * likes it. However, the OPML spec is so young that it's impossible
	 * to guarantee this will still be good years down the road.
	 *  
	 * @param feeds	List of feeds to be turned into OPML outline elements.
	 * @return
	 */
	public static String MakeOPMLString(List<Feed> feeds) {
		StringBuffer sb = new StringBuffer();
		sb.append("<opml version=\"1.0\">\n");
		// Sad but true: Rome's OPML parser does not think it can parse
		// OPML version 1.1. Changed "1.1" to "1.0" here because our 
		// application briefly couldn't parse its own output.
		sb.append("<head>\n");
		sb.append("<title>Outbox</title>\n");
		sb.append("</head><body>\n");
		for (Feed feed : feeds) {
			if (!feed.getUrl().equals("")) {
			sb.append("<outline text=\"" + feed.getTitle() + "\" title=\"" + feed.getTitle() + "\" description=\"" + feed.getDescription() + "\" xmlUrl=\"" + feed.getUrl() + "\" type=\"rss\"/>\n");
			}
		}
		sb.append("</body></opml>");
		String ret = sb.toString();
		
		// Because this is hand-assembled, it's not guaranteed perfect. International
		// characters might trip it up; one more common problem with generated XML is
		// fixed below.
		ret.replaceAll("&amp;", "&");  
		ret.replaceAll("&", "&amp;");
		return ret; 
		
	}

	/**
	 * "Exports" (prints to stdout) a feed object in RSS 2.0 format.
	 * This means that the feed will be printed out with as much metadata
	 * as this parser takes in when creating Feed objects. The feed will
	 * contain all the articles with their available Rome-compatible metadata
	 * in their exported XML file.
	 * 
	 * @param f	The Feed object to be printed to stdout.
	 */


	public static void PrintRSSFeed(Feed f) throws FeedException, IOException, IllegalAccessException {
		String outformat = "rss_2.0"; // Could also be any one of:
		PrintRSSFeed(f, outformat);
	}

	/**
	 * Using the Rome OPML module, this method parses a user-supplied
	 * OPML file (given its URL) and returns a list of strings. Each string
	 * is a URL embedded in the OPML file. This parsing treats OPML files
	 * in their most frequently used capacity, as a list of links. It discards
	 * most other information.
	 * 
	 * This parsing uses a custom user-agent to evade the 403 error which
	 * some Web sites provide to Java Web clients (and because our user agent
	 * is more informative than "Java 1.5.0.6".)
	 * 
	 * Rome's OPML parsing is not guaranteed good; it's unstable.
	 * 
	 * @param filename
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws FeedException
	 */

	public static List<String> ParseOPMLURL(String url) throws MalformedURLException, IOException, FeedException {
		String HTTP_USER_AGENT = "RSS on a Plane!";
		URL feedURL = new URL(url);
		URLConnection connection = feedURL.openConnection();
		connection.addRequestProperty("User-Agent", HTTP_USER_AGENT);
		connection.setReadTimeout(0); // milliseconds
		WireFeedInput input = new WireFeedInput();
		Opml feed = (Opml) input.build(new XmlReader(connection));
		return ParseOPML(feed);
	}

	/**
	 * Using the Rome OPML module, this method parses a user-supplied
	 * OPML file (on local drive) and returns a list of strings. Each string
	 * is a URL embedded in the OPML file. This parsing treats OPML files
	 * in their most frequently used capacity, as a list of links. It discards
	 * most other information.
	 * 
	 * Rome's OPML parsing is not guaranteed good; it's unstable.
	 *  
	 * @param filename
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws FeedException
	 */
	public static List<String> ParseOPMLFile(String filename) throws IOException, FeedException {
		WireFeedInput input = new WireFeedInput();
		File feedFile = new File(filename);
		Opml feed = (Opml) input.build(new XmlReader(feedFile));
		return ParseOPML(feed);
	}

	private static List<String> ParseOPML(Opml feed) {
		ArrayList<String> urls = new ArrayList<String>();
		List<?> outlines = feed.getOutlines();
		for (Object outlineobj : outlines) {
			Outline outline = (Outline) outlineobj;
			String rssFeedLocation = outline.getXmlUrl();
			if (!(rssFeedLocation == null)) {
				urls.add(outline.getXmlUrl());
			}
		}
		return urls;
	}

}
