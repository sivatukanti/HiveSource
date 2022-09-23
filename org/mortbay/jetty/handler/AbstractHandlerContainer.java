// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.handler;

import org.mortbay.util.LazyList;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.HandlerContainer;

public abstract class AbstractHandlerContainer extends AbstractHandler implements HandlerContainer
{
    public Handler[] getChildHandlers() {
        final Object list = this.expandChildren(null, null);
        return (Handler[])LazyList.toArray(list, Handler.class);
    }
    
    public Handler[] getChildHandlersByClass(final Class byclass) {
        final Object list = this.expandChildren(null, byclass);
        return (Handler[])LazyList.toArray(list, Handler.class);
    }
    
    public Handler getChildHandlerByClass(final Class byclass) {
        final Object list = this.expandChildren(null, byclass);
        if (list == null) {
            return null;
        }
        return (Handler)LazyList.get(list, 0);
    }
    
    protected Object expandChildren(final Object list, final Class byClass) {
        return list;
    }
    
    protected Object expandHandler(final Handler handler, Object list, final Class byClass) {
        if (handler == null) {
            return list;
        }
        if (handler != null && (byClass == null || byClass.isAssignableFrom(handler.getClass()))) {
            list = LazyList.add(list, handler);
        }
        if (handler instanceof AbstractHandlerContainer) {
            list = ((AbstractHandlerContainer)handler).expandChildren(list, byClass);
        }
        else if (handler instanceof HandlerContainer) {
            final HandlerContainer container = (HandlerContainer)handler;
            final Handler[] handlers = (byClass == null) ? container.getChildHandlers() : container.getChildHandlersByClass(byClass);
            list = LazyList.addArray(list, handlers);
        }
        return list;
    }
}
