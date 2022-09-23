// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import java.io.IOException;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import org.eclipse.jetty.util.statistic.SampleStatistic;
import org.eclipse.jetty.util.statistic.CounterStatistic;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

@ManagedObject("Tracks statistics on connections")
public class ConnectionStatistics extends AbstractLifeCycle implements Connection.Listener, Dumpable
{
    private final CounterStatistic _connections;
    private final SampleStatistic _connectionsDuration;
    private final LongAdder _rcvdBytes;
    private final AtomicLong _bytesInStamp;
    private final LongAdder _sentBytes;
    private final AtomicLong _bytesOutStamp;
    private final LongAdder _messagesIn;
    private final AtomicLong _messagesInStamp;
    private final LongAdder _messagesOut;
    private final AtomicLong _messagesOutStamp;
    
    public ConnectionStatistics() {
        this._connections = new CounterStatistic();
        this._connectionsDuration = new SampleStatistic();
        this._rcvdBytes = new LongAdder();
        this._bytesInStamp = new AtomicLong();
        this._sentBytes = new LongAdder();
        this._bytesOutStamp = new AtomicLong();
        this._messagesIn = new LongAdder();
        this._messagesInStamp = new AtomicLong();
        this._messagesOut = new LongAdder();
        this._messagesOutStamp = new AtomicLong();
    }
    
    @ManagedOperation(value = "Resets the statistics", impact = "ACTION")
    public void reset() {
        this._connections.reset();
        this._connectionsDuration.reset();
        this._rcvdBytes.reset();
        this._bytesInStamp.set(System.nanoTime());
        this._sentBytes.reset();
        this._bytesOutStamp.set(System.nanoTime());
        this._messagesIn.reset();
        this._messagesInStamp.set(System.nanoTime());
        this._messagesOut.reset();
        this._messagesOutStamp.set(System.nanoTime());
    }
    
    @Override
    protected void doStart() throws Exception {
        this.reset();
    }
    
    @Override
    public void onOpened(final Connection connection) {
        if (!this.isStarted()) {
            return;
        }
        this._connections.increment();
    }
    
    @Override
    public void onClosed(final Connection connection) {
        if (!this.isStarted()) {
            return;
        }
        this._connections.decrement();
        final long elapsed = System.currentTimeMillis() - connection.getCreatedTimeStamp();
        this._connectionsDuration.set(elapsed);
        final long bytesIn = connection.getBytesIn();
        if (bytesIn > 0L) {
            this._rcvdBytes.add(bytesIn);
        }
        final long bytesOut = connection.getBytesOut();
        if (bytesOut > 0L) {
            this._sentBytes.add(bytesOut);
        }
        final long messagesIn = connection.getMessagesIn();
        if (messagesIn > 0L) {
            this._messagesIn.add(messagesIn);
        }
        final long messagesOut = connection.getMessagesOut();
        if (messagesOut > 0L) {
            this._messagesOut.add(messagesOut);
        }
    }
    
    @ManagedAttribute("Total number of bytes received by tracked connections")
    public long getReceivedBytes() {
        return this._rcvdBytes.sum();
    }
    
    @ManagedAttribute("Total number of bytes received per second since the last invocation of this method")
    public long getReceivedBytesRate() {
        final long now = System.nanoTime();
        final long then = this._bytesInStamp.getAndSet(now);
        final long elapsed = TimeUnit.NANOSECONDS.toMillis(now - then);
        return (elapsed == 0L) ? 0L : (this.getReceivedBytes() * 1000L / elapsed);
    }
    
    @ManagedAttribute("Total number of bytes sent by tracked connections")
    public long getSentBytes() {
        return this._sentBytes.sum();
    }
    
    @ManagedAttribute("Total number of bytes sent per second since the last invocation of this method")
    public long getSentBytesRate() {
        final long now = System.nanoTime();
        final long then = this._bytesOutStamp.getAndSet(now);
        final long elapsed = TimeUnit.NANOSECONDS.toMillis(now - then);
        return (elapsed == 0L) ? 0L : (this.getSentBytes() * 1000L / elapsed);
    }
    
    @ManagedAttribute("The max duration of a connection in ms")
    public long getConnectionDurationMax() {
        return this._connectionsDuration.getMax();
    }
    
    @ManagedAttribute("The mean duration of a connection in ms")
    public double getConnectionDurationMean() {
        return this._connectionsDuration.getMean();
    }
    
    @ManagedAttribute("The standard deviation of the duration of a connection")
    public double getConnectionDurationStdDev() {
        return this._connectionsDuration.getStdDev();
    }
    
    @ManagedAttribute("The total number of connections opened")
    public long getConnectionsTotal() {
        return this._connections.getTotal();
    }
    
    @ManagedAttribute("The current number of open connections")
    public long getConnections() {
        return this._connections.getCurrent();
    }
    
    @ManagedAttribute("The max number of open connections")
    public long getConnectionsMax() {
        return this._connections.getMax();
    }
    
    @ManagedAttribute("The total number of messages received")
    public long getReceivedMessages() {
        return this._messagesIn.sum();
    }
    
    @ManagedAttribute("Total number of messages received per second since the last invocation of this method")
    public long getReceivedMessagesRate() {
        final long now = System.nanoTime();
        final long then = this._messagesInStamp.getAndSet(now);
        final long elapsed = TimeUnit.NANOSECONDS.toMillis(now - then);
        return (elapsed == 0L) ? 0L : (this.getReceivedMessages() * 1000L / elapsed);
    }
    
    @ManagedAttribute("The total number of messages sent")
    public long getSentMessages() {
        return this._messagesOut.sum();
    }
    
    @ManagedAttribute("Total number of messages sent per second since the last invocation of this method")
    public long getSentMessagesRate() {
        final long now = System.nanoTime();
        final long then = this._messagesOutStamp.getAndSet(now);
        final long elapsed = TimeUnit.NANOSECONDS.toMillis(now - then);
        return (elapsed == 0L) ? 0L : (this.getSentMessages() * 1000L / elapsed);
    }
    
    @Override
    public String dump() {
        return ContainerLifeCycle.dump(this);
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        ContainerLifeCycle.dumpObject(out, this);
        final List<String> children = new ArrayList<String>();
        children.add(String.format("connections=%s", this._connections));
        children.add(String.format("durations=%s", this._connectionsDuration));
        children.add(String.format("bytes in/out=%s/%s", this.getReceivedBytes(), this.getSentBytes()));
        children.add(String.format("messages in/out=%s/%s", this.getReceivedMessages(), this.getSentMessages()));
        ContainerLifeCycle.dump(out, indent, children);
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x", this.getClass().getSimpleName(), this.hashCode());
    }
}
