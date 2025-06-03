package br.com.messagedispatcher.util;

import br.com.messagedispatcher.config.properties.MessageDispatcherProperties;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentUtils {

    private static String appName;

    public EnvironmentUtils(MessageDispatcherProperties properties) {
        appName = properties.getRoutingKey();
    }

    public static String getAppName() {
        return appName;
    }
}