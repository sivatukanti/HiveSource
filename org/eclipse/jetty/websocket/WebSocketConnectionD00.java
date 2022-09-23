// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import org.eclipse.jetty.util.log.Log;
import java.util.Collections;
import java.util.List;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import org.eclipse.jetty.io.AsyncEndPoint;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.nio.IndirectNIOBuffer;
import java.io.IOException;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.io.AbstractConnection;

public class WebSocketConnectionD00 extends AbstractConnection implements WebSocketConnection, WebSocket.FrameConnection
{
    private static final Logger LOG;
    public static final byte LENGTH_FRAME = Byte.MIN_VALUE;
    public static final byte SENTINEL_FRAME = 0;
    private final WebSocketParser _parser;
    private final WebSocketGenerator _generator;
    private final WebSocket _websocket;
    private final String _protocol;
    private String _key1;
    private String _key2;
    private ByteArrayBuffer _hixieBytes;
    
    public WebSocketConnectionD00(final WebSocket websocket, final EndPoint endpoint, final WebSocketBuffers buffers, final long timestamp, final int maxIdleTime, final String protocol) throws IOException {
        super(endpoint, timestamp);
        this._endp.setMaxIdleTime(maxIdleTime);
        this._websocket = websocket;
        this._protocol = protocol;
        this._generator = new WebSocketGeneratorD00(buffers, this._endp);
        this._parser = new WebSocketParserD00(buffers, endpoint, new FrameHandlerD00(this._websocket));
    }
    
    public WebSocket.Connection getConnection() {
        return this;
    }
    
    public void setHixieKeys(final String key1, final String key2) {
        this._key1 = key1;
        this._key2 = key2;
        this._hixieBytes = new IndirectNIOBuffer(16);
    }
    
    public Connection handle() throws IOException {
        try {
            if (this._hixieBytes != null) {
                final Buffer buffer = this._parser.getBuffer();
                if (buffer != null && buffer.length() > 0) {
                    int l = buffer.length();
                    if (l > 8 - this._hixieBytes.length()) {
                        l = 8 - this._hixieBytes.length();
                    }
                    this._hixieBytes.put(buffer.peek(buffer.getIndex(), l));
                    buffer.skip(l);
                }
                while (this._endp.isOpen()) {
                    if (this._hixieBytes.length() == 8) {
                        this.doTheHixieHixieShake();
                        this._endp.flush((Buffer)this._hixieBytes);
                        this._hixieBytes = null;
                        this._endp.flush();
                        break;
                    }
                    final int filled = this._endp.fill((Buffer)this._hixieBytes);
                    if (filled < 0) {
                        this._endp.close();
                        break;
                    }
                }
                if (this._websocket instanceof WebSocket.OnFrame) {
                    ((WebSocket.OnFrame)this._websocket).onHandshake(this);
                }
                this._websocket.onOpen(this);
                return this;
            }
            for (boolean progress = true; progress; progress = true) {
                final int flushed = this._generator.flush();
                final int filled2 = this._parser.parseNext();
                progress = (flushed > 0 || filled2 > 0);
                this._endp.flush();
                if (this._endp instanceof AsyncEndPoint && ((AsyncEndPoint)this._endp).hasProgressed()) {}
            }
        }
        catch (IOException e) {
            WebSocketConnectionD00.LOG.debug(e);
            try {
                if (this._endp.isOpen()) {
                    this._endp.close();
                }
            }
            catch (IOException e2) {
                WebSocketConnectionD00.LOG.ignore(e2);
            }
            throw e;
        }
        finally {
            if (this._endp.isOpen()) {
                if (this._endp.isInputShutdown() && this._generator.isBufferEmpty()) {
                    this._endp.close();
                }
                else {
                    this.checkWriteable();
                }
                this.checkWriteable();
            }
        }
        return this;
    }
    
    public void onInputShutdown() throws IOException {
    }
    
    private void doTheHixieHixieShake() {
        final byte[] result = doTheHixieHixieShake(hixieCrypt(this._key1), hixieCrypt(this._key2), this._hixieBytes.asArray());
        this._hixieBytes.clear();
        this._hixieBytes.put(result);
    }
    
    public boolean isOpen() {
        return this._endp != null && this._endp.isOpen();
    }
    
    public boolean isIdle() {
        return this._parser.isBufferEmpty() && this._generator.isBufferEmpty();
    }
    
    public boolean isSuspended() {
        return false;
    }
    
    @Override
    public void onClose() {
        this._websocket.onClose(1000, "");
    }
    
    public void sendMessage(final String content) throws IOException {
        final byte[] data = content.getBytes("UTF-8");
        this._generator.addFrame((byte)0, (byte)0, data, 0, data.length);
        this._generator.flush();
        this.checkWriteable();
    }
    
    public void sendMessage(final byte[] data, final int offset, final int length) throws IOException {
        this._generator.addFrame((byte)0, (byte)(-128), data, offset, length);
        this._generator.flush();
        this.checkWriteable();
    }
    
    public boolean isMore(final byte flags) {
        return (flags & 0x8) != 0x0;
    }
    
    public void sendControl(final byte code, final byte[] content, final int offset, final int length) throws IOException {
    }
    
