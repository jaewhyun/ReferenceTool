package template.tool;

import java.io.*;
import java.util.*;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

public class SplitPane extends JFrame
						implements TreeSelectionListener {
	List<Header> listofHeaders = new ArrayList();
	
	DefaultTreeModel treeModel;
	JTree tree = new JTree(treeModel);
	JEditorPane editorPane = new JEditorPane();
	DefaultMutableTreeNode Root, Structure, Environment, Data, Data_Primitive, Data_Composite, Data_Conversion, 
		Data_StringFunctions, Data_ArrayFunctions, Control, Control_RelationalOperators, Control_Iteration, Control_Conditionals, 
		Control_LogicalOperators, Shape, Shape_2DPrimitives, Shape_Curves, Shape_3DPrimitives, Shape_Attributes, Shape_Vertex, 
		Shape_Loading_Displaying, Input, Input_Mouse, Input_Keyboard, Input_Files, Input_Time_Date, Output, Output_TextArea, 
		Output_Image, Output_Files, Transform, Lights_Camera, LC_Lights, LC_Camera, LC_Coordinates, LC_MaterialProperties,
		Color, Color_Setting, Color_Creating_Reading, Image, Image_Loading_Displaying, Image_Textures, Image_Pixels, Rendering, 
		Rendering_Shaders, Typography, Typography_Loading_Displaying, Typography_Attributes, Typography_Metrics, Math, 
		Math_Operators, Math_BitwiseOperators, Math_Calculation, Math_Trigonometry, Math_Random, Constants;
	
	JScrollPane leftScrollPane = new JScrollPane(tree);
	JScrollPane rightScrollPane = new JScrollPane(editorPane);
//	rightScrollPane.getviewport().add(editorPane);
	HTMLEditorKit editorkit;
	
	JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScrollPane, rightScrollPane);
	
	
	public SplitPane() {
		setSize(1024, 600);
		editorPane.setEditable(false);
		getContentPane().add(splitPane);
		setVisible(true);
		editorkit = new HTMLEditorKit();
		editorkit.setAutoFormSubmission(false);
		editorPane.setEditorKit(editorkit);
		
		editorPane.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					handleLink(e.getURL().toExternalForm());
				}
			}
		});
		Root = new DefaultMutableTreeNode("");
		treeModel = new DefaultTreeModel(Root);
		setTree();
		tree.setRootVisible(false);
		splitPane.setDividerLocation(160);
	}
	
	public void readinLists() {
		try (BufferedReader br = new BufferedReader(new FileReader("/data/reference_list.txt"))){
			String line;
			while((line = br.readLine()) != null) {
				// add header to root
				Header newheader = new Header(line);
				Root.add(newheader.get_header());
				//get line for the number of misc references
				line = br.readLine();
				newheader.set_misc(Integer.parseInt(line));
				// get line for the number of subheaders
				line = br.readLine();
				if(Integer.parseInt(line) == 0) {
					continue;
				} else {
//					newheader.set_numberofSubHeaders(Integer.parseInt(line));
					LinkedHashMap<SubHeader, Integer> tempHashMap = new LinkedHashMap();
					for(int i = 0; i < Integer.parseInt(line); i++) {
						// get line for subheader 
						line = br.readLine();
						SubHeader tempSubHeader = new SubHeader(line);
						// get line for number of subheader references
						Integer number = Integer.parseInt(br.readLine());
						tempHashMap.put(tempSubHeader, number);
					}
					newheader.set_subHeader_Number(tempHashMap);
				}
				
				newheader.add_subHeaders();
				listofHeaders.add(newheader);
			}
		}
	}
	
	public void assignReferences() {
		try(BufferedReader br = new BufferedReader(new FileReader("/data/reference_summary.txt"))) {
			String line;
			while((line = br.readLine()) != null) {
				for(int i = 0; i < listofHeaders.size(); i++) {
					Header tempHeader = listofHeaders.get(i);
					if(tempHeader.get_misc() != 0) {
						for(int j = 0; j < tempHeader.get_misc(); j++) {
							line = br.readLine();
							tempHeader.add_miscLeaves(line);
						}
					}
					
					
					if(tempHeader.get_numberofSubHeaders()!= 0) {
						for(Map.Entry<SubHeader, Integer> entry : tempHeader.get_subHeader_Number().entrySet()) {
							SubHeader temp = entry.getKey();
							int tempNum = entry.getValue();
							for(int j = 0; j < tempNum; j++) {
								line = br.readLine();
								temp.add_leaf(line);
							}
						}
					}
				}
			}
		}
	}
	
	public void setTree() {
		readinLists();
		assignReferences();
//		Structure = new DefaultMutableTreeNode("Structure");
//		Root.add(Structure);
//		Environment = new DefaultMutableTreeNode("Environment");
//		Root.add(Environment);
//		Data = new DefaultMutableTreeNode("Data");
//		Root.add(Data);
//		Data_Primitive = new DefaultMutableTreeNode("Primitive");
//		Data.add(Data_Primitive);
//		Data_Composite = new DefaultMutableTreeNode("Composite");
//		Data.add(Data_Composite);
//		Data_Conversion = new DefaultMutableTreeNode("String Functions");
//		Data.add(Data_Conversion);
//		Data_ArrayFunctions = new DefaultMutableTreeNode("Array Functions");
//		Data.add(Data_ArrayFunctions);
//		Control = new DefaultMutableTreeNode("Control");
//		Root.add(Control);
//		Control_RelationalOperators = new DefaultMutableTreeNode("Relational Operators");
//		Control.add(Control_RelationalOperators);
//		Control_Iteration = new DefaultMutableTreeNode("Iteration");
//		Control.add(Control_Iteration);
//		Control_Conditionals = new DefaultMutableTreeNode("Conditionals");
//		Control.add(Control_Conditionals);
//		Control_LogicalOperators = new DefaultMutableTreeNode("Logical Operators");
//		Control.add(Control_LogicalOperators);
//		Shape = new DefaultMutableTreeNode("Shape");
//		Root.add(Shape);
//		Shape_2DPrimitives = new DefaultMutableTreeNode("2D Primitives");
//		Shape.add(Shape_2DPrimitives);
//		Shape_Curves = new DefaultMutableTreeNode("Curves");
//		Shape.add(Curves);
//		Shape_3DPrimitives = new DefaultMutableTreeNode("3D Primitives");
//		Shape.add(Shape_3DPrimitives);
//		Shape_Attributes = new DefaultMutableTreeNode("Attributes");
//		Shape.add(Shape_Attributes);
//		Shape_Vertex = new DefaultMutableTreeNode("Vertex");
//		Shape.add(Shape_Vertex);
//		Shape_Loading_Displaying = new DefaultMutableTreeNode("Loading, Displaying");
//		Shape.add(Shape_Loading_Displaying);
//		Input = new DefaultMutableTreeNode("Input");
//		Root.add(Input);
//		Input_Mouse = new DefaultMutableTreeNode("Mouse");
//		Input.add(Input_Mouse);
//		Input_Keyboard = new DefaultMutableTreeNode("Keyboard");
//		Input.add(Input_Keyboard);
//		Input_Files = new DefaultMutableTreeNode("Files");
//		Input.add(Input_Files);
//		Input_Time_Date = new DefaultMutableTreeNode("Time and Date");
//		Input.add(Input_Time_Date);
//		Output = new DefaultMutableTreeNode("Output");
//		Root.add(Output);
//		Output_TextArea = new DefaultMutableTreeNode("Text Area");
//		Output.add(Output_TextArea);
//		Output_Image = new DefaultMutableTreeNode("Image");
//		Output.add(Output_Image);
//		Output_Files = new DefaultMutableTreeNode("Files");
//		Output.add(Output_Files);
//		Transform = new DefaultMutableTreeNode("Transform");
//		Root.add(Transform);
//		Lights_Camera = new DefaultMutableTreeNode("Lights and Camera");
//		Root.add(Lights_Camera);
//		LC_Lights = new DefaultMutableTreeNode("Lights");
//		Lights_Camera.add(LC_Lights);
//		LC_Camera = new DefaultMutableTreeNode("Camera");
//		Lights_Camera.add(LC_Camera);
//		LC_Coordinates = new DefaultMutableTreeNode("Coordinates");
//		Lights_Camera.add(LC_Coordinates);
//		LC_MaterialProperties = new DefaultMutableTreeNode("Material Properties");
//		Lights_Camera.add(LC_MaterialProperties);
//		Color = new DefaultMutableTreeNode("Color");
//		Root.add(Color);
//		Color_Setting = new DefaultMutableTreeNode("Setting");
//		Color.add(Color_Setting);
//		Color_Creating_Reading = new DefaultMutableTreeNode("Creating and Reading");
//		Color.add(Color_Creating_Reading);
//		Image = new DefaultMutableTreeNode("Image");
//		Root.add(Image);
//		Image_Loading_Displaying = new DefaultMutableTreeNode("Loading and Displaying");
//		Image.add(Image_Loading_Displaying);
//		Image_Textures = new DefaultMutableTreeNode("Textures");
//		Image.add(Image_Textures);
//		Image_Pixels = new DefaultMutableTreeNode("Pixels");
//		Image.add(Image_Pixels);
//		Rendering = new DefaultMutableTreeNode("Rendering");
//		Root.add(Rendering);
//		Rendering_Shaders= new DefaultMutableTreeNode("Shaders");
//		Rendering.add(Rendering_Shaders);
//		Typography = new DefaultMutableTreeNode("Typography");
//		Root.add(Typography);
//		Typography_Loading_Displaying = new DefaultMutableTreeNode("Loading and Displaying");
//		Typography.add(Typography_Loading_Displaying);
//		Typography_Attributes = new DefaultMutableTreeNode("Attributes");
//		Typography.add(Typography_Attributes);
//		Typography_Metrics = new DefaultMutableTreeNode("Metrics");
//		Typography.add(Typography_Metrics);
//		Math = new DefaultMutableTreeNode("Math");
//		Root.add(Math);
//		Math_Operators = new DefaultMutableTreeNode("Operators");
//		Math.add(Math_Operators);
//		Math_BitwiseOperators = new DefaultMutableTreeNode("Bitwise Operators");
//		Math.add(Math_Bitwise_Operators);
//		Math_Calculation = new DefaultMutableTreeNode("Calculation");
//		Math.add(Math_Calculation);
//		Math_Trigonometry = new DefaultMutableTreeNode("Trigonometry");
//		Math.add(Math_Trigonometry);
//		Math_Random = new DefaultMutableTreeNode("Random");
//		Math.add(Math_Random);
//		Constants = new DefaultMutableTreeNode("Constants");
//		Root.add(Constants);
	}
	
