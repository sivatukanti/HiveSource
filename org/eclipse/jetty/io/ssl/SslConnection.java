// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io.ssl;

import java.util.Iterator;
import org.eclipse.jetty.io.EofException;
import java.nio.channels.ClosedChannelException;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import org.eclipse.jetty.io.Connection;
import java.io.IOException;
import javax.net.ssl.SSLEngineResult;
import org.eclipse.jetty.io.WriteFlusher;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.io.AbstractEndPoint;
import org.eclipse.jetty.util.log.Log;
import java.util.ArrayList;
import org.eclipse.jetty.io.EndPoint;
import java.util.concurrent.Executor;
import org.eclipse.jetty.util.Callback;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLEngine;
import org.eclipse.jetty.io.ByteBufferPool;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.io.AbstractConnection;

public class SslConnection extends AbstractConnection
{
    private static final Logger LOG;
    private final List<SslHandshakeListener> handshakeListeners;
    private final ByteBufferPool _bufferPool;
    private final SSLEngine _sslEngine;
    private final DecryptedEndPoint _decryptedEndPoint;
    private ByteBuffer _decryptedInput;
    private ByteBuffer _encryptedInput;
    private ByteBuffer _encryptedOutput;
    private final boolean _encryptedDirectBuffers = true;
    private final boolean _decryptedDirectBuffers = false;
    private boolean _renegotiationAllowed;
    private int _renegotiationLimit;
    private boolean _closedOutbound;
    private boolean _allowMissingCloseMessage;
    private final Runnable _runCompletWrite;
    private final Runnable _runFillable;
    private final Callback _nonBlockingReadCallback;
    
    public SslConnection(final ByteBufferPool byteBufferPool, final Executor executor, final EndPoint endPoint, final SSLEngine sslEngine) {
        super(endPoint, executor);
        this.handshakeListeners = new ArrayList<SslHandshakeListener>();
        this._renegotiationLimit = -1;
        this._allowMissingCloseMessage = true;
        this._runCompletWrite = new Runnable() {
            @Override
            public void run() {
                SslConnection.this._decryptedEndPoint.getWriteFlusher().completeWrite();
            }
        };
        this._runFillable = new Runnable() {
            @Override
            public void run() {
                SslConnection.this._decryptedEndPoint.getFillInterest().fillable();
            }
        };
        this._nonBlockingReadCallback = new Callback.NonBlocking() {
            @Override
            public void succeeded() {
                SslConnection.this.onFillable();
            }
            
            @Override
            public void failed(final Throwable x) {
                SslConnection.this.onFillInterestedFailed(x);
            }
            
            @Override
            public String toString() {
                return String.format("SSLC.NBReadCB@%x{%s}", SslConnection.this.hashCode(), SslConnection.this);
            }
        };
        this._bufferPool = byteBufferPool;
        this._sslEngine = sslEngine;
        this._decryptedEndPoint = this.newDecryptedEndPoint();
    }
    
    public void addHandshakeListener(final SslHandshakeListener listener) {
        this.handshakeListeners.add(listener);
    }
    
    public boolean removeHandshakeListener(final SslHandshakeListener listener) {
        return this.handshakeListeners.remove(listener);
    }
    
    protected DecryptedEndPoint newDecryptedEndPoint() {
        return new DecryptedEndPoint();
    }
    
    public SSLEngine getSSLEngine() {
        return this._sslEngine;
    }
    
    public DecryptedEndPoint getDecryptedEndPoint() {
        return this._decryptedEndPoint;
    }
    
    public boolean isRenegotiationAllowed() {
        return this._renegotiationAllowed;
    }
    
    public void setRenegotiationAllowed(final boolean renegotiationAllowed) {
        this._renegotiationAllowed = renegotiationAllowed;
    }
    
    public int getRenegotiationLimit() {
        return this._renegotiationLimit;
    }
    
    public void setRenegotiationLimit(final int renegotiationLimit) {
        this._renegotiationLimit = renegotiationLimit;
    }
    
