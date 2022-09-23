// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.ArrayList;
import com.google.common.base.Joiner;
import java.util.Collection;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;

@InterfaceStability.Unstable
public class RefreshRegistry
{
    public static final Logger LOG;
    private final Multimap<String, RefreshHandler> handlerTable;
    
    public static RefreshRegistry defaultRegistry() {
        return RegistryHolder.registry;
    }
    
    public RefreshRegistry() {
        this.handlerTable = (Multimap<String, RefreshHandler>)HashMultimap.create();
    }
    
    public synchronized void register(final String identifier, final RefreshHandler handler) {
        if (identifier == null) {
            throw new NullPointerException("Identifier cannot be null");
        }
        this.handlerTable.put(identifier, handler);
    }
    
    public synchronized boolean unregister(final String identifier, final RefreshHandler handler) {
        return this.handlerTable.remove(identifier, handler);
    }
    
    public synchronized void unregisterAll(final String identifier) {
        this.handlerTable.removeAll(identifier);
    }
    
    public synchronized Collection<RefreshResponse> dispatch(final String identifier, final String[] args) {
        final Collection<RefreshHandler> handlers = this.handlerTable.get(identifier);
        if (handlers.size() == 0) {
            final String msg = "Identifier '" + identifier + "' does not exist in RefreshRegistry. Valid options are: " + Joiner.on(", ").join(this.handlerTable.keySet());
            throw new IllegalArgumentException(msg);
        }
        final ArrayList<RefreshResponse> responses = new ArrayList<RefreshResponse>(handlers.size());
        for (final RefreshHandler handler : handlers) {
            RefreshResponse response;
            try {
                response = handler.handleRefresh(identifier, args);
                if (response == null) {
                    throw new NullPointerException("Handler returned null.");
                }
                RefreshRegistry.LOG.info(this.handlerName(handler) + " responds to '" + identifier + "', says: '" + response.getMessage() + "', returns " + response.getReturnCode());
            }
            catch (Exception e) {
                response = new RefreshResponse(-1, e.getLocalizedMessage());
            }
            response.setSenderName(this.handlerName(handler));
            responses.add(response);
        }
        return responses;
    }
    
    private String handlerName(final RefreshHandler h) {
        return h.getClass().getName() + '@' + Integer.toHexString(h.hashCode());
    }
    
    static {
        LOG = LoggerFactory.getLogger(RefreshRegistry.class);
    }
    
    private static class RegistryHolder
    {
        public static RefreshRegistry registry;
        
        static {
            RegistryHolder.registry = new RefreshRegistry();
        }
    }
}
