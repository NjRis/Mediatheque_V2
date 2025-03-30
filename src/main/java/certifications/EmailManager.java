package certifications;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailManager {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String USERNAME = "service.mediatheque@gmail.com";
    private static final String PASSWORD = "votre_mot_de_passe";

    public static void envoyerAlerte(String message) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });

        try {
            Message email = new MimeMessage(session);
            email.setFrom(new InternetAddress(USERNAME));
            email.setRecipient(Message.RecipientType.TO,
                    new InternetAddress("jeanfrancois.brette@u-paris.fr"));
            email.setSubject("🚨 Alerte Médiathèque");
            email.setText("Document disponible : " + message);

            Transport.send(email);
        } catch (MessagingException e) {
            throw new RuntimeException("Erreur envoi email", e);
        }
    }
}
