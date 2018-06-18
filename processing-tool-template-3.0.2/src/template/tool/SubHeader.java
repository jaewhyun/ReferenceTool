package template.tool;
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
	
//	public void add_leaf(String leafInput) {
//		DefaultMutableTreeNode inputleaf = new DefaultMutableTreeNode(leafInput);
//		leaves.add(inputleaf);
//		subHeader.add(inputleaf);
//	}
//	
	public void add_leaf(ArrayList<DefaultMutableTreeNode> inputlist) {
//		System.out.println("adding these leaves");
//		System.out.println(inputlist);
		for(DefaultMutableTreeNode leafinput : inputlist) {
			leaves.add(leafinput);
		}
		for(DefaultMutableTreeNode leafinput : leaves) {
			subHeader.add(leafinput);
		}
		System.out.println(leaves);
	}
	public DefaultMutableTreeNode get_subHeader() {
		return subHeader;
	}
	
	public ArrayList<DefaultMutableTreeNode> get_leaves() {
		return leaves;
	}

}
