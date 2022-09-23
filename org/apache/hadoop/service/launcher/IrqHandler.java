// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service.launcher;

import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import sun.misc.Signal;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import sun.misc.SignalHandler;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public final class IrqHandler implements SignalHandler
{
    private static final Logger LOG;
    public static final String CONTROL_C = "INT";
    public static final String SIGTERM = "TERM";
    private final String name;
    private final Interrupted handler;
    private final AtomicInteger signalCount;
    private Signal signal;
    
    public IrqHandler(final String name, final Interrupted handler) {
        this.signalCount = new AtomicInteger(0);
        Preconditions.checkArgument(name != null, (Object)"Null \"name\"");
        Preconditions.checkArgument(handler != null, (Object)"Null \"handler\"");
        this.handler = handler;
        this.name = name;
    }
    
    public void bind() {
        Preconditions.checkState(this.signal == null, (Object)"Handler already bound");
        try {
            Signal.handle(this.signal = new Signal(this.name), this);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Could not set handler for signal \"" + this.name + "\".This can happen if the JVM has the -Xrs set.", e);
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public void raise() {
        Signal.raise(this.signal);
    }
    
    @Override
    public String toString() {
        return "IrqHandler for signal " + this.name;
    }
    
    @Override
    public void handle(final Signal s) {
        this.signalCount.incrementAndGet();
        final InterruptData data = new InterruptData(s.getName(), s.getNumber());
        IrqHandler.LOG.info("Interrupted: {}", data);
        this.handler.interrupted(data);
    }
    
    public int getSignalCount() {
        return this.signalCount.get();
    }
    
    static {
        LOG = LoggerFactory.getLogger(IrqHandler.class);
    }
    
    public static class InterruptData
    {
        private final String name;
        private final int number;
        
        public InterruptData(final String name, final int number) {
            this.name = name;
            this.number = number;
        }
        
        public String getName() {
            return this.name;
        }
        
        public int getNumber() {
            return this.number;
        }
        
        @Override
        public String toString() {
            return "signal " + this.name + '(' + this.number + ')';
        }
    }
    
    public interface Interrupted
    {
        void interrupted(final InterruptData p0);
    }
}
