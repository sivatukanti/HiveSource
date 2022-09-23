// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.core.json;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonStreamContext;

public class JsonWriteContext extends JsonStreamContext
{
    public static final int STATUS_OK_AS_IS = 0;
    public static final int STATUS_OK_AFTER_COMMA = 1;
    public static final int STATUS_OK_AFTER_COLON = 2;
    public static final int STATUS_OK_AFTER_SPACE = 3;
    public static final int STATUS_EXPECT_VALUE = 4;
    public static final int STATUS_EXPECT_NAME = 5;
    protected final JsonWriteContext _parent;
    protected final DupDetector _dups;
    protected JsonWriteContext _child;
    protected String _currentName;
    protected boolean _gotName;
    
    protected JsonWriteContext(final int type, final JsonWriteContext parent, final DupDetector dups) {
        this._child = null;
        this._type = type;
        this._parent = parent;
        this._dups = dups;
        this._index = -1;
    }
    
    protected JsonWriteContext reset(final int type) {
        this._type = type;
        this._index = -1;
        this._currentName = null;
        this._gotName = false;
        if (this._dups != null) {
            this._dups.reset();
        }
        return this;
    }
    
    @Deprecated
    public static JsonWriteContext createRootContext() {
        return createRootContext(null);
    }
    
    public static JsonWriteContext createRootContext(final DupDetector dd) {
        return new JsonWriteContext(0, null, dd);
    }
    
    public JsonWriteContext createChildArrayContext() {
        JsonWriteContext ctxt = this._child;
        if (ctxt == null) {
            ctxt = (this._child = new JsonWriteContext(1, this, (this._dups == null) ? null : this._dups.child()));
            return ctxt;
        }
        return ctxt.reset(1);
    }
    
    public JsonWriteContext createChildObjectContext() {
        JsonWriteContext ctxt = this._child;
        if (ctxt == null) {
            ctxt = (this._child = new JsonWriteContext(2, this, (this._dups == null) ? null : this._dups.child()));
            return ctxt;
        }
        return ctxt.reset(2);
    }
    
    @Override
    public final JsonWriteContext getParent() {
        return this._parent;
    }
    
    @Override
    public final String getCurrentName() {
        return this._currentName;
    }
    
    public final int writeFieldName(final String name) throws JsonProcessingException {
        this._gotName = true;
        this._currentName = name;
        if (this._dups != null) {
            this._checkDup(this._dups, name);
        }
        return (this._index >= 0) ? 1 : 0;
    }
    
    private final void _checkDup(final DupDetector dd, final String name) throws JsonProcessingException {
        if (dd.isDup(name)) {
            throw new JsonGenerationException("Duplicate field '" + name + "'");
        }
    }
    
    public final int writeValue() {
        if (this._type == 2) {
            this._gotName = false;
            ++this._index;
            return 2;
        }
        if (this._type == 1) {
            final int ix = this._index;
            ++this._index;
            return (ix >= 0) ? 1 : 0;
        }
        ++this._index;
        return (this._index == 0) ? 0 : 3;
    }
    
    protected void appendDesc(final StringBuilder sb) {
        if (this._type == 2) {
            sb.append('{');
            if (this._currentName != null) {
                sb.append('\"');
                sb.append(this._currentName);
                sb.append('\"');
            }
            else {
                sb.append('?');
            }
            sb.append('}');
        }
        else if (this._type == 1) {
            sb.append('[');
            sb.append(this.getCurrentIndex());
            sb.append(']');
        }
        else {
            sb.append("/");
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(64);
        this.appendDesc(sb);
        return sb.toString();
    }
}
