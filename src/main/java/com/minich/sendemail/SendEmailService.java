package com.minich.sendemail;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class SendEmailService {

    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd.MM.yyyy");
    public static final String SUBJECT = "Уведомление о просроченной задолженности";
    public static final String BODY_TEMPLATE = ""
            + "Уважаемый клиент.<br>"
            + "Письмо отправлено автоматически. Благодарим, если уже произвели оплату.<br><br>"
            + "На %s за Вами имеется просроченная задолженность по договору №%s от %sг. на сумму <b>%s BYN</b>. Просим погасить задолженность.<br>"
            + "По вопросам задолженности просим обращаться к Вашему менеджеру.<br><br>"
            + "С уважением, ООО «ПРИМЕР». Телефон для справок: X-XXX-XXX-XX-X, XXX-XX-XXX-XX-XX.";
    public static final String EMAIL_FROM = "example@email.com";
    public static final String APP_PASSWORD = "<app-password>";
    public static final String EMAIL_FROM_TITLE = "OOO «ПРИМЕР»";

    public static void sendEmail(String toEmail, String agreementNumber, String dateFrom, String amount) throws MessagingException, UnsupportedEncodingException {
        Date date = new Date(System.currentTimeMillis());
        String dateStr = FORMATTER.format(date);
        String sendMessage = String.format(BODY_TEMPLATE, dateStr, agreementNumber, dateFrom, amount);
        send(toEmail, sendMessage);
    }

    private  static void send(String toEmail, String message) throws MessagingException, UnsupportedEncodingException {
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, APP_PASSWORD);
            }
        };
        Session session = Session.getInstance(getSMTPProperties(), auth);
        MimeMessage emailMessage = new MimeMessage(session);

        InternetAddress fromAddress = new InternetAddress(EMAIL_FROM);
        fromAddress.setPersonal(EMAIL_FROM_TITLE);
        emailMessage.setFrom(fromAddress);

        InternetAddress[] toAddresses = { new InternetAddress(toEmail) };
        emailMessage.setRecipients(Message.RecipientType.TO, toAddresses);

        emailMessage.setSubject(SUBJECT);

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(message, "text/html; charset=utf-8");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        emailMessage.setContent(multipart);
        Transport.send(emailMessage);
    }

    private static Properties getSMTPProperties() {
        Properties defaultProps = new Properties();
        defaultProps.setProperty("mail.smtp.host", "smtp.yandex.ru");
        defaultProps.setProperty("mail.smtp.port", "465");
        defaultProps.setProperty("mail.user", EMAIL_FROM);
        defaultProps.setProperty("mail.password", APP_PASSWORD);
        defaultProps.setProperty("mail.transport.protocol", "smtp");
        defaultProps.setProperty("mail.smtp.auth", "true");
        defaultProps.setProperty("mail.smtp.starttls.enable", "true");
        defaultProps.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        defaultProps.setProperty("mail.debug", "true");
        return defaultProps;
    }
}
