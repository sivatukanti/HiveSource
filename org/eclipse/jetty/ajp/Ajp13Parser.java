// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.ajp;

import java.io.InterruptedIOException;
import javax.servlet.ServletInputStream;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.io.BufferUtil;
import org.eclipse.jetty.io.EofException;
import java.io.IOException;
import org.eclipse.jetty.io.View;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.Buffers;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.http.Parser;

public class Ajp13Parser implements Parser
{
    private static final Logger LOG;
    private static final int STATE_START = -1;
    private static final int STATE_END = 0;
    private static final int STATE_AJP13CHUNK_START = 1;
    private static final int STATE_AJP13CHUNK = 2;
    private int _state;
    private long _contentLength;
    private long _contentPosition;
    private int _chunkLength;
    private int _chunkPosition;
    private int _headers;
    private Buffers _buffers;
    private EndPoint _endp;
    private Buffer _buffer;
    private Buffer _header;
    private Buffer _body;
    private View _contentView;
    private EventHandler _handler;
    private Ajp13Generator _generator;
    private View _tok0;
    private View _tok1;
    protected int _length;
    protected int _packetLength;
    volatile int _seq;
    
    public Ajp13Parser(final Buffers buffers, final EndPoint endPoint) {
        this._state = -1;
        this._contentView = new View();
        this._seq = 0;
        this._buffers = buffers;
        this._endp = endPoint;
    }
    
    public void setEventHandler(final EventHandler handler) {
        this._handler = handler;
    }
    
    public void setGenerator(final Ajp13Generator generator) {
        this._generator = generator;
    }
    
    public long getContentLength() {
        return this._contentLength;
    }
    
    public int getState() {
        return this._state;
    }
    
    public boolean inContentState() {
        return this._state > 0;
    }
    
    public boolean inHeaderState() {
        return this._state < 0;
    }
    
    public boolean isIdle() {
        return this._state == -1;
    }
    
    public boolean isComplete() {
        return this._state == 0;
    }
    
    public boolean isMoreInBuffer() {
        return (this._header != null && this._header.hasContent()) || (this._body != null && this._body.hasContent());
    }
    
    public boolean isState(final int state) {
        return this._state == state;
    }
    
    public void parse() throws IOException {
        if (this._state == 0) {
            this.reset();
        }
        if (this._state != -1) {
            throw new IllegalStateException("!START");
        }
        while (!this.isComplete()) {
            this.parseNext();
        }
    }
    
    public boolean parseAvailable() throws IOException {
        boolean progress = this.parseNext() > 0;
        while (!this.isComplete() && this._buffer != null && this._buffer.length() > 0) {
            progress |= (this.parseNext() > 0);
        }
        return progress;
    }
    
    private int fill() throws IOException {
        int filled = -1;
        if (this._body != null && this._buffer != this._body) {
            if (this._header.length() > 0) {
                this._body.put(this._header);
            }
            this._buffer = this._body;
            if (this._buffer.length() > 0) {
                filled = this._buffer.length();
                return filled;
            }
        }
        if (this._buffer.markIndex() == 0 && this._buffer.putIndex() == this._buffer.capacity()) {
            throw new IOException("FULL");
        }
        if (this._endp != null && filled <= 0) {
            if (this._buffer == this._body) {
                this._buffer.compact();
            }
            if (this._buffer.space() == 0) {
                throw new IOException("FULL");
            }
            try {
                filled = this._endp.fill(this._buffer);
            }
            catch (IOException e) {
                Ajp13Parser.LOG.debug(e);
                this.reset();
                throw (e instanceof EofException) ? e : new EofException(e);
            }
        }
        if (filled >= 0) {
            return filled;
        }
        if (this._state > 0) {
            this._state = 0;
            this._handler.messageComplete(this._contentPosition);
            return filled;
        }
        this.reset();
        throw new EofException();
    }
    
