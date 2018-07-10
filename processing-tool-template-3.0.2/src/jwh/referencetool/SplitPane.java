package jwh.referencetool;

import java.io.*;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsConfiguration;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
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
//	JEditorPane editorPane = new JEditorPane();
	setHTML htmlPane = new setHTML();
	DefaultMutableTreeNode Root;
	JTree tree;
//	HTMLEditorKit editorkit;
	JScrollPane leftscrollPane;
	JScrollPane rightscrollPane;
	ArrayList<String> referenceList = new ArrayList<String>();
	ArrayList<Leaf> trackLeaves = new ArrayList<Leaf>();

	public SplitPane() {
		
//		File fontfile;
//		try {
//			fontfile = new File(getClass().getResource("/data/Raleway-Regular.tff").toURI());
//		} catch(URISyntaxException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, fontfile));
//		} catch(FontFormatException f | IOExcpetion) {
//			f.printStackTrace();
//		}
//		
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
		
		leftscrollPane = new JScrollPane(tree);
//		rightscrollPane = new JScrollPane(editorPane);
		rightscrollPane = new JScrollPane(htmlPane);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(leftscrollPane);
		splitPane.setRightComponent(rightscrollPane);
		
//		editorPane.setEditable(false);
		htmlPane.setEditable(false);
		
//		editorkit = new HTMLEditorKit();
		
		splitPane.setDividerLocation(200);
		
//		editorkit.setAutoFormSubmission(false);
//		editorPane.setEditorKit(editorkit);
//		htmlPane.setEditorKit(editorkit);
		
//		editorPane.addHyperlinkListener(new HyperlinkListener() {
//			@Override
//			public void hyperlinkUpdate(HyperlinkEvent e) {
//				if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
//					handleLink(e.getURL().toExternalForm());
//				}
//			}
//		});
		
		htmlPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				handleLink(e.getURL().toExternalForm());
					}
			}
		});
		
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
	
	public void printheaders() {
//		System.out.println("list of headers in order are: ");
//		for(Header currentH : listofHeaders) {
//			System.out.println(currentH.get_header().toString());
//			if(currentH.get_misc() != 0) {
//				System.out.println("the misc are: ");
//				for(DefaultMutableTreeNode cmiscleaf : currentH.get_miscLeaves()) {
//					System.out.println(cmiscleaf.toString());
//				}
//			}
//			for(Map.Entry<SubHeader, Integer> entry : currentH.get_subHeader_Number().entrySet()) {
//				SubHeader temp = entry.getKey();
//				System.out.println("subheader is: " + temp.get_subHeader().toString());
//				ArrayList<DefaultMutableTreeNode> templeaves = temp.get_leaves();
//				System.out.println("leaves are: ");
//				System.out.println(templeaves);
//				for(DefaultMutableTreeNode templeaf : templeaves) {
//					System.out.println(templeaf.toString());
//				}
//			}
//		}
//		
		Enumeration e = Root.preorderEnumeration();
		while(e.hasMoreElements()) {
			System.out.println(e.nextElement());
		}
	}
	
	public void assignReferences() {
		java.net.URL htmlURL = getClass().getResource("/data/reference_list.txt");
		System.out.println(htmlURL);
		
		int counter = 0;
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(htmlURL.openStream()))) {
			String line;
			while((line = br.readLine()) != null) {
				referenceList.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("finishedreading2");
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
		System.out.println(htmlURL);
		ArrayList<String> listofmiscs = new ArrayList<String>();
		
		int counter = 0;
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(htmlURL.openStream()))) {
			String line;
			String[] output;
			
			int count = 0;
			System.out.println("reading in miscs");
			while((line = br.readLine()) != null) {
				if(line.indexOf('_') >= 0) {
					output = line.split("_");
					for(Leaf currentleaf : trackLeaves) {
						String currentleafName = currentleaf.get_leaf().toString();
						System.out.println(currentleaf);
						if(currentleafName.equals(output[0])) {
							if(output[1].indexOf(',') == -1) {
								Integer numberofMethods = Integer.parseInt(output[1]);
								
								ArrayList<DefaultMutableTreeNode> methods = new ArrayList<DefaultMutableTreeNode>();
								for(int i = 0; i < numberofMethods; i++) {
									line = br.readLine();
									DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(line);
									System.out.println(newnode.toString());
									methods.add(newnode);
								}
								
								currentleaf.add_leafMethods(methods);
							} else {
								System.out.println("found method and number");
								String[] parsed = output[1].split(",");
								Integer numberofFields = Integer.parseInt(parsed[0]);
								System.out.println(numberofFields);
								Integer numberofMethods = Integer.parseInt(parsed[1]);
								System.out.println(numberofMethods);
								
								ArrayList<DefaultMutableTreeNode> methods = new ArrayList<DefaultMutableTreeNode>();
								ArrayList<DefaultMutableTreeNode> fields = new ArrayList<DefaultMutableTreeNode>();
								
								for(int i = 0; i < numberofFields; i++) {
									line = br.readLine();
									DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(line);
									System.out.println(newnode.toString());
									fields.add(newnode);
								}
								
								for(int i = 0; i < numberofMethods; i++) {
									line = br.readLine();
									DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(line);
									System.out.println(newnode.toString());
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
//		try {
//			
////			htmlPane.setPage(urllink);
////			editorPane.setPage(urllink);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
	}
	
	public void handleLink(String link){
		try {
			openthislink(link);
		} catch(Exception e) {
			 e.printStackTrace();
		}
	}
	
	public void openthislink(String url) throws Exception {
		Desktop.getDesktop().browse(new URI(url));
	}
	
	public void handleClose() {
		dispose();
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
				System.out.println(nodeParent.toString());
				DefaultMutableTreeNode nodeGParent = (DefaultMutableTreeNode) nodeParent.getParent();
				System.out.println(nodeGParent.toString());
				if(nodeParent.toString().equals("Methods") || nodeParent.toString().equals("Fields")) {
					String nodeGParentName = nodeGParent.toString();
					nodeName = nodeGParentName+"_"+nodeName;
				} 
				
				System.out.println(nodeName);
				
				String lasttwo = nodeName.substring(nodeName.length()-2);
				
				if(lasttwo.equals("()")) {
					nodeName = nodeName.replaceAll("[()]", "");
					nodeName = nodeName + "_";
					htmlfileName = "/data/reference/"+nodeName+".html";
					System.out.println(htmlfileName);
				} else if(nodeName.indexOf('_') >= 0) {
					htmlfileName = "/data/reference/"+nodeName+".html";
					System.out.println(htmlfileName);
				} else {
					nodeName = nodeName.replaceAll("[^a-zA-Z0-9_]", "");
					nodeName = nodeName.replaceAll("\\s+", "");
					htmlfileName = "/data/reference/"+nodeName+".html";
					System.out.println(htmlfileName);
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
}



