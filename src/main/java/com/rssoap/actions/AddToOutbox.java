package com.rssoap.actions;

import com.rssoap.control.ControlException;
import com.rssoap.control.Controller;
import com.rssoap.model.Article;
import com.rssoap.view.NoItemSelectedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import java.util.List;
/**
 * AddToOutbox Action is responsible for adding an article to the outbox.
 * @author pgroudas
 *
 */
/*
 * This class depends on Controller, View, and Article
 */
public class AddToOutbox extends Action {
	/**
	 * Constructs an AddToOutbox Action
	 */
	public AddToOutbox(){
		super("Copy to outbox");
		setImageDescriptor(ImageDescriptor.createFromFile(AddToOutbox.class,"/images/movetooutbox.png"));
		setToolTipText("Copies selected article to the outbox");
	}
	/**
	 * Copies selected article to the outbox.
	 */
	public void run(){
		//gets the controller
		Controller c = Controller.getApp();
		try{
			//gets the selected articles
			List<Article> selected = c.getActiveView().getSelectedArticles();
			
			for(Article a:selected){
				//moves the articles to the outbox
				c.moveToOutbox(a);
			}
			//updates the outboxes view
			c.getActiveView().updateOutbox();
		}catch(ControlException e){
			//some kind of internal error, just inform user
			c.setStatus("Could not copy article to outbox: "+e);
		}catch(NoItemSelectedException e){
			//no article was selected, set the message
			c.setStatus("No article selected");
		}
	}
}

