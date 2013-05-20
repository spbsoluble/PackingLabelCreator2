
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.Barcode39;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sean
 */
public class PDFLabel {
    
    
    private static final String FILE_EXTENSION = ".lbl";    //default file extension for the data save files
    private String hintText = "";
    private HardDriveInvoice invoice;
    private ArrayList<String> serials;
    private String invoiceNumber;
    private String model;
    private int unitsPerCase = 20;
    private int PACK_SIZE = 20;
    private String currentFileName = "";
    
    public PDFLabel(HardDriveInvoice hdi){
        this.invoice = hdi;
        this.invoiceNumber = hdi.getInvoiceNumber();
        this.model = hdi.getModel();
        this.serials = hdi.getSerials();
    }
    
    public PDFLabel(HardDriveInvoice hdi, String hintText, int unitsPerCase){
        this.invoice = hdi;
        this.hintText = hintText;
        this.invoiceNumber = hdi.getInvoiceNumber();
        this.unitsPerCase = unitsPerCase;
        this.model = hdi.getModel();
        this.serials = hdi.getSerials();
    }
    
    public void getFileName(){
        
    }
    
     /*
  * Function: createFileDirectory
  * Purpose: Generates the directory structure the application uses. Places a folder in the user's 'home'
  * directory called 'Documents' then the 'PackingLabels' directory where the pdf files are stored, then
  * the LabelFiles directory where the load data (FILE_EXTENSION) data is stored, and finally the csvs
  * directory where all csv copies of the data are stored. 
  * Function is 100% passive in that if will only create a directory if it doesn't exist.
  * @returns true - if directory structure was generated successfull
  * @returns false - if for whatever reason it failed...and it probably throws an exception that isn't handled
  */
 private boolean createFileDirectory(){
     return (new File(System.getProperty("user.home")+"\\Documents\\PackingLabels\\LabelFiles\\csvs")).mkdirs();
 }
    
     /*
  * Function: saveDataText
  * Purpose: Saves the label data to a text file using the FILE_EXTENSION and as a csv file
  */
 private void saveDataText(){
     String fileName;       //stores FILE_EXTENSION name
     String fileNameCSV;    //stores CSV name
     fileName = resolveFileName(FILE_EXTENSION);    //resolves the path to the FILE_EXTENSION file
     fileNameCSV = resolveFileName(".csv");         //resolves the path to the csv file
     SerialFileManager sfm = new SerialFileManager();
     sfm.addToRepoFile(this.invoice);
     
     BufferedWriter writer = null;
     BufferedWriter csvWriter = null;
             
     try{
         if(invoice != null){
             writer = new BufferedWriter(new FileWriter(fileName));   //writer object that writes to the FILE_EXTENSION file
             invoice.writeToFile(writer);
             //this.updateSavedState(true);
         }
     } catch (FileNotFoundException fnf) {  //filenotfound thrown when directory is missing
         //try and recreate directory structure
         if(createFileDirectory()){ //if successful then call this function again
             saveDataText();
         } else {
             System.out.println("Unable to create file");   //if failure then give up and don't save
             return;
         }
         //this.updateSavedState(false);
     } catch (IOException ioe) {    //thrown when writer objects
         System.out.println(ioe);
         //this.updateSavedState(false);
     } finally {
         try {
             writer.close();
             //csvWriter.close();
         } catch (IOException ex) {
             Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
         }
         
     }
 }
    
