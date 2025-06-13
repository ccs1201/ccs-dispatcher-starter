package br.com.messagedispatcher.config.properties;

import br.com.messagedispatcher.config.MessageDispatcherAutoConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@AutoConfiguration
@AutoConfigureBefore(MessageDispatcherAutoConfig.class)
@ConfigurationProperties(prefix = "message.dispatcher.logging")
@Validated
public class MessageDispatcherLoggingProperties {

    private MessageRouterLoggingProperties messageRouter;

    public MessageRouterLoggingProperties getMessageRouter() {
        return messageRouter;
    }

    public void setMessageRouter(MessageRouterLoggingProperties messageRouter) {
        this.messageRouter = messageRouter;
    }

    public static class MessageRouterLoggingProperties {
        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }
}
