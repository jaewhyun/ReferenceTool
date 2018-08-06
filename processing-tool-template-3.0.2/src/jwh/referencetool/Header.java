package jwh.referencetool;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.util.*;

public class Header {
	DefaultMutableTreeNode header;
	int misc = 0;
	Integer numberofSubHeaders = 0;
	ArrayList<Leaf> leaves = new ArrayList<Leaf>();
	LinkedHashMap<SubHeader, Integer> subHeader_Number = new LinkedHashMap<SubHeader, Integer>();
	
	public Header(String headerName) {
		header = new DefaultMutableTreeNode(headerName);
	}
	
	public void setMisc(int numInput) {
		misc = numInput;
	}
	
	public void setNumberofSubHeaders(Integer numInput) {
		numberofSubHeaders = numInput;
	}

	public void setSubHeaderNumber(LinkedHashMap<SubHeader, Integer> hashmapInput) {
		subHeader_Number =  hashmapInput;
	}
	
	public int getMisc() {
		return misc;
	}
	
	public DefaultMutableTreeNode getHeader() {
		return header;
	}
	
	public Integer getNumberofSubHeaders() {
		return numberofSubHeaders;
	}
	
	public void addMiscLeaves(Leaf newleaf) {
		leaves.add(newleaf);
	}
	
	public void connectNodes() {
		for(Leaf currentleaf : leaves) {
			header.add(currentleaf.getLeaf());
		}
	}
	
	public LinkedHashMap<SubHeader, Integer> getSubHeaderNumber() {
		return subHeader_Number;
	}
	
	public void addSubHeaders() {
		for(SubHeader subHeader : subHeader_Number.keySet()) {
			header.add(subHeader.getSubHeader());
		}
	}
}
