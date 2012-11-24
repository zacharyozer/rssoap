package model;
/**
 * Comparitor class to compare articles by date
 * @author mherdeg
 *
 */
public class ArticleComparator implements java.util.Comparator<Article> {
	/**
	 * Compare returns 1 if at was published before at, 0 if they were published 
	 * at the same time, and -1 if a1 was published after a2
	 * @param a1 Article
	 * @param a2 Article
	 * return int
	 */
	public int compare(Article a1, Article a2) {
		if (a1.getPubDate().before(a2.getPubDate())) return 1;
		else if (a2.getPubDate().before(a1.getPubDate())) return -1;
		else return 0;
	}
}
