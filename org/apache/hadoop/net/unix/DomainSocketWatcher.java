// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net.unix;

import java.io.EOFException;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.util.NativeCodeLoader;
import org.apache.commons.lang3.SystemUtils;
import java.nio.channels.ClosedChannelException;
import com.google.common.util.concurrent.Uninterruptibles;
import java.io.IOException;
import java.util.Map;
import java.util.Iterator;
import org.apache.hadoop.io.IOUtils;
import com.google.common.base.Preconditions;
import com.google.common.annotations.VisibleForTesting;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;

@InterfaceAudience.LimitedPrivate({ "HDFS" })
public final class DomainSocketWatcher implements Closeable
{
    static final Logger LOG;
    private static final String loadingFailureReason;
    private final ReentrantLock lock;
    private final Condition processedCond;
    private final LinkedList<Entry> toAdd;
    private final TreeMap<Integer, DomainSocket> toRemove;
    private final int interruptCheckPeriodMs;
    private final DomainSocket[] notificationSockets;
    private boolean closed;
    private boolean kicked;
    @VisibleForTesting
    final Thread watcherThread;
    
    private static native void anchorNative();
    
    public static String getLoadingFailureReason() {
        return DomainSocketWatcher.loadingFailureReason;
    }
    
    public DomainSocketWatcher(final int interruptCheckPeriodMs, final String src) throws IOException {
        this.lock = new ReentrantLock();
        this.processedCond = this.lock.newCondition();
        this.toAdd = new LinkedList<Entry>();
        this.toRemove = new TreeMap<Integer, DomainSocket>();
        this.closed = false;
        this.kicked = false;
        this.watcherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (DomainSocketWatcher.LOG.isDebugEnabled()) {
                    DomainSocketWatcher.LOG.debug(this + ": starting with interruptCheckPeriodMs = " + DomainSocketWatcher.this.interruptCheckPeriodMs);
                }
                final TreeMap<Integer, Entry> entries = new TreeMap<Integer, Entry>();
                final FdSet fdSet = new FdSet();
                DomainSocketWatcher.this.addNotificationSocket(entries, fdSet);
                try {
                    while (true) {
                        DomainSocketWatcher.this.lock.lock();
                        try {
                            for (final int fd : fdSet.getAndClearReadableFds()) {
                                DomainSocketWatcher.this.sendCallbackAndRemove("getAndClearReadableFds", entries, fdSet, fd);
                            }
                            if (!DomainSocketWatcher.this.toAdd.isEmpty() || !DomainSocketWatcher.this.toRemove.isEmpty()) {
                                final Iterator<Entry> iter = (Iterator<Entry>)DomainSocketWatcher.this.toAdd.iterator();
                                while (iter.hasNext()) {
                                    final Entry entry = iter.next();
                                    iter.remove();
                                    final DomainSocket sock = entry.getDomainSocket();
                                    final Entry prevEntry = entries.put(sock.fd, entry);
                                    Preconditions.checkState(prevEntry == null, (Object)(this + ": tried to watch a file descriptor that we were already watching: " + sock));
                                    if (DomainSocketWatcher.LOG.isTraceEnabled()) {
                                        DomainSocketWatcher.LOG.trace(this + ": adding fd " + sock.fd);
                                    }
                                    fdSet.add(sock.fd);
                                }
                                while (true) {
                                    final Map.Entry<Integer, DomainSocket> entry2 = DomainSocketWatcher.this.toRemove.firstEntry();
                                    if (entry2 == null) {
                                        break;
                                    }
                                    DomainSocketWatcher.this.sendCallbackAndRemove("handlePendingRemovals", entries, fdSet, entry2.getValue().fd);
                                }
                                DomainSocketWatcher.this.processedCond.signalAll();
                            }
                            if (DomainSocketWatcher.this.closed) {
                                if (DomainSocketWatcher.LOG.isDebugEnabled()) {
                                    DomainSocketWatcher.LOG.debug(this.toString() + " thread terminating.");
                                }
                                return;
                            }
                            if (Thread.interrupted()) {
                                throw new InterruptedException();
                            }
                        }
                        finally {
                            DomainSocketWatcher.this.lock.unlock();
                        }
                        doPoll0(DomainSocketWatcher.this.interruptCheckPeriodMs, fdSet);
                    }
                }
                catch (InterruptedException e2) {
                    DomainSocketWatcher.LOG.info(this.toString() + " terminating on InterruptedException");
                }
                catch (Throwable e) {
                    DomainSocketWatcher.LOG.error(this.toString() + " terminating on exception", e);
                }
                finally {
                    DomainSocketWatcher.this.lock.lock();
                    try {
                        DomainSocketWatcher.this.kick();
                        for (final Entry entry3 : entries.values()) {
                            DomainSocketWatcher.this.sendCallback("close", entries, fdSet, entry3.getDomainSocket().fd);
                        }
                        entries.clear();
                        fdSet.close();
                        DomainSocketWatcher.this.closed = true;
                        if (!DomainSocketWatcher.this.toAdd.isEmpty() || !DomainSocketWatcher.this.toRemove.isEmpty()) {
                            final Iterator<Entry> iter2 = (Iterator<Entry>)DomainSocketWatcher.this.toAdd.iterator();
                            while (iter2.hasNext()) {
                                final Entry entry3 = iter2.next();
                                entry3.getDomainSocket().refCount.unreference();
                                entry3.getHandler().handle(entry3.getDomainSocket());
                                IOUtils.cleanupWithLogger(DomainSocketWatcher.LOG, entry3.getDomainSocket());
                                iter2.remove();
                            }
                            while (true) {
                                final Map.Entry<Integer, DomainSocket> entry4 = DomainSocketWatcher.this.toRemove.firstEntry();
                                if (entry4 == null) {
                                    break;
                                }
                                DomainSocketWatcher.this.sendCallback("close", entries, fdSet, entry4.getValue().fd);
                            }
                        }
                        DomainSocketWatcher.this.processedCond.signalAll();
                    }
                    finally {
                        DomainSocketWatcher.this.lock.unlock();
                    }
                }
            }
        });
        if (DomainSocketWatcher.loadingFailureReason != null) {
            throw new UnsupportedOperationException(DomainSocketWatcher.loadingFailureReason);
        }
        Preconditions.checkArgument(interruptCheckPeriodMs > 0);
        this.interruptCheckPeriodMs = interruptCheckPeriodMs;
        this.notificationSockets = DomainSocket.socketpair();
        this.watcherThread.setDaemon(true);
        this.watcherThread.setName(src + " DomainSocketWatcher");
        this.watcherThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread thread, final Throwable t) {
                DomainSocketWatcher.LOG.error(thread + " terminating on unexpected exception", t);
            }
        });
        this.watcherThread.start();
    }
    
    @Override
    public void close() throws IOException {
        this.lock.lock();
        try {
            if (this.closed) {
                return;
            }
            if (DomainSocketWatcher.LOG.isDebugEnabled()) {
                DomainSocketWatcher.LOG.debug(this + ": closing");
            }
            this.closed = true;
        }
        finally {
            this.lock.unlock();
        }
        this.notificationSockets[0].close();
        Uninterruptibles.joinUninterruptibly(this.watcherThread);
    }
    
    @VisibleForTesting
    public boolean isClosed() {
        this.lock.lock();
        try {
            return this.closed;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void add(final DomainSocket sock, final Handler handler) {
        this.lock.lock();
        try {
            if (this.closed) {
                handler.handle(sock);
                IOUtils.cleanupWithLogger(DomainSocketWatcher.LOG, sock);
                return;
            }
            final Entry entry = new Entry(sock, handler);
            try {
                sock.refCount.reference();
            }
            catch (ClosedChannelException e1) {
                handler.handle(sock);
                return;
            }
            this.toAdd.add(entry);
            this.kick();
            do {
                this.processedCond.awaitUninterruptibly();
            } while (this.toAdd.contains(entry));
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public void remove(final DomainSocket sock) {
        this.lock.lock();
        try {
            if (this.closed) {
                return;
            }
            this.toRemove.put(sock.fd, sock);
            this.kick();
            do {
                this.processedCond.awaitUninterruptibly();
            } while (this.toRemove.containsKey(sock.fd));
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private void kick() {
        assert this.lock.isHeldByCurrentThread();
        if (this.kicked) {
            return;
        }
        try {
            this.notificationSockets[0].getOutputStream().write(0);
            this.kicked = true;
        }
        catch (IOException e) {
            if (!this.closed) {
                DomainSocketWatcher.LOG.error(this + ": error writing to notificationSockets[0]", e);
            }
        }
    }
    
    private boolean sendCallback(final String caller, final TreeMap<Integer, Entry> entries, final FdSet fdSet, final int fd) {
        if (DomainSocketWatcher.LOG.isTraceEnabled()) {
            DomainSocketWatcher.LOG.trace(this + ": " + caller + " starting sendCallback for fd " + fd);
        }
        final Entry entry = entries.get(fd);
        Preconditions.checkNotNull(entry, (Object)(this + ": fdSet contained " + fd + ", which we were not tracking."));
        final DomainSocket sock = entry.getDomainSocket();
        if (entry.getHandler().handle(sock)) {
            if (DomainSocketWatcher.LOG.isTraceEnabled()) {
                DomainSocketWatcher.LOG.trace(this + ": " + caller + ": closing fd " + fd + " at the request of the handler.");
            }
            if (this.toRemove.remove(fd) != null && DomainSocketWatcher.LOG.isTraceEnabled()) {
                DomainSocketWatcher.LOG.trace(this + ": " + caller + " : sendCallback processed fd " + fd + " in toRemove.");
            }
            try {
                sock.refCount.unreferenceCheckClosed();
            }
            catch (IOException e) {
                Preconditions.checkArgument(false, (Object)(this + ": file descriptor " + sock.fd + " was closed while still in the poll(2) loop."));
            }
            IOUtils.cleanupWithLogger(DomainSocketWatcher.LOG, sock);
            fdSet.remove(fd);
            return true;
        }
        if (DomainSocketWatcher.LOG.isTraceEnabled()) {
            DomainSocketWatcher.LOG.trace(this + ": " + caller + ": sendCallback not closing fd " + fd);
        }
        return false;
    }
    
    private void sendCallbackAndRemove(final String caller, final TreeMap<Integer, Entry> entries, final FdSet fdSet, final int fd) {
        if (this.sendCallback(caller, entries, fdSet, fd)) {
            entries.remove(fd);
        }
    }
    
    private void addNotificationSocket(final TreeMap<Integer, Entry> entries, final FdSet fdSet) {
        entries.put(this.notificationSockets[1].fd, new Entry(this.notificationSockets[1], new NotificationHandler()));
        try {
            this.notificationSockets[1].refCount.reference();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        fdSet.add(this.notificationSockets[1].fd);
        if (DomainSocketWatcher.LOG.isTraceEnabled()) {
            DomainSocketWatcher.LOG.trace(this + ": adding notificationSocket " + this.notificationSockets[1].fd + ", connected to " + this.notificationSockets[0].fd);
        }
    }
    
    @Override
    public String toString() {
        return "DomainSocketWatcher(" + System.identityHashCode(this) + ")";
    }
    
    private static native int doPoll0(final int p0, final FdSet p1) throws IOException;
    
    static {
        if (SystemUtils.IS_OS_WINDOWS) {
            loadingFailureReason = "UNIX Domain sockets are not available on Windows.";
        }
        else if (!NativeCodeLoader.isNativeCodeLoaded()) {
            loadingFailureReason = "libhadoop cannot be loaded.";
        }
        else {
            String problem;
            try {
                anchorNative();
                problem = null;
            }
            catch (Throwable t) {
                problem = "DomainSocketWatcher#anchorNative got error: " + t.getMessage();
            }
            loadingFailureReason = problem;
        }
        LOG = LoggerFactory.getLogger(DomainSocketWatcher.class);
    }
    
    private class NotificationHandler implements Handler
    {
        @Override
        public boolean handle(final DomainSocket sock) {
            assert DomainSocketWatcher.this.lock.isHeldByCurrentThread();
            try {
                DomainSocketWatcher.this.kicked = false;
                if (DomainSocketWatcher.LOG.isTraceEnabled()) {
                    DomainSocketWatcher.LOG.trace(this + ": NotificationHandler: doing a read on " + sock.fd);
                }
                if (sock.getInputStream().read() == -1) {
                    if (DomainSocketWatcher.LOG.isTraceEnabled()) {
                        DomainSocketWatcher.LOG.trace(this + ": NotificationHandler: got EOF on " + sock.fd);
                    }
                    throw new EOFException();
                }
                if (DomainSocketWatcher.LOG.isTraceEnabled()) {
                    DomainSocketWatcher.LOG.trace(this + ": NotificationHandler: read succeeded on " + sock.fd);
                }
                return false;
            }
            catch (IOException e) {
                if (DomainSocketWatcher.LOG.isTraceEnabled()) {
                    DomainSocketWatcher.LOG.trace(this + ": NotificationHandler: setting closed to true for " + sock.fd);
                }
                DomainSocketWatcher.this.closed = true;
                return true;
            }
        }
    }
    
    private static class Entry
    {
        final DomainSocket socket;
        final Handler handler;
        
        Entry(final DomainSocket socket, final Handler handler) {
            this.socket = socket;
            this.handler = handler;
        }
        
        DomainSocket getDomainSocket() {
            return this.socket;
        }
        
        Handler getHandler() {
            return this.handler;
        }
    }
    
    private static class FdSet
    {
        private long data;
        
        private static native long alloc0();
        
        FdSet() {
            this.data = alloc0();
        }
        
        native void add(final int p0);
        
        native void remove(final int p0);
        
        native int[] getAndClearReadableFds();
        
        native void close();
    }
    
    public interface Handler
    {
        boolean handle(final DomainSocket p0);
    }
}
