// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.ajp;

import org.eclipse.jetty.util.log.Log;
import java.io.UnsupportedEncodingException;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.http.HttpGenerator;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.io.Buffer;
import java.io.IOException;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.Buffers;
import java.util.HashMap;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.http.AbstractGenerator;

public class Ajp13Generator extends AbstractGenerator
{
    private static final Logger LOG;
    private static HashMap __headerHash;
    private static final byte[] AJP13_CPONG_RESPONSE;
    private static final byte[] AJP13_END_RESPONSE;
    private static final byte[] AJP13_MORE_CONTENT;
    private static String SERVER;
    private boolean _expectMore;
    private boolean _needMore;
    private boolean _needEOC;
    private boolean _bufferPrepared;
    
    public static void setServerVersion(final String version) {
        Ajp13Generator.SERVER = "Jetty(" + version + ")";
    }
    
    public Ajp13Generator(final Buffers buffers, final EndPoint io) {
        super(buffers, io);
        this._expectMore = false;
        this._needMore = false;
        this._needEOC = false;
        this._bufferPrepared = false;
    }
    
    @Override
    public boolean isRequest() {
        return false;
    }
    
    @Override
    public boolean isResponse() {
        return true;
    }
    
    @Override
    public void reset() {
        super.reset();
        this._needEOC = false;
        this._needMore = false;
        this._expectMore = false;
        this._bufferPrepared = false;
        this._last = false;
        this._state = 0;
        this._status = 0;
        this._version = 11;
        this._reason = null;
        this._method = null;
        this._uri = null;
        this._contentWritten = 0L;
        this._contentLength = -3L;
        this._last = false;
        this._head = false;
        this._noContent = false;
        this._persistent = true;
        this._header = null;
        this._buffer = null;
        this._content = null;
    }
    
