package derby;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import model.Article;
import model.Feed;
import model.Folder;
/**
 * The DBHandler acts as the intermediary between the database and the rest of
 * the system. While the DBHandler implements SQL commands, the Database is
 * responsible for the actual execution. The handler acts as a translator,
 * taking SQL commands and converting them into meaningful objects in the model.
 * 
 * Rep invarients
 * -Each user may have only one feed for a given URL (Feeds may not be in
 * multiple folders). 
 * -Folders may not have any siblings with the same name
 * -No article may list the Trash as their parent feed
 * -FeedID 0 does not exist in the DB; is the root node and all user's top 
 * level folders point to the root as their parent
 * -Each user must have at least 3 feeds / folders: Subscribed Feeds, Outbox, 
 * and Trash 
 * -Min(FeedID) for a user is Subscribed Feeds
 * -Min(FeedID)+1 for a user is the Outbox
 * -Min(FeedID)+2 for a user is the Trash
 * -Trash & outbox may only contain articles
 * -Database may only have 3 tables: Feeds, Atricles, and Users
 * -Every folder must have a distinct feedid
 * -Every feed must have a distinct feedid
 * -Every article must have a distinct articleid
 * -Every user must have a distict username and userid
 * -Only one user may be active at a time
 * 
 * @author zozer
 * 
 */
public class DBHandler {
	private Database db;
	private int currentUserID = 0;
	private Folder currentSF = null;
	private Feed currentOutbox = null;
	private Feed currentTrash = null;
	
	/**
	 * Upon construction, the DBHandler object creates a Database object. This
	 * Database is where all data for the system is stored, and serves as the
	 * ultimate repository of information. The DBHandler automatically starts
	 * the DB on instantiation.
	 * 
	 * @throws DBHandlerException if the database is not properly instantiated, 
	 * if it fails to startup, or if it fails to setup.
	 */
	public DBHandler() throws DBHandlerException {
		// Instantiate the database
		db = new Database();
		if (db == null){
			throw new DBHandlerException("Database instantiation failed.");
		}
		try {
			// Startup the DB
			startup();			
		} catch (DBHandlerException e) {
			throw new DBHandlerException("Database startup / setup failed: " 
					+ e);
		}
	}
	
	/**
	 * Runs a setup algorithm which creates the feeds, articles, and user
	 * tables, as well as the 'Subscribed Feeds' and 'Outbox' folders, which
	 * must exist on creation according to the Rep Invariant.
	 * 
	 * @throws DBException If any part of the setup fails
	 */
	private void setupDB() throws DBException{
		try {
			//Create feeds table and add fields
			StringBuilder sb = new StringBuilder();
			sb.append("CREATE TABLE feeds (");
			sb.append("feedid INT NOT NULL " +
					"PRIMARY KEY GENERATED ALWAYS AS IDENTITY " +
					"(START WITH 1, INCREMENT BY 1),");
			sb.append("feedtitle VARCHAR( 255 ),");
			sb.append("feedurl VARCHAR(255),");
			sb.append("feeddesc VARCHAR(2000),");
			sb.append("feedpubdate TIMESTAMP,");
			sb.append("feedlastupdate TIMESTAMP,");
			sb.append("feedlastupdateuser TIMESTAMP,");
			sb.append("feedicon BLOB,");
			sb.append("feedreadtime INT,");
			sb.append("feedfolder INT,");
			sb.append("feedprevfolder INT,");
			sb.append("feedupdate INT,");
			sb.append("isFolder SMALLINT,");
			sb.append("isDeleted SMALLINT,");
			sb.append("isActive SMALLINT,");
			sb.append("userID INT");
			sb.append(")");
			db.modify(sb.toString());
		//System.out.println("Created table feeds");
			
			//Create articles table and add fields
			sb = new StringBuilder();
			sb.append("CREATE TABLE articles (");
			sb.append("articleid INT NOT NULL " +
					"PRIMARY KEY GENERATED ALWAYS AS IDENTITY " +
					"(START WITH 1, INCREMENT BY 1),");
			sb.append("articletitle VARCHAR(255),");
			sb.append("articleauthor VARCHAR( 255 ),");
			sb.append("articleurl VARCHAR(255),");
			sb.append("articlecontent VARCHAR(32672),");
			sb.append("articlelastupdate TIMESTAMP,");
			sb.append("articlepubdate TIMESTAMP,");
			sb.append("articlereadtime INT,");
			sb.append("articlefolder INT,");
			sb.append("articleprevfolder INT,");
			sb.append("articlerating INT,");
			sb.append("articlevisited SMALLINT,");
			sb.append("isRead SMALLINT,");
			sb.append("isDeleted SMALLINT,");
			sb.append("isActive SMALLINT,");
			sb.append("userID INT");
			sb.append(")");
			db.modify(sb.toString());
			//Create the indexes for articles
			db.modify("CREATE INDEX contentindex ON articles(articlecontent)");
			db.modify("CREATE INDEX authorindex ON articles(articleauthor)");
			db.modify("CREATE INDEX titleindex ON articles(articletitle)");
			db.modify("CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(" +
					"'derby.storage.pageSize'," +
			"'8192')");
		//System.out.println("Created table articles");
			
			//Create the users table
			sb = new StringBuilder();
			sb.append("CREATE TABLE users (");
			sb.append("userid INT NOT NULL " +
					"PRIMARY KEY GENERATED ALWAYS AS IDENTITY " +
					"(START WITH 1, INCREMENT BY 1),");
			sb.append("username VARCHAR( 255 ),");
			sb.append("userpassword VARCHAR(255),");
			sb.append("userview SMALLINT, ");
			sb.append("isActive SMALLINT");
			sb.append(")");
			db.modify(sb.toString());
		//System.out.println("Created table users");
		} catch (Exception e) {
			throw new DBException("Database setup failed: "+e);
		}
	}
	
	/**
	 * Database startup routine. Requires that the database be instantiated, 
	 * however, this is taken care of when the DBHandler is instantiated.
	 * 
	 * @throws DBHandlerException if DB fails to startup or setup.
	 */
	private void startup() throws DBHandlerException{
		try {
			//Attempt to startup the DB
			db.startup();
			/* Check to see if there are 3 user defined tables (namely feeds, 
			 * articles, and users).
			 */ 
			ResultSet rs = db.query("select count(TABLEID) from sys.SYSTABLES " +
					"where TABLETYPE like 'T'");
			rs.next();
			/* If there are more or less than 3 tables, it indicates that the
			 * DB is somehow malformed. Attempt setup to correct this.
			 */ 
			if (rs.getInt(1) != 3){
				try{
					setupDB();
				} catch (DBException e){
					throw new DBHandlerException("Database failed to setup: "
							+e);
				}
			}
			//Make sure there's at least one user
			rs = db.query("select count(userid) from users");
			rs.next();
			/* If there is somehow less than 1 user, it indicates that the
			 * DB setup routine has just been run. Add the default user and
			 * set it to active to correct this.
			 */ 
			if (rs.getInt(1) < 1){
				try{
					addUser("Default User", "");
					userUpdate("Default User", "", "isActive = 1");
				} catch (DBHandlerException e){
					throw new DBHandlerException("Database failed to setup: "
							+e);
				}
			}			
			assert repOK();
		} catch (Exception e) {
			/* If startup / setup fails, throw an error. This should be caught
			 * someplace and prompt the user to delete the database as it
			 * indicates a large and irreperable problem. 
			 */
			throw new DBHandlerException("Database failed to startup: "+e);
		}
	}
	
	/**
	 * Shuts down the database.
	 * 
	 * @return true if runs to completion
	 */
	public boolean shutdown() throws DBHandlerException{
		try {
			db.shutdown();
		} catch (DBException e) {
			throw new DBHandlerException("Database did not shutdown properly: "
					+ e);
		}
		return true;
	}
	
	/**
	 * Deletes all tables in the database, effectively reseting the DB to its
	 * original state.
	 * 
	 * @throws DBException if any of the database operations fail
	 */
	private void deleteDB() throws DBException {
		db.modify("DROP TABLE FEEDS");
		db.modify("DROP TABLE ARTICLES");
		db.modify("DROP TABLE USERS");
	}
	
	/**
	 * Resets the database to its initial state
	 * 
	 * @return true if the database is successfully reset
	 * @throws DBHandlerException if any of 
	 */
	public boolean resetDB() throws DBHandlerException{
		boolean result = true;
		try {
			deleteDB();
			setupDB();
			addUser("Default User", "");
			userUpdate("Default User", "", "isActive = 1");
			
			/* Check to see if there are 3 user defined tables (namely feeds, 
			 * articles, and users).
			 */ 
			ResultSet rs = db.query("select count(TABLEID) from sys.SYSTABLES " +
					"where TABLETYPE like 'T'");
			rs.next();
			/* If there are more or less than 3 tables, it indicates that the
			 * DB is somehow malformed. Attempt setup to correct this.
			 */ 
			if (rs.getInt(1) != 3){
				result = false;
				throw new DBHandlerException("Resetting the database failed to " +
						"properly setup tables");
			}
			
			//Make sure there's at least one user
			rs = db.query("select count(userid) from users");
			rs.next();
			/* If there is somehow less than 1 user, it indicates that the
			 * DB setup routine has just been run. Add the default user and
			 * set it to active to correct this.
			 */ 
			if (rs.getInt(1) < 1){
				result = false;
				throw new DBHandlerException("Resetting the database failed to " +
						"properly create a default user");
			}
		} catch (Exception e){
			result = false;
			throw new DBHandlerException("Resetting the database failed: "+e);
		}
		return result;
	}
	
