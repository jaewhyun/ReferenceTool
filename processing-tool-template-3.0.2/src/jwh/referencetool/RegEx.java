package jwh.referencetool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import java.io.*;
import java.net.URL;

class RegEx {
	String theWholeThing = "";
	String description ="";
	String examples = "";
	String name = "";
	String returns = "";
	String syntax = "";
	
	ArrayList<String> syntax = ArrayList<String>();
	ArrayList<String> parameterNames = ArrayList<String>();
	ArrayList<String> parameterDescs = ArrayList<String>();
	ArrayList<String> exampleImages = ArrayList<String>();
	ArrayList<String> exampleCode = ArrayList<String>();
	
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
	
	public RegeEx(URL htmlName) {
		theWholeThing = readHTML(htmlName);
		String[] tokens = split(theWholeThing,  "<!-- ==================================== CONTENT - Headers ============================ -->");
		tokens = split(tokens[1], "<!-- ==================================== FOOTER ============================ -->");
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
		Pattern pattern = Pattern.compile("<th scope=\"row\">Examples</th><td>(.+?(?=</td>))");
		Matcher matcher = pattern.matcher(theWholeThing);
		
		if(matcher.find()) {
			String examplesBlock = matcher.group(1);
			
			// parse out individual examples
			//"<div class=\"example\"><img src=(.+?(?=alt=\"example pic\" />))"
//			pattern = Pattern.compile("(?:<div class=\"example\"><(.+?(?=<alt=\"example pic\" \/>))<pre class=\"margin\">(.+?(?=/pre>))</pre>)");
			pattern = Pattern.compile("<div class=\"example\"><img src=(.+?(?=alt=\"example pic\" />))");
			matcher = pattern.matcher(examplesBlock);
			
			while(matcher.find()) {
				exampleImages.add(matcher.group(1));
			}
			
			pattern = Pattern.comiple("<pre >(.+?(?=</pre>))|<pre class=\"margin\">(.+?(?=</pre>))");
			matcher = pattern.matcher(exampleBlock);
			
			while(matcher.find()) {
				exampleCode.add(matcher.group(1));
				String examplecode = matcher.group(1);
			}
		}
	}
	
	public void parseDescription() {
		Pattern pattern = Pattern.compile("<th scope=\"row\">Description</th>\\s*<td>(.+?(?=</td>))");
		Matcher matcher = pattern.matcher(theWholeThing);
		
		if(matcher.find()) {
			description = matcher.group(1);
		}
	}
	
	public void parseSyntax() {
		Pattern pattern = Pattern.compile("<th scope=\"row\">Syntax</th><td><pre>(.+?(?=</pre>))");
		Matcher matcher = pattern.matcher(theWholeThing);
		
		if(matcher.find()) {
			syntax = matcher.group(1);
			println(syntax);
		}
	}
	
}
