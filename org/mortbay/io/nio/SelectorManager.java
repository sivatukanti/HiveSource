// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.io.nio;

import java.util.Collection;
import java.util.Iterator;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.CancelledKeyException;
import org.mortbay.io.EndPoint;
import java.util.ArrayList;
import java.nio.channels.Selector;
import org.mortbay.thread.Timeout;
import java.util.List;
import org.mortbay.log.Log;
import org.mortbay.io.Connection;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import org.mortbay.component.AbstractLifeCycle;

public abstract class SelectorManager extends AbstractLifeCycle
{
    private static final int __JVMBUG_THRESHHOLD;
    private static final int __MONITOR_PERIOD;
    private static final int __MAX_SELECTS;
    private static final int __BUSY_PAUSE;
    private static final int __BUSY_KEY;
    private boolean _delaySelectKeyUpdate;
    private long _maxIdleTime;
    private long _lowResourcesConnections;
    private long _lowResourcesMaxIdleTime;
    private transient SelectSet[] _selectSet;
    private int _selectSets;
    private volatile int _set;
    
    public SelectorManager() {
        this._delaySelectKeyUpdate = true;
        this._selectSets = 1;
    }
    
    public void setMaxIdleTime(final long maxIdleTime) {
        this._maxIdleTime = maxIdleTime;
    }
    
    public void setSelectSets(final int selectSets) {
        final long lrc = this._lowResourcesConnections * this._selectSets;
        this._selectSets = selectSets;
        this._lowResourcesConnections = lrc / this._selectSets;
    }
    
    public long getMaxIdleTime() {
        return this._maxIdleTime;
    }
    
    public int getSelectSets() {
        return this._selectSets;
    }
    
    public boolean isDelaySelectKeyUpdate() {
        return this._delaySelectKeyUpdate;
    }
    
    public void register(final SocketChannel channel, final Object att) throws IOException {
        int s = this._set++;
        s %= this._selectSets;
        final SelectSet[] sets = this._selectSet;
        if (sets != null) {
            final SelectSet set = sets[s];
            set.addChange(channel, att);
            set.wakeup();
        }
    }
    
    public void register(final ServerSocketChannel acceptChannel) throws IOException {
        int s = this._set++;
        s %= this._selectSets;
        final SelectSet set = this._selectSet[s];
        set.addChange(acceptChannel);
        set.wakeup();
    }
    
    public long getLowResourcesConnections() {
        return this._lowResourcesConnections * this._selectSets;
    }
    
    public void setLowResourcesConnections(final long lowResourcesConnections) {
        this._lowResourcesConnections = (lowResourcesConnections + this._selectSets - 1L) / this._selectSets;
    }
    
    public long getLowResourcesMaxIdleTime() {
        return this._lowResourcesMaxIdleTime;
    }
    
    public void setLowResourcesMaxIdleTime(final long lowResourcesMaxIdleTime) {
        this._lowResourcesMaxIdleTime = lowResourcesMaxIdleTime;
    }
    
    public void doSelect(final int acceptorID) throws IOException {
        final SelectSet[] sets = this._selectSet;
        if (sets != null && sets.length > acceptorID && sets[acceptorID] != null) {
            sets[acceptorID].doSelect();
        }
    }
    
    public void setDelaySelectKeyUpdate(final boolean delaySelectKeyUpdate) {
        this._delaySelectKeyUpdate = delaySelectKeyUpdate;
    }
    
    protected abstract SocketChannel acceptChannel(final SelectionKey p0) throws IOException;
    
    public abstract boolean dispatch(final Runnable p0) throws IOException;
    
    protected void doStart() throws Exception {
        this._selectSet = new SelectSet[this._selectSets];
        for (int i = 0; i < this._selectSet.length; ++i) {
            this._selectSet[i] = new SelectSet(i);
        }
        super.doStart();
    }
    
    protected void doStop() throws Exception {
        final SelectSet[] sets = this._selectSet;
        this._selectSet = null;
        if (sets != null) {
            for (int i = 0; i < sets.length; ++i) {
                final SelectSet set = sets[i];
                if (set != null) {
                    set.stop();
                }
            }
        }
        super.doStop();
    }
    
    protected abstract void endPointClosed(final SelectChannelEndPoint p0);
    
