package br.com.messagedispatcher.listener;

import br.com.messagedispatcher.annotation.EntityEventsPublish;
import br.com.messagedispatcher.publisher.MessagePublisher;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.persister.entity.EntityPersister;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageDispatcherEntityEventsListenerImplTest {

    @Mock
    private MessagePublisher publisher;

    @InjectMocks
    private MessageDispatcherEntityEventsListenerImpl listener;

    @Mock
    private PostInsertEvent postInsertEvent;

    @Mock
    private PostUpdateEvent postUpdateEvent;

    @Mock
    private EntityPersister entityPersister;

    @Test
    void onPostInsertShouldPublishWhenEntityHasAnnotationAndPublishCreateIsTrue() {
        TestEntityWithPublishCreate entity = new TestEntityWithPublishCreate();
        when(postInsertEvent.getEntity()).thenReturn(entity);

        listener.onPostInsert(postInsertEvent);

        verify(publisher, times(1)).sendEvent(entity);
    }

    @Test
    void onPostInsertShouldNotPublishWhenEntityHasAnnotationAndPublishCreateIsFalse() {
        TestEntityWithoutPublishCreate entity = new TestEntityWithoutPublishCreate();
        when(postInsertEvent.getEntity()).thenReturn(entity);

        listener.onPostInsert(postInsertEvent);

        verify(publisher, never()).sendEvent(any());
    }

    @Test
    void onPostInsertShouldNotPublishWhenEntityDoesNotHaveAnnotation() {
        TestEntityWithoutAnnotation entity = new TestEntityWithoutAnnotation();
        when(postInsertEvent.getEntity()).thenReturn(entity);

        listener.onPostInsert(postInsertEvent);

        verify(publisher, never()).sendEvent(any());
    }

    @Test
    void onPostUpdateShouldPublishWhenEntityHasAnnotationAndPublishUpdateIsTrue() {
        TestEntityWithPublishUpdate entity = new TestEntityWithPublishUpdate();
        when(postUpdateEvent.getEntity()).thenReturn(entity);

        listener.onPostUpdate(postUpdateEvent);

        verify(publisher, times(1)).sendEvent(entity);
    }

    @Test
    void onPostUpdateShouldNotPublishWhenEntityHasAnnotationAndPublishUpdateIsFalse() {
        TestEntityWithoutPublishUpdate entity = new TestEntityWithoutPublishUpdate();
        when(postUpdateEvent.getEntity()).thenReturn(entity);

        listener.onPostUpdate(postUpdateEvent);

        verify(publisher, never()).sendEvent(any());
    }

    @Test
    void onPostUpdateShouldNotPublishWhenEntityDoesNotHaveAnnotation() {
        TestEntityWithoutAnnotation entity = new TestEntityWithoutAnnotation();
        when(postUpdateEvent.getEntity()).thenReturn(entity);

        listener.onPostUpdate(postUpdateEvent);

        verify(publisher, never()).sendEvent(any());
    }

    @Test
    void requiresPostCommitHandlingShouldReturnTrue() {
        boolean result = listener.requiresPostCommitHandling(entityPersister);

        assertTrue(result);
    }

    @Test
    void onPostInsertCommitFailedShouldNotThrowException() {
        listener.onPostInsertCommitFailed(postInsertEvent);

        verifyNoInteractions(publisher);
    }

    @Test
    void onPostUpdateCommitFailedShouldNotThrowException() {
        listener.onPostUpdateCommitFailed(postUpdateEvent);

        verifyNoInteractions(publisher);
    }

    @EntityEventsPublish
    static class TestEntityWithPublishCreate {
    }

    @EntityEventsPublish(publishCreate = false)
    static class TestEntityWithoutPublishCreate {
    }

    @EntityEventsPublish
    static class TestEntityWithPublishUpdate {
    }

    @EntityEventsPublish(publishUpdate = false)
    static class TestEntityWithoutPublishUpdate {
    }

    static class TestEntityWithoutAnnotation {
    }
}