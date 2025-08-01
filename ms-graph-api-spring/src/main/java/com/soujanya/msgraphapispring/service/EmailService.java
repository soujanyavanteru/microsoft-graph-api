package com.soujanya.msgraphapispring.service;

import com.microsoft.graph.models.*;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.graph.users.item.UserItemRequestBuilder;
import com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody;
import com.microsoft.graph.users.item.sendmail.SendMailRequestBuilder;
import com.soujanya.msgraphapispring.dto.MessageResponseDto;
import com.soujanya.msgraphapispring.model.EmailRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private GraphServiceClient mailClient;

    Logger log = LoggerFactory.getLogger(getClass());

    public void sendEmail(EmailRequest emailRequest) {

        SendMailPostRequestBody sendMailPostRequestBody = new SendMailPostRequestBody();
        Message message = new Message();
        message.setSubject(emailRequest.getSubject());

        ItemBody body = new ItemBody();
        body.setContentType(BodyType.Html);
        body.setContent(emailRequest.getMessage());
        message.setBody(body);

        LinkedList<Recipient> toRecipientsList = new LinkedList<>();
        Recipient toRecipients = new Recipient();
        EmailAddress emailAddress = new EmailAddress();
        emailAddress.setAddress(emailRequest.getRecipient());
        toRecipients.setEmailAddress(emailAddress);
        toRecipientsList.add(toRecipients);
        message.setToRecipients(toRecipientsList);

        sendMailPostRequestBody.setMessage(message);
        sendMailPostRequestBody.setSaveToSentItems(true);

        mailClient.users().byUserId(sender).sendMail().post(sendMailPostRequestBody);

    }

    public List<MessageResponseDto> listMessages() {
        MessageCollectionResponse messageCollectionResponse = mailClient.users().byUserId(sender).messages().get();
        if (messageCollectionResponse == null || messageCollectionResponse.getValue() == null)
            throw new RuntimeException("No messages found");
        return messageCollectionResponse.getValue().stream()
                .map(this::toMessageResponseDto)
                .toList();
    }

    private MessageResponseDto toMessageResponseDto(Message message) {

        String sender = message.getSender() != null && message.getSender().getEmailAddress() != null
                ? message.getSender().getEmailAddress().getAddress()
                : "";
        String subject = message.getSubject() != null ? message.getSubject() : "";
        String content = message.getBody() != null ? message.getBody().getContent() : "";


        return new MessageResponseDto(
                sender,
                subject,
                content
        );
    }


}
