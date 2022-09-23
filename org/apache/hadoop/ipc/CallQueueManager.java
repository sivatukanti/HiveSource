// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.ipc.protobuf.RpcHeaderProtos;
import java.io.IOException;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import java.util.concurrent.BlockingQueue;
import java.util.AbstractQueue;

public class CallQueueManager<E extends Schedulable> extends AbstractQueue<E> implements BlockingQueue<E>
{
    public static final Logger LOG;
    private static final int CHECKPOINT_NUM = 20;
    private static final long CHECKPOINT_INTERVAL_MS = 10L;
    private volatile boolean clientBackOffEnabled;
    private final AtomicReference<BlockingQueue<E>> putRef;
    private final AtomicReference<BlockingQueue<E>> takeRef;
    private RpcScheduler scheduler;
    
    static <E> Class<? extends BlockingQueue<E>> convertQueueClass(final Class<?> queueClass, final Class<E> elementClass) {
        return (Class<? extends BlockingQueue<E>>)queueClass;
    }
    
    static Class<? extends RpcScheduler> convertSchedulerClass(final Class<?> schedulerClass) {
        return (Class<? extends RpcScheduler>)schedulerClass;
    }
    
    public CallQueueManager(final Class<? extends BlockingQueue<E>> backingClass, final Class<? extends RpcScheduler> schedulerClass, final boolean clientBackOffEnabled, final int maxQueueSize, final String namespace, final Configuration conf) {
        final int priorityLevels = parseNumLevels(namespace, conf);
        this.scheduler = createScheduler(schedulerClass, priorityLevels, namespace, conf);
        final BlockingQueue<E> bq = this.createCallQueueInstance(backingClass, priorityLevels, maxQueueSize, namespace, conf);
        this.clientBackOffEnabled = clientBackOffEnabled;
        this.putRef = new AtomicReference<BlockingQueue<E>>(bq);
        this.takeRef = new AtomicReference<BlockingQueue<E>>(bq);
        CallQueueManager.LOG.info("Using callQueue: {}, queueCapacity: {}, scheduler: {}, ipcBackoff: {}.", backingClass, maxQueueSize, schedulerClass, clientBackOffEnabled);
    }
    
    @VisibleForTesting
    CallQueueManager(final BlockingQueue<E> queue, final RpcScheduler scheduler, final boolean clientBackOffEnabled) {
        this.putRef = new AtomicReference<BlockingQueue<E>>(queue);
        this.takeRef = new AtomicReference<BlockingQueue<E>>(queue);
        this.scheduler = scheduler;
        this.clientBackOffEnabled = clientBackOffEnabled;
    }
    
