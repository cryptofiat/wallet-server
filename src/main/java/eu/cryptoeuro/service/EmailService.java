package eu.cryptoeuro.service;

import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import eu.cryptoeuro.config.ContractConfig;

import eu.cryptoeuro.rest.command.NotifyEscrowCommand;
import org.springframework.beans.factory.annotation.Value;
import java.util.Properties;  
import javax.mail.*;  
import javax.mail.internet.*;  
  
@Slf4j
@Component
public class EmailService {  

  @Autowired
  private ContractConfig contractConfig;

  static final String host="mail.smtp2go.com";  
  static final int port=2525;  
  static final String from="wallet-server@euro2.ee";

  @Value("${email.user}")
  private String user;

  @Value("${email.password}")
  private String password;

  public void notifyEscrow(NotifyEscrowCommand cmd) {

  	String CONTRACT_ADDRESS = contractConfig.getDelegationContractAddress(); //"0xaf71e622792f47119411ce019f4ca1b8d993496e";
	String body = new String();

	//TODO: move this to thymeleaf or some other template engine
	body = "Hi " + cmd.recipientFirstName + ", \n\n";
	body += cmd.senderFirstName + " " + cmd.senderLastName;
	body += " sent you €" + cmd.amountString() + " on Euro 2.0.\n\n";
	body += "Go to http://wallet.euro2.ee to use the money.\n\n";
	body += "Transaction: https://etherscan.io/tx/" + cmd.transactionHash + "\n";
	body += "Your escrow address: https://etherscan.io/token/" + CONTRACT_ADDRESS + "?a=" + cmd.escrowAddress + "\n";

	log.info("Starting to send email with text: " + body);
	sendEmail(cmd.email, "You received €"+cmd.amountString() + " from "+cmd.senderFirstName, body);
  }
    
  public void sendEmail(String to, String subject, String body) {
	  
	   Properties props = new Properties();  
	   props.put("mail.smtp.host",host);  
	   props.put("mail.smtp.port",String.valueOf(port));  


	   //log.info("logging to email w user: "+user+" pass: "+password);
	   //Get the session object  
	   props.put("mail.smtp.auth", "true");  
	     
	   Session session = Session.getDefaultInstance(props,  
	    new javax.mail.Authenticator() {  
	      protected PasswordAuthentication getPasswordAuthentication() {  
	    return new PasswordAuthentication(user,password);  
	      }  
	    });  

	   /*
	   props.put("mail.smtp.auth", "false");  
	   Session session = Session.getDefaultInstance(props);  
	   */

	   //Compose the message  
	    try {  
	     MimeMessage message = new MimeMessage(session);  
	     message.setFrom(new InternetAddress(from));  
	     message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));  
	     message.setSubject(subject);  
	     message.setText(body);  
	       
	    //send the message  
	     Transport.send(message);  
	   
	     } catch (MessagingException e) {log.error("Error sending email "+ e.toString());}  
  };
}  
