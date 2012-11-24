package actions;

import java.util.List;

import model.Article;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import view.NoItemSelectedException;
import control.ControlException;
import control.Controller;
/**
 * Action responsible for marking an article as read.
 * @author pj
 *
 */
/*
 * Depends on Controller, Article, and View
 */
public class MarkReadAction extends Action {
	/**
	 * Constructs a new MarkReadAction
	 *
	 */
	public MarkReadAction(){
		super("Mark as read");
		setImageDescriptor(ImageDescriptor.createFromFile(MarkReadAction.class,"/images/mail-read.png"));
	}
	/**
	 * Invoked to mark the selected article as read.
	 */
	public void run(){
		//gets the controller.
		Controller c = Controller.getApp();
		try{
			//gets the selected articles
			List<Article> selected = c.getActiveView().getSelectedArticles();
			for(Article a:selected){
				//marks the article as read
				c.markRead(a);
				//updates the local article
				a.markRead(true);
				//updates the view
				c.getActiveView().updateArticle(a);
			}
			c.getActiveView().updateFeed(c.getActiveView().getSelectedFeed());
		}catch(ControlException e){
			//if theres a problem just return gracefully with a message
			c.setStatus("Couldn't mark article as read");
		}catch(NoItemSelectedException e){
			//if theres no article to mark read just set message and return
			c.setStatus("No article selected");
		}
	}
}
