
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sean
 */
public class SerialEntry{
    private int count;
    private String serialNumber;
    private JLabel count_lbl;
    private JLabel serialNumber_lbl;
    private JLabel delete_lbl;
    private JLabel edit_lbl;
    private JPanel container_panel;
    
    ImageIcon deleteIcon = new ImageIcon("the_delete_icon.png");
    ImageIcon editIcon = new ImageIcon("application_edit.png");
    
    GridLayout panelLayout = new GridLayout(0,4);
    
    public SerialEntry(int count, String serialNumber){
        this.count = count;
        this.serialNumber = serialNumber;
        createEntry();
    }
    
    private void createEntry(){
        container_panel = new JPanel();
        container_panel.setLayout(panelLayout);
        
        serialNumber_lbl = new JLabel(this.serialNumber, JLabel.LEFT);
        count_lbl = new JLabel(""+this.count, JLabel.LEFT);
        
        edit_lbl = new JLabel();
        edit_lbl.setIcon(editIcon);
        
        delete_lbl = new JLabel();
        delete_lbl.setIcon(deleteIcon);
        
       container_panel.add(count_lbl);
       container_panel.add(serialNumber_lbl);
       container_panel.add(edit_lbl);
       container_panel.add(delete_lbl);
    }
    
    public JPanel drawSerialEntry(){
        return this.container_panel;
    }
    
    public String getSerialNumber(){
        return this.serialNumber;
    }
    
    public int getCount(){
        return this.count;
    }
    
    public SerialEntry setSerialNumber(String serialNumber){
        this.serialNumber = serialNumber;
        createEntry();
        return this;
    }
    
    public SerialEntry setCount(int count){
        this.count = count;
        createEntry();
        return this;
    }
}
