package com.rssoap.view.mailBoxView.util;

import com.rssoap.model.Article;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
/**
 * Label provider for the table in the mailbox view.
 * @author pgroudas
 *
 */
public class MailBoxTableLabelProvider implements ITableLabelProvider {
	private Image read, unread;
	/**
	 * Constructs a new MailBoxTableLabelProvider.
	 *
	 */
	public MailBoxTableLabelProvider(){
		//instantiates private images
		try{
			read = new Image(null, new FileInputStream("images/mail-read.png"));
			unread = new Image(null, new FileInputStream("images/mail-new.png"));
		}catch(FileNotFoundException e){
		//System.out.println("couldn't make image: "+e);
			read = null;;
			unread = null;
		}
	}
	/**
	 * Returns the image for a particular cell.  
	 * @param element
	 * @param columnIndex
	 * @return Image
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		//if the column is the read status column, returns an image depending 
		//on the read status, otherwise returns null.
		Article a = (Article) element;
		switch(columnIndex){
		case ColumnConstants.DATE:
			return null;
		case ColumnConstants.TITLE:
			return null;
		case ColumnConstants.AUTHOR:
			return null;
		case ColumnConstants.READ_STATUS:
			//if article is in trash or outbox, read status isn't meaningful so no image
			if (a.isInTrash()||a.isInOutbox())
				return null;
			else{
				boolean isRead = a.getRead();
				return isRead ? read : unread;
			}
		}
		return null;
	}
	/**
	 * Returns the text for a particular cell.
	 * @param element
	 * @param columnIndex
	 */
	public String getColumnText(Object element, int columnIndex) {
		//for the date, title, and author columns, returns appropriate
		//info fetched from the article, returns null for read status because
		//that is depicted with an image.
		Article a = (Article) element;
		switch(columnIndex){
		case ColumnConstants.DATE:
			return DateFormat.getDateTimeInstance(
		            DateFormat.MEDIUM, DateFormat.SHORT).format(a.getPubDate());
		case ColumnConstants.TITLE:
			return a.getTitle();
		case ColumnConstants.AUTHOR:
			return a.getAuthor();
		case ColumnConstants.READ_STATUS:
			return null;
		}
		return "";
	}
	/**
	 * This implementatino of ITableLabelProvider does nothign for this method.
	 */
	public void addListener(ILabelProviderListener arg0) {
		// do nothing
	}
	/**
	 * This implementatino of ITableLabelProvider does nothign for this method.
	 */
	public void dispose() {
		//dispose of images
		if (read != null) read.dispose();
		if (unread != null) unread.dispose();
	}
	/**
	 * This implementatino of ITableLabelProvider does nothign for this method.
	 */
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}
	/**
	 * This implementatino of ITableLabelProvider does nothign for this method.
	 */
	public void removeListener(ILabelProviderListener arg0) {
		//do nothing
	}

}
