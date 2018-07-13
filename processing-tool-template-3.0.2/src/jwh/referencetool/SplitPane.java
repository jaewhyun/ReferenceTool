package jwh.referencetool;

import java.io.*;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreeNode;

import processing.app.exec.SystemOutSiphon;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class SplitPane extends JFrame {
	List<Header> listofHeaders = new ArrayList<Header>();
	DefaultTreeModel treeModel;

	setHTML htmlPane = new setHTML();
	DefaultMutableTreeNode Root;
	JTree tree;
	Boolean filtered = false;
	JTextField searchBar;
	JScrollPane leftscrollPane;
	JScrollPane rightscrollPane;
	ArrayList<String> referenceList = new ArrayList<String>();
	ArrayList<Leaf> trackLeaves = new ArrayList<Leaf>();

	public SplitPane() {
		Root = new DefaultMutableTreeNode("References");
		setTree();
		treeModel = new DefaultTreeModel(Root);
		tree = new JTree(treeModel);
	
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setToggleClickCount(1);
		tree.getSelectionModel().addTreeSelectionListener(new Selector());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
		
		JPanel leftpanel = new JPanel();
		leftpanel.setLayout(new BorderLayout());
		searchBar = new JTextField("Search");
		searchBar.addFocusListener(new SelectFocus());
		
		searchBar.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent ke) {
				super.keyTyped(ke);
				filterTree(searchBar.getText()+ke.getKeyChar());
			}
		});
		
		leftscrollPane = new JScrollPane(tree);
		rightscrollPane = new JScrollPane(htmlPane);
		
		leftpanel.add(searchBar, BorderLayout.NORTH);
		leftpanel.add(leftscrollPane, BorderLayout.CENTER);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
