package jwh.referencetool;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;
import java.io.*;

public class NodeNameGenerator {
	HashSet<String> header_subheaderNames;
	
	public NodeNameGenerator(HashSet<String> header_subheaderNames) {
		this.header_subheaderNames = header_subheaderNames;
	}
	
	public String generator(DefaultMutableTreeNode node) {
		String nodeName = node.toString();
		
		if(node.isLeaf()) {
			DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node.getParent();
			DefaultMutableTreeNode nodeGParent = (DefaultMutableTreeNode) nodeParent.getParent();
			
			if(nodeParent.toString().equals("Methods") || nodeParent.toString().equals("Fields")) {
				String nodeGParentName = nodeGParent.toString();
				nodeName = nodeGParentName+"_"+nodeName;
			}
			
			String lasttwo = "";
			if(nodeName.length() > 1) {
				lasttwo = nodeName.substring(nodeName.length() - 2);
			}
			
			if(lasttwo.equals("()")) {
				nodeName = nodeName.replaceAll("[()]", "");
				nodeName = nodeName + "_";
			} else if(nodeName.indexOf('_') >= 0) {
				nodeName = nodeName.replaceAll("[^a-zA-Z0-9_]", "");
			} else {
				nodeName = nodeName.replaceAll("[^a-zA-Z0-9_]", "");
				nodeName = nodeName.replaceAll("\\s+", "");
			}
			
		} else {
			if(!node.isRoot() 
					&& !node.toString().equals("Methods") 
					&& !node.toString().equals("Fields")
					&& !header_subheaderNames.contains(node.toString())) {
				nodeName = nodeName.replaceAll("[^a-zA-Z0-9_]", "");
				nodeName = nodeName.replaceAll("\\s+", "");
			}
		}
		
		return nodeName;
	}
}
