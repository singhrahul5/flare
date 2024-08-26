package dev.some.flare.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class RandomIdGeneratorService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int ID_LENGTH = 11;
    private static final int OTP_LENGTH = 6;
    private final Random random = new SecureRandom();

    private final Logger logger = LoggerFactory.getLogger(getClass());
//    @Transactional
//    public String generateUniqueRandomId() {
//        String randomId;
//        int retries = 0;
//        do {
//            randomId = generateRandomId();
//            try {
//                if (!videoRepository.existsById(randomId)) {
//                    Video video = new Video();
//                    video.setId(randomId);
//                    videoRepository.save(video);
//                    return randomId;
//                }
//            } catch (OptimisticLockingFailureException e) {
//                retries++;
//                if (retries >= 3) { // retry limit
//                    throw e;
//                }
//            }
//        } while (true);
//    }

    public String generateRandomId() {
        StringBuilder sb = new StringBuilder(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        logger.debug("Generated randon id {}", sb);
        return sb.toString();
    }

    public String generateOtp() {
        StringBuilder sb = new StringBuilder(OTP_LENGTH);
        sb.append(random.nextInt(1, 10));
        for (int i = 1; i < OTP_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        logger.debug("Generated randon OTP {}", sb);
        return sb.toString();
    }
}

