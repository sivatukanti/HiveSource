// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import java.util.EnumSet;
import org.eclipse.jetty.util.log.Log;
import java.nio.channels.ClosedChannelException;
import java.util.Arrays;
import java.io.IOException;
import java.nio.channels.WritePendingException;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Set;
import java.util.EnumMap;
import java.nio.ByteBuffer;
import org.eclipse.jetty.util.log.Logger;

public abstract class WriteFlusher
{
    private static final Logger LOG;
    private static final boolean DEBUG;
    private static final ByteBuffer[] EMPTY_BUFFERS;
    private static final EnumMap<StateType, Set<StateType>> __stateTransitions;
    private static final State __IDLE;
    private static final State __WRITING;
    private static final State __COMPLETING;
    private final EndPoint _endPoint;
    private final AtomicReference<State> _state;
    
    protected WriteFlusher(final EndPoint endPoint) {
        (this._state = new AtomicReference<State>()).set(WriteFlusher.__IDLE);
        this._endPoint = endPoint;
    }
    
    private boolean updateState(final State previous, final State next) {
        if (!this.isTransitionAllowed(previous, next)) {
            throw new IllegalStateException();
        }
        final boolean updated = this._state.compareAndSet(previous, next);
        if (WriteFlusher.DEBUG) {
            WriteFlusher.LOG.debug("update {}:{}{}{}", this, previous, updated ? "-->" : "!->", next);
        }
        return updated;
    }
    
    private void fail(final PendingState pending) {
        final State current = this._state.get();
        if (current.getType() == StateType.FAILED) {
            final FailedState failed = (FailedState)current;
            if (this.updateState(failed, WriteFlusher.__IDLE)) {
                pending.fail(failed.getCause());
                return;
            }
        }
        throw new IllegalStateException();
    }
    
    private void ignoreFail() {
        for (State current = this._state.get(); current.getType() == StateType.FAILED; current = this._state.get()) {
            if (this.updateState(current, WriteFlusher.__IDLE)) {
                return;
            }
        }
    }
    
    private boolean isTransitionAllowed(final State currentState, final State newState) {
        final Set<StateType> allowedNewStateTypes = WriteFlusher.__stateTransitions.get(currentState.getType());
        if (!allowedNewStateTypes.contains(newState.getType())) {
            WriteFlusher.LOG.warn("{}: {} -> {} not allowed", this, currentState, newState);
            return false;
        }
        return true;
    }
    
    public boolean isCallbackNonBlocking() {
        final State s = this._state.get();
        return s instanceof PendingState && ((PendingState)s).isCallbackNonBlocking();
    }
    
    protected abstract void onIncompleteFlush();
    
    public void write(final Callback callback, ByteBuffer... buffers) throws WritePendingException {
        if (WriteFlusher.DEBUG) {
            WriteFlusher.LOG.debug("write: {} {}", this, BufferUtil.toDetailString(buffers));
        }
        if (!this.updateState(WriteFlusher.__IDLE, WriteFlusher.__WRITING)) {
            throw new WritePendingException();
        }
        try {
            buffers = this.flush(buffers);
            if (buffers != null) {
                if (WriteFlusher.DEBUG) {
                    WriteFlusher.LOG.debug("flushed incomplete", new Object[0]);
                }
                final PendingState pending = new PendingState(buffers, callback);
                if (this.updateState(WriteFlusher.__WRITING, pending)) {
                    this.onIncompleteFlush();
                }
                else {
                    this.fail(pending);
                }
                return;
            }
            if (!this.updateState(WriteFlusher.__WRITING, WriteFlusher.__IDLE)) {
                this.ignoreFail();
            }
            if (callback != null) {
                callback.succeeded();
            }
        }
        catch (IOException e) {
            if (WriteFlusher.DEBUG) {
                WriteFlusher.LOG.debug("write exception", e);
            }
            if (this.updateState(WriteFlusher.__WRITING, WriteFlusher.__IDLE)) {
                if (callback != null) {
                    callback.failed(e);
                }
            }
            else {
                this.fail(new PendingState(buffers, callback));
            }
        }
    }
    
