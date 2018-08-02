package jwh.referencetool;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.net.URL;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import processing.app.exec.SystemOutSiphon;
import processing.app.Base;
import processing.app.tools.Tool;
import processing.app.ui.Editor;
import processing.app.ui.Toolkit;
import processing.app.Sketch;
import processing.app.SketchCode;

public class SplitPane extends JFrame{
	ArrayList<Header> listofHeaders = new ArrayList<Header>();
	Editor editor;
	Base base;
	DefaultTreeModel treeModel;
	Font font;
	setHTML htmlPane = new setHTML();
	DefaultMutableTreeNode Root;
	JTree tree;
	Boolean filtered = false;
	
	String previousSearches[] = new String[5]; 
	DefaultComboBoxModel<DefaultMutableTreeNode> boxModel = new DefaultComboBoxModel<DefaultMutableTreeNode>();
	JComboBox<DefaultMutableTreeNode> searchBar;
	
	JTextArea textArea = new JTextArea();
	JButton reset;
	JButton search;
	JCheckBox searchAll;
	boolean mustSearchAll = false;
	boolean initiated = false;
	JScrollPane leftscrollPane;
	JScrollPane rightscrollPane;
	JPanel rightpanel;
	JComponent panel;
	ArrayList<String> referenceList = new ArrayList<String>();
	ArrayList<Leaf> trackLeaves = new ArrayList<Leaf>();
	HashSet<String> header_subheaderNames = new HashSet<String>();
	String splashhtml;
	int searchCount = 0;
	int open = 0;
	int openLocation = 0;
	
	
	public SplitPane(Base baseInput) {
		this.setTitle("References");
		base = baseInput;
		setGUI();
		this.setVisible(true);
		this.setResizable(false);
	}
	