	/**
	 * Makes sure that the current state does not violate the representation
	 * invarient.
	 * 
	 * @return true if none of the representation invarient conditions are
	 * violated 
	 */
	private boolean repOK(){
		boolean result = true;
		try {
			//Make sure each Feed's UAT is unique
			ResultSet rs = db.query("Select " +
					"count(distinct articleurl||articleauthor||articletitle), " +
					"count(articleurl||articleauthor||articletitle) " +
					"from articles " +
					"where articlefolder not in (" +
					"select min(feedid)+1 " +
					"from feeds " +
					"group by userid"+
					") " +
			"group by articlefolder");
			while(rs.next()){
				if (rs.getInt(1) != rs.getInt(2)){
				//System.out.println("Make sure each Feed's UAT is unique");
					result = false;
				}
			}
		} catch (Exception e){
		//System.out.println("Checking the representation invarient failed: "+e);
		}
		
		try {
			//Make sure each articleid is unique
			ResultSet rs = db.query("Select " +
					"count(distinct articleid), " +
					"count(articleid) " +
			"from articles ");
			while(rs.next()){
				if (rs.getInt(1) != rs.getInt(2)){
				//System.out.println("Make sure each articleid is unique");
					result = false;
				}
			}
		} catch (Exception e){
		//System.out.println("Checking the representation invarient failed: "+e);
		}
		
		try {
			//Make sure each feedid is unique
			ResultSet rs = db.query("Select " +
					"count(distinct feedid), " +
					"count(feedid) " +
			"from feeds ");
			while(rs.next()){
				if (rs.getInt(1) != rs.getInt(2)){
				//System.out.println("Make sure each feedid is unique");
					result = false;
				}
			}
		} catch (Exception e){
		//System.out.println("Checking the representation invarient failed: "+e);
		}
		
		try {
			//Make sure each userid is unique
			ResultSet rs = db.query("Select " +
					"count(distinct userid), " +
					"count(userid) " +
			"from users ");
			while(rs.next()){
				if (rs.getInt(1) != rs.getInt(2)){
				//System.out.println("Make sure each userid is unique");
					result = false;
				}
			}
		} catch (Exception e){
		//System.out.println("Checking the representation invarient failed: "+e);
		}
		
		try {
			//Make sure each username is unique
			ResultSet rs = db.query("Select " +
					"count(distinct username), " +
					"count(username) " +
			"from users ");
			while(rs.next()){
				if (rs.getInt(1) != rs.getInt(2)){
				//System.out.println("Make sure each username is unique");
					result = false;
				}
			}
		} catch (Exception e){
		//System.out.println("Checking the representation invarient failed: "+e);
		}
		
		try {
			//Make sure there is only one user active at a time
			ResultSet rs = db.query("Select " +
					"count(userid) " +
					"from users " +
			"where isActive=1");
			while(rs.next()){
				if (rs.getInt(1) >1){
				//System.out.println(rs.getInt(1));
				System.out.println("Make sure there is only one user active " +
							"at a time");
					result = false;
				}
			}
		} catch (Exception e){
		//System.out.println("Checking the representation invarient failed: "+e);
		}
		
		try {
			//Make sure each Feed's URL is unique
			ResultSet rs = db.query("Select " +
					"count(distinct feedurl), " +
					"count(feedurl) " +
					"from feeds " +
			"group by userid");
			while(rs.next()){
				if (rs.getInt(1) != rs.getInt(2)){
				//System.out.println("Make sure each Feed's URL is unique");
					result = false;
				}
			}
		} catch (Exception e){
		//System.out.println("Checking the representation invarient failed: "+e);
		}
		
		try {
			/* Make sure each folder's title is unique in its folder (ie no
			 * siblings have the same name).
			 */
			ResultSet rs = db.query("Select " +
					"count(distinct feedtitle), " +
					"count(feedtitle) " +
					"from feeds " +
					"where isFolder = 1 and " +
					"feedfolder != 0 " +
			"group by feedfolder");
			while(rs.next()){
				if (rs.getInt(1) != rs.getInt(2)){
//				System.out.println("Make sure each folder's title is unique " +
//							"in its folder (ie no siblings have the same name).");
					result = false;
				}
			}
		} catch (Exception e){
		//System.out.println("Checking the representation invarient failed: "+e);
		}
		
		try {
			//Make sure the root does not exist
			ResultSet rs = db.query("Select " +
					"count(feedid) " +
					"from feeds " +
			"where feedid = 0");
			while(rs.next()){
				if (rs.getInt(1) != 0){
				//System.out.println("Make sure the root does not exist");
					result = false;
				}
			}
		} catch (Exception e){
		//System.out.println("Checking the representation invarient failed: "+e);
		}
		
		try {
			//Make sure each user has 3 top level folders that point to the root
			ResultSet rs = db.query("Select " +
					"count(feedid) " +
					"from feeds " +
					"where feedfolder = 0 " +
					"and (" +
					"feedtitle = 'Subscribed Feeds' OR " +
					"feedtitle = 'Outbox' OR " +
					"feedtitle = 'Trash'" +
					") " + "group by userid");
			while(rs.next()){
				if (rs.getInt(1) != 3){
//				System.out.println("Make sure each user has 3 top level " +
//							"folders that point to the root");
					result = false;
				}
			}
		} catch (Exception e){
		//System.out.println("Checking the representation invarient failed: "+e);
		}
		
		try {
			//Make sure subscribed feeds is the min(feedid) for each user
			ResultSet rs = db.query("Select " +
					"feedtitle " +
					"from feeds " +
					"where feedid in " +
					"(select "+
					"min(feedid) " +
					"from feeds " +
					"group by userid"
					+")");			
			while(rs.next()){
				if (!rs.getString(1).equals("Subscribed Feeds")){
				//System.out.println(rs.getString(1));
//				System.out.println("Make sure subscribed feeds is the " +
//							"min(feedid) for each user");
					result = false;
				}
			}
		} catch (Exception e){
		//System.out.println("Checking the representation invarient failed: "+e);
		}
		
		try {
			//Make sure Outbox is the min(feedid)+1 for each user
			ResultSet rs = db.query("Select " +
					"feedtitle " +
					"from feeds " +
					"where feedid in " +
					"(select "+
					"min(feedid)+1 " +
					"from feeds " +
					"group by userid"
					+")");			
			while(rs.next()){
				if (!rs.getString(1).equals("Outbox")){
//				System.out.println("Make sure Outbox is the min(feedid)+1 " +
//							"for each user");
					result = false;
				}
			}
		} catch (Exception e){
		//System.out.println("Checking the representation invarient failed: "+e);
		}
		
		try {
			//Make sure trash is the min(feedid)+2 for each user
			ResultSet rs = db.query("Select " +
					"feedtitle " +
					"from feeds " +
					"where feedid in " +
					"(select "+
					"min(feedid)+2 " +
					"from feeds " +
					"group by userid"
					+")");			
			while(rs.next()){
				if (!rs.getString(1).equals("Trash")){
//				System.out.println("Make sure trash is the min(feedid)+2 " +
//							"for each user");
					result = false;
				}
			}
		} catch (Exception e){
		//System.out.println("Checking the representation invarient failed: "+e);
		}
		
		try {
			//Make sure no article lists the trash as its parent
			ResultSet rs = db.query("Select " +
					"count(articleid) " +
					"from articles " +
					"where articlefolder in " +
					"(select min(feedid)+2 " +
					"from feeds " +
			"group by userid)");
			while(rs.next()){
				if (rs.getInt(1) != 0){
//				System.out.println("Make sure no article lists the trash " +
//							"as its parent");
					result = false;
				}
			}
		} catch (Exception e){
		//System.out.println("Checking the representation invarient failed: "+e);
		}
		
		try {
			//Make sure no folder / feed lists the trash as its parent
			ResultSet rs = db.query("Select " +
					"count(feedid) " +
					"from feeds " +
					"where feedfolder in " +
					"(select min(feedid)+2 " +
					"from feeds " +
			"group by userid)");
			while(rs.next()){
				if (rs.getInt(1) != 0){
//				System.out.println("Make sure no folder / feed lists the " +
//							"trash as its parent");
					result = false;
				}
			}
		} catch (Exception e){
		//System.out.println("Checking the representation invarient failed: "+e);
		}
		
		try {
			//Make sure no folder / feed lists the outbox as its parent
			ResultSet rs = db.query("Select " +
					"count(feedid) " +
					"from feeds " +
					"where feedfolder in " +
					"(select min(feedid)+1 " +
					"from feeds " +
			"group by userid)");
			while(rs.next()){
				if (rs.getInt(1) != 0){
//				System.out.println("Make sure no folder / feed lists the " +
//							"outbox as its parent");
					result = false;
				}
			}
		} catch (Exception e){
		//System.out.println("Checking the representation invarient failed: "+e);
		}
		
		try {
			//Make sure no folder / feed lists the outbox as its parent
			ResultSet rs = db.query("select " +
					"count(TABLEID) " +
					"from sys.SYSTABLES " +
			"where TABLETYPE like 'T'");
			while(rs.next()){
				if (rs.getInt(1) != 3){
//				System.out.println("Make sure no folder / feed lists the " +
//							"outbox as its parent");
					result = false;
				}
			}
		} catch (Exception e){
		//System.out.println("Checking the representation invarient failed: "+e);
		}
		return result;
	}
	
	/**
	 * Select compiles a string representing a SQL select query, executes that
	 * query, and then returns the ResultSet from that query. The fields array
	 * should be strings of the column names to be returned.
	 * 
	 * @param String[] fields
	 * @param String table
	 * @param String constraints
	 * @return ResultSet (Never null)
	 * @throws DBHandlerException if fields or table are null, or if the 
	 * database query corresponding to the select fails for some reason
	 */
	private ResultSet select(String[] fields, String table, String constraints)
	throws DBHandlerException {
		/* The query will fail if either fields or table are null; no need to 
		 * check 
		 */
		StringBuilder s = new StringBuilder();
		s.append("select ");
		// Add each field you'd like to return to the query
		for (String field : fields) {
			s.append(field + ", ");
		}
		// Remove the extra comma
		s.deleteCharAt(s.length() - 2);
		s.append("from " + table +" ");
		// Add constraints
		s.append(constraints);
		// Run the query
		try {
			ResultSet rs = db.query(s.toString());
			//The result should never be null, but just in case...
			if (rs == null) {
				throw new DBHandlerException("NO result returned from search. " +
				"(Should at least return an empty result).");
			} else {
				return rs;
			}
		} catch (DBException e) {
			throw new DBHandlerException("Select statement failed: " + e);
		}
	}
	
	/**
	 * Insert builds a string corresponding to a SQL insert of the specified 
	 * row values into 'table' and executes that query.
	 * 
	 * @param HashMap(String ColumnName, String Values)
	 * @param String Table
	 * @throws DBHandlerException if either the values or table are null, or 
	 * if the database query corresponding to the select fails for some reason
	 */
	private void insert(HashMap<String, String> values, String table)
	throws DBHandlerException {
		/* The query will fail if either values or table are null; no need to 
		 * check 
		 */
		
		/* Get a list of the keys in the HashMap. You really want something 
		 * that has order, as should become evident in a moment.
		 */
		ArrayList<String> fields = new ArrayList<String>(values.keySet());
		StringBuilder s = new StringBuilder();
		s.append("insert into " + table + " (");
		// Add each of the keys (fields in the DB) to the string
		for (String field : fields) {
			s.append(field + ", ");
		}
		// Remove the extra comma
		s.deleteCharAt(s.length() - 2);
		s.append(") values (");
		/* Add the values for each of the fields to the string. Using a list is
		 * important because these values must be in the same order as the keys
		 * above. A set / collection can't do this.
		 */
		for (String field : fields) {
			s.append(values.get(field) + ", ");
		}
		// Again, remove the comma
		s.deleteCharAt(s.length() - 2);
		s.append(")");
		// Run the query
		try {
			db.modify(s.toString());
		} catch (DBException e) {
			throw new DBHandlerException("Insert failed: " + e);
		}
	}

