import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.Border;

public class ImageDlg extends JDialog
{
    private JButton jButton1;       // ok 
    private JPanel contentPane;
    private VisGUI vis; // the parent window which is a JFrame 


    
    public ImageDlg(Frame vis)
    {
	super(vis);
	this.vis=(VisGUI)vis;
	initializeComponent();
	this.setVisible(false);
    }

    private void initializeComponent()
    {

	contentPane = (JPanel)this.getContentPane();

	contentPane.setLayout(null);
	//etch = BorderFactory.createEtchedBorder();
	//contentPane.setBorder (BorderFactory.createTitledBorder (etch, "Image settings"));

        JComboBox imageCombo = new JComboBox();
	imageCombo.addItem("protein.bmp");
	imageCombo.addItem("mRNA.bmp");
	JButton colorButton = new JButton();
	colorButton.setContentAreaFilled(false);
	colorButton.setOpaque(true);
	colorButton.setBackground(Color.pink);
	JLabel colorLabel = new JLabel(new ImageIcon(getClass().getResource("image.gif")));

	addComponent(contentPane, new JLabel("Select image:"), 10,15,80,20);
	addComponent(contentPane, imageCombo, 80,15,120,20);
	addComponent(contentPane, new JButton("Load image"), 210,15,90,20);
	addComponent(contentPane, new JLabel("Transparent color:"), 10,40,100,20);
	addComponent(contentPane, colorButton, 110,40,25,20);
	addComponent(contentPane, new JButton("Change"), 150,40,70,20);
	addComponent(contentPane, new JLabel("Tolerance:"), 10,70,100,20);
	addComponent(contentPane, new JTextField("0"), 110,70,30,20);
	addComponent(contentPane, colorLabel, 320,15,77,75);

	JButton jButton1 = new JButton();
	jButton1.setText("OK");
	jButton1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e)
		{
		    jButton1_actionPerformed(e);
		}

	    });
	addComponent(contentPane, jButton1, 350,120,50,25);

	this.setTitle("Image Settings");
	this.setLocation(new Point(500, 250));
	this.setSize(new Dimension(450, 190));

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
    
 
}




