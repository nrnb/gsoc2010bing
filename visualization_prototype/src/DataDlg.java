import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.Border;

public class DataDlg extends JDialog
{
    private JButton jButton1;       // ok 
    private JButton jButton2;       // cancel
    private JPanel contentPane;
    private VisGUI vis; // the parent window which is a JFrame 
    

    
    public DataDlg(Frame vis)
    {
	super(vis);
	this.vis=(VisGUI)vis;
	initializeComponent();
	this.setVisible(false);
    }

    private void initializeComponent()
    {

	contentPane = (JPanel)this.getContentPane();
	
	JButton preview = new JButton("Arial Narrow");
	preview.setContentAreaFilled(false);
	preview.setOpaque(true);
	preview.setBackground(Color.white);

	jButton1 = new JButton();
	jButton2 = new JButton();
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


	contentPane.setLayout(null);
	addComponent(contentPane, new JLabel("Font:"), 10,15,100,25);
	addComponent(contentPane, new JLabel("Display precision:"), 10,45,100,25);
	addComponent(contentPane, preview, 100,15,100,20);
	addComponent(contentPane, new JTextField("2"), 100,45,155,20);
	addComponent(contentPane, new JButton("..."), 210,15,50,25);
	addComponent(contentPane, jButton1, 210,80,50,25);
	//addComponent(contentPane, jButton2, 275,530,80,25);

	this.setTitle("Appearance");
	this.setLocation(new Point(450, 200));
	this.setSize(new Dimension(275, 145));

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

 
}