    protected abstract void endPointOpened(final SelectChannelEndPoint p0);
    
    protected abstract Connection newConnection(final SocketChannel p0, final SelectChannelEndPoint p1);
    
    protected abstract SelectChannelEndPoint newEndPoint(final SocketChannel p0, final SelectSet p1, final SelectionKey p2) throws IOException;
    
    protected void connectionFailed(final SocketChannel channel, final Throwable ex, final Object attachment) {
        Log.warn(ex);
    }
    
    static {
        __JVMBUG_THRESHHOLD = Integer.getInteger("org.mortbay.io.nio.JVMBUG_THRESHHOLD", 512);
        __MONITOR_PERIOD = Integer.getInteger("org.mortbay.io.nio.MONITOR_PERIOD", 1000);
        __MAX_SELECTS = Integer.getInteger("org.mortbay.io.nio.MAX_SELECTS", 15000);
        __BUSY_PAUSE = Integer.getInteger("org.mortbay.io.nio.BUSY_PAUSE", 50);
        __BUSY_KEY = Integer.getInteger("org.mortbay.io.nio.BUSY_KEY", -1);
    }
    
    public class SelectSet
    {
        private transient int _change;
        private transient List[] _changes;
        private transient Timeout _idleTimeout;
        private transient int _nextSet;
        private transient Timeout _retryTimeout;
        private transient Selector _selector;
        private transient int _setID;
        private volatile boolean _selecting;
        private transient int _jvmBug;
        private int _selects;
        private long _monitorStart;
        private long _monitorNext;
        private boolean _pausing;
        private SelectionKey _busyKey;
        private int _busyKeyCount;
        private long _log;
        private int _paused;
        private int _jvmFix0;
        private int _jvmFix1;
        private int _jvmFix2;
        
        SelectSet(final int acceptorID) throws Exception {
            this._setID = acceptorID;
            (this._idleTimeout = new Timeout(this)).setDuration(SelectorManager.this.getMaxIdleTime());
            (this._retryTimeout = new Timeout(this)).setDuration(0L);
            this._selector = Selector.open();
            this._changes = new ArrayList[] { new ArrayList(), new ArrayList() };
            this._change = 0;
            this._monitorStart = System.currentTimeMillis();
            this._monitorNext = this._monitorStart + SelectorManager.__MONITOR_PERIOD;
            this._log = this._monitorStart + 60000L;
        }
        
        public void addChange(final Object point) {
            synchronized (this._changes) {
                this._changes[this._change].add(point);
            }
        }
        
        public void addChange(final SelectableChannel channel, final Object att) {
            if (att == null) {
                this.addChange(channel);
            }
            else if (att instanceof EndPoint) {
                this.addChange(att);
            }
            else {
                this.addChange(new ChangeSelectableChannel(channel, att));
            }
        }
        
        public void cancelIdle(final Timeout.Task task) {
            synchronized (this) {
                task.cancel();
            }
        }
        
