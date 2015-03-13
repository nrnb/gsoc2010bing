import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.Border;

public class ColorSetDlg extends JDialog
{
    private JLabel img;
    private JButton jButton1;       // ok 
    private JButton jButton2;       // cancel
    private JPanel gradientPane;
    private JRadioButton gradientRadio;
    private JComboBox gradientCombo; 
    private JComboBox parameterCombo;
    private JComboBox imageCombo; 
    //private JComboBox imageCombo2; 
    private JPanel rulePane;
    private JRadioButton ruleRadio;
    private RuleTable ruleTable;
    private JPanel imagePane;
    private JCheckBox imageCheck;
    private JPanel contentPane;
    private String url;
    private VisGUI vis; // the parent window which is a JFrame 
    

    
    public ColorSetDlg(Frame vis,String url)
    {
	super(vis);
	this.vis=(VisGUI)vis;
	this.url=url;
	initializeComponent();
	this.setVisible(false);
    }

    private void initializeComponent()
    {
	img = new JLabel();
	img.setIcon(new ImageIcon(getClass().getResource(url)));
	jButton1 = new JButton();
	jButton2 = new JButton();
	contentPane = (JPanel)this.getContentPane();

	jButton1.setText("OK");
	jButton1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{
		    jButton1_actionPerformed(e);
		}

	    });
	jButton2.setText("Cancel");
	jButton2.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{
		    jButton2_actionPerformed(e);
		}

	    });

	gradientRadio = new JRadioButton("Color by gradient",true);
	gradientRadio.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e)
		{
		    gradientRadio_itemStateChanged(e);
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
	Border etch = BorderFactory.createEtchedBorder();
	gradientPane.setBorder (BorderFactory.createTitledBorder (etch, "Gradient settings"));
	addComponent(gradientPane, new JLabel("Gradient:"), 10,20,70,30);
	addComponent(gradientPane, gradientCombo, 70,20,370,30);
	addComponent(gradientPane, new JTextField("-1.0"), 70,50,25,20);
	addComponent(gradientPane, new JTextField("0.0"), 240,50,25,20);
	addComponent(gradientPane, new JTextField("1.0"), 415,50,25,20);
	addComponent(gradientPane, new JLabel("Parameter:"), 10,75,70,30);
	addComponent(gradientPane, parameterCombo, 70,80,370,20);


	ruleRadio = new JRadioButton("Color by rule",false);
	ruleRadio.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e)
		{
		    ruleRadio_itemStateChanged(e);
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
	etch = BorderFactory.createEtchedBorder();
	rulePane.setBorder (BorderFactory.createTitledBorder (etch, "Rule settings"));
	addComponent(rulePane, ruleTable, 10,20,410,104);
	addComponent(rulePane, new JLabel("Rule logic:") , 10,130,80,20);
	addComponent(rulePane, new JTextField() , 70,130,225,20);
	addComponent(rulePane, new JButton("Add") ,300,130,55,20);
	addComponent(rulePane, new JButton("Remove") , 357,130,75,20);
	addComponent(rulePane, listScroller , 10,160,50,68);
	addComponent(rulePane, listScroller2 , 70,160,225,68);
	addComponent(rulePane, new JLabel("Rule logic OK") , 10,230,400,20);
	addComponent(rulePane, ruleColorButton , 310,170,70,25);
	addComponent(rulePane, new JButton("Color") , 310,200,70,25);
	addComponent(rulePane, new JButton(new ImageIcon(getClass().getResource("top.gif"))),422,20,25,25);
	addComponent(rulePane, new JButton(new ImageIcon(getClass().getResource("up.gif"))), 422,47,25,25);
	addComponent(rulePane, new JButton(new ImageIcon(getClass().getResource("down.gif"))), 422,74,25,25);
	addComponent(rulePane, new JButton(new ImageIcon(getClass().getResource("bottom.gif"))), 422,101,25,25);
	rulePane.setVisible(false);
	//enableTree(rulePane,false);
	//ruleTable.enableTable(false);
	//ruleTable.displayTable(false);




	imageCheck = new JCheckBox("Use image",false);
	imageCheck.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e)
		{
		    imageCheck_itemStateChanged(e);
		}
		
	    });
	imagePane = new JPanel();
	imagePane.setLayout(null);
	etch = BorderFactory.createEtchedBorder();
	imagePane.setBorder (BorderFactory.createTitledBorder (etch, "Image settings"));

        imageCombo = new JComboBox();
	imageCombo.addItem("protein.bmp");
	imageCombo.addItem("mRNA.bmp");
	JButton colorButton = new JButton();
	colorButton.setContentAreaFilled(false);
	colorButton.setOpaque(true);
	colorButton.setBackground(Color.pink);
	JLabel colorLabel = new JLabel(new ImageIcon(getClass().getResource("image.gif")));

	addComponent(imagePane, new JLabel("Select image:"), 10,15,80,20);
	addComponent(imagePane, imageCombo, 80,15,120,20);
	addComponent(imagePane, new JButton("Load image"), 210,15,90,20);
	addComponent(imagePane, new JLabel("Transparent color:"), 10,40,100,20);
	addComponent(imagePane, colorButton, 110,40,25,20);
	addComponent(imagePane, new JButton("Change"), 150,40,70,20);
	addComponent(imagePane, new JLabel("Tolerance:"), 10,70,100,20);
	addComponent(imagePane, new JTextField("0"), 110,70,30,20);
	addComponent(imagePane, colorLabel, 320,15,77,75);

	imagePane.setVisible(false);



	contentPane.setLayout(null);
	addComponent(contentPane, gradientRadio, 5,0,150,25);
	addComponent(contentPane, gradientPane, 5,22,460,110);
	addComponent(contentPane, ruleRadio, 5,135,150,25);
	addComponent(contentPane, rulePane, 5,50,460,260);
	addComponent(contentPane, imageCheck, 5,160,150,25);
	addComponent(contentPane, imagePane, 5,185,460,100);
	addComponent(contentPane, jButton1, 365,190,80,25);
	addComponent(contentPane, jButton2, 275,190,80,25);

	this.setTitle("Edit Color Set");
	this.setLocation(new Point(450, 200));
	this.setSize(new Dimension(490, 260));
    }

    /** Add Component Without a Layout Manager (Absolute Positioning) */
    private void addComponent(Container container,Component c,int x,int y,int width,int height)
    {
	c.setBounds(x,y,width,height);
	container.add(c);
    }


    private void jButton1_actionPerformed(ActionEvent e)
    {
	this.setVisible(false);
    }
    
    private void jButton2_actionPerformed(ActionEvent e)
    {
	this.setVisible(false);
    }

    private void gradientRadio_itemStateChanged(ItemEvent e) 
    { 
	if(e.getStateChange()==ItemEvent.DESELECTED){
	    gradientPane.setVisible(false);
	    ruleRadio.setSelected(true);
	    if(imagePane.isVisible()){
		this.setSize(new Dimension(490, 510));
		ruleRadio.reshape(5,25,150,25);
		imageCheck.reshape(5,310,150,25);
		jButton1.reshape(365,450,80,25);
		jButton2.reshape(275,450,80,25);
		imagePane.reshape(5,345,460,100);
	    } else {
		this.setSize(new Dimension(490, 410));
		ruleRadio.reshape(5,25,150,25);
		imageCheck.reshape(5,310,150,25);
		jButton1.reshape(365,350,80,25);
		jButton2.reshape(275,350,80,25);
	    }
	} else {
	    gradientPane.setVisible(true);
	    ruleRadio.setSelected(false);
	    if(imagePane.isVisible()){
		this.setSize(new Dimension(490, 360));
		ruleRadio.reshape(5,135,150,25);
		imageCheck.reshape(5,160,150,25);
		jButton1.reshape(365,290,80,25);
		jButton2.reshape(275,290,80,25);
		imagePane.reshape(5,185,460,100);
		
	    } else {
		this.setSize(new Dimension(490, 260));
		ruleRadio.reshape(5,135,150,25);
		imageCheck.reshape(5,160,150,25);
		jButton1.reshape(365,190,80,25);
		jButton2.reshape(275,190,80,25);
	    }

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

    private void imageCheck_itemStateChanged(ItemEvent e) 
    { 
	if(e.getStateChange()==ItemEvent.SELECTED){
	    imagePane.setVisible(true);
	    if(gradientPane.isVisible()){
		this.setSize(new Dimension(490, 360));
		jButton1.reshape(365,290,80,25);
		jButton2.reshape(275,290,80,25);
		imagePane.reshape(5,185,460,100);
	    } else {
		this.setSize(new Dimension(490, 510));
		jButton1.reshape(365,450,80,25);
		jButton2.reshape(275,450,80,25);
		imagePane.reshape(5,345,460,100);
	    }
	} else {
	    imagePane.setVisible(false);
	    if(gradientPane.isVisible()){
		this.setSize(new Dimension(490, 260));
		jButton1.reshape(365,190,80,25);
		jButton2.reshape(275,190,80,25);
	    } else {
		this.setSize(new Dimension(490, 410));
		jButton1.reshape(365,350,80,25);
		jButton2.reshape(275,350,80,25);
	    }

	}
    }

    /****************  Utilities  ********************/
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




