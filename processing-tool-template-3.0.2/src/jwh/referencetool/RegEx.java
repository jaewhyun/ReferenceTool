package jwh.referencetool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import java.io.*;
import java.net.URL;

public class RegEx {
	String theWholeThing = "";
	String description ="";
	String examples = "";
	String name = "";
	String returns = "";
	String syntax = "";
	
	ArrayList<String> syntaxlist = new ArrayList<String>();
	ArrayList<String> parameterNames = new ArrayList<String>();
	ArrayList<String> parameterDescs = new ArrayList<String>();
	ArrayList<String> exampleImages = new ArrayList<String>();
	ArrayList<String> exampleCode = new ArrayList<String>();
	
	private String readHTML(URL htmlName) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(htmlName.openStream()));
		String line;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		
		try {
			while ((line = in.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			
			return stringBuilder.toString();
		} finally {
			in.close();
		}
	}
	
	public RegEx(URL htmlName) {
		try {
			theWholeThing = readHTML(htmlName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] tokens = theWholeThing.split("<!-- ==================================== CONTENT - Headers ============================ -->");
		tokens = tokens[1].split("<!-- ==================================== FOOTER ============================ -->");
		String theWholeThing = tokens[0];
	}
	
	public void parseName() {
		Pattern pattern = Pattern.compile("<th scope=\"row\">Name</th>\\s*<td><h3>(.+?(?=</h3>))");
		Matcher matcher = pattern.matcher(theWholeThing);
		if(matcher.find()) {
			name = matcher.group(1);
		}
	}
	
	public void parseExamples() {
		//parse out entire block
		Pattern pattern = Pattern.compile("<tr class=\"\"><th scope=\"row\">Examples</th><td>([\\S\\s]+?(?=</td>))");
		Matcher matcher = pattern.matcher(theWholeThing);
		
		if(matcher.find()) {
			String examplesBlock = matcher.group(1);
			
			// parse out individual examples
			pattern = Pattern.compile("<div class=\"example\"><img src=(.+?(?=alt=\"example pic\" />))");
			matcher = pattern.matcher(examplesBlock);
			
			while(matcher.find()) {
				exampleImages.add(matcher.group(1));
			}
			
			pattern = Pattern.compile("<pre.*>\\n*\\s*(.+?(?=\\n*</pre>))");
			matcher = pattern.matcher(examplesBlock);
			
			while(matcher.find()) {
				exampleCode.add(matcher.group(1));
				String examplecode = matcher.group(1);
			}
		}
	}
	
	public void parseDescription() {
		Pattern pattern = Pattern.compile("<tr class=\"\">\\n*\\s*<th scope=\"row\">Description</th>\\n*\\s*<td>\\n*\\s*([\\S\\s]+?(?=\\n*</tr>))");
		Matcher matcher = pattern.matcher(theWholeThing);
		
		if(matcher.find()) {
			description = matcher.group(1);
		}
	}
	
	public void parseSyntax() {
		Pattern pattern = Pattern.compile("<th scope=\"row\">Syntax</th><td><pre>([\\S\\s]+?(?=</pre>))");
		Matcher matcher = pattern.matcher(theWholeThing);
		
		if(matcher.find()) {
			syntax = matcher.group(1);
			syntaxlist.add(syntax);
			System.out.println(syntax);
		}
	}
	
	public void parseParameters() {
		Pattern pattern = Pattern.compile("<th scope=\"row\">Parameters</th><td><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">([\\S\\s]+?(?=</table>))");
		Matcher matcher = pattern.matcher(theWholeThing);
		
		if(matcher.find()) {
			String paramsBlock = matcher.group(1);
			pattern = Pattern.compile("(?:<th scope=\"row\" class=\"code\">(.+?(?=</th>))</th>\\n*<td>(.+?(?=</td>))</td>)");
			matcher = pattern.matcher(paramsBlock);
			
			while(matcher.find()) {
				parameterNames.add(matcher.group(1));
				parameterDescs.add(matcher.group(2));
			}
		}
	}
	
	
	
}
