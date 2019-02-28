package com.bracketboys.iac.services.frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FrontendService implements FrontendServiceInterface {
    private Logger logger = LoggerFactory.getLogger(FrontendService.class);
    private int count;

    @Override
    public void convertYoutubeLink(String link) {
        logger.info("Start converting {} - {}", count++, link);
    }
}