        public void doSelect() throws IOException {
            SelectionKey key = null;
            try {
                final List changes;
                final Selector selector;
                synchronized (this._changes) {
                    changes = this._changes[this._change];
                    this._change = ((this._change == 0) ? 1 : 0);
                    this._selecting = true;
                    selector = this._selector;
                }
                try {
                    for (int i = 0; i < changes.size(); ++i) {
                        try {
                            final Object o = changes.get(i);
                            if (o instanceof EndPoint) {
                                final SelectChannelEndPoint endpoint = (SelectChannelEndPoint)o;
                                endpoint.doUpdateKey();
                            }
                            else if (o instanceof Runnable) {
                                SelectorManager.this.dispatch((Runnable)o);
                            }
                            else if (o instanceof ChangeSelectableChannel) {
                                final ChangeSelectableChannel asc = (ChangeSelectableChannel)o;
                                final SelectableChannel channel = asc._channel;
                                final Object att = asc._attachment;
                                if (channel instanceof SocketChannel && ((SocketChannel)channel).isConnected()) {
                                    key = channel.register(selector, 1, att);
                                    final SelectChannelEndPoint endpoint2 = SelectorManager.this.newEndPoint((SocketChannel)channel, this, key);
                                    key.attach(endpoint2);
                                    endpoint2.dispatch();
                                }
                                else if (channel.isOpen()) {
                                    channel.register(selector, 8, att);
                                }
                            }
                            else if (o instanceof SocketChannel) {
                                final SocketChannel channel2 = (SocketChannel)o;
                                if (channel2.isConnected()) {
                                    key = channel2.register(selector, 1, null);
                                    final SelectChannelEndPoint endpoint3 = SelectorManager.this.newEndPoint(channel2, this, key);
                                    key.attach(endpoint3);
                                    endpoint3.dispatch();
                                }
                                else if (channel2.isOpen()) {
                                    channel2.register(selector, 8, null);
                                }
                            }
                            else if (o instanceof ServerSocketChannel) {
                                final ServerSocketChannel channel3 = (ServerSocketChannel)o;
                                channel3.register(this.getSelector(), 16);
                            }
                            else {
                                if (!(o instanceof ChangeTask)) {
                                    throw new IllegalArgumentException(o.toString());
                                }
                                ((ChangeTask)o).run();
                            }
                        }
                        catch (Exception e) {
                            if (SelectorManager.this.isRunning()) {
                                Log.warn(e);
                            }
                            else {
                                Log.debug(e);
                            }
                        }
                        catch (Error e2) {
                            if (SelectorManager.this.isRunning()) {
                                Log.warn(e2);
                            }
                            else {
                                Log.debug(e2);
                            }
                        }
                    }
                }
                finally {
                    changes.clear();
                }
                long idle_next = 0L;
                long retry_next = 0L;
                long now = System.currentTimeMillis();
                synchronized (this) {
                    this._idleTimeout.setNow(now);
                    this._retryTimeout.setNow(now);
                    if (SelectorManager.this._lowResourcesConnections > 0L && selector.keys().size() > SelectorManager.this._lowResourcesConnections) {
                        this._idleTimeout.setDuration(SelectorManager.this._lowResourcesMaxIdleTime);
                    }
                    else {
                        this._idleTimeout.setDuration(SelectorManager.this._maxIdleTime);
                    }
                    idle_next = this._idleTimeout.getTimeToNext();
                    retry_next = this._retryTimeout.getTimeToNext();
                }
                long wait = 1000L;
                if (idle_next >= 0L && wait > idle_next) {
                    wait = idle_next;
                }
                if (wait > 0L && retry_next >= 0L && wait > retry_next) {
                    wait = retry_next;
                }
                if (wait > 2L) {
                    if (this._pausing) {
                        try {
                            Thread.sleep(SelectorManager.__BUSY_PAUSE);
                        }
                        catch (InterruptedException e3) {
                            Log.ignore(e3);
                        }
                    }
                    final long before = now;
                    final int selected = selector.select(wait);
                    now = System.currentTimeMillis();
                    this._idleTimeout.setNow(now);
                    this._retryTimeout.setNow(now);
                    ++this._selects;
                    if (now > this._monitorNext) {
                        this._selects = (int)(this._selects * SelectorManager.__MONITOR_PERIOD / (now - this._monitorStart));
                        this._pausing = (this._selects > SelectorManager.__MAX_SELECTS);
                        if (this._pausing) {
                            ++this._paused;
                        }
                        this._selects = 0;
                        this._jvmBug = 0;
                        this._monitorStart = now;
                        this._monitorNext = now + SelectorManager.__MONITOR_PERIOD;
                    }
                    if (now > this._log) {
                        if (this._paused > 0) {
                            Log.info(this + " Busy selector - injecting delay " + this._paused + " times");
                        }
                        if (this._jvmFix2 > 0) {
                            Log.info(this + " JVM BUG(s) - injecting delay" + this._jvmFix2 + " times");
                        }
                        if (this._jvmFix1 > 0) {
                            Log.info(this + " JVM BUG(s) - recreating selector " + this._jvmFix1 + " times, canceled keys " + this._jvmFix0 + " times");
                        }
                        else if (Log.isDebugEnabled() && this._jvmFix0 > 0) {
                            Log.info(this + " JVM BUG(s) - canceled keys " + this._jvmFix0 + " times");
                        }
                        this._paused = 0;
                        this._jvmFix2 = 0;
                        this._jvmFix1 = 0;
                        this._jvmFix0 = 0;
                        this._log = now + 60000L;
                    }
                    if (selected == 0 && wait > 10L && now - before < wait / 2L) {
                        ++this._jvmBug;
                        if (this._jvmBug > SelectorManager.__JVMBUG_THRESHHOLD) {
                            try {
                                if (this._jvmBug == SelectorManager.__JVMBUG_THRESHHOLD + 1) {
                                    ++this._jvmFix2;
                                }
                                Thread.sleep(SelectorManager.__BUSY_PAUSE);
                            }
                            catch (InterruptedException e4) {
                                Log.ignore(e4);
                            }
                        }
                        else {
                            if (this._jvmBug == SelectorManager.__JVMBUG_THRESHHOLD) {
                                synchronized (this) {
                                    ++this._jvmFix1;
                                    final Selector new_selector = Selector.open();
                                    for (final SelectionKey k : this._selector.keys()) {
                                        if (k.isValid()) {
                                            if (k.interestOps() == 0) {
                                                continue;
                                            }
                                            final SelectableChannel channel4 = k.channel();
                                            final Object attachment = k.attachment();
                                            if (attachment == null) {
                                                this.addChange(channel4);
                                            }
                                            else {
                                                this.addChange(channel4, attachment);
                                            }
                                        }
                                    }
                                    final Selector old_selector = this._selector;
                                    this._selector = new_selector;
                                    try {
                                        old_selector.close();
                                    }
                                    catch (Exception e5) {
                                        Log.warn(e5);
                                    }
                                    return;
                                }
                            }
                            if (this._jvmBug % 32 == 31) {
                                int cancelled = 0;
                                for (final SelectionKey j : selector.keys()) {
                                    if (j.isValid() && j.interestOps() == 0) {
                                        j.cancel();
                                        ++cancelled;
                                    }
                                }
                                if (cancelled > 0) {
                                    ++this._jvmFix0;
                                }
                                return;
                            }
                        }
                    }
                    else if (SelectorManager.__BUSY_KEY > 0 && selected == 1 && this._selects > SelectorManager.__MAX_SELECTS) {
                        final SelectionKey busy = selector.selectedKeys().iterator().next();
                        if (busy == this._busyKey) {
                            if (++this._busyKeyCount > SelectorManager.__BUSY_KEY && !(busy.channel() instanceof ServerSocketChannel)) {
                                final SelectChannelEndPoint endpoint4 = (SelectChannelEndPoint)busy.attachment();
                                Log.warn("Busy Key " + busy.channel() + " " + endpoint4);
                                busy.cancel();
                                if (endpoint4 != null) {
                                    SelectorManager.this.dispatch(new Runnable() {
                                        public void run() {
                                            try {
                                                endpoint4.close();
                                            }
                                            catch (IOException e) {
                                                Log.ignore(e);
                                            }
                                        }
                                    });
                                }
                            }
                        }
                        else {
                            this._busyKeyCount = 0;
                        }
                        this._busyKey = busy;
                    }
                }
                else {
                    selector.selectNow();
                    ++this._selects;
                }
                if (this._selector == null || !selector.isOpen()) {
                    return;
                }
                final Iterator iter2 = selector.selectedKeys().iterator();
                while (iter2.hasNext()) {
                    key = iter2.next();
                    try {
                        if (!key.isValid()) {
                            key.cancel();
                            final SelectChannelEndPoint endpoint5 = (SelectChannelEndPoint)key.attachment();
                            if (endpoint5 == null) {
                                continue;
                            }
                            endpoint5.doUpdateKey();
                        }
                        else {
                            final Object att2 = key.attachment();
                            if (att2 instanceof SelectChannelEndPoint) {
                                final SelectChannelEndPoint endpoint6 = (SelectChannelEndPoint)att2;
                                endpoint6.dispatch();
                            }
                            else if (key.isAcceptable()) {
                                final SocketChannel channel5 = SelectorManager.this.acceptChannel(key);
                                if (channel5 == null) {
                                    continue;
                                }
                                channel5.configureBlocking(false);
                                this._nextSet = ++this._nextSet % SelectorManager.this._selectSet.length;
                                if (this._nextSet == this._setID) {
                                    final SelectionKey cKey = channel5.register(SelectorManager.this._selectSet[this._nextSet].getSelector(), 1);
                                    final SelectChannelEndPoint endpoint4 = SelectorManager.this.newEndPoint(channel5, SelectorManager.this._selectSet[this._nextSet], cKey);
                                    cKey.attach(endpoint4);
                                    if (endpoint4 != null) {
                                        endpoint4.dispatch();
                                    }
                                }
                                else {
                                    SelectorManager.this._selectSet[this._nextSet].addChange(channel5);
                                    SelectorManager.this._selectSet[this._nextSet].wakeup();
                                }
                            }
                            else if (key.isConnectable()) {
                                final SocketChannel channel5 = (SocketChannel)key.channel();
                                boolean connected = false;
                                try {
                                    connected = channel5.finishConnect();
                                }
                                catch (Exception e6) {
                                    SelectorManager.this.connectionFailed(channel5, e6, att2);
                                }
                                finally {
                                    if (connected) {
                                        key.interestOps(1);
                                        final SelectChannelEndPoint endpoint7 = SelectorManager.this.newEndPoint(channel5, this, key);
                                        key.attach(endpoint7);
                                        endpoint7.dispatch();
                                    }
                                    else {
                                        key.cancel();
                                    }
                                }
                            }
                            else {
                                final SocketChannel channel5 = (SocketChannel)key.channel();
                                final SelectChannelEndPoint endpoint8 = SelectorManager.this.newEndPoint(channel5, this, key);
                                key.attach(endpoint8);
                                if (key.isReadable()) {
                                    endpoint8.dispatch();
                                }
                            }
                            key = null;
                        }
                    }
                    catch (CancelledKeyException e7) {
                        Log.ignore(e7);
                    }
                    catch (Exception e8) {
                        if (SelectorManager.this.isRunning()) {
                            Log.warn(e8);
                        }
                        else {
                            Log.ignore(e8);
                        }
                        if (key == null || key.channel() instanceof ServerSocketChannel || !key.isValid()) {
                            continue;
                        }
                        key.interestOps(0);
                        key.cancel();
                    }
                }
                selector.selectedKeys().clear();
                this._idleTimeout.tick(now);
                this._retryTimeout.tick(now);
            }
            catch (ClosedSelectorException e9) {
                Log.warn(e9);
            }
            catch (CancelledKeyException e10) {
                Log.ignore(e10);
            }
            finally {
                this._selecting = false;
            }
        }
        
