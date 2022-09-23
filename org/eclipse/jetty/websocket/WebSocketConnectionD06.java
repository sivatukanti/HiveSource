// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.Utf8StringBuilder;
import java.io.UnsupportedEncodingException;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.B64Code;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;
import org.eclipse.jetty.io.AsyncEndPoint;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.Connection;
import java.io.IOException;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.io.AbstractConnection;

public class WebSocketConnectionD06 extends AbstractConnection implements WebSocketConnection
{
    private static final Logger LOG;
    static final byte OP_CONTINUATION = 0;
    static final byte OP_CLOSE = 1;
    static final byte OP_PING = 2;
    static final byte OP_PONG = 3;
    static final byte OP_TEXT = 4;
    static final byte OP_BINARY = 5;
    static final int CLOSE_NORMAL = 1000;
    static final int CLOSE_SHUTDOWN = 1001;
    static final int CLOSE_PROTOCOL = 1002;
    static final int CLOSE_BADDATA = 1003;
    static final int CLOSE_LARGE = 1004;
    private static final byte[] MAGIC;
    private final WebSocketParser _parser;
    private final WebSocketGenerator _generator;
    private final WebSocket _webSocket;
    private final WebSocket.OnFrame _onFrame;
    private final WebSocket.OnBinaryMessage _onBinaryMessage;
    private final WebSocket.OnTextMessage _onTextMessage;
    private final WebSocket.OnControl _onControl;
    private final String _protocol;
    private volatile boolean _closedIn;
    private volatile boolean _closedOut;
    private int _maxTextMessageSize;
    private int _maxBinaryMessageSize;
    private final WebSocketParser.FrameHandler _frameHandler;
    private final WebSocket.FrameConnection _connection;
    
    static boolean isLastFrame(final int flags) {
        return (flags & 0x8) != 0x0;
    }
    
