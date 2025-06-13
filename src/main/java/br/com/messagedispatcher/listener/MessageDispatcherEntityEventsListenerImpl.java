package br.com.messagedispatcher.listener;

import br.com.messagedispatcher.annotation.EntityEventsPublish;
import br.com.messagedispatcher.publisher.MessagePublisher;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageDispatcherEntityEventsListenerImpl implements MessageDispatcherEntityEventsListener {

    private static final Logger log = LoggerFactory.getLogger(MessageDispatcherEntityEventsListenerImpl.class);
    private final MessagePublisher publisher;

    public MessageDispatcherEntityEventsListenerImpl(MessagePublisher publisher) {
        this.publisher = publisher;
        log.debug("Entity Listener iniciado.");
    }

    @Override
    public void onPostInsertCommitFailed(PostInsertEvent event) {
        log.debug("Commit falhou para insert de: {}", event);
    }

    @Override
    public void onPostUpdateCommitFailed(PostUpdateEvent event) {
        log.debug("Commit falhou para update de: {}", event);
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        if (shouldPublish(event.getEntity()) && event.getEntity().getClass().getAnnotation(EntityEventsPublish.class).publishCreate())
            publish(event.getEntity(), "criada");
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        if (shouldPublish(event.getEntity()) && event.getEntity().getClass().getAnnotation(EntityEventsPublish.class).publishUpdate())
            publish(event.getEntity(), "atualizada");
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return true;
    }

    private boolean shouldPublish(Object entity) {
        return entity.getClass().isAnnotationPresent(EntityEventsPublish.class);
    }

    private void publish(Object entity, String action) {
        publisher.sendEvent(entity);
        if (log.isDebugEnabled()) {
            try {
                log.debug("Evento publicado Entity: {} {} ", entity.getClass().getSimpleName(), action);
            } catch (Exception e) {
                log.error("Erro ao tentar converter objeto para json.", e);
            }
        }
    }
}