    public void generatePDFLabel(boolean print){
        try{
            if(this.invoice.getSerials().size() <= 0){
                //custom title, error icon
                JOptionPane.showMessageDialog(null,
                    "No barcodes to generate, don't waste a label!",
                    "No data error",
                    JOptionPane.ERROR_MESSAGE);
                    //this.updateSavedState(true);
                return;
            } else if (this.invoice.getInvoiceNumber().equals(this.hintText) || this.invoiceNumber.trim().equals("")){
                 JOptionPane.showMessageDialog(null,
                    "Please title the label! \nFormat: Invoice, PO, Customer",
                    "Invalid Title",
                    JOptionPane.ERROR_MESSAGE);
                 //this.updateSavedState(false);
                return;
            }
            /*
             * step 1
             * Create document and set the page size. A6 is the closest thing to 4x6
             */

            Document document = new Document(PageSize.A6, 7.5f, 7.5f,0f,0f);
            /*
             * step 2
             * Create writer object and create the actual PDF file.
             */
            PdfWriter writer;
            String fileName = resolveFileName(".pdf");
            try{
                writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            } catch (FileNotFoundException fnf) {
               boolean success = createFileDirectory();
                if (success) {
                  writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
                } else {
                    System.err.println("Unable to make directory");
                    return;
                }
            }

            /*
             * step 3
             * Open the document by opening that actual PDF file
             */
            document.open();

            /*
             * step 4
             * Initialize table and barcode objects as well as other variables to be used
             */
            PdfContentByte cb = writer.getDirectContent();      //needed to convert a barcode to an image

            int caseNumber = 1;     //the current box number for the label; defaults to 1
            int numberOfCases = 1;  //the max number of boxes; defaults to 1

            PdfPTable table = initTable(3);     //init pdf table 

            PdfPCell cell = new PdfPCell();     //initalize cell object to dump the cell objects into
            cell.setFixedHeight(1f);            //set the max height of a cell table cell element
            cell.setPaddingTop(1f);             //set the padding at the top of the cell to help with spacing


            Barcode128 code128 = new Barcode128(); //init barcode object 
            code128.setSize(6f);                 //set barcode text size
            code128.setBarHeight(8);             //set the height of the bars
            code128.setX(.5f);                   //set the width of the bars
            int counter = 1;

            //determine the number of boxes based on the number of serial numbers scanned in and units per box
            numberOfCases = (int)Math.ceil((float)this.invoice.getHdQuantity() / unitsPerCase);
            if(numberOfCases <= 0){
                numberOfCases = 1;
            }


            addHeader(document, writer, caseNumber, numberOfCases); //add title and header to the document

            for(int i = 0; i < this.invoice.getHdQuantity(); i ++, counter++){
                try{
                    
                    String value = this.serials.get(i);                          //convert value to string

                    //if for whatever reason the value is null or empty do nothing, decrement the counter and continue to the next row
                    if(value == null || value.trim().equals("")){
                        counter--;
                        continue;
                    } else {
                        boolean atLabelCapacity = (i % PACK_SIZE == 0 && i != 0);   //check to see if you're at the max units per label
                        boolean atBoxCapacity = (i % unitsPerCase == 0 && i != 0); //check to see if you're at max units for a box
                        if(atBoxCapacity || atLabelCapacity) {                      //if you're at capacity you're going to need to make a newlabel
                            if((atLabelCapacity && atBoxCapacity) || atBoxCapacity){ //if you're at both capacities then increment case number                                              //of if you're just at box capacity increment case number
                                caseNumber++;
                            } 
                            document.add(table);                                    //add the current table to the document
                            document.newPage();                                     //create a new page
                            addHeader(document, writer, caseNumber, numberOfCases); //add header to the new page
                            table = initTable(3);                                   //initialize a new table object
                        }

                        //create cell to hold current item number/iteration/count
                        cell = new PdfPCell(new Paragraph(""+counter, new Font(Font.FontFamily.HELVETICA, 6)));
                        cell.setBorder(0);              //remove cell border
                        cell.setPaddingTop(1f);         //pad the top of the cell a little for spacing
                        table.addCell(cell);            //add the cell to table

                        code128.setCode(value.toUpperCase());    //set barcode code

                        if(value.length() > 40){            //if you're over 40 chars adjust the scale a bit
                            code128.setX(.4f);
                        }

                        //create barcode image
                        com.itextpdf.text.Image tehImage = code128.createImageWithBarcode(cb, null, null); 

                        cell = new PdfPCell(tehImage);  //add image data to cell object
                        cell.setBorder(0);              //remove cell border
                        cell.setPaddingTop(1f);         //set the top padding for spacing
                        table.addCell(cell);            //add the cell to the table
                    }
                } catch(Exception e) {  //catch any exceptions and just press forward
                    continue;
                }
            }


            document.add(table);  //finally add table to the document

            writer.setOpenAction(new PdfAction(PdfAction.PRINTDIALOG)); //make it so print dialog opens when pdf opens
            /*
             * step 5
             * Close the document
             */
            document.close();

            //save the data to .FILE_EXTENSION and .csv
            this.saveDataText();


            //open up adobe with the file you just made
            if (print && Desktop.isDesktopSupported()) {
                try {
                    File myFile = new File(fileName);
                    Desktop.getDesktop().open(myFile);
                } catch (FileNotFoundException fnf){
                    JOptionPane.showMessageDialog(null,
                        "Please close the PDF file before creating a new label!",
                        "File access error",
                        JOptionPane.ERROR_MESSAGE);
                    //this.updateSavedState(true);
                    return;
                } catch (IOException ioe) {
                      JOptionPane.showMessageDialog(null,
                        "Error generating PDF file, please restart program and try again.",
                        "IO Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                } 
            }
        } catch (FileNotFoundException fnf){
                    JOptionPane.showMessageDialog(null,
                    "Please close the PDF file before creating a new label!",
                    "File access error",
                    JOptionPane.ERROR_MESSAGE);
                return;
        } catch (DocumentException de) {
                JOptionPane.showMessageDialog(null,
                    "There was a problem generating the label, please create a new label and try again.",
                    "Document error",
                    JOptionPane.ERROR_MESSAGE);
                return;
        } 
    }
    
    /*
  * Function: resolveFileName
  * Purpose: Resolves the path for a file to be saved by the program. Also handles the auto
  * filename incrementing.
  * @param: String fileType - the type of file that is being saved {PDF,LBL,CSV}
  * @returns: String fileName - the name as the absolute path to the file
  */
 private String resolveFileName(String fileType){
     String fileName;   //holds file name
     File dataFile;     //file object to test if file exists
     String location;   //location of the file
     String title = this.invoice.getInvoiceNumber();  //title of the file
     
     //if no file name provided then use 'untitled'
     if(title.trim() == null || title.trim().equals("")){
         title = "untitled";
     }
     
     //Block to determine where to save the file based on file extension
     if(fileType.equals(".pdf")){
         location = System.getProperty("user.home")+"\\Documents\\PackingLabels\\";
     } else if (fileType.equals(".lbl")) {
         location = System.getProperty("user.home")+"\\Documents\\PackingLabels\\LabelFiles\\";
     } else if (fileType.equals(".csv")){
         location = System.getProperty("user.home")+"\\Documents\\PackingLabels\\LabelFiles\\csvs\\";
     } else {
         location = System.getProperty("user.home")+"\\Documents\\PackingLabels\\LabelFiles\\";
     }
     
     int i = 0; //file name increment counter
     do{        //loop until you have a unique file name
         if(i == 0){
             fileName = location + (title+fileType).trim();
         } else {
             fileName = location + (title + "["+i+"]"+fileType).trim();
         }
         dataFile = new File(fileName);
         i++;
     } while(dataFile.exists());    
     this.setCurrentFileName(fileName);
     return fileName;
 }
    
 private void setCurrentFileName(String fileName){
     this.currentFileName = fileName;
 }
 /*
 * Function: manipulatePdf
 * Purpose: *unused* Rotates the pdf by 90 degrees after writing; intended for use with
 * long barcode handling.
 * @param: String src: the source pdf file to rotate
 * @param: String dest: the destination pdf file to save rotation.
 */
 public void manipulatePdf(String src, String dest) throws IOException, DocumentException {
        PdfReader reader = new PdfReader(src);
        int n = reader.getNumberOfPages();
        int rot;
        PdfDictionary pageDict;
        for (int i = 1; i <= n; i++) {
            rot = reader.getPageRotation(i);
            pageDict = reader.getPageN(i);
            pageDict.put(PdfName.ROTATE, new PdfNumber(rot + 90));
        }
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dest));
        stamper.close();
 }

