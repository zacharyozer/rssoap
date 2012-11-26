package com.rssoap.actions;

import com.rssoap.control.Controller;
import com.rssoap.model.Article;
import com.rssoap.view.NoItemSelectedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
/**
 * Action responsible for submitting an article to digg.
 * @author pj
 *
 */
/*
 * Depends on Controller, Article and View
 */
public class DiggAction extends Action {
	/**
	 * Constructs a new digg action
	 *
	 */
	public DiggAction(){
		super("Submit this article to Digg");
		setImageDescriptor(ImageDescriptor.createFromFile(DiggAction.class,"/images/digg.png"));
		setToolTipText("Submits this article to digg.com");
	}
	/**
	 * Invoked to submit article to Digg.com
	 */
	public void run(){
		//gets controller
		Controller c = Controller.getApp();
		try{
			//gets selected article
			Article selected = c.getActiveView().getSelectedArticle();
			//builds url required to submit article
			StringBuilder builder = new StringBuilder();
			builder.append("http://digg.com/submit?phase=2&url=");
			String url = URLEncoder.encode(selected.getUrl(),"UTF-8");
			builder.append(url);
			builder.append("&title=");
			builder.append(selected.getTitle());
			builder.append("&bodytext=");
			builder.append(selected.getSummary());
			//sets the view's url to the url that was just built.
			c.getActiveView().setURL(builder.toString());
		}catch(NoItemSelectedException e){
			//no article was selected, just report to user and return
			c.setStatus("No article selected");
		}catch(UnsupportedEncodingException e){
			//this shouldn't happen, but for whatever if it does, just inform
			//user and return.
			c.setStatus("Error encoding url in UTF-8");
		}
	}
}