    public boolean isAllowMissingCloseMessage() {
        return this._allowMissingCloseMessage;
    }
    
    public void setAllowMissingCloseMessage(final boolean allowMissingCloseMessage) {
        this._allowMissingCloseMessage = allowMissingCloseMessage;
    }
    
    @Override
    public void onOpen() {
        super.onOpen();
        this.getDecryptedEndPoint().getConnection().onOpen();
    }
    
    @Override
    public void onClose() {
        this._decryptedEndPoint.getConnection().onClose();
        super.onClose();
    }
    
    @Override
    public void close() {
        this.getDecryptedEndPoint().getConnection().close();
    }
    
    @Override
    public boolean onIdleExpired() {
        return this.getDecryptedEndPoint().getConnection().onIdleExpired();
    }
    
    @Override
    public void onFillable() {
        if (SslConnection.LOG.isDebugEnabled()) {
            SslConnection.LOG.debug("onFillable enter {}", this._decryptedEndPoint);
        }
        if (this._decryptedEndPoint.isInputShutdown()) {
            this._decryptedEndPoint.close();
        }
        this._decryptedEndPoint.getFillInterest().fillable();
        boolean runComplete = false;
        synchronized (this._decryptedEndPoint) {
            if (this._decryptedEndPoint._flushRequiresFillToProgress) {
                this._decryptedEndPoint._flushRequiresFillToProgress = false;
                runComplete = true;
            }
        }
        if (runComplete) {
            this._runCompletWrite.run();
        }
        if (SslConnection.LOG.isDebugEnabled()) {
            SslConnection.LOG.debug("onFillable exit {}", this._decryptedEndPoint);
        }
    }
    
    public void onFillInterestedFailed(final Throwable cause) {
        this._decryptedEndPoint.getFillInterest().onFail(cause);
        boolean failFlusher = false;
        synchronized (this._decryptedEndPoint) {
            if (this._decryptedEndPoint._flushRequiresFillToProgress) {
                this._decryptedEndPoint._flushRequiresFillToProgress = false;
                failFlusher = true;
            }
        }
        if (failFlusher) {
            this._decryptedEndPoint.getWriteFlusher().onFail(cause);
        }
    }
    
    @Override
    public String toString() {
        ByteBuffer b = this._encryptedInput;
        final int ei = (b == null) ? -1 : b.remaining();
        b = this._encryptedOutput;
        final int eo = (b == null) ? -1 : b.remaining();
        b = this._decryptedInput;
        final int di = (b == null) ? -1 : b.remaining();
        return String.format("SslConnection@%x{%s,eio=%d/%d,di=%d} -> %s", this.hashCode(), this._sslEngine.getHandshakeStatus(), ei, eo, di, this._decryptedEndPoint.getConnection());
    }
    
    static {
        LOG = Log.getLogger(SslConnection.class);
    }
    
    public class DecryptedEndPoint extends AbstractEndPoint
    {
        private boolean _fillRequiresFlushToProgress;
        private boolean _flushRequiresFillToProgress;
        private boolean _cannotAcceptMoreAppDataToFlush;
        private boolean _handshaken;
        private boolean _underFlown;
        private final Callback _writeCallback;
        