    public void completeWrite() {
        if (WriteFlusher.DEBUG) {
            WriteFlusher.LOG.debug("completeWrite: {}", this);
        }
        final State previous = this._state.get();
        if (previous.getType() != StateType.PENDING) {
            return;
        }
        PendingState pending = (PendingState)previous;
        if (!this.updateState(pending, WriteFlusher.__COMPLETING)) {
            return;
        }
        try {
            ByteBuffer[] buffers = pending.getBuffers();
            buffers = this.flush(buffers);
            if (buffers != null) {
                if (WriteFlusher.DEBUG) {
                    WriteFlusher.LOG.debug("flushed incomplete {}", BufferUtil.toDetailString(buffers));
                }
                if (buffers != pending.getBuffers()) {
                    pending = new PendingState(buffers, pending._callback);
                }
                if (this.updateState(WriteFlusher.__COMPLETING, pending)) {
                    this.onIncompleteFlush();
                }
                else {
                    this.fail(pending);
                }
                return;
            }
            if (!this.updateState(WriteFlusher.__COMPLETING, WriteFlusher.__IDLE)) {
                this.ignoreFail();
            }
            pending.complete();
        }
        catch (IOException e) {
            if (WriteFlusher.DEBUG) {
                WriteFlusher.LOG.debug("completeWrite exception", e);
            }
            if (this.updateState(WriteFlusher.__COMPLETING, WriteFlusher.__IDLE)) {
                pending.fail(e);
            }
            else {
                this.fail(pending);
            }
        }
    }
    
    protected ByteBuffer[] flush(ByteBuffer[] buffers) throws IOException {
        boolean progress = true;
        while (progress && buffers != null) {
            final int before = (buffers.length == 0) ? 0 : buffers[0].remaining();
            final boolean flushed = this._endPoint.flush(buffers);
            int r = (buffers.length == 0) ? 0 : buffers[0].remaining();
            if (WriteFlusher.LOG.isDebugEnabled()) {
                WriteFlusher.LOG.debug("Flushed={} {}/{}+{} {}", flushed, before - r, before, buffers.length - 1, this);
            }
            if (flushed) {
                return null;
            }
            progress = (before != r);
            int not_empty;
            for (not_empty = 0; r == 0; r = buffers[not_empty].remaining()) {
                if (++not_empty == buffers.length) {
                    buffers = null;
                    not_empty = 0;
                    break;
                }
                progress = true;
            }
            if (not_empty <= 0) {
                continue;
            }
            buffers = Arrays.copyOfRange(buffers, not_empty, buffers.length);
        }
        if (WriteFlusher.LOG.isDebugEnabled()) {
            WriteFlusher.LOG.debug("!fully flushed {}", this);
        }
        return (buffers == null) ? WriteFlusher.EMPTY_BUFFERS : buffers;
    }
    
    public boolean onFail(final Throwable cause) {
        while (true) {
            final State current = this._state.get();
            switch (current.getType()) {
                case IDLE:
                case FAILED: {
                    if (WriteFlusher.DEBUG) {
                        WriteFlusher.LOG.debug("ignored: {} {}", this, cause);
                    }
                    return false;
                }
                case PENDING: {
                    if (WriteFlusher.DEBUG) {
                        WriteFlusher.LOG.debug("failed: {} {}", this, cause);
                    }
                    final PendingState pending = (PendingState)current;
                    if (this.updateState(pending, WriteFlusher.__IDLE)) {
                        return pending.fail(cause);
                    }
                    continue;
                }
                default: {
                    if (WriteFlusher.DEBUG) {
                        WriteFlusher.LOG.debug("failed: {} {}", this, cause);
                    }
                    if (this.updateState(current, new FailedState(cause))) {
                        return false;
                    }
                    continue;
                }
            }
        }
    }
    
