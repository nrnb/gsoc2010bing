import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.net.URL;
import java.awt.image.BufferedImage;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class VisGUI extends JFrame
{  
    private JComboBox visType; 
    private JPanel contentPane;
    private JList colorSetList;
    private JPanel colorSetPane;
    private JPanel previewPane;
    private JPanel previewPane2;
    private TableDialogEditDemo colorSetTable;
    private JButton colorSetNewButton;
    private JButton colorSetEditButton;
    private JButton colorSetDeleteButton;
    private JPanel textLabelPane;
    private JCheckBox textLabelBox;
    private JCheckBox colorDataNodesBox;
    private JRadioButton textLabelButton;
    private JRadioButton identifierButton;
    private JRadioButton basicButton;
    private JRadioButton advancedButton;
    private JButton okButton,okButton2;
    static public ColorSetDlg colorSetDlg1;
    static public ColorSetDlg colorSetDlg2;
    static public DataDlg dataDlg;
    static public ImageDlg imageDlg;

    //private JPanel visPane;

    private JTabbedPane tabPane;

    private JPanel basicPane;
    private JPanel gradientPane;
    private JCheckBox gradientCheck;
    private JComboBox gradientCombo; 
    private JComboBox parameterCombo;
    private JPanel rulePane;
    private JCheckBox ruleCheck;
    private RuleTable ruleTable;
    private JRadioButton gradientRadio;
    private JRadioButton ruleRadio;
    
    
    private JPanel advancedPane;





    public VisGUI()
    {
	super();
	initializeComponent();
	this.setVisible(true);
    }
    
    private void initializeComponent()
    {
	contentPane = (JPanel)this.getContentPane(); 

	visType = new JComboBox();
	visType.addItem("auto-generated"); 	

	basicButton = new JRadioButton("Basic",true);
	advancedButton = new JRadioButton("Advanced");
	basicButton.setActionCommand("basic");
	advancedButton.setActionCommand("advanced");
	ButtonGroup group = new ButtonGroup();
	group.add(basicButton);
	group.add(advancedButton);
	basicButton.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e)
		{
		    basicButton_itemStateChanged(e);
		}
		
	    });
	
	advancedButton.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e)
		{
		    advancedButton_itemStateChanged(e);
		}
		
	    });

	okButton = new JButton("OK");
	okButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{
		    okButton_actionPerformed(e);
		}
		
	    });
	okButton2 = new JButton("OK");
	okButton2.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{
		    okButton_actionPerformed(e);
		}
		
	    });


	previewPane = new JPanel();
	previewPane.setLayout(null);
	Border etch0 = BorderFactory.createEtchedBorder();
	previewPane.setBorder (BorderFactory.createTitledBorder (etch0, "Preview"));
	addComponent(previewPane, new JLabel(new ImageIcon(getClass().getResource("preview.gif"))),60,12,118,60);

	previewPane2 = new JPanel();
	previewPane2.setLayout(null);
	Border etch02 = BorderFactory.createEtchedBorder();
	previewPane2.setBorder (BorderFactory.createTitledBorder (etch02, "Preview"));
	addComponent(previewPane2, new JLabel(new ImageIcon(getClass().getResource("preview.gif"))),60,12,118,60);


	// basic

	gradientRadio = new JRadioButton("Color by gradient",true);
	gradientRadio.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e)
		{
		    gradientRadio_itemStateChanged(e);
		}
		
	    });
	ruleRadio = new JRadioButton("Color by rule",false);
	ruleRadio.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e)
		{
		    ruleRadio_itemStateChanged(e);
		}
		
	    });


	gradientCheck = new JCheckBox("Color by gradient",true);
	gradientCheck.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e)
		{
		    gradientCheck_itemStateChanged(e);
		}
		
	    });
	
	gradientCombo = new JComboBox();
	gradientCombo.addItem(new ImageIcon(getClass().getResource("colorset-1.gif")));
	gradientCombo.addItem(new ImageIcon(getClass().getResource("colorset-2.gif")));
	gradientCombo.addItem(new ImageIcon(getClass().getResource("colorset-3.gif")));

	parameterCombo = new JComboBox();
	parameterCombo.addItem("log Fold EB vs ES");
	parameterCombo.addItem("ES avg");
	parameterCombo.addItem("ES2");
	parameterCombo.addItem("ES6");
	parameterCombo.addItem("EB avg");
	parameterCombo.addItem("EB1");
	parameterCombo.addItem("EB3");
	parameterCombo.addItem("EB5");
	parameterCombo.addItem("EB7");
	parameterCombo.addItem("EB9");

	gradientPane = new JPanel();
	gradientPane.setLayout(null);
	Border etchg = BorderFactory.createEtchedBorder();
	gradientPane.setBorder (BorderFactory.createTitledBorder (etchg, "Gradient settings"));
	addComponent(gradientPane, new JLabel("Gradient:"), 10,20,70,30);
	addComponent(gradientPane, gradientCombo, 70,20,370,30);
	addComponent(gradientPane, new JTextField("-1.0"), 70,50,25,20);
	addComponent(gradientPane, new JTextField("0.0"), 240,50,25,20);
	addComponent(gradientPane, new JTextField("1.0"), 415,50,25,20);
	addComponent(gradientPane, new JLabel("Parameter:"), 10,75,70,30);
	addComponent(gradientPane, parameterCombo, 70,80,370,20);

	ruleCheck = new JCheckBox("Color by rule",true);
	ruleCheck.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e)
		{
		    ruleCheck_itemStateChanged(e);
		}
		
	    });
	Object [] operators = {"AND", " OR", "  =", " <>", "  <", "  >", " <=", " >="};
	ruleTable = new RuleTable();
	JList list = new JList(operators); 
	list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
	list.setVisibleRowCount(4);
	JScrollPane listScroller = new JScrollPane(list);
	//listScroller.setPreferredSize(new Dimension(250, 80));

	String [] parameters = {"log Fold EB vs ES","ES avg","ES2","ES6",
				"EB avg","EB1","ES3","EB5","ES7","ES9"};
	JList list2 = new JList(parameters); 
	JScrollPane listScroller2 = new JScrollPane(list2);
	
	JButton ruleColorButton = new JButton();
	ruleColorButton.setContentAreaFilled(false);
	ruleColorButton.setOpaque(true);
	ruleColorButton.setBackground(Color.black);

	rulePane = new JPanel();
	rulePane.setLayout(null);
	Border etchr = BorderFactory.createEtchedBorder();
	rulePane.setBorder (BorderFactory.createTitledBorder (etchr, "Rule settings"));
	addComponent(rulePane, ruleTable, 10,17,410,105);
	addComponent(rulePane, new JLabel("Rule logic:") , 10,130,80,20);
	addComponent(rulePane, new JTextField() , 70,130,225,20);
	addComponent(rulePane, new JButton("Add") ,310,130,55,20);
	addComponent(rulePane, new JButton("Remove") , 370,130,75,20);
	addComponent(rulePane, listScroller , 10,160,50,68);
	addComponent(rulePane, listScroller2 , 70,160,225,68);
	addComponent(rulePane, new JLabel("Rule logic OK") , 10,230,400,20);
	addComponent(rulePane, ruleColorButton , 310,170,70,25);
	addComponent(rulePane, new JButton("Color") , 310,200,70,25);
	addComponent(rulePane, new JButton(new ImageIcon(getClass().getResource("top.gif"))),422,17,25,25);
	addComponent(rulePane, new JButton(new ImageIcon(getClass().getResource("up.gif"))), 422,44,25,25);
	addComponent(rulePane, new JButton(new ImageIcon(getClass().getResource("down.gif"))), 422,71,25,25);
	addComponent(rulePane, new JButton(new ImageIcon(getClass().getResource("bottom.gif"))), 422,98,25,25);



	basicPane = new JPanel();
	basicPane.setLayout(null);
	addComponent(basicPane, previewPane, 10,0,460,90);
	addComponent(basicPane, gradientRadio, 5,100,150,25);
	addComponent(basicPane, ruleRadio, 5,240,150,25);
	addComponent(basicPane, gradientPane, 10,125,460,110);
	addComponent(basicPane, rulePane, 10,155,460,260);
	addComponent(basicPane, okButton, 403,273,60,25);
	rulePane.setVisible(false);
	

	// advanced

	colorSetPane=new JPanel();
	colorSetPane.setLayout(null);
	colorSetTable = new TableDialogEditDemo();
	colorSetNewButton=new JButton("New");
	colorSetNewButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{
		    colorSetNewButton_actionPerformed(e);
		}
		
	    });
	colorSetEditButton=new JButton("Edit");
	colorSetEditButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{
		    colorSetNewButton_actionPerformed(e);
		}
		
	    });
	colorSetDeleteButton=new JButton("Delete");

	JPanel colorTablePane = new JPanel();
	colorTablePane.setLayout(null);
	Border etchc = BorderFactory.createEtchedBorder();
	colorTablePane.setBorder (BorderFactory.createTitledBorder (etchc, "Color Sets"));
	addComponent(colorTablePane, colorSetTable, 5,20,415,100);
	addComponent(colorTablePane, new JButton(new ImageIcon(getClass().getResource("top.gif"))),422,20,25,25);
	addComponent(colorTablePane, new JButton(new ImageIcon(getClass().getResource("up.gif"))), 422,47,25,25);
	addComponent(colorTablePane, new JButton(new ImageIcon(getClass().getResource("down.gif"))), 422,74,25,25);
	addComponent(colorTablePane, new JButton(new ImageIcon(getClass().getResource("bottom.gif"))), 422,101,25,25);
	addComponent(colorSetPane, colorTablePane, 0,0,460,130);
	addComponent(colorSetPane, colorSetNewButton, 255,130,60,25);
	addComponent(colorSetPane, colorSetEditButton, 320,130,60,25);
	addComponent(colorSetPane, colorSetDeleteButton, 385,130,70,25);


	advancedPane = new JPanel();

	JCheckBox [] parChecks = {new JCheckBox("log Fold EB vs ES"),
				  new JCheckBox("ES avg"),
				  new JCheckBox("ES2"),
				  new JCheckBox("ES6"),
				  new JCheckBox("EB avg"),
				  new JCheckBox("EB1"),
				  new JCheckBox("ES3"),
				  new JCheckBox("EB5"),
				  new JCheckBox("ES7"),
				  new JCheckBox("ES9")};

	JList listCheck = new CheckBoxList ();
	DefaultListModel defModel = new DefaultListModel();
	listCheck.setModel(defModel);
	//CheckBoxList listCheck = new CheckBoxList();
	for(int i=0;i<parChecks.length;i++)
	    defModel.addElement(parChecks[i]);

	JScrollPane listScrollerCheck = new JScrollPane(listCheck);

	JButton appButton=new JButton("Appearance...");
	appButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{
		    appButton_actionPerformed(e);
		}
		
	    });


	textLabelPane = new JPanel();
	textLabelPane.setLayout(null);
	Border etch = BorderFactory.createEtchedBorder();
	textLabelPane.setBorder (BorderFactory.createTitledBorder (etch, null));
	addComponent(textLabelPane, new JLabel("Select parameters:"), 10,10,100,25);
	addComponent(textLabelPane, listScrollerCheck, 10,40,410,108);
	addComponent(textLabelPane, new JButton(new ImageIcon(getClass().getResource("top.gif"))),422,40,25,25);
	addComponent(textLabelPane, new JButton(new ImageIcon(getClass().getResource("up.gif"))), 422,67,25,25);
	addComponent(textLabelPane, new JButton(new ImageIcon(getClass().getResource("down.gif"))), 422,94,25,25);
	addComponent(textLabelPane, new JButton(new ImageIcon(getClass().getResource("bottom.gif"))), 422,121,25,25);
	addComponent(textLabelPane, appButton, 10,155,120,25);
	textLabelPane.setVisible(false);
	textLabelBox = new JCheckBox("Show data value",false);
	textLabelBox.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e)
		{
		    textLabelBox_itemStateChanged(e);
		}
		
	    });
	colorDataNodesBox = new JCheckBox("Color DataNodes by their data value",true);
	colorDataNodesBox.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e)
		{
		    colorDataNodesBox_itemStateChanged(e);
		}
		
	    });


	colorSetDlg1 = new ColorSetDlg(this,"colorSetDlg1.gif");
	colorSetDlg2 = new ColorSetDlg(this,"colorSetDlg2.gif");
	dataDlg = new DataDlg(this);
	imageDlg = new ImageDlg(this);

	advancedPane.setLayout(null);
	addComponent(advancedPane, new JLabel("Visualization:"), 10,10,100,20);
	addComponent(advancedPane, visType, 80,10,390,20);
	addComponent(advancedPane, new JButton("New"), 270,35,60,25);
	addComponent(advancedPane, new JButton("Edit"), 335,35,60,25);
	addComponent(advancedPane, new JButton("Delete"), 400,35,70,25);
	addComponent(advancedPane, previewPane2, 10,60,460,90);
	addComponent(advancedPane, colorSetPane, 10,155,460,160);
	addComponent(advancedPane, textLabelBox, 10,310,400,25);
	addComponent(advancedPane, textLabelPane, 10,340,460,200);
	//addComponent(advancedPane, new JSeparator(SwingConstants.HORIZONTAL), 0,400,640,10);
	addComponent(advancedPane, okButton2, 403,353,60,25);


	// main view
	contentPane.setLayout(null);

	/*
	addComponent(contentPane, new JLabel("Visualization:"), 10,10,100,20);
	addComponent(contentPane, visType, 80,10,390,20);
	addComponent(contentPane, new JButton("New"), 270,35,60,25);
	addComponent(contentPane, new JButton("Edit"), 335,35,60,25);
	addComponent(contentPane, new JButton("Delete"), 400,35,70,25);
	addComponent(contentPane, basicButton, 10,60,80,25);
	addComponent(contentPane, advancedButton, 100,60,100,25);
	addComponent(contentPane, previewPane, 200,60,270,80);
	addComponent(contentPane, gradientCheck, 10,125,150,25);
	addComponent(contentPane, ruleCheck, 10,265,150,25);
	addComponent(contentPane, basicPane, 0,125,600,500);
	addComponent(contentPane, advancedPane, 0,125,600,500);
	advancedPane.setVisible(false);
	*/	

	tabPane = new JTabbedPane();;
	tabPane.addTab("Basic", basicPane);
	tabPane.addTab("Advanced", advancedPane);
	addComponent(contentPane, tabPane, 0,0,490,600);
	tabPane.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    tabPane_stateChanged(e);
		}
	    });
	
	this.setTitle("Visualization options");
	this.setLocation(new Point(200, 100));
	this.setSize(new Dimension(490, 370));
    }
    
    private void addComponent(Container container,Component c,int x,int y,int width,int height)
    {
	c.setBounds(x,y,width,height);
	container.add(c);
    }
    
    /****************  Event Methods  ********************/

    private void tabPane_stateChanged(ChangeEvent e)
    {
	if(tabPane.getSelectedIndex()==0){
	    if(gradientPane.isVisible()){
		this.setSize(new Dimension(490, 370));
	    } else {
		this.setSize(new Dimension(490, 530));
	    }
	} else {
	    if(textLabelPane.isVisible()){
		this.setSize(new Dimension(490, 630));
	    } else {
		this.setSize(new Dimension(490, 440));
	    }
	}

    }

    private void gradientRadio_itemStateChanged(ItemEvent e) 
    { 
	if(e.getStateChange()==ItemEvent.DESELECTED){
	    gradientPane.setVisible(false);
	    ruleRadio.setSelected(true);
	    this.setSize(new Dimension(490, 530));
	    ruleRadio.reshape(5,130,150,25);
	    okButton.reshape(403,423,60,25);
	} else {
	    gradientPane.setVisible(true);
	    ruleRadio.setSelected(false);
	    this.setSize(new Dimension(490, 370));
	    ruleRadio.reshape(5,240,150,25);
	    okButton.reshape(403,273,60,25);
	}
    }

    private void ruleRadio_itemStateChanged(ItemEvent e) 
    { 
	if(e.getStateChange()==ItemEvent.DESELECTED){
	    rulePane.setVisible(false);
	    gradientRadio.setSelected(true);
	} else {
	    rulePane.setVisible(true);
	    gradientRadio.setSelected(false);
	}
    }


    private void textLabelBox_itemStateChanged(ItemEvent e) 
    { 
	if(e.getStateChange()==ItemEvent.DESELECTED){
	    textLabelPane.setVisible(false);
	    this.setSize(new Dimension(490, 440));
	    okButton2.reshape(403,353,60,25);
	} else {
	    textLabelPane.setVisible(true);
	    this.setSize(new Dimension(490, 630));
	    okButton2.reshape(403,543,60,25);
	}
    }




    private void colorDataNodesBox_itemStateChanged(ItemEvent e) 
    { 
	if(e.getStateChange()==ItemEvent.DESELECTED){
	    enableTree(colorSetPane,false);
	    colorSetTable.displayTable(false);
	} else {
	    enableTree(colorSetPane,true);
	    colorSetTable.displayTable(true);
	}
    }

    private void basicButton_itemStateChanged(ItemEvent e)
    {
	if(e.getStateChange() == ItemEvent.SELECTED){
	    basicPane.setVisible(true);
	    gradientCheck.setVisible(true);
	    ruleCheck.setVisible(true);
	    advancedPane.setVisible(false);
	    if(gradientPane.isVisible() && rulePane.isVisible())
		this.setSize(new Dimension(490, 620));
	    if(gradientPane.isVisible() && !rulePane.isVisible())
		this.setSize(new Dimension(490, 370));
	    if(!gradientPane.isVisible() && rulePane.isVisible())
		this.setSize(new Dimension(490, 520));
	    if(!gradientPane.isVisible() && !rulePane.isVisible())
		this.setSize(new Dimension(490, 270));
	} else {
	}
    }
    
    private void advancedButton_itemStateChanged(ItemEvent e)
    {
	if(e.getStateChange() == ItemEvent.SELECTED){
	    basicPane.setVisible(false);
	    gradientCheck.setVisible(false);
	    ruleCheck.setVisible(false);
	    advancedPane.setVisible(true);
	    if(textLabelPane.isVisible())
		this.setSize(new Dimension(490, 620));
	    else 
		this.setSize(new Dimension(490, 420));
	} else {
	}
    }

    private void gradientCheck_itemStateChanged(ItemEvent e)
    {
	if(e.getStateChange() == ItemEvent.SELECTED){
	    gradientPane.setVisible(true);
	    if(rulePane.isVisible()){
		this.setSize(new Dimension(490, 620));
		ruleCheck.reshape(10,265,150,25);
		rulePane.reshape(10,165,460,260);
		okButton.reshape(403,433,60,25);
	    } else {
		this.setSize(new Dimension(490, 370));
		ruleCheck.reshape(10,265,150,25);
		rulePane.reshape(10,165,460,260);
		okButton.reshape(403,183,60,25);
	    }
		
	} else {
	    gradientPane.setVisible(false);
	    if(rulePane.isVisible()){
		this.setSize(new Dimension(490, 520));
		ruleCheck.reshape(10,165,150,25);
		rulePane.reshape(10,65,460,260);
		okButton.reshape(403,333,60,25);
	    } else {
		this.setSize(new Dimension(490, 270));
		ruleCheck.reshape(10,165,150,25);
		rulePane.reshape(10,65,460,260);
		okButton.reshape(403,83,60,25);
	    }
	}
    }

    private void ruleCheck_itemStateChanged(ItemEvent e)
    {
	if(e.getStateChange() == ItemEvent.SELECTED){
	    rulePane.setVisible(true);
	    if(gradientPane.isVisible()){
		okButton.reshape(403,433,60,25);
		this.setSize(new Dimension(490, 620));
	    } else {
		okButton.reshape(403,333,60,25);
		this.setSize(new Dimension(490, 520));
	    }
	} else {
	    rulePane.setVisible(false);
	    if(gradientPane.isVisible()){
		okButton.reshape(403,163,60,25);
		this.setSize(new Dimension(490, 350));
	    } else {
		okButton.reshape(403,63,60,25);
		this.setSize(new Dimension(490, 250));
	    }
	}
    }



    private void okButton_actionPerformed(ActionEvent e)
    {
	System.exit(0);
    }

    private void colorSetNewButton_actionPerformed(ActionEvent e)
    {
	colorSetDlg2.setVisible(true);
    }

    private void appButton_actionPerformed(ActionEvent e)
    {
	dataDlg.setVisible(true);
    }

    /****************  Utilities  ********************/

    private Image getScaledImage(Image srcImg, int w, int h){
	BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	Graphics2D g2 = resizedImg.createGraphics();
	g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	g2.drawImage(srcImg, 0, 0, w, h, null);
	g2.dispose();
	return resizedImg;
    }
    
    private void enableTree(Container root, boolean enable) {
	Component children[] = root.getComponents();
	for(int i = 0; i < children.length; i++) {
	    children[i].setEnabled(enable);
	    if(children[i] instanceof Container) {
		enableTree((Container)children[i], enable);
	    } else {
		children[i].setEnabled(enable);
	    }
	}
    }
}



