package jwh.referencetool;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import java.io.*;
import java.net.URL;
import java.util.*;

public class setHTML extends JEditorPane {
	HTMLEditorKit editorkit;
	StyleSheet css = new StyleSheet();
	String name = "";
	String returns = "";
	
	ArrayList<String> syntaxList = new ArrayList<String>();
	ArrayList<String> descriptionList = new ArrayList<String>();
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
		System.out.println(name);
		regexer.parseExamples();
		exampleImages = regexer.get_exampleImages();
		System.out.println(exampleImages);
		exampleCodes = regexer.get_exampleCodes();
		System.out.println(exampleCodes);
		descriptionList = regexer.parseDescription();
		System.out.println(descriptionList);
		regexer.parseParameters();
		parameterNames = regexer.get_parameterNames();
		System.out.println(parameterNames);
		parameterDescs = regexer.get_parameterDescs();
		System.out.println(parameterDescs);
		syntaxList = regexer.parseSyntax();
		System.out.println(syntaxList);
		returns = regexer.parseReturns();
		System.out.println(returns);
		
		fillIn(name, syntaxList, descriptionList, parameterNames, parameterDescs, exampleImages, exampleCodes);
	}
	
	public void fillIn(String name, ArrayList<String> syntaxList, ArrayList<String> descriptionList, ArrayList<String> parameterNames, 
		ArrayList<String> parameterDescs, ArrayList<String> exampleImages, ArrayList<String> exampleCodes) {
		
		String hr = "<tr valign=\"top\"><td class=\"widthStyle\">&nbsp;</td><td><hr></td></tr>";
		String examples = "";
		String finalString = "";
		for(int i = 0; i < exampleCodes.size(); i++) {
			String exampletr = "";

			if(i > 0) {
				exampletr = "<tr valign=\"top\"><td class=\"widthStyle\">&nbsp;</td>";
			}
			
			String imageLocation = "";
			if(exampleImages.size() != 0) {
				imageLocation = exampleImages.get(i);
				imageLocation = new StringBuilder(imageLocation).insert(1, "../").toString();
				imageLocation = "<td><img src="+imageLocation+"></td>";
//				System.out.println(imageLocation);
			}
			
			String code = "";
			code = exampleCodes.get(i).replaceAll("\\n", "<br>");
			
			code = "<td>"+ code + "</td></tr>";
			
			if(i < (exampleCodes.size()-1) && imageLocation.equals("")) {
				code = code + hr;
			}
//			System.out.println(code);
			
			if(!imageLocation.equals("")) {
				finalString = exampletr+imageLocation+code;
			}
			System.out.println(finalString);
		}
		
		String namestring = "<table>"
				+ "<tr valign= \"top\">"
				+ "<td class=\"widthStyle\"><u>Name</u></td>"
				+ "<td><b>" + name + "</b></td>"
				+ "</tr>" 
				+ "</table>"
				+ "<table class=\"sectionStyle\">" 
				+ "<tr class=\"sectionheaderStyle\"><u>Examples</u></td>";
		
		String total = namestring + finalString;
		
		this.setText(total);
		
//		this.setText(finalString);
		

		// return to this after figuring out html manipulation and tags for java
		
	}
}
