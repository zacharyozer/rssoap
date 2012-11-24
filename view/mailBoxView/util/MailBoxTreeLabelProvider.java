package view.mailBoxView.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import model.Article;
import model.Feed;
import model.Folder;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import control.ControlException;
import control.Controller;
/**
 * Label provider for the tree in the mailbox view.
 * @author pgroudas
 *
 */
public class MailBoxTreeLabelProvider implements ILabelProvider {
	private Image folder,feed,article,trash,outbox;
	Controller control;
	/**
	 * Constructs a new MailBoxTreeLabelProvider.
	 *
	 */
	public MailBoxTreeLabelProvider(){
		try{
			folder = new Image(null, new FileInputStream("images/folder.png"));
			trash = new Image(null, new FileInputStream("images/trash.png"));
			feed = new Image(null, new FileInputStream("images/feed.png"));
			article = new Image(null, new FileInputStream("images/feed.png"));
			outbox = new Image(null, new FileInputStream("images/outbox.png"));
			control = Controller.getApp();
		}catch(FileNotFoundException e){
			folder = null;
			trash = null;
			feed = null;
			article = null;
			outbox = null;
			control = Controller.getApp();
		}
	}
	/**
	 * Gets the image to show for the specified item in the tree.
	 * @param o
	 * @return Image
	 */
	public Image getImage(Object o) {
		//compares the types and id's of object to find the correct image
		try{
			if (o instanceof Folder){
				return folder;
			}else if (o instanceof Feed){
				Feed f = (Feed) o;
				if (f.getId()==control.getTrash().getId())
					return trash;
				else if(f.getId()==control.getOutbox().getId())
					return outbox;
				else return feed;
			}else if (o instanceof Article){
				return article;
			}else return null;
		}catch(ControlException e){
			return null;
		}
	}
	/**
	 * Gets the text to display for an item in the tree.
	 * @param arg0
	 * @return String
	 */
	public String getText(Object arg0) {
		//makes comparisons to detemine what type of item it is
		//and returns appropriate text.  Feeds also show how many new articles
		//they contain, while the trash and out box merely show how many articles
		//they contain.  There is room for improvement of this implementation
		//because there are like 4 database queries for each feed label...
		if (arg0 instanceof Folder){
			return ((Folder) arg0).getTitle();
		}else if (arg0 instanceof Feed){
			Feed f = (Feed) arg0;
			try{
			if (f.getId()==control.getTrash().getId()){
					//int numNew = control.getArticles(f).size();
					//return new String("("+numNew+") "+f.getTitle());
					return f.getTitle();
			}else if (f.getId()==control.getOutbox().getId()){
					//int numNew = control.getArticles(f).size();
					//return new String("("+numNew+") "+f.getTitle());
					return f.getTitle();
			}else{
					int numNew = control.getNumberOfNewArticles(f);
					return new String("("+numNew+") "+f.getTitle());
			}
			}catch(ControlException e){
				return ((Feed) arg0).getTitle();
			}
		}else if (arg0 instanceof Article){
			return ((Article) arg0).getTitle();
		}else return null;
	}
	/**
	 * This implementation of ILabelProvider does nothign for this method.
	 */
	public void addListener(ILabelProviderListener arg0) {
		// do nothing
	}
	/**
	 * Disposes the label provider
	 */
	public void dispose() {
		if (folder != null) folder.dispose();
		if (feed != null) feed.dispose();
		if (article != null) article.dispose();
		if (trash != null) trash.dispose();
		if (outbox != null) outbox.dispose();
	}
	/**
	 * This implementation of ILabelProvider does nothign for this method.
	 */
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}
	/**
	 * This implementation of ILabelProvider does nothign for this method.
	 */
	public void removeListener(ILabelProviderListener arg0) {
		//do nothing
	}

}
