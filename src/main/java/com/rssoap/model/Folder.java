package com.rssoap.model;

/**
 * Folder class represents a folder that can contain other folders and feeds.  There is 
 * a special case when a folder can contain articles (the trash folder).  How that is implemented 
 * is likely to change dramatically.
 * @author pgroudas
 *
 */
public class Folder{
	
	private int id = -1;
	private int parentid = -1;
	private String title;
	/**
	 * Constructor for creating a folder from the database, when the ID of its parent
	 * and the ID of itself and its title are all known. If title should happen to be 
	 * null (quite odd), sets to "". 
	 * 
	 * @param id
	 * @param parentid
	 * @param title
	 */
	public Folder(int id, int parentid, String title){
		this.id = id;
		this.parentid = parentid;
		if (title == null) this.title = "";
			else this.title = title;
	}
	/**
	 * "Small" constructor used by the GUI to add folders. Here the
	 * title is the only explicitly set parameter; id and parentid default
	 * to 0. 
	 * @param title
	 */
	public Folder(String title){
		if (title == null) this.title = "";
			else this.title = title;
	}
	/**
	 * Tests if two folders represent the same folder. Folders which have the same id
	 * are "the same" (name equivelance is not sufficient, since two folders with the same
	 * name can exist if they have different parents.) Note that this can only be used
	 * reliably on two folders created by the database, since otherwise two folders created
	 * with the "small" constructor might have the same id (0).
	 * @param f1 Folder
	 * @param f2 Folder 
	 * @return boolean
	 */
	static boolean isSame(Folder f1, Folder f2){
		return (f1.getId() == f2.getId());
	}
	/**
	 * Gets the folder's title. This should never be null (only "" if a null
	 * title was supplied).
	 * @return String title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * Gets the folder's id, if it was generated with an id; otherwise throws an error.
	 * @return int ID
	 * @throws IllegalAccessError	If the folder has no id (it was made and not taken from the database), throws an illegal access error.
	 */
	public int getId(){
		if (id == -1) {
			throw new IllegalAccessError("Attempted to access ID for a Folder with no ID set.");
		}
		return id;
	}
	/**
	 * Gets the id of the folder's parent, if it available.
	 * @return int parentid
	 * @throws IllegalAccessError	If the folder has no parentid (it was made and not taken from the database), throws an illegal access error.
	 */
	public int getParentId(){
		if (id == -1) {
			throw new IllegalAccessError("Attempted to access parentID for a Folder with no parentID set.");
		}
		return parentid;
	}
}