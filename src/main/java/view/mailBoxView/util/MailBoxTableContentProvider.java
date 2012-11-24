package view.mailBoxView.util;

import java.util.List;

import model.Article;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
/**
 * Content provider for the table in the mailbox view.
 * @author pgroudas
 *
 */
public class MailBoxTableContentProvider implements IStructuredContentProvider {
	/**
	 * Constructs a new MailBoxTableContentProvider
	 */
	public MailBoxTableContentProvider(){
	}
	/**
	 * Gets the rows of the table.  Iterates through the list, removes any 
	 * elements that are not articles, and returns an array of the remaining 
	 * elements.
	 */
	public Object[] getElements(Object arg0) {
		if (arg0 instanceof List){
			List l = (List) arg0;
			for (Object o:l){
				//if an object isn't an article remove it.
				if(o instanceof Article){
					//do nothing
				}else l.remove(o);
			}
			return l.toArray();
		}else return new Object[0];
	}
	/**
	 * this implementation of MailBoxTableContentProvider does nothing for this method.
	 */
	public void dispose() {
		// nothing to dispose
	}
	/**
	 * this implementation of MailBoxTableContentProvider does nothing for this method.
	 */
	public void inputChanged(Viewer viewer, Object arg1, Object arg2) {
		//do nothing
	}
}
