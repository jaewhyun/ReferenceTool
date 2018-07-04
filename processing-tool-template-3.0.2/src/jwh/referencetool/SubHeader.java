package jwh.referencetool;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.util.*;

public class SubHeader {
	DefaultMutableTreeNode subHeader;
	ArrayList<Leaf> leaves = new ArrayList<Leaf>();
	
	public SubHeader(String subheaderInput) {
		subHeader = new DefaultMutableTreeNode(subheaderInput);
	}
	
	public void set_leaves(ArrayList<Leaf> leavesinput) {
		leaves = leavesinput;
	}
	
	public void add_leaf(ArrayList<Leaf> inputlist) {
		for(Leaf currentleaf : inputlist) {
			leaves.add(currentleaf);
		}
		
		for(Leaf currentLeaf : leaves) {
			subHeader.add(currentLeaf.get_leaf());
		}
	}
	
	public DefaultMutableTreeNode get_subHeader() {
		return subHeader;
	}
	
	public ArrayList<Leaf> get_leaves() {
		return leaves;
	}
}
