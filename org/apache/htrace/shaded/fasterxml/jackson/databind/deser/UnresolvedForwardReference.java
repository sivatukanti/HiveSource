// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser;

import java.util.Iterator;
import java.util.ArrayList;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonLocation;
import java.util.List;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;

public final class UnresolvedForwardReference extends JsonMappingException
{
    private static final long serialVersionUID = 1L;
    private ReadableObjectId _roid;
    private List<UnresolvedId> _unresolvedIds;
    
    public UnresolvedForwardReference(final String msg, final JsonLocation loc, final ReadableObjectId roid) {
        super(msg, loc);
        this._roid = roid;
    }
    
    public UnresolvedForwardReference(final String msg) {
        super(msg);
        this._unresolvedIds = new ArrayList<UnresolvedId>();
    }
    
    public ReadableObjectId getRoid() {
        return this._roid;
    }
    
    public Object getUnresolvedId() {
        return this._roid.getKey().key;
    }
    
    public void addUnresolvedId(final Object id, final Class<?> type, final JsonLocation where) {
        this._unresolvedIds.add(new UnresolvedId(id, type, where));
    }
    
    public List<UnresolvedId> getUnresolvedIds() {
        return this._unresolvedIds;
    }
    
    @Override
    public String getMessage() {
        final String msg = super.getMessage();
        if (this._unresolvedIds == null) {
            return msg;
        }
        final StringBuilder sb = new StringBuilder(msg);
        final Iterator<UnresolvedId> iterator = this._unresolvedIds.iterator();
        while (iterator.hasNext()) {
            final UnresolvedId unresolvedId = iterator.next();
            sb.append(unresolvedId.toString());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append('.');
        return sb.toString();
    }
}
