// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.MaxCountExceededException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Collection;

public class IterationManager
{
    private final Incrementor iterations;
    private final Collection<IterationListener> listeners;
    
    public IterationManager(final int maxIterations) {
        this.iterations = new Incrementor(maxIterations);
        this.listeners = new CopyOnWriteArrayList<IterationListener>();
    }
    
    public IterationManager(final int maxIterations, final Incrementor.MaxCountExceededCallback callBack) {
        this.iterations = new Incrementor(maxIterations, callBack);
        this.listeners = new CopyOnWriteArrayList<IterationListener>();
    }
    
    public void addIterationListener(final IterationListener listener) {
        this.listeners.add(listener);
    }
    
    public void fireInitializationEvent(final IterationEvent e) {
        for (final IterationListener l : this.listeners) {
            l.initializationPerformed(e);
        }
    }
    
    public void fireIterationPerformedEvent(final IterationEvent e) {
        for (final IterationListener l : this.listeners) {
            l.iterationPerformed(e);
        }
    }
    
    public void fireIterationStartedEvent(final IterationEvent e) {
        for (final IterationListener l : this.listeners) {
            l.iterationStarted(e);
        }
    }
    
    public void fireTerminationEvent(final IterationEvent e) {
        for (final IterationListener l : this.listeners) {
            l.terminationPerformed(e);
        }
    }
    
    public int getIterations() {
        return this.iterations.getCount();
    }
    
    public int getMaxIterations() {
        return this.iterations.getMaximalCount();
    }
    
    public void incrementIterationCount() throws MaxCountExceededException {
        this.iterations.incrementCount();
    }
    
    public void removeIterationListener(final IterationListener listener) {
        this.listeners.remove(listener);
    }
    
    public void resetIterationCount() {
        this.iterations.resetCount();
    }
}
