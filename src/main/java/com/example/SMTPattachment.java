package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Hello world!
 *
 */
public class SMTPattachment 
{
    public static void main( String[] args )
    {
        Dotenv env = Dotenv.load();

        // create properties, use gmail smtp
        Properties p = new Properties();
        p.put("mail.smtp.host", env.get("HOST"));
        p.put("mail.smtp.port", env.get("PORT"));
        p.put("mail.smtp.user", env.get("USERNAME"));

        p.put("mail.smtp.auth", env.get("AUTH"));
        p.put("mail.smtp.starttls.enable", env.get("STARTTLS"));
        p.put("mail.smtp.debug", env.get("DEBUG"));

        p.put("mail.smtp.socketFactory.port", env.get("PORT"));
        p.put("mail.smtp.socketFactory.class", env.get("SOCKET_FACTORY"));
        p.put("mail.smtp.socketFactory.fallback", "false");

        System.out.println(p);

        List<String> recipients = new ArrayList<String>();
        recipients.add("culcalcul22@gmail.com");
        recipients.add("syamsul@alterra.id");

        // looping through recipients
        for (int i=0; i<recipients.size(); i++){
            try {
                // create session
                Session session = Session.getDefaultInstance(p, null);
                // session.setDebug(true);
                
                // mime message is message object, create it
                Message m = new MimeMessage(session);
    
                // set from, recipient, sent date, subject, text
                m.setFrom(new InternetAddress(env.get("FROM")));
                
                // set double recipient
                m.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients.get(i)));
    
                m.setSentDate(new java.util.Date());
                m.setSubject(env.get("SUBJECT"));
                
                // set multipart message
                Multipart multipart = new MimeMultipart();

                //  body in first part
                BodyPart body = new MimeBodyPart();
                body.setText("This is body");
                multipart.addBodyPart(body);

                // body in second part, overwrite old body
                body = new MimeBodyPart();
                DataSource source = new FileDataSource("./pom.xml"); // attachment file
                body.setDataHandler(new DataHandler(source));
                body.setFileName("attachment 1");
                multipart.addBodyPart(body);

                // create message from multipart
                m.setContent(multipart);

                // save
                m.saveChanges();
                
                // set transport using our session smtp, and connect it with our email
                Transport ts = session.getTransport("smtp");
                ts.connect(env.get("HOST"), env.get("USERNAME"), env.get("PASSWORD"));
    
                // send message to all recipient
                ts.sendMessage(m, m.getAllRecipients());
    
                // close transport
                ts.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        
    }
}