	/**
	 * Returns a list of articles satisfying the constraint. If no constraint 
	 * is specified, all articles will be returned.
	 * 
	 * @param String SQLconstraint (Has "Where"; Requires "AND")
	 * @return List(Article) (never null)
	 * @throws DBHandlerException if there is an error running the query or 
	 * creating the article object
	 */
	private List<Article> getArticles(String constraint)
	throws DBHandlerException {
		//The list of all potential fields
		/*String[] fields = { "articleid", "articletitle", "articleauthor",
				"articleurl", "articlecontent", "articlelastupdate",
				"articlepubdate", "articlereadtime", "articlefolder",
				"articleprevfolder", "articlerating", "articlevisited",
				"isRead", "isDeleted", "isActive" };*/
		
		// Fields to be retreived from the database for each article
		String[] fields = { "articleid", "articletitle", "articleauthor",
				"articleurl", "articlecontent", "articlepubdate", 
				"articlerating", "isRead", "articlefolder", "isActive" };
		// Run the query; Should never return null 
		ResultSet rs = select(fields, "articles", "Where userid = "
				+ getCurrentUserID() +" "+ constraint);
		/* List to be returned; Creating the ArrayList ensures that the method
		 * never returns null
		 */
		List<Article> results = new ArrayList<Article>();
		try {
			//For all articles in the result...
			while (rs.next()) {
				//Convert specified fields from a smallints to a Booleans
				boolean readBoolean, deletedBoolean,outboxBoolean;
				if (rs.getInt("isRead") == 1) {
					readBoolean = true;
				} else {
					readBoolean = false;
				}
				if (rs.getInt("isActive") == 0) {
					deletedBoolean = true;
				} else {
					deletedBoolean = false;
				}
				if (rs.getInt("articlefolder") == getOutbox().getId()){
					outboxBoolean = true;
				} else {
					outboxBoolean = false;
				}
				//Create a new article using the results
				Article article = new Article(
						rs.getInt("articleid"),
						rs.getInt("articlefolder"),
						rs.getString("articleauthor"),
						rs.getString("articletitle"),
						rs.getString("articleurl"),
						rs.getString("articlecontent"),
						rs.getTimestamp("articlepubdate"),
						readBoolean,
						deletedBoolean,
						outboxBoolean,
						rs.getInt("articlerating"));
				//Add the new article to the list of new articles being returned 
				results.add(article);
			}
		} catch (SQLException e) {
			throw new DBHandlerException(
					"Searching for articles with the constraint '"+constraint+"' " +
					"produced the following error: " + e);
		}
		return results;
	}
	
	/**
	 * Gets an Article based on its URL, title, author, and the feed it's in.
	 * To search in all folders, 0 (the root node) should be passed as the last
	 * term, the FeedID. NOTE: inactive, deleted articles will be returned.
	 * 
	 * @param String url
	 * @param String author
	 * @param String title
	 * @param int feedID
	 * @param String Constraint
	 * @return Article, unless no article is found, in which case the result is
	 * null
	 * @throws DBHandlerException if there is an error retrieving the articles 
	 * from the database, if more than one article is returned by the search,
	 * or if the feed passed in does not exist in the database 
	 */
	private Article getArticlebyFeedUAT(String url, String author, String title,
			Feed feed, String constraint) throws DBHandlerException {
		Article result = null;
		//Make searching within a feed optional
		String oFeed = "";
		//If the last term is not zero, add the folder constraint
		if (feed.getId() != 0) {
			try {
				getFeedbyID(feed.getId());
			} catch (DBHandlerException e){
				throw new DBHandlerException("Error while trying to get the " +
						"article "+title+" from feed " + feed.getTitle() 
						+ ": Feed does not exist in the database.");
			}
			oFeed = " AND articlefolder = " + feed.getId();
		}
		//Add the search constraints
		StringBuilder s = new StringBuilder();
		s.append("AND articleurl like '"+ url.replaceAll("'", "''")+"' ");
		s.append("AND articleauthor like '" + author.replaceAll("'", "''")+"' ");
		s.append("AND articletitle like '" + title.replaceAll("'", "''") + "' ");

		s.append(oFeed.replaceAll("'", "''"));
		//Run the search
		List<Article> articleList = getArticles(s.toString());
		//If there's more than one result, throw an error (not unique)
		if (articleList.size() > 1) {
			throw new DBHandlerException("More than one article '" + title
					+ "' found with author " + author + " in feed " + feed);
			//If there are no results, return null
		} else if (articleList.size() == 0) {
			result = null;
			//If there's one result, return it
		} else {
			result = articleList.get(0);
		}
		return result;
	}
	
	/**
	 * Gets an Article based on its Article ID.
	 * 
	 * @param int ID
	 * @return Article
	 * @throws DBHandlerException if there is an error retrieving the article 
	 * from the database or if more than one article is returned by the search
	 */
	private Article getArticlebyID(int ID) throws DBHandlerException {
		Article result;
		StringBuilder s = new StringBuilder();
		//Add the articleID constraint
		s.append("and articleid = " + ID +" ");
		//Get all articles matching the constraint
		List<Article> articleList = getArticles(s.toString());
		//If there's more or less than one, throw an error
		if (articleList.size() != 1) {
			throw new DBHandlerException("No article or more than one article " +
					"found matching ID " + ID);
		} else {
			//otherwise, return the result
			result = articleList.get(0);
		}
		return result;
	}

	/**
	 * Retrieves a list of articles from the database that are in the specified
	 * feed (based on feedID). Only active, non-deleted articles are returned.
	 * 
	 * @param Feed
	 * @return List(Article)
	 * @throws DBHandlerException if there is an error retrieving the articles from
	 * the database or if the Feed provided has no ID
	 */
	public List<Article> getArticlesbyFeed(Feed feed)
	throws DBHandlerException {
		try {
			//Make sure the feed exists
			getFeedbyID(feed.getId());
		} catch (DBHandlerException e){
			//If not, throw an error
			throw new DBHandlerException("Error while trying to get the articles" +
					" of feed " + feed.getTitle() + ": Feed does not exist in " +
			"the database or has no ID.");
		}
		//Otherwise, get the feed's articles
		StringBuilder s = new StringBuilder();
		s.append("and articlefolder = " + feed.getId() + " ");
		s.append("and isActive = 1 ");
		s.append("and isDeleted = 0 ");

		return getArticles(s.toString());
	}
	
	/**
	 * Gets a list of the articles in the trash
	 * 
	 * @return List<Article> in the Trash
	 * @throws DBHandlerException if the database query fails
	 */
	public List<Article> getTrashArticles() throws DBHandlerException {
		//Get the inactive, non-permanently deleted aritcles
		StringBuilder s = new StringBuilder();
		s.append("and isActive = 0 ");
		s.append("and isDeleted = 0 ");
		return getArticles(s.toString());
	}
	
	/**
	 * Gets a list of articles based on a search string. The search looks for
	 * the string in the author, title, and content of the article, depending
	 * on the flags set by the booleans. Only active, non-deleted articles are
	 * returned.
	 * 
	 * @param boolean byAuthor
	 * @param boolean byTitle
	 * @param boolean byContent
	 * @param String searchConstraint
	 * @return List (articles) matching the constraint in the fields specified
	 * @throws DBHandlerException if no search fields are selected or if the 
	 * database fails while performing the search.
	 */
	public List<Article> searchArticles(boolean byAuthor, 
			boolean byTitle, 
			boolean byContent, 
			String constraint) 
			throws DBHandlerException{
		try {
			//Make sure at least one field is selected
			if (!(byAuthor||byTitle||byContent)){
				throw new DBHandlerException("No search field was selected");
			}
			//Add the constraints
			StringBuilder sb = new StringBuilder();
			sb.append("AND (");
			if (byAuthor){
				sb.append(" LOWER(articleAuthor) like " +
				"LOWER('%" + constraint.replaceAll("'", "''") + "%') or");
			} 
			if (byTitle){
				sb.append(" LOWER(articletitle) like " +
				"LOWER('%" + constraint.replaceAll("'", "''") + "%') or");
			} 
			if (byContent){
				sb.append(" LOWER(articleContent) like " +
				"LOWER('%" + constraint.replaceAll("'", "''") + "%') or");
			}
			//Get rid of the extra 'or'
			sb.delete(sb.length()-3, sb.length());
			sb.append(") ");
			sb.append("and isActive = 1 ");
			sb.append("and isDeleted = 0 ");
			//Run the search
			return getArticles(sb.toString());
		} catch (Exception e){
			throw new DBHandlerException("There was an error while trying to " +
					"search for articles: "+ e); 
		}
		
	}
	
	/**
	 * Gets a list of articles within a feed based on a search string. The 
	 * search looks for the string in the author, title, and content of the 
	 * article, depending on the flags set by the booleans. Only active, 
	 * non-deleted articles are returned.
	 * 
	 * @param boolean byAuthor
	 * @param boolean byTitle
	 * @param boolean byContent
	 * @param feed
	 * @param String searchConstraint
	 * @return List (articles) matching the constraint in the fields specified
	 * @throws DBHandlerException if no search fields are selected or if the 
	 * database fails while performing the search.
	 */
	public List<Article> searchArticles(boolean byAuthor, 
			boolean byTitle, 
			boolean byContent, 
			Feed feed, 
			String constraint)
			throws DBHandlerException{
		try {
			//Make sure at least one field is selected
			if (!(byAuthor||byTitle||byContent)){
				throw new DBHandlerException("No search field was selected");
			}
			//Add the constraints
			StringBuilder sb = new StringBuilder();
			sb.append("AND (");
			if (byAuthor){
				sb.append(" LOWER(articleAuthor) like " +
				"LOWER('%" + constraint.replaceAll("'", "''") + "%') or");
			} 
			if (byTitle){
				sb.append(" LOWER(articletitle) like " +
				"LOWER('%" + constraint.replaceAll("'", "''") + "%') or");
			} 
			if (byContent){
				sb.append(" LOWER(articleContent) like " +
				"LOWER('%" + constraint.replaceAll("'", "''") + "%') or");
			}
			//Get rid of the extra 'or'
			sb.delete(sb.length()-3, sb.length());
			sb.append(") ");
			sb.append("and isActive = 1 ");
			sb.append("and isDeleted = 0 ");
			//Look in the folder
			sb.append("and articlefolder = " + feed.getId() + " ");
			//Run the search
			return getArticles(sb.toString());
		} catch (Exception e){
			throw new DBHandlerException("There was an error while trying to " +
					"search for articles: "+ e); 
		}
	}
	
	
	/**
	 * Gets a list of articles based on a search string. The search looks for
	 * the string in the author, title, and content of the article, depending
	 * on the flags set by the booleans. Only active, non-deleted articles are
	 * returned.
	 * 
	 * @param boolean byAuthor
	 * @param boolean byTitle
	 * @param boolean byContent
	 * @param String searchConstraint
	 * @return List (articles) matching the constraint in the fields specified
	 * @throws DBHandlerException if no search fields are selected or if the 
	 * database fails while performing the search.
	 */
	public List<Article> searchArticlesOrdered(boolean byAuthor, 
			boolean byTitle, 
			boolean byContent, 
			String constraint) 
			throws DBHandlerException{
		try {
			//Make sure at least one field is selected
			if (!(byAuthor||byTitle||byContent)){
				throw new DBHandlerException("No search field was selected");
			}
			//Add the constraints
			StringBuilder sb = new StringBuilder();
			sb.append("AND (");
			if (byAuthor){
				sb.append(" LOWER(articleAuthor) like " +
				"LOWER('%" + constraint.replaceAll("'", "''") + "%') or");
			} 
			if (byTitle){
				sb.append(" LOWER(articletitle) like " +
				"LOWER('%" + constraint.replaceAll("'", "''") + "%') or");
			} 
			if (byContent){
				sb.append(" LOWER(articleContent) like " +
				"LOWER('%" + constraint.replaceAll("'", "''") + "%') or");
			}
			//Get rid of the extra 'or'
			sb.delete(sb.length()-3, sb.length());
			sb.append(") ");
			sb.append("and isActive = 1 ");
			sb.append("and isDeleted = 0 ");
			//Add the ordering
			sb.append("order by articlepubdate");
			//Run the search
			return getArticles(sb.toString());
		} catch (Exception e){
			throw new DBHandlerException("There was an error while trying to " +
					"search for articles: "+ e); 
		}
		
	}
	
