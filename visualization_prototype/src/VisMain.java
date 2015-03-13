import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class VisMain
{
    
    public static void main(String[] args)
    {
       
	JFrame.setDefaultLookAndFeelDecorated(true);
	JDialog.setDefaultLookAndFeelDecorated(true);
	try
	{
	    UIManager.setLookAndFeel(
	    UIManager.getSystemLookAndFeelClassName());
	}
	catch (Exception ex)
	{
	    System.out.println("Failed loading L&F: ");
	    System.out.println(ex);
	}
	
	final JFrame w=new VisGUI();
	w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
