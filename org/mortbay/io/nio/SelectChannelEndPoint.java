// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io.nio;

import org.mortbay.jetty.HttpException;
import org.mortbay.jetty.EofException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import org.mortbay.io.Buffer;
import java.io.IOException;
import org.mortbay.log.Log;
import java.nio.channels.ByteChannel;
import java.nio.channels.SocketChannel;
import org.mortbay.thread.Timeout;
import org.mortbay.io.Connection;
import java.nio.channels.SelectionKey;

public class SelectChannelEndPoint extends ChannelEndPoint implements Runnable
{
    protected SelectorManager _manager;
    protected SelectorManager.SelectSet _selectSet;
    protected boolean _dispatched;
    protected boolean _writable;
    protected SelectionKey _key;
    protected int _interestOps;
    protected boolean _readBlocked;
    protected boolean _writeBlocked;
    protected Connection _connection;
    private Timeout.Task _timeoutTask;
    
    public Connection getConnection() {
        return this._connection;
    }
    
    public SelectChannelEndPoint(final SocketChannel channel, final SelectorManager.SelectSet selectSet, final SelectionKey key) {
        super(channel);
        this._dispatched = false;
        this._writable = true;
        this._timeoutTask = new IdleTask();
        this._manager = selectSet.getManager();
        this._selectSet = selectSet;
        this._connection = this._manager.newConnection(channel, this);
        this._manager.endPointOpened(this);
        this._key = key;
    }
    
    void dispatch() throws IOException {
        boolean dispatch_done = true;
        try {
            if (this.dispatch(this._manager.isDelaySelectKeyUpdate())) {
                dispatch_done = false;
                dispatch_done = this._manager.dispatch(this);
            }
        }
        finally {
            if (!dispatch_done) {
                Log.warn("dispatch failed!");
                this.undispatch();
            }
        }
    }
    
    public boolean dispatch(final boolean assumeShortDispatch) throws IOException {
        synchronized (this) {
            if (this._key == null || !this._key.isValid()) {
                this._readBlocked = false;
                this._writeBlocked = false;
                this.notifyAll();
                return false;
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
                return false;
            }
            if (!assumeShortDispatch) {
                this._key.interestOps(0);
            }
            if (this._dispatched) {
                this._key.interestOps(0);
                return false;
            }
            if ((this._key.readyOps() & 0x4) == 0x4 && (this._key.interestOps() & 0x4) == 0x4) {
                this._interestOps = (this._key.interestOps() & 0xFFFFFFFB);
                this._key.interestOps(this._interestOps);
                this._writable = true;
            }
            this._dispatched = true;
        }
        return true;
    }
    
    public void scheduleIdle() {
        this._selectSet.scheduleIdle(this._timeoutTask);
    }
    
    public void cancelIdle() {
        this._selectSet.cancelIdle(this._timeoutTask);
    }
    
    protected void idleExpired() {
        try {
            this.close();
        }
        catch (IOException e) {
            Log.ignore(e);
        }
    }
    
    public void undispatch() {
        synchronized (this) {
            try {
                this._dispatched = false;
                this.updateKey();
            }
            catch (Exception e) {
                Log.ignore(e);
                this._interestOps = -1;
                this._selectSet.addChange(this);
            }
        }
    }
    
    public int flush(final Buffer header, final Buffer buffer, final Buffer trailer) throws IOException {
        final int l = super.flush(header, buffer, trailer);
        this._writable = (l > 0);
        return l;
    }
    
    public int flush(final Buffer buffer) throws IOException {
        final int l = super.flush(buffer);
        this._writable = (l > 0);
        return l;
    }
    
    public boolean blockReadable(final long timeoutMs) throws IOException {
        synchronized (this) {
            final long start = this._selectSet.getNow();
            try {
                this._readBlocked = true;
                while (this.isOpen() && this._readBlocked) {
                    try {
                        this.updateKey();
                        this.wait(timeoutMs);
                        if (this._readBlocked && timeoutMs < this._selectSet.getNow() - start) {
                            return false;
                        }
                        continue;
                    }
                    catch (InterruptedException e) {
                        Log.warn(e);
                    }
                }
            }
            finally {
                this._readBlocked = false;
            }
        }
        return true;
    }
    
    public boolean blockWritable(final long timeoutMs) throws IOException {
        synchronized (this) {
            final long start = this._selectSet.getNow();
            try {
                this._writeBlocked = true;
                while (this.isOpen() && this._writeBlocked) {
                    try {
                        this.updateKey();
                        this.wait(timeoutMs);
                        if (this._writeBlocked && timeoutMs < this._selectSet.getNow() - start) {
                            return false;
                        }
                        continue;
                    }
                    catch (InterruptedException e) {
                        Log.warn(e);
                    }
                }
            }
            finally {
                this._writeBlocked = false;
                this.scheduleIdle();
            }
        }
        return true;
    }
    
    public void setWritable(final boolean writable) {
        this._writable = writable;
    }
    
    public void scheduleWrite() {
        this._writable = false;
        this.updateKey();
    }
    
    private void updateKey() {
        synchronized (this) {
            int ops = -1;
            if (this.getChannel().isOpen()) {
                ops = ((this._key != null && this._key.isValid()) ? this._key.interestOps() : -1);
                this._interestOps = (((!this._dispatched || this._readBlocked) ? 1 : 0) | ((!this._writable || this._writeBlocked) ? 4 : 0));
            }
            if (this._interestOps == ops && this.getChannel().isOpen()) {
                return;
            }
        }
        this._selectSet.addChange(this);
        this._selectSet.wakeup();
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
                                Log.ignore(e);
                                if (this._key != null && this._key.isValid()) {
                                    this._key.cancel();
                                }
                                this.cancelIdle();
                                this._manager.endPointClosed(this);
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
                    this._key.interestOps(0);
                    this._key.cancel();
                }
                this.cancelIdle();
                this._manager.endPointClosed(this);
                this._key = null;
            }
        }
    }
    
    public void run() {
        try {
            this._connection.handle();
        }
        catch (ClosedChannelException e) {
            Log.ignore(e);
        }
        catch (EofException e2) {
            Log.debug("EOF", e2);
            try {
                this.close();
            }
            catch (IOException e3) {
                Log.ignore(e3);
            }
        }
        catch (HttpException e4) {
            Log.debug("BAD", e4);
            try {
                this.close();
            }
            catch (IOException e3) {
                Log.ignore(e3);
            }
        }
        catch (Throwable e5) {
            Log.warn("handle failed", e5);
            try {
                this.close();
            }
            catch (IOException e3) {
                Log.ignore(e3);
            }
        }
        finally {
            this.undispatch();
        }
    }
    
    public void close() throws IOException {
        try {
            super.close();
        }
        catch (IOException e) {
            Log.ignore(e);
        }
        finally {
            this.updateKey();
        }
    }
    
    public String toString() {
        return "SCEP@" + this.hashCode() + "[d=" + this._dispatched + ",io=" + this._interestOps + ",w=" + this._writable + ",b=" + this._readBlocked + "|" + this._writeBlocked + "]";
    }
    
    public Timeout.Task getTimeoutTask() {
        return this._timeoutTask;
    }
    
    public SelectorManager.SelectSet getSelectSet() {
        return this._selectSet;
    }
    
    public class IdleTask extends Timeout.Task
    {
        public void expired() {
            SelectChannelEndPoint.this.idleExpired();
        }
        
        public String toString() {
            return "TimeoutTask:" + SelectChannelEndPoint.this.toString();
        }
    }
}