        public DecryptedEndPoint() {
            super(null, SslConnection.this.getEndPoint().getLocalAddress(), SslConnection.this.getEndPoint().getRemoteAddress());
            this._writeCallback = new Callback() {
                @Override
                public void succeeded() {
                    boolean fillable = false;
                    synchronized (DecryptedEndPoint.this) {
                        if (SslConnection.LOG.isDebugEnabled()) {
                            SslConnection.LOG.debug("write.complete {}", SslConnection.this.getEndPoint());
                        }
                        DecryptedEndPoint.this.releaseEncryptedOutputBuffer();
                        DecryptedEndPoint.this._cannotAcceptMoreAppDataToFlush = false;
                        if (DecryptedEndPoint.this._fillRequiresFlushToProgress) {
                            DecryptedEndPoint.this._fillRequiresFlushToProgress = false;
                            fillable = true;
                        }
                    }
                    if (fillable) {
                        DecryptedEndPoint.this.getFillInterest().fillable();
                    }
                    SslConnection.this._runCompletWrite.run();
                }
                
                @Override
                public void failed(final Throwable x) {
                    final boolean fail_filler;
                    synchronized (DecryptedEndPoint.this) {
                        if (SslConnection.LOG.isDebugEnabled()) {
                            SslConnection.LOG.debug("write failed {}", SslConnection.this, x);
                        }
                        BufferUtil.clear(SslConnection.this._encryptedOutput);
                        DecryptedEndPoint.this.releaseEncryptedOutputBuffer();
                        DecryptedEndPoint.this._cannotAcceptMoreAppDataToFlush = false;
                        fail_filler = DecryptedEndPoint.this._fillRequiresFlushToProgress;
                        if (DecryptedEndPoint.this._fillRequiresFlushToProgress) {
                            DecryptedEndPoint.this._fillRequiresFlushToProgress = false;
                        }
                    }
                    AbstractConnection.this.failedCallback(new Callback() {
                        @Override
                        public void failed(final Throwable x) {
                            if (fail_filler) {
                                DecryptedEndPoint.this.getFillInterest().onFail(x);
                            }
                            DecryptedEndPoint.this.getWriteFlusher().onFail(x);
                        }
                    }, x);
                }
                
                @Override
                public boolean isNonBlocking() {
                    return DecryptedEndPoint.this.getWriteFlusher().isCallbackNonBlocking();
                }
            };
            super.setIdleTimeout(-1L);
        }
        
        @Override
        public long getIdleTimeout() {
            return SslConnection.this.getEndPoint().getIdleTimeout();
        }
        
        @Override
        public void setIdleTimeout(final long idleTimeout) {
            SslConnection.this.getEndPoint().setIdleTimeout(idleTimeout);
        }
        
        @Override
        public boolean isOpen() {
            return SslConnection.this.getEndPoint().isOpen();
        }
        
        @Override
        protected WriteFlusher getWriteFlusher() {
            return super.getWriteFlusher();
        }
        
        @Override
        protected void onIncompleteFlush() {
            boolean try_again = false;
            boolean write = false;
            boolean need_fill_interest = false;
            synchronized (this) {
                if (SslConnection.LOG.isDebugEnabled()) {
                    SslConnection.LOG.debug("onIncompleteFlush {}", SslConnection.this);
                }
                if (BufferUtil.hasContent(SslConnection.this._encryptedOutput)) {
                    this._cannotAcceptMoreAppDataToFlush = true;
                    write = true;
                }
                else if (SslConnection.this._sslEngine.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_UNWRAP) {
                    this._flushRequiresFillToProgress = true;
                    need_fill_interest = !SslConnection.this.isFillInterested();
                }
                else {
                    try_again = true;
                }
            }
            if (write) {
                SslConnection.this.getEndPoint().write(this._writeCallback, SslConnection.this._encryptedOutput);
            }
            else if (need_fill_interest) {
                this.ensureFillInterested();
            }
            else if (try_again) {
                if (this.isOutputShutdown()) {
                    this.getWriteFlusher().onClose();
                }
                else {
                    AbstractConnection.this.getExecutor().execute(SslConnection.this._runCompletWrite);
                }
            }
        }
        
        @Override
        protected void needsFillInterest() throws IOException {
            boolean write = false;
            boolean fillable;
            synchronized (this) {
                fillable = (BufferUtil.hasContent(SslConnection.this._decryptedInput) || (BufferUtil.hasContent(SslConnection.this._encryptedInput) && !this._underFlown));
                if (!fillable && this._fillRequiresFlushToProgress) {
                    if (BufferUtil.hasContent(SslConnection.this._encryptedOutput)) {
                        this._cannotAcceptMoreAppDataToFlush = true;
                        write = true;
                    }
                    else {
                        this._fillRequiresFlushToProgress = false;
                        fillable = true;
                    }
                }
            }
            if (write) {
                SslConnection.this.getEndPoint().write(this._writeCallback, SslConnection.this._encryptedOutput);
            }
            else if (fillable) {
                AbstractConnection.this.getExecutor().execute(SslConnection.this._runFillable);
            }
            else {
                this.ensureFillInterested();
            }
        }
        
