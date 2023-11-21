package com.astrosea.richer.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;

@Component
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);


    //不影响运行
    //MailProperties 的password必须是授权码
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public int sendMail(String to, String subject, String content) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content,true);
            mailSender.send(helper.getMimeMessage());
            return port;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int sendMail(List<String> toList, String subject, String content) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            int port = serverSocket.getLocalPort();
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(toList.toArray(new String[0]));
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(helper.getMimeMessage());
            return port;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建邮件 html
     * @param to
     * @param mainContent
     * @param remark
     * @return
     */
    public String getHtmlContent(String to, String mainContent, String remark) {
        String userEmail = to;

        StringBuilder content = new StringBuilder();
        content.append("<!DOCTYPE html>\n");
        content.append("<html lang=\"en\">\n");
        content.append("<head>\n");
        content.append("    <meta charset=\"UTF-8\">\n");
        content.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        content.append("    <title>Astro</title>\n");
        content.append("    <style>\n");
        content.append("        body {\n");
        content.append("            margin: 0;\n");
        content.append("            padding: 0;\n");
        content.append("        }\n");
        content.append("\n");
        content.append("        .container {\n");
        content.append("            max-width: 1200px;\n");
        content.append("            width: 100%;\n");
        content.append("            margin: 0 auto;\n");
        content.append("            height: 80vh;\n");
        content.append("            max-height: 1200px;\n");
        content.append("        }\n");
        content.append("\n");
        content.append("        .content {\n");
        content.append("            margin: 10px 5%;\n");
        content.append("        }\n");
        content.append("\n");
        content.append("        img {\n");
        content.append("            width: 100%;\n");
        content.append("        }\n");
        content.append("\n");
        content.append("        h3 {\n");
        content.append("            margin-bottom: 50px;\n");
        content.append("        }\n");
        content.append("\n");
        content.append("        .bottom-img {\n");
        content.append("            position: absolute;\n");
        content.append("            bottom: 0;\n");
        content.append("            max-width: 1200px;\n");
        content.append("            margin-top: 40px; \n");
        content.append("        }\n");
        content.append("\n");
        content.append("        .eamil-code {\n");
        content.append("            font-weight: 700;\n");
        content.append("        }\n");
        content.append("\n");
        content.append("        @media (min-width: 1200px) {\n");
        content.append("            .container {\n");
        content.append("                max-width: 800px;\n");
        content.append("            }\n");
        content.append("\n");
        content.append("            .bottom-img {\n");
        content.append("                max-width: 800px;\n");
        content.append("            }\n");
        content.append("        }\n");
        content.append("    </style>\n");
        content.append("</head>\n");
        content.append("<body>\n");
        content.append("    <div class=\"container\">\n");
        content.append("        <img src=\"https://pic.astrosea.io/astro-email/email-page-top.png\" alt=\"\">\n");
        content.append("        <div class=\"content\">\n");
        content.append("            <h3>尊敬的用户：" + userEmail + "</h3>\n");  // 在这里插入变量 email
        content.append("            <p>" + remark.replaceAll("\n", "<br>") + "</p>\n");// 邮件内容备注 remark
        content.append("            <div>\n");
        content.append("                <pre><span class=\"email-code\">" + mainContent.replaceAll("\n", "<br>") + "</span></pre>\n");
        content.append("            </div>\n");
        content.append("        </div>\n");
        content.append("        <img class=\"bottom-img\" src=\"https://pic.astrosea.io/astro-email/email-page-bottom.png\" alt=\"\">\n");
        content.append("    </div>\n");
        content.append("</body>\n");
        content.append("</html>");
        return content.toString();
    }






}
