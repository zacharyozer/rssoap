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
 * Trash action is responsible for moving an article to the trash
 * @author pgroudas
 *
 */
/*
 * Depends on Controller, Article and View
 */
public class TrashAction extends Action {
	/**
	 * Constructs an Trash Action
	 */
	public TrashAction(){
		super("Move to Trash");
		setImageDescriptor(ImageDescriptor.createFromFile(TrashAction.class,"/images/delete.png"));
		setToolTipText("Move selected article to the trash");
	}
	/**
	 * Trashes selected article
	 */ 
	public void run(){
		//gets controller
		Controller c = Controller.getApp();
		try{
			//gets selected feed
			Feed f = c.getActiveView().getSelectedFeed();
			//gets selected article
			List<Article> selected = c.getActiveView().getSelectedArticles();
			//puts article in trash
			for(Article a:selected){
				c.setTrash(a);
			}
			//updates the view
			c.getActiveView().updateFeed(f);
			c.getActiveView().updateTrash();
			c.getActiveView().updateArticles(f);
		}
		catch(NoItemSelectedException e){
			//if theres no article selected then just return with a message
			c.setStatus("No Article Selected");
			return;
		}
		catch(ControlException e){
			//if theres some internal error, just return with a message
			c.setStatus(e.getMessage());
		}
	}
}
