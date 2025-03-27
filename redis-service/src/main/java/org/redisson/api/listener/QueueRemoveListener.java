package org.redisson.api.listener;

import org.redisson.api.ObjectListener;

public interface QueueRemoveListener extends ObjectListener {
    void onQueueRemove(String message);
}
