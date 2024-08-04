package com.experiment.dsa1;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.Gmail.Users.Messages.Send;
import com.google.api.services.gmail.model.Message;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static com.experiment.dsa1.Dsa1Application.*;

@Service
public class GmailServiceAndBuild {

    private static MimeMessage createEmail(String toEmailAddress,
                                           String fromEmailAddress,
                                           String subject,
                                           String bodyText) throws MessagingException {

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(fromEmailAddress));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(toEmailAddress));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    private static Message createMessageWithEmail(MimeMessage emailContent) throws MessagingException, IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    private static Gmail GmailService() throws IOException, GeneralSecurityException {
        Gmail gmailService;
        long initial = (timeAtWhichAccessTokenGenerated.getTime() + accessTokenExpiration)*1000;
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken(accessToken, new Date(initial)));

        HttpRequestInitializer httpRequestInitializer = new HttpCredentialsAdapter(credentials);
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        gmailService = new Gmail.Builder(HTTP_TRANSPORT, new JacksonFactory(), httpRequestInitializer)
                .setApplicationName("project2898ad")
                .build();
        return gmailService;
    }

    public Send buildAndSendEmail(String summary, List<EventAttendee> attendees,
                                  String signedInUser,
                                  String eventUrl) throws MessagingException, IOException, GeneralSecurityException {
        String emailSubject = "Upcoming meeting";
        String emailBodyText = "";
        for(EventAttendee attendee: attendees){
            emailBodyText = "Hi, you have an upcoming meeting. \n your upcoming meeting is: "
                    + summary + " \n and the event url is: "+ eventUrl ;

            MimeMessage emailCreated = createEmail(attendee.getEmail(), signedInUser, emailSubject, emailBodyText);
            Message messageCreated = createMessageWithEmail(emailCreated);
            Gmail service = GmailService();

            try{
                Send message = service.users().messages().send("me", messageCreated);
                System.out.println( "message: " + message);
                System.out.println("------------------------------");
                System.out.println("beautified message: "+ message.toString());
                return message;
            }catch(GoogleJsonResponseException exception){
                GoogleJsonResponseException error = exception;
                if(error.getDetails().getCode() == 403){
                    System.out.println("Unable to send message: "+ error.getDetails());
                }else{
                    throw exception;
                }
            }
        }
    return null;
    }




}


