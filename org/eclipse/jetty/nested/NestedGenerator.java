// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.nested;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.http.HttpFields;
import java.io.IOException;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.Buffers;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.http.AbstractGenerator;

public class NestedGenerator extends AbstractGenerator
{
    private static final Logger LOG;
    final HttpServletResponse _response;
    final String _nestedIn;
    
    public NestedGenerator(final Buffers buffers, final EndPoint io, final HttpServletResponse response, final String nestedIn) {
        super(buffers, io);
        this._response = response;
        this._nestedIn = nestedIn;
    }
    
    public void addContent(final Buffer content, final boolean last) throws IOException {
        NestedGenerator.LOG.debug("addContent {} {}", content.length(), last);
        if (this._noContent) {
            content.clear();
            return;
        }
        if (content.isImmutable()) {
            throw new IllegalArgumentException("immutable");
        }
        if (this._last || this._state == 4) {
            NestedGenerator.LOG.debug("Ignoring extra content {}", content);
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
        else if (!last || this._buffer != null) {
            this.initBuffer();
            int len = 0;
            len = this._buffer.put(this._content);
            if (len > 0 && this._buffer.space() == 0) {
                --len;
                this._buffer.setPutIndex(this._buffer.putIndex() - 1);
            }
            NestedGenerator.LOG.debug("copied {} to buffer", new Object[] { len });
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
        this.initBuffer();
        this._buffer.put(b);
        return this._buffer.space() <= 1;
    }
    
    private void initBuffer() throws IOException {
        if (this._buffer == null) {
            this._buffer = this._buffers.getBuffer();
        }
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
    public int prepareUncheckedAddContent() throws IOException {
        this.initBuffer();
        return this._buffer.space();
    }
    
    @Override
    public void completeHeader(final HttpFields fields, final boolean allContentAdded) throws IOException {
        if (NestedGenerator.LOG.isDebugEnabled()) {
            NestedGenerator.LOG.debug("completeHeader: {}", fields.toString().trim().replace("\r\n", "|"));
        }
        if (this._state != 0) {
            return;
        }
        if (this._last && !allContentAdded) {
            throw new IllegalStateException("last?");
        }
        this._last |= allContentAdded;
        if (this._persistent == null) {
            this._persistent = (this._version > 10);
        }
        if (this._reason == null) {
            this._response.setStatus(this._status);
        }
        else {
            this._response.setStatus(this._status, this._reason.toString());
        }
        if (this._status == 100 || this._status == 204 || this._status == 304) {
            this._noContent = true;
            this._content = null;
        }
        final boolean has_server = false;
        if (fields != null) {
            for (int s = fields.size(), f = 0; f < s; ++f) {
                final HttpFields.Field field = fields.getField(f);
                if (field != null) {
                    this._response.setHeader(field.getName(), field.getValue());
                }
            }
        }
        if (!has_server && this._status > 100 && this.getSendServerVersion()) {
            this._response.setHeader("Server", "Jetty(" + Server.getVersion() + ",nested in " + this._nestedIn + ")");
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
        }
        this.flushBuffer();
    }
    
    @Override
    public int flushBuffer() throws IOException {
        if (this._state == 0) {
            throw new IllegalStateException("State==HEADER");
        }
        int len = 0;
        if (this._buffer == null) {
            if (this._content != null && this._content.length() > 0) {
                len = this._endp.flush(this._content);
                if (len > 0) {
                    this._content.skip(len);
                }
            }
        }
        else {
            if (this._buffer.length() == 0 && this._content != null && this._content.length() > 0) {
                this._content.skip(this._buffer.put(this._content));
            }
            final int size = this._buffer.length();
            len = this._endp.flush(this._buffer);
            NestedGenerator.LOG.debug("flushBuffer {} of {}", len, size);
            if (len > 0) {
                this._buffer.skip(len);
            }
        }
        if (this._content != null && this._content.length() == 0) {
            this._content = null;
        }
        if (this._buffer != null && this._buffer.length() == 0 && this._content == null) {
            this._buffers.returnBuffer(this._buffer);
            this._buffer = null;
        }
        if (this._state == 3 && this._buffer == null && this._content == null) {
            this._state = 4;
        }
        return len;
    }
    
    static {
        LOG = Log.getLogger(NestedGenerator.class);
    }
}
