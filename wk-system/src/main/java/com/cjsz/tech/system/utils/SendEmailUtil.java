package com.cjsz.tech.system.utils;

import org.springframework.context.annotation.Configuration;

import com.cjsz.tech.system.domain.MailSetting;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

/**
 * 发送邮件
 * Created by yunke on 16/2/29.
 */
@Configuration
public class SendEmailUtil {

    /**
     * 获取Session
     *
     * @return
     */
    private static Session getSession(MailSetting setting) {
        Properties props = new Properties();
        // 配置文件对象
        // 邮箱服务地址
        props.setProperty("mail.smtp.host ", setting.getSmtp_url());
        // 是否进行验证
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        // 创建一个会话
        Session session = Session.getInstance(props);
        // 打开调试，会打印与邮箱服务器回话的内容
        //session.setDebug(true);
        return session;
    }
       /* props.put("mail.smtp.host", setting.getSmtp_url());//设置服务器地址
        props.put("mail.store.protocol", "smtp");//设置协议
        props.put("mail.smtp.port", setting.getPort());//设置端口
        props.put("mail.smtp.auth", "true");
        props.setProperty("mail.transport.protocol", "smtp");
        final String account = setting.getAccount();

        final String pwd = setting.getPwd();

        Authenticator authenticator = new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account, pwd);
            }

        };
        Session session = Session.getInstance(props, authenticator);

        return session;*/


    public static boolean send(MailSetting setting, String email, String theme, String content) {
        Session session = getSession(setting);
        try {
            Message message = new MimeMessage(session);
            // 如果发送人没有写对，那么会出现 javamail 550 Invalid User
            // 如果发送人写的和使用的帐号不一致，则会出现 553 Mail from must equal authorized user
            InternetAddress from = new InternetAddress(setting.getAccount(), setting.getAccount_name());
            message.setFrom(from);
            InternetAddress to = new InternetAddress(email);
            message.setRecipient(Message.RecipientType.TO, to);
            message.setSubject(MimeUtility.encodeText(theme));
            message.setContent(content, "text/html;charset=utf-8");
            message.setSentDate(new Date());
            Transport transport = session.getTransport("smtp");
            // 具体你使用邮箱的smtp地址和端口，应该到邮箱里面查看，如果使用了SSL，网易的端口应该是 465/994
            transport.connect(setting.getSmtp_url(), Integer.valueOf(setting.getPort()), setting.getAccount(), setting.getPwd());
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            // Instantiate a message  
           /* Message msg = new MimeMessage(session);
            //Set message attributes  
            msg.setFrom(new InternetAddress(setting.getAccount(), setting.getAccount_name()));

            InternetAddress[] address = {new InternetAddress(email, setting.getAccount_name())};
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(theme);
            msg.setSentDate(new Date());
            msg.setContent(content, "text/html;charset=utf-8");
            //Send the message  
            Transport.send(msg);*/
            return true;
        } catch (Exception mex) {
            mex.printStackTrace();
            return false;
        }
    }
}