	private void setGUI() {
		Font htmlfont = Toolkit.getSansFont(9, Font.PLAIN);
		
		Root = new DefaultMutableTreeNode("12345");
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
		rightpanel = new JPanel();
		  
		leftpanel.setLayout(new BoxLayout(leftpanel, BoxLayout.Y_AXIS));
		rightpanel.setLayout(new BorderLayout());
		
		searchAll = new JCheckBox("Search All");
		java.net.URL htmlURL = getClass().getResource("/data/splash.html");
		try {
			splashhtml = splashHTML(htmlURL);
		} catch (IOException e) {
			e.printStackTrace();
		}

		textArea.getDocument().addDocumentListener(new DocListener());
		reset = new JButton("RESET");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				collapseAll(tree);
				searchAll.setSelected(false);
				mustSearchAll = false;
				((JTextField) searchBar.getEditor().getEditorComponent()).setText("");
				htmlPane.setText(splashhtml);
				search.setEnabled(false);
				if(panel != null) {
					panel.removeAll();
					panel.revalidate();
				}
				enableComponents(leftscrollPane, true);
			}
		});
	
		search = new JButton("Search");
		search.setEnabled(false);
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enableComponents(leftscrollPane, true);
			}
		});
				
		JPanel buttonCheckPanel = new JPanel();
		buttonCheckPanel.setLayout(new BoxLayout(buttonCheckPanel, BoxLayout.LINE_AXIS));
		buttonCheckPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		buttonCheckPanel.add(searchAll);
		buttonCheckPanel.add(Box.createRigidArea(new Dimension(20, 0)));
		buttonCheckPanel.add(search);
		
		JPanel savedSearchesPanel = new JPanel();
		savedSearchesPanel.setLayout(new BoxLayout(savedSearchesPanel, BoxLayout.LINE_AXIS));
		searchBar = new JComboBox<DefaultMutableTreeNode>(boxModel);
		searchBar.setEditable(true);

		searchBar.setMaximumRowCount(5);
		searchBar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selStart = textArea.getSelectionStart();
				int selEnd = textArea.getSelectionEnd();
				textArea.replaceRange(searchBar.getSelectedItem().toString(), selStart, selEnd);
				((JTextField) searchBar.getEditor().getEditorComponent()).selectAll();
			}
		});
		
		((JTextField) searchBar.getEditor().getEditorComponent()).getDocument().addDocumentListener(new DocListener());
		((JTextField) searchBar.getEditor().getEditorComponent()).getDocument().putProperty("term", "Search");

		savedSearchesPanel.add(searchBar);
		
		leftscrollPane = new JScrollPane(tree);

		rightscrollPane = new JScrollPane(htmlPane);
		htmlPane.setFont(font);

		rightpanel.add(rightscrollPane, BorderLayout.CENTER);
		
		searchBar.setAlignmentX(Component.CENTER_ALIGNMENT);
		leftpanel.add(savedSearchesPanel);
		buttonCheckPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		leftpanel.add(buttonCheckPanel);
		leftscrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		leftpanel.add(leftscrollPane);
		JPanel resetPanel = new JPanel();
		resetPanel.setLayout(new BorderLayout(0,0));
		resetPanel.add(reset);
		leftpanel.add(resetPanel);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(leftpanel);
		splitPane.setRightComponent(rightpanel);

		htmlPane.setText(splashhtml);

		htmlPane.setEditable(false);
		
		splitPane.setDividerLocation(200);
		splitPane.setDividerSize(2);
		
		splitPane.setPreferredSize(new Dimension(770,400));
		
		this.getContentPane().add(splitPane);
		
		readinAllHTML();
	}
	
	private void readinLists() {
		java.net.URL htmlURL = getClass().getResource("/data/reference_summary.txt");
				
		try (BufferedReader br = new BufferedReader(new InputStreamReader(htmlURL.openStream()))){
			String line;
			while((line = br.readLine()) != null) {
				Header newheader = new Header(line);
				header_subheaderNames.add(line);
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
						header_subheaderNames.add(line);
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
	
	private void assignReferences() {
		java.net.URL htmlURL = getClass().getResource("/data/reference_list.txt");
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
	
	private void assignleafMiscs() {
		java.net.URL htmlURL = getClass().getResource("/data/leafmiscs.txt");
		ArrayList<String> listofmiscs = new ArrayList<String>();
		
		int counter = 0;
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(htmlURL.openStream()))) {
			String line;
			String[] output;
			
			int count = 0;
			while((line = br.readLine()) != null) {
				if(line.indexOf('_') >= 0) {
					output = line.split("_");
					for(Leaf currentleaf : trackLeaves) {
						String currentleafName = currentleaf.get_leaf().toString();
						if(currentleafName.equals(output[0])) {
//							clickableNodes.add(currentleafName.toUpperCase());
							if(output[1].indexOf(',') == -1) {
								Integer numberofMethods = Integer.parseInt(output[1]);
								
								ArrayList<DefaultMutableTreeNode> methods = new ArrayList<DefaultMutableTreeNode>();
								for(int i = 0; i < numberofMethods; i++) {
									line = br.readLine();
									DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(line);
									methods.add(newnode);
								}
								
								currentleaf.add_leafMethods(methods);
							} else {
								String[] parsed = output[1].split(",");
								Integer numberofFields = Integer.parseInt(parsed[0]);
								Integer numberofMethods = Integer.parseInt(parsed[1]);
								
								ArrayList<DefaultMutableTreeNode> methods = new ArrayList<DefaultMutableTreeNode>();
								ArrayList<DefaultMutableTreeNode> fields = new ArrayList<DefaultMutableTreeNode>();
								
								for(int i = 0; i < numberofFields; i++) {
									line = br.readLine();
									DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(line);
									fields.add(newnode);
								}
								
								for(int i = 0; i < numberofMethods; i++) {
									line = br.readLine();
									DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(line);
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
	
	private void setTree() {
		readinLists();
		assignReferences();
		assignleafMiscs();
		for(Header head : listofHeaders) {
			head.connect_nodes();
		}
	}
	
	public void setFile(String nodeName, String original) {
		
		System.out.println("in setfile"); 
		htmlPane.parseHTML(null, nodeName, initiated, searchAll.isSelected(), ((JTextField) searchBar.getEditor().getEditorComponent()).getText());  
		
		HashMap<String, ArrayList<String>> mapofCodes = htmlPane.get_mapofCodes();
		ArrayList<String> exampleCodes = null;
		if(mapofCodes.containsKey(nodeName)) {
			exampleCodes = mapofCodes.get(nodeName);
		}
		
		if(exampleCodes.size() != 0) {
			panel = Box.createHorizontalBox();
			panel.setBackground(new Color(245, 245, 245));
			JPanel panelButtons = new JPanel();
			panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.LINE_AXIS));

			panelButtons.add(Box.createRigidArea(new Dimension(10,0)));
			panelButtons.add(Box.createHorizontalGlue());
			
			for(int i = 0; i < exampleCodes.size(); i++) {
				JButton newbutton;
				final String exampleCode = exampleCodes.get(i);
				
				newbutton = new JButton(Integer.toString(i+1));
				newbutton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(open == 0) {
							base.handleNew();
							openLocation = base.getEditors().size() - 1;
						}
						open = 1;
						
						if(openLocation < base.getEditors().size()) {
							Editor editor = base.getEditors().get(openLocation);
							
							editor.setText(exampleCode);
							editor.requestFocus();
						} else {
							base.handleNew();
							openLocation = base.getEditors().size() - 1;
							Editor editor = base.getEditors().get(openLocation);
							
							editor.setText(exampleCode);
							editor.requestFocus();
						}
					}
				});
				newbutton.setActionCommand(Integer.toString(i));
				panelButtons.add(newbutton);
			}
			
			panel.add(panelButtons);
			rightpanel.add(panel, BorderLayout.PAGE_END);
			panel.revalidate();
			
		} else {
			if(panel != null) {
				panel.removeAll();
			}
		}
		System.out.println("successfully setfile");
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
	
	private void filterTree(String text) {
		String filteredText = text;
		DefaultMutableTreeNode filteredRoot = copyNode(Root);
		
		if(text.trim().toString().equals("")) {
			treeModel.setRoot(Root);
			tree.setModel(treeModel);

			tree.updateUI();
//			
			leftscrollPane.getViewport().setView(tree);
			
			DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
			renderer.setLeafIcon(null);
			renderer.setClosedIcon(null);
			renderer.setOpenIcon(null);
			
			return;
		} else {
			HashMap<String, String> searchAllExamples = htmlPane.get_searchAllExamples();
			HashMap<String, String> searchAllDescriptions = htmlPane.get_searchAllDescriptions();
			HashMap<String, String> savedHTML = htmlPane.get_savedHTML();
			
			TreeNodeBuilder b = new TreeNodeBuilder(text, savedHTML, header_subheaderNames, searchAllExamples, searchAllDescriptions);
			
			filteredRoot = b.prune((DefaultMutableTreeNode) filteredRoot.getRoot(), searchAll.isSelected());

			treeModel.setRoot(filteredRoot);
			
			tree.setModel(treeModel);

			tree.updateUI();
	
			leftscrollPane.getViewport().setView(tree);	

		}
		
		for(int i = 0; i<tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
		
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		renderer.setOpenIcon(null);
	
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
	
	public void readinAllHTML() {
		Enumeration e = Root.preorderEnumeration();
		
		while(e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			
			String nodeName = "";
			String htmlfileName = "";
			NodeNameGenerator gen = new NodeNameGenerator(header_subheaderNames);
			nodeName = gen.generator(node);
			
			if(node.isLeaf()) {
				htmlfileName = "/data/reference/" + nodeName + ".html";
				java.net.URL htmlURL = getClass().getResource(htmlfileName);
				htmlPane.parseHTML(htmlURL, nodeName, initiated, false, "");
			} else {
				if(!node.isRoot() 
						&& !node.toString().equals("Methods") 
						&& !node.toString().equals("Fields")
						&& !header_subheaderNames.contains(node.toString())) {
					htmlfileName = "/data/reference/" + nodeName + ".html";
					java.net.URL htmlURL = getClass().getResource(htmlfileName);
					htmlPane.parseHTML(htmlURL, nodeName, initiated, false, "");
				}
			}
		}
	}
	
	
	private static String splashHTML(URL htmlURL) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(htmlURL.openStream()));
		String line;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");
		
		try {
			while ((line = in.readLine()) != null) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			
			return stringBuilder.toString();
		} finally {
			in.close();
		}
	}
	
	private class Selector implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {

			System.out.println("yikes");
			if(panel != null) 
				panel.removeAll();
		
			initiated = true;
			
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		
			if (node == null) 
				return;
			
			NodeNameGenerator gen = new NodeNameGenerator(header_subheaderNames);
			String nodeName = gen.generator(node);
			
			String htmlfileName = null;
			
			System.out.println("here");
			if(node.isLeaf()) {
				DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node.getParent();
				DefaultMutableTreeNode nodeGParent = (DefaultMutableTreeNode) nodeParent.getParent();

				System.out.println("here too");
				String currentText = ((JTextField) searchBar.getEditor().getEditorComponent()).getText();
				System.out.println(currentText);
			
				setFile(nodeName, node.toString());
				
				searchBar.insertItemAt(node, 0);
				
				if(searchBar.getItemCount() > 5) {
					searchBar.removeItemAt(5);
				}
			} else {
				if(!node.isRoot() 
						&& !node.toString().equals("Methods") 
						&& !node.toString().equals("Fields")
						&& !header_subheaderNames.contains(node.toString())) {
					setFile(nodeName, node.toString());
					
					searchBar.insertItemAt(node, 0);
					
					if(searchBar.getItemCount() > 4) {
						searchBar.removeItemAt(5);
					}
				}
			}
		}
	}
	
	private class SelectFocus extends FocusAdapter {
		public void focusGained(FocusEvent evt) {

		}
	}
	
	private void collapseAll(JTree tree) {
		treeModel.setRoot(Root);
		tree.setModel(treeModel);
		
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setClosedIcon(null);
		
		renderer.setOpenIcon(null);

		int row = tree.getRowCount() - 1;
		while(row >= 0) {
			tree.collapseRow(row);
			row--;
		}
	}
	
	/*
	 * https://stackoverflow.com/questions/10985734/java-swing-enabling-disabling-all-components-in-jpanel
	 * Andrew Thompson
	 */
	private static void enableComponents(Container container, boolean enable) {
		Component[] components = container.getComponents();
		for(Component component : components) {
			component.setEnabled(enable);
			if(component instanceof Container) {
				enableComponents((Container) component, enable);
			}
		}
	}

	private class DocListener implements DocumentListener {
		public void insertUpdate(DocumentEvent e) {
			filterTree(((JTextField) searchBar.getEditor().getEditorComponent()).getText());
			enableComponents(leftscrollPane, false);
			search.setEnabled(true);
		}
		
		public void removeUpdate(DocumentEvent e) {
			filterTree(((JTextField) searchBar.getEditor().getEditorComponent()).getText());
			enableComponents(leftscrollPane, false);
			search.setEnabled(true);
			
			if(((JTextField) searchBar.getEditor().getEditorComponent()).getText().equals("")) {
				enableComponents(leftscrollPane, true);
			}
		}
		
		public void changedUpdate(DocumentEvent e) {
			
		}
	}
}