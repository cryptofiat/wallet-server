package eu.cryptoeuro.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;
import java.util.Properties;  
import javax.mail.*;  
import javax.mail.internet.*;  
  
@Slf4j
@Component
public class EmailService {  

  static final String host="mail.smtp2go.com";  
  static final int port=2525;  
  static final String from="wallet-server@euro2.ee";
  @Value("${email.user}")
  static final String user="euro20";

  //TODO: this should be read from server config
  @Value("${email.password}")
  static final String password="abvcv12313";
    
  public void sendEmail(String to, String subject, String body) {
	  
	   log.info("logging to email w user: "+user+" pass: "+password);
	   //Get the session object  
	   Properties props = new Properties();  
	   props.put("mail.smtp.host",host);  
	   props.put("mail.smtp.port",String.valueOf(port));  
	   props.put("mail.smtp.auth", "true");  
	     
	   Session session = Session.getDefaultInstance(props,  
	    new javax.mail.Authenticator() {  
	      protected PasswordAuthentication getPasswordAuthentication() {  
	    return new PasswordAuthentication(user,password);  
	      }  
	    });  
	  
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
