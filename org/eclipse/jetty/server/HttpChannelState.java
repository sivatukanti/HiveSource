// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.log.Log;
import javax.servlet.ServletResponse;
import org.eclipse.jetty.server.handler.ContextHandler;
import java.util.concurrent.TimeoutException;
import javax.servlet.ServletContext;
import org.eclipse.jetty.util.thread.Scheduler;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import javax.servlet.AsyncEvent;
import java.util.ArrayList;
import javax.servlet.AsyncListener;
import java.util.List;
import org.eclipse.jetty.util.thread.Locker;
import org.eclipse.jetty.util.log.Logger;

public class HttpChannelState
{
    private static final Logger LOG;
    private static final long DEFAULT_TIMEOUT;
    private final boolean DEBUG;
    private final Locker _locker;
    private final HttpChannel _channel;
    private List<AsyncListener> _asyncListeners;
    private State _state;
    private Async _async;
    private boolean _initial;
    private boolean _asyncReadPossible;
    private Interest _asyncRead;
    private boolean _asyncWritePossible;
    private long _timeoutMs;
    private AsyncContextEvent _event;
    
    protected HttpChannelState(final HttpChannel channel) {
        this.DEBUG = HttpChannelState.LOG.isDebugEnabled();
        this._locker = new Locker();
        this._asyncRead = Interest.NONE;
        this._timeoutMs = HttpChannelState.DEFAULT_TIMEOUT;
        this._channel = channel;
        this._state = State.IDLE;
        this._async = Async.NOT_ASYNC;
        this._initial = true;
    }
    
    public State getState() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return this._state;
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
    