    public int parseNext() throws IOException {
        int total_filled = 0;
        if (this._buffer == null) {
            if (this._header == null) {
                this._header = this._buffers.getHeader();
            }
            this._buffer = this._header;
            this._tok0 = new View(this._header);
            this._tok1 = new View(this._header);
            this._tok0.setPutIndex(this._tok0.getIndex());
            this._tok1.setPutIndex(this._tok1.getIndex());
        }
        if (this._state == 0) {
            throw new IllegalStateException("STATE_END");
        }
        if (this._state > 0 && this._contentPosition == this._contentLength) {
            this._state = 0;
            this._handler.messageComplete(this._contentPosition);
            return 1;
        }
        Label_1523: {
            if (this._state < 0) {
                if (this._packetLength <= 0) {
                    if (this._buffer.length() < 4) {
                        if (total_filled < 0) {
                            total_filled = 0;
                        }
                        total_filled += this.fill();
                        if (this._buffer.length() < 4) {
                            return total_filled;
                        }
                    }
                    this._contentLength = -3L;
                    final int _magic = Ajp13RequestPacket.getInt(this._buffer);
                    if (_magic != 4660) {
                        throw new IOException("Bad AJP13 rcv packet: 0x" + Integer.toHexString(_magic) + " expected " + "0x" + Integer.toHexString(4660) + " " + this);
                    }
                    this._packetLength = Ajp13RequestPacket.getInt(this._buffer);
                    if (this._packetLength > 8192) {
                        throw new IOException("AJP13 packet (" + this._packetLength + "bytes) too large for buffer");
                    }
                }
                if (this._buffer.length() < this._packetLength) {
                    if (total_filled < 0) {
                        total_filled = 0;
                    }
                    total_filled += this.fill();
                    if (this._buffer.length() < this._packetLength) {
                        return total_filled;
                    }
                }
                Buffer bufHeaderName = null;
                Buffer bufHeaderValue = null;
                int attr_type = 0;
                final byte packetType = Ajp13RequestPacket.getByte(this._buffer);
                switch (packetType) {
                    case 2: {
                        this._handler.startForwardRequest();
                        this._handler.parsedMethod(Ajp13RequestPacket.getMethod(this._buffer));
                        this._handler.parsedProtocol(Ajp13RequestPacket.getString(this._buffer, this._tok0));
                        this._handler.parsedUri(Ajp13RequestPacket.getString(this._buffer, this._tok1));
                        this._handler.parsedRemoteAddr(Ajp13RequestPacket.getString(this._buffer, this._tok1));
                        this._handler.parsedRemoteHost(Ajp13RequestPacket.getString(this._buffer, this._tok1));
                        this._handler.parsedServerName(Ajp13RequestPacket.getString(this._buffer, this._tok1));
                        this._handler.parsedServerPort(Ajp13RequestPacket.getInt(this._buffer));
                        this._handler.parsedSslSecure(Ajp13RequestPacket.getBool(this._buffer));
                        this._headers = Ajp13RequestPacket.getInt(this._buffer);
                        for (int h = 0; h < this._headers; ++h) {
                            bufHeaderName = Ajp13RequestPacket.getHeaderName(this._buffer, this._tok0);
                            bufHeaderValue = Ajp13RequestPacket.getString(this._buffer, this._tok1);
                            if (bufHeaderName != null && bufHeaderName.toString().equals("content-length")) {
                                this._contentLength = BufferUtil.toLong(bufHeaderValue);
                                if (this._contentLength == 0L) {
                                    this._contentLength = 0L;
                                }
                            }
                            this._handler.parsedHeader(bufHeaderName, bufHeaderValue);
                        }
                        for (attr_type = (Ajp13RequestPacket.getByte(this._buffer) & 0xFF); attr_type != 255; attr_type = (Ajp13RequestPacket.getByte(this._buffer) & 0xFF)) {
                            switch (attr_type) {
                                case 3: {
                                    this._handler.parsedRemoteUser(Ajp13RequestPacket.getString(this._buffer, this._tok1));
                                    break;
                                }
                                case 4: {
                                    this._handler.parsedAuthorizationType(Ajp13RequestPacket.getString(this._buffer, this._tok1));
                                    break;
                                }
                                case 5: {
                                    this._handler.parsedQueryString(Ajp13RequestPacket.getString(this._buffer, this._tok1));
                                    break;
                                }
                                case 6: {
                                    this._handler.parsedRequestAttribute("org.eclipse.jetty.ajp.JVMRoute", Ajp13RequestPacket.getString(this._buffer, this._tok1));
                                    break;
                                }
                                case 7: {
                                    this._handler.parsedSslCert(Ajp13RequestPacket.getString(this._buffer, this._tok1));
                                    break;
                                }
                                case 8: {
                                    this._handler.parsedSslCipher(Ajp13RequestPacket.getString(this._buffer, this._tok1));
                                    break;
                                }
                                case 9: {
                                    this._handler.parsedSslSession(Ajp13RequestPacket.getString(this._buffer, this._tok1));
                                    break;
                                }
                                case 10: {
                                    this._handler.parsedRequestAttribute(Ajp13RequestPacket.getString(this._buffer, this._tok0).toString(), Ajp13RequestPacket.getString(this._buffer, this._tok1));
                                    break;
                                }
                                case 11: {
                                    final int length = Ajp13RequestPacket.getInt(this._buffer);
                                    if (length > 0 && length < 16) {
                                        this._buffer.skip(-2);
                                        this._handler.parsedSslKeySize(Integer.parseInt(Ajp13RequestPacket.getString(this._buffer, this._tok1).toString()));
                                        break;
                                    }
                                    this._handler.parsedSslKeySize(length);
                                    break;
                                }
                                case 12: {
                                    break;
                                }
                                case 13: {
                                    break;
                                }
                                case 1: {
                                    this._handler.parsedContextPath(Ajp13RequestPacket.getString(this._buffer, this._tok1));
                                    break;
                                }
                                case 2: {
                                    this._handler.parsedServletPath(Ajp13RequestPacket.getString(this._buffer, this._tok1));
                                    break;
                                }
                                default: {
                                    Ajp13Parser.LOG.warn("Unsupported Ajp13 Request Attribute {}", new Integer(attr_type));
                                    break;
                                }
                            }
                        }
                        this._contentPosition = 0L;
                        switch ((int)this._contentLength) {
                            case 0: {
                                this._state = 0;
                                this._handler.headerComplete();
                                this._handler.messageComplete(this._contentPosition);
                                break Label_1523;
                            }
                            case -3: {
                                this._generator.getBodyChunk();
                                if (this._buffers != null && this._body == null && this._buffer == this._header && this._header.length() <= 0) {
                                    (this._body = this._buffers.getBuffer()).clear();
                                }
                                this._state = 1;
                                this._handler.headerComplete();
                                return total_filled;
                            }
                            default: {
                                if (this._buffers != null && this._body == null && this._buffer == this._header && this._contentLength > this._header.capacity() - this._header.getIndex()) {
                                    (this._body = this._buffers.getBuffer()).clear();
                                }
                                this._state = 1;
                                this._handler.headerComplete();
                                return total_filled;
                            }
                        }
                        break;
                    }
                    case 10: {
                        this._generator.sendCPong();
                        if (this._header != null) {
                            this._buffers.returnBuffer(this._header);
                            this._header = null;
                        }
                        if (this._body != null) {
                            this._buffers.returnBuffer(this._body);
                            this._body = null;
                        }
                        this._buffer = null;
                        this.reset();
                        return -1;
                    }
                    case 7: {
                        this.shutdownRequest();
                        return -1;
                    }
                    default: {
                        Ajp13Parser.LOG.warn("AJP13 message type ({PING}: " + packetType + " ) not supported/recognized as an AJP request", new Object[0]);
                        throw new IllegalStateException("PING is not implemented");
                    }
                }
            }
        }
        while (this._state > 0) {
            switch (this._state) {
                case 1: {
                    if (this._buffer.length() < 6) {
                        if (total_filled < 0) {
                            total_filled = 0;
                        }
                        total_filled += this.fill();
                        if (this._buffer.length() < 6) {
                            return total_filled;
                        }
                    }
                    final int _magic2 = Ajp13RequestPacket.getInt(this._buffer);
                    if (_magic2 != 4660) {
                        throw new IOException("Bad AJP13 rcv packet: 0x" + Integer.toHexString(_magic2) + " expected " + "0x" + Integer.toHexString(4660) + " " + this);
                    }
                    this._chunkPosition = 0;
                    this._chunkLength = Ajp13RequestPacket.getInt(this._buffer) - 2;
                    Ajp13RequestPacket.getInt(this._buffer);
                    if (this._chunkLength == 0) {
                        this._state = 0;
                        this._generator.gotBody();
                        this._handler.messageComplete(this._contentPosition);
                        return total_filled;
                    }
                    this._state = 2;
                    continue;
                }
                case 2: {
                    if (this._buffer.length() < this._chunkLength) {
                        if (total_filled < 0) {
                            total_filled = 0;
                        }
                        total_filled += this.fill();
                        if (this._buffer.length() < this._chunkLength) {
                            return total_filled;
                        }
                    }
                    int remaining = this._chunkLength - this._chunkPosition;
                    if (remaining == 0) {
                        this._state = 1;
                        if (this._contentPosition < this._contentLength) {
                            this._generator.getBodyChunk();
                        }
                        else {
                            this._generator.gotBody();
                        }
                        return total_filled;
                    }
                    if (this._buffer.length() < remaining) {
                        remaining = this._buffer.length();
                    }
                    final Buffer chunk = Ajp13RequestPacket.get(this._buffer, remaining);
                    this._contentPosition += chunk.length();
                    this._chunkPosition += chunk.length();
                    this._contentView.update(chunk);
                    remaining = this._chunkLength - this._chunkPosition;
                    if (remaining == 0) {
                        this._state = 1;
                        if (this._contentPosition < this._contentLength || this._contentLength == -3L) {
                            this._generator.getBodyChunk();
                        }
                        else {
                            this._generator.gotBody();
                        }
                    }
                    this._handler.content(chunk);
                    return total_filled;
                }
                default: {
                    throw new IllegalStateException("Invalid Content State");
                }
            }
        }
        return total_filled;
    }
    
