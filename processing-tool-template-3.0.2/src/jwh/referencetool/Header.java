package jwh.referencetool;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.util.*;

public class Header {
	DefaultMutableTreeNode header;
	int misc = 0;
	Integer numberofSubHeaders = 0;
//	ArrayList<DefaultMutableTreeNode> miscLeaves = new ArrayList<DefaultMutableTreeNode>();
	ArrayList<Leaf> leaves = new ArrayList<Leaf>();
	LinkedHashMap<SubHeader, Integer> subHeader_Number = new LinkedHashMap<SubHeader, Integer>();
	
	public Header(String headerName) {
		header = new DefaultMutableTreeNode(headerName);
	}
	
	public void set_misc(int numInput) {
		misc = numInput;
	}
	
	public void set_numberofSubHeaders(Integer numInput) {
		numberofSubHeaders = numInput;
	}
//	
	public void set_subHeader_Number(LinkedHashMap<SubHeader, Integer> hashmapInput) {
		subHeader_Number =  hashmapInput;
	}
	
	public int get_misc() {
		return misc;
	}
	
	public DefaultMutableTreeNode get_header() {
		return header;
	}
	
	public Integer get_numberofSubHeaders() {
		return numberofSubHeaders;
	}
	
	public void add_miscLeaves(Leaf newleaf) {
//		DefaultMutableTreeNode temp = new DefaultMutableTreeNode(leaf);
		leaves.add(newleaf);
	}
	
	public void connect_nodes() {
		System.out.println("connecting nodes");
		for(Leaf currentleaf : leaves) {
			header.add(currentleaf.get_leaf());
			
			DefaultMutableTreeNode treenode = currentleaf.get_leaf();
			Enumeration e = treenode.preorderEnumeration();
			while(e.hasMoreElements()) {
				System.out.println(e.nextElement());
			}
		}
	}
	
	public LinkedHashMap<SubHeader, Integer> get_subHeader_Number() {
		return subHeader_Number;
	}
	
	public void add_subHeaders() {
		for(SubHeader subHeader : subHeader_Number.keySet()) {
			header.add(subHeader.get_subHeader());
		}
	}
	
	public ArrayList<Leaf> get_miscLeaves() {
		return leaves;
	}
}