	/**
	 * Gets a random, unread article from the database. Only active,
	 * non-deleted articles are returned.
	 * 
	 * @return Article
	 * @throws DBHandlerException if there is an error retrieving the articles 
	 * from the database or if the user has no articles from which to choose a
	 * random one.
	 */
	public Article getArticleRandom() throws DBHandlerException {
		StringBuilder s = new StringBuilder();
		s.append("and isRead = 0 ");
		s.append("and isActive = 1 ");
		s.append("and isDeleted = 0 ");
		s.append("and userid = "+ getCurrentUserID() +" ");
		try{
			//Get all active articles
			List<Article> articleList = getArticles(s.toString());
			//IF the user has articles
			if (articleList.size() > 0) {
				//Come up with a random number within the size of the array
				Integer randomInt = new Integer(
						new Long(
								Math.round((
										articleList.size()-1)*
										Math.random())).toString());
				//And return the article at the index of the random number
				return articleList.get(randomInt);
			}
			else throw new DBHandlerException("Cannot get a random article: " +
					"this user has no active articles in the database");
		} catch (DBHandlerException e){
			throw new DBHandlerException("Error getting a random article: "+e);
		}
	}
	
	/**
	 * Returns a list of Feed objects which satisfy the constraint. If no
	 * constraint is specified, all Feeds will be returned.
	 * 
	 * @param String SQLconstraint (Has "Where"; Requires "AND")
	 * @return List(Feed) (never null)
	 * @throws DBHandlerException if there is an error running the query or an 
	 * error creating the Feed object
	 */
	private List<Feed> getFeeds(String constraint) throws DBHandlerException {
		//The list of all potential fields
		/*String[] fields = { "feedid", "feedtitle", "feedurl", "feeddesc",
				"feedpubdate", "feedlastupdate", "feedlastupdateuser",
				"feedicon", "feedreadtime", "feedfolder", "feedprevfolder",
				"isFolder", "isDeleted", "isActive" };*/
		//The list of fields the ResultSet will contain
		String[] fields = { "feedid", "feedtitle", "feedurl", "feeddesc",
				"feedpubdate", "feedlastupdate", "feedlastupdateuser",
				"feedfolder", "feedupdate" };
		/* Run the query on the feeds table with the constraint
		 * Should never return null
		 */
		ResultSet rs = select(fields, "feeds", "Where isFolder = 0 and userid = "
				+ getCurrentUserID() +" "+ constraint);
		
		/* List to be returned; Creating the ArrayList ensures that the method
		 * never returns null
		 */
		List<Feed> results = new ArrayList<Feed>();
		try {
			//For each feed returned
			while (rs.next()) {
				//Create a new Feed object using the results
				Feed feed = new Feed(rs.getInt("feedid"), 
						rs.getInt("feedfolder"),
						rs.getString("feedtitle"),
						rs.getString("feeddesc"),
						rs.getString("feedurl"),
						rs.getTimestamp("feedlastupdate"), 
						rs.getTimestamp("feedlastupdateuser"), 
						rs.getTimestamp("feedpubdate"),
						rs.getInt("feedupdate"));
				//And add the feed to the results
				results.add(feed);
			}
		} catch (SQLException e) {
			throw new DBHandlerException(					
					"Searching for feeds with the constraint '"+constraint+"' " +
					"produced the following error: " + e);
		}
		return results;
	}
	
	/**
	 * Gets a Feed based on its Feed ID.
	 * 
	 * @param int FeedID
	 * @return Feed
	 * @throws DBHandlerException if there is an error retrieving the feed 
	 * from the database, if the feedid is not unique, or if a feed with 
	 * the specified feedid does not exist.
	 */
	private Feed getFeedbyID(int ID) throws DBHandlerException {
		Feed result;
		//Get the list of feeds with the appropriate ID
		List<Feed> feedList = getFeeds("and feedid = " + ID+" ");
		//Make sure the result is unique
		if (feedList.size() != 1) {
			throw new DBHandlerException("No feed or more than one feed found " +
					"matching ID " + ID);
		} else {
			//Return the result
			result = feedList.get(0);
		}
		return result;
	}
	
	/**
	 * Gets a Feed based on its URL.
	 * 
	 * @param String URL
	 * @return Feed
	 * @throws DBHandlerException if there is an error retrieving the feed 
	 * from the database or if a feed with feedurl URL does not exist.
	 */
	private Feed getFeedbyURL(String URL) throws DBHandlerException {
		Feed result;
		//Get the list of feeds that match the URL
		List<Feed> feedList = getFeeds("and feedurl like '" 
				+ URL.replaceAll("'", "''") + "' ");
		//Make sure the result is unique
		if (feedList.size() != 1) {
			throw new DBHandlerException("No feed found matching URL " + URL);
		} else {
			//Return the result
			result = feedList.get(0);
		}
		return result;
	}
	
	/**
	 * Retrieves the Feed which is the parent of the specified Article.
	 * 
	 * @param Article
	 * @return Feed
	 * @throws DBHandlerException if there is an error retrieving the a unique
	 * parent Feed from the database or if an Article with the specified 
	 * articleid does not exist in the database.
	 */
	public Feed getParent(Article a) throws DBHandlerException{
		try {
			//Make sure the article exists
			getArticlebyID(a.getId());
		} catch (Exception e){
			throw new DBHandlerException("Error while trying to get the " +
					"parent of article " + a.getTitle() + ": article does " +
			"not exist in the database.");
		}
		//If so, return the parent
		return getFeedbyID(a.getParentid());
	}
	
	/**
	 * Retrieves the Feeds which are children of a specified folder.
	 * 
	 * @param Folder
	 * @return List(Feed)
	 * @throws DBHandlerException if there is an error retrieving the Feeds 
	 * from the database or if there is no Folder with the specified folderid
	 */
	public List<Feed> getChildrenFeeds(Folder f) throws DBHandlerException {
		try {
			//Make sure the folder exists
			getFolderbyID(f.getId());
		} catch (DBHandlerException e){
			throw new DBHandlerException("Error while trying to get the " +
					"children of folder " + f.getTitle() + ": Folder does " +
			"not exist in the database.");
		}
		//Run the query and return the feeds
		return getFeeds("and feedfolder = " + f.getId()+" ");
	}
	
	/**
	 * Gets all Feeds in the database for a given user.
	 * 
	 * @return List(Feed)
	 * @throws DBHandlerException if there is an error retrieving the feeds 
	 * from the database.
	 */
	public List<Feed> getFeedsAll() throws DBHandlerException {
		return getFeeds("");
	}
	
	/**
	 * Gets all the feeds to be updated in a given minute, based on absolute
	 * time, not the time since the program was started.
	 * 
	 * @return List(Feed)
	 * @throws DBHandlerException if there is an error retrieving the feeds 
	 * from the database.
	 */
	public List<Feed> getFeedstoUpdate() throws DBHandlerException{
		/* Add the constraint that if the current number of minutes since 
		 * midnight is divisible by the feedupdate interval, that feed should
		 * be updated. This effectively means that a feed with a five minute
		 * update interval would be updated at 12:00 am, 12:05 am, and not at
		 * repeated five minute intervals from the start of the program's
		 * running
		 */
		return getFeeds("and mod(hour(current_time)*60+minute(current_time)," +
				"feedupdate) = 0");
	}
	
	/**
	 * Returns a list of Folder objects which satisfy the constraint. If no
	 * constraint is specified, all Folders will be returned.
	 * 
	 * @param String SQLconstraint (Has "Where"; Requires "AND ")
	 * @return List(Folder) (never null)
	 * @throws DBHandlerException if there is an error running the query or an
	 * error creating the Folder object
	 */
	private List<Folder> getFolders(String constraint)
	throws DBHandlerException {
		//The list all fields the ResultSet could contain
		/*String[] fields = { "feedid", "feedtitle", "feedurl", "feeddesc",
				"feedpubdate", "feedlastupdate", "feedlastupdateuser",
				"feedicon", "feedreadtime", "feedfolder", "feedprevfolder",
				"isFolder", "isDeleted", "isActive" };*/
		//The list of fields the ResultSet will contain		
		String[] fields = { "feedid", "feedtitle", "feedfolder" };
		/* Run the query on the feeds table with the constraint
		 * Should never return null
		 */
		ResultSet rs = select(fields, "feeds", "Where isFolder = 1 and userid = "
				+ getCurrentUserID() +" "+ constraint);
		/* List to be returned; Creating the ArrayList ensures that the method
		 * never returns null
		 */
		List<Folder> results = new ArrayList<Folder>();
		try {
			//For each folder returned
			while (rs.next()) {
				//Create a new Folder object using the results
				Folder folder = new Folder(rs.getInt("feedid"),
						rs.getInt("feedfolder"), 
						rs.getString("feedtitle"));
				//And add the Folder object to the list of folders to be returned
				results.add(folder);
			}
		} catch (SQLException e) {
			throw new DBHandlerException(
					"Searching for folders with the constraint '"+constraint+"' " +
					"produced the following error: " + e);
		}
		return results;
	}
	
	/**
	 * Gets a Folder based on its FolderID.
	 * 
	 * @param int FolderID
	 * @return Folder
	 * @throws DBHandlerException if there is an error retrieving the Folder 
	 * from the database or if a Folder with the specified FolderID does not
	 * exist.
	 */
	private Folder getFolderbyID(int ID) throws DBHandlerException {
		Folder result;
		List<Folder> folderList = getFolders("and feedid = " + ID+" ");
		if (folderList.size() != 1) {
			throw new DBHandlerException("No folder found matching ID " + ID);
		} else {
			result = folderList.get(0);
		}
		return result;
	}
	
	/**
	 * Retrieves the Folder which is the parent of the specified Feed.
	 * 
	 * @param Feed
	 * @return Folder
	 * @throws DBHandlerException if there is an error retrieving the a unique
	 * parent Folder from the database or if a Feed the specified feedid does
	 * not exist in the database.
	 */
	public Folder getParent(Feed f) throws DBHandlerException {
		Folder result;
		try {
			//Get all potential parents
			List<Folder> tempList = getFolders("and feedid = " 
					+ f.getParentId() +" ");
			/* If the Feed does not exist, or if it has more or less than one 
			 * parent, throw an error
			 */
			if (tempList.size() != 1) {
				throw new DBHandlerException("Feed " + f.getTitle()
						+ " does not exist in the database, has no parent, " +
				"or has more than one parent.");
				//Otherwise, return the parent
			} else {
				result = tempList.get(0);
			}
		} catch (DBHandlerException e) {
			throw new DBHandlerException(
					"There was an error while trying to get the parent of feed "
					+ f.getTitle() + ": " + e);
		}
		return result;
	}
	
