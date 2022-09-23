// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.jetty.util.component.Container;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.ConcurrentMap;
import org.eclipse.jetty.util.statistic.SampleStatistic;
import org.eclipse.jetty.util.statistic.CounterStatistic;
import java.util.concurrent.atomic.AtomicLong;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

@Deprecated
@ManagedObject("Connector Statistics")
public class ConnectorStatistics extends AbstractLifeCycle implements Dumpable, Connection.Listener
{
    private static final Sample ZERO;
    private final AtomicLong _startMillis;
    private final CounterStatistic _connectionStats;
    private final SampleStatistic _messagesIn;
    private final SampleStatistic _messagesOut;
    private final SampleStatistic _connectionDurationStats;
    private final ConcurrentMap<Connection, Sample> _samples;
    private final LongAdder _closedIn;
    private final LongAdder _closedOut;
    private AtomicLong _nanoStamp;
    private volatile int _messagesInPerSecond;
    private volatile int _messagesOutPerSecond;
    private static final long SECOND_NANOS;
    
    public ConnectorStatistics() {
        this._startMillis = new AtomicLong(-1L);
        this._connectionStats = new CounterStatistic();
        this._messagesIn = new SampleStatistic();
        this._messagesOut = new SampleStatistic();
        this._connectionDurationStats = new SampleStatistic();
        this._samples = new ConcurrentHashMap<Connection, Sample>();
        this._closedIn = new LongAdder();
        this._closedOut = new LongAdder();
        this._nanoStamp = new AtomicLong();
    }
    
    @Override
    public void onOpened(final Connection connection) {
        if (this.isStarted()) {
            this._connectionStats.increment();
            this._samples.put(connection, ConnectorStatistics.ZERO);
        }
    }
    
    @Override
    public void onClosed(final Connection connection) {
        if (this.isStarted()) {
            final int msgsIn = connection.getMessagesIn();
            final int msgsOut = connection.getMessagesOut();
            this._messagesIn.set(msgsIn);
            this._messagesOut.set(msgsOut);
            this._connectionStats.decrement();
            this._connectionDurationStats.set(System.currentTimeMillis() - connection.getCreatedTimeStamp());
            final Sample sample = this._samples.remove(connection);
            if (sample != null) {
                this._closedIn.add(msgsIn - sample._messagesIn);
                this._closedOut.add(msgsOut - sample._messagesOut);
            }
        }
    }
    
    @ManagedAttribute("Total number of bytes received by this connector")
    public int getBytesIn() {
        return -1;
    }
    
    @ManagedAttribute("Total number of bytes sent by this connector")
    public int getBytesOut() {
        return -1;
    }
    
    @ManagedAttribute("Total number of connections seen by this connector")
    public int getConnections() {
        return (int)this._connectionStats.getTotal();
    }
    
    @ManagedAttribute("Connection duration maximum in ms")
    public long getConnectionDurationMax() {
        return this._connectionDurationStats.getMax();
    }
    
    @ManagedAttribute("Connection duration mean in ms")
    public double getConnectionDurationMean() {
        return this._connectionDurationStats.getMean();
    }
    
    @ManagedAttribute("Connection duration standard deviation")
    public double getConnectionDurationStdDev() {
        return this._connectionDurationStats.getStdDev();
    }
    
    @ManagedAttribute("Messages In for all connections")
    public int getMessagesIn() {
        return (int)this._messagesIn.getTotal();
    }
    
    @ManagedAttribute("Messages In per connection maximum")
    public int getMessagesInPerConnectionMax() {
        return (int)this._messagesIn.getMax();
    }
    
    @ManagedAttribute("Messages In per connection mean")
    public double getMessagesInPerConnectionMean() {
        return this._messagesIn.getMean();
    }
    
    @ManagedAttribute("Messages In per connection standard deviation")
    public double getMessagesInPerConnectionStdDev() {
        return this._messagesIn.getStdDev();
    }
    
    @ManagedAttribute("Connections open")
    public int getConnectionsOpen() {
        return (int)this._connectionStats.getCurrent();
    }
    
