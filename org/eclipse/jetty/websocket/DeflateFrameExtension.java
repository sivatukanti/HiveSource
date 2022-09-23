// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.websocket;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import java.util.zip.DataFormatException;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.io.Buffer;
import java.util.Map;
import java.util.zip.Inflater;
import java.util.zip.Deflater;
import org.eclipse.jetty.util.log.Logger;

public class DeflateFrameExtension extends AbstractExtension
{
    private static final Logger LOG;
    private int _minLength;
    private Deflater _deflater;
    private Inflater _inflater;
    
    public DeflateFrameExtension() {
        super("x-deflate-frame");
        this._minLength = 8;
    }
    
    @Override
    public boolean init(final Map<String, String> parameters) {
        if (!parameters.containsKey("minLength")) {
            parameters.put("minLength", Integer.toString(this._minLength));
        }
        if (super.init(parameters)) {
            this._minLength = this.getInitParameter("minLength", this._minLength);
            this._deflater = new Deflater();
            this._inflater = new Inflater();
            return true;
        }
        return false;
    }
    
    @Override
    public void onFrame(final byte flags, final byte opcode, Buffer buffer) {
        if (this.getConnection().isControl(opcode) || !this.isFlag(flags, 1)) {
            super.onFrame(flags, opcode, buffer);
            return;
        }
        if (buffer.array() == null) {
            buffer = buffer.asMutableBuffer();
        }
        int length = 0xFF & buffer.get();
        if (length >= 126) {
            int b = (length == 127) ? 8 : 2;
            length = 0;
            while (b-- > 0) {
                length = 256 * length + (0xFF & buffer.get());
            }
        }
        this._inflater.setInput(buffer.array(), buffer.getIndex(), buffer.length());
        final ByteArrayBuffer buf = new ByteArrayBuffer(length);
        try {
            while (this._inflater.getRemaining() > 0) {
                final int inflated = this._inflater.inflate(buf.array(), buf.putIndex(), buf.space());
                if (inflated == 0) {
                    throw new DataFormatException("insufficient data");
                }
                buf.setPutIndex(buf.putIndex() + inflated);
            }
            super.onFrame(this.clearFlag(flags, 1), opcode, buf);
        }
        catch (DataFormatException e) {
            DeflateFrameExtension.LOG.warn(e);
            this.getConnection().close(1007, e.toString());
        }
    }
    
    @Override
    public void addFrame(final byte flags, final byte opcode, final byte[] content, final int offset, final int length) throws IOException {
        if (this.getConnection().isControl(opcode) || length < this._minLength) {
            super.addFrame(this.clearFlag(flags, 1), opcode, content, offset, length);
            return;
        }
        this._deflater.reset();
        this._deflater.setInput(content, offset, length);
        this._deflater.finish();
        final byte[] out = new byte[length];
        int out_offset = 0;
        if (length > 65535) {
            out[out_offset++] = 127;
            out[out_offset++] = 0;
            out[out_offset++] = 0;
            out[out_offset++] = 0;
            out[out_offset++] = 0;
            out[out_offset++] = (byte)(length >> 24 & 0xFF);
            out[out_offset++] = (byte)(length >> 16 & 0xFF);
            out[out_offset++] = (byte)(length >> 8 & 0xFF);
            out[out_offset++] = (byte)(length & 0xFF);
        }
        else if (length >= 126) {
            out[out_offset++] = 126;
            out[out_offset++] = (byte)(length >> 8);
            out[out_offset++] = (byte)(length & 0xFF);
        }
        else {
            out[out_offset++] = (byte)(length & 0x7F);
        }
        final int l = this._deflater.deflate(out, out_offset, length - out_offset);
        if (this._deflater.finished()) {
            super.addFrame(this.setFlag(flags, 1), opcode, out, 0, l + out_offset);
        }
        else {
            super.addFrame(this.clearFlag(flags, 1), opcode, content, offset, length);
        }
    }
    
    static {
        LOG = Log.getLogger(DeflateFrameExtension.class);
    }
}