//	public void set_leaves() {
//		Structure.add(new DefaultMutableTreeNode("() (parentheses)"));
//		Structure.add(new DefaultMutableTreeNode(", (comma)"));
//		Structure.add(new DefaultMutableTreeNode(". (dot)"));
//		Structure.add(new DefaultMutableTreeNode("/* */ (multiline comment)"));
//		Structure.add(new DefaultMutableTreeNode("/** */ (doc comment)"));
//		Structure.add(new DefaultMutableTreeNode("/// (comment)"));
//		Structure.add(new DefaultMutableTreeNode("; (semicolon)"));
//		Structure.add(new DefaultMutableTreeNode("= (assign)"));
//		Structure.add(new DefaultMutableTreeNode("[] (array access)"));
//		Structure.add(new DefaultMutableTreeNode("{} (curly braces)"));
//		Control_RelationalOperators.add(new DefaultMutableTreeNode("!= (inequality)"));
//		Control_RelationalOperators.add(new DefaultMutableTreeNode("< (less than)"));
//		Control_RelationalOperators.add(new DefaultMutableTreeNode("<= (less than or equal to)");
//		Control_RelationalOperators.add(new DefaultMutableTreeNode("== (equality)");
//		Control_RelationalOperators.add(new DefaultMutableTreeNode("> (greater than)");
//		Control_RelationalOperators.add(new DefaultMutableTreeNode(">= (greater than or equal to)");
//		Control_Conditionals.add(new DefaultMutableTreeNode("?: (conditional)"));
//		Math_Operators.add(new DefaultMutableTreeNode("% (modulo)"));
//		Math_Operators.add(new DefaultMutableTreeNode("* (multiply)"));
//		Math_Operators.add(new DefaultMutableTreeNode("*= (multiply assign)"));
//		Math_Operators.add(new DefaultMutableTreeNode("+ (addition)"));
//		Math_Operators.add(new DefaultMutableTreeNode("++ (increment)"));
//		Math_Operators.add(new DefaultMutableTreeNode("+= (add assign)"));
//		Math_Operators.add(new DefaultMutableTreeNode("- (minus)"));
//		Math_Operators.add(new DefaultMutableTreeNode("-- (decrement)"));
//		Math_Operators.add(new DefaultMutableTreeNode("-= (subtract assign)"));
//		Math_Operators.add(new DefaultMutableTreeNode("/ (divide)"));
//		Math_Operators.add(new DefaultMutableTreeNode("/= (divide assign)"));
//		Math_BitwiseOperators.add(new DefaultMutableTreeNode("& (bitwise AND)"));
//		Math_BitwiseOperators.add(new DefaultMutableTreeNode("<< (left shift)"));
//		Math_BitwiseOperators.add(new DefaultMutableTreeNode(">> (right shift)"));
//		Math_BitwiseOperators.add(new DefaultMutableTreeNode("| (bitwise OR)"));
//	}
	
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
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				
		if (node == null) 
			return;
		
		if(node.isLeaf()) {
			
		}
	}
}
