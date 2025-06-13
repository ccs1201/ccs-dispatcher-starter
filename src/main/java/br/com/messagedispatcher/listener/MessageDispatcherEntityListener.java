package br.com.messagedispatcher.listener;

import br.com.messagedispatcher.annotation.EntityEventPublishes;
import br.com.messagedispatcher.publisher.MessagePublisher;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnClass(name = "org.springframework.data.jpa.repository.JpaRepository")
public class MessageDispatcherEntityListener {

    private static final Logger log = LoggerFactory.getLogger(MessageDispatcherEntityListener.class);
    private final MessagePublisher publisher;

    public MessageDispatcherEntityListener(MessagePublisher publisher) {
        this.publisher = publisher;
    }

    @PostPersist
    public void onPersist(Object entity) {
        if (shouldPublish(entity) && entity.getClass().getAnnotation(EntityEventPublishes.class).publishCreate())
            publish(entity, "criada");
    }

    @PostRemove
    public void onRemove(Object entity) {
        if (shouldPublish(entity) && entity.getClass().getAnnotation(EntityEventPublishes.class).publishDelete())
            publish(entity, "removida");
    }

    @PostUpdate
    public void onUpdate(Object entity) {
        if (shouldPublish(entity) && entity.getClass().getAnnotation(EntityEventPublishes.class).publishUpdate())
            publish(entity, "atualizada");
    }

    private void publish(Object entity, String action) {
        publisher.sendEvent(entity);
        if (log.isDebugEnabled()) {
            log.debug("Evento publicado Entity {} {} Dados: {}.", action, entity.getClass().getSimpleName(), entity);

        }
    }

    private boolean shouldPublish(Object entity) {
        return entity.getClass().isAnnotationPresent(EntityEventPublishes.class);
    }
}
