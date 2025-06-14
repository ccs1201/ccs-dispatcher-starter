package br.com.messagedispatcher.util;

import br.com.messagedispatcher.config.properties.MessageDispatcherProperties;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class MessageDispatcherUtils {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(MessageDispatcherUtils.class);

    private static String appName;

    @PostConstruct
    public void init() {
        log.debug("MessageDispatcherUtils inicializado.");
    }

    public MessageDispatcherUtils(MessageDispatcherProperties properties) {
        appName = properties.getRoutingKey();
    }

    public static String getAppName() {
        return appName;
    }
}