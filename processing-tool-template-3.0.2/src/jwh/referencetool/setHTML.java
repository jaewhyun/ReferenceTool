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
		regexer.parseExamples();
		exampleImages = regexer.get_exampleImages();
		exampleCodes = regexer.get_exampleCodes();
		descriptionList = regexer.parseDescription();
		regexer.parseParameters();
		parameterNames = regexer.get_parameterNames();
		parameterDescs = regexer.get_parameterDescs();
		syntaxList = regexer.parseSyntax();
		returns = regexer.parseReturns();
	}
	
	public void fillIn(String name, ArrayList<String> syntaxList, ArrayList<String> descriptionList, ArrayList<String> parameterNames, 
		ArrayList<String> parameterDescs, ArrayList<String> exampleImages, ArrayList<String> exampleCodes) {
		
		setText("<table>"
				+ "<tr valign= \"top\">"
				+ "<td class=\"widthStyle\">Name</td>"
				+ "<td><b>" + name + "</b></td>"
				+ "</tr>" 
				+ "</table>"
				+ "<table class=\"sectionStyle\">" 
				+ "<tr class=\"sectionheaderStyle\">Examples</td>"
				);
		
		
		// return to this after figuring out html manipulation and tags for java
		
	}
}
