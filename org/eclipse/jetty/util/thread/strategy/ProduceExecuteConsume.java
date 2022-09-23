// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.thread.strategy;

import org.eclipse.jetty.util.log.Log;
import java.util.concurrent.Executor;
import org.eclipse.jetty.util.thread.Locker;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.thread.ExecutionStrategy;

public class ProduceExecuteConsume extends ExecutingExecutionStrategy implements ExecutionStrategy
{
    private static final Logger LOG;
    private final Locker _locker;
    private final Producer _producer;
    private State _state;
    
    public ProduceExecuteConsume(final Producer producer, final Executor executor) {
        super(executor);
        this._locker = new Locker();
        this._state = State.IDLE;
        this._producer = producer;
    }
    
    @Override
    public void execute() {
        final Locker.Lock locked = this._locker.lock();
        Throwable x0 = null;
        try {
            switch (this._state) {
                case IDLE: {
                    this._state = State.PRODUCE;
                    break;
                }
                case PRODUCE:
                case EXECUTE: {
                    this._state = State.EXECUTE;
                    return;
                }
            }
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (locked != null) {
                $closeResource(x0, locked);
            }
        }
        while (true) {
            final Runnable task = this._producer.produce();
            if (ProduceExecuteConsume.LOG.isDebugEnabled()) {
                ProduceExecuteConsume.LOG.debug("{} produced {}", this._producer, task);
            }
            if (task == null) {
                final Locker.Lock locked2 = this._locker.lock();
                Throwable x2 = null;
                try {
                    switch (this._state) {
                        case IDLE: {
                            throw new IllegalStateException();
                        }
                        case PRODUCE: {
                            this._state = State.IDLE;
                            return;
                        }
                        case EXECUTE: {
                            this._state = State.PRODUCE;
                            continue;
                        }
                    }
                }
                catch (Throwable t2) {
                    x2 = t2;
                    throw t2;
                }
                finally {
                    if (locked2 != null) {
                        $closeResource(x2, locked2);
                    }
                }
            }
            this.execute(task);
        }
    }
    
    @Override
    public void dispatch() {
        this.execute();
    }
    
    private static /* synthetic */ void $closeResource(final Throwable x0, final AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            }
            catch (Throwable exception) {
                x0.addSuppressed(exception);
            }
        }
        else {
            x1.close();
        }
    }
    
    static {
        LOG = Log.getLogger(ProduceExecuteConsume.class);
    }
    
    public static class Factory implements ExecutionStrategy.Factory
    {
        @Override
        public ExecutionStrategy newExecutionStrategy(final Producer producer, final Executor executor) {
            return new ProduceExecuteConsume(producer, executor);
        }
    }
    
    private enum State
    {
        IDLE, 
        PRODUCE, 
        EXECUTE;
    }
}