    @ManagedAttribute("Connections open maximum")
    public int getConnectionsOpenMax() {
        return (int)this._connectionStats.getMax();
    }
    
    @ManagedAttribute("Messages Out for all connections")
    public int getMessagesOut() {
        return (int)this._messagesIn.getTotal();
    }
    
    @ManagedAttribute("Messages In per connection maximum")
    public int getMessagesOutPerConnectionMax() {
        return (int)this._messagesIn.getMax();
    }
    
    @ManagedAttribute("Messages In per connection mean")
    public double getMessagesOutPerConnectionMean() {
        return this._messagesIn.getMean();
    }
    
    @ManagedAttribute("Messages In per connection standard deviation")
    public double getMessagesOutPerConnectionStdDev() {
        return this._messagesIn.getStdDev();
    }
    
    @ManagedAttribute("Connection statistics started ms since epoch")
    public long getStartedMillis() {
        final long start = this._startMillis.get();
        return (start < 0L) ? 0L : (System.currentTimeMillis() - start);
    }
    
    @ManagedAttribute("Messages in per second calculated over period since last called")
    public int getMessagesInPerSecond() {
        this.update();
        return this._messagesInPerSecond;
    }
    
    @ManagedAttribute("Messages out per second calculated over period since last called")
    public int getMessagesOutPerSecond() {
        this.update();
        return this._messagesOutPerSecond;
    }
    
    public void doStart() {
        this.reset();
    }
    
    public void doStop() {
        this._samples.clear();
    }
    
    @ManagedOperation("Reset the statistics")
    public void reset() {
        this._startMillis.set(System.currentTimeMillis());
        this._messagesIn.reset();
        this._messagesOut.reset();
        this._connectionStats.reset();
        this._connectionDurationStats.reset();
        this._samples.clear();
    }
    
    @ManagedOperation("dump thread state")
    @Override
    public String dump() {
        return ContainerLifeCycle.dump(this);
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        ContainerLifeCycle.dumpObject(out, this);
        ContainerLifeCycle.dump(out, indent, Arrays.asList("connections=" + this._connectionStats, "duration=" + this._connectionDurationStats, "in=" + this._messagesIn, "out=" + this._messagesOut));
    }
    
    public static void addToAllConnectors(final Server server) {
        for (final Connector connector : server.getConnectors()) {
            if (connector instanceof Container) {
                ((Container)connector).addBean(new ConnectorStatistics());
            }
        }
    }
    
    private synchronized void update() {
        final long now = System.nanoTime();
        final long then = this._nanoStamp.get();
        final long duration = now - then;
        if (duration > ConnectorStatistics.SECOND_NANOS / 2L && this._nanoStamp.compareAndSet(then, now)) {
            long msgsIn = this._closedIn.sumThenReset();
            long msgsOut = this._closedOut.sumThenReset();
            for (final Map.Entry<Connection, Sample> entry : this._samples.entrySet()) {
                final Connection connection = entry.getKey();
                final Sample sample = entry.getValue();
                final Sample next = new Sample(connection);
                if (this._samples.replace(connection, sample, next)) {
                    msgsIn += next._messagesIn - sample._messagesIn;
                    msgsOut += next._messagesOut - sample._messagesOut;
                }
            }
            this._messagesInPerSecond = (int)(msgsIn * ConnectorStatistics.SECOND_NANOS / duration);
            this._messagesOutPerSecond = (int)(msgsOut * ConnectorStatistics.SECOND_NANOS / duration);
        }
    }
    
    static {
        ZERO = new Sample();
        SECOND_NANOS = TimeUnit.SECONDS.toNanos(1L);
    }
    
    private static class Sample
    {
        final int _messagesIn;
        final int _messagesOut;
        
        Sample() {
            this._messagesIn = 0;
            this._messagesOut = 0;
        }
        
        Sample(final Connection connection) {
            this._messagesIn = connection.getMessagesIn();
            this._messagesOut = connection.getMessagesOut();
        }
    }
}