    public void sendFrame(final byte flags, final byte opcode, final byte[] content, final int offset, final int length) throws IOException {
        this._generator.addFrame((byte)0, opcode, content, offset, length);
        this._generator.flush();
        this.checkWriteable();
    }
    
    public void close(final int code, final String message) {
        throw new UnsupportedOperationException();
    }
    
    public void disconnect() {
        this.close();
    }
    
    @Override
    public void close() {
        try {
            this._generator.flush();
            this._endp.close();
        }
        catch (IOException e) {
            WebSocketConnectionD00.LOG.ignore(e);
        }
    }
    
    public void shutdown() {
        this.close();
    }
    
    public void fillBuffersFrom(final Buffer buffer) {
        this._parser.fill(buffer);
    }
    
    private void checkWriteable() {
        if (!this._generator.isBufferEmpty() && this._endp instanceof AsyncEndPoint) {
            ((AsyncEndPoint)this._endp).scheduleWrite();
        }
    }
    
    static long hixieCrypt(final String key) {
        long number = 0L;
        int spaces = 0;
        for (final char c : key.toCharArray()) {
            if (Character.isDigit(c)) {
                number = number * 10L + (c - '0');
            }
            else if (c == ' ') {
                ++spaces;
            }
        }
        return number / spaces;
    }
    
    public static byte[] doTheHixieHixieShake(final long key1, final long key2, final byte[] key3) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final byte[] fodder = { (byte)(0xFFL & key1 >> 24), (byte)(0xFFL & key1 >> 16), (byte)(0xFFL & key1 >> 8), (byte)(0xFFL & key1), (byte)(0xFFL & key2 >> 24), (byte)(0xFFL & key2 >> 16), (byte)(0xFFL & key2 >> 8), (byte)(0xFFL & key2), 0, 0, 0, 0, 0, 0, 0, 0 };
            System.arraycopy(key3, 0, fodder, 8, 8);
            md.update(fodder);
            return md.digest();
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public void setMaxTextMessageSize(final int size) {
    }
    
    public void setMaxIdleTime(final int ms) {
        try {
            this._endp.setMaxIdleTime(ms);
        }
        catch (IOException e) {
            WebSocketConnectionD00.LOG.warn(e);
        }
    }
    
    public void setMaxBinaryMessageSize(final int size) {
    }
    
    public int getMaxTextMessageSize() {
        return -1;
    }
    
    public int getMaxIdleTime() {
        return this._endp.getMaxIdleTime();
    }
    
    public int getMaxBinaryMessageSize() {
        return -1;
    }
    
    public String getProtocol() {
        return this._protocol;
    }
    
    protected void onFrameHandshake() {
        if (this._websocket instanceof WebSocket.OnFrame) {
            ((WebSocket.OnFrame)this._websocket).onHandshake(this);
        }
    }
    
    protected void onWebsocketOpen() {
        this._websocket.onOpen(this);
    }
    
    public boolean isMessageComplete(final byte flags) {
        return true;
    }
    
    public byte binaryOpcode() {
        return -128;
    }
    
    public byte textOpcode() {
        return 0;
    }
    
    public boolean isControl(final byte opcode) {
        return false;
    }
    
    public boolean isText(final byte opcode) {
        return (opcode & 0xFFFFFF80) == 0x0;
    }
    
    public boolean isBinary(final byte opcode) {
        return (opcode & 0xFFFFFF80) != 0x0;
    }
    
    public boolean isContinuation(final byte opcode) {
        return false;
    }
    
    public boolean isClose(final byte opcode) {
        return false;
    }
    
    public boolean isPing(final byte opcode) {
        return false;
    }
    
    public boolean isPong(final byte opcode) {
        return false;
    }
    
    public List<Extension> getExtensions() {
        return Collections.emptyList();
    }
    
    public byte continuationOpcode() {
        return 0;
    }
    
    public byte finMask() {
        return 0;
    }
    
    public void setAllowFrameFragmentation(final boolean allowFragmentation) {
    }
    
    public boolean isAllowFrameFragmentation() {
        return false;
    }
    
    static {
        LOG = Log.getLogger(WebSocketConnectionD00.class);
    }
    
    static class FrameHandlerD00 implements WebSocketParser.FrameHandler
    {
        final WebSocket _websocket;
        
        FrameHandlerD00(final WebSocket websocket) {
            this._websocket = websocket;
        }
        
        public void onFrame(final byte flags, final byte opcode, final Buffer buffer) {
            try {
                final byte[] array = buffer.array();
                if (opcode == 0) {
                    if (this._websocket instanceof WebSocket.OnTextMessage) {
                        ((WebSocket.OnTextMessage)this._websocket).onMessage(buffer.toString("UTF-8"));
                    }
                }
                else if (this._websocket instanceof WebSocket.OnBinaryMessage) {
                    ((WebSocket.OnBinaryMessage)this._websocket).onMessage(array, buffer.getIndex(), buffer.length());
                }
            }
            catch (Throwable th) {
                WebSocketConnectionD00.LOG.warn(th);
            }
        }
        
        public void close(final int code, final String message) {
        }
    }
}