	/**
	 * Retrieves the Folder which is the parent of the specified Folder.
	 * 
	 * @param Folder
	 * @return Folder
	 * @throws DBHandlerException if there is an error retrieving the a unique
	 * parent Folder from the database or if a Folder the specified feedid does
	 * not exist in the database.
	 */
	public Folder getParent(Folder f) throws DBHandlerException {
		Folder result;
		try {
			//Get all potential parents
			List<Folder> tempList = getFolders("and feedid = " + 
					f.getParentId()+" ");
			/* If the Folder does not exist, or if it has more or less than one 
			 * parent, throw an error
			 */
			if (tempList.size() != 1) {
				throw new DBHandlerException("Folder " + f.getTitle()
						+ " does not exist in the database, has no parent, " +
				"or has more than one parent.");
				//Otherwise, return the parent
			} else {
				result = tempList.get(0);
			}
		} catch (DBHandlerException e) {
			throw new DBHandlerException(
					"There was an error while trying to get the parent of folder "
					+ f.getTitle() + ": " + e);
		}
		return result;
	}
	
	/**
	 * Retrieves the Folders which are children of a specified folder.
	 * 
	 * @param Folder
	 * @return List(Folder)
	 * @throws DBHandlerException if there is an error retrieving the Folders 
	 * from the database or if a Folder with folderid FolderID does not exist.
	 */	
	public List<Folder> getChildrenFolders(Folder f) throws DBHandlerException {
		try {
			//Make sure the folder exists
			getFolderbyID(f.getId());
		} catch (DBHandlerException e){
			throw new DBHandlerException("Error while trying to get the children" +
					" of folder " + f.getTitle() + ": Folder does not exist in " +
			"the database.");
		}
		return getFolders("and feedfolder = " + f.getId()+" ");
	}
	
	/**
	 * Retrieves the list of folders contained by the root folder.
	 * 
	 * @return List(Objcect)
	 * @throws DBHandlerException or if there is an error retrieving the 
	 * Subscribed Feeds Folder, Outbox Feed, or Trash Feed from the database
	 */
	public List<Object> getRoot() throws DBHandlerException {
		List<Object> results = new ArrayList<Object>();
		results.add(getSubscribedFeeds());
		results.add(getOutbox());
		results.add(getTrash());
		return results;
	}
	
	/**
	 * Gets the subscribed feeds folder
	 * 
	 * @return Folder outbox
	 * @throws DBHandlerException if the database query fails or if the folder
	 * is not unique for the user
	 */
	public Folder getSubscribedFeeds() throws DBHandlerException{
		//Check to see if there is a globally defined subscribed feeds folder
		if (currentSF==null){
			String[] fields = { "MIN(feedid)" };
			/* If Subscribed Feeds is not defined, run a query on the feeds 
			 * table to get the folder; Should never return null
			 */
			ResultSet rs = select(fields, "feeds", "Where isFolder = 1 and " +
					"userid = " + getCurrentUserID() + " and feedtitle like " +
							"'Subscribed Feeds'");
			try {
				//Check to make sure the folder is unique for the user
				if (rs.getFetchSize() != 1){
					throw new DBHandlerException("There is either more than one " +
					"or no 'Subscribed Feeds' folder for the current user.");
				}
				rs.next();
				/* Create a new Folder object using the results and set the
				 * global folder equal to the new folder
				 */				
				currentSF = getFolderbyID(rs.getInt(1));
				return currentSF;
			} catch (SQLException e) {
				throw new DBHandlerException(
						"A database error occured while getting the 'Subscribed " +
						"Feeds' folder: " + e);
			}
		} else {
			//If the global folder is defined, simply return it
			return currentSF;
		}
	}

	/**
	 * Gets the outbox feed
	 * 
	 * @return Folder outbox
	 * @throws DBHandlerException if the database query fails or if the feed
	 * is not unique for the user
	 */
	public Feed getOutbox() throws DBHandlerException{
		//Check to see if there is a globally defined outbox folder
		if (currentOutbox == null){
			String[] fields = { "MIN(feedid)" };
			/* If outbox is not defined, run a query on the feeds 
			 * table to get the feed; Should never return null
			 */
			ResultSet rs = select(fields, "feeds", "Where isFolder = 0 and userid = "
					+ getCurrentUserID() + " and feedtitle like 'Outbox'");
			/* List to be returned; Creating the ArrayList ensures that the method
			 * never returns null
			 */
			try {
				//Check to make sure the folder is unique for the user
				if (rs.getFetchSize() != 1){
					throw new DBHandlerException("There is either more than one " +
					"or no 'Outbox' feed for the current user.");
				}
				rs.next();
				/* Create a new feed object using the results and set the
				 * global outbox feed equal to the new folder
				 */		
				currentOutbox = getFeedbyID(rs.getInt(1));
				return currentOutbox;
			} catch (SQLException e) {
				throw new DBHandlerException(
						"A database error occured while getting the 'Outbox' " +
						"feed: " + e);
			}
		} else {
			//If the global outbox is defined, simply return it
			return currentOutbox;
		}
	}
	
	/**
	 * Gets the Trash feed
	 * 
	 * @return Feed trash
	 * @throws DBHandlerException if the database query fails or if the feed
	 * is not unique for the user
	 */
	public Feed getTrash() throws DBHandlerException {
		//Check to see if there is a globally defined trash folder
		if (currentTrash == null){
			String[] fields = { "MIN(feedid)" };
			/* If trash is not defined, run a query on the feeds 
			 * table to get the feed; Should never return null
			 */
			ResultSet rs = select(fields, "feeds", "Where isFolder = 0 and userid = "
					+ getCurrentUserID() + " and feedtitle like 'Trash'");
			/* List to be returned; Creating the ArrayList ensures that the method
			 * never returns null
			 */
			try {
				//Check to make sure the folder is unique for the user
				if (rs.getFetchSize() != 1){
					throw new DBHandlerException("There is either more than one " +
					"or no 'Trash' feed for the current user.");
				}
				rs.next();
				/* Create a new feed object using the results and set the
				 * global trash feed equal to the new folder
				 */		
				currentTrash = getFeedbyID(rs.getInt(1));
				return currentTrash;
			} catch (SQLException e) {
				throw new DBHandlerException(
						"A database error occured while getting the 'Trash' " +
						"feed: " + e);
			}
		} else {
			//If the global trash feed is defined, simply return it
			return currentTrash;
		}
	}
	
	/**
	 * Gets a user's userid based on their username.
	 * 
	 * @param String username
	 * @return int Userid
	 * @throws DBHandlerException if the database query fails, or if there is 
	 * more than one or no user with the specified username
	 */
	public int getUserID(String username) throws DBHandlerException{
		//Select the user's userid
		String[] fields = { "userid" };
		ResultSet rs = select(fields, 
				"users", 
				"where username like '"+username+"'");
		try {
			//Make sure there's only one user
			if (rs.getFetchSize()!=1){
				throw new DBHandlerException("There is either no user or more" +
						" than one user with username "+username);
			} else {
				//If there's only one user, return their userid
				rs.next();
				return rs.getInt("userid");
			}
		} catch (SQLException e) {
			throw new DBHandlerException(					
					"There was an error getting the userid for user "+ username +
					": "+ e);
		}
	}
	
	/**
	 * Returns the username of the current active user
	 * 
	 * @return String username
	 * @throws DBHandlerException if the database query fails, or if there is 
	 * more than one or no active 
	 */
	public String getCurrentUser() throws DBHandlerException{
		//Run a select query to get the username of the active user
		String[] fields = {"username"};
		// Run the query; Should never return null 
		ResultSet rs = select(fields, "users", "Where isActive = 1");
		try {
			//Make sure there is only one active user
			if (rs.getFetchSize()!=1){
				throw new DBHandlerException("There is either no active user or " +
				"more than one active user.");
			} else {
				//If there is one active user, return their username
				rs.next();
				return rs.getString("username");
			}
		} catch (SQLException e) {
			throw new DBHandlerException(
					"Getting the current user produced the following error: "
					+ e);
		}
	}
	
	/**
	 * Returns the userid of the active user.
	 * 
	 * @return int userid
	 * @throws DBHandlerException if the database query fails, or if there is 
	 * more than one or no user with the specified username or userid
	 */
	public int getCurrentUserID() throws DBHandlerException{
		//Checks if the global variable currentuserid has been set 
		if (currentUserID == 0){
			/* If not, find the current active user and set the global variable
			 * equal to the value returned by the database
			 */
			currentUserID = getUserID(getCurrentUser());
		}
		return currentUserID;
	}
	

	/**
	 * Gets a list of all usernames
	 * 
	 * @return List(String username) (never null)
	 * @throws DBHandlerException if the database query fails
	 */
	public List<String> getAllUsers() throws DBHandlerException{
		//Run a select query to get all usernames
		String[] fields = { "username" };
		ResultSet rs = select(fields, 
				"users", 
		"");
		//Using an array list ensures the result is never null 
		List<String> results = new ArrayList<String>();
		try {
			//Add each of the usernames from the select to the array
			while(rs.next()){
				results.add(rs.getString("username"));
			}
		} catch (SQLException e) {
			throw new DBHandlerException(					
					"There was an error getting the usernames for all users:"
					+ e);
		}
		//Return the results
		return results;
	}
	
	/**
	 * Gets the viewid of the view of the current user
	 * 
	 * @return int viewid
	 * @throws DBHandlerException if the database query fails or if there is
	 * more than one active user
	 */
	public int getCurrentUserView() throws DBHandlerException{
		//Run a select query to get the viewid for the current user
		String[] fields = { "userview" };
		ResultSet rs = select(fields, 
				"users", 
				"where userid = "+getCurrentUserID()+" ");
		try {
			//Make sure there's only one active user
			if (rs.getFetchSize()!=1){
				throw new DBHandlerException("There is either no user or more" +
						" than one user with username "+getCurrentUser());
			} else {
				//If there's only one user, return their viewid
				rs.next();
				return rs.getInt("userview");
			}
		} catch (SQLException e) {
			throw new DBHandlerException(					
					"There was an error getting the view for user "+ getCurrentUser() +
					": "+ e);
		}
	}
	
	/**
	 * Returns a count of the number of articles in the database which meet the
	 * condition of the constraint.
	 * 
	 * @param String constraint
	 * @return int Count(Articles)
	 * @throws DBException if the database query fails
	 */
	private int getArticlesCount(String constraint) throws DBException {
		int tempint;
		try {
			/* Run a select query to get the count of the number of articles 
			 * that meet the constraint
			 */
			StringBuilder s = new StringBuilder();
			s.append("select ");
			s.append("COUNT(DISTINCT articleid) ");
			s.append("from articles ");
			s.append("where userid = "+ getCurrentUserID() +" "
					+ constraint);
			ResultSet rs = db.query(s.toString());
			
			rs.next();
			tempint = rs.getInt(1);
		} catch (Exception e) {
			throw new DBException(
					"Error while trying to count the number of articles in the database: "
					+ e);
		}
		//Return the number of articles that meet the constraint
		return tempint;
	}

	/**
	 * Returns a count of the number of articles in the database whose author,
	 * title, or contents contain a substring matching the constraint.
	 *  
	 * @param String constraint
	 * @return int Count(Articles) that meet the constraint
	 * @throws DBHandlerException if the database query fails or if the
	 * contraint is empty
	 */
	public int getNumberArticles(String constraint) throws DBHandlerException{
		//Throw an error if there's no constraint
		if (constraint.length()==0)
			throw new DBHandlerException("Passed the empty string");
		try{
			/* Otherwise, return the count of the articles whose  author,
			 * title, or contents contain a substring matching the constraint.
			 */
			return getArticlesCount("and (LOWER(articletitle) like LOWER('%"+constraint+"%') or " +
					"LOWER(articleauthor) like LOWER('%"+constraint+"%') or "+
					"LOWER(articlecontent) like LOWER('%"+constraint+"%')) ");
		}catch(DBException e){
			throw new DBHandlerException("");
		}
		
	}
	
