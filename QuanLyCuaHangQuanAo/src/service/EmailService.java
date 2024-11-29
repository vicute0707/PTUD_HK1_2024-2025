package service;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {
    // Thông tin cấu hình email gửi
    private static final String HOST_NAME = "smtp.gmail.com";
    private static final String TLS_PORT = "587"; 
    private static final String FROM_EMAIL = "vi0978294041@gmail.com";  // Email của shop
    private static final String APP_PASSWORD = "pnkv bpxh ukyv dbsu\r\n"
    		+ ""; // App Password từ Google
    private static final String SHOP_NAME = "Fashion Shop PTITHCM";
    
    // Thiết lập cấu hình cho Gmail SMTP
    private static Properties getMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", HOST_NAME);  
        props.put("mail.smtp.port", TLS_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        return props;
    }
    
    // Tạo phiên làm việc email
    private static Session createMailSession() {
        return Session.getInstance(getMailProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });
    }
    
    // Gửi email đặt lại mật khẩu
    public static boolean sendPasswordResetEmail(String toEmail, String username, String newPassword) {
        try {
            // Tạo message
            Message message = new MimeMessage(createMailSession());
            
            // Thiết lập thông tin người gửi
            message.setFrom(new InternetAddress(FROM_EMAIL, SHOP_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Đặt lại mật khẩu - " + SHOP_NAME);
            
            // Tạo nội dung email
            String htmlContent = 
                "<div style='font-family: Arial, sans-serif; padding: 20px; max-width: 600px; margin: 0 auto;'>" +
                    "<div style='background: #DB2777; padding: 10px; text-align: center;'>" +
                        "<h1 style='color: white; margin: 0;'>" + SHOP_NAME + "</h1>" +
                    "</div>" +
                    "<div style='background: #fff; padding: 20px; border: 1px solid #ddd;'>" +
                        "<h2 style='color: #DB2777; margin-top: 0;'>Đặt lại mật khẩu</h2>" +
                        "<p>Xin chào <b>" + username + "</b>,</p>" +
                        "<p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.</p>" +
                        "<div style='background: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;'>" +
                            "<p style='margin: 0;'>Mật khẩu mới của bạn là: " +
                            "<b style='color: #DB2777; font-size: 18px;'>" + newPassword + "</b></p>" +
                        "</div>" +
                        "<p><b style='color: #DC2626;'>Lưu ý:</b> Vui lòng đổi mật khẩu ngay sau khi đăng nhập để đảm bảo an toàn.</p>" +
                        "<hr style='border: none; border-top: 1px solid #eee; margin: 20px 0;'>" +
                        "<p style='color: #666; font-size: 12px;'>Email này được gửi tự động từ hệ thống. " +
                        "Vui lòng không trả lời email này.</p>" +
                    "</div>" +
                    "<div style='text-align: center; padding: 20px; color: #666;'>" +
                        "<p style='margin: 0;'>&copy; 2024 " + SHOP_NAME + ". All rights reserved.</p>" +
                    "</div>" +
                "</div>";

            // Thiết lập nội dung
            message.setContent(htmlContent, "text/html; charset=UTF-8");
            
            // Gửi email
            Transport.send(message);
            
            System.out.println("Đã gửi email thành công đến: " + toEmail);
            return true;
            
        } catch (AuthenticationFailedException e) {
            System.err.println("Lỗi xác thực email: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            System.err.println("Lỗi gửi email: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi không xác định: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Gửi email thông báo đăng nhập
    public static boolean sendLoginNotificationEmail(String toEmail, String username, String loginTime, String ipAddress) {
        try {
            Message message = new MimeMessage(createMailSession());
            message.setFrom(new InternetAddress(FROM_EMAIL, SHOP_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Thông báo đăng nhập - " + SHOP_NAME);
            
            String htmlContent = 
                "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +
                    "<h2 style='color: #DB2777;'>Thông báo đăng nhập</h2>" +
                    "<p>Xin chào <b>" + username + "</b>,</p>" +
                    "<p>Tài khoản của bạn vừa được đăng nhập:</p>" +
                    "<ul>" +
                        "<li>Thời gian: " + loginTime + "</li>" +
                        "<li>Địa chỉ IP: " + ipAddress + "</li>" +
                    "</ul>" +
                    "<p>Nếu không phải bạn, vui lòng đổi mật khẩu ngay!</p>" +
                "</div>";
            
            message.setContent(htmlContent, "text/html; charset=UTF-8");
            Transport.send(message);
            return true;
            
        } catch (Exception e) {
            System.err.println("Lỗi gửi email thông báo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    

}