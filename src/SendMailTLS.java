import java.io.File;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
 
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;
 
public class SendMailTLS {
        final String username = "scanner@unixsurplus.com";
        final String password = "un1xsurplus";
        Properties props = new Properties();
        Session session;
        
	public SendMailTLS (String toAddress, String invoiceNumber, String sourceFile, HardDriveInvoice hdi) {
                props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("scanner@unixsurplus.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(toAddress));
			message.setSubject("Serial Numbers for invoice: " + invoiceNumber);
			message.setText(hdi.toString());
                        DataSource source = new FileDataSource(sourceFile);
                        DataHandler handler = new DataHandler(source);
                        message.setDataHandler(handler);
                        message.setFileName(this.getFileName(sourceFile));
			Transport.send(message);
                        JOptionPane.showMessageDialog(null,"Email sent sucessfully to "+toAddress);
 
		} catch (MessagingException e) {
                        System.err.println(e);
			JOptionPane.showMessageDialog(null,
                        "An error occurred sending the email. Please try again, or contact the systems administrator.",
                        "Email send failure",
                        JOptionPane.ERROR_MESSAGE);
		}
	}
        
        public SendMailTLS (String toAddress, String invoiceNumber, String [] sourceFiles, HardDriveInvoice hdi) {
                props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		 try{
                    Message message = new MimeMessage(session);
                                        message.setFrom(new InternetAddress("scanner@unixsurplus.com"));
                                        message.setRecipients(Message.RecipientType.TO,
                                                InternetAddress.parse(toAddress));
                                        message.setSubject("Serial Numbers for invoice: " + invoiceNumber);
                                        message.setText(hdi.toString());
                    Multipart multipart = new MimeMultipart();
                    MimeBodyPart messageBodyPart = new MimeBodyPart();
                    messageBodyPart.setText(hdi.toString());
                    multipart.addBodyPart(messageBodyPart);

                    // Part two is attachment
                    for( int i = 0; i < sourceFiles.length; i++ ) {
                        messageBodyPart = new MimeBodyPart();
                        FileDataSource fileDataSource =new FileDataSource(sourceFiles[i]);
                        messageBodyPart.setDataHandler(new DataHandler(fileDataSource));
                        messageBodyPart.setFileName(new File(sourceFiles[i]).getName());
                        multipart.addBodyPart(messageBodyPart);
                    }
                    // Put parts in message
                    message.setContent(multipart);
                    // Send the message
                    Transport.send( message );
                    JOptionPane.showMessageDialog(null,"Email sent sucessfully to "+toAddress);
                } catch(MessagingException e){
                    System.err.println(e);
                    JOptionPane.showMessageDialog(null,
                        "An error occurred sending the email. Please try again, or contact the systems administrator.",
                        "Email send failure",
                        JOptionPane.ERROR_MESSAGE);
                }
	}
        
        private String getFileName(String filePath){
            File attachment = new File(filePath);
            return attachment.getName();
        }
       
}