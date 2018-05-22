package info.investdigital.service;

import com.sendgrid.*;
import info.investdigital.common.Const;
import info.investdigital.common.UResParam;
import info.investdigital.entity.MailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author ccl
 * @time 2018-03-15 18:21
 * @name SendGridService
 * @desc:
 */
@Slf4j
@Service
public class SendGridService {
    private final String MAIL_REQUEST_ENDPOINT= "mail/send";

    @Resource
    private UResParam uresParam;

    public void sendMail(String to, String subject, String content) throws IOException {
        sendMail(/*fromAddr*/uresParam.getFromAddr(),to,subject,content);
    }

    public void sendMail(String from,String to, String subject, String content) throws IOException {
        SendGrid sg = new SendGrid(/*sendGridApiKey*/uresParam.getSendGridApiKey());
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody("{\"personalizations\":[{\"to\":[{\"email\":\"" + to
                + "\"}],\"subject\":\"" + subject + "\"}],\"from\":{\"email\":\"" + from + "\"}," +
                "\"content\":[{\"type\":\"text/plain\",\"value\": \"" + content + "\"}]}");
        Response response = sg.api(request);
        log.info(response.getStatusCode() + "");
        log.info(response.getBody());
        log.info(response.getHeaders() + "");
    }

    public void sendHtmlMail(String toAddr, String subject, String context) throws IOException {
        sendHtmlMail(/*fromAddr*/uresParam.getFromAddr(),toAddr,subject,context);
    }

    public void sendHtmlMail(String fromAddr,String toAddr, String subject, String context) throws IOException {
//        Email from = new Email(fromAddr);
//        Email  to = new Email(toAddr);
//        Content content = new Content(MAIL_TYPE_HTML,context);
//        Mail mail = new Mail(from, subject, to, content);
//        sendMail(mail);

        sendMail(fromAddr,toAddr,subject,context, Const.MAIL_TYPE_HTML);
    }

    public void sendMail(MailVO vo) throws IOException {
        Email  from = new Email(vo.getFromAddress());
        from.setName(vo.getFromUser());

        Email  to = new Email(vo.getToAddress());
        to.setName(vo.getToUser());
        Content content = new Content(vo.getType(),vo.getContent());
        Mail mail = new Mail(from, vo.getSubject(), to, content);

        sendMail(mail);
        log.info("邮件已经发送到：",vo.getToAddress());
    }

    public void sendMail(String fromAddr,String toAddr, String subject, String context,String type) throws IOException {
        Email  from = new Email(fromAddr);
        Email  to = new Email(toAddr);
        Content content = new Content(type,context);
        Mail mail = new Mail(from, subject, to, content);
//        mail.personalization.get(0).addSubstitution("-name-", "Example User");
//        mail.personalization.get(0).addSubstitution("-city-", "Denver");
//        mail.setTemplateId("13b8f94f-bcae-4ec6-b752-70d6cb59f932");
        sendMail(mail);
    }

    private void sendMail(Mail mail) throws IOException{
        SendGrid sg = new SendGrid(/*sendGridApiKey*/uresParam.getSendGridApiKey());
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint(MAIL_REQUEST_ENDPOINT);
            request.setBody(mail.build());
            Response response = sg.api(request);
            log.info(""+response.getStatusCode());
            log.info(response.getBody());
            log.info(response.getHeaders().toString());
        } catch (IOException ex) {
            throw ex;
        }
    }
}
