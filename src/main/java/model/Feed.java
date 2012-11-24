package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Feed class represents an RSS feed. A Feed itself has a title,
 * description, url, and a date that it was last updated. Under some
 * circumstances, a Feed is also a collection of Articles: the knowsChildren
 * boolean determines whether this is the case. A feed created by a parser, or
 * in general added to under any circumstances, contains some Articles;
 * otherwise, a feed does not contain articles, and if you want a feed's
 * articles, you should ask a database.
 * 
 * @author pgroudas
 * 
 */
public class Feed {
	private boolean knowsChildren = false;
	private String title, description, url;
	private int id = -1;
	private int parentid = -1;
	private int updateInterval = -1;
	private Date lastUpdatedByFeed, lastUpdatedByUser, publishedDate;
	private List<Article> articles = new ArrayList<Article>();
	
	/**
	 * Constructor that is used when the DBHandler creates and returns a string, so this
	 * feed should have an id. This is a complete representation of the feed from
	 * the database, so should not have null values in place of strings; nevertheless,
	 * if the relevant input is null, title, description, and URL will be stored as
	 * empty strings instead of null values. Likewise, null date input will become
	 * a new generic date object.
	 * 
	 * @param id	Guaranteed unique (comes from database).
	 * @param parentid
	 * @param title
	 * @param description
	 * @param url
	 * @param lastUpdatedByFeed
	 * @param lastUpdatedByUser
	 * @param publishedDate
     * @param updateInterval
	 */
	public Feed(int id, 
			int parentid, 
			String title, 
			String description, 
			String url, 
			Date lastUpdatedByFeed, 
			Date lastUpdatedByUser, 
			Date publishedDate,
			int updateInterval){
		
		if (title==null)
			this.title = "";
		else this.title = title;
		
		if (description==null)
			this.description = "";
		else this.description = description;
		
		if (url==null)
			this.url = "";
		else this.url = url;
		
		this.id = id;
		this.parentid = parentid;
		this.updateInterval = updateInterval;
		
		//TODO:Handle improper dates in a meaningful way.
		
		if (lastUpdatedByFeed==null){
			this.lastUpdatedByFeed = new Date();
		}else
			this.lastUpdatedByFeed = new Date(lastUpdatedByFeed.getTime());
		if (lastUpdatedByUser==null){
			this.lastUpdatedByUser = new Date();
		}else
			this.lastUpdatedByUser = new Date(lastUpdatedByUser.getTime());
		if (publishedDate == null){
			this.publishedDate = new Date();
		}else
			this.publishedDate = new Date(publishedDate.getTime());
	}
	/**
	 * Constructor that is used when the RomeFeedParser creates a feed, so this kind 
	 * of feed should not have an id. Title, description, and URL become empty strings if
	 * null values are supplied; the date fields lastUpdatedByFeed, lastUpdatedByUser, and
	 * publishedDate become new date objects if null values are given.
	 * 
	 * @param title
	 * @param description
	 * @param url
	 * @param lastUpdatedByFeed
	 * @param lastUpdatedByUser
	 * @param publishedDate
	 */
	public Feed(String title, String description, String url, Date lastUpdatedByFeed, 
			Date lastUpdatedByUser, Date publishedDate){
		
		if (title==null)
			this.title = "";
		else this.title = title;
		
		if (description==null)
			this.description = "";
		else this.description = description;
		
		if (url==null)
			this.url = "";
		else this.url = url;
		
		//TODO:Handle improper dates in a meaningful way.
		if (lastUpdatedByFeed==null){
			this.lastUpdatedByFeed = new Date();
		}else
			this.lastUpdatedByFeed = new Date(lastUpdatedByFeed.getTime());
		if (lastUpdatedByUser==null){
			this.lastUpdatedByUser = new Date();
		}else
			this.lastUpdatedByUser = new Date(lastUpdatedByUser.getTime());
		if (publishedDate == null){
			this.publishedDate = new Date();
		}else
			this.publishedDate = new Date(publishedDate.getTime());
		this.knowsChildren = true; // Set to true by default, since empty lists are okay.
		//We can use either of these, the above is less elegant but doesn't expose our
		//implementation
		/*
		this.lastUpdatedByFeed = lastUpdatedByFeed;
		this.lastUpdatedByUser = lastUpdatedByUser;
		this.publishedDate = publishedDate;
		*/
	}
	/**
	 * The method addArticle adds the specified article to the feed, if it is not already 
	 * in the feed. If it is already in the feed, it returns false.
	 * 
	 * @return true if the article was successfully added.
	 */
	public boolean addArticle(Article a){
		if (!knowsChildren){
			setKnowsChildren(true);
		}
		/*
		 * checks if the article is already in the feed, and if so returns false.  
		 * Then it returns the result fo articles.add(a), in case there is some other
		 * problem with adding the article, it will still return false.
		 */
		if (this.hasArticle(a)){
			return false;
		}else return articles.add(a);
	}
	/**
	 * Adds a list of articles to the feed, calling the addArticle method. Because
	 * each addArticle returns a boolean value, this has no return type; it's void!
	 * Note that duplicate articles will not be added (per the contract of addArticle).
	 * 
	 * @param a	The list of articles to be added to the feed.
	 */
	public void addArticles(List<Article> a){
		for (Article b:a){
			addArticle(b);
		}
	}
	
