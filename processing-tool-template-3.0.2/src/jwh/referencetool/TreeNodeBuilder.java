package jwh.referencetool;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;
import java.io.*;

/**
 * Tree widget which allows the tree to be filtered on keystroke time. Only nodes who's
 * toString matches the search field will remain in the tree or its parents.
 *
 * Copyright (c) Oliver.Watkins
 */

/**
 * Class that prunes off all leaves which do not match the search string.
 *
 * @author Oliver.Watkins
 */

public class TreeNodeBuilder {
	private String textToMatch;
//	HashSet<String> clickableNodes;
	HashSet<String> header_subheaderNames_toUpper;
	
	public TreeNodeBuilder(String textToMatch, HashSet<String> header_subheaderNames_toUpper) {
		this.textToMatch = textToMatch;
//		this.clickableNodes = clickableNodes;
		this.header_subheaderNames_toUpper = header_subheaderNames_toUpper;
	}
	
	public DefaultMutableTreeNode prune(DefaultMutableTreeNode root) {
		boolean badLeaves = true;
		
		while(badLeaves) {
			badLeaves = removeBadLeaves(root);
		}
		
		return root;
	}
	
	private boolean removeBadLeaves(DefaultMutableTreeNode root) {
//		System.out.println("removing bad leaves");
		boolean badLeaves = false;
		
		DefaultMutableTreeNode leaf = root.getFirstLeaf();
//		System.out.println("current leaf is:" + leaf.toString());
		
		if(leaf.isRoot())
			return false;
		
		int leafCount = root.getLeafCount();
		for(int i = 0; i < leafCount; i++) {
			DefaultMutableTreeNode nextLeaf = leaf.getNextLeaf();

			String parentstring = leaf.getParent().toString().toLowerCase();
			String gparentstring = "";
			String rootstring = root.toString().toLowerCase();
			if(leaf.getParent().getParent() != null) {
				gparentstring = leaf.getParent().getParent().toString().toLowerCase();
			}
			

			if(!leaf.getUserObject().toString().startsWith(textToMatch) 
					&& !leaf.getUserObject().toString().startsWith(textToMatch.toLowerCase())
					&& !leaf.getUserObject().toString().toLowerCase().contains(textToMatch.toLowerCase()) 
					&& !parentstring.contains(textToMatch.toLowerCase())
					&& !gparentstring.contains(textToMatch.toLowerCase())){
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) leaf.getParent();

				
				if(parent != null) {
					parent.remove(leaf);
				}
				
				badLeaves = true;
			}
			
			leaf = nextLeaf;
//			System.out.println("moving on to the next leaf");
		}
		
//		System.out.println("done removing bad leaves");
		
		
		return badLeaves;
	}
}