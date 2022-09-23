// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.thread.strategy;

import org.eclipse.jetty.util.log.Log;
import java.io.Closeable;
import java.util.concurrent.Executor;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.util.thread.Locker;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.thread.ExecutionStrategy;

public class ExecuteProduceConsume extends ExecutingExecutionStrategy implements ExecutionStrategy, Runnable
{
    private static final Logger LOG;
    private final Locker _locker;
    private final Runnable _runExecute;
    private final Producer _producer;
    private final ThreadPool _threadPool;
    private boolean _idle;
    private boolean _execute;
    private boolean _producing;
    private boolean _pending;
    private boolean _lowThreads;
    
    public ExecuteProduceConsume(final Producer producer, final Executor executor) {
        super(executor);
        this._locker = new Locker();
        this._runExecute = new RunExecute();
        this._idle = true;
        this._producer = producer;
        this._threadPool = ((executor instanceof ThreadPool) ? ((ThreadPool)executor) : null);
    }
    
    @Deprecated
    public ExecuteProduceConsume(final Producer producer, final Executor executor, final ExecutionStrategy lowResourceStrategy) {
        this(producer, executor);
    }
    
    @Override
    public void execute() {
        if (ExecuteProduceConsume.LOG.isDebugEnabled()) {
            ExecuteProduceConsume.LOG.debug("{} execute", this);
        }
        boolean produce = false;
        final Locker.Lock locked = this._locker.lock();
        Throwable x0 = null;
        try {
            if (this._idle) {
                if (this._producing) {
                    throw new IllegalStateException();
                }
                final boolean producing = true;
                this._producing = producing;
                produce = producing;
                this._idle = false;
            }
            else {
                this._execute = true;
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
        if (produce) {
            this.produceConsume();
        }
    }
    
    @Override
    public void dispatch() {
        if (ExecuteProduceConsume.LOG.isDebugEnabled()) {
            ExecuteProduceConsume.LOG.debug("{} spawning", this);
        }
        boolean dispatch = false;
        final Locker.Lock locked = this._locker.lock();
        Throwable x0 = null;
        try {
            if (this._idle) {
                dispatch = true;
            }
            else {
                this._execute = true;
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
        if (dispatch) {
            this.execute(this._runExecute);
        }
    }
    
    @Override
    public void run() {
        if (ExecuteProduceConsume.LOG.isDebugEnabled()) {
            ExecuteProduceConsume.LOG.debug("{} run", this);
        }
        boolean produce = false;
        final Locker.Lock locked = this._locker.lock();
        Throwable x0 = null;
        try {
            this._pending = false;
            if (!this._idle && !this._producing) {
                final boolean producing = true;
                this._producing = producing;
                produce = producing;
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
        if (produce) {
            this.produceConsume();
        }
    }
    
    private void produceConsume() {
        if (this._threadPool != null && this._threadPool.isLowOnThreads() && !this.produceExecuteConsume()) {
            return;
        }
        this.executeProduceConsume();
    }
    
    public boolean isLowOnThreads() {
        return this._lowThreads;
    }
    
    private boolean produceExecuteConsume() {
        if (ExecuteProduceConsume.LOG.isDebugEnabled()) {
            ExecuteProduceConsume.LOG.debug("{} enter low threads mode", this);
        }
        this._lowThreads = true;
        try {
            boolean idle = false;
            while (this._threadPool.isLowOnThreads()) {
                final Runnable task = this._producer.produce();
                if (ExecuteProduceConsume.LOG.isDebugEnabled()) {
                    ExecuteProduceConsume.LOG.debug("{} produced {}", this._producer, task);
                }
                if (task == null) {
                    final Locker.Lock locked = this._locker.lock();
                    Throwable x0 = null;
                    try {
                        if (this._execute) {
                            this._execute = false;
                            this._producing = true;
                            this._idle = false;
                            continue;
                        }
                        this._producing = false;
                        final boolean idle2 = true;
                        this._idle = idle2;
                        idle = idle2;
                        break;
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
                }
                this.executeProduct(task);
            }
            return !idle;
        }
        finally {
            this._lowThreads = false;
            if (ExecuteProduceConsume.LOG.isDebugEnabled()) {
                ExecuteProduceConsume.LOG.debug("{} exit low threads mode", this);
            }
        }
    }
    
    protected void executeProduct(final Runnable task) {
        if (task instanceof Rejectable) {
            try {
                ((Rejectable)task).reject();
                if (task instanceof Closeable) {
                    ((Closeable)task).close();
                }
            }
            catch (Throwable x) {
                ExecuteProduceConsume.LOG.debug(x);
            }
        }
        else {
            this.execute(task);
        }
    }
    
    private void executeProduceConsume() {
        if (ExecuteProduceConsume.LOG.isDebugEnabled()) {
            ExecuteProduceConsume.LOG.debug("{} produce enter", this);
        }
        while (true) {
            if (ExecuteProduceConsume.LOG.isDebugEnabled()) {
                ExecuteProduceConsume.LOG.debug("{} producing", this);
            }
            Runnable task = this._producer.produce();
            if (ExecuteProduceConsume.LOG.isDebugEnabled()) {
                ExecuteProduceConsume.LOG.debug("{} produced {}", this, task);
            }
            boolean dispatch = false;
            Locker.Lock locked = this._locker.lock();
            Throwable x0 = null;
            try {
                this._producing = false;
                if (task == null) {
                    if (this._execute) {
                        this._idle = false;
                        this._producing = true;
                        this._execute = false;
                        continue;
                    }
                    this._idle = true;
                    break;
                }
                else {
                    if (!this._pending) {
                        final boolean pending = true;
                        this._pending = pending;
                        dispatch = pending;
                    }
                    this._execute = false;
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
            if (dispatch) {
                if (ExecuteProduceConsume.LOG.isDebugEnabled()) {
                    ExecuteProduceConsume.LOG.debug("{} dispatch", this);
                }
                if (!this.execute(this)) {
                    task = null;
                }
            }
            if (ExecuteProduceConsume.LOG.isDebugEnabled()) {
                ExecuteProduceConsume.LOG.debug("{} run {}", this, task);
            }
            if (task != null) {
                task.run();
            }
            if (ExecuteProduceConsume.LOG.isDebugEnabled()) {
                ExecuteProduceConsume.LOG.debug("{} ran {}", this, task);
            }
            locked = this._locker.lock();
            Throwable x2 = null;
            try {
                if (this._producing || this._idle) {
                    break;
                }
                this._producing = true;
            }
            catch (Throwable t2) {
                x2 = t2;
                throw t2;
            }
            finally {
                if (locked != null) {
                    $closeResource(x2, locked);
                }
            }
        }
        if (ExecuteProduceConsume.LOG.isDebugEnabled()) {
            ExecuteProduceConsume.LOG.debug("{} produce exit", this);
        }
    }
    
    public Boolean isIdle() {
        final Locker.Lock locked = this._locker.lock();
        Throwable x0 = null;
        try {
            return this._idle;
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
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("EPC ");
        final Locker.Lock locked = this._locker.lock();
        Throwable x0 = null;
        try {
            builder.append(this._idle ? "Idle/" : "");
            builder.append(this._producing ? "Prod/" : "");
            builder.append(this._pending ? "Pend/" : "");
            builder.append(this._execute ? "Exec/" : "");
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
        builder.append(this._producer);
        return builder.toString();
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
        LOG = Log.getLogger(ExecuteProduceConsume.class);
    }
    
    private class RunExecute implements Runnable
    {
        @Override
        public void run() {
            ExecuteProduceConsume.this.execute();
        }
    }
    
    public static class Factory implements ExecutionStrategy.Factory
    {
        @Override
        public ExecutionStrategy newExecutionStrategy(final Producer producer, final Executor executor) {
            return new ExecuteProduceConsume(producer, executor);
        }
    }
}
