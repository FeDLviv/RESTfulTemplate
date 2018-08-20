package net.omisoft.rest.service.mail;

import org.thymeleaf.context.Context;

public interface MailService {

    void send(String mailTo, String title, String text);

    void sendWithAttachment(String mailTo, String title, String text, String pathToAttachment);

    void sendWithHtmlContent(String mailTo, String title, Context html, byte[] inlineData);

}
