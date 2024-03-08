package club.asyncraft.asyncauth.common.util;

import club.asyncraft.asyncauth.AsyncAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtils {

    private static Logger logger;

    public static Logger getLogger() {
        if (logger == null) {
            logger = LoggerFactory.getLogger(AsyncAuth.class);
        }
        return logger;
    }

}
