package net.omisoft.rest.service.mail;

import lombok.AllArgsConstructor;
import net.omisoft.rest.configuration.MessageSourceConfiguration;
import net.omisoft.rest.exception.BadRequestException;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
@AllArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final Environment environment;
    private final MessageSourceConfiguration message;

    @Override
    public void send(String mailTo, String title, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(environment.getProperty("spring.mail.username"));
        mailMessage.setTo(mailTo);
        mailMessage.setSubject(title);
        mailMessage.setText(text);
        mailSender.send(mailMessage);
    }

    @Override
    public void sendWithAttachment(String mailTo, String title, String text, String pathToAttachment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(environment.getProperty("spring.mail.username"));
            helper.setTo(mailTo);
            helper.setSubject(title);
            helper.setText(text);
            FileSystemResource file
                    = new FileSystemResource(new File(pathToAttachment));
            //TODO change file name
            helper.addAttachment("test.png", file);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new BadRequestException(message.getMessage("exception.email.send"));
        }
    }

    @Override
    public void sendWithHtmlContent(String mailTo, String title, Context html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(environment.getProperty("spring.mail.username"));
            helper.setTo(mailTo);
            helper.setSubject(title);
            helper.setText(templateEngine.process("mailTemplate", html), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new BadRequestException(message.getMessage("exception.email.send"));
        }
    }

}
