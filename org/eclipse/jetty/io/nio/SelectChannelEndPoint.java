// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io.nio;

import org.eclipse.jetty.util.log.Log;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.util.thread.Timeout;
import org.eclipse.jetty.io.Connection;
import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.io.ConnectedEndPoint;
import org.eclipse.jetty.io.AsyncEndPoint;

public class SelectChannelEndPoint extends ChannelEndPoint implements AsyncEndPoint, ConnectedEndPoint
{
    public static final Logger LOG;
    private final SelectorManager.SelectSet _selectSet;
    private final SelectorManager _manager;
    private SelectionKey _key;
    private final Runnable _handler;
    private int _interestOps;
    private volatile AsyncConnection _connection;
    private boolean _dispatched;
    private boolean _asyncDispatch;
    private volatile boolean _writable;
    private boolean _readBlocked;
    private boolean _writeBlocked;
    private boolean _open;
    private volatile long _idleTimestamp;
    private boolean _ishut;
    
    public SelectChannelEndPoint(final SocketChannel channel, final SelectorManager.SelectSet selectSet, final SelectionKey key, final int maxIdleTime) throws IOException {
        super(channel, maxIdleTime);
        this._handler = new Runnable() {
            public void run() {
                SelectChannelEndPoint.this.handle();
            }
        };
        this._dispatched = false;
        this._asyncDispatch = false;
        this._writable = true;
        this._manager = selectSet.getManager();
        this._selectSet = selectSet;
        this._dispatched = false;
        this._asyncDispatch = false;
        this._open = true;
        this._key = key;
        this.setCheckForIdle(true);
    }
    
    public SelectionKey getSelectionKey() {
        synchronized (this) {
            return this._key;
        }
    }
    
    public SelectorManager getSelectManager() {
        return this._manager;
    }
    
    public Connection getConnection() {
        return this._connection;
    }
    
    public void setConnection(final Connection connection) {
        final Connection old = this._connection;
        this._connection = (AsyncConnection)connection;
        if (old != null && old != this._connection) {
            this._manager.endPointUpgraded(this, old);
        }
    }
    
    public long getIdleTimestamp() {
        return this._idleTimestamp;
    }
    
    public void schedule() {
        synchronized (this) {
            if (this._key == null || !this._key.isValid()) {
                this._readBlocked = false;
                this._writeBlocked = false;
                this.notifyAll();
                return;
            }
            if (this._readBlocked || this._writeBlocked) {
                if (this._readBlocked && this._key.isReadable()) {
                    this._readBlocked = false;
                }
                if (this._writeBlocked && this._key.isWritable()) {
                    this._writeBlocked = false;
                }
                this.notifyAll();
                this._key.interestOps(0);
                if (!this._dispatched) {
                    this.updateKey();
                }
                return;
            }
            if ((this._key.readyOps() & 0x4) == 0x4 && (this._key.interestOps() & 0x4) == 0x4) {
                this._interestOps = (this._key.interestOps() & 0xFFFFFFFB);
                this._key.interestOps(this._interestOps);
                this._writable = true;
            }
            if (this._dispatched) {
                this._key.interestOps(0);
            }
            else {
                this.dispatch();
                if (this._dispatched && !this._selectSet.getManager().isDeferringInterestedOps0()) {
                    this._key.interestOps(0);
                }
            }
        }
    }
    
    public void asyncDispatch() {
        synchronized (this) {
            if (this._dispatched) {
                this._asyncDispatch = true;
            }
            else {
                this.dispatch();
            }
        }
    }
    
    public void dispatch() {
        synchronized (this) {
            if (this._dispatched) {
                throw new IllegalStateException("dispatched");
            }
            this._dispatched = true;
            final boolean dispatched = this._manager.dispatch(this._handler);
            if (!dispatched) {
                this._dispatched = false;
                SelectChannelEndPoint.LOG.warn("Dispatched Failed! " + this + " to " + this._manager, new Object[0]);
                this.updateKey();
            }
        }
    }
    
    protected boolean undispatch() {
        synchronized (this) {
            if (this._asyncDispatch) {
                return this._asyncDispatch = false;
            }
            this._dispatched = false;
            this.updateKey();
        }
        return true;
    }
    
    public void cancelTimeout(final Timeout.Task task) {
        this.getSelectSet().cancelTimeout(task);
    }
    
    public void scheduleTimeout(final Timeout.Task task, final long timeoutMs) {
        this.getSelectSet().scheduleTimeout(task, timeoutMs);
    }
    
    public void setCheckForIdle(final boolean check) {
        this._idleTimestamp = (check ? System.currentTimeMillis() : 0L);
    }
    
    public boolean isCheckForIdle() {
        return this._idleTimestamp != 0L;
    }
    
    protected void notIdle() {
        if (this._idleTimestamp != 0L) {
            this._idleTimestamp = System.currentTimeMillis();
        }
    }
    
