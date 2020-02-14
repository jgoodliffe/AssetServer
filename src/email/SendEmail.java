package email;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Sends an email to a specified address
 */
public class SendEmail {
    Properties properties = System.getProperties();
    String host = "localhost";
    String from = "do-not-reply@productionmanager.cc";

    public SendEmail() {
        //this.host=host;
        properties.setProperty("mail.smtp.host", host);
    }

    public boolean newEmail(String to, String content, String subject) {
        try {
            Session session = Session.getDefaultInstance(properties);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(from);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(content);
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