    public void reset() {
        this._state = -1;
        this._contentLength = -3L;
        this._contentPosition = 0L;
        this._length = 0;
        this._packetLength = 0;
        if (this._body != null && this._body.hasContent()) {
            if (this._header == null) {
                this._header = this._buffers.getHeader();
                this._tok0.update(this._header);
                this._tok0.update(0, 0);
                this._tok1.update(this._header);
                this._tok1.update(0, 0);
            }
            else {
                this._header.setMarkIndex(-1);
                this._header.compact();
            }
            int take = this._header.space();
            if (take > this._body.length()) {
                take = this._body.length();
            }
            this._body.peek(this._body.getIndex(), take);
            this._body.skip(this._header.put(this._body.peek(this._body.getIndex(), take)));
        }
        if (this._header != null) {
            this._header.setMarkIndex(-1);
        }
        if (this._body != null) {
            this._body.setMarkIndex(-1);
        }
        this._buffer = this._header;
    }
    
    public void returnBuffers() {
        if (this._body != null && !this._body.hasContent() && this._body.markIndex() == -1) {
            if (this._buffer == this._body) {
                this._buffer = this._header;
            }
            if (this._buffers != null) {
                this._buffers.returnBuffer(this._body);
            }
            this._body = null;
        }
        if (this._header != null && !this._header.hasContent() && this._header.markIndex() == -1) {
            if (this._buffer == this._header) {
                this._buffer = null;
            }
            this._buffers.returnBuffer(this._header);
            this._header = null;
        }
    }
    
