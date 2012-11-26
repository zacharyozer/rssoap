package com.rssoap.actions;

import com.rssoap.control.ControlException;
import com.rssoap.control.Controller;
import com.rssoap.model.Article;
import com.rssoap.model.Feed;
import com.rssoap.view.NoItemSelectedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import java.util.List;
/**
 * UnTrash action is responsible for restoring an article from the trash to its original location
 * @author pgroudas
 *
 */
/*
 * Depends on Controller, Article and View
 */
public class UnTrashAction extends Action {
	/**
	 * Constructs an UnTrash Action
	 */
	public UnTrashAction(){
		super("Restore article to original location");
		setImageDescriptor(ImageDescriptor.createFromFile(UnTrashAction.class,"/images/undelete.png"));
		setToolTipText("Remove article from trash bin");
	}
	/**
	 * UnTrashes selected article
	 */
	public void run(){
		//gets controller
		Controller c = Controller.getApp();
		try{
			//gets selected feed
			Feed f = c.getActiveView().getSelectedFeed();
			//gets selected article
			List<Article> selected = c.getActiveView().getSelectedArticles();
			//restores article from trash
			for(Article a:selected){
				c.setUnTrash(a);
			}
			//updates the view
			c.getActiveView().updateFeed(f);
			c.getActiveView().updateTrash();
			c.getActiveView().updateArticles(f);
		}
		catch(NoItemSelectedException e){
			//if article is selected then return with a message
			c.setStatus("No Article Selected");
			return;
		}
		catch(ControlException e){
			//if something broke then return with a message
			c.setStatus(e.getMessage());
		}
	}
}
