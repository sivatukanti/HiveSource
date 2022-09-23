// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import org.eclipse.jetty.util.Utf8Appendable;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.Utf8StringBuilder;
import java.io.UnsupportedEncodingException;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.B64Code;
import java.security.MessageDigest;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.AsyncEndPoint;
import org.eclipse.jetty.io.Connection;
import java.util.Collections;
import java.util.Iterator;
import java.io.IOException;
import org.eclipse.jetty.io.EndPoint;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.io.AbstractConnection;

public class WebSocketConnectionRFC6455 extends AbstractConnection implements WebSocketConnection
{
    private static final Logger LOG;
    static final byte OP_CONTINUATION = 0;
    static final byte OP_TEXT = 1;
    static final byte OP_BINARY = 2;
    static final byte OP_EXT_DATA = 3;
    static final byte OP_CONTROL = 8;
    static final byte OP_CLOSE = 8;
    static final byte OP_PING = 9;
    static final byte OP_PONG = 10;
    static final byte OP_EXT_CTRL = 11;
    static final int CLOSE_NORMAL = 1000;
    static final int CLOSE_SHUTDOWN = 1001;
    static final int CLOSE_PROTOCOL = 1002;
    static final int CLOSE_BAD_DATA = 1003;
    static final int CLOSE_UNDEFINED = 1004;
    static final int CLOSE_NO_CODE = 1005;
    static final int CLOSE_NO_CLOSE = 1006;
    static final int CLOSE_BAD_PAYLOAD = 1007;
    static final int CLOSE_POLICY_VIOLATION = 1008;
    static final int CLOSE_MESSAGE_TOO_LARGE = 1009;
    static final int CLOSE_REQUIRED_EXTENSION = 1010;
    static final int CLOSE_SERVER_ERROR = 1011;
    static final int CLOSE_FAILED_TLS_HANDSHAKE = 1015;
    static final int FLAG_FIN = 8;
    static final int VERSION = 13;
    private static final byte[] MAGIC;
    private final List<Extension> _extensions;
    private final WebSocketParserRFC6455 _parser;
    private final WebSocketGeneratorRFC6455 _generator;
    private final WebSocketGenerator _outbound;
    private final WebSocket _webSocket;
    private final WebSocket.OnFrame _onFrame;
    private final WebSocket.OnBinaryMessage _onBinaryMessage;
    private final WebSocket.OnTextMessage _onTextMessage;
    private final WebSocket.OnControl _onControl;
    private final String _protocol;
    private final int _draft;
    private final ClassLoader _context;
    private volatile int _closeCode;
    private volatile String _closeMessage;
    private volatile boolean _closedIn;
    private volatile boolean _closedOut;
    private int _maxTextMessageSize;
    private int _maxBinaryMessageSize;
    private final WebSocket.FrameConnection _connection;
    
    static boolean isLastFrame(final byte flags) {
        return (flags & 0x8) != 0x0;
    }
    
    static boolean isControlFrame(final byte opcode) {
        return (opcode & 0x8) != 0x0;
    }
    
    public WebSocketConnectionRFC6455(final WebSocket websocket, final EndPoint endpoint, final WebSocketBuffers buffers, final long timestamp, final int maxIdleTime, final String protocol, final List<Extension> extensions, final int draft) throws IOException {
        this(websocket, endpoint, buffers, timestamp, maxIdleTime, protocol, extensions, draft, null);
    }
    
