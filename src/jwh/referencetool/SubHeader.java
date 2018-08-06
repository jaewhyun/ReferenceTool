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

	public void addLeaves(ArrayList<Leaf> inputlist) {
		for(Leaf currentleaf : inputlist) {
			leaves.add(currentleaf);
		}
		
		for(Leaf currentLeaf : leaves) {
			subHeader.add(currentLeaf.getLeaf());
		}
	}
	
	public DefaultMutableTreeNode getSubHeader() {
		return subHeader;
	}
}