    public void checkIdleTimestamp(final long now) {
        final long idleTimestamp = this._idleTimestamp;
        if (idleTimestamp != 0L && this._maxIdleTime > 0) {
            final long idleForMs = now - idleTimestamp;
            if (idleForMs > this._maxIdleTime) {
                this.onIdleExpired(idleForMs);
                this._idleTimestamp = now;
            }
        }
    }
    
    public void onIdleExpired(final long idleForMs) {
        this._connection.onIdleExpired(idleForMs);
    }
    
    @Override
    public int fill(final Buffer buffer) throws IOException {
        final int fill = super.fill(buffer);
        if (fill > 0) {
            this.notIdle();
        }
        return fill;
    }
    
    @Override
    public int flush(final Buffer header, final Buffer buffer, final Buffer trailer) throws IOException {
        final int l = super.flush(header, buffer, trailer);
        if (l == 0 && ((header != null && header.hasContent()) || (buffer != null && buffer.hasContent()) || (trailer != null && trailer.hasContent()))) {
            synchronized (this) {
                if (this._dispatched) {
                    this._writable = false;
                }
            }
        }
        else if (l > 0) {
            this._writable = true;
            this.notIdle();
        }
        return l;
    }
    
    @Override
    public int flush(final Buffer buffer) throws IOException {
        final int l = super.flush(buffer);
        if (l == 0 && buffer != null && buffer.hasContent()) {
            synchronized (this) {
                if (this._dispatched) {
                    this._writable = false;
                }
            }
        }
        else if (l > 0) {
            this._writable = true;
            this.notIdle();
        }
        return l;
    }
    
    @Override
    public boolean blockReadable(final long timeoutMs) throws IOException {
        synchronized (this) {
            if (this.isInputShutdown()) {
                throw new EofException();
            }
            long now = this._selectSet.getNow();
            final long end = now + timeoutMs;
            final boolean check = this.isCheckForIdle();
            this.setCheckForIdle(true);
            try {
                this._readBlocked = true;
                while (!this.isInputShutdown() && this._readBlocked) {
                    try {
                        this.updateKey();
                        this.wait((timeoutMs >= 0L) ? (end - now) : 10000L);
                    }
                    catch (InterruptedException e) {
                        SelectChannelEndPoint.LOG.warn(e);
                    }
                    finally {
                        now = this._selectSet.getNow();
                    }
                    if (this._readBlocked && timeoutMs > 0L && now >= end) {
                        return false;
                    }
                }
            }
            finally {
                this._readBlocked = false;
                this.setCheckForIdle(check);
            }
        }
        return true;
    }
    
    @Override
    public boolean blockWritable(final long timeoutMs) throws IOException {
        synchronized (this) {
            if (this.isOutputShutdown()) {
                throw new EofException();
            }
            long now = this._selectSet.getNow();
            final long end = now + timeoutMs;
            final boolean check = this.isCheckForIdle();
            this.setCheckForIdle(true);
            try {
                this._writeBlocked = true;
                while (this._writeBlocked && !this.isOutputShutdown()) {
                    try {
                        this.updateKey();
                        this.wait((timeoutMs >= 0L) ? (end - now) : 10000L);
                    }
                    catch (InterruptedException e) {
                        SelectChannelEndPoint.LOG.warn(e);
                    }
                    finally {
                        now = this._selectSet.getNow();
                    }
                    if (this._writeBlocked && timeoutMs > 0L && now >= end) {
                        return false;
                    }
                }
            }
            finally {
                this._writeBlocked = false;
                this.setCheckForIdle(check);
            }
        }
        return true;
    }
    
    public void scheduleWrite() {
        if (this._writable) {
            SelectChannelEndPoint.LOG.debug("Required scheduleWrite {}", this);
        }
        this._writable = false;
        this.updateKey();
    }
    
    public boolean isWritable() {
        return this._writable;
    }
    
    public boolean hasProgressed() {
        return false;
    }
    
    private void updateKey() {
        final boolean changed;
        synchronized (this) {
            int current_ops = -1;
            if (this.getChannel().isOpen()) {
                final boolean read_interest = this._readBlocked || (!this._dispatched && !this._connection.isSuspended());
                final boolean write_interest = this._writeBlocked || (!this._dispatched && !this._writable);
                this._interestOps = (((!this._socket.isInputShutdown() && read_interest) ? 1 : 0) | ((!this._socket.isOutputShutdown() && write_interest) ? 4 : 0));
                try {
                    current_ops = ((this._key != null && this._key.isValid()) ? this._key.interestOps() : -1);
                }
                catch (Exception e) {
                    this._key = null;
                    SelectChannelEndPoint.LOG.ignore(e);
                }
            }
            changed = (this._interestOps != current_ops);
        }
        if (changed) {
            this._selectSet.addChange(this);
            this._selectSet.wakeup();
        }
    }
    
