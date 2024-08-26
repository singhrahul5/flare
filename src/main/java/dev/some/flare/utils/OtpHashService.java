package dev.some.flare.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class OtpHashService {

    private final Mac mac;

    @Autowired
    public OtpHashService(@Value("${app.security.jwt-secret-key}") String secretKey) throws Exception {
        byte[] key = Base64.getDecoder().decode(secretKey);
        SecretKeySpec keySpec = new SecretKeySpec(key, "HmacSHA256");
        mac = Mac.getInstance("HmacSHA256");
        mac.init(keySpec);
    }

    public String hashOtp(String otp) {
        byte[] rawHmac = mac.doFinal(otp.getBytes());
        return Base64.getEncoder().encodeToString(rawHmac);
    }
}

