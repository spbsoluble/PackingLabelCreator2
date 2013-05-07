
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sean
 */
public class HardDriveInvoice {
    private String invoiceNumber;
    private String model;
    private Date date;
    private int hdQuantity;
    private ArrayList<String> serials;
    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    
    public HardDriveInvoice(String invoiceNumber,String model, Date date, ArrayList<String> serials){
        this.invoiceNumber = invoiceNumber;
        this.model = model;
        this.date = date;
        this.serials = serials;
        this.hdQuantity = serials.size();
    }
    
    public String getInvoiceNumber(){
        return this.invoiceNumber;
    }
    
    public String getModel(){
        return this.model;
    }
    
    public String getDate(){
        return this.dateFormat.format(this.date);
    }
    
    public int getHdQuantity(){
        return this.hdQuantity;
    }
    
    public ArrayList<String> getSerials(){
        return this.serials;
    }
    
    public boolean findSerial(String serial){
        if(this.serials.contains(serial) || 
                this.serials.contains(serial.toLowerCase()) || this.serials.contains(serial.toUpperCase())){
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public String toString(){
        String output = "";
        output += "Invoice: " + this.invoiceNumber + "\n";
        output += "Model: " + this.model + "\n";
        output += "Date: " + this.getDate() + "\n";
        output += "Qty: " + this.hdQuantity + "\n";
        
        for(int i = 0; i < serials.size(); i++){
            output += i+1 + ":" + serials.get(i) + "\n";
        }
        return output;
    }
    
    public void writeToFile(BufferedWriter bw){
        try {
            bw.newLine();
            bw.write( "Invoice: " + this.invoiceNumber);

            bw.newLine();
            bw.write( "Model: " + this.model);
            bw.newLine();
            bw.write( "Date: " + this.getDate());
            bw.newLine();
            bw.write( "Qty: " + this.hdQuantity);
            bw.newLine();

            for(int i = 0; i < serials.size(); i++){
                bw.write( i+1 + ":" + serials.get(i));
                bw.newLine();
            }
            bw.newLine();
        } catch (IOException ex) {
            Logger.getLogger(HardDriveInvoice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
