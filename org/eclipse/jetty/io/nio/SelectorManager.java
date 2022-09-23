// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io.nio;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.ArrayList;
import java.nio.channels.Channel;
import java.nio.channels.ClosedSelectorException;
import java.util.Iterator;
import java.nio.channels.CancelledKeyException;
import org.eclipse.jetty.io.EndPoint;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.nio.channels.Selector;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.eclipse.jetty.util.thread.Timeout;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.TypeUtil;
import java.util.Collection;
import org.eclipse.jetty.util.component.AggregateLifeCycle;
import java.nio.channels.SelectionKey;
import org.eclipse.jetty.io.AsyncEndPoint;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.ConnectedEndPoint;
import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public abstract class SelectorManager extends AbstractLifeCycle implements Dumpable
{
    public static final Logger LOG;
    private static final int __MONITOR_PERIOD;
    private static final int __MAX_SELECTS;
    private static final int __BUSY_PAUSE;
    private static final int __IDLE_TICK;
    private int _maxIdleTime;
    private int _lowResourcesMaxIdleTime;
    private long _lowResourcesConnections;
    private SelectSet[] _selectSet;
    private int _selectSets;
    private volatile int _set;
    private boolean _deferringInterestedOps0;
    private int _selectorPriorityDelta;
    
    public SelectorManager() {
        this._selectSets = 1;
        this._set = 0;
        this._deferringInterestedOps0 = true;
        this._selectorPriorityDelta = 0;
    }
    
    public void setMaxIdleTime(final long maxIdleTime) {
        this._maxIdleTime = (int)maxIdleTime;
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
    
    public SelectSet getSelectSet(final int i) {
        return this._selectSet[i];
    }
    
    public void register(final SocketChannel channel, final Object att) {
        int s = this._set++;
        if (s < 0) {
            s = -s;
        }
        s %= this._selectSets;
        final SelectSet[] sets = this._selectSet;
        if (sets != null) {
            final SelectSet set = sets[s];
            set.addChange(channel, att);
            set.wakeup();
        }
    }
    
    public void register(final SocketChannel channel) {
        int s = this._set++;
        if (s < 0) {
            s = -s;
        }
        s %= this._selectSets;
        final SelectSet[] sets = this._selectSet;
        if (sets != null) {
            final SelectSet set = sets[s];
            set.addChange(channel);
            set.wakeup();
        }
    }
    
    public void register(final ServerSocketChannel acceptChannel) {
        int s = this._set++;
        if (s < 0) {
            s = -s;
        }
        s %= this._selectSets;
        final SelectSet set = this._selectSet[s];
        set.addChange(acceptChannel);
        set.wakeup();
    }
    
    public int getSelectorPriorityDelta() {
        return this._selectorPriorityDelta;
    }
    
    public void setSelectorPriorityDelta(final int delta) {
        this._selectorPriorityDelta = delta;
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
        this._lowResourcesMaxIdleTime = (int)lowResourcesMaxIdleTime;
    }
    
    public abstract boolean dispatch(final Runnable p0);
    
    @Override
    protected void doStart() throws Exception {
        this._selectSet = new SelectSet[this._selectSets];
        for (int i = 0; i < this._selectSet.length; ++i) {
            this._selectSet[i] = new SelectSet(i);
        }
        super.doStart();
        for (int i = 0; i < this.getSelectSets(); ++i) {
            final int id = i;
            final boolean selecting = this.dispatch(new Runnable() {
                public void run() {
                    final String name = Thread.currentThread().getName();
                    final int priority = Thread.currentThread().getPriority();
                    try {
                        final SelectSet[] sets = SelectorManager.this._selectSet;
                        if (sets == null) {
                            return;
                        }
                        final SelectSet set = sets[id];
                        Thread.currentThread().setName(name + " Selector" + id);
                        if (SelectorManager.this.getSelectorPriorityDelta() != 0) {
                            Thread.currentThread().setPriority(Thread.currentThread().getPriority() + SelectorManager.this.getSelectorPriorityDelta());
                        }
                        SelectorManager.LOG.debug("Starting {} on {}", Thread.currentThread(), this);
                        while (SelectorManager.this.isRunning()) {
                            try {
                                set.doSelect();
                            }
                            catch (IOException e) {
                                SelectorManager.LOG.ignore(e);
                            }
                            catch (Exception e2) {
                                SelectorManager.LOG.warn(e2);
                            }
                        }
                    }
                    finally {
                        SelectorManager.LOG.debug("Stopped {} on {}", Thread.currentThread(), this);
                        Thread.currentThread().setName(name);
                        if (SelectorManager.this.getSelectorPriorityDelta() != 0) {
                            Thread.currentThread().setPriority(priority);
                        }
                    }
                }
            });
            if (!selecting) {
                throw new IllegalStateException("!Selecting");
            }
        }
    }
    
    @Override
    protected void doStop() throws Exception {
        final SelectSet[] sets = this._selectSet;
        this._selectSet = null;
        if (sets != null) {
            for (final SelectSet set : sets) {
                if (set != null) {
                    set.stop();
                }
            }
        }
        super.doStop();
    }
    
    protected abstract void endPointClosed(final SelectChannelEndPoint p0);
    
    protected abstract void endPointOpened(final SelectChannelEndPoint p0);
    
    protected abstract void endPointUpgraded(final ConnectedEndPoint p0, final Connection p1);
    
    public abstract AsyncConnection newConnection(final SocketChannel p0, final AsyncEndPoint p1, final Object p2);
    
    protected abstract SelectChannelEndPoint newEndPoint(final SocketChannel p0, final SelectSet p1, final SelectionKey p2) throws IOException;
    
    protected void connectionFailed(final SocketChannel channel, final Throwable ex, final Object attachment) {
        SelectorManager.LOG.warn(ex + "," + channel + "," + attachment, new Object[0]);
        SelectorManager.LOG.debug(ex);
    }
    
    public String dump() {
        return AggregateLifeCycle.dump(this);
    }
    
    public void dump(final Appendable out, final String indent) throws IOException {
        AggregateLifeCycle.dumpObject(out, this);
        AggregateLifeCycle.dump(out, indent, TypeUtil.asList(this._selectSet));
    }
    
    public boolean isDeferringInterestedOps0() {
        return this._deferringInterestedOps0;
    }
    
    public void setDeferringInterestedOps0(final boolean deferringInterestedOps0) {
        this._deferringInterestedOps0 = deferringInterestedOps0;
    }
    
    static {
        LOG = Log.getLogger("org.eclipse.jetty.io.nio");
        __MONITOR_PERIOD = Integer.getInteger("org.eclipse.jetty.io.nio.MONITOR_PERIOD", 1000);
        __MAX_SELECTS = Integer.getInteger("org.eclipse.jetty.io.nio.MAX_SELECTS", 100000);
        __BUSY_PAUSE = Integer.getInteger("org.eclipse.jetty.io.nio.BUSY_PAUSE", 50);
        __IDLE_TICK = Integer.getInteger("org.eclipse.jetty.io.nio.IDLE_TICK", 400);
    }
    
    public class SelectSet implements Dumpable
    {
        private final int _setID;
        private final Timeout _timeout;
        private final ConcurrentLinkedQueue<Object> _changes;
        private volatile Selector _selector;
        private volatile Thread _selecting;
        private int _busySelects;
        private long _monitorNext;
        private boolean _pausing;
        private boolean _paused;
        private volatile long _idleTick;
        private ConcurrentMap<SelectChannelEndPoint, Object> _endPoints;
        
        SelectSet(final int acceptorID) throws Exception {
            this._changes = new ConcurrentLinkedQueue<Object>();
            this._endPoints = new ConcurrentHashMap<SelectChannelEndPoint, Object>();
            this._setID = acceptorID;
            this._idleTick = System.currentTimeMillis();
            (this._timeout = new Timeout(this)).setDuration(0L);
            this._selector = Selector.open();
            this._monitorNext = System.currentTimeMillis() + SelectorManager.__MONITOR_PERIOD;
        }
        
        public void addChange(final Object change) {
            this._changes.add(change);
        }
        
        public void addChange(final SelectableChannel channel, final Object att) {
            if (att == null) {
                this.addChange(channel);
            }
            else if (att instanceof EndPoint) {
                this.addChange(att);
            }
            else {
                this.addChange(new ChannelAndAttachment(channel, att));
            }
        }
        
        public void doSelect() throws IOException {
            try {
                this._selecting = Thread.currentThread();
                final Selector selector = this._selector;
                if (selector == null) {
                    return;
                }
                int changes = this._changes.size();
                Object change;
                while (changes-- > 0 && (change = this._changes.poll()) != null) {
                    Channel ch = null;
                    SelectionKey key = null;
                    try {
                        if (change instanceof EndPoint) {
                            final SelectChannelEndPoint endpoint = (SelectChannelEndPoint)change;
                            ch = endpoint.getChannel();
                            endpoint.doUpdateKey();
                        }
                        else if (change instanceof ChannelAndAttachment) {
                            final ChannelAndAttachment asc = (ChannelAndAttachment)change;
                            final SelectableChannel channel = (SelectableChannel)(ch = asc._channel);
                            final Object att = asc._attachment;
                            if (channel instanceof SocketChannel && ((SocketChannel)channel).isConnected()) {
                                key = channel.register(selector, 1, att);
                                final SelectChannelEndPoint endpoint2 = this.createEndPoint((SocketChannel)channel, key);
                                key.attach(endpoint2);
                                endpoint2.schedule();
                            }
                            else {
                                if (!channel.isOpen()) {
                                    continue;
                                }
                                key = channel.register(selector, 8, att);
                            }
                        }
                        else if (change instanceof SocketChannel) {
                            final SocketChannel channel2 = (SocketChannel)(ch = (SocketChannel)change);
                            key = channel2.register(selector, 1, null);
                            final SelectChannelEndPoint endpoint3 = this.createEndPoint(channel2, key);
                            key.attach(endpoint3);
                            endpoint3.schedule();
                        }
                        else if (change instanceof ChangeTask) {
                            ((Runnable)change).run();
                        }
                        else {
                            if (!(change instanceof Runnable)) {
                                throw new IllegalArgumentException(change.toString());
                            }
                            SelectorManager.this.dispatch((Runnable)change);
                        }
                    }
                    catch (CancelledKeyException e) {
                        SelectorManager.LOG.ignore(e);
                    }
                    catch (Throwable e2) {
                        if (SelectorManager.this.isRunning()) {
                            SelectorManager.LOG.warn(e2);
                        }
                        else {
                            SelectorManager.LOG.debug(e2);
                        }
                        try {
                            if (ch != null) {
                                ch.close();
                            }
                        }
                        catch (IOException e3) {
                            SelectorManager.LOG.debug(e3);
                        }
                    }
                }
                int selected = selector.selectNow();
                long now = System.currentTimeMillis();
                if (selected == 0 && selector.selectedKeys().isEmpty()) {
                    if (this._pausing) {
                        try {
                            Thread.sleep(SelectorManager.__BUSY_PAUSE);
                        }
                        catch (InterruptedException e4) {
                            SelectorManager.LOG.ignore(e4);
                        }
                        now = System.currentTimeMillis();
                    }
                    this._timeout.setNow(now);
                    final long to_next_timeout = this._timeout.getTimeToNext();
                    long wait = (this._changes.size() == 0) ? SelectorManager.__IDLE_TICK : 0L;
                    if (wait > 0L && to_next_timeout >= 0L && wait > to_next_timeout) {
                        wait = to_next_timeout;
                    }
                    if (wait > 0L) {
                        final long before = now;
                        selected = selector.select(wait);
                        now = System.currentTimeMillis();
                        this._timeout.setNow(now);
                        if (SelectorManager.__MONITOR_PERIOD > 0 && now - before <= 1L && ++this._busySelects > SelectorManager.__MAX_SELECTS) {
                            this._pausing = true;
                            if (!this._paused) {
                                this._paused = true;
                                SelectorManager.LOG.warn("Selector {} is too busy, pausing!", this);
                            }
                        }
                    }
                }
                if (this._selector == null || !selector.isOpen()) {
                    return;
                }
                for (SelectionKey key2 : selector.selectedKeys()) {
                    SocketChannel channel3 = null;
                    try {
                        if (!key2.isValid()) {
                            key2.cancel();
                            final SelectChannelEndPoint endpoint4 = (SelectChannelEndPoint)key2.attachment();
                            if (endpoint4 == null) {
                                continue;
                            }
                            endpoint4.doUpdateKey();
                        }
                        else {
                            final Object att2 = key2.attachment();
                            if (att2 instanceof SelectChannelEndPoint) {
                                if (key2.isReadable() || key2.isWritable()) {
                                    ((SelectChannelEndPoint)att2).schedule();
                                }
                            }
                            else if (key2.isConnectable()) {
                                channel3 = (SocketChannel)key2.channel();
                                boolean connected = false;
                                try {
                                    connected = channel3.finishConnect();
                                }
                                catch (Exception e5) {
                                    SelectorManager.this.connectionFailed(channel3, e5, att2);
                                }
                                finally {
                                    if (connected) {
                                        key2.interestOps(1);
                                        final SelectChannelEndPoint endpoint5 = this.createEndPoint(channel3, key2);
                                        key2.attach(endpoint5);
                                        endpoint5.schedule();
                                    }
                                    else {
                                        key2.cancel();
                                    }
                                }
                            }
                            else {
                                channel3 = (SocketChannel)key2.channel();
                                final SelectChannelEndPoint endpoint6 = this.createEndPoint(channel3, key2);
                                key2.attach(endpoint6);
                                if (key2.isReadable()) {
                                    endpoint6.schedule();
                                }
                            }
                            key2 = null;
                        }
                    }
                    catch (CancelledKeyException e6) {
                        SelectorManager.LOG.ignore(e6);
                    }
                    catch (Exception e7) {
                        if (SelectorManager.this.isRunning()) {
                            SelectorManager.LOG.warn(e7);
                        }
                        else {
                            SelectorManager.LOG.ignore(e7);
                        }
                        try {
                            if (channel3 != null) {
                                channel3.close();
                            }
                        }
                        catch (IOException e8) {
                            SelectorManager.LOG.debug(e8);
                        }
                        if (key2 == null || key2.channel() instanceof ServerSocketChannel || !key2.isValid()) {
                            continue;
                        }
                        key2.cancel();
                    }
                }
                selector.selectedKeys().clear();
                now = System.currentTimeMillis();
                this._timeout.setNow(now);
                for (Timeout.Task task = this._timeout.expired(); task != null; task = this._timeout.expired()) {
                    if (task instanceof Runnable) {
                        SelectorManager.this.dispatch((Runnable)task);
                    }
                }
                if (now - this._idleTick > SelectorManager.__IDLE_TICK) {
                    this._idleTick = now;
                    final long idle_now = (SelectorManager.this._lowResourcesConnections > 0L && selector.keys().size() > SelectorManager.this._lowResourcesConnections) ? (now + SelectorManager.this._maxIdleTime - SelectorManager.this._lowResourcesMaxIdleTime) : now;
                    SelectorManager.this.dispatch(new Runnable() {
                        public void run() {
                            for (final SelectChannelEndPoint endp : SelectSet.this._endPoints.keySet()) {
                                endp.checkIdleTimestamp(idle_now);
                            }
                        }
                        
                        @Override
                        public String toString() {
                            return "Idle-" + super.toString();
                        }
                    });
                }
                if (SelectorManager.__MONITOR_PERIOD > 0 && now > this._monitorNext) {
                    this._busySelects = 0;
                    this._pausing = false;
                    this._monitorNext = now + SelectorManager.__MONITOR_PERIOD;
                }
            }
            catch (ClosedSelectorException e9) {
                if (SelectorManager.this.isRunning()) {
                    SelectorManager.LOG.warn(e9);
                }
                else {
                    SelectorManager.LOG.ignore(e9);
                }
            }
            catch (CancelledKeyException e10) {
                SelectorManager.LOG.ignore(e10);
            }
            finally {
                this._selecting = null;
            }
        }
        
        private void renewSelector() {
            try {
                synchronized (this) {
                    final Selector selector = this._selector;
                    if (selector == null) {
                        return;
                    }
                    final Selector new_selector = Selector.open();
                    for (final SelectionKey k : selector.keys()) {
                        if (k.isValid()) {
                            if (k.interestOps() == 0) {
                                continue;
                            }
                            final SelectableChannel channel = k.channel();
                            final Object attachment = k.attachment();
                            if (attachment == null) {
                                this.addChange(channel);
                            }
                            else {
                                this.addChange(channel, attachment);
                            }
                        }
                    }
                    this._selector.close();
                    this._selector = new_selector;
                }
            }
            catch (IOException e) {
                throw new RuntimeException("recreating selector", e);
            }
        }
        
        public SelectorManager getManager() {
            return SelectorManager.this;
        }
        
        public long getNow() {
            return this._timeout.getNow();
        }
        
        public void scheduleTimeout(final Timeout.Task task, final long timeoutMs) {
            if (!(task instanceof Runnable)) {
                throw new IllegalArgumentException("!Runnable");
            }
            this._timeout.schedule(task, timeoutMs);
        }
        
        public void cancelTimeout(final Timeout.Task task) {
            task.cancel();
        }
        
        public void wakeup() {
            try {
                final Selector selector = this._selector;
                if (selector != null) {
                    selector.wakeup();
                }
            }
            catch (Exception e) {
                this.addChange(new ChangeTask() {
                    public void run() {
                        SelectSet.this.renewSelector();
                    }
                });
                this.renewSelector();
            }
        }
        
        private SelectChannelEndPoint createEndPoint(final SocketChannel channel, final SelectionKey sKey) throws IOException {
            final SelectChannelEndPoint endp = SelectorManager.this.newEndPoint(channel, this, sKey);
            SelectorManager.LOG.debug("created {}", endp);
            SelectorManager.this.endPointOpened(endp);
            this._endPoints.put(endp, this);
            return endp;
        }
        
        public void destroyEndPoint(final SelectChannelEndPoint endp) {
            SelectorManager.LOG.debug("destroyEndPoint {}", endp);
            this._endPoints.remove(endp);
            SelectorManager.this.endPointClosed(endp);
        }
        
        Selector getSelector() {
            return this._selector;
        }
        
        void stop() throws Exception {
            try {
                for (int i = 0; i < 100 && this._selecting != null; ++i) {
                    this.wakeup();
                    Thread.sleep(10L);
                }
            }
            catch (Exception e) {
                SelectorManager.LOG.ignore(e);
            }
            synchronized (this) {
                Selector selector = this._selector;
                for (final SelectionKey key : selector.keys()) {
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
                    catch (IOException e2) {
                        SelectorManager.LOG.ignore(e2);
                    }
                }
                this._timeout.cancelAll();
                try {
                    selector = this._selector;
                    if (selector != null) {
                        selector.close();
                    }
                }
                catch (IOException e3) {
                    SelectorManager.LOG.ignore(e3);
                }
                this._selector = null;
            }
        }
        
        public String dump() {
            return AggregateLifeCycle.dump(this);
        }
        
        public void dump(final Appendable out, final String indent) throws IOException {
            out.append(String.valueOf(this)).append(" id=").append(String.valueOf(this._setID)).append("\n");
            final Thread selecting = this._selecting;
            Object where = "not selecting";
            final StackTraceElement[] trace = (StackTraceElement[])((selecting == null) ? null : selecting.getStackTrace());
            if (trace != null) {
                for (final StackTraceElement t : trace) {
                    if (t.getClassName().startsWith("org.eclipse.jetty.")) {
                        where = t;
                        break;
                    }
                }
            }
            final Selector selector = this._selector;
            if (selector != null) {
                final ArrayList<Object> dump = new ArrayList<Object>(selector.keys().size() * 2);
                dump.add(where);
                final CountDownLatch latch = new CountDownLatch(1);
                this.addChange(new ChangeTask() {
                    public void run() {
                        SelectSet.this.dumpKeyState(dump);
                        latch.countDown();
                    }
                });
                try {
                    latch.await(5L, TimeUnit.SECONDS);
                }
                catch (InterruptedException e) {
                    SelectorManager.LOG.ignore(e);
                }
                AggregateLifeCycle.dump(out, indent, dump);
            }
        }
        
        public void dumpKeyState(final List<Object> dumpto) {
            final Selector selector = this._selector;
            final Set<SelectionKey> keys = selector.keys();
            dumpto.add(selector + " keys=" + keys.size());
            for (final SelectionKey key : keys) {
                if (key.isValid()) {
                    dumpto.add(key.attachment() + " iOps=" + key.interestOps() + " rOps=" + key.readyOps());
                }
                else {
                    dumpto.add(key.attachment() + " iOps=-1 rOps=-1");
                }
            }
        }
        
        @Override
        public String toString() {
            final Selector selector = this._selector;
            return String.format("%s keys=%d selected=%d", super.toString(), (selector != null && selector.isOpen()) ? selector.keys().size() : -1, (selector != null && selector.isOpen()) ? selector.selectedKeys().size() : -1);
        }
    }
    
    private static class ChannelAndAttachment
    {
        final SelectableChannel _channel;
        final Object _attachment;
        
        public ChannelAndAttachment(final SelectableChannel channel, final Object attachment) {
            this._channel = channel;
            this._attachment = attachment;
        }
    }
    
    private interface ChangeTask extends Runnable
    {
    }
}
