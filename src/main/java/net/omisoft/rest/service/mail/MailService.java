package net.omisoft.rest.service.mail;

public interface MailService {

    void send(String mailTo, String title, String text);

    void sendWithAttachment(String mailTo, String title, String text, String pathToAttachment);

}
