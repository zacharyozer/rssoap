package view.fightView;

import java.io.File;
import java.io.FileWriter;
/**
 * A util class that offers a static method that generates the html for the 
 * fight view.
 * @author zozer
 *
 */
public class Util {
	/**
	 * Based on the two specified variables, writes an html file so that once loaded 
	 * it will show the 'google fights' type animation.
	 * @param i int count1
	 * @param j int count2
	 */
	public static void generateFight(int i, int j){
		StringBuilder s = new StringBuilder();
		s.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
		s.append("<html>");
		s.append("<head>");
		s.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		s.append("<title>Article Fight</title>");
		s.append("<link rel=\"stylesheet\" href=\"Styles/main.css\" type=\"text/css\">");
		s.append("<script type='text/javascript' src='Scripts/main.js'></script>");
		s.append("</head>");
		s.append("<body onload='doFight("+i+", "+j+")'>");
		s.append("<div id='content'>");
		s.append("<div id='writein'></div>");
		s.append("<div id='writein2'></div>");
		s.append("</div>");
		s.append("</body>");
		s.append("</html>");
		try{
			//builds html string and writes it to a file
			String output = s.toString();
			File outfile = new File("articlefight/articlefight.html");
			FileWriter out = new FileWriter(outfile);
			out.write(output);
			out.close();
		} catch (Exception e){
		//System.out.println("shit");
		}

	}
}
