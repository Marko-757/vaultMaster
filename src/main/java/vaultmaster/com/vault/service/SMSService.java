/*package vaultmaster.com.vault.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SMSService {

    private final String fromPhoneNumber;

    public SMSService(
            @Value("${twilio.account_sid}") String accountSid,
            @Value("${twilio.auth_token}") String authToken,
            @Value("${twilio.phone_number}") String fromPhoneNumber) {
        this.fromPhoneNumber = fromPhoneNumber;
        Twilio.init(accountSid, authToken);
    }

    public void sendSMS(String to, String messageBody) {
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(to),
                new com.twilio.type.PhoneNumber(fromPhoneNumber),
                messageBody
        ).create();

        System.out.println("Sent message with SID: " + message.getSid());
    }
}
*/