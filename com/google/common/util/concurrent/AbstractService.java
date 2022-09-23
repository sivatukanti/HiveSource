// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.util.concurrent;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.concurrent.ExecutionException;
import java.util.Iterator;
import com.google.common.base.Preconditions;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.util.concurrent.Executor;
import com.google.common.collect.Lists;
import javax.annotation.concurrent.GuardedBy;
import java.util.List;
import com.google.common.annotations.Beta;

@Beta
public abstract class AbstractService implements Service
{
    private final Monitor monitor;
    private final Transition startup;
    private final Transition shutdown;
    private final Monitor.Guard isStartable;
    private final Monitor.Guard isStoppable;
    private final Monitor.Guard hasReachedRunning;
    private final Monitor.Guard isStopped;
    @GuardedBy("monitor")
    private final List<ListenerExecutorPair> listeners;
    private final ExecutionQueue queuedListeners;
    @GuardedBy("monitor")
    private volatile StateSnapshot snapshot;
    
    protected AbstractService() {
        this.monitor = new Monitor();
        this.startup = new Transition();
        this.shutdown = new Transition();
        this.isStartable = new Monitor.Guard(this.monitor) {
            @Override
            public boolean isSatisfied() {
                return AbstractService.this.state() == State.NEW;
            }
        };
        this.isStoppable = new Monitor.Guard(this.monitor) {
            @Override
            public boolean isSatisfied() {
                return AbstractService.this.state().compareTo(State.RUNNING) <= 0;
            }
        };
        this.hasReachedRunning = new Monitor.Guard(this.monitor) {
            @Override
            public boolean isSatisfied() {
                return AbstractService.this.state().compareTo(State.RUNNING) >= 0;
            }
        };
        this.isStopped = new Monitor.Guard(this.monitor) {
            @Override
            public boolean isSatisfied() {
                return AbstractService.this.state().isTerminal();
            }
        };
        this.listeners = (List<ListenerExecutorPair>)Lists.newArrayList();
        this.queuedListeners = new ExecutionQueue();
        this.snapshot = new StateSnapshot(State.NEW);
        this.addListener(new Listener() {
            @Override
            public void running() {
                AbstractService.this.startup.set(State.RUNNING);
            }
            
            @Override
            public void stopping(final State from) {
                if (from == State.STARTING) {
                    AbstractService.this.startup.set(State.STOPPING);
                }
            }
            
            @Override
            public void terminated(final State from) {
                if (from == State.NEW) {
                    AbstractService.this.startup.set(State.TERMINATED);
                }
                AbstractService.this.shutdown.set(State.TERMINATED);
            }
            
            @Override
            public void failed(final State from, final Throwable failure) {
                switch (from) {
                    case STARTING: {
                        AbstractService.this.startup.setException(failure);
                        AbstractService.this.shutdown.setException(new Exception("Service failed to start.", failure));
                        break;
                    }
                    case RUNNING: {
                        AbstractService.this.shutdown.setException(new Exception("Service failed while running", failure));
                        break;
                    }
                    case STOPPING: {
                        AbstractService.this.shutdown.setException(failure);
                        break;
                    }
                    default: {
                        throw new AssertionError((Object)("Unexpected from state: " + from));
                    }
                }
            }
        }, MoreExecutors.sameThreadExecutor());
    }
    
    protected abstract void doStart();
    
    protected abstract void doStop();
    
    @Override
    public final Service startAsync() {
        if (this.monitor.enterIf(this.isStartable)) {
            try {
                this.snapshot = new StateSnapshot(State.STARTING);
                this.starting();
                this.doStart();
            }
            catch (Throwable startupFailure) {
                this.notifyFailed(startupFailure);
            }
            finally {
                this.monitor.leave();
                this.executeListeners();
            }
            return this;
        }
        throw new IllegalStateException("Service " + this + " has already been started");
    }
    
    @Deprecated
    @Override
    public final ListenableFuture<State> start() {
        if (this.monitor.enterIf(this.isStartable)) {
            try {
                this.snapshot = new StateSnapshot(State.STARTING);
                this.starting();
                this.doStart();
            }
            catch (Throwable startupFailure) {
                this.notifyFailed(startupFailure);
            }
            finally {
                this.monitor.leave();
                this.executeListeners();
            }
        }
        return this.startup;
    }
    
    @Override
    public final Service stopAsync() {
        this.stop();
        return this;
    }
    
