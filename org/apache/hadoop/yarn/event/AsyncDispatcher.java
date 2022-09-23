// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.event;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.util.ShutdownHookManager;
import org.apache.hadoop.conf.Configuration;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class AsyncDispatcher extends AbstractService implements Dispatcher
{
    private static final Log LOG;
    private final BlockingQueue<Event> eventQueue;
    private volatile boolean stopped;
    private volatile boolean drainEventsOnStop;
    private volatile boolean drained;
    private Object waitForDrained;
    private volatile boolean blockNewEvents;
    private EventHandler handlerInstance;
    private Thread eventHandlingThread;
    protected final Map<Class<? extends Enum>, EventHandler> eventDispatchers;
    private boolean exitOnDispatchException;
    
    public AsyncDispatcher() {
        this(new LinkedBlockingQueue<Event>());
    }
    
    public AsyncDispatcher(final BlockingQueue<Event> eventQueue) {
        super("Dispatcher");
        this.stopped = false;
        this.drainEventsOnStop = false;
        this.drained = true;
        this.waitForDrained = new Object();
        this.blockNewEvents = false;
        this.handlerInstance = null;
        this.eventQueue = eventQueue;
        this.eventDispatchers = new HashMap<Class<? extends Enum>, EventHandler>();
    }
    
    Runnable createThread() {
        return new Runnable() {
            @Override
            public void run() {
                while (!AsyncDispatcher.this.stopped && !Thread.currentThread().isInterrupted()) {
                    AsyncDispatcher.this.drained = AsyncDispatcher.this.eventQueue.isEmpty();
                    if (AsyncDispatcher.this.blockNewEvents) {
                        synchronized (AsyncDispatcher.this.waitForDrained) {
                            if (AsyncDispatcher.this.drained) {
                                AsyncDispatcher.this.waitForDrained.notify();
                            }
                        }
                    }
                    Event event;
                    try {
                        event = AsyncDispatcher.this.eventQueue.take();
                    }
                    catch (InterruptedException ie) {
                        if (!AsyncDispatcher.this.stopped) {
                            AsyncDispatcher.LOG.warn("AsyncDispatcher thread interrupted", ie);
                        }
                        return;
                    }
                    if (event != null) {
                        AsyncDispatcher.this.dispatch(event);
                    }
                }
            }
        };
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        this.exitOnDispatchException = conf.getBoolean("yarn.dispatcher.exit-on-error", false);
        super.serviceInit(conf);
    }
    
    @Override
    protected void serviceStart() throws Exception {
        super.serviceStart();
        (this.eventHandlingThread = new Thread(this.createThread())).setName("AsyncDispatcher event handler");
        this.eventHandlingThread.start();
    }
    
    public void setDrainEventsOnStop() {
        this.drainEventsOnStop = true;
    }
    
    @Override
    protected void serviceStop() throws Exception {
        if (this.drainEventsOnStop) {
            this.blockNewEvents = true;
            AsyncDispatcher.LOG.info("AsyncDispatcher is draining to stop, igonring any new events.");
            synchronized (this.waitForDrained) {
                while (!this.drained && this.eventHandlingThread.isAlive()) {
                    this.waitForDrained.wait(1000L);
                    AsyncDispatcher.LOG.info("Waiting for AsyncDispatcher to drain.");
                }
            }
        }
        this.stopped = true;
        if (this.eventHandlingThread != null) {
            this.eventHandlingThread.interrupt();
            try {
                this.eventHandlingThread.join();
            }
            catch (InterruptedException ie) {
                AsyncDispatcher.LOG.warn("Interrupted Exception while stopping", ie);
            }
        }
        super.serviceStop();
    }
    
    protected void dispatch(final Event event) {
        if (AsyncDispatcher.LOG.isDebugEnabled()) {
            AsyncDispatcher.LOG.debug("Dispatching the event " + event.getClass().getName() + "." + event.toString());
        }
        final Class<? extends Enum> type = event.getType().getDeclaringClass();
        try {
            final EventHandler handler = this.eventDispatchers.get(type);
            if (handler == null) {
                throw new Exception("No handler for registered for " + type);
            }
            handler.handle(event);
        }
        catch (Throwable t) {
            AsyncDispatcher.LOG.fatal("Error in dispatcher thread", t);
            if (this.exitOnDispatchException && !ShutdownHookManager.get().isShutdownInProgress() && !this.stopped) {
                AsyncDispatcher.LOG.info("Exiting, bbye..");
                System.exit(-1);
            }
        }
    }
    
    @Override
    public void register(final Class<? extends Enum> eventType, final EventHandler handler) {
        final EventHandler<Event> registeredHandler = this.eventDispatchers.get(eventType);
        AsyncDispatcher.LOG.info("Registering " + eventType + " for " + handler.getClass());
        if (registeredHandler == null) {
            this.eventDispatchers.put(eventType, handler);
        }
        else if (!(registeredHandler instanceof MultiListenerHandler)) {
            final MultiListenerHandler multiHandler = new MultiListenerHandler();
            multiHandler.addHandler(registeredHandler);
            multiHandler.addHandler(handler);
            this.eventDispatchers.put(eventType, multiHandler);
        }
        else {
            final MultiListenerHandler multiHandler = (MultiListenerHandler)registeredHandler;
            multiHandler.addHandler(handler);
        }
    }
    
    @Override
    public EventHandler getEventHandler() {
        if (this.handlerInstance == null) {
            this.handlerInstance = new GenericEventHandler();
        }
        return this.handlerInstance;
    }
    
    static {
        LOG = LogFactory.getLog(AsyncDispatcher.class);
    }
    
    class GenericEventHandler implements EventHandler<Event>
    {
        @Override
        public void handle(final Event event) {
            if (AsyncDispatcher.this.blockNewEvents) {
                return;
            }
            AsyncDispatcher.this.drained = false;
            final int qSize = AsyncDispatcher.this.eventQueue.size();
            if (qSize != 0 && qSize % 1000 == 0) {
                AsyncDispatcher.LOG.info("Size of event-queue is " + qSize);
            }
            final int remCapacity = AsyncDispatcher.this.eventQueue.remainingCapacity();
            if (remCapacity < 1000) {
                AsyncDispatcher.LOG.warn("Very low remaining capacity in the event-queue: " + remCapacity);
            }
            try {
                AsyncDispatcher.this.eventQueue.put(event);
            }
            catch (InterruptedException e) {
                if (!AsyncDispatcher.this.stopped) {
                    AsyncDispatcher.LOG.warn("AsyncDispatcher thread interrupted", e);
                }
                throw new YarnRuntimeException(e);
            }
        }
    }
    
    static class MultiListenerHandler implements EventHandler<Event>
    {
        List<EventHandler<Event>> listofHandlers;
        
        public MultiListenerHandler() {
            this.listofHandlers = new ArrayList<EventHandler<Event>>();
        }
        
        @Override
        public void handle(final Event event) {
            for (final EventHandler<Event> handler : this.listofHandlers) {
                handler.handle(event);
            }
        }
        
        void addHandler(final EventHandler<Event> handler) {
            this.listofHandlers.add(handler);
        }
    }
}