    Buffer getHeaderBuffer() {
        return this._buffer;
    }
    
    private void shutdownRequest() {
        this._state = 0;
        if (!Ajp13SocketConnector.__allowShutdown) {
            Ajp13Parser.LOG.warn("AJP13: Shutdown Request is Denied, allowShutdown is set to false!!!", new Object[0]);
            return;
        }
        if (Ajp13SocketConnector.__secretWord != null) {
            Ajp13Parser.LOG.warn("AJP13: Validating Secret Word", new Object[0]);
            try {
                final String secretWord = Ajp13RequestPacket.getString(this._buffer, this._tok1).toString();
                if (!Ajp13SocketConnector.__secretWord.equals(secretWord)) {
                    Ajp13Parser.LOG.warn("AJP13: Shutdown Request Denied, Invalid Sercret word!!!", new Object[0]);
                    throw new IllegalStateException("AJP13: Secret Word is Invalid: Peer has requested shutdown but, Secret Word did not match");
                }
            }
            catch (Exception e) {
                Ajp13Parser.LOG.warn("AJP13: Secret Word is Required!!!", new Object[0]);
                Ajp13Parser.LOG.debug(e);
                throw new IllegalStateException("AJP13: Secret Word is Required: Peer has requested shutdown but, has not provided a Secret Word");
            }
            Ajp13Parser.LOG.warn("AJP13: Shutdown Request is Denied, allowShutdown is set to false!!!", new Object[0]);
            return;
        }
        Ajp13Parser.LOG.warn("AJP13: Peer Has Requested for Shutdown!!!", new Object[0]);
        Ajp13Parser.LOG.warn("AJP13: Jetty 6 is shutting down !!!", new Object[0]);
        System.exit(0);
    }
    
