package model;

import java.text.DateFormat;
/**
 * Util class holds static methods to perform tasks.  In particular, the generateHTML() method 
 * generates the html for the article summaries
 * @author mherdeg,zozer
 *
 */
public class Util {
	/**
	 * Generates the html of the summary for the specified article
	 * @param a Article
	 * @return String html
	 */
	public static String generateHTML(Article a){		
		StringBuilder s = new StringBuilder();
		s.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		s.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		s.append("<head>");
		s.append("<meta http-equiv=\"content-type\" content=\"text/html;charset=utf-8\" />");
		s.append("<meta name=\"generator\" content=\"Adobe GoLive\" />");
		s.append("<title>Preview Window</title>");
		s.append("<style type=\"text/css\" media=\"all\"><!--");
		s.append("--></style>");
		s.append("</head>");
		s.append("<body>");
		s.append("<style type=\"text/css\">");
		s.append(".title{");
		s.append("display:block;");
		s.append("}");
		s.append(".title *{");
		s.append("display:block;");
		s.append("height:1px;");
		s.append("overflow:hidden;");
		s.append("background:#C3D9FF;");
		s.append("}");
		s.append(".title1{");
		s.append("border-right:1px solid #e5eeff;");
		s.append("padding-right:1px;");
		s.append("margin-right:3px;");
		s.append("border-left:1px solid #e5eeff;");
		s.append("padding-left:1px;");
		s.append("margin-left:3px;");
		s.append("background:#d2e2ff;");
		s.append("}");
		s.append(".title2{");
		s.append("border-right:1px solid #f9fbff;");
		s.append("border-left:1px solid #f9fbff;");
		s.append("padding:0px 1px;");
		s.append("background:#cee0ff;");
		s.append("margin:0px 1px;");
		s.append("}");
		s.append(".title3{");
		s.append("border-right:1px solid #cee0ff;");
		s.append("border-left:1px solid #cee0ff;");
		s.append("margin:0px 1px;");
		s.append("}");
		s.append(".title4{");
		s.append("border-right:1px solid #e5eeff;");
		s.append("border-left:1px solid #e5eeff;");
		s.append("}");
		s.append(".title5{");
		s.append("border-right:1px solid #d2e2ff;");
		s.append("border-left:1px solid #d2e2ff;");
		s.append("}");
		s.append(".title_content{");
		s.append("padding:0px 5px;");
		s.append("background:#C3D9FF;");
		s.append("}");
		s.append("</style>");
		s.append("<div>");
		s.append("<b class=\"title\">");
		s.append("<b class=\"title1\"><b></b></b>");
		s.append("<b class=\"title2\"><b></b></b>");
		s.append("<b class=\"title3\"></b>");
		s.append("<b class=\"title4\"></b>");
		s.append("<b class=\"title5\"></b>");
		s.append("</b> <div class=\"title_content\">");
		s.append("<div style=\"float:right;\">");
		s.append(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(a.getPubDate()));
		s.append("</div>");
		s.append("<div>");
		s.append("<a href=\""+a.getUrl()+"\">");
		s.append(a.getTitle());
		s.append("</a>");
		s.append("</div>");
		s.append("<b class=\"title\">");
		s.append("<b class=\"title5\"></b>");
		s.append("<b class=\"title4\"></b>");
		s.append("<b class=\"title3\"></b>");
		s.append("<b class=\"title2\"><b></b></b>");
		s.append("<b class=\"title1\"><b></b></b>");
		s.append("</b>");
		s.append("</div>");
		s.append("<style type=\"text/css\">");
		s.append(".article{");
		s.append("display:block;");
		s.append("}");
		s.append(".article *{");
		s.append("display:block;");
		s.append("height:1px;");
		s.append("overflow:hidden;");
		s.append("background:#FFFFCC;");
		s.append("}");
		s.append(".article1{");
		s.append("border-right:1px solid #ffffe9;");
		s.append("padding-right:1px;");
		s.append("margin-right:3px;");
		s.append("border-left:1px solid #ffffe9;");
		s.append("padding-left:1px;");
		s.append("margin-left:3px;");
		s.append("background:#ffffd8;");
		s.append("}");
		s.append(".article2{");
		s.append("border-right:1px solid #fffff9;");
		s.append("border-left:1px solid #fffff9;");
		s.append("padding:0px 1px;");
		s.append("background:#ffffd5;");
		s.append("margin:0px 1px;");
		s.append("}");
		s.append(".article3{");
		s.append("border-right:1px solid #ffffd5;");
		s.append("border-left:1px solid #ffffd5;");
		s.append("margin:0px 1px;");
		s.append("}");
		s.append(".article4{");
		s.append("border-right:1px solid #ffffe9;");
		s.append("border-left:1px solid #ffffe9;");
		s.append("}");
		s.append(".article5{");
		s.append("border-right:1px solid #ffffd8;");
		s.append("border-left:1px solid #ffffd8;");
		s.append("}");
		s.append(".article_content{");
		s.append("padding:0px 5px;");
		s.append("background:#FFFFCC;");
		s.append("}");
		s.append("</style>");
		s.append("<div>");
		s.append("<b class=\"article\">");
		s.append("<b class=\"article1\"><b></b></b>");
		s.append("<b class=\"article2\"><b></b></b>");
		s.append("<b class=\"article3\"></b>");
		s.append("<b class=\"article4\"></b>");
		s.append("<b class=\"article5\"></b>");
		s.append("</b> <div class=\"article_content\">");
		s.append(a.getSummary());
		s.append("</div>");
		s.append("<b class=\"article\">");
		s.append("<b class=\"article5\"></b>");
		s.append("<b class=\"article4\"></b>");
		s.append("<b class=\"article3\"></b>");
		s.append("<b class=\"article2\"><b></b></b>");
		s.append("<b class=\"article1\"><b></b></b>");
		s.append("</b>");
		s.append("</div>");
		s.append("</body>");
		s.append("</html>"); 

		return s.toString().replaceAll("</p>", "<br/>").replaceAll("<p>", "<br/>");
	}
}
