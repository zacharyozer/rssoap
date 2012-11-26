package com.rssoap.actions;

import com.rssoap.control.Controller;
import com.rssoap.model.Article;
import com.rssoap.view.NoItemSelectedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
/**
 * Action responsible for submitting an article to the user's del.icio.us 
 * account.  
 * 
 * @author pgroudas
 *
 */
/*
 * Depends on Controller, Article and View
 */
public class DeliciousAction extends Action {
	/**
	 * Constructs a DeliciousAction
	 *
	 */
	public DeliciousAction(){
		super("Tag this article on your del.icio.us");
		setImageDescriptor(ImageDescriptor.createFromFile(DeliciousAction.class,"/images/delicious.gif"));
		setToolTipText("Submits this article to your del.icio.us account");
	}
	/**
	 * Invoked to submit the selected article to del.icio.us
	 */
	public void run(){
		//gets the controller
		Controller c = Controller.getApp();
		try{
			//gets the selected article
			Article selected = c.getActiveView().getSelectedArticle();
			//builds a string of the url required to submit article
			StringBuilder builder = new StringBuilder();
			builder.append("http://del.icio.us/post?v=4;url=");
			String url = URLEncoder.encode(selected.getUrl(),"UTF-8");
			builder.append(url);
			builder.append(";title=");
			builder.append(selected.getTitle());
			//sets the view's browser to the current url.
			c.getActiveView().setURL(builder.toString());
		}catch(NoItemSelectedException e){
			//if there is no article selected, then just return and show message in status bar
			c.setStatus("No article selected");
		}catch(UnsupportedEncodingException e){
			//some error with URLEncoder, shouldn't really happen with UTF-8
			c.setStatus("Error encoding url in UTF-8");
		}
	}
}
