package view.mailBoxView.util;

import model.Article;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * This class implements the sorting for the article table in the mail view
 * @author pgroudas
 *
 */
public class ArticleViewerSorter extends ViewerSorter {
	private static final int ASCENDING = 1;
	private static final int DESCENDING = 0;
	private int column;
	private int direction;
	
	/**
	 * Does the sort.  If it's a different column from the previous sort, do an 
	 * ascending sort.  If it's the same column as the last sort, toggle the sort direction.
	 * 
	 * @param column
	 */
	public void doSort(int column){
		if (column == this.column){
			//same column as last sort, toggle the direction
			direction = 1-direction;
		}else{
			//new column; do an ascending sort
			this.column = column;
			direction = ASCENDING;
		}
	}
	/**
	 * Compares the object for sorting
	 * @param viewer Viewer
	 * @param e1 Object
	 * @param e2 Object
	 * @return int
	 */
	public int compare(Viewer viewer, Object e1, Object e2){
		int rc = 0;
		Article a1 = (Article) e1;
		Article a2 = (Article) e2;
		//Determine which column and do the appropriate sort.
		switch(column){
		case ColumnConstants.READ_STATUS:
			rc = a1.getRead() ? 1:-1;
			break;
		case ColumnConstants.DATE:
			rc = a1.getPubDate().after(a2.getPubDate()) ? 1 :-1;
			break;
		case ColumnConstants.AUTHOR:
			rc = collator.compare(a1.getAuthor(), a2.getAuthor());
			break;
		case ColumnConstants.TITLE:
			rc = collator.compare(a1.getTitle(),a2.getTitle());
			break;
		}
		if (direction == DESCENDING) rc = -rc;
		return rc;
	}
}