    static boolean isControlFrame(final int opcode) {
        switch (opcode) {
            case 1:
            case 2:
            case 3: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public WebSocketConnectionD06(final WebSocket websocket, final EndPoint endpoint, final WebSocketBuffers buffers, final long timestamp, final int maxIdleTime, final String protocol) throws IOException {
        super(endpoint, timestamp);
        this._maxBinaryMessageSize = -1;
        this._frameHandler = new FrameHandlerD06();
        this._connection = new FrameConnectionD06();
        this._endp.setMaxIdleTime(maxIdleTime);
        this._webSocket = websocket;
        this._onFrame = ((this._webSocket instanceof WebSocket.OnFrame) ? ((WebSocket.OnFrame)this._webSocket) : null);
        this._onTextMessage = ((this._webSocket instanceof WebSocket.OnTextMessage) ? ((WebSocket.OnTextMessage)this._webSocket) : null);
        this._onBinaryMessage = ((this._webSocket instanceof WebSocket.OnBinaryMessage) ? ((WebSocket.OnBinaryMessage)this._webSocket) : null);
        this._onControl = ((this._webSocket instanceof WebSocket.OnControl) ? ((WebSocket.OnControl)this._webSocket) : null);
        this._generator = new WebSocketGeneratorD06(buffers, this._endp, null);
        this._parser = new WebSocketParserD06(buffers, endpoint, this._frameHandler, true);
        this._protocol = protocol;
        this._maxTextMessageSize = buffers.getBufferSize();
        this._maxBinaryMessageSize = -1;
    }
    
    public WebSocket.Connection getConnection() {
        return this._connection;
    }
    
    public Connection handle() throws IOException {
        try {
            boolean progress = true;
            while (progress) {
                final int flushed = this._generator.flush();
                final int filled = this._parser.parseNext();
                progress = (flushed > 0 || filled > 0);
                if (filled < 0 || flushed < 0) {
                    this._endp.close();
                    break;
                }
            }
        }
        catch (IOException e3) {
            try {
                this._endp.close();
            }
            catch (IOException e2) {
                WebSocketConnectionD06.LOG.ignore(e2);
            }
            throw e3;
        }
        finally {
            if (this._endp.isOpen()) {
                if (this._closedIn && this._closedOut && this._generator.isBufferEmpty()) {
                    this._endp.close();
                }
                else if (this._endp.isInputShutdown() && !this._closedIn) {
                    this.closeIn(1002, null);
                }
                else {
                    this.checkWriteable();
                }
            }
        }
        return this;
    }
    
    public void onInputShutdown() throws IOException {
    }
    
    public boolean isIdle() {
        return this._parser.isBufferEmpty() && this._generator.isBufferEmpty();
    }
    
    public void onIdleExpired(final long idleForMs) {
        this.closeOut(1000, "Idle");
    }
    
    public boolean isSuspended() {
        return false;
    }
    
    @Override
    public void onClose() {
        this._webSocket.onClose(1000, "");
    }
    
    public synchronized void closeIn(final int code, final String message) {
        WebSocketConnectionD06.LOG.debug("ClosedIn {} {}", this, message);
        try {
            if (this._closedOut) {
                this._endp.close();
            }
            else {
                this.closeOut(code, message);
            }
        }
        catch (IOException e) {
            WebSocketConnectionD06.LOG.ignore(e);
        }
        finally {
            this._closedIn = true;
        }
    }
    
    public synchronized void closeOut(int code, final String message) {
        WebSocketConnectionD06.LOG.debug("ClosedOut {} {}", this, message);
        try {
            if (this._closedIn || this._closedOut) {
                this._endp.close();
            }
            else {
                if (code <= 0) {
                    code = 1000;
                }
                final byte[] bytes = ("xx" + ((message == null) ? "" : message)).getBytes("ISO-8859-1");
                bytes[0] = (byte)(code / 256);
                bytes[1] = (byte)(code % 256);
                this._generator.addFrame((byte)8, (byte)1, bytes, 0, bytes.length);
            }
            this._generator.flush();
        }
        catch (IOException e) {
            WebSocketConnectionD06.LOG.ignore(e);
        }
        finally {
            this._closedOut = true;
        }
    }
    
    public void shutdown() {
        final WebSocket.Connection connection = this._connection;
        if (connection != null) {
            connection.close(1001, null);
        }
    }
    
    public void fillBuffersFrom(final Buffer buffer) {
        this._parser.fill(buffer);
    }
    
    private void checkWriteable() {
        if (!this._generator.isBufferEmpty() && this._endp instanceof AsyncEndPoint) {
            ((AsyncEndPoint)this._endp).scheduleWrite();
        }
    }
    
    public List<Extension> getExtensions() {
        return Collections.emptyList();
    }
    
    protected void onFrameHandshake() {
        if (this._onFrame != null) {
            this._onFrame.onHandshake(this._connection);
        }
    }
    
    protected void onWebSocketOpen() {
        this._webSocket.onOpen(this._connection);
    }
    
    public static String hashKey(final String key) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(key.getBytes("UTF-8"));
            md.update(WebSocketConnectionD06.MAGIC);
            return new String(B64Code.encode(md.digest()));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    static {
        LOG = Log.getLogger(WebSocketConnectionD06.class);
        try {
            MAGIC = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11".getBytes("ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private class FrameConnectionD06 implements WebSocket.FrameConnection
    {
        volatile boolean _disconnecting;
        int _maxTextMessage;
        int _maxBinaryMessage;
        
        private FrameConnectionD06() {
            this._maxTextMessage = WebSocketConnectionD06.this._maxTextMessageSize;
            this._maxBinaryMessage = WebSocketConnectionD06.this._maxBinaryMessageSize;
        }
        
        public synchronized void sendMessage(final String content) throws IOException {
            if (WebSocketConnectionD06.this._closedOut) {
                throw new IOException("closing");
            }
            final byte[] data = content.getBytes("UTF-8");
            WebSocketConnectionD06.this._generator.addFrame((byte)8, (byte)4, data, 0, data.length);
            WebSocketConnectionD06.this._generator.flush();
            WebSocketConnectionD06.this.checkWriteable();
        }
        
        public synchronized void sendMessage(final byte[] content, final int offset, final int length) throws IOException {
            if (WebSocketConnectionD06.this._closedOut) {
                throw new IOException("closing");
            }
            WebSocketConnectionD06.this._generator.addFrame((byte)8, (byte)5, content, offset, length);
            WebSocketConnectionD06.this._generator.flush();
            WebSocketConnectionD06.this.checkWriteable();
        }
        
        public void sendFrame(final byte flags, final byte opcode, final byte[] content, final int offset, final int length) throws IOException {
            if (WebSocketConnectionD06.this._closedOut) {
                throw new IOException("closing");
            }
            WebSocketConnectionD06.this._generator.addFrame(flags, opcode, content, offset, length);
            WebSocketConnectionD06.this._generator.flush();
            WebSocketConnectionD06.this.checkWriteable();
        }
        
        public void sendControl(final byte control, final byte[] data, final int offset, final int length) throws IOException {
            if (WebSocketConnectionD06.this._closedOut) {
                throw new IOException("closing");
            }
            WebSocketConnectionD06.this._generator.addFrame((byte)8, control, data, offset, length);
            WebSocketConnectionD06.this._generator.flush();
            WebSocketConnectionD06.this.checkWriteable();
        }
        
        public boolean isMessageComplete(final byte flags) {
            return WebSocketConnectionD06.isLastFrame(flags);
        }
        
        public boolean isOpen() {
            return WebSocketConnectionD06.this._endp != null && WebSocketConnectionD06.this._endp.isOpen();
        }
        
        public void close(final int code, final String message) {
            if (this._disconnecting) {
                return;
            }
            this._disconnecting = true;
            WebSocketConnectionD06.this.closeOut(code, message);
        }
        
        public void setMaxIdleTime(final int ms) {
            try {
                WebSocketConnectionD06.this._endp.setMaxIdleTime(ms);
            }
            catch (IOException e) {
                WebSocketConnectionD06.LOG.warn(e);
            }
        }
        
        public void setMaxTextMessageSize(final int size) {
            this._maxTextMessage = size;
        }
        
        public void setMaxBinaryMessageSize(final int size) {
            this._maxBinaryMessage = size;
        }
        
        public int getMaxTextMessageSize() {
            return this._maxTextMessage;
        }
        
        public int getMaxIdleTime() {
            return WebSocketConnectionD06.this._endp.getMaxIdleTime();
        }
        
        public int getMaxBinaryMessageSize() {
            return this._maxBinaryMessage;
        }
        
        public String getProtocol() {
            return WebSocketConnectionD06.this._protocol;
        }
        
        public byte binaryOpcode() {
            return 5;
        }
        
        public byte textOpcode() {
            return 4;
        }
        
        public byte continuationOpcode() {
            return 0;
        }
        
        public byte finMask() {
            return 8;
        }
        
        public boolean isControl(final byte opcode) {
            return WebSocketConnectionD06.isControlFrame(opcode);
        }
        
        public boolean isText(final byte opcode) {
            return opcode == 4;
        }
        
        public boolean isBinary(final byte opcode) {
            return opcode == 5;
        }
        
        public boolean isContinuation(final byte opcode) {
            return opcode == 0;
        }
        
        public boolean isClose(final byte opcode) {
            return opcode == 1;
        }
        
        public boolean isPing(final byte opcode) {
            return opcode == 2;
        }
        
        public boolean isPong(final byte opcode) {
            return opcode == 3;
        }
        
        public void disconnect() {
            this.close();
        }
        
        public void close() {
            this.close(1000, null);
        }
        
        @Override
        public String toString() {
            return this.getClass().getSimpleName() + "@" + WebSocketConnectionD06.this._endp.getLocalAddr() + ":" + WebSocketConnectionD06.this._endp.getLocalPort() + "<->" + WebSocketConnectionD06.this._endp.getRemoteAddr() + ":" + WebSocketConnectionD06.this._endp.getRemotePort();
        }
        
        public void setAllowFrameFragmentation(final boolean allowFragmentation) {
        }
        
        public boolean isAllowFrameFragmentation() {
            return false;
        }
    }
    
    private class FrameHandlerD06 implements WebSocketParser.FrameHandler
    {
        private final Utf8StringBuilder _utf8;
        private ByteArrayBuffer _aggregate;
        private byte _opcode;
        
        private FrameHandlerD06() {
            this._utf8 = new Utf8StringBuilder();
            this._opcode = -1;
        }
        
        public void onFrame(final byte flags, final byte opcode, final Buffer buffer) {
            final boolean lastFrame = WebSocketConnectionD06.isLastFrame(flags);
            synchronized (WebSocketConnectionD06.this) {
                if (WebSocketConnectionD06.this._closedIn) {
                    return;
                }
                try {
                    final byte[] array = buffer.array();
                    if (WebSocketConnectionD06.this._onFrame != null && WebSocketConnectionD06.this._onFrame.onFrame(flags, opcode, array, buffer.getIndex(), buffer.length())) {
                        return;
                    }
                    if (WebSocketConnectionD06.this._onControl != null && WebSocketConnectionD06.isControlFrame(opcode) && WebSocketConnectionD06.this._onControl.onControl(opcode, array, buffer.getIndex(), buffer.length())) {
                        return;
                    }
                    switch (opcode) {
                        case 0: {
                            if (this._opcode == 4 && WebSocketConnectionD06.this._connection.getMaxTextMessageSize() >= 0) {
                                if (!this._utf8.append(buffer.array(), buffer.getIndex(), buffer.length(), WebSocketConnectionD06.this._connection.getMaxTextMessageSize())) {
                                    WebSocketConnectionD06.this._connection.close(1004, "Text message size > " + WebSocketConnectionD06.this._connection.getMaxTextMessageSize() + " chars");
                                    this._utf8.reset();
                                    this._opcode = -1;
                                    break;
                                }
                                if (lastFrame && WebSocketConnectionD06.this._onTextMessage != null) {
                                    this._opcode = -1;
                                    final String msg = this._utf8.toString();
                                    this._utf8.reset();
                                    WebSocketConnectionD06.this._onTextMessage.onMessage(msg);
                                    break;
                                }
                                break;
                            }
                            else {
                                if (this._opcode < 0 || WebSocketConnectionD06.this._connection.getMaxBinaryMessageSize() < 0) {
                                    break;
                                }
                                if (this._aggregate.space() < this._aggregate.length()) {
                                    WebSocketConnectionD06.this._connection.close(1004, "Message size > " + WebSocketConnectionD06.this._connection.getMaxBinaryMessageSize());
                                    this._aggregate.clear();
                                    this._opcode = -1;
                                    break;
                                }
                                this._aggregate.put(buffer);
                                if (lastFrame && WebSocketConnectionD06.this._onBinaryMessage != null) {
                                    try {
                                        WebSocketConnectionD06.this._onBinaryMessage.onMessage(this._aggregate.array(), this._aggregate.getIndex(), this._aggregate.length());
                                    }
                                    finally {
                                        this._opcode = -1;
                                        this._aggregate.clear();
                                    }
                                    break;
                                }
                                break;
                            }
                            break;
                        }
                        case 2: {
                            WebSocketConnectionD06.LOG.debug("PING {}", this);
                            if (!WebSocketConnectionD06.this._closedOut) {
                                WebSocketConnectionD06.this._connection.sendControl((byte)3, buffer.array(), buffer.getIndex(), buffer.length());
                                break;
                            }
                            break;
                        }
                        case 3: {
                            WebSocketConnectionD06.LOG.debug("PONG {}", this);
                            break;
                        }
                        case 1: {
                            int code = -1;
                            String message = null;
                            if (buffer.length() >= 2) {
                                code = buffer.array()[buffer.getIndex()] * 255 + buffer.array()[buffer.getIndex() + 1];
                                if (buffer.length() > 2) {
                                    message = new String(buffer.array(), buffer.getIndex() + 2, buffer.length() - 2, "UTF-8");
                                }
                            }
                            WebSocketConnectionD06.this.closeIn(code, message);
                            break;
                        }
                        case 4: {
                            if (WebSocketConnectionD06.this._onTextMessage == null) {
                                break;
                            }
                            if (lastFrame) {
                                WebSocketConnectionD06.this._onTextMessage.onMessage(buffer.toString("UTF-8"));
                                break;
                            }
                            if (WebSocketConnectionD06.this._connection.getMaxTextMessageSize() < 0) {
                                break;
                            }
                            if (this._utf8.append(buffer.array(), buffer.getIndex(), buffer.length(), WebSocketConnectionD06.this._connection.getMaxTextMessageSize())) {
                                this._opcode = 4;
                                break;
                            }
                            this._utf8.reset();
                            this._opcode = -1;
                            WebSocketConnectionD06.this._connection.close(1004, "Text message size > " + WebSocketConnectionD06.this._connection.getMaxTextMessageSize() + " chars");
                            break;
                        }
                        default: {
                            if (WebSocketConnectionD06.this._onBinaryMessage == null) {
                                break;
                            }
                            if (lastFrame) {
                                WebSocketConnectionD06.this._onBinaryMessage.onMessage(array, buffer.getIndex(), buffer.length());
                                break;
                            }
                            if (WebSocketConnectionD06.this._connection.getMaxBinaryMessageSize() < 0) {
                                break;
                            }
                            if (buffer.length() > WebSocketConnectionD06.this._connection.getMaxBinaryMessageSize()) {
                                WebSocketConnectionD06.this._connection.close(1004, "Message size > " + WebSocketConnectionD06.this._connection.getMaxBinaryMessageSize());
                                if (this._aggregate != null) {
                                    this._aggregate.clear();
                                }
                                this._opcode = -1;
                                break;
                            }
                            this._opcode = opcode;
                            if (this._aggregate == null) {
                                this._aggregate = new ByteArrayBuffer(WebSocketConnectionD06.this._connection.getMaxBinaryMessageSize());
                            }
                            this._aggregate.put(buffer);
                            break;
                        }
                    }
                }
                catch (Throwable th) {
                    WebSocketConnectionD06.LOG.warn(th);
                }
            }
        }
        
        public void close(final int code, final String message) {
            WebSocketConnectionD06.this._connection.close(code, message);
        }
        
        @Override
        public String toString() {
            return WebSocketConnectionD06.this.toString() + "FH";
        }
    }
}