    public WebSocketConnectionRFC6455(final WebSocket websocket, final EndPoint endpoint, final WebSocketBuffers buffers, final long timestamp, final int maxIdleTime, final String protocol, final List<Extension> extensions, final int draft, final MaskGen maskgen) throws IOException {
        super(endpoint, timestamp);
        this._maxTextMessageSize = -1;
        this._maxBinaryMessageSize = -1;
        this._connection = new WSFrameConnection();
        this._context = Thread.currentThread().getContextClassLoader();
        this._draft = draft;
        this._endp.setMaxIdleTime(maxIdleTime);
        this._webSocket = websocket;
        this._onFrame = ((this._webSocket instanceof WebSocket.OnFrame) ? ((WebSocket.OnFrame)this._webSocket) : null);
        this._onTextMessage = ((this._webSocket instanceof WebSocket.OnTextMessage) ? ((WebSocket.OnTextMessage)this._webSocket) : null);
        this._onBinaryMessage = ((this._webSocket instanceof WebSocket.OnBinaryMessage) ? ((WebSocket.OnBinaryMessage)this._webSocket) : null);
        this._onControl = ((this._webSocket instanceof WebSocket.OnControl) ? ((WebSocket.OnControl)this._webSocket) : null);
        this._generator = new WebSocketGeneratorRFC6455(buffers, this._endp, maskgen);
        this._extensions = extensions;
        final WebSocketParser.FrameHandler frameHandler = new WSFrameHandler();
        if (this._extensions != null) {
            int e = 0;
            for (final Extension extension : this._extensions) {
                extension.bind(this._connection, (e == extensions.size() - 1) ? frameHandler : ((Extension)extensions.get(e + 1)), (WebSocketGenerator)((e == 0) ? this._generator : ((Extension)extensions.get(e - 1))));
                ++e;
            }
        }
        this._outbound = (WebSocketGenerator)((this._extensions == null || this._extensions.size() == 0) ? this._generator : ((Extension)extensions.get(extensions.size() - 1)));
        final WebSocketParser.FrameHandler inbound = (this._extensions == null || this._extensions.size() == 0) ? frameHandler : ((Extension)extensions.get(0));
        this._parser = new WebSocketParserRFC6455(buffers, endpoint, inbound, maskgen == null);
        this._protocol = protocol;
    }
    
    public WebSocket.Connection getConnection() {
        return this._connection;
    }
    
    public List<Extension> getExtensions() {
        if (this._extensions == null) {
            return Collections.emptyList();
        }
        return this._extensions;
    }
    
    public Connection handle() throws IOException {
        final Thread current = Thread.currentThread();
        final ClassLoader oldcontext = current.getContextClassLoader();
        current.setContextClassLoader(this._context);
        try {
            for (boolean progress = true; progress; progress = true) {
                final int flushed = this._generator.flushBuffer();
                final int filled = this._parser.parseNext();
                progress = (flushed > 0 || filled > 0);
                this._endp.flush();
                if (this._endp instanceof AsyncEndPoint && ((AsyncEndPoint)this._endp).hasProgressed()) {}
            }
        }
        catch (IOException e3) {
            try {
                if (this._endp.isOpen()) {
                    this._endp.close();
                }
            }
            catch (IOException e2) {
                WebSocketConnectionRFC6455.LOG.ignore(e2);
            }
            throw e3;
        }
        finally {
            current.setContextClassLoader(oldcontext);
            this._parser.returnBuffer();
            this._generator.returnBuffer();
            if (this._endp.isOpen()) {
                if (this._closedIn && this._closedOut && this._outbound.isBufferEmpty()) {
                    this._endp.close();
                }
                else if (this._endp.isInputShutdown() && !this._closedIn) {
                    this.closeIn(1006, null);
                }
                else {
                    this.checkWriteable();
                }
            }
        }
        return this;
    }
    
    public void onInputShutdown() throws IOException {
        if (!this._closedIn) {
            this._endp.close();
        }
    }
    
    public boolean isIdle() {
        return this._parser.isBufferEmpty() && this._outbound.isBufferEmpty();
    }
    
    public void onIdleExpired(final long idleForMs) {
        this.closeOut(1000, "Idle for " + idleForMs + "ms > " + this._endp.getMaxIdleTime() + "ms");
    }
    
    public boolean isSuspended() {
        return false;
    }
    
    @Override
    public void onClose() {
        final boolean closed;
        synchronized (this) {
            closed = (this._closeCode == 0);
            if (closed) {
                this._closeCode = 1006;
            }
        }
        if (closed) {
            this._webSocket.onClose(1006, "closed");
        }
    }
    
    public void closeIn(final int code, final String message) {
        WebSocketConnectionRFC6455.LOG.debug("ClosedIn {} {} {}", this, code, message);
        final boolean closed_out;
        final boolean tell_app;
        synchronized (this) {
            closed_out = this._closedOut;
            this._closedIn = true;
            tell_app = (this._closeCode == 0);
            if (tell_app) {
                this._closeCode = code;
                this._closeMessage = message;
            }
        }
        try {
            if (tell_app) {
                this._webSocket.onClose(code, message);
            }
        }
        finally {
            if (!closed_out) {
                this.closeOut(code, message);
            }
        }
    }
    
