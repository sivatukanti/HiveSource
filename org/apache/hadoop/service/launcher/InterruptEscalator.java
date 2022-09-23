// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service.launcher;

import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.service.Service;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class InterruptEscalator implements IrqHandler.Interrupted
{
    private static final Logger LOG;
    private final AtomicBoolean signalAlreadyReceived;
    private final WeakReference<ServiceLauncher> ownerRef;
    private final int shutdownTimeMillis;
    private final List<IrqHandler> interruptHandlers;
    private boolean forcedShutdownTimedOut;
    
    public InterruptEscalator(final ServiceLauncher owner, final int shutdownTimeMillis) {
        this.signalAlreadyReceived = new AtomicBoolean(false);
        this.interruptHandlers = new ArrayList<IrqHandler>(2);
        Preconditions.checkArgument(owner != null, (Object)"null owner");
        this.ownerRef = new WeakReference<ServiceLauncher>(owner);
        this.shutdownTimeMillis = shutdownTimeMillis;
    }
    
    private ServiceLauncher getOwner() {
        return this.ownerRef.get();
    }
    
    private Service getService() {
        final ServiceLauncher owner = this.getOwner();
        return (owner != null) ? owner.getService() : null;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InterruptEscalator{");
        sb.append(" signalAlreadyReceived=").append(this.signalAlreadyReceived.get());
        final ServiceLauncher owner = this.ownerRef.get();
        if (owner != null) {
            sb.append(", owner= ").append(owner.toString());
        }
        sb.append(", shutdownTimeMillis=").append(this.shutdownTimeMillis);
        sb.append(", forcedShutdownTimedOut=").append(this.forcedShutdownTimedOut);
        sb.append('}');
        return sb.toString();
    }
    
    @Override
    public void interrupted(final IrqHandler.InterruptData interruptData) {
        String message = "Service interrupted by " + interruptData.toString();
        InterruptEscalator.LOG.warn(message);
        if (!this.signalAlreadyReceived.compareAndSet(false, true)) {
            message = "Repeated interrupt: escalating to a JVM halt";
            InterruptEscalator.LOG.warn(message);
            ExitUtil.halt(3, message);
        }
        final Service service = this.getService();
        if (service != null) {
            final ServiceForcedShutdown shutdown = new ServiceForcedShutdown(service, this.shutdownTimeMillis);
            final Thread thread = new Thread(shutdown);
            thread.setDaemon(true);
            thread.setName("Service Forced Shutdown");
            thread.start();
            try {
                thread.join(this.shutdownTimeMillis);
            }
            catch (InterruptedException ex) {}
            this.forcedShutdownTimedOut = !shutdown.getServiceWasShutdown();
            if (this.forcedShutdownTimedOut) {
                InterruptEscalator.LOG.warn("Service did not shut down in time");
            }
        }
        ExitUtil.terminate(3, message);
    }
    
    public synchronized void register(final String signalName) {
        final IrqHandler handler = new IrqHandler(signalName, this);
        handler.bind();
        this.interruptHandlers.add(handler);
    }
    
    public synchronized IrqHandler lookup(final String signalName) {
        for (final IrqHandler irqHandler : this.interruptHandlers) {
            if (irqHandler.getName().equals(signalName)) {
                return irqHandler;
            }
        }
        return null;
    }
    
    public boolean isForcedShutdownTimedOut() {
        return this.forcedShutdownTimedOut;
    }
    
    public boolean isSignalAlreadyReceived() {
        return this.signalAlreadyReceived.get();
    }
    
    static {
        LOG = LoggerFactory.getLogger(InterruptEscalator.class);
    }
    
    protected static class ServiceForcedShutdown implements Runnable
    {
        private final int shutdownTimeMillis;
        private final AtomicBoolean serviceWasShutdown;
        private Service service;
        
        public ServiceForcedShutdown(final Service service, final int shutdownTimeMillis) {
            this.serviceWasShutdown = new AtomicBoolean(false);
            this.shutdownTimeMillis = shutdownTimeMillis;
            this.service = service;
        }
        
        @Override
        public void run() {
            if (this.service != null) {
                this.service.stop();
                this.serviceWasShutdown.set(this.service.waitForServiceToStop(this.shutdownTimeMillis));
            }
            else {
                this.serviceWasShutdown.set(true);
            }
        }
        
        private boolean getServiceWasShutdown() {
            return this.serviceWasShutdown.get();
        }
    }
}