    public void addListener(final AsyncListener listener) {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            if (this._asyncListeners == null) {
                this._asyncListeners = new ArrayList<AsyncListener>();
            }
            this._asyncListeners.add(listener);
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
    
    public void setTimeout(final long ms) {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            this._timeoutMs = ms;
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
    
    public long getTimeout() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return this._timeoutMs;
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
    
    public AsyncContextEvent getAsyncContextEvent() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return this._event;
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
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return String.format("%s@%x{s=%s a=%s i=%b r=%s/%s w=%b}", this.getClass().getSimpleName(), this.hashCode(), this._state, this._async, this._initial, this._asyncRead, this._asyncReadPossible, this._asyncWritePossible);
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
    
    private String getStatusStringLocked() {
        return String.format("s=%s i=%b a=%s", this._state, this._initial, this._async);
    }
    
    public String getStatusString() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return this.getStatusStringLocked();
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
    
    protected Action handling() {
        if (this.DEBUG) {
            HttpChannelState.LOG.debug("{} handling {}", this, this._state);
        }
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            switch (this._state) {
                case IDLE: {
                    this._initial = true;
                    this._state = State.DISPATCHED;
                    return Action.DISPATCH;
                }
                case COMPLETING:
                case COMPLETED: {
                    return Action.TERMINATED;
                }
                case ASYNC_WOKEN: {
                    if (this._asyncRead.isInterested() && this._asyncReadPossible) {
                        this._state = State.ASYNC_IO;
                        this._asyncRead = Interest.NONE;
                        return Action.READ_CALLBACK;
                    }
                    if (this._asyncWritePossible) {
                        this._state = State.ASYNC_IO;
                        this._asyncWritePossible = false;
                        return Action.WRITE_CALLBACK;
                    }
                    switch (this._async) {
                        case COMPLETE: {
                            this._state = State.COMPLETING;
                            return Action.COMPLETE;
                        }
                        case DISPATCH: {
                            this._state = State.DISPATCHED;
                            this._async = Async.NOT_ASYNC;
                            return Action.ASYNC_DISPATCH;
                        }
                        case EXPIRING: {
                            break;
                        }
                        case EXPIRED: {
                            this._state = State.DISPATCHED;
                            this._async = Async.NOT_ASYNC;
                            return Action.ERROR_DISPATCH;
                        }
                        case STARTED: {
                            return Action.WAIT;
                        }
                        case ERRORING: {
                            this._state = State.DISPATCHED;
                            return Action.ASYNC_ERROR;
                        }
                        case NOT_ASYNC: {
                            break;
                        }
                        default: {
                            throw new IllegalStateException(this.getStatusStringLocked());
                        }
                    }
                    return Action.WAIT;
                }
                default: {
                    throw new IllegalStateException(this.getStatusStringLocked());
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
    
    public void startAsync(final AsyncContextEvent event) {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        List<AsyncListener> lastAsyncListeners;
        try {
            if (this._state != State.DISPATCHED || this._async != Async.NOT_ASYNC) {
                throw new IllegalStateException(this.getStatusStringLocked());
            }
            this._async = Async.STARTED;
            this._event = event;
            lastAsyncListeners = this._asyncListeners;
            this._asyncListeners = null;
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
        if (lastAsyncListeners != null) {
            final Runnable callback = new Runnable() {
                @Override
                public void run() {
                    for (final AsyncListener listener : lastAsyncListeners) {
                        try {
                            listener.onStartAsync(event);
                        }
                        catch (Throwable e) {
                            HttpChannelState.LOG.warn(e);
                        }
                    }
                }
                
                @Override
                public String toString() {
                    return "startAsync";
                }
            };
            this.runInContext(event, callback);
        }
    }
    
    protected void error(final Throwable th) {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            if (this._event != null) {
                this._event.addThrowable(th);
            }
            this._async = Async.ERRORING;
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
    
    public void asyncError(final Throwable failure) {
        AsyncContextEvent event = null;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            switch (this._state) {
                case IDLE:
                case COMPLETING:
                case COMPLETED:
                case ASYNC_WOKEN:
                case ASYNC_IO:
                case DISPATCHED:
                case UPGRADED: {
                    break;
                }
                case ASYNC_WAIT: {
                    this._event.addThrowable(failure);
                    this._state = State.ASYNC_WOKEN;
                    this._async = Async.ERRORING;
                    event = this._event;
                    break;
                }
                default: {
                    throw new IllegalStateException(this.getStatusStringLocked());
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
        if (event != null) {
            this.cancelTimeout(event);
            this.runInContext(event, this._channel);
        }
    }
    
    protected Action unhandle() {
        boolean read_interested = false;
        if (this.DEBUG) {
            HttpChannelState.LOG.debug("{} unhandle {}", this, this._state);
        }
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        Action action = null;
        try {
            switch (this._state) {
                case COMPLETING:
                case COMPLETED: {
                    return Action.TERMINATED;
                }
                case ASYNC_IO:
                case DISPATCHED: {
                    this._initial = false;
                    switch (this._async) {
                        case COMPLETE: {
                            this._state = State.COMPLETING;
                            this._async = Async.NOT_ASYNC;
                            action = Action.COMPLETE;
                            break;
                        }
                        case DISPATCH: {
                            this._state = State.DISPATCHED;
                            this._async = Async.NOT_ASYNC;
                            action = Action.ASYNC_DISPATCH;
                            break;
                        }
                        case EXPIRED: {
                            this._state = State.DISPATCHED;
                            this._async = Async.NOT_ASYNC;
                            action = Action.ERROR_DISPATCH;
                            break;
                        }
                        case STARTED: {
                            if (this._asyncRead.isInterested() && this._asyncReadPossible) {
                                this._state = State.ASYNC_IO;
                                this._asyncRead = Interest.NONE;
                                action = Action.READ_CALLBACK;
                                break;
                            }
                            if (this._asyncWritePossible) {
                                this._state = State.ASYNC_IO;
                                this._asyncWritePossible = false;
                                action = Action.WRITE_CALLBACK;
                                break;
                            }
                            this._state = State.ASYNC_WAIT;
                            action = Action.WAIT;
                            if (this._asyncRead == Interest.NEEDED) {
                                this._asyncRead = Interest.REGISTERED;
                                read_interested = true;
                            }
                            final Scheduler scheduler = this._channel.getScheduler();
                            if (scheduler != null && this._timeoutMs > 0L) {
                                this._event.setTimeoutTask(scheduler.schedule(this._event, this._timeoutMs, TimeUnit.MILLISECONDS));
                            }
                            break;
                        }
                        case EXPIRING: {
                            this._state = State.ASYNC_WAIT;
                            action = Action.WAIT;
                            break;
                        }
                        case ERRORING: {
                            this._state = State.DISPATCHED;
                            action = Action.ASYNC_ERROR;
                            break;
                        }
                        case ERRORED: {
                            this._state = State.DISPATCHED;
                            action = Action.ERROR_DISPATCH;
                            this._async = Async.NOT_ASYNC;
                            break;
                        }
                        case NOT_ASYNC: {
                            this._state = State.COMPLETING;
                            action = Action.COMPLETE;
                            break;
                        }
                        default: {
                            this._state = State.COMPLETING;
                            action = Action.COMPLETE;
                            break;
                        }
                    }
                    break;
                }
                default: {
                    throw new IllegalStateException(this.getStatusStringLocked());
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
        if (read_interested) {
            this._channel.onAsyncWaitForContent();
        }
        return action;
    }
    
    public void dispatch(final ServletContext context, final String path) {
        boolean dispatch = false;
        AsyncContextEvent event = null;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            boolean started = false;
            event = this._event;
            switch (this._async) {
                case STARTED: {
                    started = true;
                    break;
                }
                case EXPIRING:
                case ERRORED: {
                    break;
                }
                default: {
                    throw new IllegalStateException(this.getStatusStringLocked());
                }
            }
            this._async = Async.DISPATCH;
            if (context != null) {
                this._event.setDispatchContext(context);
            }
            if (path != null) {
                this._event.setDispatchPath(path);
            }
            if (started) {
                switch (this._state) {
                    case ASYNC_WOKEN:
                    case ASYNC_IO:
                    case DISPATCHED: {
                        break;
                    }
                    case ASYNC_WAIT: {
                        this._state = State.ASYNC_WOKEN;
                        dispatch = true;
                        break;
                    }
                    default: {
                        HttpChannelState.LOG.warn("async dispatched when complete {}", this);
                        break;
                    }
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
        this.cancelTimeout(event);
        if (dispatch) {
            this.scheduleDispatch();
        }
    }
    
    protected void onTimeout() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        AsyncContextEvent event;
        List<AsyncListener> listeners;
        try {
            if (this._async != Async.STARTED) {
                return;
            }
            this._async = Async.EXPIRING;
            event = this._event;
            listeners = this._asyncListeners;
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
        if (HttpChannelState.LOG.isDebugEnabled()) {
            HttpChannelState.LOG.debug("Async timeout {}", this);
        }
        if (listeners != null) {
            final Runnable callback = new Runnable() {
                @Override
                public void run() {
                    for (final AsyncListener listener : listeners) {
                        try {
                            listener.onTimeout(event);
                        }
                        catch (Throwable e) {
                            HttpChannelState.LOG.debug(e);
                            event.addThrowable(e);
                            HttpChannelState.this._channel.getRequest().setAttribute("javax.servlet.error.exception", event.getThrowable());
                            break;
                        }
                    }
                }
                
                @Override
                public String toString() {
                    return "onTimeout";
                }
            };
            this.runInContext(event, callback);
        }
        boolean dispatch = false;
        final Locker.Lock lock2 = this._locker.lock();
        Throwable x2 = null;
        try {
            switch (this._async) {
                case EXPIRING: {
                    if (event.getThrowable() == null) {
                        this._async = Async.EXPIRED;
                        this._event.addThrowable(new TimeoutException("Async API violation"));
                        break;
                    }
                    this._async = Async.ERRORING;
                    break;
                }
                case COMPLETE:
                case DISPATCH: {
                    break;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
            if (this._state == State.ASYNC_WAIT) {
                this._state = State.ASYNC_WOKEN;
                dispatch = true;
            }
        }
        catch (Throwable t2) {
            x2 = t2;
            throw t2;
        }
        finally {
            if (lock2 != null) {
                $closeResource(x2, lock2);
            }
        }
        if (dispatch) {
            if (HttpChannelState.LOG.isDebugEnabled()) {
                HttpChannelState.LOG.debug("Dispatch after async timeout {}", this);
            }
            this.scheduleDispatch();
        }
    }
    
    public void complete() {
        boolean handle = false;
        AsyncContextEvent event = null;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            boolean started = false;
            event = this._event;
            switch (this._async) {
                case STARTED: {
                    started = true;
                    break;
                }
                case EXPIRING:
                case ERRORED: {
                    break;
                }
                default: {
                    throw new IllegalStateException(this.getStatusStringLocked());
                }
            }
            this._async = Async.COMPLETE;
            if (started && this._state == State.ASYNC_WAIT) {
                handle = true;
                this._state = State.ASYNC_WOKEN;
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
        this.cancelTimeout(event);
        if (handle) {
            this.runInContext(event, this._channel);
        }
    }
    
    public void errorComplete() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            this._async = Async.COMPLETE;
            this._event.setDispatchContext(null);
            this._event.setDispatchPath(null);
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
        this.cancelTimeout();
    }
    
    protected void onError() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x2 = null;
        List<AsyncListener> aListeners;
        AsyncContextEvent event;
        try {
            if (this._state != State.DISPATCHED) {
                throw new IllegalStateException(this.getStatusStringLocked());
            }
            aListeners = this._asyncListeners;
            event = this._event;
            this._async = Async.ERRORED;
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
        if (event != null && aListeners != null) {
            event.getSuppliedRequest().setAttribute("javax.servlet.error.exception", event.getThrowable());
            event.getSuppliedRequest().setAttribute("javax.servlet.error.message", event.getThrowable().getMessage());
            for (final AsyncListener listener : aListeners) {
                try {
                    listener.onError(event);
                }
                catch (Throwable x) {
                    HttpChannelState.LOG.info("Exception while invoking listener " + listener, x);
                }
            }
        }
    }
    
    protected void onComplete() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        List<AsyncListener> aListeners = null;
        AsyncContextEvent event = null;
        try {
            switch (this._state) {
                case COMPLETING: {
                    aListeners = this._asyncListeners;
                    event = this._event;
                    this._state = State.COMPLETED;
                    this._async = Async.NOT_ASYNC;
                    break;
                }
                default: {
                    throw new IllegalStateException(this.getStatusStringLocked());
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
        if (event != null) {
            if (aListeners != null) {
                final Runnable callback = new Runnable() {
                    @Override
                    public void run() {
                        for (final AsyncListener listener : aListeners) {
                            try {
                                listener.onComplete(event);
                            }
                            catch (Throwable e) {
                                HttpChannelState.LOG.warn(e);
                            }
                        }
                    }
                    
                    @Override
                    public String toString() {
                        return "onComplete";
                    }
                };
                this.runInContext(event, callback);
            }
            event.completed();
        }
    }
    
    protected void recycle() {
        this.cancelTimeout();
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            switch (this._state) {
                case ASYNC_IO:
                case DISPATCHED: {
                    throw new IllegalStateException(this.getStatusStringLocked());
                }
                case UPGRADED: {}
                default: {
                    this._asyncListeners = null;
                    this._state = State.IDLE;
                    this._async = Async.NOT_ASYNC;
                    this._initial = true;
                    this._asyncReadPossible = false;
                    this._asyncRead = Interest.NONE;
                    this._asyncWritePossible = false;
                    this._timeoutMs = HttpChannelState.DEFAULT_TIMEOUT;
                    this._event = null;
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
    }
    
    public void upgrade() {
        this.cancelTimeout();
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            switch (this._state) {
                case IDLE:
                case COMPLETED: {
                    this._asyncListeners = null;
                    this._state = State.UPGRADED;
                    this._async = Async.NOT_ASYNC;
                    this._initial = true;
                    this._asyncReadPossible = false;
                    this._asyncRead = Interest.NONE;
                    this._asyncWritePossible = false;
                    this._timeoutMs = HttpChannelState.DEFAULT_TIMEOUT;
                    this._event = null;
                    break;
                }
                default: {
                    throw new IllegalStateException(this.getStatusStringLocked());
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
    
    protected void scheduleDispatch() {
        this._channel.execute(this._channel);
    }
    
    protected void cancelTimeout() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        AsyncContextEvent event;
        try {
            event = this._event;
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
        this.cancelTimeout(event);
    }
    
    protected void cancelTimeout(final AsyncContextEvent event) {
        if (event != null) {
            event.cancelTimeoutTask();
        }
    }
    
    public boolean isIdle() {
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
    
    public boolean isExpired() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return this._async == Async.EXPIRED;
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
    
    public boolean isInitial() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return this._initial;
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
    
    public boolean isSuspended() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return this._state == State.ASYNC_WAIT || (this._state == State.DISPATCHED && this._async == Async.STARTED);
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
    
    boolean isCompleting() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return this._state == State.COMPLETING;
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
    
    boolean isCompleted() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return this._state == State.COMPLETED;
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
    
    public boolean isAsyncStarted() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            if (this._state == State.DISPATCHED) {
                return this._async != Async.NOT_ASYNC;
            }
            return this._async == Async.STARTED || this._async == Async.EXPIRING;
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
    
    public boolean isAsync() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return !this._initial || this._async != Async.NOT_ASYNC;
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
    
    public Request getBaseRequest() {
        return this._channel.getRequest();
    }
    
    public HttpChannel getHttpChannel() {
        return this._channel;
    }
    
    public ContextHandler getContextHandler() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        AsyncContextEvent event;
        try {
            event = this._event;
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
        return this.getContextHandler(event);
    }
    
    ContextHandler getContextHandler(final AsyncContextEvent event) {
        if (event != null) {
            final ContextHandler.Context context = (ContextHandler.Context)event.getServletContext();
            if (context != null) {
                return context.getContextHandler();
            }
        }
        return null;
    }
    
    public ServletResponse getServletResponse() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        AsyncContextEvent event;
        try {
            event = this._event;
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
        return this.getServletResponse(event);
    }
    
    public ServletResponse getServletResponse(final AsyncContextEvent event) {
        if (event != null && event.getSuppliedResponse() != null) {
            return event.getSuppliedResponse();
        }
        return this._channel.getResponse();
    }
    
    void runInContext(final AsyncContextEvent event, final Runnable runnable) {
        final ContextHandler contextHandler = this.getContextHandler(event);
        if (contextHandler == null) {
            runnable.run();
        }
        else {
            contextHandler.handle(this._channel.getRequest(), runnable);
        }
    }
    
    public Object getAttribute(final String name) {
        return this._channel.getRequest().getAttribute(name);
    }
    
    public void removeAttribute(final String name) {
        this._channel.getRequest().removeAttribute(name);
    }
    
    public void setAttribute(final String name, final Object attribute) {
        this._channel.getRequest().setAttribute(name, attribute);
    }
    
    public void onReadUnready() {
        boolean interested = false;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            if (this._asyncRead != Interest.REGISTERED) {
                this._asyncReadPossible = false;
                if (this._state == State.ASYNC_WAIT) {
                    interested = true;
                    this._asyncRead = Interest.REGISTERED;
                }
                else {
                    this._asyncRead = Interest.NEEDED;
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
        if (interested) {
            this._channel.onAsyncWaitForContent();
        }
    }
    
    public boolean onReadPossible() {
        boolean woken = false;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            this._asyncReadPossible = true;
            if (this._state == State.ASYNC_WAIT && this._asyncRead.isInterested()) {
                woken = true;
                this._state = State.ASYNC_WOKEN;
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
        return woken;
    }
    
    public boolean onReadReady() {
        boolean woken = false;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            this._asyncRead = Interest.REGISTERED;
            this._asyncReadPossible = true;
            if (this._state == State.ASYNC_WAIT) {
                woken = true;
                this._state = State.ASYNC_WOKEN;
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
        return woken;
    }
    
    public boolean isReadPossible() {
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            return this._asyncReadPossible;
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
    
    public boolean onWritePossible() {
        boolean handle = false;
        final Locker.Lock lock = this._locker.lock();
        Throwable x0 = null;
        try {
            this._asyncWritePossible = true;
            if (this._state == State.ASYNC_WAIT) {
                this._state = State.ASYNC_WOKEN;
                handle = true;
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
        return handle;
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
        LOG = Log.getLogger(HttpChannelState.class);
        DEFAULT_TIMEOUT = Long.getLong("org.eclipse.jetty.server.HttpChannelState.DEFAULT_TIMEOUT", 30000L);
    }
    
    public enum State
    {
        IDLE, 
        DISPATCHED, 
        ASYNC_WAIT, 
        ASYNC_WOKEN, 
        ASYNC_IO, 
        COMPLETING, 
        COMPLETED, 
        UPGRADED;
    }
    
    public enum Action
    {
        DISPATCH, 
        ASYNC_DISPATCH, 
        ERROR_DISPATCH, 
        ASYNC_ERROR, 
        WRITE_CALLBACK, 
        READ_CALLBACK, 
        COMPLETE, 
        TERMINATED, 
        WAIT;
    }
    
    public enum Async
    {
        NOT_ASYNC, 
        STARTED, 
        DISPATCH, 
        COMPLETE, 
        EXPIRING, 
        EXPIRED, 
        ERRORING, 
        ERRORED;
    }
    
    public enum Interest
    {
        NONE(false), 
        NEEDED(true), 
        REGISTERED(true);
        
        final boolean _interested;
        
        boolean isInterested() {
            return this._interested;
        }
        
        private Interest(final boolean interest) {
            this._interested = interest;
        }
    }
}
