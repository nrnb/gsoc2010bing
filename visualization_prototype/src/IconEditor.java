import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class IconEditor extends AbstractCellEditor
                         implements TableCellEditor,
			            ActionListener {
    ImageIcon currentColor;
    JButton button;
    protected static final String EDIT = "edit";

    public IconEditor() {
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);

        //Set up the dialog that the button brings up.
	//colorSetDlg = new ColorSetDlg(null);

    }

    /**
     * Handles events from the editor button and from
     * the dialog's OK button.
     */
    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
	    int x = ((JButton)e.getSource()).getX();
	    //button.setIcon(currentColor);
	    //System.out.println(x);
	    if(x < 200)
		VisGUI.colorSetDlg1.setVisible(true);
	    else 
		VisGUI.imageDlg.setVisible(true);
            //Make the renderer reappear.
            fireEditingStopped();
	} else { //User pressed dialog's "OK" button.
        }
    }

    
    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
        return currentColor;
    }

    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        currentColor = (ImageIcon)value;
        return button;
    }
    
}

