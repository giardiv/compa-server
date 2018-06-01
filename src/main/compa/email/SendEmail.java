package compa.email;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.io.UnsupportedEncodingException;

import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

//TODO put sendEmail in package of services
public class SendEmail {

    private static final String login = "compa.map@gmail.com";
    private static final String password = "Compa-2605";

    public static void sendEmail(final String toEmail, final String subject, final String body, Handler<AsyncResult<Message>> resultHandler) {

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

        Vertx.vertx().executeBlocking( future -> {
            try {
                msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
                msg.addHeader("format", "flowed");
                msg.addHeader("Content-Transfer-Encoding", "8bit");

                final InternetAddress[] dest = InternetAddress.parse(toEmail, false);
                msg.setFrom(new InternetAddress("compa_service@compa.com", "Compa"));
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
            future.complete(msg);

        }, resultHandler);

    }

    public boolean isValide(String email){
        //Java Version
        int port = 587;
        String host = "smtp.gmail.com";
        String user = email;
        String pwd = "email password";
        Boolean valide = false;

        try {
            Properties props = new Properties();
            // required for gmail
            props.put("mail.smtp.starttls.enable","true");
            props.put("mail.smtp.auth", "true");
            // or use getDefaultInstance instance if desired...
            Session session = Session.getInstance(props, null);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, port, user, pwd);
            transport.close();
            System.out.println("success");
            valide = true;
        }
        catch(AuthenticationFailedException e) {
            System.out.println("AuthenticationFailedException - for authentication failures");
            e.printStackTrace();
        }
        catch(MessagingException e) {
            System.out.println("for other failures");
            e.printStackTrace();
        }
        return valide;
    }

}