    public void onClose() {
        this.onFail(new ClosedChannelException());
    }
    
    boolean isIdle() {
        return this._state.get().getType() == StateType.IDLE;
    }
    
    public boolean isInProgress() {
        switch (this._state.get().getType()) {
            case PENDING:
            case WRITING:
            case COMPLETING: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    @Override
    public String toString() {
        return String.format("WriteFlusher@%x{%s}", this.hashCode(), this._state.get());
    }
    
    public String toStateString() {
        switch (this._state.get().getType()) {
            case WRITING: {
                return "W";
            }
            case PENDING: {
                return "P";
            }
            case COMPLETING: {
                return "C";
            }
            case IDLE: {
                return "-";
            }
            case FAILED: {
                return "F";
            }
            default: {
                return "?";
            }
        }
    }
    
    static {
        LOG = Log.getLogger(WriteFlusher.class);
        DEBUG = WriteFlusher.LOG.isDebugEnabled();
        EMPTY_BUFFERS = new ByteBuffer[] { BufferUtil.EMPTY_BUFFER };
        __stateTransitions = new EnumMap<StateType, Set<StateType>>(StateType.class);
        __IDLE = new IdleState();
        __WRITING = new WritingState();
        __COMPLETING = new CompletingState();
        WriteFlusher.__stateTransitions.put(StateType.IDLE, EnumSet.of(StateType.WRITING));
        WriteFlusher.__stateTransitions.put(StateType.WRITING, EnumSet.of(StateType.IDLE, StateType.PENDING, StateType.FAILED));
        WriteFlusher.__stateTransitions.put(StateType.PENDING, EnumSet.of(StateType.COMPLETING, StateType.IDLE));
        WriteFlusher.__stateTransitions.put(StateType.COMPLETING, EnumSet.of(StateType.IDLE, StateType.PENDING, StateType.FAILED));
        WriteFlusher.__stateTransitions.put(StateType.FAILED, EnumSet.of(StateType.IDLE));
    }
    
    private enum StateType
    {
        IDLE, 
        WRITING, 
        PENDING, 
        COMPLETING, 
        FAILED;
    }
    
    private static class State
    {
        private final StateType _type;
        
        private State(final StateType stateType) {
            this._type = stateType;
        }
        
        public StateType getType() {
            return this._type;
        }
        
        @Override
        public String toString() {
            return String.format("%s", this._type);
        }
    }
    
    private static class IdleState extends State
    {
        private IdleState() {
            super(StateType.IDLE);
        }
    }
    
    private static class WritingState extends State
    {
        private WritingState() {
            super(StateType.WRITING);
        }
    }
    
    private static class FailedState extends State
    {
        private final Throwable _cause;
        
        private FailedState(final Throwable cause) {
            super(StateType.FAILED);
            this._cause = cause;
        }
        
        public Throwable getCause() {
            return this._cause;
        }
    }
    
    private static class CompletingState extends State
    {
        private CompletingState() {
            super(StateType.COMPLETING);
        }
    }
    
    private class PendingState extends State
    {
        private final Callback _callback;
        private final ByteBuffer[] _buffers;
        
        private PendingState(final ByteBuffer[] buffers, final Callback callback) {
            super(StateType.PENDING);
            this._buffers = buffers;
            this._callback = callback;
        }
        
        public ByteBuffer[] getBuffers() {
            return this._buffers;
        }
        
        protected boolean fail(final Throwable cause) {
            if (this._callback != null) {
                this._callback.failed(cause);
                return true;
            }
            return false;
        }
        
        protected void complete() {
            if (this._callback != null) {
                this._callback.succeeded();
            }
        }
        
        boolean isCallbackNonBlocking() {
            return this._callback != null && this._callback.isNonBlocking();
        }
    }
}
