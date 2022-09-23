// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.nio.channels.ClosedChannelException;
import org.eclipse.jetty.util.thread.Locker;

public abstract class IteratingCallback implements Callback
{
    private Locker _locker;
    private State _state;
    private boolean _iterate;
    
    protected IteratingCallback() {
        this._locker = new Locker();
        this._state = State.IDLE;
    }
    
    protected IteratingCallback(final boolean needReset) {
        this._locker = new Locker();
        this._state = (needReset ? State.SUCCEEDED : State.IDLE);
    }
    
    protected abstract Action process() throws Throwable;
    
    protected void onCompleteSuccess() {
    }
    
    protected void onCompleteFailure(final Throwable cause) {
    }
    
    public void iterate() {
        boolean process = false;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            switch (this._state) {
                case PENDING:
                case CALLED: {
                    break;
                }
                case IDLE: {
                    this._state = State.PROCESSING;
                    process = true;
                    break;
                }
                case PROCESSING: {
                    this._iterate = true;
                    break;
                }
                case FAILED:
                case SUCCEEDED: {
                    break;
                }
                default: {
                    throw new IllegalStateException(this.toString());
                }
            }
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
        if (process) {
            this.processing();
        }
    }
    
    private void processing() {
        boolean on_complete_success = false;
    Label_0379:
        while (true) {
            Action action;
            try {
                action = this.process();
            }
            catch (Throwable x) {
                this.failed(x);
                break;
            }
            final Locker.Lock lock = this._locker.lock();
            Throwable x2 = null;
            try {
                switch (this._state) {
                    case PROCESSING: {
                        switch (action) {
                            case IDLE: {
                                if (this._iterate) {
                                    this._iterate = false;
                                    this._state = State.PROCESSING;
                                    continue;
                                }
                                this._state = State.IDLE;
                                break Label_0379;
                            }
                            case SCHEDULED: {
                                this._state = State.PENDING;
                                break Label_0379;
                            }
                            case SUCCEEDED: {
                                this._iterate = false;
                                this._state = State.SUCCEEDED;
                                on_complete_success = true;
                                break Label_0379;
                            }
                            default: {
                                throw new IllegalStateException(String.format("%s[action=%s]", this, action));
                            }
                        }
                        break;
                    }
                    case CALLED: {
                        switch (action) {
                            case SCHEDULED: {
                                this._state = State.PROCESSING;
                                continue;
                            }
                            default: {
                                throw new IllegalStateException(String.format("%s[action=%s]", this, action));
                            }
                        }
                        break;
                    }
                    case FAILED:
                    case SUCCEEDED:
                    case CLOSED: {
                        break;
                    }
                    default: {
                        throw new IllegalStateException(String.format("%s[action=%s]", this, action));
                    }
                }
            }
            catch (Throwable t) {
                x2 = t;
                throw t;
            }
            finally {
                if (lock != null) {
                    $closeResource(x2, lock);
                }
            }
            break;
        }
        if (on_complete_success) {
            this.onCompleteSuccess();
        }
    }
    
    @Override
    public void succeeded() {
        boolean process = false;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            switch (this._state) {
                case PROCESSING: {
                    this._state = State.CALLED;
                    break;
                }
                case PENDING: {
                    this._state = State.PROCESSING;
                    process = true;
                    break;
                }
                case FAILED:
                case CLOSED: {
                    break;
                }
                default: {
                    throw new IllegalStateException(this.toString());
                }
            }
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
        if (process) {
            this.processing();
        }
    }
    
    @Override
    public void failed(final Throwable x) {
        boolean failure = false;
        final Locker.Lock lock = this._locker.lock();
        Throwable x2 = null;
        try {
            switch (this._state) {
                case CALLED:
                case IDLE:
                case FAILED:
                case SUCCEEDED:
                case CLOSED: {
                    break;
                }
                case PENDING:
                case PROCESSING: {
                    this._state = State.FAILED;
                    failure = true;
                    break;
                }
                default: {
                    throw new IllegalStateException(this.toString());
                }
            }
        }
        catch (Throwable t) {
            x2 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x2, lock);
            }
        }
        if (failure) {
            this.onCompleteFailure(x);
        }
    }
    
    public void close() {
        boolean failure = false;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            switch (this._state) {
                case IDLE:
                case FAILED:
                case SUCCEEDED: {
                    this._state = State.CLOSED;
                    break;
                }
                case CLOSED: {
                    break;
                }
                default: {
                    this._state = State.CLOSED;
                    failure = true;
                    break;
                }
            }
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
        if (failure) {
            this.onCompleteFailure(new ClosedChannelException());
        }
    }
    
    boolean isIdle() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return this._state == State.IDLE;
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
    }
    
    public boolean isClosed() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return this._state == State.CLOSED;
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
    }
    
    public boolean isFailed() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return this._state == State.FAILED;
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
    }
    
    public boolean isSucceeded() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return this._state == State.SUCCEEDED;
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
    }
    
    public boolean reset() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            switch (this._state) {
                case IDLE: {
                    return true;
                }
                case FAILED:
                case SUCCEEDED: {
                    this._iterate = false;
                    this._state = State.IDLE;
                    return true;
                }
                default: {
                    return false;
                }
            }
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (lock != null) {
                $closeResource(x0, lock);
            }
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s[%s]", super.toString(), this._state);
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
    
    private enum State
    {
        IDLE, 
        PROCESSING, 
        PENDING, 
        CALLED, 
        SUCCEEDED, 
        FAILED, 
        CLOSED;
    }
    
    protected enum Action
    {
        IDLE, 
        SCHEDULED, 
        SUCCEEDED;
    }
}
