package com.rssoap.actions;

import com.rssoap.control.ControlException;
import com.rssoap.control.Controller;
import com.rssoap.model.Article;
import com.rssoap.view.NoItemSelectedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import java.util.List;

/**
 * Action responsible for marking an article as new.
 * @author pgroudas
 *
 */
/*
 * Depends on Article, Controller, and View
 */
public class MarkNewAction extends Action {
	/**
	 * Constructs a new MarkNewAction
	 *
	 */
	public MarkNewAction(){
		super("Mark as new");
		setImageDescriptor(ImageDescriptor.createFromFile(MarkNewAction.class,"/images/mail-new.png"));
	}
	/**
	 * Invoked to mark the selected article as new.
	 */
	public void run(){
		//gets the controller.
		Controller c = Controller.getApp();
		try{
			//gets the selected articles
			List<Article> selected = c.getActiveView().getSelectedArticles();
			for(Article a:selected){
				//marks the article as read
				c.markUnread(a);
				//marsk the local article file as read
				a.markRead(false);
				//updates the view
				c.getActiveView().updateArticle(a);
			}
			c.getActiveView().updateFeed(c.getActiveView().getSelectedFeed());
		}catch(ControlException e){
			//this shouldn't happen
			c.setStatus("Couldn't mark article as new");
		}catch(NoItemSelectedException e){
			//if they have no article selected, then show error in statusbar and return
			c.setStatus("No article selected");
		}
	}
}

