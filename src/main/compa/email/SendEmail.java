package compa.email;

import com.sun.mail.util.MailSSLSocketFactory;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmail {

    private static final String login = "compa.map@gmail.com";
    private static final String password = "Compa-2605";

    public static void sendEmail(final String toEmail, final String subject, final String body) {

        final Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.sendpartial", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.trust", "*");
        //props.put("mail.debug", "true");

        final MimeMessage msg = new MimeMessage(Session.getInstance(props));
        try {
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            final InternetAddress[] dest = InternetAddress.parse(toEmail, false);
            msg.setFrom(new InternetAddress("no_reply@test.com", "no_reply"));
            msg.setReplyTo(dest);
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, dest);

            System.out.println("Message is ready");
            Transport.send(msg, login, password);

            System.out.println("EMail Sent Successfully!!");
        } catch (final MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}