//		splitPane.setLeftComponent(leftscrollPane);
		splitPane.setLeftComponent(leftpanel);
		splitPane.setRightComponent(rightscrollPane);
		
		htmlPane.setEditable(false);
		
		splitPane.setDividerLocation(200);
		
		splitPane.setPreferredSize(new Dimension(700,400));
		this.getContentPane().add(splitPane);
	}
	
	public void readinLists() {
		java.net.URL htmlURL = getClass().getResource("/data/reference_summary.txt");
				
		try (BufferedReader br = new BufferedReader(new InputStreamReader(htmlURL.openStream()))){
			String line;
			while((line = br.readLine()) != null) {
				Header newheader = new Header(line);
				Root.add(newheader.get_header());
				line = br.readLine();
				newheader.set_misc(Integer.parseInt(line));
				line = br.readLine();
				Integer num_subHeader = Integer.parseInt(line);
				
				if(num_subHeader != 0) {
					newheader.set_numberofSubHeaders(num_subHeader);
					LinkedHashMap<SubHeader, Integer> tempHashMap = new LinkedHashMap<SubHeader, Integer>();
					for(int i = 0; i < num_subHeader; i++) {
						line = br.readLine();
						SubHeader tempSubHeader = new SubHeader(line);
						line = br.readLine();
						Integer number = Integer.parseInt(line);
						tempHashMap.put(tempSubHeader, number);
					}
					newheader.set_subHeader_Number(tempHashMap);
				}
				
				newheader.add_subHeaders();
				listofHeaders.add(newheader);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	public void printheaders() {
//
//		Enumeration e = Root.preorderEnumeration();
//		while(e.hasMoreElements()) {
//			System.out.println(e.nextElement());
//		}
//	}
//	
	public void assignReferences() {
		java.net.URL htmlURL = getClass().getResource("/data/reference_list.txt");
//		System.out.println(htmlURL);
		
		int counter = 0;
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(htmlURL.openStream()))) {
			String line;
			while((line = br.readLine()) != null) {
				referenceList.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < listofHeaders.size(); i++) {
			// per header
			Header tempHeader = listofHeaders.get(i);
			LinkedHashMap<SubHeader, Integer> tempSubHeaders = tempHeader.get_subHeader_Number();
			// check misc
			int tempHeaderMiscNum = tempHeader.get_misc();
			// if misc not 0
			if(tempHeader.get_misc() != 0) {
				for(int j = counter; j < tempHeaderMiscNum + counter; j++) {
					// add misc references
					Leaf newleaf = new Leaf(referenceList.get(j));
					if(referenceList.get(j).indexOf('`') >= 0) {
						trackLeaves.add(newleaf);
					}
					
					tempHeader.add_miscLeaves(newleaf);
				}
			}
			
			counter += tempHeaderMiscNum;
			
			// check for subheaders and if number of subheaders is not 0
			if(tempHeader.get_numberofSubHeaders()!=0) {
				// per subhead
				for(Map.Entry<SubHeader, Integer> entry : tempSubHeaders.entrySet()) {
					SubHeader tempSubHead = entry.getKey();
					Integer tempSubHeadNum = entry.getValue();
					ArrayList<Leaf> inputList = new ArrayList<Leaf>();
					for(int j = counter; j < tempSubHeadNum + counter; j++) {
						// add reference
						String tempString = referenceList.get(j);
						Leaf newleaf = new Leaf(tempString);
						if(tempString.indexOf('`') >= 0) {
							trackLeaves.add(newleaf);
						}
						inputList.add(newleaf);
					}
					
					tempSubHead.add_leaf(inputList);
					counter += tempSubHeadNum;
				}
			}
		}
	}
	
	public void assignleafMiscs() {
		java.net.URL htmlURL = getClass().getResource("/data/leafmiscs.txt");
//		System.out.println(htmlURL);
		ArrayList<String> listofmiscs = new ArrayList<String>();
		
		int counter = 0;
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(htmlURL.openStream()))) {
			String line;
			String[] output;
			
			int count = 0;
//			System.out.println("reading in miscs");
			while((line = br.readLine()) != null) {
				if(line.indexOf('_') >= 0) {
					output = line.split("_");
					for(Leaf currentleaf : trackLeaves) {
						String currentleafName = currentleaf.get_leaf().toString();
//						System.out.println(currentleaf);
						if(currentleafName.equals(output[0])) {
							if(output[1].indexOf(',') == -1) {
								Integer numberofMethods = Integer.parseInt(output[1]);
								
								ArrayList<DefaultMutableTreeNode> methods = new ArrayList<DefaultMutableTreeNode>();
								for(int i = 0; i < numberofMethods; i++) {
									line = br.readLine();
									DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(line);
//									System.out.println(newnode.toString());
									methods.add(newnode);
								}
								
								currentleaf.add_leafMethods(methods);
							} else {
								String[] parsed = output[1].split(",");
								Integer numberofFields = Integer.parseInt(parsed[0]);
//								System.out.println(numberofFields);
								Integer numberofMethods = Integer.parseInt(parsed[1]);
//								System.out.println(numberofMethods);
								
								ArrayList<DefaultMutableTreeNode> methods = new ArrayList<DefaultMutableTreeNode>();
								ArrayList<DefaultMutableTreeNode> fields = new ArrayList<DefaultMutableTreeNode>();
								
								for(int i = 0; i < numberofFields; i++) {
									line = br.readLine();
									DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(line);
//									System.out.println(newnode.toString());
									fields.add(newnode);
								}
								
								for(int i = 0; i < numberofMethods; i++) {
									line = br.readLine();
									DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(line);
//									System.out.println(newnode.toString());
									methods.add(newnode);
								}
								
								currentleaf.add_leafMethods(methods);
								currentleaf.add_leafFields(fields);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setTree() {
		readinLists();
		assignReferences();
		assignleafMiscs();
		for(Header head : listofHeaders) {
			head.connect_nodes();
		}
//		printheaders();
	}
	

	public void setFile(URL urllink) {
		htmlPane.parseHTML(urllink);
	}
	
	public void handleClose() {
		dispose();
	}
	
	/**
	 * Tree widget which allows the tree to be filtered on keystroke time. Only nodes who's
	 * toString matches the search field will remain in the tree or its parents.
	 *
	 * Copyright (c) Oliver.Watkins
	 */
	/**
	 *
	 * @param text
	 */
	
	public void filterTree(String text) {
		String filteredText = text;
		DefaultMutableTreeNode filteredRoot = copyNode(Root);
		
		if(text.trim().toString().equals("")) {
			treeModel.setRoot(Root);
			tree.setModel(treeModel);
			tree.updateUI();
			leftscrollPane.getViewport().setView(tree);
			
			for(int i = 0; i< tree.getRowCount();i++) {
				tree.expandRow(i);
			}
			
			return;
		} else {
			TreeNodeBuilder b = new TreeNodeBuilder(text);
			filteredRoot = b.prune((DefaultMutableTreeNode) filteredRoot.getRoot());
			
			treeModel.setRoot(filteredRoot);
			
			tree.setModel(treeModel);
			tree.updateUI();
			leftscrollPane.getViewport().setView(tree);	

		}
		
		for(int i = 0; i<tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
	
		filtered = true;
	}
	
	private DefaultMutableTreeNode copyNode(DefaultMutableTreeNode orig) {
		DefaultMutableTreeNode newOne = new DefaultMutableTreeNode();
		newOne.setUserObject(orig.getUserObject());
		Enumeration enm = orig.children();
		
		while(enm.hasMoreElements()) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) enm.nextElement();
			newOne.add(copyNode(child));
		}
		
		return newOne;
	}
	
	
	private class Selector implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			
			if (node == null) 
				return;
			
			String nodeName = node.toString();
			
			String htmlfileName = null;
//			Boolean misc = false;
			
			if(node.isLeaf()) {
				DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node.getParent();
//				System.out.println(nodeParent.toString());
				DefaultMutableTreeNode nodeGParent = (DefaultMutableTreeNode) nodeParent.getParent();
//				System.out.println(nodeGParent.toString());
				if(nodeParent.toString().equals("Methods") || nodeParent.toString().equals("Fields")) {
					String nodeGParentName = nodeGParent.toString();
					nodeName = nodeGParentName+"_"+nodeName;
				} 
				
//				System.out.println(nodeName);
				
				String lasttwo = nodeName.substring(nodeName.length()-2);
				
				if(lasttwo.equals("()")) {
					nodeName = nodeName.replaceAll("[()]", "");
					nodeName = nodeName + "_";
					htmlfileName = "/data/reference/"+nodeName+".html";
//					System.out.println(htmlfileName);
				} else if(nodeName.indexOf('_') >= 0) {
					htmlfileName = "/data/reference/"+nodeName+".html";
//					System.out.println(htmlfileName);
				} else {
					nodeName = nodeName.replaceAll("[^a-zA-Z0-9_]", "");
					nodeName = nodeName.replaceAll("\\s+", "");
					htmlfileName = "/data/reference/"+nodeName+".html";
//					System.out.println(htmlfileName);
				}
				
				java.net.URL htmlURL = getClass().getResource(htmlfileName);
				setFile(htmlURL);
			} else {
				DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node.getParent();
				DefaultMutableTreeNode nodeGParent = (DefaultMutableTreeNode) nodeParent.getParent();
				if(nodeParent != Root 
						&& !node.toString().equals("Methods") 
						&& !node.toString().equals("Fields")
						&& nodeGParent != Root) {
					nodeName = nodeName.replaceAll("[^a-zA-Z0-9_]", "");
					nodeName = nodeName.replaceAll("\\s+", "");
					htmlfileName = "/data/reference/"+nodeName+".html";
					java.net.URL htmlURL = getClass().getResource(htmlfileName);
					setFile(htmlURL);
				}
			}
		}
	}
	
	private class SelectFocus extends FocusAdapter {
		public void focusGained(FocusEvent evt) {
			searchBar.setText("");
		}
		
		public void focusLost(FocusEvent evt) {
			searchBar.setText("Search");
			collapseAll(tree);
		}
	}
	
	public void collapseAll(JTree tree) {
		int row = tree.getRowCount() - 1;
		while(row >= 0) {
			tree.collapseRow(row);
			row--;
		}
	}
}