 /*
  * Function: scaleBarcode
  * Purpose: creates a barcode image given a string. The image is scaled if the string length
  * is longer than 17 characters; which seems to be the max number of characters that fit on a 
  * 4x6 label.
  * @param: String code: the string you want to convert to a barcode image
  * @param: PdfWriter writer: reference to a writer which is needed to created the image
  * @returns: itext.text.Image image of the barcode
  */
 public com.itextpdf.text.Image scaleBarcode(String code, PdfWriter writer){
    Barcode39 code39 = new Barcode39();
    code39.setSize(6f);     //sets font size of text below the barcode
    code39.setBarHeight(8); //sets the height of the bars for the barcode
    code39.setX(1);         //sets the min width of the bars
    
    code39.setCode(code);   //binds the input string to the Barcode39 object's code
    PdfContentByte cb = writer.getDirectContent();  //required to create the image
    com.itextpdf.text.Image tehImage = code39.createImageWithBarcode(cb, null, null); //itext Image object of the barcode
    
    //image scaling logic; limited to 17 characters unscaled by 4x6. 37 characters max scalable 
    if(code.length() > 17){
        tehImage.scaleAbsoluteWidth(270);
    }
    return tehImage;
 }
 
 /*
  * Function: initTable
  * Purpose: Initializes the pdf table that holds the serial numbers. Is called for each 'page' 
  * of the pdf.
  * @param: int size - the number of columns in the table
  * @returns: PdfPTable with the given number of columns and column widths
  */
 public PdfPTable initTable(int size){
        PdfPTable table = new PdfPTable(size);  //creates table object and sets number of columns
       
        float dims [] = new float [2];          //the array to hold the column widths
        dims[0] = .2f;                          //hard coded first column width; item number
        dims[1] = 4f;                           //hard coded barcode column width
        table = new PdfPTable(dims);            //creates the table with the new column widths
        table.setWidthPercentage(100);          //sets the width of the table to the width of the page
        return table;
 }
  
