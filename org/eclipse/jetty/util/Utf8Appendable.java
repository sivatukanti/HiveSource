// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import org.eclipse.jetty.util.log.Log;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.eclipse.jetty.util.log.Logger;

public abstract class Utf8Appendable
{
    protected static final Logger LOG;
    public static final char REPLACEMENT = '\ufffd';
    public static final byte[] REPLACEMENT_UTF8;
    private static final int UTF8_ACCEPT = 0;
    private static final int UTF8_REJECT = 12;
    protected final Appendable _appendable;
    protected int _state;
    private static final byte[] BYTE_TABLE;
    private static final byte[] TRANS_TABLE;
    private int _codep;
    
    public Utf8Appendable(final Appendable appendable) {
        this._state = 0;
        this._appendable = appendable;
    }
    
    public abstract int length();
    
    protected void reset() {
        this._state = 0;
    }
    
    private void checkCharAppend() throws IOException {
        if (this._state != 0) {
            this._appendable.append('\ufffd');
            final int state = this._state;
            this._state = 0;
            throw new NotUtf8Exception("char appended in state " + state);
        }
    }
    
    public void append(final char c) {
        try {
            this.checkCharAppend();
            this._appendable.append(c);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void append(final String s) {
        try {
            this.checkCharAppend();
            this._appendable.append(s);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void append(final String s, final int offset, final int length) {
        try {
            this.checkCharAppend();
            this._appendable.append(s, offset, offset + length);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void append(final byte b) {
        try {
            this.appendByte(b);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void append(final ByteBuffer buf) {
        try {
            while (buf.remaining() > 0) {
                this.appendByte(buf.get());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void append(final byte[] b, final int offset, final int length) {
        try {
            for (int end = offset + length, i = offset; i < end; ++i) {
                this.appendByte(b[i]);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public boolean append(final byte[] b, final int offset, final int length, final int maxChars) {
        try {
            for (int end = offset + length, i = offset; i < end; ++i) {
                if (this.length() > maxChars) {
                    return false;
                }
                this.appendByte(b[i]);
            }
            return true;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void appendByte(final byte b) throws IOException {
        if (b > 0 && this._state == 0) {
            this._appendable.append((char)(b & 0xFF));
        }
        else {
            final int i = b & 0xFF;
            final int type = Utf8Appendable.BYTE_TABLE[i];
            this._codep = ((this._state == 0) ? (255 >> type & i) : ((i & 0x3F) | this._codep << 6));
            final int next = Utf8Appendable.TRANS_TABLE[this._state + type];
            switch (next) {
                case 0: {
                    this._state = next;
                    if (this._codep < 55296) {
                        this._appendable.append((char)this._codep);
                        break;
                    }
                    for (final char c : Character.toChars(this._codep)) {
                        this._appendable.append(c);
                    }
                    break;
                }
                case 12: {
                    final String reason = "byte " + TypeUtil.toHexString(b) + " in state " + this._state / 12;
                    this._codep = 0;
                    this._state = 0;
                    this._appendable.append('\ufffd');
                    throw new NotUtf8Exception(reason);
                }
                default: {
                    this._state = next;
                    break;
                }
            }
        }
    }
    
    public boolean isUtf8SequenceComplete() {
        return this._state == 0;
    }
    
    protected void checkState() {
        if (!this.isUtf8SequenceComplete()) {
            this._codep = 0;
            this._state = 0;
            try {
                this._appendable.append('\ufffd');
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            throw new NotUtf8Exception("incomplete UTF8 sequence");
        }
    }
    
    public String toReplacedString() {
        if (!this.isUtf8SequenceComplete()) {
            this._codep = 0;
            this._state = 0;
            try {
                this._appendable.append('\ufffd');
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            final Throwable th = new NotUtf8Exception("incomplete UTF8 sequence");
            Utf8Appendable.LOG.warn(th.toString(), new Object[0]);
            Utf8Appendable.LOG.debug(th);
        }
        return this._appendable.toString();
    }
    
    static {
        LOG = Log.getLogger(Utf8Appendable.class);
        REPLACEMENT_UTF8 = new byte[] { -17, -65, -67 };
        BYTE_TABLE = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 10, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 3, 3, 11, 6, 6, 6, 5, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8 };
        TRANS_TABLE = new byte[] { 0, 12, 24, 36, 60, 96, 84, 12, 12, 12, 48, 72, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 0, 12, 12, 12, 12, 12, 0, 12, 0, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 24, 12, 12, 12, 12, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 12, 12, 24, 12, 12, 12, 12, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 36, 12, 36, 12, 12, 12, 36, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12 };
    }
    
    public static class NotUtf8Exception extends IllegalArgumentException
    {
        public NotUtf8Exception(final String reason) {
            super("Not valid UTF8! " + reason);
        }
    }
}
