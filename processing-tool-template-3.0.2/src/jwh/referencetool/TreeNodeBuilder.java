package jwh.referencetool;

import javax.swing.tree.DefaultMutableTreeNode;

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
	
	public TreeNodeBuilder(String textToMatch) {
		this.textToMatch = textToMatch;
	}
	
	public DefaultMutableTreeNode prune(DefaultMutableTreeNode root) {
		boolean badLeaves = true;
		
		while(badLeaves) {
			badLeaves = removeBadLeaves(root);
		}
		
		return root;
	}
	
	private boolean removeBadLeaves(DefaultMutableTreeNode root) {
		boolean badLeaves = false;
		
		DefaultMutableTreeNode leaf = root.getFirstLeaf();
		
		if(leaf.isRoot())
			return false;
		
		int leafCount = root.getLeafCount();
		for(int i = 0; i < leafCount; i++) {
			DefaultMutableTreeNode nextLeaf = leaf.getNextLeaf();
			
			if(!leaf.getUserObject().toString().startsWith(textToMatch) && !leaf.getUserObject().toString().startsWith(textToMatch.toLowerCase())) {
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) leaf.getParent();
				
				if(parent != null)
					parent.remove(leaf);
				
				badLeaves = true;
			}
			
			leaf = nextLeaf;
		}
		
		return badLeaves;
	}
}
