// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import org.eclipse.jetty.util.log.Log;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.thread.Scheduler;
import java.util.concurrent.Executor;
import org.eclipse.jetty.util.log.Logger;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.net.InetAddress;
import org.eclipse.jetty.util.annotation.ManagedObject;
import java.net.InetSocketAddress;
import java.util.List;

public interface SocketAddressResolver
{
    void resolve(final String p0, final int p1, final Promise<List<InetSocketAddress>> p2);
    
    @ManagedObject("The synchronous address resolver")
    public static class Sync implements SocketAddressResolver
    {
        @Override
        public void resolve(final String host, final int port, final Promise<List<InetSocketAddress>> promise) {
            try {
                final InetAddress[] addresses = InetAddress.getAllByName(host);
                final List<InetSocketAddress> result = new ArrayList<InetSocketAddress>(addresses.length);
                for (final InetAddress address : addresses) {
                    result.add(new InetSocketAddress(address, port));
                }
                if (result.isEmpty()) {
                    promise.failed(new UnknownHostException());
                }
                else {
                    promise.succeeded(result);
                }
            }
            catch (Throwable x) {
                promise.failed(x);
            }
        }
    }
    
    @ManagedObject("The asynchronous address resolver")
    public static class Async implements SocketAddressResolver
    {
        private static final Logger LOG;
        private final Executor executor;
        private final Scheduler scheduler;
        private final long timeout;
        
        public Async(final Executor executor, final Scheduler scheduler, final long timeout) {
            this.executor = executor;
            this.scheduler = scheduler;
            this.timeout = timeout;
        }
        
        public Executor getExecutor() {
            return this.executor;
        }
        
        public Scheduler getScheduler() {
            return this.scheduler;
        }
        
        @ManagedAttribute(value = "The timeout, in milliseconds, to resolve an address", readonly = true)
        public long getTimeout() {
            return this.timeout;
        }
        
        @Override
        public void resolve(final String host, final int port, final Promise<List<InetSocketAddress>> promise) {
            Scheduler.Task task;
            final AtomicBoolean complete;
            Thread thread;
            final AtomicBoolean atomicBoolean;
            final TimeoutException ex;
            final Thread thread2;
            long start;
            InetAddress[] addresses;
            long elapsed;
            List<InetSocketAddress> result;
            final InetAddress[] array;
            int length;
            int i = 0;
            InetAddress address;
            this.executor.execute(() -> {
                task = null;
                complete = new AtomicBoolean();
                if (this.timeout > 0L) {
                    thread = Thread.currentThread();
                    task = this.scheduler.schedule(() -> {
                        if (atomicBoolean.compareAndSet(false, true)) {
                            new TimeoutException("DNS timeout " + this.getTimeout() + " ms");
                            promise.failed(ex);
                            thread2.interrupt();
                        }
                        return;
                    }, this.timeout, TimeUnit.MILLISECONDS);
                }
                try {
                    start = System.nanoTime();
                    addresses = InetAddress.getAllByName(host);
                    elapsed = System.nanoTime() - start;
                    if (Async.LOG.isDebugEnabled()) {
                        Async.LOG.debug("Resolved {} in {} ms", host, TimeUnit.NANOSECONDS.toMillis(elapsed));
                    }
                    result = new ArrayList<InetSocketAddress>(addresses.length);
                    for (length = array.length; i < length; ++i) {
                        address = array[i];
                        result.add(new InetSocketAddress(address, port));
                    }
                    if (complete.compareAndSet(false, true)) {
                        if (result.isEmpty()) {
                            promise.failed(new UnknownHostException());
                        }
                        else {
                            promise.succeeded(result);
                        }
                    }
                }
                catch (Throwable x) {
                    if (complete.compareAndSet(false, true)) {
                        promise.failed(x);
                    }
                }
                finally {
                    if (task != null) {
                        task.cancel();
                    }
                }
            });
        }
        
        static {
            LOG = Log.getLogger(SocketAddressResolver.class);
        }
    }
}
