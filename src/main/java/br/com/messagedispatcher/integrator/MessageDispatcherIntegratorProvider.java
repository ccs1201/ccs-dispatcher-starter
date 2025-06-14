package br.com.messagedispatcher.integrator;

import br.com.messagedispatcher.listener.MessageDispatcherEntityEventsListener;
import br.com.messagedispatcher.integrator.hibernate.MessageDispatcherHibernateIntegrator;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "message.dispatcher.entity-events.enabled", havingValue = "true")
public class MessageDispatcherIntegratorProvider implements IntegratorProvider {

    private final MessageDispatcherEntityEventsListener listener;

    public MessageDispatcherIntegratorProvider(MessageDispatcherEntityEventsListener listener) {
        this.listener = listener;
    }

    @Override
    public List<Integrator> getIntegrators() {
        return List.of(new MessageDispatcherHibernateIntegrator(listener));
    }
}
