// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.listen;

import java.util.Iterator;
import org.apache.curator.utils.ThreadUtils;
import com.google.common.base.Function;
import java.util.concurrent.Executor;
import org.apache.curator.shaded.com.google.common.util.concurrent.MoreExecutors;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.slf4j.Logger;

public class ListenerContainer<T> implements Listenable<T>
{
    private final Logger log;
    private final Map<T, ListenerEntry<T>> listeners;
    
    public ListenerContainer() {
        this.log = LoggerFactory.getLogger(this.getClass());
        this.listeners = (Map<T, ListenerEntry<T>>)Maps.newConcurrentMap();
    }
    
    @Override
    public void addListener(final T listener) {
        this.addListener(listener, MoreExecutors.sameThreadExecutor());
    }
    
    @Override
    public void addListener(final T listener, final Executor executor) {
        this.listeners.put(listener, new ListenerEntry<T>(listener, executor));
    }
    
    @Override
    public void removeListener(final T listener) {
        this.listeners.remove(listener);
    }
    
    public void clear() {
        this.listeners.clear();
    }
    
    public int size() {
        return this.listeners.size();
    }
    
    public void forEach(final Function<T, Void> function) {
        for (final ListenerEntry<T> entry : this.listeners.values()) {
            entry.executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        function.apply(entry.listener);
                    }
                    catch (Throwable e) {
                        ThreadUtils.checkInterrupted(e);
                        ListenerContainer.this.log.error(String.format("Listener (%s) threw an exception", entry.listener), e);
                    }
                }
            });
        }
    }
}
