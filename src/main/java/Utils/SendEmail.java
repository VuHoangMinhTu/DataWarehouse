package Utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SendEmail {
    static final String from = "21130430@st.hcmuaf.edu.vn";
    static final String password = "mpvr jxut gcjh qqhc";


    public String getCurrentDateTime() {
        ZonedDateTime currentDateTime = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z z");
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }


    public void sendMail(String addressTo, String error) {
        // khai báo
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Tao auth
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // TODO Auto-generated method stub
                return new PasswordAuthentication(from, password);
            }

        };
        // phiên làm việc
        Session session = Session.getInstance(props, auth);
        // Gửi Email

        // Tạo một tin nhắn
        MimeMessage msg = new MimeMessage(session);
        try {
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.setFrom(new InternetAddress(from, "Kyma Store"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(addressTo, false));
            msg.setSubject("You have an error occurring at " + getCurrentDateTime() + " with the following content");
            msg.setSentDate(new Date());
            // Nội dung
            msg.setContent("<p><strong>" + error + "</strong></p>", "text/html; charset=UTF-8");
            // Gửi mail
            Transport.send(msg);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        SendEmail sendEmail = new SendEmail();
//        sendEmail.sendMail("tranlocmom@gmail.com", "hahaha");
    }

}