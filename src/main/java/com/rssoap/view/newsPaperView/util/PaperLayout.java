package com.rssoap.view.newsPaperView.util;

import com.rssoap.model.Article;
import com.rssoap.model.Feed;
import com.rssoap.parser.RomeFeedParser;

import java.util.ArrayList;
import java.util.List;
/**
 * Class responsible for generating the html for the newspaper view
 * @author mherdeg
 *
 */
public class PaperLayout {
	
	public static void main(String[] args) {
		try {
			Feed f = RomeFeedParser.ParseURL("http://www.nytimes.com/services/xml/rss/nyt/HomePage.xml");
		//System.out.println(MakeHTML(GetNewUnreadArticles(f.getChildren())));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Create a list of newly unread articles from a supplied list of articles.
	 * This is simple now, but could be extended to rank the articles by
	 * some supplied rating or measure of importance.
	 * @param articles	All articles (from, say, a feed).
	 * @return	List of articles.
	 */
	public static List<Article> GetNewUnreadArticles(List<Article> articles) {
		List<Article> chosenarticles = new ArrayList<Article>();
		for (Article article : articles) {
			if (!article.getRead()) chosenarticles.add(article);
//			 Note: an article will not be marked as read when it's clicked on. Sad but true.
		}
		/*for (Article article : articles) {
			chosenarticles.add(article);
		}*/
//		 Collections.sort(chosenarticles, new ArticleComparator());

//		Commented out: we could sort by age, but instead we sort by
//		the order in which the feed provider presents new information. Based on
//		limited testing, the NY Times occasionally puts less important, but new, 
//		stories below the others. 
		
		return chosenarticles;
	}
	
	/**
	 * Turn a list of articles into an HTML page that displays a newspaper-like feed.
	 * This HTML page shows at most 21 things: six articles, plus (if the feed contains
	 * more than six articles) up to fifteen more articles in a pane on the right, Wall
	 * Street Journal-style.
	 * @param articles
	 * @return
	 */
	public static String MakeHTML(List<Article> articles) {
		StringBuilder sb = new StringBuilder();
		int numarticles = articles.size();
		int verybrieflysize;
		if (numarticles < 6) verybrieflysize = 0;
		else verybrieflysize = numarticles - 5;
		if (verybrieflysize > 15) verybrieflysize = 15;
		ArrayList<Article> briefs = new ArrayList<Article>();
		if (verybrieflysize > 0) {
			for (int i = 0; i < verybrieflysize ; i++) {
				briefs.add(articles.get(i + 5));
			}
		}
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		sb.append("<head>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" />");
		sb.append("<style type=\"text/css\">");
		sb.append("img { display : none;}");
		sb.append("body {");
			sb.append("margin: 0px;");
			sb.append("font: 14px Georgia, \"Times New Roman\", Times, serif;");
			sb.append("text-decoration: none;");
			sb.append("text-align:justify;");
		sb.append("}");
		sb.append("</style>");
		sb.append("</head>");
		sb.append("<BODY style=\"background:#D9D9D9\">");
		sb.append("<TABLE style=\"text-align: left; width: 100%; padding:10px\" border=\"0\">");
		sb.append("<TBODY>");

		
		// Rank one		
		if (numarticles > 0) {
			Article a = articles.get(0);
			sb.append("<TR><TD height=\"\" valign=\"top\" style=\"border-bottom:dashed; border-bottom-width:thin\">");
			sb.append("<H1>");
			sb.append("<A HREF=\"" + a.getUrl() + "\">" + a.getTitle() + "</A>");
			sb.append("</H1>");
			if (a.getAuthor().replaceAll(" ", "").length()>0){
				sb.append("<div>By: "+a.getAuthor()+"<div>");
			}
			sb.append(a.getSummary());
			sb.append("</TD>");
			
			if (briefs.size() > 0) {
				sb.append("<TD colspan=\"1\" rowspan=\"5\" width=\"10\"></TD>");
				sb.append("<TD colspan=\"1\" rowspan=\"5\" width=\"40%\" valign=\"top\" style=\"border-left:solid; border-left-width:thin; padding-left:10px\">");
				sb.append("<div style=\"background-color: #CCCC99; padding-bottom:15px;padding-right:15px; padding-left:15px; padding-top:1px\">");
				sb.append("<H2>Very Briefly</H2>");
				for (Article article : briefs) {
					sb.append("<A HREF=\"" + article.getUrl() + "\"><STRONG>" + article.getTitle() + "</A></STRONG>");
					String summary = article.getSummary();
//					System.out.println(summary);
					summary = summary.replaceAll("\n", " ");
					summary = summary.trim();
					summary = summary.replaceAll("<a.*\">", "");
					summary = summary.replaceAll("</a>", "");
					summary = summary.replaceAll("<i>", "");
					summary = summary.replaceAll("</i>", "");
					summary = summary.replaceAll("<b>", "");
					summary = summary.replaceAll("</b>", "");
					summary = summary.replaceAll("<div.*>", "");
					summary = summary.replaceAll("</div>", "");
					summary = summary.replaceAll("<img.*/>", "");
//					System.out.println(summary);
					if (summary.length() > 350) 
						summary = summary.substring(0, 350) + "...";
					if (!summary.equals("")) 
						sb.append(": " + summary);

					sb.append("<div align=\"center\" style=\"line-height:24px\">*&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*&nbsp;&nbsp;&nbsp;&nbsp;*</div>");
				}
				sb.append("</div>");
				sb.append("</TD>");
			}
			sb.append("</TR>");

			if (numarticles > 1) {
				a = articles.get(1);
				sb.append("<TR><TD height=\"\" valign=\"top\">");
				sb.append("<H2>");
				sb.append("<A HREF=\"" + a.getUrl() + "\">" + a.getTitle() + "</A>");
				sb.append("</H2>");
				if (a.getAuthor().replaceAll(" ", "").length()>0){
					sb.append("<div>By: "+a.getAuthor()+"<div>");
				}
				sb.append(a.getSummary());
				sb.append("</TD></TR>");
			}
			if (numarticles > 2) {
				a = articles.get(2);
				sb.append("<TR><TD height=\"\"valign=\"top\">");
				sb.append("<H3>");
				sb.append("<A HREF=\"" + a.getUrl() + "\">" + a.getTitle() + "</A>");
				sb.append("</H3>");
				if (a.getAuthor().replaceAll(" ", "").length()>0){
					sb.append("<div>By: "+a.getAuthor()+"<div>");
				}
				sb.append(a.getSummary());
				sb.append("</TD></TR>");
			}
			if (numarticles > 3) {
				a = articles.get(3);
				sb.append("<TR><TD height=\"\"valign=\"top\">");
				sb.append("<H4>");
				sb.append("<A HREF=\"" + a.getUrl() + "\">" + a.getTitle() + "</A>");
				sb.append("</H4>");
				if (a.getAuthor().replaceAll(" ", "").length()>0){
					sb.append("<div>By: "+a.getAuthor()+"<div>");
				}
				sb.append(a.getSummary());
				sb.append("</TD></TR>");
			}
			if (numarticles > 4) {
				a = articles.get(4);
				sb.append("<TR><TD height=\"\"valign=\"top\">");
				sb.append("<H4>");
				sb.append("<A HREF=\"" + a.getUrl() + "\">" + a.getTitle() + "</A>");
				sb.append("</H4>");
				if (a.getAuthor().replaceAll(" ", "").length()>0){
					sb.append("<div>By: "+a.getAuthor()+"<div>");
				}
				sb.append(a.getSummary());
				sb.append("</TD></TR>");
			}
		}
		
		sb.append("</TBODY></TABLE><BR></BODY></HTML>");
		//System.out.println(sb.toString());
		return sb.toString();
		
	}

}