    private static <T extends RpcScheduler> T createScheduler(final Class<T> theClass, final int priorityLevels, final String ns, final Configuration conf) {
        try {
            final Constructor<T> ctor = theClass.getDeclaredConstructor(Integer.TYPE, String.class, Configuration.class);
            return ctor.newInstance(priorityLevels, ns, conf);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (InvocationTargetException e2) {
            throw new RuntimeException(theClass.getName() + " could not be constructed.", e2.getCause());
        }
        catch (Exception ex) {
            try {
                final Constructor<T> ctor = theClass.getDeclaredConstructor(Integer.TYPE);
                return ctor.newInstance(priorityLevels);
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (InvocationTargetException e2) {
                throw new RuntimeException(theClass.getName() + " could not be constructed.", e2.getCause());
            }
            catch (Exception ex2) {
                try {
                    final Constructor<T> ctor = theClass.getDeclaredConstructor((Class<?>[])new Class[0]);
                    return ctor.newInstance(new Object[0]);
                }
                catch (RuntimeException e) {
                    throw e;
                }
                catch (InvocationTargetException e2) {
                    throw new RuntimeException(theClass.getName() + " could not be constructed.", e2.getCause());
                }
                catch (Exception ex3) {
                    throw new RuntimeException(theClass.getName() + " could not be constructed.");
                }
            }
        }
    }
    
    private <T extends BlockingQueue<E>> T createCallQueueInstance(final Class<T> theClass, final int priorityLevels, final int maxLen, final String ns, final Configuration conf) {
        try {
            final Constructor<T> ctor = theClass.getDeclaredConstructor(Integer.TYPE, Integer.TYPE, String.class, Configuration.class);
            return ctor.newInstance(priorityLevels, maxLen, ns, conf);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (InvocationTargetException e2) {
            throw new RuntimeException(theClass.getName() + " could not be constructed.", e2.getCause());
        }
        catch (Exception ex) {
            try {
                final Constructor<T> ctor = theClass.getDeclaredConstructor(Integer.TYPE);
                return ctor.newInstance(maxLen);
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (InvocationTargetException e2) {
                throw new RuntimeException(theClass.getName() + " could not be constructed.", e2.getCause());
            }
            catch (Exception ex2) {
                try {
                    final Constructor<T> ctor = theClass.getDeclaredConstructor((Class<?>[])new Class[0]);
                    return ctor.newInstance(new Object[0]);
                }
                catch (RuntimeException e) {
                    throw e;
                }
                catch (InvocationTargetException e2) {
                    throw new RuntimeException(theClass.getName() + " could not be constructed.", e2.getCause());
                }
                catch (Exception ex3) {
                    throw new RuntimeException(theClass.getName() + " could not be constructed.");
                }
            }
        }
    }
    
    boolean isClientBackoffEnabled() {
        return this.clientBackOffEnabled;
    }
    
    boolean shouldBackOff(final Schedulable e) {
        return this.scheduler.shouldBackOff(e);
    }
    
    void addResponseTime(final String name, final int priorityLevel, final int queueTime, final int processingTime) {
        this.scheduler.addResponseTime(name, priorityLevel, queueTime, processingTime);
    }
    
    int getPriorityLevel(final Schedulable e) {
        return this.scheduler.getPriorityLevel(e);
    }
    
    void setClientBackoffEnabled(final boolean value) {
        this.clientBackOffEnabled = value;
    }
    
    @Override
    public void put(final E e) throws InterruptedException {
        if (!this.isClientBackoffEnabled()) {
            this.putRef.get().put(e);
        }
        else if (this.shouldBackOff(e)) {
            this.throwBackoff();
        }
        else {
            this.add(e);
        }
    }
    
    @Override
    public boolean add(final E e) {
        try {
            return this.putRef.get().add(e);
        }
        catch (CallQueueOverflowException ex) {
            throw ex;
        }
        catch (IllegalStateException ise) {
            this.throwBackoff();
            return true;
        }
    }
    
    private void throwBackoff() throws IllegalStateException {
        throw CallQueueOverflowException.DISCONNECT;
    }
    
    @Override
    public boolean offer(final E e) {
        return this.putRef.get().offer(e);
    }
    
    @Override
    public boolean offer(final E e, final long timeout, final TimeUnit unit) throws InterruptedException {
        return this.putRef.get().offer(e, timeout, unit);
    }
    
    @Override
    public E peek() {
        return this.takeRef.get().peek();
    }
    
    @Override
    public E poll() {
        return this.takeRef.get().poll();
    }
    
    @Override
    public E poll(final long timeout, final TimeUnit unit) throws InterruptedException {
        return this.takeRef.get().poll(timeout, unit);
    }
    
    @Override
    public E take() throws InterruptedException {
        E e;
        for (e = null; e == null; e = this.takeRef.get().poll(1000L, TimeUnit.MILLISECONDS)) {}
        return e;
    }
    
    @Override
    public int size() {
        return this.takeRef.get().size();
    }
    
    @Override
    public int remainingCapacity() {
        return this.takeRef.get().remainingCapacity();
    }
    
    private static int parseNumLevels(final String ns, final Configuration conf) {
        int retval = conf.getInt(ns + "." + "faircallqueue.priority-levels", 0);
        if (retval == 0) {
            retval = conf.getInt(ns + "." + "scheduler.priority.levels", 4);
        }
        else {
            CallQueueManager.LOG.warn(ns + "." + "faircallqueue.priority-levels" + " is deprecated. Please use " + ns + "." + "scheduler.priority.levels" + ".");
        }
        if (retval < 1) {
            throw new IllegalArgumentException("numLevels must be at least 1");
        }
        return retval;
    }
    
    public synchronized void swapQueue(final Class<? extends RpcScheduler> schedulerClass, final Class<? extends BlockingQueue<E>> queueClassToUse, final int maxSize, final String ns, final Configuration conf) {
        final int priorityLevels = parseNumLevels(ns, conf);
        this.scheduler.stop();
        final RpcScheduler newScheduler = createScheduler(schedulerClass, priorityLevels, ns, conf);
        final BlockingQueue<E> newQ = this.createCallQueueInstance(queueClassToUse, priorityLevels, maxSize, ns, conf);
        final BlockingQueue<E> oldQ = this.putRef.get();
        this.putRef.set(newQ);
        while (!this.queueIsReallyEmpty(oldQ)) {}
        this.takeRef.set(newQ);
        this.scheduler = newScheduler;
        CallQueueManager.LOG.info("Old Queue: " + this.stringRepr(oldQ) + ", Replacement: " + this.stringRepr(newQ));
    }
    
    private boolean queueIsReallyEmpty(final BlockingQueue<?> q) {
        for (int i = 0; i < 20; ++i) {
            try {
                Thread.sleep(10L);
            }
            catch (InterruptedException ie) {
                return false;
            }
            if (!q.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    private String stringRepr(final Object o) {
        return o.getClass().getName() + '@' + Integer.toHexString(o.hashCode());
    }
    
    @Override
    public int drainTo(final Collection<? super E> c) {
        return this.takeRef.get().drainTo(c);
    }
    
    @Override
    public int drainTo(final Collection<? super E> c, final int maxElements) {
        return this.takeRef.get().drainTo(c, maxElements);
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.takeRef.get().iterator();
    }
    
    static {
        LOG = LoggerFactory.getLogger(CallQueueManager.class);
    }
    
    static class CallQueueOverflowException extends IllegalStateException
    {
        private static String TOO_BUSY;
        static final CallQueueOverflowException KEEPALIVE;
        static final CallQueueOverflowException DISCONNECT;
        
        CallQueueOverflowException(final IOException ioe, final RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto status) {
            super("Queue full", new RpcServerException(ioe.getMessage(), ioe) {
                @Override
                public RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto getRpcStatusProto() {
                    return status;
                }
            });
        }
        
        @Override
        public IOException getCause() {
            return (IOException)super.getCause();
        }
        
        static {
            CallQueueOverflowException.TOO_BUSY = "Server too busy";
            KEEPALIVE = new CallQueueOverflowException(new RetriableException(CallQueueOverflowException.TOO_BUSY), RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.ERROR);
            DISCONNECT = new CallQueueOverflowException(new RetriableException(CallQueueOverflowException.TOO_BUSY + " - disconnecting"), RpcHeaderProtos.RpcResponseHeaderProto.RpcStatusProto.FATAL);
        }
    }
}
