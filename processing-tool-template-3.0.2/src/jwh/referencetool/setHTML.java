package jwh.referencetool;

import javax.swing.JEditorPane;
import javax.swing.JButton;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import processing.app.Platform;
import processing.app.ui.Editor;

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
	String constructor = "";
	HTMLDocument doc;

	ArrayList<String> parameterNames = new ArrayList<String>();
	ArrayList<String> methodNames = new ArrayList<String>();
	ArrayList<String> methodDescs = new ArrayList<String>();
	ArrayList<String> fieldNames = new ArrayList<String>();
	ArrayList<String> fieldDescs = new ArrayList<String>();
	ArrayList<String> parameterDescs = new ArrayList<String>();
	ArrayList<String> exampleImages = new ArrayList<String>();
	ArrayList<String> exampleCodes = new ArrayList<String>();
	HashMap<String, ArrayList<String>> mapofCodes = new HashMap<String, ArrayList<String>>();
	ArrayList<String> related = new ArrayList<String>();
	HashMap<String, String> savedHTML = new HashMap<String, String>();
	
	public setHTML() {
		// not sure how this is used just yet
		this.setContentType("text/html");
//		editorkit = new HTMLEditorKit();
		editorkit = new PreWrapHTMLEditorKit();
		css = editorkit.getStyleSheet();
		setCSS();
		editorkit.setAutoFormSubmission(false);
		this.setEditorKit(editorkit);
		doc = (HTMLDocument) this.getDocument();
//		try {
//			doc.setBase(new URL("http://www.google.com"));
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
		
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
//		java.net.URL font = getClass().getResource("/data/Raleway-Regular.ttf");
//		String fontString = font.toString();
//		System.out.println(fontString);
//		css.addRule("body {font-family:" + fontString + "; font-size:9px}");
		css.addRule("body {font-family: raleway; font-size:9px}");
		css.addRule(".sectionStyle {padding-top: 20px}");
		css.addRule(".widthStyle {width : 70px}");
		css.addRule(".sectionheaderStyle {width : 70px; valign: top}");
	}
	
	public void parseHTML(URL urlLink, String original, boolean initiated) {
		if(savedHTML.containsKey(original) && initiated) {
			this.setText(savedHTML.get(original));
		} else {
			if(!original.equals("Methods") && !original.equals("Fields")) {
				RegEx regexer = new RegEx(urlLink);
				name = regexer.parseName();

				regexer.parseExamples();
				exampleImages = regexer.get_exampleImages();
				exampleCodes = regexer.get_exampleCodes();
				if(!mapofCodes.containsKey(original)) {
					mapofCodes.put(original, exampleCodes);
				}

				description = regexer.parseDescription();
				regexer.parseParameters();
				parameterNames = regexer.get_parameterNames();
				parameterDescs = regexer.get_parameterDescs();

				regexer.parseMethods();
				methodNames = regexer.get_methodNames();
				methodDescs = regexer.get_methodDescs();
				
				regexer.parseFields();
				fieldNames = regexer.get_fieldNames();
				fieldDescs = regexer.get_fieldDescs();
				
				syntax = regexer.parseSyntax();
				returns = regexer.parseReturns();
			
				constructor = regexer.parseConstructor();
				related = regexer.parseRelated();

				fillIn(urlLink, original);
			}
		}
	}
	
	public HashMap<String, ArrayList<String>> get_mapofCodes() {
		return mapofCodes;
	}
	
	public void fillIn(URL urllink, String original) {
		String finalexampleString = exampleString(urllink);
		String descriptionString = descriptionString();
		String syntaxString = syntaxString();
		String parameterString = parameterString();
		String returnString = returnString();
		String relatedString = relatedString();
		String methodString = methodString();
		String fieldString = fieldString();
		String constructorString = constructorString();
		
		String namestring = "<table>"
				+ "<tr valign= \"top\">"
				+ "<td class=\"widthStyle\"><u>Name</u></td>"
				+ "<td><b>" + name + "</b></td>"
				+ "</tr>" 
				+ "</table>";
		
		String total = namestring + finalexampleString + descriptionString + fieldString + methodString + syntaxString + constructorString + parameterString + returnString + relatedString;
//		System.out.println(total);
		total = total.replace("<<", "&lt;&lt;");
		total = total.replaceAll(" *< ", " &lt ");
		total = total.replaceAll(" *<= ", " &lt;= ");
//		this.setText(total);
		if(!savedHTML.containsKey(original)) {
			savedHTML.put(original, total);
		}
	}
	
	public String exampleString(URL urllink) {
		String hr = "<tr valign=\"top\"><td class=\"widthStyle\">&nbsp;</td><td><hr></td></tr>";
		String examples = "";
		String finalexampleString = "<table class=\"sectionStyle\">" 
				+ "<tr valign = \"top\"><td class=\"sectionheaderStyle\"><u>Examples</u></td>";
		ArrayList<String> nomoreImagesCode = new ArrayList<String>();
		boolean nomoreImages = false;
		
		if(exampleCodes.size() != 0) {
			for(int i = 0; i < exampleCodes.size(); i++) {
				String exampletr = "";

				if(i > 0) {
					exampletr = "<tr valign=\"top\"><td class=\"widthStyle\">&nbsp;</td>";
				}
				
				if(i >= exampleImages.size() && exampleImages.size() != 0)
					nomoreImages = true;
				
				String imageLocation = "";
				if(exampleImages.size() != 0 && nomoreImages == false) {
					imageLocation = exampleImages.get(i).trim();
					String testString = "modes/java/reference/"+imageLocation;
					File imagefile = Platform.getContentFile(testString);
					testString = imagefile.toURI().toString();
//					System.out.println(testString);
					imageLocation = "<td><img src=\""+testString+"\"></td><td width = 10px>&nbsp;</td>";
				}
				
				String code = "";
				String returnCode = "";
				returnCode = exampleCodes.get(i).trim();
				
				String codeLines[] = returnCode.split("\\r?\\n");
				for(int j = 0; j < codeLines.length; j++) {
					if(codeLines[j].contains("//") 
							&& !codeLines[j].contains("https://") 
							&& !codeLines[j].contains("http://")) {
						codeLines[j] = codeLines[j].replace("//", "<span style=\"color: #3d9a3e\">//");
						if(codeLines[j].contains(";")) {
							codeLines[j] = codeLines[j].replaceAll(";", ";</span>");
						}
						codeLines[j] = codeLines[j] + "</span>";
					} 
					
					if(codeLines[j].contains("/*") || codeLines[j].contains("/**")) {
						codeLines[j] = codeLines[j].replace("/*", "<span style=\"color: #3d9a3e\">/*");
						if(codeLines[j].contains("\\*+\\/")) {
							codeLines[j] = codeLines[j].replace("\\*+\\/", "*/</span>");
						}	
						codeLines[j] = codeLines[j].replaceAll("(\\*+\\/)", "*/</span>");
					}
					
					code = code + codeLines[j] + "<br>";
				}
				
//				System.out.println(code);

//				code = code.replaceAll("//", "<span style=\"color: #3d9a3e\">//");
				code = "<td valign = \"top\"><pre >"+ code + "</pre></td></tr>";
				if(nomoreImages == true) {
					nomoreImagesCode.add(exampletr + code);
					code = "";
				}
			
				if(i < (exampleCodes.size()-1) && imageLocation.equals("")) {
					code = code + hr;
				}
//				//System.out.println(code);
				
				if(!imageLocation.equals("")) {
					finalexampleString = finalexampleString + exampletr + imageLocation+code;
				} else {
					finalexampleString = finalexampleString + exampletr + code;
				}
			}
			
//			finalexampleString = finalexampleString + "</table>";
			
			if(nomoreImages == false) {
				finalexampleString = finalexampleString + "</table>";
			} else {
				String allcode = "";
				for(int i = 0; i < nomoreImagesCode.size(); i++) {
					allcode = allcode + nomoreImagesCode.get(i);
				}
				
				allcode = "<table padding-top=\"0\" margin=\"0\">" + allcode + "</table>";
				
				finalexampleString = finalexampleString + "</table>" + allcode;
			}
			
		} else {
			finalexampleString = "";
		}

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
	
	public String constructorString() {
		String constructorstring = "<table class=\"sectionStyle\"><tr valign=\"top\"><td class=\"widthStyle\"><u>Constructor</u></td>";
		
		if(!constructor.equals("")) {
			constructorstring = constructorstring + constructor + "</td></tr></table>";
		} else {
			constructorstring = "";
		}
		
		return constructorstring;
		
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
	
	public String relatedString() {
		String relatedstring = "<table class=\"sectionStyle\"><tr valign=\"top\"><td class=\"widthStyle\"><u>Related</u></td>";
		String finalrelatedstring = "";
		
		if(related.size() != 0) {
			for(int i = 0; i < related.size(); i++) {
				String addon = "";
				if(i > 0) {
					addon = "<td class=\"widthStyle\">&nbsp;</td>";
				}
				
				String relatedName = related.get(i);
				relatedName = "<td class = \"widthStyle\">" + relatedName + "</td></tr>";
				
				finalrelatedstring = finalrelatedstring + addon + relatedName;
			}
			
			finalrelatedstring = relatedstring + finalrelatedstring + "</table>";
		}
		
		return finalrelatedstring;
	}
	
	public String methodString() {
		String methodstring = "<table class=\"sectionStyle\"><tr valign=\"top\"><td class=\"widthStyle\"><u>Methods</u></td>";
		String finalmethodstring = "";
		if(methodNames.size() != 0) {
			for(int i = 0; i < methodNames.size(); i++) {
				String addon = "";
				if(i > 0) {
					addon = "<td class=\"widthStyle\">&nbsp;</td>";
				}
				
				String name = methodNames.get(i);
				name = "<td class = \"widthStyle\"><b>"+name+"</b></td>";
				String description = "<td>"+methodDescs.get(i)+"</td></tr>";
				
				finalmethodstring = finalmethodstring + addon + name + description;
			}
			
			finalmethodstring = methodstring + finalmethodstring + "</table>";
		}
		
//		//System.out.println(finalparamstring);
		return finalmethodstring;
	}
	
	public String fieldString() {
		String fieldstring = "<table class=\"sectionStyle\"><tr valign=\"top\"><td class=\"widthStyle\"><u>Fields</u></td>";
		String finalfieldstring = "";
		if(fieldNames.size() != 0) {
			for(int i = 0; i < fieldNames.size(); i++) {
				String addon = "";
				if(i > 0) {
					addon = "<td class=\"widthStyle\">&nbsp;</td>";
				}
				
				String name = fieldNames.get(i);
				name = "<td class = \"widthStyle\"><b>"+name+"</b></td>";
				String description = "<td>"+fieldDescs.get(i)+"</td></tr>";
				
				finalfieldstring = finalfieldstring + addon + name + description;
			}
			
			finalfieldstring = fieldstring + finalfieldstring + "</table>";
		}
		
//		//System.out.println(finalparamstring);
		return finalfieldstring;
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