    @Deprecated
    @Override
    public final ListenableFuture<State> stop() {
        if (this.monitor.enterIf(this.isStoppable)) {
            try {
                final State previous = this.state();
                switch (previous) {
                    case NEW: {
                        this.snapshot = new StateSnapshot(State.TERMINATED);
                        this.terminated(State.NEW);
                        break;
                    }
                    case STARTING: {
                        this.snapshot = new StateSnapshot(State.STARTING, true, null);
                        this.stopping(State.STARTING);
                        break;
                    }
                    case RUNNING: {
                        this.snapshot = new StateSnapshot(State.STOPPING);
                        this.stopping(State.RUNNING);
                        this.doStop();
                        break;
                    }
                    case STOPPING:
                    case TERMINATED:
                    case FAILED: {
                        throw new AssertionError((Object)("isStoppable is incorrectly implemented, saw: " + previous));
                    }
                    default: {
                        throw new AssertionError((Object)("Unexpected state: " + previous));
                    }
                }
            }
            catch (Throwable shutdownFailure) {
                this.notifyFailed(shutdownFailure);
            }
            finally {
                this.monitor.leave();
                this.executeListeners();
            }
        }
        return this.shutdown;
    }
    
    @Deprecated
    @Override
    public State startAndWait() {
        return Futures.getUnchecked(this.start());
    }
    
    @Deprecated
    @Override
    public State stopAndWait() {
        return Futures.getUnchecked(this.stop());
    }
    
    @Override
    public final void awaitRunning() {
        this.monitor.enterWhenUninterruptibly(this.hasReachedRunning);
        try {
            this.checkCurrentState(State.RUNNING);
        }
        finally {
            this.monitor.leave();
        }
    }
    
    @Override
    public final void awaitRunning(final long timeout, final TimeUnit unit) throws TimeoutException {
        if (this.monitor.enterWhenUninterruptibly(this.hasReachedRunning, timeout, unit)) {
            try {
                this.checkCurrentState(State.RUNNING);
            }
            finally {
                this.monitor.leave();
            }
            return;
        }
        throw new TimeoutException("Timed out waiting for " + this + " to reach the RUNNING state. " + "Current state: " + this.state());
    }
    
    @Override
    public final void awaitTerminated() {
        this.monitor.enterWhenUninterruptibly(this.isStopped);
        try {
            this.checkCurrentState(State.TERMINATED);
        }
        finally {
            this.monitor.leave();
        }
    }
    
    @Override
    public final void awaitTerminated(final long timeout, final TimeUnit unit) throws TimeoutException {
        if (this.monitor.enterWhenUninterruptibly(this.isStopped, timeout, unit)) {
            try {
                final State state = this.state();
                this.checkCurrentState(State.TERMINATED);
            }
            finally {
                this.monitor.leave();
            }
            return;
        }
        throw new TimeoutException("Timed out waiting for " + this + " to reach a terminal state. " + "Current state: " + this.state());
    }
    
    @GuardedBy("monitor")
    private void checkCurrentState(final State expected) {
        final State actual = this.state();
        if (actual == expected) {
            return;
        }
        if (actual == State.FAILED) {
            throw new IllegalStateException("Expected the service to be " + expected + ", but the service has FAILED", this.failureCause());
        }
        throw new IllegalStateException("Expected the service to be " + expected + ", but was " + actual);
    }
    
    protected final void notifyStarted() {
        this.monitor.enter();
        try {
            if (this.snapshot.state != State.STARTING) {
                final IllegalStateException failure = new IllegalStateException("Cannot notifyStarted() when the service is " + this.snapshot.state);
                this.notifyFailed(failure);
                throw failure;
            }
            if (this.snapshot.shutdownWhenStartupFinishes) {
                this.snapshot = new StateSnapshot(State.STOPPING);
                this.doStop();
            }
            else {
                this.snapshot = new StateSnapshot(State.RUNNING);
                this.running();
            }
        }
        finally {
            this.monitor.leave();
            this.executeListeners();
        }
    }
    
    protected final void notifyStopped() {
        this.monitor.enter();
        try {
            final State previous = this.snapshot.state;
            if (previous != State.STOPPING && previous != State.RUNNING) {
                final IllegalStateException failure = new IllegalStateException("Cannot notifyStopped() when the service is " + previous);
                this.notifyFailed(failure);
                throw failure;
            }
            this.snapshot = new StateSnapshot(State.TERMINATED);
            this.terminated(previous);
        }
        finally {
            this.monitor.leave();
            this.executeListeners();
        }
    }
    
