import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.ImageIcon;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.Component;
import java.net.URL;


public class TableDialogEditDemo extends JPanel {
    private boolean DEBUG = false;
    private JScrollPane scrollPane;
    private JTable table;
    private JDialog editColorSet;

    public TableDialogEditDemo() {
        super(new GridLayout(1,0));
	//Border etch = BorderFactory.createEtchedBorder();
	//setBorder (BorderFactory.createTitledBorder (etch, "Color Sets"));

	//this.vis=vis;
        table = new JTable(new MyTableModel());
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        //Create the scroll pane and add the table to it.
        scrollPane = new JScrollPane(table);

	//Set up table size.
        initTableSize(table);	

        //Set up renderer and editor for the Favorite Color column.
        //table.setDefaultRenderer(Image.class,
	//                   new ImageRenderer(true));

        table.setDefaultEditor(ImageIcon.class,
                               new IconEditor());

        //Add the scroll pane to this panel.
        add(scrollPane);

	//editColorSet=


    }

    private void initTableSize(JTable table) {
	table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	table.setRowHeight(18);  
	table.getColumnModel().getColumn(0).setPreferredWidth(65);
	table.getColumnModel().getColumn(1).setPreferredWidth(150);
	table.getColumnModel().getColumn(2).setPreferredWidth(80);
	table.getColumnModel().getColumn(3).setPreferredWidth(50);
	table.getColumnModel().getColumn(4).setPreferredWidth(50);
    }

    public void displayTable(boolean flag) {
	scrollPane.setVisible(flag);
    }


    class MyTableModel extends AbstractTableModel {
        private String[] columnNames = {"Name",
                                        "Color Set",
                                        "Parameters",
                                        "Image",
                                        "Display"};
	
	/*
	private JButton[] buttons ={
	    createButton("colorset-1.gif",190,15),
	};
	*/
	
	


	private Object[][] data = {
            {"color set-1", 
	     new ImageIcon(getScaledImage((new ImageIcon(getClass().getResource("colorset-1.gif"))).getImage(),190,15)),
	     //buttons[0],
	     "fold EB vs ES",
	     new ImageIcon(getScaledImage((new ImageIcon(getClass().getResource("protein_hi.gif"))).getImage(),16,16)), 
	     new Boolean(true)},
	    {"color set-2", 
	     new ImageIcon(getScaledImage((new ImageIcon(getClass().getResource("colorset-4.gif"))).getImage(),190,15)),
	     "ES1, ES2", 
	     new ImageIcon(getScaledImage((new ImageIcon(getClass().getResource("mRNA_hi.gif"))).getImage(),16,16)), 
	     new Boolean(true)},
	    {"color set-3",
	     new ImageIcon(getScaledImage((new ImageIcon(getClass().getResource("colorset-2.gif"))).getImage(),190,15)),
	     "",  new ImageIcon(""), 
	     new Boolean(false)},
	    {"color set-4", 
	     new ImageIcon(getScaledImage((new ImageIcon(getClass().getResource("colorset-3.gif"))).getImage(),190,15)),
	     "", new ImageIcon(""), new Boolean(false)},
	    {"color set-5", new ImageIcon(""),
	     "", new ImageIcon(""), 
	     new Boolean(false)},
	    {"color set-6", new ImageIcon(""),
	     "", new ImageIcon(""), 
	     new Boolean(false)},
	    {"color set-7", new ImageIcon(""),
	     "", new ImageIcon(""), 
	     new Boolean(false)},
	};
	
	/*
        private Object[][] data = {
            {"color set-1", new Color(153, 0, 153),"fold EB vs ES", 
	     new ImageIcon(getScaledImage((new ImageIcon("protein_hi.gif")).getImage(),16,16)), new Boolean(true)},
            {"color set-2", new Color(51, 51, 153),"ES1, ES2", 
	     new ImageIcon(getScaledImage((new ImageIcon("mRNA_hi.gif")).getImage(),16,16)), new Boolean(true)},
            {"color set-3", new Color(51, 102, 51),
             "", new ImageIcon(""), new Boolean(false)},
            {"color set-4", Color.red,
             "", new ImageIcon(""), new Boolean(false)},
            {"color set-5", Color.pink,
             "", new ImageIcon(""), new Boolean(false)}
        };
	*/
        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
	    /*
            if (col < 1) {
                return false;
            } else {
                return true;
            }
	    */
	    if (col == 2) 
		return false;
	    else
		return true;
        }

        public void setValueAt(Object value, int row, int col) {
            if (DEBUG) {
                System.out.println("Setting value at " + row + "," + col
                                   + " to " + value
                                   + " (an instance of "
                                   + value.getClass() + ")");
            }

            data[row][col] = value;
            fireTableCellUpdated(row, col);

            if (DEBUG) {
                System.out.println("New value of data:");
                printDebugData();
            }
        }

        private void printDebugData() {
            int numRows = getRowCount();
            int numCols = getColumnCount();

            for (int i=0; i < numRows; i++) {
                System.out.print("    row " + i + ":");
                for (int j=0; j < numCols; j++) {
                    System.out.print("  " + data[i][j]);
                }
                System.out.println();
            }
            System.out.println("--------------------------");
        }

	/**
	 * Resizes an image using a Graphics2D object backed by a BufferedImage.
	 * @param srcImg - source image to scale
	 * @param w - desired width
	 * @param h - desired height
	 * @return - the new resized image
	 */
	private Image getScaledImage(Image srcImg, int w, int h){
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g2 = resizedImg.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();
	    return resizedImg;
	}
	
	/*

	private JButton createButton(String img, int w, int h){
	    JButton button = new JButton(new ImageIcon(getScaledImage((new ImageIcon(getClass().getResource(img))).getImage(),w,h)));
	    button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e)
		    {
			vis.dataDlg.setVisible(true);
		    }
		    
		});
	    return button;
	}
	*/
	
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
	/*
       //Create and set up the window.
        JFrame frame = new JFrame("TableDialogEditDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new TableDialogEditDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
	*/
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