    @Override
    public int getContentBufferSize() {
        try {
            this.initContent();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return super.getContentBufferSize() - 7;
    }
    
    @Override
    public void increaseContentBufferSize(final int contentBufferSize) {
    }
    
    public void addContent(final Buffer content, final boolean last) throws IOException {
        if (this._noContent) {
            content.clear();
            return;
        }
        if (content.isImmutable()) {
            throw new IllegalArgumentException("immutable");
        }
        if (this._last || this._state == 4) {
            Ajp13Generator.LOG.debug("Ignoring extra content {}", content);
            content.clear();
            return;
        }
        this._last = last;
        if (!this._endp.isOpen()) {
            this._state = 4;
            return;
        }
        if (this._content != null && this._content.length() > 0) {
            this.flushBuffer();
            if (this._content != null && this._content.length() > 0) {
                throw new IllegalStateException("FULL");
            }
        }
        this._content = content;
        this._contentWritten += content.length();
        if (this._head) {
            content.clear();
            this._content = null;
        }
        else {
            this.initContent();
            int len = 0;
            len = this._buffer.put(this._content);
            if (len > 0 && this._buffer.space() == 0) {
                --len;
                this._buffer.setPutIndex(this._buffer.putIndex() - 1);
            }
            this._content.skip(len);
            if (this._content.length() == 0) {
                this._content = null;
            }
        }
    }
    
    public boolean addContent(final byte b) throws IOException {
        if (this._noContent) {
            return false;
        }
        if (this._last || this._state == 4) {
            throw new IllegalStateException("Closed");
        }
        if (!this._endp.isOpen()) {
            this._state = 4;
            return false;
        }
        if (this._content != null && this._content.length() > 0) {
            this.flushBuffer();
            if (this._content != null && this._content.length() > 0) {
                throw new IllegalStateException("FULL");
            }
        }
        ++this._contentWritten;
        if (this._head) {
            return false;
        }
        this.initContent();
        this._buffer.put(b);
        return this._buffer.space() <= 1;
    }
    
    @Override
    public int prepareUncheckedAddContent() throws IOException {
        if (this._noContent) {
            return -1;
        }
        if (this._last || this._state == 4) {
            throw new IllegalStateException("Closed");
        }
        if (!this._endp.isOpen()) {
            this._state = 4;
            return -1;
        }
        final Buffer content = this._content;
        if (content != null && content.length() > 0) {
            this.flushBuffer();
            if (content != null && content.length() > 0) {
                throw new IllegalStateException("FULL");
            }
        }
        this.initContent();
        this._contentWritten -= this._buffer.length();
        if (this._head) {
            return Integer.MAX_VALUE;
        }
        return this._buffer.space() - 1;
    }
    
    @Override
    public void completeHeader(final HttpFields fields, final boolean allContentAdded) throws IOException {
        if (this._state != 0) {
            return;
        }
        if (this._last && !allContentAdded) {
            throw new IllegalStateException("last?");
        }
        this._last |= allContentAdded;
        final boolean has_server = false;
        if (this._persistent == null) {
            this._persistent = (this._version > 10);
        }
        if (this._header == null) {
            this._header = this._buffers.getHeader();
        }
        final Buffer tmpbuf = this._buffer;
        this._buffer = this._header;
        try {
            this._buffer.put((byte)65);
            this._buffer.put((byte)66);
            this.addInt(0);
            this._buffer.put((byte)4);
            this.addInt(this._status);
            if (this._reason == null) {
                this._reason = HttpGenerator.getReasonBuffer(this._status);
            }
            if (this._reason == null) {
                this._reason = new ByteArrayBuffer(Integer.toString(this._status));
            }
            this.addBuffer(this._reason);
            if (this._status == 100 || this._status == 204 || this._status == 304) {
                this._noContent = true;
                this._content = null;
            }
            final int field_index = this._buffer.putIndex();
            this.addInt(0);
            int num_fields = 0;
            if (fields != null) {
                for (int s = fields.size(), f = 0; f < s; ++f) {
                    final HttpFields.Field field = fields.getField(f);
                    if (field != null) {
                        ++num_fields;
                        final byte[] codes = Ajp13Generator.__headerHash.get(field.getName());
                        if (codes != null) {
                            this._buffer.put(codes);
                        }
                        else {
                            this.addString(field.getName());
                        }
                        this.addString(field.getValue());
                    }
                }
            }
            if (!has_server && this._status > 100 && this.getSendServerVersion()) {
                ++num_fields;
                this.addString("Server");
                this.addString(Ajp13Generator.SERVER);
            }
            final int tmp = this._buffer.putIndex();
            this._buffer.setPutIndex(field_index);
            this.addInt(num_fields);
            this._buffer.setPutIndex(tmp);
            final int payloadSize = this._buffer.length() - 4;
            this.addInt(2, payloadSize);
        }
        finally {
            this._buffer = tmpbuf;
        }
        this._state = 2;
    }
    
    @Override
    public void complete() throws IOException {
        if (this._state == 4) {
            return;
        }
        super.complete();
        if (this._state < 3) {
            this._state = 3;
            this._needEOC = true;
        }
        this.flushBuffer();
    }
    
    @Override
    public int flushBuffer() throws IOException {
        try {
            if (this._state == 0 && !this._expectMore) {
                throw new IllegalStateException("State==HEADER");
            }
            this.prepareBuffers();
            if (this._endp == null) {
                if (!this._expectMore && this._needEOC && this._buffer != null) {
                    this._buffer.put(Ajp13Generator.AJP13_END_RESPONSE);
                }
                this._needEOC = false;
                return 0;
            }
            int total = 0;
            long last_len = -1L;
        Label_0480:
            while (true) {
                int len = -1;
                final int to_flush = ((this._header != null && this._header.length() > 0) ? 4 : 0) | ((this._buffer != null && this._buffer.length() > 0) ? 2 : 0);
                switch (to_flush) {
                    case 7: {
                        throw new IllegalStateException();
                    }
                    case 6: {
                        len = this._endp.flush(this._header, this._buffer, (Buffer)null);
                        break;
                    }
                    case 5: {
                        throw new IllegalStateException();
                    }
                    case 4: {
                        len = this._endp.flush(this._header);
                        break;
                    }
                    case 3: {
                        throw new IllegalStateException();
                    }
                    case 2: {
                        len = this._endp.flush(this._buffer);
                        break;
                    }
                    case 1: {
                        throw new IllegalStateException();
                    }
                    case 0: {
                        if (this._header != null) {
                            this._header.clear();
                        }
                        this._bufferPrepared = false;
                        if (this._buffer != null) {
                            this._buffer.clear();
                            this._buffer.setPutIndex(7);
                            this._buffer.setGetIndex(7);
                            if (this._content != null && this._content.length() < this._buffer.space() && this._state != 3) {
                                this._buffer.put(this._content);
                                this._content.clear();
                                this._content = null;
                                break Label_0480;
                            }
                        }
                        if (this._expectMore || this._needEOC || (this._content != null && this._content.length() != 0)) {
                            this.prepareBuffers();
                            break;
                        }
                        if (this._state == 3) {
                            this._state = 4;
                            break Label_0480;
                        }
                        break Label_0480;
                    }
                }
                if (len <= 0) {
                    if (last_len <= 0L) {
                        break;
                    }
                    break;
                }
                else {
                    last_len = len;
                    total += len;
                }
            }
            return total;
        }
        catch (IOException e) {
            Ajp13Generator.LOG.ignore(e);
            throw (e instanceof EofException) ? e : new EofException(e);
        }
    }
    
    private void prepareBuffers() {
        if (!this._bufferPrepared) {
            if (this._content != null && this._content.length() > 0 && this._buffer != null && this._buffer.space() > 0) {
                int len = this._buffer.put(this._content);
                if (len > 0 && this._buffer.space() == 0) {
                    --len;
                    this._buffer.setPutIndex(this._buffer.putIndex() - 1);
                }
                this._content.skip(len);
                if (this._content.length() == 0) {
                    this._content = null;
                }
                if (this._buffer.length() == 0) {
                    this._content = null;
                }
            }
            if (this._buffer != null) {
                final int payloadSize = this._buffer.length();
                if (payloadSize > 0) {
                    this._bufferPrepared = true;
                    this._buffer.put((byte)0);
                    final int put = this._buffer.putIndex();
                    this._buffer.setGetIndex(0);
                    this._buffer.setPutIndex(0);
                    this._buffer.put((byte)65);
                    this._buffer.put((byte)66);
                    this.addInt(payloadSize + 4);
                    this._buffer.put((byte)3);
                    this.addInt(payloadSize);
                    this._buffer.setPutIndex(put);
                }
            }
            if (this._needMore) {
                if (this._header == null) {
                    this._header = this._buffers.getHeader();
                }
                if (this._buffer == null && this._header != null && this._header.space() >= Ajp13Generator.AJP13_MORE_CONTENT.length) {
                    this._header.put(Ajp13Generator.AJP13_MORE_CONTENT);
                    this._needMore = false;
                }
                else if (this._buffer != null && this._buffer.space() >= Ajp13Generator.AJP13_MORE_CONTENT.length) {
                    this._buffer.put(Ajp13Generator.AJP13_MORE_CONTENT);
                    this._needMore = false;
                    this._bufferPrepared = true;
                }
            }
            if (!this._expectMore && this._needEOC) {
                if (this._buffer == null && this._header.space() >= Ajp13Generator.AJP13_END_RESPONSE.length) {
                    this._header.put(Ajp13Generator.AJP13_END_RESPONSE);
                    this._needEOC = false;
                }
                else if (this._buffer != null && this._buffer.space() >= Ajp13Generator.AJP13_END_RESPONSE.length) {
                    this._buffer.put(Ajp13Generator.AJP13_END_RESPONSE);
                    this._needEOC = false;
                    this._bufferPrepared = true;
                }
            }
        }
    }
    
    @Override
    public boolean isComplete() {
        return !this._expectMore && this._state == 4;
    }
    
    private void initContent() throws IOException {
        if (this._buffer == null) {
            (this._buffer = this._buffers.getBuffer()).setPutIndex(7);
            this._buffer.setGetIndex(7);
        }
    }
    
    private void addInt(final int i) {
        this._buffer.put((byte)(i >> 8 & 0xFF));
        this._buffer.put((byte)(i & 0xFF));
    }
    
    private void addInt(final int startIndex, final int i) {
        this._buffer.poke(startIndex, (byte)(i >> 8 & 0xFF));
        this._buffer.poke(startIndex + 1, (byte)(i & 0xFF));
    }
    
    private void addString(final String str) throws UnsupportedEncodingException {
        if (str == null) {
            this.addInt(65535);
            return;
        }
        final byte[] b = str.getBytes("ISO-8859-1");
        this.addInt(b.length);
        this._buffer.put(b);
        this._buffer.put((byte)0);
    }
    
    private void addBuffer(final Buffer b) {
        if (b == null) {
            this.addInt(65535);
            return;
        }
        this.addInt(b.length());
        this._buffer.put(b);
        this._buffer.put((byte)0);
    }
    
    public void getBodyChunk() throws IOException {
        final ByteArrayBuffer bf = new ByteArrayBuffer(Ajp13Generator.AJP13_MORE_CONTENT);
        this._endp.flush((Buffer)bf);
    }
    
    public void gotBody() {
        this._needMore = false;
        this._expectMore = false;
    }
    
    public void sendCPong() throws IOException {
        final Buffer buff = this._buffers.getBuffer();
        buff.put(Ajp13Generator.AJP13_CPONG_RESPONSE);
        do {
            this._endp.flush(buff);
        } while (buff.length() > 0);
        this._buffers.returnBuffer(buff);
        this.reset();
    }
    
    static {
        LOG = Log.getLogger(Ajp13Generator.class);
        Ajp13Generator.__headerHash = new HashMap();
        final byte[] xA001 = { -96, 1 };
        final byte[] xA2 = { -96, 2 };
        final byte[] xA3 = { -96, 3 };
        final byte[] xA4 = { -96, 4 };
        final byte[] xA5 = { -96, 5 };
        final byte[] xA6 = { -96, 6 };
        final byte[] xA7 = { -96, 7 };
        final byte[] xA8 = { -96, 8 };
        final byte[] xA9 = { -96, 9 };
        final byte[] xA00A = { -96, 10 };
        final byte[] xA00B = { -96, 11 };
        Ajp13Generator.__headerHash.put("Content-Type", xA001);
        Ajp13Generator.__headerHash.put("Content-Language", xA2);
        Ajp13Generator.__headerHash.put("Content-Length", xA3);
        Ajp13Generator.__headerHash.put("Date", xA4);
        Ajp13Generator.__headerHash.put("Last-Modified", xA5);
        Ajp13Generator.__headerHash.put("Location", xA6);
        Ajp13Generator.__headerHash.put("Set-Cookie", xA7);
        Ajp13Generator.__headerHash.put("Set-Cookie2", xA8);
        Ajp13Generator.__headerHash.put("Servlet-Engine", xA9);
        Ajp13Generator.__headerHash.put("Status", xA00A);
        Ajp13Generator.__headerHash.put("WWW-Authenticate", xA00B);
        AJP13_CPONG_RESPONSE = new byte[] { 65, 66, 0, 1, 9 };
        AJP13_END_RESPONSE = new byte[] { 65, 66, 0, 2, 5, 1 };
        AJP13_MORE_CONTENT = new byte[] { 65, 66, 0, 3, 6, 31, -7 };
        Ajp13Generator.SERVER = "Server: Jetty(7.x.x)";
    }
}