  /*
  * Function: addHeader
  * Purpose: Adds the header of the label that contains label title, model number, and the case number.
  * @param: Document document - reference to the current document being written to.
  * @param: PdfWriter writer - reference to the current writer writing to the pdf.
  * @param: int caseNumber - the current 'box' the label is on
  * @param: int numberOfCases - the max number of 'boxes' 
  */
  public void addHeader(Document document, PdfWriter writer, int caseNumber, int numberOfCases) throws DocumentException{
        String invoiceNumber = this.invoice.getInvoiceNumber();        //sends to uppercase in case conversion to barcode is needed
        String modelNumber = this.invoice.getModel();
        
        //label title object
        Paragraph labelTitle;
          
        //scaling logic for the title to prevent wrapping and offsetting the number of elements on a label
        if(invoiceNumber.length() < 32)
             labelTitle= new Paragraph(invoiceNumber,new Font(Font.FontFamily.COURIER,15));
        else if (invoiceNumber.length() < 39){
            labelTitle= new Paragraph(invoiceNumber,new Font(Font.FontFamily.COURIER,12));
        } else if(invoiceNumber.length() < 59) {
            labelTitle= new Paragraph(invoiceNumber,new Font(Font.FontFamily.COURIER,8));
        } else {
            labelTitle= new Paragraph(invoiceNumber,new Font(Font.FontFamily.COURIER,5));
        }
        
        //table object holding the title to hold the barcode
        PdfPTable titleTable = new PdfPTable(1);
        
        //sets table to the width of the page
        titleTable.setWidthPercentage(100f);
        
        labelTitle.setAlignment(Element.ALIGN_CENTER);
        
        //adds the label title to the document object
        document.add(labelTitle);
        
        //table object that holds header and case text
        PdfPTable table = new PdfPTable(new float[] {5f, 2f});
        table.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell();
        
        //Label Header Number
        Paragraph labelHeaderNumber = new Paragraph(modelNumber,new Font(Font.FontFamily.HELVETICA,12));
        labelHeaderNumber.setAlignment(Element.ALIGN_LEFT);
        cell = new PdfPCell(labelHeaderNumber);
    
        cell.setBorder(0);  //remove cell border
        table.addCell(cell);//adds the title cell to the table object
        
        //object to hold current box of max boxes text
         Paragraph caseNumberLabel = new Paragraph("Box: " + caseNumber + " of " + numberOfCases,new Font(Font.FontFamily.HELVETICA,12));
         
         //set text alignment to the right of the cell
         caseNumberLabel.setAlignment(Element.ALIGN_RIGHT);
         
         //puts the object text into a PdfPCell object
         cell = new PdfPCell(caseNumberLabel);
         cell.setBorder(0); //remove cell border
         table.addCell(cell);   //add the cell to the table object
        
        document.add(table);    //add table to the document 
        
        //paragraph object that holds 'Serial Numbers' label for the table
        Paragraph serials = new Paragraph("      Serial Numbers:", new Font(Font.FontFamily.HELVETICA,8));
        
        //adds serial numbers label to the document
        document.add(serials);
    }
}