	/**
	 * The method hasArticle tests if the article is already in the feed, based on the 
	 * article.isSame() method.
	 * 
	 * @param a Article to be compared.
	 * @return true if for some Article b in the feed b.isSame(a) returns true.
	 * @return false if the article a is unique in the feed
	 */
	private boolean hasArticle(Article a){
		boolean hasArt = false;
		for (Article b: articles){
			if (b.isSame(a)){
				hasArt = true;
				break;
			}
		}
		return hasArt;
	}
	/**
	 * The method removeArticle attempts to remove the specified article from the Feed.  If 
	 * one such feed is found it is removed and the method returns true.  If more than one 
	 * article matches the specified article, then nothing is guaranteed other than that 
	 * one will be removed.
	 * 
	 * @return true if the specified article is removed from the feed.
	 */
	public boolean removeArticle(Article a){
		for (Article b:articles){
			if (b.isSame(a)){
				return articles.remove(b);
			}
		}
		return false;
	}
	/**
	 * Tests equivalence of two feeds by comparing the two
	 * feeds' URLs. A URL should be globally unique within 
	 * a user's set of subscribed feeds.
	 * 
	 * @param f1	First feed to be tested
	 * @param f2	Second feed to be tested
	 * @return	True if the feeds are the same (have same URL); false otherwise.
	 */
	public static boolean isSame(Feed f1, Feed f2){
		return f1.getUrl().equals(f2.getUrl());
	}
	/**
	 * Gets the articles from a feed, should rarely be used as the database should be 
	 * the source of this type of information, except when the parser is creating the feed.
	 * @return	List of all articles which the feed thinks are its children (the database may be more up to date and may disagree with this method)
	 * @throws IllegalAccessException	if the feed has not yet been instantiated with its children (for instance, it is newly created) 
	 */
	public List<Article> getChildren() throws IllegalAccessException{
		if (!knowsChildren){
			throw new IllegalAccessException("Can't get articles, the feed hasn't had them instantiated yet");
		}else{
			return new ArrayList<Article>(articles);
		}
	}
	/**
	 * This method returns a boolean indication whether or not this feed is actually containing its children,
	 * which isn't trivial because feeds returned by the database don't have references to their children.
	 * @return
	 */
	public boolean knowsChildren(){
		return knowsChildren;
	}
	/**
	 * sets the knowsChildren private data field.
	 * @param b
	 */
	private void setKnowsChildren(boolean b){
		knowsChildren = b;
	}
	/**
	 * Gets the description of the feed. This longwinded (optional) description tells
	 * the user what the feed is all about.
	 * @return String description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * Gets the id of the feed, if it has one; throws an error otherwise.
	 * @return int	The feed's id.
	 * @throws IllegalAccessError	If this feed was constructed as "small" (without an id), throw this exception.
	 */
	public int getId(){
		if (id == -1) {
			throw new IllegalAccessError("Attempted to access ID for a Feed with no ID set.");
		}
		return id;
	}
	/**
	 * Gets the id of the feed's parent folder, if it has one; throws an error otherwise.
	 * @return int	parentId	The id of the feed's parent folder.
	 * @throws IllegalAccessError	If this feed was constructed as "small" (without a parent id), throw this exception.
	 */
	public int getParentId(){
		if (id == -1) {
			throw new IllegalAccessError("Attempted to access parentID for a Feed with no parentID set.");
		}
		return parentid;
	}
	//TODO: fix it to not return the actual date objects
	/**
	 * Returns the date when the feed was last updated.
	 * @return Date lastUpdated
	 */
	public Date getLastUpdatedByFeed() {
		return lastUpdatedByFeed;
	}
	/**
	 * Returns the date the feed was last updated by the user.
	 * @return Date lastUpdatedByUser
	 */
	public Date getLastUpdatedByUser() {
		return 	lastUpdatedByUser;
	}
	/**
	 * Returns the date the feed was published.
	 * @return Date pubDate
	 */
	public Date getPublishedDate() {
		return publishedDate;
	}
	/**
	 * Gets the title of the Feed, which should not be null (but might conceivably 
	 * be the empty string "").
	 * @return String title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * Sets the title of the feed. If input is null, sets the title of the feed to
	 * the empty string; otherwise sets feed title to the input.
	 * @param title String	New title for feed.
	 */
	public void setTitle(String title) {
		if (title == null) this.title = "";
		else this.title = title;
	}
	/**
	 * Gets the url of the Feed. This should not be null, as feeds are considered
	 * unique on the basis of their urls.
	 * @return String url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * Gets the updateInterval of the feed, an integer representing how frequently the
	 * application should try to update the feed. Only works if the feed has been created
	 * with an update interval!
	 * @return int updateInterval
	 * @throws IllegalAccessError	If the feed has not been instantiated with an update interval, give this exception.
	 */
	public int getUpdateInterval() {
		if (id == -1) {
			throw new IllegalAccessError("Attempted to access update interval for a Feed with no feed interval set.");
		}
		return updateInterval;
	}
}