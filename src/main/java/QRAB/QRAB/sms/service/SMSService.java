/*package QRAB.QRAB.sms.service;

import QRAB.QRAB.sms.dto.SmsRequestDTO;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
*//*
@Service@Slf4j
@Transactional(readOnly = true)
public class SMSService {
    private static final String AWS_SNS_SMS_TYPE = "AWS.SNS.SMS.SMSType";
    private static final String AWS_SNS_SMS_TYPE_VALUE = "Transactional";
    private static final String AWS_SNS_DATA_TYPE = "String";
    private final AmazonSNS snsClient;

    @Autowired
    public SMSService(AmazonSNS snsClient) {
        this.snsClient = snsClient;
    }

    public void sendSms(String mobileNo, String message) {
        int requestTimeout = 3000;
        Map<String, MessageAttributeValue> smsAttributes = new HashMap<>();
        smsAttributes.put(AWS_SNS_SMS_TYPE, new MessageAttributeValue()
                .withStringValue(AWS_SNS_SMS_TYPE_VALUE)
                .withDataType(AWS_SNS_DATA_TYPE));

        PublishRequest publishRequest = new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(mobileNo)
                .withMessageAttributes(smsAttributes)
                .withSdkRequestTimeout(requestTimeout);

        // PublishResult를 사용하여 상태 확인
        PublishResult result = snsClient.publish(publishRequest);

        // 로그나 출력으로 PublishResult 결과 확인
        System.out.println("Message ID: " + result.getMessageId());  // 발송된 메시지 ID 확인
        System.out.println("Result: " + result.toString());  // 전체 결과 확인

        // log로 메시지 발송 결과 디버깅
        log.info("SNS SMS Publish Result: {}", result.toString());
    }

}
*/