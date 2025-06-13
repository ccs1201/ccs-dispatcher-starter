package br.com.messagedispatcher.listener;

import org.hibernate.event.spi.PostCommitInsertEventListener;
import org.hibernate.event.spi.PostCommitUpdateEventListener;

public interface MessageDispatcherEntityEventsListener extends PostCommitInsertEventListener, PostCommitUpdateEventListener {
}
