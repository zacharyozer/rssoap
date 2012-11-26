package com.rssoap.dialogs;

import com.rssoap.control.ControlException;
import com.rssoap.control.Controller;
import com.rssoap.model.Article;
import org.eclipse.jface.wizard.Wizard;

import java.util.Date;
/**
 * Wizard class that is used to create a new article in a user's outbox.
 * @author pgroudas
 *
 */
public class NewArticleWizard extends Wizard {
	private ArticleTitlePage titlePage;
	private ArticleContentsPage contentsPage;
	
	/**
	 * Constructs a new NewArticleWizard
	 *
	 */
	public NewArticleWizard(){
		super();
		setWindowTitle("Create a new Article");
		titlePage = new ArticleTitlePage();
		contentsPage = new ArticleContentsPage();
		addPage(titlePage);
		addPage(contentsPage);
	}
	/**
	 * Gathers user's input from wizard, and from it creates a new article and adds it
	 * to the outbox.
	 * @return boolean
	 */
	@Override
	public boolean performFinish() {
		//creates a new article.
		Article a = new Article(titlePage.getAuthor(),titlePage.getTitle(),contentsPage.getUrl(),contentsPage.getSummary(),new Date());
		//gets controller
		Controller control = Controller.getApp();
		//copies article to outbox
		try{
			control.moveToOutbox(a);
		}catch(ControlException e){
			control.setStatus("Problem adding article");
			return false;
		}
		return true;
	}

}
