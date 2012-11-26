package com.rssoap.view.mailBoxView.util;

import com.rssoap.control.ControlException;
import com.rssoap.control.Controller;
import com.rssoap.model.Feed;
import com.rssoap.model.Folder;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import java.util.ArrayList;
import java.util.List;
/**
 * MailBoxTreeContentProvider is a class to provide the content to the tree in a 
 * MailBoxView
 * @author pgroudas
 *
 */
public class MailBoxTreeContentProvider implements ITreeContentProvider {
	private Controller control;
	/**
	 * Constructs a new MailBoxTreeContentProvider
	 * @param c
	 */
	public MailBoxTreeContentProvider(Controller c){
		control = c;
	}
	/**
	 * Gets the children of the specified object in the tree.
	 * @param arg0
	 * @return Object[]
	 */
	public Object[] getChildren(Object arg0) {
		try{
			//if its a feed, it has no children
			if (arg0 instanceof Feed){
				return new Object[0];
			}
			//if its a folder, its all the sub folders appended to all the
			//children feeds
			if(arg0 instanceof Folder){
				Folder f = (Folder) arg0;
				List<Folder> folders = control.getChildrenFolders(f);
				List<Feed> feeds = control.getChildrenFeeds(f);
				List<Object> l = new ArrayList<Object>();
				l.addAll(folders);
				l.addAll(feeds);
				return l.toArray();	
			}
			else return new Object[0];
		}catch(ControlException e){
			//if theres a problem, show it in the statusbar.
			control.setStatus("Problem getting children: "+e.getMessage());
			return new Object[0];
		}
	}
	/**
	 * Gets the parent of a particular object in the tree.
	 * @param arg0
	 * @return Object
	 */
	public Object getParent(Object arg0) {
		try{
			//if its any folder but the subscribed feeds folder, just get
			//and return its parent
			if (arg0 instanceof Folder){
				Folder f = (Folder) arg0;
				if (f.getId()==control.getSubscribedFeeds().getId()){
					return null;
				}
				return control.getParent((Folder) arg0);
			}
			//if its any feed but the outbox or trash feed, just get and 
			//return its parent.
			else if (arg0 instanceof Feed){
				Feed f = (Feed) arg0;
				if (f.getId()==control.getTrash().getId()||f.getId()==control.getOutbox().getId())
					return null;
				else return control.getParent((Feed) arg0);
			}else return null;
		}catch(ControlException e){
			//if theres a problem, show it in statusbar
			//control.setStatus("Problem getting parent of item: "+e.getMessage());
			return new Object();
		}
	}
	/**
	 * Returns true if the specified item has any children.
	 * @param arg0
	 * @return boolean
	 */
	public boolean hasChildren(Object arg0) {
		return getChildren(arg0).length>0;
	}
	/**
	 * Gets the root elements for the tree.
	 * @param arg0
	 * @return Object[]
	 */
	public Object[] getElements(Object arg0) {
		try{
			//gets the root elements
			return control.getRoot().toArray();
		}
		catch(ControlException e){
			//show problem in statusbar
			//control.setStatus("Problem fetching root: "+e.getMessage());
			return new Object[0];
		}
	}
	/**
	 * This implementation of ITreeContentProvider does nothing for this method.
	 */
	public void dispose() {
		//nothing to dispose
	}
	/**
	 * This implementation of ITreeContentProvider does nothing for this method.
	 */
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		//nothing to change
	}
}