        public SelectorManager getManager() {
            return SelectorManager.this;
        }
        
        public long getNow() {
            return this._idleTimeout.getNow();
        }
        
        public void scheduleIdle(final Timeout.Task task) {
            synchronized (this) {
                if (this._idleTimeout.getDuration() <= 0L) {
                    return;
                }
                task.schedule(this._idleTimeout);
            }
        }
        
        public void scheduleTimeout(final Timeout.Task task, final long timeout) {
            synchronized (this) {
                this._retryTimeout.schedule(task, timeout);
            }
        }
        
        public void wakeup() {
            final Selector selector = this._selector;
            if (selector != null) {
                selector.wakeup();
            }
        }
        
        Selector getSelector() {
            return this._selector;
        }
        
        void stop() throws Exception {
            for (boolean selecting = true; selecting; selecting = this._selecting) {
                this.wakeup();
            }
            final ArrayList keys = new ArrayList((Collection<? extends E>)this._selector.keys());
            for (final SelectionKey key : keys) {
                if (key == null) {
                    continue;
                }
                final Object att = key.attachment();
                if (!(att instanceof EndPoint)) {
                    continue;
                }
                final EndPoint endpoint = (EndPoint)att;
                try {
                    endpoint.close();
                }
                catch (IOException e) {
                    Log.ignore(e);
                }
            }
            synchronized (this) {
                for (boolean selecting = this._selecting; selecting; selecting = this._selecting) {
                    this.wakeup();
                }
                this._idleTimeout.cancelAll();
                this._retryTimeout.cancelAll();
                try {
                    if (this._selector != null) {
                        this._selector.close();
                    }
                }
                catch (IOException e2) {
                    Log.ignore(e2);
                }
                this._selector = null;
            }
        }
    }
    
    private static class ChangeSelectableChannel
    {
        final SelectableChannel _channel;
        final Object _attachment;
        
        public ChangeSelectableChannel(final SelectableChannel channel, final Object attachment) {
            this._channel = channel;
            this._attachment = attachment;
        }
    }
    
    private interface ChangeTask
    {
        void run();
    }
}
