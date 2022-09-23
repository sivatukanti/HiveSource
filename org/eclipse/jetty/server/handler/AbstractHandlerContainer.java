// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.server.Server;
import java.util.Collection;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HandlerContainer;

public abstract class AbstractHandlerContainer extends AbstractHandler implements HandlerContainer
{
    @Override
    public Handler[] getChildHandlers() {
        final List<Handler> list = new ArrayList<Handler>();
        this.expandChildren(list, null);
        return list.toArray(new Handler[list.size()]);
    }
    
    @Override
    public Handler[] getChildHandlersByClass(final Class<?> byclass) {
        final List<Handler> list = new ArrayList<Handler>();
        this.expandChildren(list, byclass);
        return list.toArray(new Handler[list.size()]);
    }
    
    @Override
    public <T extends Handler> T getChildHandlerByClass(final Class<T> byclass) {
        final List<Handler> list = new ArrayList<Handler>();
        this.expandChildren(list, byclass);
        if (list.isEmpty()) {
            return null;
        }
        return (T)list.get(0);
    }
    
    protected void expandChildren(final List<Handler> list, final Class<?> byClass) {
    }
    
    protected void expandHandler(final Handler handler, final List<Handler> list, final Class<?> byClass) {
        if (handler == null) {
            return;
        }
        if (byClass == null || byClass.isAssignableFrom(handler.getClass())) {
            list.add(handler);
        }
        if (handler instanceof AbstractHandlerContainer) {
            ((AbstractHandlerContainer)handler).expandChildren(list, byClass);
        }
        else if (handler instanceof HandlerContainer) {
            final HandlerContainer container = (HandlerContainer)handler;
            final Handler[] handlers = (byClass == null) ? container.getChildHandlers() : container.getChildHandlersByClass(byClass);
            list.addAll(Arrays.asList(handlers));
        }
    }
    
    public static <T extends HandlerContainer> T findContainerOf(final HandlerContainer root, final Class<T> type, final Handler handler) {
        if (root == null || handler == null) {
            return null;
        }
        final Handler[] branches = root.getChildHandlersByClass(type);
        if (branches != null) {
            for (final Handler h : branches) {
                final T container = (T)h;
                final Handler[] candidates = container.getChildHandlersByClass(handler.getClass());
                if (candidates != null) {
                    for (final Handler c : candidates) {
                        if (c == handler) {
                            return container;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public void setServer(final Server server) {
        if (server == this.getServer()) {
            return;
        }
        if (this.isStarted()) {
            throw new IllegalStateException("STARTED");
        }
        super.setServer(server);
        final Handler[] handlers = this.getHandlers();
        if (handlers != null) {
            for (final Handler h : handlers) {
                h.setServer(server);
            }
        }
    }
}