    void doUpdateKey() {
        synchronized (this) {
            if (this.getChannel().isOpen()) {
                if (this._interestOps > 0) {
                    if (this._key == null || !this._key.isValid()) {
                        final SelectableChannel sc = (SelectableChannel)this.getChannel();
                        if (sc.isRegistered()) {
                            this.updateKey();
                        }
                        else {
                            try {
                                this._key = ((SelectableChannel)this.getChannel()).register(this._selectSet.getSelector(), this._interestOps, this);
                            }
                            catch (Exception e) {
                                SelectChannelEndPoint.LOG.ignore(e);
                                if (this._key != null && this._key.isValid()) {
                                    this._key.cancel();
                                }
                                if (this._open) {
                                    this._selectSet.destroyEndPoint(this);
                                }
                                this._open = false;
                                this._key = null;
                            }
                        }
                    }
                    else {
                        this._key.interestOps(this._interestOps);
                    }
                }
                else if (this._key != null && this._key.isValid()) {
                    this._key.interestOps(0);
                }
                else {
                    this._key = null;
                }
            }
            else {
                if (this._key != null && this._key.isValid()) {
                    this._key.cancel();
                }
                if (this._open) {
                    this._open = false;
                    this._selectSet.destroyEndPoint(this);
                }
                this._key = null;
            }
        }
    }
    
    protected void handle() {
        boolean dispatched = true;
        try {
            while (dispatched) {
                try {
                    while (true) {
                        final AsyncConnection next = (AsyncConnection)this._connection.handle();
                        if (next == this._connection) {
                            break;
                        }
                        SelectChannelEndPoint.LOG.debug("{} replaced {}", next, this._connection);
                        final Connection old = this._connection;
                        this._connection = next;
                        this._manager.endPointUpgraded(this, old);
                    }
                }
                catch (ClosedChannelException e) {
                    SelectChannelEndPoint.LOG.ignore(e);
                }
                catch (EofException e2) {
                    SelectChannelEndPoint.LOG.debug("EOF", e2);
                    try {
                        this.close();
                    }
                    catch (IOException e3) {
                        SelectChannelEndPoint.LOG.ignore(e3);
                    }
                }
                catch (IOException e4) {
                    SelectChannelEndPoint.LOG.warn(e4.toString(), new Object[0]);
                    try {
                        this.close();
                    }
                    catch (IOException e3) {
                        SelectChannelEndPoint.LOG.ignore(e3);
                    }
                }
                catch (Throwable e5) {
                    SelectChannelEndPoint.LOG.warn("handle failed", e5);
                    try {
                        this.close();
                    }
                    catch (IOException e3) {
                        SelectChannelEndPoint.LOG.ignore(e3);
                    }
                }
                finally {
                    if (!this._ishut && this.isInputShutdown() && this.isOpen()) {
                        this._ishut = true;
                        try {
                            this._connection.onInputShutdown();
                        }
                        catch (Throwable x) {
                            SelectChannelEndPoint.LOG.warn("onInputShutdown failed", x);
                            try {
                                this.close();
                            }
                            catch (IOException e6) {
                                SelectChannelEndPoint.LOG.ignore(e6);
                            }
                            this.updateKey();
                        }
                        finally {
                            this.updateKey();
                        }
                    }
                    dispatched = !this.undispatch();
                }
            }
            if (dispatched) {
                for (dispatched = !this.undispatch(); dispatched; dispatched = !this.undispatch()) {
                    SelectChannelEndPoint.LOG.warn("SCEP.run() finally DISPATCHED", new Object[0]);
                }
            }
        }
        finally {
            if (dispatched) {
                for (dispatched = !this.undispatch(); dispatched; dispatched = !this.undispatch()) {
                    SelectChannelEndPoint.LOG.warn("SCEP.run() finally DISPATCHED", new Object[0]);
                }
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        try {
            super.close();
        }
        catch (IOException e) {
            SelectChannelEndPoint.LOG.ignore(e);
        }
        finally {
            this.updateKey();
        }
    }
    
    @Override
    public String toString() {
        final SelectionKey key = this._key;
        String keyString = "";
        if (key != null) {
            if (key.isValid()) {
                if (key.isReadable()) {
                    keyString += "r";
                }
                if (key.isWritable()) {
                    keyString += "w";
                }
            }
            else {
                keyString += "!";
            }
        }
        else {
            keyString += "-";
        }
        return String.format("SCEP@%x{l(%s)<->r(%s),d=%b,open=%b,ishut=%b,oshut=%b,rb=%b,wb=%b,w=%b,i=%d%s}-{%s}", this.hashCode(), this._socket.getRemoteSocketAddress(), this._socket.getLocalSocketAddress(), this._dispatched, this.isOpen(), this.isInputShutdown(), this.isOutputShutdown(), this._readBlocked, this._writeBlocked, this._writable, this._interestOps, keyString, this._connection);
    }
    
    public SelectorManager.SelectSet getSelectSet() {
        return this._selectSet;
    }
    
    @Override
    public void setMaxIdleTime(final int timeMs) throws IOException {
        this._maxIdleTime = timeMs;
    }
    
    static {
        LOG = Log.getLogger("org.eclipse.jetty.io.nio");
    }
}
