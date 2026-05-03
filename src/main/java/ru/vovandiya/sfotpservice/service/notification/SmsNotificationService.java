package ru.vovandiya.sfotpservice.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Service("smsNotification")
@Slf4j
public class SmsNotificationService implements NotificationService {

    private final String host;
    private final int port;
    private final String systemId;
    private final String password;
    private final String systemType;
    private final String sourceAddress;

    public SmsNotificationService() {
        var config = loadConfig();
        host = config.getProperty("smpp.host");
        port = Integer.parseInt(config.getProperty("smpp.port"));
        systemId = config.getProperty("smpp.system_id");
        password = config.getProperty("smpp.password");
        systemType = config.getProperty("smpp.system_type");
        sourceAddress = config.getProperty("smpp.source_addr");
    }

    @Override
    public void send(String destination, String code) {
        var session = new SMPPSession();
        try {
            var bindParameter = new BindParameter(
                BindType.BIND_TX, systemId, password, systemType,
                TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, sourceAddress);

            session.connectAndBind(host, port, bindParameter);
            session.submitShortMessage(
                systemType,
                TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, sourceAddress,
                TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, destination,
                new ESMClass(), (byte) 0, (byte) 1, null, null,
                new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT), (byte) 0,
                new GeneralDataCoding(Alphabet.ALPHA_DEFAULT), (byte) 0,
                ("Your code: " + code).getBytes(StandardCharsets.UTF_8));
            log.info("OTP sent via SMS to {}", destination);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}", destination, e);
            throw new RuntimeException("Failed to send SMS", e);
        } finally {
            session.unbindAndClose();
        }
    }

    private Properties loadConfig() {
        try {
            var props = new Properties();
            props.load(SmsNotificationService.class
                .getClassLoader().getResourceAsStream("sms.properties"));
            return props;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load SMS configuration", e);
        }
    }
}