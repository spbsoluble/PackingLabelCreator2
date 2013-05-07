import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

/**
 *
 * @author Sean
 */


public class SerialFileManager {
    
    private static final String defaultFilePath = "10.10.50.50/Storage/HDSerials/";
    private static final String defaultFileName = "serialsInventory.txt";
    private static final String defaultNetworkUserName = "admin";
    private static final String defaultNetworkPassword = "un1xsurplus";
    private static final String defaultPrefix = "smb://";
    private static final String entryPrefix = "\n###\n";
    private static final String entryPostfix = "\n/###";
    
    SmbFile repository;
    
    
    public SerialFileManager(){
        //check if file exists if not create file
        //open output stream and append data to it
    }
    
    public void createRepoFile(){
        try{
            String storagePath = defaultPrefix + defaultNetworkUserName + ":" + 
                    defaultNetworkPassword + "@" + defaultFilePath + defaultFileName;
            SmbFile repoFile = new SmbFile(storagePath);
            this.repository = repoFile;
            if(!repoFile.exists()){
               repoFile.createNewFile();
            }
        } catch (SmbException ex){
            System.err.println(ex.toString());
        } catch (MalformedURLException ex){
            System.err.println(ex.toString());
            System.err.println("Invalid URL; please enter a valid path");
        }
    }
    
    public void createRepoFile(String path, String fileName){
        try{    
            String storagePath = defaultPrefix + defaultNetworkUserName + ":" + 
                    defaultNetworkPassword + "@" + path + fileName;
            SmbFile repoFile = new SmbFile(storagePath);
            this.repository = repoFile;
            if(!repoFile.exists()){
               repoFile.createNewFile();
            }
        } catch (SmbException ex){
            System.err.println(ex.toString());
        } catch (MalformedURLException ex){
            System.err.println(ex.toString());
            System.err.println("Invalid URL; please enter a valid path");
        }
    }
    
    public void createRepoFile(String path, String fileName, String userName, String password){
        try{    
            String storagePath = defaultPrefix + userName + ":" + 
                    password + "@" + path + fileName;
            SmbFile repoFile = new SmbFile(storagePath);
            this.repository = repoFile;
            if(!repoFile.exists()){
               repoFile.createNewFile();
            }
        } catch (SmbException ex){
            System.err.println(ex.toString());
        } catch (MalformedURLException ex){
            System.err.println(ex.toString());
            System.err.println("Invalid URL; please enter a valid path");
        }
    }
    
    public void readRepoFile(){
        
    }
    
    public void addToRepoFile(HardDriveInvoice invoice){
         
        
        try {
            String entry = entryPrefix + invoice.toString() + entryPostfix;
             String storagePath = defaultPrefix + defaultNetworkUserName + ":" + 
                    defaultNetworkPassword + "@" + defaultFilePath + defaultFileName;
            SmbFile repoFile = new SmbFile(storagePath);
            
            //os = new SmbFileOutputStream(repoFile);
            FileWriter fw = new FileWriter("\\\\"+defaultFilePath + defaultFileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            invoice.writeToFile(bw);
            bw.close();
            fw.close();
        
        } catch (SmbException ex) {
            Logger.getLogger(SerialFileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SerialFileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(SerialFileManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
                Logger.getLogger(SerialFileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            System.out.print("done");
        }
         
   }
    
    
    
}
