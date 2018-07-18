package jwh.referencetool;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import processing.app.Platform;

import java.awt.Desktop;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class setHTML extends JEditorPane {
	HTMLEditorKit editorkit;
	StyleSheet css = new StyleSheet();
	String name = "";
	String returns = "";
	String description = "";
	String syntax = "";
	
	ArrayList<String> parameterNames = new ArrayList<String>();
	ArrayList<String> parameterDescs = new ArrayList<String>();
	ArrayList<String> exampleImages = new ArrayList<String>();
	ArrayList<String> exampleCodes = new ArrayList<String>();
	
	public setHTML() {
		// not sure how this is used just yet
		this.setContentType("text/html");
		editorkit = new HTMLEditorKit();
		css = editorkit.getStyleSheet();
		setCSS();
		editorkit.setAutoFormSubmission(false);
		this.setEditorKit(editorkit);
		this.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				handleLink(e.getURL().toExternalForm());
					}
			}
		});
	}
	
	public void setCSS() {
		css.addRule("body {font-family: raleway; font-size:9px}");
		css.addRule(".sectionStyle {padding-top: 20px}");
		css.addRule(".widthStyle {width : 70px}");
		css.addRule(".sectionheaderStyle {width : 70px; valign: top}");
	}
	
	public void parseHTML(URL urlLink) {
		RegEx regexer = new RegEx(urlLink);
		name = regexer.parseName();
//		//System.out.println(name);
		regexer.parseExamples();
		exampleImages = regexer.get_exampleImages();
//		//System.out.println(exampleImages);
		exampleCodes = regexer.get_exampleCodes();
//		//System.out.println(exampleCodes);
		description = regexer.parseDescription();
		regexer.parseParameters();
		parameterNames = regexer.get_parameterNames();
//		//System.out.println(parameterNames);
		parameterDescs = regexer.get_parameterDescs();
//		//System.out.println(parameterDescs);
		syntax = regexer.parseSyntax();
		returns = regexer.parseReturns();
//		//System.out.println(returns);
		
		fillIn();
	}
	
	public void fillIn() {
		String finalexampleString = exampleString();
		String descriptionString = descriptionString();
		String syntaxString = syntaxString();
		String parameterString = parameterString();
		String returnString = returnString();
		
		String namestring = "<table>"
				+ "<tr valign= \"top\">"
				+ "<td class=\"widthStyle\"><u>Name</u></td>"
				+ "<td><b>" + name + "</b></td>"
				+ "</tr>" 
				+ "</table>";
		
		String total = namestring + finalexampleString + descriptionString + syntaxString + parameterString + returnString;
//		System.out.println(total);
		this.setText(total);
	}
	
	public String exampleString() {
		String hr = "<tr valign=\"top\"><td class=\"widthStyle\">&nbsp;</td><td><hr></td></tr>";
		String examples = "";
		String finalexampleString = "<table class=\"sectionStyle\">" 
				+ "<tr valign = \"top\"><td class=\"sectionheaderStyle\"><u>Examples</u></td>";
		if(exampleCodes.size() != 0) {
			for(int i = 0; i < exampleCodes.size(); i++) {
				String exampletr = "";

				if(i > 0) {
					exampletr = "<tr valign=\"top\"><td class=\"widthStyle\">&nbsp;</td>";
				}
				
				String imageLocation = "";
				if(exampleImages.size() != 0) {
					imageLocation = exampleImages.get(i).trim();
					String testString = "modes/java/reference/"+imageLocation;
					File imagefile = Platform.getContentFile(testString);
					testString = imagefile.toURI().toString();
					imageLocation = "<td><img src=\""+testString+"\"></td><td width = 10px>&nbsp;</td>";
				}
				
				String code = "";
				String returnCode = "";
				returnCode = exampleCodes.get(i).trim();
				
				String codeLines[] = returnCode.split("\\r?\\n");
				for(int j = 0; j < codeLines.length; j++) {
					System.out.println(codeLines[j]);
					if(codeLines[j].contains("//")) {
						codeLines[j] = codeLines[j].replaceAll("//", "<span style=\"color: #3d9a3e\">//");
						codeLines[j] = codeLines[j] + "</span>";
					} else {
						if(codeLines[j].matches("([0-9]*)")) {
							System.out.println("found number");
							ArrayList<String> numbers = new ArrayList<String>();
							Pattern pattern = Pattern.compile("([0-9]*)");
							Matcher matcher = pattern.matcher(codeLines[j]);
							
							while(matcher.find()) {
								System.out.println("here");
								String numberFound = matcher.group(1);
								numbers.add(matcher.group(1));
								System.out.println(numberFound);
							}
							
							for(int z = 0; z < numbers.size(); z++) {
								String replaceNew = "<span style=\"color:#dfbf3a\">" + numbers.get(z) + "</span>";
								codeLines[j] = codeLines[j].replace(numbers.get(z), replaceNew);
								System.out.println(codeLines[j]);
							}
						}
						
//						codeLines[j] = "<span style=\"color: #192cff\">" + codeLines[j];
//						codeLines[j] = codeLines[j] + "</span>";
					}
					
					code = code + codeLines[j] + "<br>";
				}
				
				System.out.println(code);

//				code = code.replaceAll("//", "<span style=\"color: #3d9a3e\">//");
				
				code = "<td valign = \"top\"><pre >"+ code + "</pre></td></tr>";
				
				if(i < (exampleCodes.size()-1) && imageLocation.equals("")) {
					code = code + hr;
				}
//				//System.out.println(code);
				
				if(!imageLocation.equals("")) {
					finalexampleString = finalexampleString + exampletr+imageLocation+code;
				} else {
					finalexampleString = finalexampleString + exampletr+code;
				}
			}
			
			finalexampleString = finalexampleString + "</table>";
		} else {
			finalexampleString = "";
		}

		
//		//System.out.println(finalexampleString);
//		//System.out.println("\\n");
		return finalexampleString;
	}
	
	public String descriptionString() {
		String descriptionstring = "<table class=\"sectionStyle\"><tr valign=\"top\"><td class=\"widthStyle\"><u>Description</u></td><td>";
		
		descriptionstring = descriptionstring + description + "</td></tr></table>";
		
//		//System.out.println(descriptionstring);
		return descriptionstring;
	}
	
	public String syntaxString() {
		String syntaxstring = "<table class=\"sectionStyle\"><tr valign=\"top\"><td class=\"widthStyle\"><u>Syntax</u></td><pre >";
		
		if(!syntax.equals("")) {
			syntax = syntax.trim().replaceAll("\\n", "<br>");
			
			syntaxstring = syntaxstring + syntax + "</pre></td></tr></table>";
	
		} else {
			syntaxstring = "";
		}
		
		return syntaxstring;
	}
	
	public String parameterString() {
		String parameterstring = "<table class=\"sectionStyle\"><tr valign=\"top\"><td class=\"widthStyle\"><u>Parameters</u></td>";
		String finalparamstring = "";
		if(parameterNames.size() != 0) {
			for(int i = 0; i < parameterNames.size(); i++) {
				String addon = "";
				if(i > 0) {
					addon = "<td class=\"widthStyle\">&nbsp;</td>";
				}
				
				String name = parameterNames.get(i);
				name = "<td class = \"widthStyle\"><b>"+name+"</b></td>";
				String description = "<td>"+parameterDescs.get(i)+"</td></tr>";
				
				finalparamstring = finalparamstring + addon + name + description;
			}
			
			finalparamstring = parameterstring + finalparamstring + "</table>";
		}
		
//		//System.out.println(finalparamstring);
		return finalparamstring;
	}
	
	public String returnString() {
		String returnstring = "";
		if(!returns.equals("")) {
			returnstring = "<table class=\"sectionStyle\"><tr valign=\"top\"><td class=\"widthStyle\"><u>Returns</u></td><td>"+returns+"</td></tr></table>";
		}
		
//		System.out.println(returnstring);

		return returnstring;
	}
	
	public void handleLink(String link){
		try {
			openthislink(link);
		} catch(Exception e) {
			 e.printStackTrace();
		}
	}
	
	public void openthislink(String url) throws Exception {
		Desktop.getDesktop().browse(new URI(url));
	}
}
