package model;

import java.util.Date;
/**
 * The Article class represents an article from an RSS feed. This class
 * has two types of instantiations, one created directly from the database 
 * and one created by the parser. The latter has less knowledge of its environment
 * (and in particular it has no database id number). 
 * 
 * @author pgroudas
 *
 */
public class Article {
	private String author, title, url, summary;
	private Date pubDate;
	private boolean read; 
	private boolean inTrash;
	private boolean inOutbox;
	private boolean hasId;
	private int rating, id, parentid;
	/**
	 * Constructor takes input information and constructs an Article object.
	 * This represents an article which is brand new and has no id (intended usage is for
	 * the parser to create a new article, not for the database to create an article with
	 * existing information -- since any database construction should use the article's id.)
	 *
	 * By default, a new article is unread (read == false). It also has a default rating 
	 * of zero. It is, by default, not in the Trash or Outbox. Additionally, it has no 
	 * id (hasid = false).
	 * 
	 * @param author Author of article; If null, it is set to the empty string
	 * @param title Title of article; If null, it is set to the empty string
	 * @param url URL of full article; If null, it is set to the empty string
	 * @param summary Text summary of article; If null, it is set to the empty string
	 * @param pubDate Date that article was published. If null, it is passed along as null.
	 */
	public Article(String author, String title, String url, String summary, Date pubDate){
		if (author==null)
			this.author = "";
		else this.author = author;
		
		if (title==null)
			this.title = "";
		else this.title = title;
		
		if (url==null)
			this.url = "";
		else this.url = url;
		
		if (summary==null)
			this.summary = "";
		else this.summary = summary;
		
		//TODO: Handle Null Date input
		this.pubDate = pubDate;

		this.read = false;
		this.inTrash = false;
		this.inOutbox = false;
		this.rating = 0; // Default rating is "zero". 
		
		//when constructed with this info it is complete
		hasId = false;
	}
	/**
	 * Constructor takes input information and constructs an Article object with a given
	 * id. This represents an article which already exists in the database (already has an id).
	 * 
	 * @param id Internal id number for the article. (From a database; guaranteed to be unique.)
     * @param parentid	Internal id number for the article's parent.
	 * @param author Author of article. If null, becomes the empty string.
	 * @param title Title of article. If null, becomes the empty string.
	 * @param url URL of full article. If null, becomes the empty string.
	 * @param summary Text summary of article. If null, becomes the empty string.
	 * @param pubDate Date that article was published. If null, is passed along as a null date.
	 * @param read Whether or not the article has been read yet.
	 * @param trash Whether or not this article is in the trash.
     * @param outbox	Whether or not this article is in the outbox.
	 * @param rating	The article's rating.
	 */
	public Article(int id, int parentid, String author, String title, String url, String summary, Date pubDate, boolean read,boolean trash, boolean outbox,int rating){
		if (author==null)
			this.author = "";
		else this.author = author;
		
		if (title==null)
			this.title = "";
		else this.title = title;
		
		if (url==null)
			this.url = "";
		else this.url = url;
		
		if (summary==null)
			this.summary = "";
		else this.summary = summary;
		
		//TODO: Handle Null Date input
		this.pubDate = pubDate;

		this.read = read;
		this.inTrash = trash;
		this.inOutbox = outbox;
		this.rating = rating;
		
		this.id = id;
		//when constructed with this info it is complete
		hasId = true;
		this.parentid = parentid;
	}
	/**
	 * Marks the article as read. Any article can be marked as read; should
	 * always work, and returns nothing.
	 * 
	 * @param read	The new read status of the article (true->read, false->unread)
	 */
	public void markRead(boolean read){
		this.read = read;
	}
	
	/**
	 * Compares an article to another given article. Two articles are "the same"
	 * if they have the same author, title, and URL.
	 * 
	 * @param a Article to be compared.
	 * @return true if the two articles have the same author, title, and url.
	 */
	public boolean isSame(Article a){
		return (author.equals(a.getAuthor())&&
				title.equals(a.getTitle())&&
				url.equals(a.getUrl()));
	}
	/**
	 * Tests if article knows its id (basically tests if this article was returned by the database)
	 * @return boolean
	 */
	private boolean hasId(){
		return hasId;
	}
	/**
	 * Get the article's author. Because of the way this is constructed, should never
	 * be null (only the empty string.) 
	 * @return String author
	 */
	public String getAuthor() {
		return author;
	}
	/**
	 * Gets the article's published date, supplied on construction.
	 * @return	Date pubDate
	 */
	public Date getPubDate() {
		return pubDate;
	}
	/**
	 * Gets the article's summary, which is the (optional) bit of content displayed
	 * beneath the article's title in the mailbox view. This may be the empty string,
	 * but shouldn't be null. 
	 * @return String summary
	 */
	public String getSummary() {
		return summary;
	}
	/**
	 * Gets the article's title. This should not be null.
	 * @return String title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * Gets the article's url, supplied on construction. This should not be null.
	 * @return	String url
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * Gets the article's id, if it was created in such a way that it has an id (i.e.
	 * if it was created by the database and not by the parser).
	 * @return	int ID
	 * @throws IllegalAccessException if this article has no id (this means the article wasn't made from the database)
	 */
	public int getId() throws IllegalAccessException{
		if (this.hasId()){
			return id;
		}else {
			throw new IllegalAccessException();
		}
	}
	/**
	 * Returns the read status of the article. True means read, false means unread.
	 * @return boolean read
	 */
	public boolean getRead() {
		return read;
	}
	/**
	 * Returns the article's rating, an integer.
	 * @return int rating
	 */
	public int getRating() {
		return rating;
	}
	/**
	 * Returns the article's parent id. This will be zero for an article that is 
	 * not constructed from the database (and therefore has no knowledge of its parent).
	 * @return int parentid
	 */
	public int getParentid() {
		return parentid;
	}
	/**
	 * Tests to see if the article is in the trash. True means it is trashed.
	 * @return boolean inTrash
	 */
	public boolean isInTrash() {
		return inTrash;
	}
	/**
	 * Sets the article's trashed status. True means it is in the trash. 
	 * @param inTrash boolean	New trash status of the article
	 */
	public void setInTrash(boolean inTrash) {
		this.inTrash = inTrash;
	}
	/**
	 * Sets the article's in-outbox status. True means it is in the outbox feed.
	 * @param inOutbox boolean	New outbox status of the article
	 */
	public void setInOutbox(boolean inOutbox) {
		this.inOutbox = inOutbox;
	}
	/**
	 * Tests to see if the article is in the outbox.
	 * @return
	 */
	public boolean isInOutbox() {
		return inOutbox;
	}
}
