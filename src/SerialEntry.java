
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
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
public class SerialEntry extends JPanel implements MouseListener{
    private int count;
    private String serialNumber;
    private JLabel count_lbl;
    private JLabel serialNumber_lbl;
    private JLabel delete_lbl;
    private JLabel edit_lbl;
    private GUI parent;
    
    ImageIcon deleteIcon = new ImageIcon("the_delete_icon.png");
    ImageIcon editIcon = new ImageIcon("application_edit.png");
    
    //BorderLayout panelLayout = new BorderLayout();
    GridBagLayout panelLayout = new GridBagLayout();
    
    public SerialEntry(int count, String serialNumber, GUI parent){
        this.count = count;
        this.parent = parent;
        this.serialNumber = serialNumber;
        createEntry();
        this.addMouseListener(this);
    }
    
    private void createEntry(){
        this.setLayout(panelLayout);
        GridBagConstraints constraints = new GridBagConstraints();
        //RIGHT GAP
        constraints.insets = new Insets(5,5,5,5);
        
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = 0;
        count_lbl = new JLabel(""+this.count+") ");
        this.add(count_lbl,constraints);
        
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 5;
        constraints.gridheight = 1;
        serialNumber_lbl = new JLabel(this.serialNumber);
        this.add(serialNumber_lbl, constraints);
        
        /*JPanel countPanel = new JPanel(new FlowLayout());
        countPanel.add(count_lbl);
        countPanel.add(serialNumber_lbl);*/
        
        
        constraints.gridx = 6;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        edit_lbl = new JLabel();
        edit_lbl.setIcon(editIcon);
        //this.add(edit_lbl,constraints);
        
        constraints.gridx = 7;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        delete_lbl = new JLabel();
        delete_lbl.setIcon(deleteIcon);
        //this.add(delete_lbl,constraints);
    
      
       //this.add(serialNumber_lbl);
       /*countPanel.add(edit_lbl);
       countPanel.add(delete_lbl);
       
        this.add(countPanel,BorderLayout.WEST);*/
    }
    
    private void setPanelSizes(){
        this.count_lbl.setSize(50, 100);
        this.count_lbl.setPreferredSize(new Dimension(50,1000));
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

    @Override
    public void mouseClicked(MouseEvent me) {
        System.out.println("I was clicked and my number is " + count);
        this.removeAll();
        this.validate();
        this.repaint();
        this.notifyParent();
    }

    @Override
    public void mousePressed(MouseEvent me) {
         
    }

    @Override
    public void mouseReleased(MouseEvent me) {
         
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        
    }

    @Override
    public void mouseExited(MouseEvent me) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void notifyParent(){
        parent.removeSerial(count);
    }
}