	/**
	 * Retrieves the number of articles from the database that are unread and 
	 * belong to the specified feed.
	 * 
	 * @param feed
	 * @return int
	 * @throws DBHandlerException if there is an error retrieving the articles from the
	 * database or if the Feed provided has no ID
	 */
	public int getNumberOfNewArticles(Feed feed)
	throws DBHandlerException {
		//Make sure the feed exists
		try {
			getFeedbyID(feed.getId());
		} catch (DBHandlerException e){
			throw new DBHandlerException("Error while trying to get the number " +
					"of new articles for feed " + feed.getTitle() + ": Feed " +
			"does not exist in the database.");
		}
		/*If the feed exists, return the count of the number of new, non-
		 * deleted articles
		 */
		StringBuilder s = new StringBuilder();
		s.append("and articlefolder = " + feed.getId() + " ");
		s.append("and isActive = 1 ");
		s.append("and isDeleted = 0 ");
		s.append("and isRead = 0 ");
		s.append("and userid = "+ getCurrentUserID() +" ");
		try {
			return getArticlesCount(s.toString());
		} catch (DBException e){
			throw new DBHandlerException("Database failed while trying to count " +
			"new articles");
		}
		
	}	

	/**
	 * Adds an article to the specified feed in the database.
	 * 
	 * @param Article
	 * @param Feed
	 * @return boolean true if runs to completion
	 * @throws DBHandlerException if the database query corresponding to the
	 * insert fails or if the feed isn't in the database.
	 */
	private boolean add(Article article, Feed feed) throws DBHandlerException {
		//Make sure the feed exists
		try {
			getFeedbyID(feed.getId());
		} catch (DBHandlerException e){
			throw new DBHandlerException("Adding article '"+article.getTitle()+
					"' to feed "+feed.getTitle()+" failed because the feed isn't "
					+"in the database.");
		}
		HashMap<String, String> a = new HashMap<String, String>();
		//Add each of the fields and their value to the hashmap
		a.put("articletitle", "'" + article.getTitle().replaceAll("'", "''")
				+ "'");
		a.put("articleauthor", "'" + article.getAuthor().replaceAll("'", "''")
				+ "'");
		a.put("articleurl", "'" + article.getUrl().replaceAll("'", "''") + "'");
		a.put("articlecontent", "'"
				+ article.getSummary().replaceAll("'", "''") + "'");
		a.put("articlelastupdate", "null");
		a.put("articlepubdate", "TIMESTAMP('"
				+ datetoString(article.getPubDate()) + "')");
		a.put("articlereadtime", "null");
		a.put("articlefolder", new Integer(feed.getId()).toString());
		a.put("articleprevfolder", "null");
		a.put("articlerating", "0");
		a.put("articlevisited", "0");
		a.put("isRead", "0");
		a.put("isDeleted", "0");
		a.put("isActive", "1");
		a.put("userid", new Integer(getUserID(getCurrentUser())).toString());
		
		try {
			//Adds the articles to the DB
			insert(a, "articles");
		} catch (DBHandlerException e) {
			throw new DBHandlerException("DB error while adding article "
					+ article.getTitle() + " to feed " + feed.getTitle() + ": "
					+ e);
		}
		return true;
	}
	
	/**
	 * Adds a list of articles to a specified feed.
	 * 
	 * @param List (Article)
	 * @param Feed
	 * @return boolean true if runs to completion
	 * @throws DBHandlerException if the database query fails or if the destination 
	 * feed does not exist
	 */
	private boolean addArticlestoFeed(List<Article> list, Feed f)
	throws DBHandlerException {
		for (Article a : list) {
			this.add(a, f);
		}
		return true;
	}
	
	/**
	 * Copies an article into a Feed
	 * 
	 * @param Article
	 * @param Feed destination
	 * @return true if the article is successfully added; false otherwise
	 * @throws DBHandlerException if the destination Feed does not exist, or if the 
	 * database operation fails
	 */
	public boolean copyArticle(Article source, Feed destination)
	throws DBHandlerException {
		boolean result = true;
		//Make sure the destination exists
		try {			
			getFeedbyID(destination.getId());
		} catch (Exception e){
			result = false;
			throw new DBHandlerException("Copying article " + source.getTitle() + "failed" +
					"because either the source article or the destination feed do not " +
					"exist in the database.");
		}
		//Add the article to its new feed
		add(source, destination);
		//Make sure it was properly copied over
		try{
			getArticlebyFeedUAT(source.getUrl(),
					source.getAuthor(), 
					source.getTitle(), 
					destination,
					"");
		} catch (DBHandlerException e){
			result = false;
			throw new DBHandlerException("Article copy failed");
		}
		assert repOK();
		return result;
	}
	
	/**
	 * Adds a feed to the specified folder in the database.
	 * 
	 * @param Feed
	 * @param Folder
	 * @throws DBHandlerException if the database query corresponding to the
	 * insert fails or if the destination folder doesn't exist in the database.
	 */
	private void add(Feed feed, Folder folder) throws DBHandlerException {
		try {
			getFolderbyID(folder.getId());
		} catch (DBHandlerException e){
			throw new DBHandlerException("Adding feed '"+feed.getTitle()+
					"' to folder "+feed.getTitle()+" failed because the folder" +
			" isn't in the database.");
		}
		HashMap<String, String> a = new HashMap<String, String>();
		//Add each of the fields and their value to the hashmap
		a.put("feedtitle", "'" + feed.getTitle().replaceAll("'", "''") + "'");
		a.put("feedurl", "'" + feed.getUrl().replaceAll("'", "''") + "'");
		a.put("feeddesc", "'" + feed.getDescription().replaceAll("'", "''")
				+ "'");
		a.put("feedpubdate", "TIMESTAMP('"
				+ datetoString(feed.getPublishedDate()) + "')");
		a.put("feedlastupdate", "TIMESTAMP('"
				+ datetoString(feed.getLastUpdatedByFeed()) + "')");
		a.put("feedlastupdateuser", "TIMESTAMP('"
				+ datetoString(feed.getLastUpdatedByUser()) + "')");
		a.put("feedreadtime", "0");
		a.put("feedfolder", new Integer(folder.getId()).toString());
		a.put("feedprevfolder", "null");
		a.put("feedupdate", "30");
		a.put("isFolder", "0");
		a.put("isDeleted", "0");
		a.put("isActive", "1");
		a.put("userid", new Integer(getUserID(getCurrentUser())).toString());
		
		try {
			//Add the feed to the DB
			insert(a, "feeds");
		} catch (DBHandlerException e) {
			throw new DBHandlerException("DB error while adding feed "
					+ feed.getTitle() + " to folder " + folder.getTitle() + ": "
					+ e);
		}
	}
	
	
	/**
	 * Adds a feed to a folder in the database.
	 * 
	 * @param Feed
	 * @param Folder
	 * @return Feed (with ID)
	 * @throws DBHandlerException if the feed already exists in the database, if the
	 * feed is not successfully added, or if the feed does not contain any
	 * articles 
	 */
	public Feed addFeed(Feed feed, Folder folder) throws DBHandlerException {
		Feed result;
		// If no folder is specified, add to the Subscribed root
		if (folder == null) {
			folder = getSubscribedFeeds();
		}
		// Make sure the feed doesn't already exist in the DB
		boolean inDB = true;
		try {
			getFeedbyURL(feed.getUrl());
		} catch (DBHandlerException e){
			inDB = false;
		}
		//If the feed isn't in the database
		if (!inDB){
			//Add the feed to the folder
			add(feed, folder);
			/* Retrieve the newly added feed in order to get the ID and make
			 * sure that the feed was successfully added
			 */ 
			try {
				result = getFeedbyURL(feed.getUrl());
			} catch (DBHandlerException e){
				throw new DBHandlerException("Feed " + feed.getTitle() +
						" was not successfully added.");
			}
			//If the original feed knows its children
			if (feed.knowsChildren()) {
				try {
					/* Add the articles in the parsed feed to the feed in the
					 * database 
					 */
					addArticlestoFeed(feed.getChildren(), result);
				} catch (IllegalAccessException e) {
					throw new DBHandlerException(
							"Feed "
							+ feed.getTitle()
							+ " had no articles to be added to the database: "
							+ e);
				}
			} else {
				throw new DBHandlerException("Feed " + feed.getTitle()
						+ " was not successfully added to the database");
			}
			assert repOK();
			return result;
		} else {
			throw new DBHandlerException("Feed " + feed.getTitle()
					+ " is already in the database");
		}
	}
	
	/**
	 * Adds a subfolder to a folder in the database.
	 * 
	 * @param Folder parent
	 * @param Folder child
	 * @throws DBHandlerException if the database query corresponding to the
	 * insert fails or if the parent isn't in the database.
	 */
	private void add(Folder parent, Folder child) throws DBHandlerException {
		//Make sure the parent is in the DB
		try {
			getFolderbyID(parent.getId());
		} catch (DBHandlerException e){
			throw new DBHandlerException("Adding subfolder '"+child.getTitle()+
					"' to folder "+parent.getTitle()+" failed because the folder" +
			" isn't in the database.");
		}
		HashMap<String, String> a = new HashMap<String, String>();
		//Add each of the fields and their value to the hashmap
		a.put("feedtitle", "'" + child.getTitle().replaceAll("'", "''") + "'");
		a.put("feedurl", "null");
		a.put("feeddesc", "null");
		a.put("feedpubdate", "null");
		a.put("feedlastupdate", "null");
		a.put("feedlastupdateuser", "null");
		a.put("feedreadtime", "0");
		a.put("feedfolder", new Integer(parent.getId()).toString());
		a.put("feedprevfolder", "null");
		a.put("isFolder", "1");
		a.put("isDeleted", "0");
		a.put("isActive", "1");
		a.put("userid", new Integer(getUserID(getCurrentUser())).toString());
		
		try {
			//Add the folder to the DB
			insert(a, "feeds");
		} catch (DBHandlerException e) {
			throw new DBHandlerException("DB error while adding subfolder "
					+ child.getTitle() + " to folder " + parent.getTitle()
					+ ": " + e);
		}
	}
	
	/**
	 * Adds a folder to another folder in the database.
	 * 
	 * @param Folder parent
	 * @param Folder child
	 * @return Updated Folder parent
	 * @throws DBHandlerException if the parent does not exist, if the
	 * database addition fails, or if the folder has the same name as a 
	 * potential sibling (Folder at the same depth).
	 */
	public Folder addFolder(Folder parent, Folder child)
	throws DBHandlerException {
		List<Folder> children = getChildrenFolders(parent);
		//Make sure that the folder name is unique
		for (Folder f: children){
			if(f.getTitle().equals(child.getTitle())){
				throw new DBHandlerException("A subfolder named" + 
						child.getTitle() + " already exists in folder " + 
						parent.getTitle());
			}
		}
		//Add the folder
		add(parent, child);
		assert repOK();
		//Make sure the folder was successfully added
		try {
			return getFolderbyID(parent.getId());
		} catch (DBHandlerException e){
			throw new DBHandlerException("Subfolder " + child.getTitle() +
					" was not successfully added to " + parent.getTitle());
		}
	}
	
