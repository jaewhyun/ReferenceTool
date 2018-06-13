import template.tool;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.util.*;

public class SubHeader {
	DefaultMutableTreeNode subHeader;
	ArrayList<DefaultMutableTreeNode> leaves = new ArrayList<DefaultMutableTreeNode>();
	
	public SubHeader(String subheaderInput) {
		subHeader = new DefaultMutableTreeNode(subheaderInput);
	}
//	public void set_subHeader(String subheaderInput) {
//		subHeader = new DefaultMutableTreeNode(subheaderInput);
//	}
	
	public void add_leaf(String leafInput) {
		leaves.add(new DefaultMutableTreeNode(leafInput));
	}
	
	public void addLeaftosubHeader() {
		for(int i = 0; i < leaves.size(); i++) {
			subHeader.add(leaves.get(i));
		}
	}

}