    protected final void notifyFailed(final Throwable cause) {
        Preconditions.checkNotNull(cause);
        this.monitor.enter();
        try {
            final State previous = this.state();
            switch (previous) {
                case TERMINATED:
                case NEW: {
                    throw new IllegalStateException("Failed while in state:" + previous, cause);
                }
                case STARTING:
                case RUNNING:
                case STOPPING: {
                    this.snapshot = new StateSnapshot(State.FAILED, false, cause);
                    this.failed(previous, cause);
                    break;
                }
                case FAILED: {
                    break;
                }
                default: {
                    throw new AssertionError((Object)("Unexpected state: " + previous));
                }
            }
        }
        finally {
            this.monitor.leave();
            this.executeListeners();
        }
    }
    
    @Override
    public final boolean isRunning() {
        return this.state() == State.RUNNING;
    }
    
    @Override
    public final State state() {
        return this.snapshot.externalState();
    }
    
    @Override
    public final Throwable failureCause() {
        return this.snapshot.failureCause();
    }
    
    @Override
    public final void addListener(final Listener listener, final Executor executor) {
        Preconditions.checkNotNull(listener, (Object)"listener");
        Preconditions.checkNotNull(executor, (Object)"executor");
        this.monitor.enter();
        try {
            final State currentState = this.state();
            if (currentState != State.TERMINATED && currentState != State.FAILED) {
                this.listeners.add(new ListenerExecutorPair(listener, executor));
            }
        }
        finally {
            this.monitor.leave();
        }
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [" + this.state() + "]";
    }
    
    private void executeListeners() {
        if (!this.monitor.isOccupiedByCurrentThread()) {
            this.queuedListeners.execute();
        }
    }
    
    @GuardedBy("monitor")
    private void starting() {
        for (final ListenerExecutorPair pair : this.listeners) {
            this.queuedListeners.add(new Runnable() {
                @Override
                public void run() {
                    pair.listener.starting();
                }
            }, pair.executor);
        }
    }
    
    @GuardedBy("monitor")
    private void running() {
        for (final ListenerExecutorPair pair : this.listeners) {
            this.queuedListeners.add(new Runnable() {
                @Override
                public void run() {
                    pair.listener.running();
                }
            }, pair.executor);
        }
    }
    
    @GuardedBy("monitor")
    private void stopping(final State from) {
        for (final ListenerExecutorPair pair : this.listeners) {
            this.queuedListeners.add(new Runnable() {
                @Override
                public void run() {
                    pair.listener.stopping(from);
                }
            }, pair.executor);
        }
    }
    
    @GuardedBy("monitor")
    private void terminated(final State from) {
        for (final ListenerExecutorPair pair : this.listeners) {
            this.queuedListeners.add(new Runnable() {
                @Override
                public void run() {
                    pair.listener.terminated(from);
                }
            }, pair.executor);
        }
        this.listeners.clear();
    }
    
    @GuardedBy("monitor")
    private void failed(final State from, final Throwable cause) {
        for (final ListenerExecutorPair pair : this.listeners) {
            this.queuedListeners.add(new Runnable() {
                @Override
                public void run() {
                    pair.listener.failed(from, cause);
                }
            }, pair.executor);
        }
        this.listeners.clear();
    }
    
    private class Transition extends AbstractFuture<State>
    {
        @Override
        public State get(final long timeout, final TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
            try {
                return super.get(timeout, unit);
            }
            catch (TimeoutException e) {
                throw new TimeoutException(AbstractService.this.toString());
            }
        }
    }
    
    private static class ListenerExecutorPair
    {
        final Listener listener;
        final Executor executor;
        
        ListenerExecutorPair(final Listener listener, final Executor executor) {
            this.listener = listener;
            this.executor = executor;
        }
    }
    
    @Immutable
    private static final class StateSnapshot
    {
        final State state;
        final boolean shutdownWhenStartupFinishes;
        @Nullable
        final Throwable failure;
        
        StateSnapshot(final State internalState) {
            this(internalState, false, null);
        }
        
        StateSnapshot(final State internalState, final boolean shutdownWhenStartupFinishes, @Nullable final Throwable failure) {
            Preconditions.checkArgument(!shutdownWhenStartupFinishes || internalState == State.STARTING, "shudownWhenStartupFinishes can only be set if state is STARTING. Got %s instead.", internalState);
            Preconditions.checkArgument(!(failure != null ^ internalState == State.FAILED), "A failure cause should be set if and only if the state is failed.  Got %s and %s instead.", internalState, failure);
            this.state = internalState;
            this.shutdownWhenStartupFinishes = shutdownWhenStartupFinishes;
            this.failure = failure;
        }
        
        State externalState() {
            if (this.shutdownWhenStartupFinishes && this.state == State.STARTING) {
                return State.STOPPING;
            }
            return this.state;
        }
        
        Throwable failureCause() {
            Preconditions.checkState(this.state == State.FAILED, "failureCause() is only valid if the service has failed, service is %s", this.state);
            return this.failure;
        }
    }
}
