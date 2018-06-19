package template.tool;

import java.io.*;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;

import java.awt.Dimension;
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

import processing.app.exec.SystemOutSiphon;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class SplitPane extends JFrame {
	List<Header> listofHeaders = new ArrayList<Header>();
	DefaultTreeModel treeModel;
	JEditorPane editorPane = new JEditorPane();
	DefaultMutableTreeNode Root;
	JTree tree;
	HTMLEditorKit editorkit;
	JScrollPane leftscrollPane;
	JScrollPane rightscrollPane;
	ArrayList<String> referenceList = new ArrayList<String>();

	public SplitPane() {
		Root = new DefaultMutableTreeNode("");
		setTree();
		treeModel = new DefaultTreeModel(Root);
		tree = new JTree(treeModel);
		tree.setRootVisible(false);
		tree.getSelectionModel().addTreeSelectionListener(new Selector());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		leftscrollPane = new JScrollPane(tree);
		rightscrollPane = new JScrollPane(editorPane);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(leftscrollPane);
		splitPane.setRightComponent(rightscrollPane);
		
		splitPane.setDividerLocation(200);
		
		editorPane.setEditable(false);
		editorkit = new HTMLEditorKit();
//		editorkit.setAutoFormSubmission(false);
//		editorPane.setEditorKit(editorkit);
		
//		editorPane.addHyperlinkListener(new HyperlinkListener() {
//			@Override
//			public void hyperlinkUpdate(HyperlinkEvent e) {
//				if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
//					handleLink(e.getURL().toExternalForm());
//				}
//			}
//		});
		splitPane.setPreferredSize(new Dimension(600,400));
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
					tempHeader.add_miscLeaves(referenceList.get(j));
				}
			}
			
			counter += tempHeaderMiscNum;
			
			// check for subheaders and if number of subheaders is not 0
			if(tempHeader.get_numberofSubHeaders()!=0) {
				// per subhead
				for(Map.Entry<SubHeader, Integer> entry : tempSubHeaders.entrySet()) {
					SubHeader tempSubHead = entry.getKey();
					Integer tempSubHeadNum = entry.getValue();
					ArrayList<DefaultMutableTreeNode> inputList = new ArrayList<DefaultMutableTreeNode>();
					for(int j = counter; j < tempSubHeadNum + counter; j++) {
						// add reference
						String tempString = referenceList.get(j);
						inputList.add(new DefaultMutableTreeNode(tempString));
					}
					
					tempSubHead.add_leaf(inputList);
					counter += tempSubHeadNum;
				}
			}
		}
	}
	
	public void setTree() {
		readinLists();
		assignReferences();
		printheaders();
	}
	

	public void setFile(URL urllink) {
		try {
			editorPane.setPage(urllink);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
	
//	@Override
//	public void valueChanged(TreeSelectionEvent e) {
//		
//	}
	private class Selector implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			
			if (node == null) 
				return;
			
			String nodeName = node.toString();
			
			if(node.isLeaf()) {
				
			}
		}
	}
}