        @Override
        public void setConnection(final Connection connection) {
            if (connection instanceof AbstractConnection) {
                final AbstractConnection a = (AbstractConnection)connection;
                if (a.getInputBufferSize() < SslConnection.this._sslEngine.getSession().getApplicationBufferSize()) {
                    a.setInputBufferSize(SslConnection.this._sslEngine.getSession().getApplicationBufferSize());
                }
            }
            super.setConnection(connection);
        }
        
        public SslConnection getSslConnection() {
            return SslConnection.this;
        }
        
        @Override
        public int fill(final ByteBuffer buffer) throws IOException {
            try {
                synchronized (this) {
                    Throwable failure = null;
                    try {
                        if (BufferUtil.hasContent(SslConnection.this._decryptedInput)) {
                            return BufferUtil.append(buffer, SslConnection.this._decryptedInput);
                        }
                        if (SslConnection.this._encryptedInput == null) {
                            SslConnection.this._encryptedInput = SslConnection.this._bufferPool.acquire(SslConnection.this._sslEngine.getSession().getPacketBufferSize(), true);
                        }
                        else {
                            BufferUtil.compact(SslConnection.this._encryptedInput);
                        }
                        ByteBuffer app_in;
                        if (BufferUtil.space(buffer) > SslConnection.this._sslEngine.getSession().getApplicationBufferSize()) {
                            app_in = buffer;
                        }
                        else if (SslConnection.this._decryptedInput == null) {
                            app_in = (SslConnection.this._decryptedInput = SslConnection.this._bufferPool.acquire(SslConnection.this._sslEngine.getSession().getApplicationBufferSize(), false));
                        }
                        else {
                            app_in = SslConnection.this._decryptedInput;
                        }
                    Label_2349:
                        while (true) {
                            final int net_filled = SslConnection.this.getEndPoint().fill(SslConnection.this._encryptedInput);
                            if (net_filled > 0 && !this._handshaken && SslConnection.this._sslEngine.isOutboundDone()) {
                                throw new SSLHandshakeException("Closed during handshake");
                            }
                            while (true) {
                                final int pos = BufferUtil.flipToFill(app_in);
                                SSLEngineResult unwrapResult;
                                try {
                                    unwrapResult = SslConnection.this._sslEngine.unwrap(SslConnection.this._encryptedInput, app_in);
                                }
                                finally {
                                    BufferUtil.flipToFlush(app_in, pos);
                                }
                                if (SslConnection.LOG.isDebugEnabled()) {
                                    SslConnection.LOG.debug("net={} unwrap {} {}", net_filled, unwrapResult.toString().replace('\n', ' '), SslConnection.this);
                                    SslConnection.LOG.debug("filled {} {}", BufferUtil.toHexSummary(buffer), SslConnection.this);
                                }
                                final SSLEngineResult.HandshakeStatus handshakeStatus = SslConnection.this._sslEngine.getHandshakeStatus();
                                final SSLEngineResult.HandshakeStatus unwrapHandshakeStatus = unwrapResult.getHandshakeStatus();
                                final SSLEngineResult.Status unwrapResultStatus = unwrapResult.getStatus();
                                this._underFlown = (unwrapResultStatus == SSLEngineResult.Status.BUFFER_UNDERFLOW || (unwrapResultStatus == SSLEngineResult.Status.OK && unwrapResult.bytesConsumed() == 0 && unwrapResult.bytesProduced() == 0));
                                if (this._underFlown) {
                                    if (net_filled < 0 && SslConnection.this._sslEngine.getUseClientMode()) {
                                        this.closeInbound();
                                    }
                                    if (net_filled <= 0) {
                                        return net_filled;
                                    }
                                }
                                switch (unwrapResultStatus) {
                                    case CLOSED: {
                                        switch (handshakeStatus) {
                                            case NOT_HANDSHAKING: {
                                                return -1;
                                            }
                                            case NEED_TASK: {
                                                SslConnection.this._sslEngine.getDelegatedTask().run();
                                                continue;
                                            }
                                            case NEED_WRAP: {
                                                return -1;
                                            }
                                            case NEED_UNWRAP: {
                                                return -1;
                                            }
                                            default: {
                                                throw new IllegalStateException();
                                            }
                                        }
                                        break;
                                    }
                                    case BUFFER_UNDERFLOW:
                                    case OK: {
                                        if (unwrapHandshakeStatus == SSLEngineResult.HandshakeStatus.FINISHED) {
                                            this.handshakeFinished();
                                        }
                                        if (!this.allowRenegotiate(handshakeStatus)) {
                                            return -1;
                                        }
                                        if (unwrapResult.bytesProduced() > 0) {
                                            if (app_in == buffer) {
                                                return unwrapResult.bytesProduced();
                                            }
                                            return BufferUtil.append(buffer, SslConnection.this._decryptedInput);
                                        }
                                        else {
                                            switch (handshakeStatus) {
                                                case NOT_HANDSHAKING: {
                                                    if (this._underFlown) {
                                                        continue Label_2349;
                                                    }
                                                    continue;
                                                }
                                                case NEED_TASK: {
                                                    SslConnection.this._sslEngine.getDelegatedTask().run();
                                                    continue;
                                                }
                                                case NEED_WRAP: {
                                                    if (this._flushRequiresFillToProgress) {
                                                        return 0;
                                                    }
                                                    this._fillRequiresFlushToProgress = true;
                                                    this.flush(BufferUtil.EMPTY_BUFFER);
                                                    if (!BufferUtil.isEmpty(SslConnection.this._encryptedOutput)) {
                                                        return 0;
                                                    }
                                                    this._fillRequiresFlushToProgress = false;
                                                    if (this._underFlown) {
                                                        continue Label_2349;
                                                    }
                                                    continue;
                                                }
                                                case NEED_UNWRAP: {
                                                    if (this._underFlown) {
                                                        continue Label_2349;
                                                    }
                                                    continue;
                                                }
                                                default: {
                                                    throw new IllegalStateException();
                                                }
                                            }
                                        }
                                        break;
                                    }
                                    default: {
                                        throw new IllegalStateException();
                                    }
                                }
                            }
                        }
                    }
                    catch (SSLHandshakeException x) {
                        this.notifyHandshakeFailed(SslConnection.this._sslEngine, x);
                        failure = x;
                        throw x;
                    }
                    catch (SSLException x2) {
                        if (!this._handshaken) {
                            x2 = (SSLException)new SSLHandshakeException(x2.getMessage()).initCause(x2);
                            this.notifyHandshakeFailed(SslConnection.this._sslEngine, x2);
                        }
                        failure = x2;
                        throw x2;
                    }
                    catch (Throwable x3) {
                        failure = x3;
                        throw x3;
                    }
                    finally {
                        if (this._flushRequiresFillToProgress) {
                            this._flushRequiresFillToProgress = false;
                            AbstractConnection.this.getExecutor().execute((failure == null) ? SslConnection.this._runCompletWrite : new FailWrite(failure));
                        }
                        if (SslConnection.this._encryptedInput != null && !SslConnection.this._encryptedInput.hasRemaining()) {
                            SslConnection.this._bufferPool.release(SslConnection.this._encryptedInput);
                            SslConnection.this._encryptedInput = null;
                        }
                        if (SslConnection.this._decryptedInput != null && !SslConnection.this._decryptedInput.hasRemaining()) {
                            SslConnection.this._bufferPool.release(SslConnection.this._decryptedInput);
                            SslConnection.this._decryptedInput = null;
                        }
                    }
                }
            }
            catch (Throwable x4) {
                this.close(x4);
                throw x4;
            }
        }
        
