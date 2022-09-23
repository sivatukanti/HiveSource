// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import java.util.Collection;
import java.lang.reflect.Method;
import java.util.Enumeration;
import org.mortbay.jetty.handler.HandlerCollection;
import javax.servlet.ServletException;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.ListIterator;
import java.util.Iterator;
import org.mortbay.thread.QueuedThreadPool;
import org.mortbay.component.LifeCycle;
import org.mortbay.util.MultiException;
import org.mortbay.log.Log;
import org.mortbay.util.LazyList;
import org.mortbay.jetty.bio.SocketConnector;
import java.util.ArrayList;
import java.util.List;
import org.mortbay.util.AttributesMap;
import org.mortbay.component.Container;
import org.mortbay.jetty.security.UserRealm;
import org.mortbay.thread.ThreadPool;
import org.mortbay.util.Attributes;
import org.mortbay.jetty.handler.HandlerWrapper;

public class Server extends HandlerWrapper implements Attributes
{
    public static final String UNKNOWN_VERSION = "6.1.x";
    public static final String SNAPSHOT_VERSION = "6.1-SNAPSHOT";
    private static ShutdownHookThread hookThread;
    private static String _version;
    private ThreadPool _threadPool;
    private Connector[] _connectors;
    private UserRealm[] _realms;
    private Container _container;
    private SessionIdManager _sessionIdManager;
    private boolean _sendServerVersion;
    private boolean _sendDateHeader;
    private AttributesMap _attributes;
    private List _dependentLifeCycles;
    private int _graceful;
    
    public Server() {
        this._container = new Container();
        this._sendServerVersion = true;
        this._sendDateHeader = false;
        this._attributes = new AttributesMap();
        this._dependentLifeCycles = new ArrayList();
        this._graceful = 0;
        this.setServer(this);
    }
    
    public Server(final int port) {
        this._container = new Container();
        this._sendServerVersion = true;
        this._sendDateHeader = false;
        this._attributes = new AttributesMap();
        this._dependentLifeCycles = new ArrayList();
        this._graceful = 0;
        this.setServer(this);
        final Connector connector = new SocketConnector();
        connector.setPort(port);
        this.setConnectors(new Connector[] { connector });
    }
    
    public static String getVersion() {
        return Server._version;
    }
    
    public Container getContainer() {
        return this._container;
    }
    
    public boolean getStopAtShutdown() {
        return Server.hookThread.contains(this);
    }
    
    public void setStopAtShutdown(final boolean stop) {
        if (stop) {
            Server.hookThread.add(this);
        }
        else {
            Server.hookThread.remove(this);
        }
    }
    
    public Connector[] getConnectors() {
        return this._connectors;
    }
    
    public void addConnector(final Connector connector) {
        this.setConnectors((Connector[])LazyList.addToArray(this.getConnectors(), connector, Connector.class));
    }
    
    public void removeConnector(final Connector connector) {
        this.setConnectors((Connector[])LazyList.removeFromArray(this.getConnectors(), connector));
    }
    
    public void setConnectors(final Connector[] connectors) {
        if (connectors != null) {
            for (int i = 0; i < connectors.length; ++i) {
                connectors[i].setServer(this);
            }
        }
        this._container.update(this, this._connectors, connectors, "connector");
        this._connectors = connectors;
    }
    
    public ThreadPool getThreadPool() {
        return this._threadPool;
    }
    
    public void setThreadPool(final ThreadPool threadPool) {
        this._container.update(this, this._threadPool, threadPool, "threadpool", true);
        this._threadPool = threadPool;
    }
    
    protected void doStart() throws Exception {
        Log.info("jetty-" + Server._version);
        HttpGenerator.setServerVersion(Server._version);
        final MultiException mex = new MultiException();
        for (int i = 0; this._realms != null && i < this._realms.length; ++i) {
            if (this._realms[i] instanceof LifeCycle) {
                ((LifeCycle)this._realms[i]).start();
            }
        }
        final Iterator itor = this._dependentLifeCycles.iterator();
        while (itor.hasNext()) {
            try {
                itor.next().start();
            }
            catch (Throwable e) {
                mex.add(e);
            }
        }
        if (this._threadPool == null) {
            final QueuedThreadPool tp = new QueuedThreadPool();
            this.setThreadPool(tp);
        }
        if (this._sessionIdManager != null) {
            this._sessionIdManager.start();
        }
        try {
            if (this._threadPool instanceof LifeCycle) {
                ((LifeCycle)this._threadPool).start();
            }
        }
        catch (Throwable e) {
            mex.add(e);
        }
        try {
            super.doStart();
        }
        catch (Throwable e) {
            Log.warn("Error starting handlers", e);
        }
        if (this._connectors != null) {
            for (int j = 0; j < this._connectors.length; ++j) {
                try {
                    this._connectors[j].start();
                }
                catch (Throwable e2) {
                    mex.add(e2);
                }
            }
        }
        mex.ifExceptionThrow();
    }
    