    public void closeOut(int code, final String message) {
        WebSocketConnectionRFC6455.LOG.debug("ClosedOut {} {} {}", this, code, message);
        final boolean closed_out;
        final boolean tell_app;
        synchronized (this) {
            closed_out = this._closedOut;
            this._closedOut = true;
            tell_app = (this._closeCode == 0);
            if (tell_app) {
                this._closeCode = code;
                this._closeMessage = message;
            }
        }
        try {
            if (tell_app) {
                this._webSocket.onClose(code, message);
            }
        }
        finally {
            try {
                if (!closed_out) {
                    if (code < 0 || code == 1005 || code == 1006 || code == 1015) {
                        code = -1;
                    }
                    else if (code == 0) {
                        code = 1000;
                    }
                    final byte[] bytes = ("xx" + ((message == null) ? "" : message)).getBytes("ISO-8859-1");
                    bytes[0] = (byte)(code / 256);
                    bytes[1] = (byte)(code % 256);
                    this._outbound.addFrame((byte)8, (byte)8, bytes, 0, (code > 0) ? bytes.length : 0);
                    this._outbound.flush();
                }
            }
            catch (IOException e) {
                WebSocketConnectionRFC6455.LOG.ignore(e);
            }
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
        if (!this._outbound.isBufferEmpty() && this._endp instanceof AsyncEndPoint) {
            ((AsyncEndPoint)this._endp).scheduleWrite();
        }
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
            md.update(WebSocketConnectionRFC6455.MAGIC);
            return new String(B64Code.encode(md.digest()));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s p=%s g=%s", this.getClass().getSimpleName(), this._parser, this._generator);
    }
    
    static {
        LOG = Log.getLogger(WebSocketConnectionRFC6455.class);
        try {
            MAGIC = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11".getBytes("ISO-8859-1");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private class WSFrameConnection implements WebSocket.FrameConnection
    {
        private volatile boolean _disconnecting;
        
        public void sendMessage(final String content) throws IOException {
            if (WebSocketConnectionRFC6455.this._closedOut) {
                throw new IOException("closedOut " + WebSocketConnectionRFC6455.this._closeCode + ":" + WebSocketConnectionRFC6455.this._closeMessage);
            }
            final byte[] data = content.getBytes("UTF-8");
            WebSocketConnectionRFC6455.this._outbound.addFrame((byte)8, (byte)1, data, 0, data.length);
            WebSocketConnectionRFC6455.this.checkWriteable();
        }
        
        public void sendMessage(final byte[] content, final int offset, final int length) throws IOException {
            if (WebSocketConnectionRFC6455.this._closedOut) {
                throw new IOException("closedOut " + WebSocketConnectionRFC6455.this._closeCode + ":" + WebSocketConnectionRFC6455.this._closeMessage);
            }
            WebSocketConnectionRFC6455.this._outbound.addFrame((byte)8, (byte)2, content, offset, length);
            WebSocketConnectionRFC6455.this.checkWriteable();
        }
        
        public void sendFrame(final byte flags, final byte opcode, final byte[] content, final int offset, final int length) throws IOException {
            if (WebSocketConnectionRFC6455.this._closedOut) {
                throw new IOException("closedOut " + WebSocketConnectionRFC6455.this._closeCode + ":" + WebSocketConnectionRFC6455.this._closeMessage);
            }
            WebSocketConnectionRFC6455.this._outbound.addFrame(flags, opcode, content, offset, length);
            WebSocketConnectionRFC6455.this.checkWriteable();
        }
        
        public void sendControl(final byte ctrl, final byte[] data, final int offset, final int length) throws IOException {
            if (WebSocketConnectionRFC6455.this._closedOut) {
                throw new IOException("closedOut " + WebSocketConnectionRFC6455.this._closeCode + ":" + WebSocketConnectionRFC6455.this._closeMessage);
            }
            WebSocketConnectionRFC6455.this._outbound.addFrame((byte)8, ctrl, data, offset, length);
            WebSocketConnectionRFC6455.this.checkWriteable();
        }
        
        public boolean isMessageComplete(final byte flags) {
            return WebSocketConnectionRFC6455.isLastFrame(flags);
        }
        
        public boolean isOpen() {
            return WebSocketConnectionRFC6455.this._endp != null && WebSocketConnectionRFC6455.this._endp.isOpen();
        }
        
        public void close(final int code, final String message) {
            if (this._disconnecting) {
                return;
            }
            this._disconnecting = true;
            WebSocketConnectionRFC6455.this.closeOut(code, message);
        }
        
        public void setMaxIdleTime(final int ms) {
            try {
                WebSocketConnectionRFC6455.this._endp.setMaxIdleTime(ms);
            }
            catch (IOException e) {
                WebSocketConnectionRFC6455.LOG.warn(e);
            }
        }
        
        public void setMaxTextMessageSize(final int size) {
            WebSocketConnectionRFC6455.this._maxTextMessageSize = size;
        }
        
        public void setMaxBinaryMessageSize(final int size) {
            WebSocketConnectionRFC6455.this._maxBinaryMessageSize = size;
        }
        
        public int getMaxIdleTime() {
            return WebSocketConnectionRFC6455.this._endp.getMaxIdleTime();
        }
        
        public int getMaxTextMessageSize() {
            return WebSocketConnectionRFC6455.this._maxTextMessageSize;
        }
        
        public int getMaxBinaryMessageSize() {
            return WebSocketConnectionRFC6455.this._maxBinaryMessageSize;
        }
        
        public String getProtocol() {
            return WebSocketConnectionRFC6455.this._protocol;
        }
        
        public byte binaryOpcode() {
            return 2;
        }
        
        public byte textOpcode() {
            return 1;
        }
        
        public byte continuationOpcode() {
            return 0;
        }
        
        public byte finMask() {
            return 8;
        }
        
        public boolean isControl(final byte opcode) {
            return WebSocketConnectionRFC6455.isControlFrame(opcode);
        }
        
        public boolean isText(final byte opcode) {
            return opcode == 1;
        }
        
        public boolean isBinary(final byte opcode) {
            return opcode == 2;
        }
        
        public boolean isContinuation(final byte opcode) {
            return opcode == 0;
        }
        
        public boolean isClose(final byte opcode) {
            return opcode == 8;
        }
        
        public boolean isPing(final byte opcode) {
            return opcode == 9;
        }
        
        public boolean isPong(final byte opcode) {
            return opcode == 10;
        }
        
        public void disconnect() {
            this.close(1000, null);
        }
        
        public void close() {
            this.close(1000, null);
        }
        
        public void setAllowFrameFragmentation(final boolean allowFragmentation) {
            WebSocketConnectionRFC6455.this._parser.setFakeFragments(allowFragmentation);
        }
        
        public boolean isAllowFrameFragmentation() {
            return WebSocketConnectionRFC6455.this._parser.isFakeFragments();
        }
        
        @Override
        public String toString() {
            return String.format("%s@%x l(%s:%d)<->r(%s:%d)", this.getClass().getSimpleName(), this.hashCode(), WebSocketConnectionRFC6455.this._endp.getLocalAddr(), WebSocketConnectionRFC6455.this._endp.getLocalPort(), WebSocketConnectionRFC6455.this._endp.getRemoteAddr(), WebSocketConnectionRFC6455.this._endp.getRemotePort());
        }
    }
    
    private class WSFrameHandler implements WebSocketParser.FrameHandler
    {
        private static final int MAX_CONTROL_FRAME_PAYLOAD = 125;
        private final Utf8StringBuilder _utf8;
        private ByteArrayBuffer _aggregate;
        private byte _opcode;
        
        private WSFrameHandler() {
            this._utf8 = new Utf8StringBuilder(512);
            this._opcode = -1;
        }
        
        public void onFrame(final byte flags, final byte opcode, final Buffer buffer) {
            final boolean lastFrame = WebSocketConnectionRFC6455.isLastFrame(flags);
            synchronized (WebSocketConnectionRFC6455.this) {
                if (WebSocketConnectionRFC6455.this._closedIn) {
                    return;
                }
            }
            try {
                final byte[] array = buffer.array();
                if (WebSocketConnectionRFC6455.isControlFrame(opcode) && buffer.length() > 125) {
                    this.errorClose(1002, "Control frame too large: " + buffer.length() + " > " + 125);
                    return;
                }
                if ((flags & 0x7) != 0x0) {
                    this.errorClose(1002, "RSV bits set 0x" + Integer.toHexString(flags));
                    return;
                }
                if (WebSocketConnectionRFC6455.this._closeCode != 0 && WebSocketConnectionRFC6455.this._closeCode != 1000 && opcode != 8) {
                    return;
                }
                if (WebSocketConnectionRFC6455.this._onFrame != null && WebSocketConnectionRFC6455.this._onFrame.onFrame(flags, opcode, array, buffer.getIndex(), buffer.length())) {
                    return;
                }
                if (WebSocketConnectionRFC6455.this._onControl != null && WebSocketConnectionRFC6455.isControlFrame(opcode) && WebSocketConnectionRFC6455.this._onControl.onControl(opcode, array, buffer.getIndex(), buffer.length())) {
                    return;
                }
                switch (opcode) {
                    case 0: {
                        if (this._opcode == -1) {
                            this.errorClose(1002, "Bad Continuation");
                            return;
                        }
                        if (WebSocketConnectionRFC6455.this._onTextMessage != null && this._opcode == 1) {
                            if (this._utf8.append(buffer.array(), buffer.getIndex(), buffer.length(), WebSocketConnectionRFC6455.this._connection.getMaxTextMessageSize())) {
                                if (lastFrame) {
                                    this._opcode = -1;
                                    final String msg = this._utf8.toString();
                                    this._utf8.reset();
                                    WebSocketConnectionRFC6455.this._onTextMessage.onMessage(msg);
                                }
                            }
                            else {
                                this.textMessageTooLarge();
                            }
                        }
                        if (this._opcode < 0 || WebSocketConnectionRFC6455.this._connection.getMaxBinaryMessageSize() < 0 || this._aggregate == null || !this.checkBinaryMessageSize(this._aggregate.length(), buffer.length())) {
                            break;
                        }
                        this._aggregate.put(buffer);
                        if (lastFrame && WebSocketConnectionRFC6455.this._onBinaryMessage != null) {
                            try {
                                WebSocketConnectionRFC6455.this._onBinaryMessage.onMessage(this._aggregate.array(), this._aggregate.getIndex(), this._aggregate.length());
                            }
                            finally {
                                this._opcode = -1;
                                this._aggregate.clear();
                            }
                            break;
                        }
                        break;
                    }
                    case 9: {
                        WebSocketConnectionRFC6455.LOG.debug("PING {}", this);
                        if (!WebSocketConnectionRFC6455.this._closedOut) {
                            WebSocketConnectionRFC6455.this._connection.sendControl((byte)10, buffer.array(), buffer.getIndex(), buffer.length());
                            break;
                        }
                        break;
                    }
                    case 10: {
                        WebSocketConnectionRFC6455.LOG.debug("PONG {}", this);
                        break;
                    }
                    case 8: {
                        int code = 1005;
                        String message = null;
                        if (buffer.length() >= 2) {
                            code = (0xFF & buffer.array()[buffer.getIndex()]) * 256 + (0xFF & buffer.array()[buffer.getIndex() + 1]);
                            if (code < 1000 || code == 1004 || code == 1006 || code == 1005 || (code > 1011 && code <= 2999) || code >= 5000) {
                                this.errorClose(1002, "Invalid close code " + code);
                                return;
                            }
                            if (buffer.length() > 2 && this._utf8.append(buffer.array(), buffer.getIndex() + 2, buffer.length() - 2, WebSocketConnectionRFC6455.this._connection.getMaxTextMessageSize())) {
                                message = this._utf8.toString();
                                this._utf8.reset();
                            }
                        }
                        else if (buffer.length() == 1) {
                            this.errorClose(1002, "Invalid payload length of 1");
                            return;
                        }
                        WebSocketConnectionRFC6455.this.closeIn(code, message);
                        break;
                    }
                    case 1: {
                        if (this._opcode != -1) {
                            this.errorClose(1002, "Expected Continuation" + Integer.toHexString(opcode));
                            return;
                        }
                        if (WebSocketConnectionRFC6455.this._onTextMessage == null) {
                            break;
                        }
                        if (WebSocketConnectionRFC6455.this._connection.getMaxTextMessageSize() <= 0) {
                            if (lastFrame) {
                                WebSocketConnectionRFC6455.this._onTextMessage.onMessage(buffer.toString("UTF-8"));
                                break;
                            }
                            WebSocketConnectionRFC6455.LOG.warn("Frame discarded. Text aggregation disabled for {}", WebSocketConnectionRFC6455.this._endp);
                            this.errorClose(1008, "Text frame aggregation disabled");
                            break;
                        }
                        else {
                            if (!this._utf8.append(buffer.array(), buffer.getIndex(), buffer.length(), WebSocketConnectionRFC6455.this._connection.getMaxTextMessageSize())) {
                                this.textMessageTooLarge();
                                break;
                            }
                            if (lastFrame) {
                                final String msg = this._utf8.toString();
                                this._utf8.reset();
                                WebSocketConnectionRFC6455.this._onTextMessage.onMessage(msg);
                                break;
                            }
                            this._opcode = 1;
                            break;
                        }
                        break;
                    }
                    case 2: {
                        if (this._opcode != -1) {
                            this.errorClose(1002, "Expected Continuation" + Integer.toHexString(opcode));
                            return;
                        }
                        if (WebSocketConnectionRFC6455.this._onBinaryMessage == null || !this.checkBinaryMessageSize(0, buffer.length())) {
                            break;
                        }
                        if (lastFrame) {
                            WebSocketConnectionRFC6455.this._onBinaryMessage.onMessage(array, buffer.getIndex(), buffer.length());
                            break;
                        }
                        if (WebSocketConnectionRFC6455.this._connection.getMaxBinaryMessageSize() >= 0) {
                            this._opcode = opcode;
                            if (this._aggregate == null) {
                                this._aggregate = new ByteArrayBuffer(WebSocketConnectionRFC6455.this._connection.getMaxBinaryMessageSize());
                            }
                            this._aggregate.put(buffer);
                            break;
                        }
                        WebSocketConnectionRFC6455.LOG.warn("Frame discarded. Binary aggregation disabed for {}", WebSocketConnectionRFC6455.this._endp);
                        this.errorClose(1008, "Binary frame aggregation disabled");
                        break;
                    }
                    default: {
                        this.errorClose(1002, "Bad opcode 0x" + Integer.toHexString(opcode));
                        break;
                    }
                }
            }
            catch (Utf8Appendable.NotUtf8Exception notUtf8) {
                WebSocketConnectionRFC6455.LOG.warn("NOTUTF8 - {} for {}", notUtf8, WebSocketConnectionRFC6455.this._endp, notUtf8);
                WebSocketConnectionRFC6455.LOG.debug(notUtf8);
                this.errorClose(1007, "Invalid UTF-8");
            }
            catch (Throwable e) {
                WebSocketConnectionRFC6455.LOG.warn("{} for {}", e, WebSocketConnectionRFC6455.this._endp, e);
                WebSocketConnectionRFC6455.LOG.debug(e);
                this.errorClose(1011, "Internal Server Error: " + e);
            }
        }
        
        private void errorClose(final int code, final String message) {
            WebSocketConnectionRFC6455.this._connection.close(code, message);
            try {
                WebSocketConnectionRFC6455.this._endp.close();
            }
            catch (IOException e) {
                WebSocketConnectionRFC6455.LOG.warn(e.toString(), new Object[0]);
                WebSocketConnectionRFC6455.LOG.debug(e);
            }
        }
        
        private boolean checkBinaryMessageSize(final int bufferLen, final int length) {
            final int max = WebSocketConnectionRFC6455.this._connection.getMaxBinaryMessageSize();
            if (max > 0 && bufferLen + length > max) {
                WebSocketConnectionRFC6455.LOG.warn("Binary message too large > {}B for {}", WebSocketConnectionRFC6455.this._connection.getMaxBinaryMessageSize(), WebSocketConnectionRFC6455.this._endp);
                WebSocketConnectionRFC6455.this._connection.close(1009, "Message size > " + WebSocketConnectionRFC6455.this._connection.getMaxBinaryMessageSize());
                this._opcode = -1;
                if (this._aggregate != null) {
                    this._aggregate.clear();
                }
                return false;
            }
            return true;
        }
        
        private void textMessageTooLarge() {
            WebSocketConnectionRFC6455.LOG.warn("Text message too large > {} chars for {}", WebSocketConnectionRFC6455.this._connection.getMaxTextMessageSize(), WebSocketConnectionRFC6455.this._endp);
            WebSocketConnectionRFC6455.this._connection.close(1009, "Text message size > " + WebSocketConnectionRFC6455.this._connection.getMaxTextMessageSize() + " chars");
            this._opcode = -1;
            this._utf8.reset();
        }
        
        public void close(final int code, final String message) {
            if (code != 1000) {
                WebSocketConnectionRFC6455.LOG.warn("Close: " + code + " " + message, new Object[0]);
            }
            WebSocketConnectionRFC6455.this._connection.close(code, message);
        }
        
        @Override
        public String toString() {
            return WebSocketConnectionRFC6455.this.toString() + "FH";
        }
    }
}
