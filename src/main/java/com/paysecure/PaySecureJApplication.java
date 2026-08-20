package com.paysecure;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class PaySecureJApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaySecureJApplication.class, args);
    }

    /**
     * Evidence banner - required by assignment section 2.1.
     * Replace the tag below if your evidence tag ever changes.
     */
    @Component
    static class EvidenceBanner {
        private static final Logger log = LoggerFactory.getLogger(EvidenceBanner.class);

        @PostConstruct
        public void printBanner() {
            log.info("=========================================================");
            log.info(" PaySecure-J LAB BUILD  |  Evidence Tag: CJ-1134-C");
            log.info(" Student ID: mhm0231134  |  Variant: C");
            log.info(" WARNING: intentionally vulnerable - local/lab use only");
            log.info("=========================================================");
        }
    }
}