    protected void doStop() throws Exception {
        final MultiException mex = new MultiException();
        for (int i = 0; this._realms != null && i < this._realms.length; ++i) {
            if (this._realms[i] instanceof LifeCycle) {
                ((LifeCycle)this._realms[i]).stop();
            }
        }
        if (this._graceful > 0) {
            if (this._connectors != null) {
                int i = this._connectors.length;
                while (i-- > 0) {
                    Log.info("Graceful shutdown {}", this._connectors[i]);
                    try {
                        this._connectors[i].close();
                    }
                    catch (Throwable e) {
                        mex.add(e);
                    }
                }
            }
            final Handler[] contexts = this.getChildHandlersByClass(Graceful.class);
            for (int c = 0; c < contexts.length; ++c) {
                final Graceful context = (Graceful)contexts[c];
                Log.info("Graceful shutdown {}", context);
                context.setShutdown(true);
            }
            Thread.sleep(this._graceful);
        }
        if (this._connectors != null) {
            int i = this._connectors.length;
            while (i-- > 0) {
                try {
                    this._connectors[i].stop();
                }
                catch (Throwable e) {
                    mex.add(e);
                }
            }
        }
        try {
            super.doStop();
        }
        catch (Throwable e2) {
            mex.add(e2);
        }
        if (this._sessionIdManager != null) {
            this._sessionIdManager.stop();
        }
        try {
            if (this._threadPool instanceof LifeCycle) {
                ((LifeCycle)this._threadPool).stop();
            }
        }
        catch (Throwable e2) {
            mex.add(e2);
        }
        if (!this._dependentLifeCycles.isEmpty()) {
            final ListIterator itor = this._dependentLifeCycles.listIterator(this._dependentLifeCycles.size());
            while (itor.hasPrevious()) {
                try {
                    itor.previous().stop();
                }
                catch (Throwable e) {
                    mex.add(e);
                }
            }
        }
        mex.ifExceptionThrow();
    }
    
    public void handle(final HttpConnection connection) throws IOException, ServletException {
        final String target = connection.getRequest().getPathInfo();
        if (Log.isDebugEnabled()) {
            Log.debug("REQUEST " + target + " on " + connection);
            this.handle(target, connection.getRequest(), connection.getResponse(), 1);
            Log.debug("RESPONSE " + target + "  " + connection.getResponse().getStatus());
        }
        else {
            this.handle(target, connection.getRequest(), connection.getResponse(), 1);
        }
    }
    
    public void join() throws InterruptedException {
        this.getThreadPool().join();
    }
    
    public UserRealm[] getUserRealms() {
        return this._realms;
    }
    
    public void setUserRealms(final UserRealm[] realms) {
        this._container.update(this, this._realms, realms, "realm", true);
        this._realms = realms;
    }
    
    public void addUserRealm(final UserRealm realm) {
        this.setUserRealms((UserRealm[])LazyList.addToArray(this.getUserRealms(), realm, UserRealm.class));
    }
    
    public void removeUserRealm(final UserRealm realm) {
        this.setUserRealms((UserRealm[])LazyList.removeFromArray(this.getUserRealms(), realm));
    }
    
    public SessionIdManager getSessionIdManager() {
        return this._sessionIdManager;
    }
    
    public void setSessionIdManager(final SessionIdManager sessionIdManager) {
        this._container.update(this, this._sessionIdManager, sessionIdManager, "sessionIdManager", true);
        this._sessionIdManager = sessionIdManager;
    }
    
    public void setSendServerVersion(final boolean sendServerVersion) {
        this._sendServerVersion = sendServerVersion;
    }
    
    public boolean getSendServerVersion() {
        return this._sendServerVersion;
    }
    
    public void setSendDateHeader(final boolean sendDateHeader) {
        this._sendDateHeader = sendDateHeader;
    }
    
    public boolean getSendDateHeader() {
        return this._sendDateHeader;
    }
    
