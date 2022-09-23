// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.util.log.Log;
import javax.servlet.ServletException;
import java.io.IOException;
import org.eclipse.jetty.server.HttpChannelState;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import java.util.Iterator;
import java.util.Arrays;
import org.eclipse.jetty.util.ArrayUtil;
import java.util.HashMap;
import org.eclipse.jetty.util.ArrayTernaryTrie;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.eclipse.jetty.util.Trie;
import org.eclipse.jetty.server.Handler;
import java.util.concurrent.ConcurrentMap;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.annotation.ManagedObject;

@ManagedObject("Context Handler Collection")
public class ContextHandlerCollection extends HandlerCollection
{
    private static final Logger LOG;
    private final ConcurrentMap<ContextHandler, Handler> _contextBranches;
    private volatile Trie<Map.Entry<String, Branch[]>> _pathBranches;
    private Class<? extends ContextHandler> _contextClass;
    
    public ContextHandlerCollection() {
        super(true);
        this._contextBranches = new ConcurrentHashMap<ContextHandler, Handler>();
        this._contextClass = ContextHandler.class;
    }
    
    @ManagedOperation("update the mapping of context path to context")
    public void mapContexts() {
        this._contextBranches.clear();
        if (this.getHandlers() == null) {
            this._pathBranches = new ArrayTernaryTrie<Map.Entry<String, Branch[]>>(false, 16);
            return;
        }
        final Map<String, Branch[]> map = new HashMap<String, Branch[]>();
        for (final Handler handler : this.getHandlers()) {
            final Branch branch = new Branch(handler);
            for (final String contextPath : branch.getContextPaths()) {
                final Branch[] branches = map.get(contextPath);
                map.put(contextPath, ArrayUtil.addToArray(branches, branch, Branch.class));
            }
            for (final ContextHandler context : branch.getContextHandlers()) {
                this._contextBranches.putIfAbsent(context, branch.getHandler());
            }
        }
        for (final Map.Entry<String, Branch[]> entry : map.entrySet()) {
            final Branch[] branches2 = entry.getValue();
            final Branch[] sorted = new Branch[branches2.length];
            int i = 0;
            for (final Branch branch2 : branches2) {
                if (branch2.hasVirtualHost()) {
                    sorted[i++] = branch2;
                }
            }
            for (final Branch branch2 : branches2) {
                if (!branch2.hasVirtualHost()) {
                    sorted[i++] = branch2;
                }
            }
            entry.setValue(sorted);
        }
        int capacity = 512;
        Trie<Map.Entry<String, Branch[]>> trie = null;
    Label_0371:
        while (true) {
            trie = new ArrayTernaryTrie<Map.Entry<String, Branch[]>>(false, capacity);
            for (final Map.Entry<String, Branch[]> entry2 : map.entrySet()) {
                if (!trie.put(entry2.getKey().substring(1), entry2)) {
                    capacity += 512;
                    continue Label_0371;
                }
            }
            break;
        }
        if (ContextHandlerCollection.LOG.isDebugEnabled()) {
            for (final String ctx : trie.keySet()) {
                ContextHandlerCollection.LOG.debug("{}->{}", ctx, Arrays.asList((Branch[])trie.get(ctx).getValue()));
            }
        }
        this._pathBranches = trie;
    }
    
    @Override
    public void setHandlers(final Handler[] handlers) {
        super.setHandlers(handlers);
        if (this.isStarted()) {
            this.mapContexts();
        }
    }
    
    @Override
    protected void doStart() throws Exception {
        this.mapContexts();
        super.doStart();
    }
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final Handler[] handlers = this.getHandlers();
        if (handlers == null || handlers.length == 0) {
            return;
        }
        final HttpChannelState async = baseRequest.getHttpChannelState();
        if (async.isAsync()) {
            final ContextHandler context = async.getContextHandler();
            if (context != null) {
                final Handler branch = this._contextBranches.get(context);
                if (branch == null) {
                    context.handle(target, baseRequest, request, response);
                }
                else {
                    branch.handle(target, baseRequest, request, response);
                }
                return;
            }
        }
        if (target.startsWith("/")) {
            int l;
            for (int limit = target.length() - 1; limit >= 0; limit = l - 2) {
                final Map.Entry<String, Branch[]> branches = this._pathBranches.getBest(target, 1, limit);
                if (branches == null) {
                    break;
                }
                l = branches.getKey().length();
                if (l == 1 || target.length() == l || target.charAt(l) == '/') {
                    for (final Branch branch2 : branches.getValue()) {
                        branch2.getHandler().handle(target, baseRequest, request, response);
                        if (baseRequest.isHandled()) {
                            return;
                        }
                    }
                }
            }
        }
        else {
            for (int i = 0; i < handlers.length; ++i) {
                handlers[i].handle(target, baseRequest, request, response);
                if (baseRequest.isHandled()) {
                    return;
                }
            }
        }
    }
    
    public ContextHandler addContext(final String contextPath, final String resourceBase) {
        try {
            final ContextHandler context = (ContextHandler)this._contextClass.newInstance();
            context.setContextPath(contextPath);
            context.setResourceBase(resourceBase);
            this.addHandler(context);
            return context;
        }
        catch (Exception e) {
            ContextHandlerCollection.LOG.debug(e);
            throw new Error(e);
        }
    }
    
    public Class<?> getContextClass() {
        return this._contextClass;
    }
    
    public void setContextClass(final Class<? extends ContextHandler> contextClass) {
        if (contextClass == null || !ContextHandler.class.isAssignableFrom(contextClass)) {
            throw new IllegalArgumentException();
        }
        this._contextClass = contextClass;
    }
    
    static {
        LOG = Log.getLogger(ContextHandlerCollection.class);
    }
    
    private static final class Branch
    {
        private final Handler _handler;
        private final ContextHandler[] _contexts;
        
        Branch(final Handler handler) {
            this._handler = handler;
            if (handler instanceof ContextHandler) {
                this._contexts = new ContextHandler[] { (ContextHandler)handler };
            }
            else if (handler instanceof HandlerContainer) {
                final Handler[] contexts = ((HandlerContainer)handler).getChildHandlersByClass(ContextHandler.class);
                System.arraycopy(contexts, 0, this._contexts = new ContextHandler[contexts.length], 0, contexts.length);
            }
            else {
                this._contexts = new ContextHandler[0];
            }
        }
        
        Set<String> getContextPaths() {
            final Set<String> set = new HashSet<String>();
            for (final ContextHandler context : this._contexts) {
                set.add(context.getContextPath());
            }
            return set;
        }
        
        boolean hasVirtualHost() {
            for (final ContextHandler context : this._contexts) {
                if (context.getVirtualHosts() != null && context.getVirtualHosts().length > 0) {
                    return true;
                }
            }
            return false;
        }
        
        ContextHandler[] getContextHandlers() {
            return this._contexts;
        }
        
        Handler getHandler() {
            return this._handler;
        }
        
        @Override
        public String toString() {
            return String.format("{%s,%s}", this._handler, Arrays.asList(this._contexts));
        }
    }
}
