package template.tool;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.util.*;

public class Header {
	DefaultMutableTreeNode header;
	int misc = 0;
	Integer numberofSubHeaders = 0;
	ArrayList<DefaultMutableTreeNode> miscLeaves = new ArrayList<DefaultMutableTreeNode>();
	LinkedHashMap<SubHeader, Integer> subHeader_Number = new LinkedHashMap();
	
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
	
	public void set_numberofSubHeaders(Integer numInput) {
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
	
	public Integer get_numberofSubHeaders() {
		return numberofSubHeaders;
	}
	
	public void add_miscLeaves(String leaf) {
		DefaultMutableTreeNode temp = new DefaultMutableTreeNode(leaf);
		miscLeaves.add(temp);
		header.add(temp);
	}
	
	public LinkedHashMap<SubHeader, Integer> get_subHeader_Number() {
		return subHeader_Number;
	}
	
	public void add_subHeaders() {
		for(SubHeader subHeader : subHeader_Number.keySet()) {
			header.add(subHeader.get_subHeader());
		}
	}
	
	public ArrayList<DefaultMutableTreeNode> get_miscLeaves() {
		return miscLeaves;
	}
}
