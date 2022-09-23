// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.async;

import java.util.concurrent.TimeoutException;
import java.util.Iterator;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.spi.SelectorProvider;
import java.util.Comparator;
import java.util.TreeSet;
import java.nio.channels.Selector;
import parquet.org.slf4j.LoggerFactory;
import parquet.org.apache.thrift.TException;
import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import parquet.org.slf4j.Logger;

public class TAsyncClientManager
{
    private static final Logger LOGGER;
    private final SelectThread selectThread;
    private final ConcurrentLinkedQueue<TAsyncMethodCall> pendingCalls;
    
    public TAsyncClientManager() throws IOException {
        this.pendingCalls = new ConcurrentLinkedQueue<TAsyncMethodCall>();
        (this.selectThread = new SelectThread()).start();
    }
    
    public void call(final TAsyncMethodCall method) throws TException {
        if (!this.isRunning()) {
            throw new TException("SelectThread is not running");
        }
        method.prepareMethodCall();
        this.pendingCalls.add(method);
        this.selectThread.getSelector().wakeup();
    }
    
    public void stop() {
        this.selectThread.finish();
    }
    
    public boolean isRunning() {
        return this.selectThread.isAlive();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(TAsyncClientManager.class.getName());
    }
    
    private class SelectThread extends Thread
    {
        private final Selector selector;
        private volatile boolean running;
        private final TreeSet<TAsyncMethodCall> timeoutWatchSet;
        
        public SelectThread() throws IOException {
            this.timeoutWatchSet = new TreeSet<TAsyncMethodCall>(new TAsyncMethodCallTimeoutComparator());
            this.selector = SelectorProvider.provider().openSelector();
            this.running = true;
            this.setName("TAsyncClientManager#SelectorThread " + this.getId());
            this.setDaemon(true);
        }
        
        public Selector getSelector() {
            return this.selector;
        }
        
        public void finish() {
            this.running = false;
            this.selector.wakeup();
        }
        
        @Override
        public void run() {
            while (this.running) {
                try {
                    try {
                        if (this.timeoutWatchSet.size() == 0) {
                            this.selector.select();
                        }
                        else {
                            final long nextTimeout = this.timeoutWatchSet.first().getTimeoutTimestamp();
                            final long selectTime = nextTimeout - System.currentTimeMillis();
                            if (selectTime > 0L) {
                                this.selector.select(selectTime);
                            }
                            else {
                                this.selector.selectNow();
                            }
                        }
                    }
                    catch (IOException e) {
                        TAsyncClientManager.LOGGER.error("Caught IOException in TAsyncClientManager!", e);
                    }
                    this.transitionMethods();
                    this.timeoutMethods();
                    this.startPendingMethods();
                }
                catch (Exception exception) {
                    TAsyncClientManager.LOGGER.error("Ignoring uncaught exception in SelectThread", exception);
                }
            }
        }
        
        private void transitionMethods() {
            try {
                final Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    final SelectionKey key = keys.next();
                    keys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    final TAsyncMethodCall methodCall = (TAsyncMethodCall)key.attachment();
                    methodCall.transition(key);
                    if (!methodCall.isFinished() && !methodCall.getClient().hasError()) {
                        continue;
                    }
                    this.timeoutWatchSet.remove(methodCall);
                }
            }
            catch (ClosedSelectorException e) {
                TAsyncClientManager.LOGGER.error("Caught ClosedSelectorException in TAsyncClientManager!", e);
            }
        }
        
        private void timeoutMethods() {
            final Iterator<TAsyncMethodCall> iterator = (Iterator<TAsyncMethodCall>)this.timeoutWatchSet.iterator();
            final long currentTime = System.currentTimeMillis();
            while (iterator.hasNext()) {
                final TAsyncMethodCall methodCall = iterator.next();
                if (currentTime < methodCall.getTimeoutTimestamp()) {
                    break;
                }
                iterator.remove();
                methodCall.onError(new TimeoutException("Operation " + methodCall.getClass() + " timed out after " + (currentTime - methodCall.getStartTime()) + " ms."));
            }
        }
        
        private void startPendingMethods() {
            TAsyncMethodCall methodCall;
            while ((methodCall = TAsyncClientManager.this.pendingCalls.poll()) != null) {
                try {
                    methodCall.start(this.selector);
                    final TAsyncClient client = methodCall.getClient();
                    if (!client.hasTimeout() || client.hasError()) {
                        continue;
                    }
                    this.timeoutWatchSet.add(methodCall);
                }
                catch (Exception exception) {
                    TAsyncClientManager.LOGGER.warn("Caught exception in TAsyncClientManager!", exception);
                    methodCall.onError(exception);
                }
            }
        }
    }
    
    private static class TAsyncMethodCallTimeoutComparator implements Comparator<TAsyncMethodCall>
    {
        public int compare(final TAsyncMethodCall left, final TAsyncMethodCall right) {
            if (left.getTimeoutTimestamp() == right.getTimeoutTimestamp()) {
                return (int)(left.getSequenceId() - right.getSequenceId());
            }
            return (int)(left.getTimeoutTimestamp() - right.getTimeoutTimestamp());
        }
    }
}
