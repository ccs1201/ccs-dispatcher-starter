package br.com.messagedispatcher.listener.entity;

import org.hibernate.event.spi.PostCommitInsertEventListener;
import org.hibernate.event.spi.PostCommitUpdateEventListener;

public interface MessageDispatcherEntityEventsListener extends PostCommitInsertEventListener, PostCommitUpdateEventListener {
}