    public boolean isPersistent() {
        return true;
    }
    
    public void setPersistent(final boolean persistent) {
        Ajp13Parser.LOG.warn("AJP13.setPersistent is not IMPLEMENTED!", new Object[0]);
    }
    
    static {
        LOG = Log.getLogger(Ajp13Parser.class);
    }
    
    public static class Input extends ServletInputStream
    {
        private Ajp13Parser _parser;
        private EndPoint _endp;
        private long _maxIdleTime;
        private View _content;
        
        public Input(final Ajp13Parser parser, final long maxIdleTime) {
            this._parser = parser;
            this._endp = parser._endp;
            this._maxIdleTime = maxIdleTime;
            this._content = this._parser._contentView;
        }
        
        @Override
        public int read() throws IOException {
            int c = -1;
            if (this.blockForContent()) {
                c = (0xFF & this._content.get());
            }
            return c;
        }
        
        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            int l = -1;
            if (this.blockForContent()) {
                l = this._content.get(b, off, len);
            }
            return l;
        }
        
        private boolean blockForContent() throws IOException {
            if (this._content.length() > 0) {
                return true;
            }
            if (this._parser.isState(0) || this._parser.isState(-1)) {
                return false;
            }
            if (this._endp == null) {
                this._parser.parseNext();
            }
            else if (this._endp.isBlocking()) {
                this._parser.parseNext();
                while (this._content.length() == 0 && !this._parser.isState(0)) {
                    this._parser.parseNext();
                }
            }
            else {
                long filled = this._parser.parseNext();
                boolean blocked = false;
                while (this._content.length() == 0 && !this._parser.isState(0)) {
                    if (filled > 0L) {
                        blocked = false;
                    }
                    else if (filled == 0L) {
                        if (blocked) {
                            throw new InterruptedIOException("timeout");
                        }
                        blocked = true;
                        this._endp.blockReadable(this._maxIdleTime);
                    }
                    filled = this._parser.parseNext();
                }
            }
            return this._content.length() > 0;
        }
    }
    
    public interface EventHandler
    {
        void content(final Buffer p0) throws IOException;
        
        void headerComplete() throws IOException;
        
        void messageComplete(final long p0) throws IOException;
        
        void parsedHeader(final Buffer p0, final Buffer p1) throws IOException;
        
        void parsedMethod(final Buffer p0) throws IOException;
        
        void parsedProtocol(final Buffer p0) throws IOException;
        
        void parsedQueryString(final Buffer p0) throws IOException;
        
        void parsedRemoteAddr(final Buffer p0) throws IOException;
        
        void parsedRemoteHost(final Buffer p0) throws IOException;
        
        void parsedRequestAttribute(final String p0, final Buffer p1) throws IOException;
        
        void parsedRequestAttribute(final String p0, final int p1) throws IOException;
        
        void parsedServerName(final Buffer p0) throws IOException;
        
        void parsedServerPort(final int p0) throws IOException;
        
        void parsedSslSecure(final boolean p0) throws IOException;
        
        void parsedUri(final Buffer p0) throws IOException;
        
        void startForwardRequest() throws IOException;
        
        void parsedAuthorizationType(final Buffer p0) throws IOException;
        
        void parsedRemoteUser(final Buffer p0) throws IOException;
        
        void parsedServletPath(final Buffer p0) throws IOException;
        
        void parsedContextPath(final Buffer p0) throws IOException;
        
        void parsedSslCert(final Buffer p0) throws IOException;
        
        void parsedSslCipher(final Buffer p0) throws IOException;
        
        void parsedSslSession(final Buffer p0) throws IOException;
        
        void parsedSslKeySize(final int p0) throws IOException;
    }
}
