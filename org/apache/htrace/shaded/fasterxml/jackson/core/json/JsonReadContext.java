// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.core.json;

import org.apache.htrace.shaded.fasterxml.jackson.core.io.CharTypes;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParseException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonLocation;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonStreamContext;

public final class JsonReadContext extends JsonStreamContext
{
    protected final JsonReadContext _parent;
    protected final DupDetector _dups;
    protected int _lineNr;
    protected int _columnNr;
    protected String _currentName;
    protected JsonReadContext _child;
    
    public JsonReadContext(final JsonReadContext parent, final DupDetector dups, final int type, final int lineNr, final int colNr) {
        this._child = null;
        this._parent = parent;
        this._dups = dups;
        this._type = type;
        this._lineNr = lineNr;
        this._columnNr = colNr;
        this._index = -1;
    }
    
    protected void reset(final int type, final int lineNr, final int colNr) {
        this._type = type;
        this._index = -1;
        this._lineNr = lineNr;
        this._columnNr = colNr;
        this._currentName = null;
        if (this._dups != null) {
            this._dups.reset();
        }
    }
    
    @Deprecated
    public static JsonReadContext createRootContext(final int lineNr, final int colNr) {
        return createRootContext(lineNr, colNr, null);
    }
    
    public static JsonReadContext createRootContext(final int lineNr, final int colNr, final DupDetector dups) {
        return new JsonReadContext(null, dups, 0, lineNr, colNr);
    }
    
    @Deprecated
    public static JsonReadContext createRootContext() {
        return createRootContext(null);
    }
    
    public static JsonReadContext createRootContext(final DupDetector dups) {
        return new JsonReadContext(null, dups, 0, 1, 0);
    }
    
    public JsonReadContext createChildArrayContext(final int lineNr, final int colNr) {
        JsonReadContext ctxt = this._child;
        if (ctxt == null) {
            ctxt = (this._child = new JsonReadContext(this, (this._dups == null) ? null : this._dups.child(), 1, lineNr, colNr));
        }
        else {
            ctxt.reset(1, lineNr, colNr);
        }
        return ctxt;
    }
    
    public JsonReadContext createChildObjectContext(final int lineNr, final int colNr) {
        JsonReadContext ctxt = this._child;
        if (ctxt == null) {
            ctxt = (this._child = new JsonReadContext(this, (this._dups == null) ? null : this._dups.child(), 2, lineNr, colNr));
            return ctxt;
        }
        ctxt.reset(2, lineNr, colNr);
        return ctxt;
    }
    
    @Override
    public String getCurrentName() {
        return this._currentName;
    }
    
    @Override
    public JsonReadContext getParent() {
        return this._parent;
    }
    
    public JsonLocation getStartLocation(final Object srcRef) {
        final long totalChars = -1L;
        return new JsonLocation(srcRef, totalChars, this._lineNr, this._columnNr);
    }
    
    public boolean expectComma() {
        final int ix = ++this._index;
        return this._type != 0 && ix > 0;
    }
    
    public void setCurrentName(final String name) throws JsonProcessingException {
        this._currentName = name;
        if (this._dups != null) {
            this._checkDup(this._dups, name);
        }
    }
    
    private void _checkDup(final DupDetector dd, final String name) throws JsonProcessingException {
        if (dd.isDup(name)) {
            throw new JsonParseException("Duplicate field '" + name + "'", dd.findLocation());
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(64);
        switch (this._type) {
            case 0: {
                sb.append("/");
                break;
            }
            case 1: {
                sb.append('[');
                sb.append(this.getCurrentIndex());
                sb.append(']');
                break;
            }
            case 2: {
                sb.append('{');
                if (this._currentName != null) {
                    sb.append('\"');
                    CharTypes.appendQuoted(sb, this._currentName);
                    sb.append('\"');
                }
                else {
                    sb.append('?');
                }
                sb.append('}');
                break;
            }
        }
        return sb.toString();
    }
}
