import template.tool;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.util.*;

public class Header {
	DefaultMutableTreeNode header;
	int misc = 0;
	int numberofSubHeaders = 0;
	ArrayList<DefaultMutableTreeNode> miscLeaves = new ArrayList<DefaultMutableTreeNode>();
	LinkedHashMap<SubHeader, int> subHeader_Number = new LinkedHashMap();
	
	public Header(String headerName) {
		header = new DefaultMutableTreeNode(headerName);
	}
	
//	public void set_headerName(String nameInput) {
//		headerName = nameInput;
//	}
//	
	public void set_misc(int numInput) {
		misc = numInput;
	}
	
	public void set_numberofSubHeaders(int numInput) {
		numberofSubHeaders = numInput;
	}
//	
	public void set_subHeader_Number(LinkedHashMap hashmapInput) {
		subHeader_Number =  hashmapInput;
	}
	
	public int get_misc() {
		return misc;
	}
	
	public DefaultMutableTreeNode get_header() {
		return header;
	}
	
	public int get_numberofSubHeaders() {
		return numberofSubHeaders;
	}
	public void add_miscLeaves(String leaf) {
		DefaultMutableTreeNode temp = new DefaultMutablTreeNode(leaf);
		miscLeaves.add(temp);
		header.add(temp);
	}
	
	public LinkedHashMap<SubHeader, int> get_subHeader_Number() {
		return subHeader_Number;
	}
	
	public void add_subHeaders {
		for(DefaultMutableTreeNode subHeader : subHeader_Number.keySet()) {
			header.add(subHeader);
		}
	}
}
