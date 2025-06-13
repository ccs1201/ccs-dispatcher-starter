package br.com.messagedispatcher.entity.listener;

import br.com.messagedispatcher.annotation.EntityEventPublishes;
import br.com.messagedispatcher.publisher.MessagePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageDispatcherEntityListenerTest {

    @Mock
    private MessagePublisher publisher;

    private MessageDispatcherEntityListener listener;

    @BeforeEach
    void setUp() {
        listener = new MessageDispatcherEntityListener(publisher);
    }

    @Test
    void onPersist_shouldPublishWhenAnnotationPresentAndPublishCreateIsTrue() {
        // Arrange
        TestEntity entity = new TestEntity();
        
        // Act
        listener.onPersist(entity);
        
        // Assert
        verify(publisher, times(1)).sendEvent(entity);
    }

    @Test
    void onPersist_shouldNotPublishWhenAnnotationNotPresent() {
        // Arrange
        NonAnnotatedEntity entity = new NonAnnotatedEntity();
        
        // Act
        listener.onPersist(entity);
        
        // Assert
        verify(publisher, never()).sendEvent(any());
    }

    @Test
    void onPersist_shouldNotPublishWhenPublishCreateIsFalse() {
        // Arrange
        NoCreateEntity entity = new NoCreateEntity();
        
        // Act
        listener.onPersist(entity);
        
        // Assert
        verify(publisher, never()).sendEvent(any());
    }

    @Test
    void onRemove_shouldPublishWhenAnnotationPresentAndPublishDeleteIsTrue() {
        // Arrange
        TestEntity entity = new TestEntity();
        
        // Act
        listener.onRemove(entity);
        
        // Assert
        verify(publisher, times(1)).sendEvent(entity);
    }

    @Test
    void onUpdate_shouldPublishWhenAnnotationPresentAndPublishUpdateIsTrue() {
        // Arrange
        TestEntity entity = new TestEntity();
        
        // Act
        listener.onUpdate(entity);
        
        // Assert
        verify(publisher, times(1)).sendEvent(entity);
    }

    @EntityEventPublishes
    static class TestEntity {
    }

    static class NonAnnotatedEntity {
    }

    @EntityEventPublishes(publishCreate = false)
    static class NoCreateEntity {
    }
}