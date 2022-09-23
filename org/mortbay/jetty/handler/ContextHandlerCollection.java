// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.handler;

import org.mortbay.log.Log;
import javax.servlet.ServletException;
import java.io.IOException;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.HttpConnection;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.mortbay.util.LazyList;
import java.util.HashMap;
import java.util.Map;
import org.mortbay.jetty.HandlerContainer;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.servlet.PathMap;

public class ContextHandlerCollection extends HandlerCollection
{
    private PathMap _contextMap;
    private Class _contextClass;
    
    public ContextHandlerCollection() {
        this._contextClass = ContextHandler.class;
    }
    
    public void mapContexts() {
        final PathMap contextMap = new PathMap();
        final Handler[] branches = this.getHandlers();
        for (int b = 0; branches != null && b < branches.length; ++b) {
            Handler[] handlers = null;
            if (branches[b] instanceof ContextHandler) {
                handlers = new Handler[] { branches[b] };
            }
            else {
                if (!(branches[b] instanceof HandlerContainer)) {
                    continue;
                }
                handlers = ((HandlerContainer)branches[b]).getChildHandlersByClass(ContextHandler.class);
            }
            for (int i = 0; i < handlers.length; ++i) {
                final ContextHandler handler = (ContextHandler)handlers[i];
                String contextPath = handler.getContextPath();
                if (contextPath == null || contextPath.indexOf(44) >= 0 || contextPath.startsWith("*")) {
                    throw new IllegalArgumentException("Illegal context spec:" + contextPath);
                }
                if (!contextPath.startsWith("/")) {
                    contextPath = '/' + contextPath;
                }
                if (contextPath.length() > 1) {
                    if (contextPath.endsWith("/")) {
                        contextPath += "*";
                    }
                    else if (!contextPath.endsWith("/*")) {
                        contextPath += "/*";
                    }
                }
                Object contexts = contextMap.get(contextPath);
                final String[] vhosts = handler.getVirtualHosts();
                if (vhosts != null && vhosts.length > 0) {
                    Map hosts;
                    if (contexts instanceof Map) {
                        hosts = (Map)contexts;
                    }
                    else {
                        hosts = new HashMap();
                        hosts.put("*", contexts);
                        contextMap.put(contextPath, hosts);
                    }
                    for (int j = 0; j < vhosts.length; ++j) {
                        final String vhost = vhosts[j];
                        contexts = hosts.get(vhost);
                        contexts = LazyList.add(contexts, branches[b]);
                        hosts.put(vhost, contexts);
                    }
                }
                else if (contexts instanceof Map) {
                    final Map hosts = (Map)contexts;
                    contexts = hosts.get("*");
                    contexts = LazyList.add(contexts, branches[b]);
                    hosts.put("*", contexts);
                }
                else {
                    contexts = LazyList.add(contexts, branches[b]);
                    contextMap.put(contextPath, contexts);
                }
            }
        }
        this._contextMap = contextMap;
    }
    
    public void setHandlers(final Handler[] handlers) {
        this._contextMap = null;
        super.setHandlers(handlers);
        if (this.isStarted()) {
            this.mapContexts();
        }
    }
    
    protected void doStart() throws Exception {
        this.mapContexts();
        super.doStart();
    }
    
    public void handle(final String target, final HttpServletRequest request, final HttpServletResponse response, final int dispatch) throws IOException, ServletException {
        final Handler[] handlers = this.getHandlers();
        if (handlers == null || handlers.length == 0) {
            return;
        }
        final Request base_request = HttpConnection.getCurrentConnection().getRequest();
        final PathMap map = this._contextMap;
        if (map != null && target != null && target.startsWith("/")) {
            final Object contexts = map.getLazyMatches(target);
            for (int i = 0; i < LazyList.size(contexts); ++i) {
                final Map.Entry entry = (Map.Entry)LazyList.get(contexts, i);
                Object list = entry.getValue();
                if (list instanceof Map) {
                    final Map hosts = (Map)list;
                    final String host = this.normalizeHostname(request.getServerName());
                    list = hosts.get(host);
                    for (int j = 0; j < LazyList.size(list); ++j) {
                        final Handler handler = (Handler)LazyList.get(list, j);
                        handler.handle(target, request, response, dispatch);
                        if (base_request.isHandled()) {
                            return;
                        }
                    }
                    list = hosts.get("*." + host.substring(host.indexOf(".") + 1));
                    for (int j = 0; j < LazyList.size(list); ++j) {
                        final Handler handler = (Handler)LazyList.get(list, j);
                        handler.handle(target, request, response, dispatch);
                        if (base_request.isHandled()) {
                            return;
                        }
                    }
                    list = hosts.get("*");
                    for (int j = 0; j < LazyList.size(list); ++j) {
                        final Handler handler = (Handler)LazyList.get(list, j);
                        handler.handle(target, request, response, dispatch);
                        if (base_request.isHandled()) {
                            return;
                        }
                    }
                }
                else {
                    for (int k = 0; k < LazyList.size(list); ++k) {
                        final Handler handler2 = (Handler)LazyList.get(list, k);
                        handler2.handle(target, request, response, dispatch);
                        if (base_request.isHandled()) {
                            return;
                        }
                    }
                }
            }
        }
        else {
            for (int l = 0; l < handlers.length; ++l) {
                handlers[l].handle(target, request, response, dispatch);
                if (base_request.isHandled()) {
                    return;
                }
            }
        }
    }
    
    public ContextHandler addContext(final String contextPath, final String resourceBase) {
        try {
            final ContextHandler context = this._contextClass.newInstance();
            context.setContextPath(contextPath);
            context.setResourceBase(resourceBase);
            this.addHandler(context);
            return context;
        }
        catch (Exception e) {
            Log.debug(e);
            throw new Error(e);
        }
    }
    
    public Class getContextClass() {
        return this._contextClass;
    }
    
    public void setContextClass(final Class contextClass) {
        if (contextClass == null || !ContextHandler.class.isAssignableFrom(contextClass)) {
            throw new IllegalArgumentException();
        }
        this._contextClass = contextClass;
    }
    
    private String normalizeHostname(final String host) {
        if (host == null) {
            return null;
        }
        if (host.endsWith(".")) {
            return host.substring(0, host.length() - 1);
        }
        return host;
    }
}