        private void handshakeFinished() {
            if (this._handshaken) {
                if (SslConnection.LOG.isDebugEnabled()) {
                    SslConnection.LOG.debug("Renegotiated {}", SslConnection.this);
                }
                if (SslConnection.this._renegotiationLimit > 0) {
                    SslConnection.this._renegotiationLimit--;
                }
            }
            else {
                this._handshaken = true;
                if (SslConnection.LOG.isDebugEnabled()) {
                    SslConnection.LOG.debug("{} handshake succeeded {}/{} {}", SslConnection.this._sslEngine.getUseClientMode() ? "client" : "resumed server", SslConnection.this._sslEngine.getSession().getProtocol(), SslConnection.this._sslEngine.getSession().getCipherSuite(), SslConnection.this);
                }
                this.notifyHandshakeSucceeded(SslConnection.this._sslEngine);
            }
        }
        
        private boolean allowRenegotiate(final SSLEngineResult.HandshakeStatus handshakeStatus) {
            if (!this._handshaken || handshakeStatus == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING) {
                return true;
            }
            if (!SslConnection.this.isRenegotiationAllowed()) {
                if (SslConnection.LOG.isDebugEnabled()) {
                    SslConnection.LOG.debug("Renegotiation denied {}", SslConnection.this);
                }
                this.shutdownInput();
                return false;
            }
            if (SslConnection.this.getRenegotiationLimit() == 0) {
                if (SslConnection.LOG.isDebugEnabled()) {
                    SslConnection.LOG.debug("Renegotiation limit exceeded {}", SslConnection.this);
                }
                this.shutdownInput();
                return false;
            }
            return true;
        }
        
