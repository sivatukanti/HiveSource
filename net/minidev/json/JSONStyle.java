// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json;

import java.io.IOException;

public class JSONStyle
{
    public static final int FLAG_PROTECT_KEYS = 1;
    public static final int FLAG_PROTECT_4WEB = 2;
    public static final int FLAG_PROTECT_VALUES = 4;
    public static final int FLAG_AGRESSIVE = 8;
    public static final int FLAG_IGNORE_NULL = 16;
    public static final JSONStyle NO_COMPRESS;
    public static final JSONStyle MAX_COMPRESS;
    public static final JSONStyle LT_COMPRESS;
    private boolean _protectKeys;
    private boolean _protect4Web;
    private boolean _protectValues;
    private boolean _ignore_null;
    private JStylerObj.MustProtect mpKey;
    private JStylerObj.MustProtect mpValue;
    private JStylerObj.StringProtector esc;
    
    static {
        NO_COMPRESS = new JSONStyle(0);
        MAX_COMPRESS = new JSONStyle(-1);
        LT_COMPRESS = new JSONStyle(2);
    }
    
    public JSONStyle(final int FLAG) {
        this._protectKeys = ((FLAG & 0x1) == 0x0);
        this._protectValues = ((FLAG & 0x4) == 0x0);
        this._protect4Web = ((FLAG & 0x2) == 0x0);
        this._ignore_null = ((FLAG & 0x10) > 0);
        JStylerObj.MustProtect mp;
        if ((FLAG & 0x8) > 0) {
            mp = JStylerObj.MP_AGGRESIVE;
        }
        else {
            mp = JStylerObj.MP_SIMPLE;
        }
        if (this._protectValues) {
            this.mpValue = JStylerObj.MP_TRUE;
        }
        else {
            this.mpValue = mp;
        }
        if (this._protectKeys) {
            this.mpKey = JStylerObj.MP_TRUE;
        }
        else {
            this.mpKey = mp;
        }
        if (this._protect4Web) {
            this.esc = JStylerObj.ESCAPE4Web;
        }
        else {
            this.esc = JStylerObj.ESCAPE_LT;
        }
    }
    
    public JSONStyle() {
        this(0);
    }
    
    public boolean protectKeys() {
        return this._protectKeys;
    }
    
    public boolean protectValues() {
        return this._protectValues;
    }
    
    public boolean protect4Web() {
        return this._protect4Web;
    }
    
    public boolean ignoreNull() {
        return this._ignore_null;
    }
    
    public boolean indent() {
        return false;
    }
    
    public boolean mustProtectKey(final String s) {
        return this.mpKey.mustBeProtect(s);
    }
    
    public boolean mustProtectValue(final String s) {
        return this.mpValue.mustBeProtect(s);
    }
    
    public void writeString(final Appendable out, final String value) throws IOException {
        if (!this.mustProtectValue(value)) {
            out.append(value);
        }
        else {
            out.append('\"');
            JSONValue.escape(value, out, this);
            out.append('\"');
        }
    }
    
    public void escape(final String s, final Appendable out) {
        this.esc.escape(s, out);
    }
    
    public void objectStart(final Appendable out) throws IOException {
        out.append('{');
    }
    
    public void objectStop(final Appendable out) throws IOException {
        out.append('}');
    }
    
    public void objectFirstStart(final Appendable out) throws IOException {
    }
    
    public void objectNext(final Appendable out) throws IOException {
        out.append(',');
    }
    
    public void objectElmStop(final Appendable out) throws IOException {
    }
    
    public void objectEndOfKey(final Appendable out) throws IOException {
        out.append(':');
    }
    
    public void arrayStart(final Appendable out) throws IOException {
        out.append('[');
    }
    
    public void arrayStop(final Appendable out) throws IOException {
        out.append(']');
    }
    
    public void arrayfirstObject(final Appendable out) throws IOException {
    }
    
    public void arrayNextElm(final Appendable out) throws IOException {
        out.append(',');
    }
    
    public void arrayObjectEnd(final Appendable out) throws IOException {
    }
}