	/**
	 * Adds a user to the database
	 * 
	 * @param String userName
	 * @param String password
	 * @return ture if runs to completion
	 * @throws DBHandlerException if the user is already in the DB or if the DB
	 * inserts fail 
	 */
	public boolean addUser(String userName, String password)
	throws DBHandlerException{
		boolean inDB = true;
		//Make sure the user isn't already in the DB
		try {
			getUserID(userName);
		} catch (DBHandlerException e){
			inDB = false;
		}
		//If they're not in the DB add the user and their root folders
		if (!inDB){
			//Add the user
			HashMap<String, String> fields = new HashMap<String, String>();
			fields.put("username", "'"+userName+"'");
			fields.put("userpassword", "'"+password+"'");
			fields.put("isActive", "0");
			try{
				insert(fields, "users");
			} catch (DBHandlerException e){
				throw new DBHandlerException("Error while trying to add user "+
						userName+": "+e);
			}	
			/* Add Subscribed Feeds Outbox, and trash; Should create feeds and add 
			 * those, but doing so doesn't let one set certain fields equal to 
			 * null.
			 */
			
			//Add Subscribed feeds
			fields = new HashMap<String, String>();
			fields.put("feedtitle", "'Subscribed Feeds'");
			fields.put("feedurl", "null");
			fields.put("feeddesc", "null");
			fields.put("feedpubdate", "null");
			fields.put("feedlastupdate", "null");
			fields.put("feedlastupdateuser", "null");
			fields.put("feedreadtime", "0");
			fields.put("feedfolder", "0");
			fields.put("feedprevfolder", "null");
			fields.put("isFolder", "1");
			fields.put("isDeleted", "0");
			fields.put("isActive", "1");
			fields.put("userid", new Integer(getUserID(userName)).toString());
			try{
				insert(fields, "feeds");
			} catch (DBHandlerException e){
				throw new DBHandlerException("Error while trying to add user "+
						userName+": "+e);
			}	
			//Add Outbox
			fields = new HashMap<String, String>();
			fields.put("feedtitle", "'Outbox'");
			fields.put("feedurl", "null");
			fields.put("feeddesc", "null");
			fields.put("feedpubdate", "null");
			fields.put("feedlastupdate", "null");
			fields.put("feedlastupdateuser", "null");
			fields.put("feedreadtime", "0");
			fields.put("feedfolder", "0");
			fields.put("feedprevfolder", "null");
			fields.put("isFolder", "0");
			fields.put("isDeleted", "0");
			fields.put("isActive", "1");
			fields.put("userid", new Integer(getUserID(userName)).toString());
			try{
				insert(fields, "feeds");
			} catch (DBHandlerException e){
				throw new DBHandlerException("Error while trying to add user "+
						userName+": "+e);
			}	
			
			//Add Trash
			fields = new HashMap<String, String>();
			fields.put("feedtitle", "'Trash'");
			fields.put("feedurl", "null");
			fields.put("feeddesc", "null");
			fields.put("feedpubdate", "null");
			fields.put("feedlastupdate", "null");
			fields.put("feedlastupdateuser", "null");
			fields.put("feedreadtime", "0");
			fields.put("feedfolder", "0");
			fields.put("feedprevfolder", "null");
			fields.put("isFolder", "0");
			fields.put("isDeleted", "0");
			fields.put("isActive", "1");
			fields.put("userID", new Integer(getUserID(userName)).toString());
			try{
				insert(fields, "feeds");
			} catch (DBHandlerException e){
				throw new DBHandlerException("Error while trying to add user "+
						userName+": "+e);
			}	
		} else {
			throw new DBHandlerException("Cannot create user "+userName+": " +
			"A user with that username already exists in the database.");
		}
		assert repOK();
		return true;
	}
	
	/**
	 * Modifies an article already in the database based on String.
	 * 
	 * @param Article a
	 * @param String (includs "set"; requires 'field = value') 
	 * @return Updated Article a
	 * @throws DBHandlerException if article a does not exist or is non-unique
	 * in the database, or if the database update operation fails
	 */
	private Article articleUpdate(Article a, String set)
	throws DBHandlerException {
		//Make sure the article is in the DB & unique
		try {
			getArticlebyID(a.getId());
		} catch (Exception e){
			throw new DBHandlerException("Error while trying to update article" +
					a.getTitle() + ": Article does not exist in the database.");
		}
		try {
			//Update the article
			db.modify("Update articles set " + set + " where articleid = "
					+ a.getId());
			assert repOK();
			//Return the updated article
			return getArticlebyID(a.getId());
		} catch (Exception e) {
			//If the query fails, throw an error
			throw new DBHandlerException(
					"Error while trying to update article " + a.getTitle()
					+ ": " + e);
		}
	}
	
	/**
	 * Set the read status of an article to read
	 * 
	 * @param Article a
	 * @return Updated Article a
	 * @throws DBHandlerException if article a does not exist or is non-unique
	 * in the database, or if the database update operation fails
	 */
	public Article markRead(Article a) throws DBHandlerException {
		return articleUpdate(a, " isRead = 1 ");
	}
	
	/**
	 * Set the read status of an article to unread
	 * 
	 * @param Article a
	 * @return Updated Article a
	 * @throws DBHandlerException if article a does not exist or is non-unique
	 * in the database, or if the database update operation fails
	 */
	public Article markUnread(Article a) throws DBHandlerException {
		return articleUpdate(a, " isRead = 0 ");
	}
	
	/**
	 * Set the rating of an article to int i
	 * 
	 * @param Article a
	 * @return Updated Article a
	 * @throws DBHandlerException if article a does not exist or is non-unique
	 * in the database, or if the database update operation fails
	 */
	public Article setRating(Article a, int i) throws DBHandlerException {
		return articleUpdate(a, " articlerating = " + i + " ");
	}
	
	/**
	 * Set the amount of time you spent reading an article.
	 * 
	 * @param Article a
	 * @param int i	The read time (amount of time spent reading article).
	 * @return Updated Article a
	 * @throws DBHandlerException if article a does not exist or is non-unique
	 * in the database, or if the database update operation fails
	 */
	public Article setReadTime(Article a, int i) throws DBHandlerException {
		return articleUpdate(a, " articlereadtime = " + i + " ");
	}
	
	/**
	 * Put an article in the Trash
	 * 
	 * @param Article a
	 * @return Updated Article a
	 * @throws DBHandlerException if article a does not exist or is non-unique
	 * in the database, or if the database update operation fails
	 */
	public Article setTrash(Article a) throws DBHandlerException {
		return articleUpdate(a, " isActive = 0 ");
	}
	
	/**
	 * Move an article out of the Trash
	 * 
	 * @param Article a
	 * @return Updated Article a
	 * @throws DBHandlerException if article a does not exist or is non-unique
	 * in the database, or if the database update operation fails
	 */
	public Article setUnTrash(Article a) throws DBHandlerException {
		return articleUpdate(a, " isActive = 1 ");
	}
	
	/**
	 * Permanently delete an article
	 * 
	 * @param Article a
	 * @return Updated Article a
	 * @throws DBHandlerException if article a does not exist or is non-unique
	 * in the database, or if the database update operation fails
	 */
	public Article setDelete(Article a) throws DBHandlerException {
		return articleUpdate(a, " isDeleted = 1 ");
	}
	
	/**
	 * "Empties" the trash. Articles remain in the database (but are invisible)
	 * until cleanup() is run. Should return a List(Article) with no elements
	 * if it runs to completetion
	 * 
	 * @return boolean True if runs to completion
	 * @throws DBHandlerException if there is an error running the query to
	 * empty the trash or an error getting the contents of the Trash after
	 * emptying it.
	 */
	public boolean emptyTrash() throws DBHandlerException {
		try {
			//Set the inActive articles (those in the trash) to deleted
			db.modify(" Update articles set isDeleted = 1 where " +
					"isActive = 0 and isDeleted = 0 and userid = "+ 
					getCurrentUserID() +" ");
		} catch (DBException e) {
			throw new DBHandlerException("Error while emptying trash: " + e);
		}
		assert repOK();
		return true;
	}
	
	/**
	 * Updates a field of a feed
	 * 
	 * @param Feed f
	 * @param String set (includs "set"; requires 'field = value') 
	 * @return updatedFeed f
	 * @throws DBHandlerException of the feed does not exist in the DB or if
	 * the DB operation fails
	 */
	private Feed feedUpdate(Feed f, String set)
	throws DBHandlerException{
		try {
			//Makes sure that the feed exists in the DB and is unique
			getFeedbyID(f.getId());
		} catch (Exception e){
			throw new DBHandlerException("Error while trying to update feed" +
					f.getTitle() + ": feed does not exist in the database.");
		}
		try {
			//Update the feed
			db.modify("Update feeds set " + set + " where feedid = "
					+ f.getId());
			//Return the updated feed
			assert repOK();
			return getFeedbyID(f.getId());
		} catch (Exception e) {
			//If the query fails, throw an error
			throw new DBHandlerException(
					"Error while trying to update feed " + f.getTitle()
					+ ": " + e);
		}
	}
	
	/**
	 * Sets how often a feed will be automatically updated
	 * 
	 * @param Feed f
	 * @param int updateInterval (minutes)
	 * @return updatedFeed f
	 * @throws DBHandlerException
	 */
	public Feed setFeedUpdateInterval(Feed f, int i)
	throws DBHandlerException{
		return feedUpdate(f, "feedupdate = "+i);
	}
	
	/**
	 * Sets the title a feed
	 * 
	 * @param Feed f
	 * @param String newTitle
	 * @return updatedFeed f
	 * @throws DBHandlerException
	 */
	public Feed setFeedTitle(Feed f, String title)
	throws DBHandlerException{
		return feedUpdate(f, "feedtitle = '"+title+"' ");
	}
	
	/**
	 * Moves a Feed from one parent folder to another
	 * 
	 * @param Feed source
	 * @param Folder destination
	 * @return boolean true if runs to completion
	 * @throws DBHandlerException if the source Feed or destination Folder
	 * does not exist, or if the database update operation fails
	 */
	public boolean moveFeed(Feed source, Folder destination)
	throws DBHandlerException {
		//Make sure the source and destination exist
		try {
			getFeedbyID(source.getId());
			getFolderbyID(destination.getId());
		} catch (DBHandlerException e){
			throw new DBHandlerException("Error while trying to move a Feed: " +
			"Either the source or destination are not in the database.");
		}
		try {
			//Update the location of the child
			db.modify("Update feeds set feedfolder = " + destination.getId()
					+ " where feedid = " + source.getId());
		} catch (DBException e) {
			throw new DBHandlerException("Error while moving folder "
					+ source.getTitle() + " to " + destination.getTitle()
					+ ": " + e);
		}
		assert repOK();
		return true;
	}
	