        private void shutdownInput() {
            try {
                SslConnection.this._sslEngine.closeInbound();
            }
            catch (Throwable x) {
                SslConnection.LOG.ignore(x);
            }
        }
        
        private void closeInbound() throws SSLException {
            final SSLEngineResult.HandshakeStatus handshakeStatus = SslConnection.this._sslEngine.getHandshakeStatus();
            try {
                SslConnection.this._sslEngine.closeInbound();
            }
            catch (SSLException x) {
                if (handshakeStatus == SSLEngineResult.HandshakeStatus.NOT_HANDSHAKING && !SslConnection.this.isAllowMissingCloseMessage()) {
                    throw x;
                }
                SslConnection.LOG.ignore(x);
            }
        }
        
        @Override
        public boolean flush(final ByteBuffer... appOuts) throws IOException {
            if (SslConnection.LOG.isDebugEnabled()) {
                for (final ByteBuffer b : appOuts) {
                    SslConnection.LOG.debug("flush {} {}", BufferUtil.toHexSummary(b), SslConnection.this);
                }
            }
            try {
                synchronized (this) {
                    try {
                        if (!this._cannotAcceptMoreAppDataToFlush) {
                            if (SslConnection.this._encryptedOutput == null) {
                                SslConnection.this._encryptedOutput = SslConnection.this._bufferPool.acquire(SslConnection.this._sslEngine.getSession().getPacketBufferSize(), true);
                            }
                            boolean allConsumed = false;
                        Label_0844:
                            while (true) {
                                BufferUtil.compact(SslConnection.this._encryptedOutput);
                                final int pos = BufferUtil.flipToFill(SslConnection.this._encryptedOutput);
                                SSLEngineResult wrapResult;
                                try {
                                    wrapResult = SslConnection.this._sslEngine.wrap(appOuts, SslConnection.this._encryptedOutput);
                                }
                                finally {
                                    BufferUtil.flipToFlush(SslConnection.this._encryptedOutput, pos);
                                }
                                if (SslConnection.LOG.isDebugEnabled()) {
                                    SslConnection.LOG.debug("wrap {} {}", wrapResult.toString().replace('\n', ' '), SslConnection.this);
                                }
                                final SSLEngineResult.Status wrapResultStatus = wrapResult.getStatus();
                                allConsumed = true;
                                for (final ByteBuffer b2 : appOuts) {
                                    if (BufferUtil.hasContent(b2)) {
                                        allConsumed = false;
                                    }
                                }
                                switch (wrapResultStatus) {
                                    case CLOSED: {
                                        if (BufferUtil.hasContent(SslConnection.this._encryptedOutput)) {
                                            this._cannotAcceptMoreAppDataToFlush = true;
                                            SslConnection.this.getEndPoint().flush(SslConnection.this._encryptedOutput);
                                            SslConnection.this.getEndPoint().shutdownOutput();
                                            if (BufferUtil.hasContent(SslConnection.this._encryptedOutput)) {
                                                return false;
                                            }
                                        }
                                        else {
                                            SslConnection.this.getEndPoint().shutdownOutput();
                                        }
                                        return allConsumed;
                                    }
                                    case BUFFER_UNDERFLOW: {
                                        throw new IllegalStateException();
                                    }
                                    default: {
                                        if (SslConnection.LOG.isDebugEnabled()) {
                                            SslConnection.LOG.debug("wrap {} {} {}", wrapResultStatus, BufferUtil.toHexSummary(SslConnection.this._encryptedOutput), SslConnection.this);
                                        }
                                        if (wrapResult.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED) {
                                            this.handshakeFinished();
                                        }
                                        final SSLEngineResult.HandshakeStatus handshakeStatus = SslConnection.this._sslEngine.getHandshakeStatus();
                                        if (!this.allowRenegotiate(handshakeStatus)) {
                                            SslConnection.this.getEndPoint().shutdownOutput();
                                            return allConsumed;
                                        }
                                        if (BufferUtil.hasContent(SslConnection.this._encryptedOutput) && !SslConnection.this.getEndPoint().flush(SslConnection.this._encryptedOutput)) {
                                            SslConnection.this.getEndPoint().flush(SslConnection.this._encryptedOutput);
                                        }
                                        switch (handshakeStatus) {
                                            case NOT_HANDSHAKING: {
                                                if (!allConsumed && wrapResult.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.FINISHED && BufferUtil.isEmpty(SslConnection.this._encryptedOutput)) {
                                                    continue;
                                                }
                                                return allConsumed && BufferUtil.isEmpty(SslConnection.this._encryptedOutput);
                                            }
                                            case NEED_TASK: {
                                                SslConnection.this._sslEngine.getDelegatedTask().run();
                                                continue;
                                            }
                                            case NEED_WRAP: {
                                                continue;
                                            }
                                            case NEED_UNWRAP: {
                                                if (this._fillRequiresFlushToProgress || this.getFillInterest().isInterested()) {
                                                    break Label_0844;
                                                }
                                                this._flushRequiresFillToProgress = true;
                                                this.fill(BufferUtil.EMPTY_BUFFER);
                                                if (SslConnection.this._sslEngine.getHandshakeStatus() == SSLEngineResult.HandshakeStatus.NEED_WRAP) {
                                                    continue;
                                                }
                                                break Label_0844;
                                            }
                                            case FINISHED: {
                                                throw new IllegalStateException();
                                            }
                                            default: {
                                                continue;
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                            return allConsumed && BufferUtil.isEmpty(SslConnection.this._encryptedOutput);
                        }
                        if (SslConnection.this._sslEngine.isOutboundDone()) {
                            throw new EofException(new ClosedChannelException());
                        }
                        return false;
                    }
                    catch (SSLHandshakeException x) {
                        this.notifyHandshakeFailed(SslConnection.this._sslEngine, x);
                        throw x;
                    }
                    finally {
                        this.releaseEncryptedOutputBuffer();
                    }
                }
            }
            catch (Throwable x2) {
                this.close(x2);
                throw x2;
            }
        }
        
        private void releaseEncryptedOutputBuffer() {
            if (!Thread.holdsLock(this)) {
                throw new IllegalStateException();
            }
            if (SslConnection.this._encryptedOutput != null && !SslConnection.this._encryptedOutput.hasRemaining()) {
                SslConnection.this._bufferPool.release(SslConnection.this._encryptedOutput);
                SslConnection.this._encryptedOutput = null;
            }
        }
        
        @Override
        public void shutdownOutput() {
            try {
                boolean flush = false;
                boolean close = false;
                synchronized (SslConnection.this._decryptedEndPoint) {
                    final boolean ishut = this.isInputShutdown();
                    final boolean oshut = this.isOutputShutdown();
                    if (SslConnection.LOG.isDebugEnabled()) {
                        SslConnection.LOG.debug("shutdownOutput: oshut={}, ishut={} {}", oshut, ishut, SslConnection.this);
                    }
                    if (oshut) {
                        return;
                    }
                    if (!SslConnection.this._closedOutbound) {
                        SslConnection.this._closedOutbound = true;
                        SslConnection.this._sslEngine.closeOutbound();
                        flush = true;
                    }
                    if (ishut) {
                        close = true;
                    }
                }
                if (flush) {
                    this.flush(BufferUtil.EMPTY_BUFFER);
                }
                if (close) {
                    SslConnection.this.getEndPoint().close();
                }
                else {
                    this.ensureFillInterested();
                }
            }
            catch (Throwable x) {
                SslConnection.LOG.ignore(x);
                SslConnection.this.getEndPoint().close();
            }
        }
        
        private void ensureFillInterested() {
            if (this.getFillInterest().isCallbackNonBlocking()) {
                SslConnection.this.tryFillInterested(SslConnection.this._nonBlockingReadCallback);
            }
            else {
                SslConnection.this.tryFillInterested();
            }
        }
        
        @Override
        public boolean isOutputShutdown() {
            return SslConnection.this._sslEngine.isOutboundDone() || SslConnection.this.getEndPoint().isOutputShutdown();
        }
        
        @Override
        public void close() {
            this.shutdownOutput();
            SslConnection.this.getEndPoint().close();
            super.close();
        }
        
        @Override
        protected void close(final Throwable failure) {
            this.shutdownOutput();
            SslConnection.this.getEndPoint().close();
            super.close(failure);
        }
        
        @Override
        public Object getTransport() {
            return SslConnection.this.getEndPoint();
        }
        
        @Override
        public boolean isInputShutdown() {
            return SslConnection.this.getEndPoint().isInputShutdown() || SslConnection.this._sslEngine.isInboundDone();
        }
        
        private void notifyHandshakeSucceeded(final SSLEngine sslEngine) {
            SslHandshakeListener.Event event = null;
            for (final SslHandshakeListener listener : SslConnection.this.handshakeListeners) {
                if (event == null) {
                    event = new SslHandshakeListener.Event(sslEngine);
                }
                try {
                    listener.handshakeSucceeded(event);
                }
                catch (Throwable x) {
                    SslConnection.LOG.info("Exception while notifying listener " + listener, x);
                }
            }
        }
        
        private void notifyHandshakeFailed(final SSLEngine sslEngine, final Throwable failure) {
            SslHandshakeListener.Event event = null;
            for (final SslHandshakeListener listener : SslConnection.this.handshakeListeners) {
                if (event == null) {
                    event = new SslHandshakeListener.Event(sslEngine);
                }
                try {
                    listener.handshakeFailed(event, failure);
                }
                catch (Throwable x) {
                    SslConnection.LOG.info("Exception while notifying listener " + listener, x);
                }
            }
        }
        
        @Override
        public String toString() {
            return super.toString() + "->" + SslConnection.this.getEndPoint().toString();
        }
        
        private class FailWrite implements Runnable
        {
            private final Throwable failure;
            
            private FailWrite(final Throwable failure) {
                this.failure = failure;
            }
            
            @Override
            public void run() {
                DecryptedEndPoint.this.getWriteFlusher().onFail(this.failure);
            }
        }
    }
}
