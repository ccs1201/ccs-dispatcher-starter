package br.com.messagedispatcher.integrator.hibernate;

import br.com.messagedispatcher.listener.MessageDispatcherEntityEventsListener;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageDispatcherHibernateIntegrator implements Integrator {

    private static final Logger log = LoggerFactory.getLogger(MessageDispatcherHibernateIntegrator.class);
    private final MessageDispatcherEntityEventsListener listener;

    public MessageDispatcherHibernateIntegrator(MessageDispatcherEntityEventsListener listener) {
        this.listener = listener;
    }

    @Override
    public void integrate(Metadata metadata, BootstrapContext bootstrapContext,
                          SessionFactoryImplementor sessionFactory) {
        log.debug("Registrando MessageDispatcherEntityEventsListener via Integrator");

        var eventListenerRegistry =
                sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);

        eventListenerRegistry.getEventListenerGroup(EventType.POST_COMMIT_INSERT)
                .appendListener(listener);
        eventListenerRegistry.getEventListenerGroup(EventType.POST_COMMIT_UPDATE)
                .appendListener(listener);

        log.debug("MessageDispatcherEntityEventsListener registrado com sucesso");
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory,
                             SessionFactoryServiceRegistry serviceRegistry) {
    }
}