	/**
	 * Takes a feed from the parser (so it should have its children articles
	 * but no id) and updates the database appropriately. Returns a new feed
	 * object that has been updated appropriately.
	 * 
	 * @param Feed f
	 * @return UpdatedFeed f
	 * @throws DBHandlerException if the feed being updated is not already in
	 * the DB, if there is a problem adding the new articles to the DB, or if
	 * there is a problem getting the new feed after the update
	 */
	public Feed updateFeed(Feed f) throws DBHandlerException {
		try {
			// Get the "real" DB feed associated with the parser-supplied feed
			Feed updateme;
			try {
				updateme = getFeedbyURL(f.getUrl());
			} catch (DBHandlerException e) {
				throw new DBHandlerException(
						"Error while trying to update feed with title "
						+ f.getTitle() + ", URL " + f.getUrl()
						+ ": couldn't find original feed in database. "
						+ e);
			}
			// Make sure the feed knows its children
			if (f.knowsChildren()) {
				List<Article> articleList = f.getChildren();
				// For each of the articles in the feed
				for (Article a : articleList) {
					// Check to see if the articles are in the DB already
					if (getArticlebyFeedUAT(a.getUrl(), a.getAuthor(), a.getTitle(), updateme, "") == null) {
						// If not, add them
						add(a, updateme);
					}
				}
			}
			assert repOK();
			//Return the updated feed
			return getFeedbyID(updateme.getId());
		} catch (Exception e) {
			throw new DBHandlerException("Error while trying to update feed "
					+ f.getTitle() + ": " + e);
		}
	}
	
	/**
	 * This takes a list of feeds and runs update feed for each of them. 
	 * List(Feed) feedList should come from the parser, and each feed must 
	 * return true for knowsChildren to ensure that update to work properly.
	 * 
	 * @param List(Feed) feedList
	 * @return boolean true if runs to completion
	 * @throws DBHandlerException if there is a problem updating any feed in
	 * the list of feeds (including attempting to update a non-existant feed).
	 */
	public boolean updateAll(List<Feed> feedList) throws DBHandlerException {
		for (Feed f : feedList) {
			updateFeed(f);
		}
		return true;
	}
	
	/**
	 * Moves a subfolder from one parent to another
	 * 
	 * @param Folder source
	 * @param Folder destination
	 * @return boolean true if runs to completion
	 * @throws DBHandlerException if the source or destination Folders do not
	 * exist, or if the database update operation fails
	 */
	public boolean moveFolder(Folder source, Folder destination)
	throws DBHandlerException {
		//Make sure the source and destination exist
		try {
			getFolderbyID(source.getId());
			getFolderbyID(destination.getId());
		} catch (DBHandlerException e){
			throw new DBHandlerException("Error while trying to move a folder: " +
			"Either the source or destination are not in the database.");
		}
		try {
			//Move the source into the destination
			db.modify("Update feeds set feedfolder = " + destination.getId()
					+ " where feedid = " + source.getId());
		} catch (DBException e) {
			throw new DBHandlerException("Error while moving folder "
					+ source.getTitle() + " to " + destination.getTitle()
					+ ": " + e);
		}
		assert repOK();
		return true;
	}
	
	/**
	 * Updates a user's profile
	 * 
	 * @param String userName
	 * @param String password
	 * @param String set (includs "set"; requires 'field = value') 
	 * @return true if runs to completion
	 * @throws DBHandlerException if the user is not in the database or if the
	 * DB update fails
	 */
	private boolean userUpdate(String userName, String password, String set)
	throws DBHandlerException {
		String constraint = "";
		//If the password isn't blank, add it before the constraint
		if (!password.equals("")){
			constraint = "and userpassword like '"+password+"'";
		}
		//Make sure the user is in the DB
		try {
			getUserID(userName);
		} catch (DBHandlerException e){
			throw new DBHandlerException("Error trying to modify user "
					+userName+": " + "User is not in the database");
		}
		try {
			//Update the DB
			db.modify("Update users set " + set + " where username like '"
					+ userName+"' "+constraint);
		} catch (DBException e){
			throw new DBHandlerException("Attempting set "+set+" for user "
					+userName+" failed: "+ e);
		}
		assert repOK();
		return true;
	}
	
	/**
	 * Sets the current, active user
	 * 
	 * @param String userName
	 * @param String password
	 * @return true if runs to completion
	 * @throws DBHandlerException if the user does not exist or if the DB
	 * operation fails
	 */
	public boolean setCurrentUser(String userName, String password)
	throws DBHandlerException{
		try {
			//Update the user table entries
			//Set the currently active user to inactive
			userUpdate(getCurrentUser(),"", "isActive = 0");
			//active the user who will be active
			userUpdate(userName,password, "isActive = 1");
			/*Void the previously cached root folders and userid; these will be
			 * rebuilt automatically by other functions as they are called
			 */
			currentUserID = 0;
			currentSF = null;
			currentOutbox = null;
			currentTrash = null;
		} catch (DBHandlerException e){
			throw new DBHandlerException("Error while trying to set the current " +
					"user: "+e);
		}
		assert repOK();
		return true;
	}
	
	/**
	 * Set the current user's active view
	 * 
	 * @param int viewid
	 * @return true if runs to completion
	 * @throws DBHandlerException if the user is not in the database or if 
	 * the DB update operation fails
	 */
	public boolean setCurrentUserView(int i) throws DBHandlerException{
		userUpdate(getCurrentUser(),"", "userview = "+i);
		assert repOK();
		return true;
	}
	
	/**
	 * Deletes all articles from the specified feed in the database.
	 * 
	 * @param Feed f
	 * @throws DBHandlerException if the database operation fails, or if the
	 * feed has no valid ID
	 */
	private void deleteArticles(Feed f) throws DBHandlerException {
		//Make sure the feed exists
		try {
			getFeedbyID(f.getId());
		} catch (DBHandlerException e){
			throw new DBHandlerException("Error while trying to delete articles " +
					"from feed "+ f.getTitle() + ": Feed does not exist in the " +
			"database.");
		}
		//Delete all articles the in the folder 
		try {
			db.modify("Delete from articles where articlefolder = "
					+ f.getId());
		} catch (DBException e) {
			throw new DBHandlerException(
					"Error while trying to delete articles from feed "
					+ f.getTitle() + ": " + e);
		}
	}
	
	/**
	 * Deletes a feed from the database.
	 * 
	 * @param Feed f
	 * @throws DBHandlerException if the database operation fails, if the feed
	 * has no valid ID, or if the feed the user is trying to delete is
	 * Subscribed Feeds, the outbox, or the trash 
	 */
	private void deleteFeed(Feed f) throws DBHandlerException {
		//Make sure the feed exists
		try {
			getFeedbyID(f.getId());
		} catch (DBHandlerException e){
			throw new DBHandlerException("Error while trying to delete feed " 
					+ f.getTitle() + ": Feed does not exist in the database.");
		}
		try {
			//Make sure the feed isn't a top level folder
			if ((f.getId()==getOutbox().getId())||
					(f.getId()==getTrash().getId())||
					(f.getId()==getSubscribedFeeds().getId())){
				throw new DBHandlerException("You cannot delete " + f.getTitle()
						+ "; it is in the root");
			}
			//Delete the feed
			db.modify("Delete from feeds where feedid = " + f.getId());
		} catch (DBException e) {
			throw new DBHandlerException("Error while trying to delete feed "
					+ f.getTitle() + ": " + e);
		}
	}
	
	/**
	 * Unsubscribes a feed from the database. Deletes the specified feed and
	 * all its articles.
	 * 
	 * @param Feed
	 * @return boolean true if both the articles and feed are successfully
	 * removed
	 * @throws DBHandlerException if the feed does not exist, or if there is a
	 * error performing the database operation 
	 */
	public boolean unSubscribe(Feed f) throws DBHandlerException {
		/* Already checks to make sure it's not a top level feed via the
		 * call to deletefodler
		 */
		boolean result = true;
		try {
			//Delete the feed's articles
			deleteArticles(f);
			//Confirm the articles were deleted
			if (getArticlesbyFeed(f).size()==0){
				//If all the articles are gone, delete the feed
				deleteFeed(f);
				boolean success = false;
				//Then, check to make sure the feed was deleted
				try{
					getFeedbyID(f.getId());
				} catch (DBHandlerException e){
					success = true;
				}
				if (!success){
					result = false;
					throw new DBHandlerException("Feed "+ f.getTitle() + " was " +
					"not successfully removed");
				}
			} else {
				result = false;
				throw new DBHandlerException("Not all articles were " +
						"successfully removed");
			}
		} catch (DBHandlerException e) {
			result = false;
			throw new DBHandlerException("Error while trying to Unsubscribe from" +
					" feed " + f.getTitle() + ": " + e);
		}
		assert repOK();
		//Returns true if all articles were deleted and the feed was deleted
		return result;
	}	

	/**
	 * Deletes a folder from the database (permanently)
	 * 
	 * @param Folder
	 * @return boolean true if runs to completion
	 * @throws DBHandlerException if the folder is not in the database, if
	 * the database operation fails, or if the folder passed is the 
	 * 'Subscribed Feeds' folder
	 */
	public boolean deleteFolder(Folder f) throws DBHandlerException {
		//Make sure the folder is in the DB
		try {
			getFolderbyID(f.getId());
			if (f.getId() == getSubscribedFeeds().getId()){
				throw new DBHandlerException("Cannot delete the 'Subscribed " +
						"Feeds' folder");
			}
		} catch (DBHandlerException e){
			throw new DBHandlerException("Error while trying to delete folder "
					+ f.getTitle() + ": The folder is not in the database.");
		}
		//Unsubscribe from each of the folder's feeds
		List<Feed> feedsToRemove = getChildrenFeeds(f);
		for (Feed r:feedsToRemove){
			unSubscribe(r);
		}
		/* Recursively delete each of the folder's children folders; this will
		 * unsubscribe the user from any feeds buried in subfolders and 
		 * delete those folders
		 */
		List<Folder> foldersToRemove = getChildrenFolders(f);
		for (Folder r:foldersToRemove){
			deleteFolder(r);
		}
		try {
			db.modify("Delete from feeds where feedid = " + f.getId());
		} catch (DBException e) {
			throw new DBHandlerException("Error while trying to delete folder "
					+ f.getTitle() + ": " + e);
		}
		assert repOK();
		return true;
	}
	
	/**
	 * Delete's a user from the database. as well as their feeds, folders, and
	 * articles
	 * 
	 * @param String username
	 * @param String password
	 * @return true if runs to completion
	 * @throws DBHandlerException if the user does not exist or if the DB 
	 * deletion operations fail
	 */
	public boolean deleteUser(String username, String password)
	throws DBHandlerException{
		try {
			int userid;
			//Make sure the user exists
			try {
				userid = getUserID(username);
			} catch (DBHandlerException e){
				throw new DBHandlerException("User "+username+" could not be " +
				"deleted because the user is not in the database.");
			}
			//Delete users
			db.modify("Delete from users where userid = "+userid);
			//Delete feeds
			db.modify("Delete from feeds where userid = "+userid);
			//Delete articles
			db.modify("Delete from articles where userid = "+userid);
		} catch (DBException e){
			throw new DBHandlerException("There was an error while trying " +
					"to delete user "+username+": "+e);
		}
		assert repOK();
		return true;
	}
	
	/**
	 * Converts a date to a Timestamp friendly string.
	 * @param Date
	 * @return Timestamp(Date) --> String
	 */
	private String datetoString(Date d) {
		return new Timestamp(d.getTime()).toString();
	}
}