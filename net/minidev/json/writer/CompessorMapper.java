// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.writer;

import net.minidev.json.JSONValue;
import java.io.IOException;
import net.minidev.json.JSONStyle;

public class CompessorMapper extends JsonReaderI<CompessorMapper>
{
    private Appendable out;
    private JSONStyle compression;
    private Boolean _isObj;
    private boolean needSep;
    private boolean isOpen;
    private boolean isClosed;
    
    private boolean isArray() {
        return this._isObj == Boolean.FALSE;
    }
    
    private boolean isObject() {
        return this._isObj == Boolean.TRUE;
    }
    
    private boolean isCompressor(final Object obj) {
        return obj instanceof CompessorMapper;
    }
    
    public CompessorMapper(final JsonReader base, final Appendable out, final JSONStyle compression) {
        this(base, out, compression, null);
    }
    
    public CompessorMapper(final JsonReader base, final Appendable out, final JSONStyle compression, final Boolean isObj) {
        super(base);
        this.needSep = false;
        this.isOpen = false;
        this.isClosed = false;
        this.out = out;
        this.compression = compression;
        this._isObj = isObj;
    }
    
    @Override
    public JsonReaderI<?> startObject(final String key) throws IOException {
        this.open(this);
        this.startKey(key);
        final CompessorMapper r = new CompessorMapper(this.base, this.out, this.compression, true);
        this.open(r);
        return r;
    }
    
    @Override
    public JsonReaderI<?> startArray(final String key) throws IOException {
        this.open(this);
        this.startKey(key);
        final CompessorMapper r = new CompessorMapper(this.base, this.out, this.compression, false);
        this.open(r);
        return r;
    }
    
    private void startKey(final String key) throws IOException {
        this.addComma();
        if (this.isArray()) {
            return;
        }
        if (!this.compression.mustProtectKey(key)) {
            this.out.append(key);
        }
        else {
            this.out.append('\"');
            JSONValue.escape(key, this.out, this.compression);
            this.out.append('\"');
        }
        this.out.append(':');
    }
    
    @Override
    public void setValue(final Object current, final String key, final Object value) throws IOException {
        if (this.isCompressor(value)) {
            this.addComma();
            return;
        }
        this.startKey(key);
        this.writeValue(value);
    }
    
    @Override
    public void addValue(final Object current, final Object value) throws IOException {
        this.addComma();
        this.writeValue(value);
    }
    
    private void addComma() throws IOException {
        if (this.needSep) {
            this.out.append(',');
        }
        else {
            this.needSep = true;
        }
    }
    
    private void writeValue(final Object value) throws IOException {
        if (value instanceof String) {
            this.compression.writeString(this.out, (String)value);
        }
        else if (this.isCompressor(value)) {
            this.close(value);
        }
        else {
            JSONValue.writeJSONString(value, this.out, this.compression);
        }
    }
    
    @Override
    public Object createObject() {
        this._isObj = true;
        try {
            this.open(this);
        }
        catch (Exception ex) {}
        return this;
    }
    
    @Override
    public Object createArray() {
        this._isObj = false;
        try {
            this.open(this);
        }
        catch (Exception ex) {}
        return this;
    }
    
    @Override
    public CompessorMapper convert(final Object current) {
        try {
            this.close(current);
            return this;
        }
        catch (Exception e) {
            return this;
        }
    }
    
    private void close(final Object obj) throws IOException {
        if (!this.isCompressor(obj)) {
            return;
        }
        if (((CompessorMapper)obj).isClosed) {
            return;
        }
        ((CompessorMapper)obj).isClosed = true;
        if (((CompessorMapper)obj).isObject()) {
            this.out.append('}');
            this.needSep = true;
        }
        else if (((CompessorMapper)obj).isArray()) {
            this.out.append(']');
            this.needSep = true;
        }
    }
    
    private void open(final Object obj) throws IOException {
        if (!this.isCompressor(obj)) {
            return;
        }
        if (((CompessorMapper)obj).isOpen) {
            return;
        }
        ((CompessorMapper)obj).isOpen = true;
        if (((CompessorMapper)obj).isObject()) {
            this.out.append('{');
            this.needSep = false;
        }
        else if (((CompessorMapper)obj).isArray()) {
            this.out.append('[');
            this.needSep = false;
        }
    }
}