    public void addLifeCycle(final LifeCycle c) {
        if (c == null) {
            return;
        }
        if (!this._dependentLifeCycles.contains(c)) {
            this._dependentLifeCycles.add(c);
            this._container.addBean(c);
        }
        try {
            if (this.isStarted()) {
                c.start();
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void removeLifeCycle(final LifeCycle c) {
        if (c == null) {
            return;
        }
        this._dependentLifeCycles.remove(c);
        this._container.removeBean(c);
    }
    
    public void addHandler(final Handler handler) {
        if (this.getHandler() == null) {
            this.setHandler(handler);
        }
        else if (this.getHandler() instanceof HandlerCollection) {
            ((HandlerCollection)this.getHandler()).addHandler(handler);
        }
        else {
            final HandlerCollection collection = new HandlerCollection();
            collection.setHandlers(new Handler[] { this.getHandler(), handler });
            this.setHandler(collection);
        }
    }
    
    public void removeHandler(final Handler handler) {
        if (this.getHandler() instanceof HandlerCollection) {
            ((HandlerCollection)this.getHandler()).removeHandler(handler);
        }
    }
    
    public Handler[] getHandlers() {
        if (this.getHandler() instanceof HandlerCollection) {
            return ((HandlerCollection)this.getHandler()).getHandlers();
        }
        return null;
    }
    
    public void setHandlers(final Handler[] handlers) {
        HandlerCollection collection;
        if (this.getHandler() instanceof HandlerCollection) {
            collection = (HandlerCollection)this.getHandler();
        }
        else {
            collection = new HandlerCollection();
            this.setHandler(collection);
        }
        collection.setHandlers(handlers);
    }
    
    public void clearAttributes() {
        this._attributes.clearAttributes();
    }
    
    public Object getAttribute(final String name) {
        return this._attributes.getAttribute(name);
    }
    
    public Enumeration getAttributeNames() {
        return AttributesMap.getAttributeNamesCopy(this._attributes);
    }
    
    public void removeAttribute(final String name) {
        this._attributes.removeAttribute(name);
    }
    
    public void setAttribute(final String name, final Object attribute) {
        this._attributes.setAttribute(name, attribute);
    }
    
    public int getGracefulShutdown() {
        return this._graceful;
    }
    
    public void setGracefulShutdown(final int timeoutMS) {
        this._graceful = timeoutMS;
    }
    
    static {
        Server.hookThread = new ShutdownHookThread();
        Server._version = ((Server.class.getPackage() != null && Server.class.getPackage().getImplementationVersion() != null) ? Server.class.getPackage().getImplementationVersion() : "6.1.x");
    }
    
    private static class ShutdownHookThread extends Thread
    {
        private boolean hooked;
        private ArrayList servers;
        
        private ShutdownHookThread() {
            this.hooked = false;
            this.servers = new ArrayList();
        }
        
        private void createShutdownHook() {
            if (!Boolean.getBoolean("JETTY_NO_SHUTDOWN_HOOK") && !this.hooked) {
                try {
                    final Method shutdownHook = Runtime.class.getMethod("addShutdownHook", Thread.class);
                    shutdownHook.invoke(Runtime.getRuntime(), this);
                    this.hooked = true;
                }
                catch (Exception e) {
                    if (Log.isDebugEnabled()) {
                        Log.debug("No shutdown hook in JVM ", e);
                    }
                }
            }
        }
        
        public boolean add(final Server server) {
            this.createShutdownHook();
            return this.servers.add(server);
        }
        
        public boolean contains(final Server server) {
            return this.servers.contains(server);
        }
        
        public boolean addAll(final Collection c) {
            this.createShutdownHook();
            return this.servers.addAll(c);
        }
        
        public void clear() {
            this.createShutdownHook();
            this.servers.clear();
        }
        
        public boolean remove(final Server server) {
            this.createShutdownHook();
            return this.servers.remove(server);
        }
        
        public boolean removeAll(final Collection c) {
            this.createShutdownHook();
            return this.servers.removeAll(c);
        }
        
        public void run() {
            this.setName("Shutdown");
            Log.info("Shutdown hook executing");
            for (final Server svr : this.servers) {
                if (svr == null) {
                    continue;
                }
                try {
                    svr.stop();
                }
                catch (Exception e) {
                    Log.warn(e);
                }
                Log.info("Shutdown hook complete");
                try {
                    Thread.sleep(1000L);
                }
                catch (Exception e) {
                    Log.warn(e);
                }
            }
        }
    }
    
    public interface Graceful
    {
        void setShutdown(final boolean p0);
    }
}
