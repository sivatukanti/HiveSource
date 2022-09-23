// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonLocation;

public class UnresolvedId
{
    private final Object _id;
    private final JsonLocation _location;
    private final Class<?> _type;
    
    public UnresolvedId(final Object id, final Class<?> type, final JsonLocation where) {
        this._id = id;
        this._type = type;
        this._location = where;
    }
    
    public Object getId() {
        return this._id;
    }
    
    public Class<?> getType() {
        return this._type;
    }
    
    public JsonLocation getLocation() {
        return this._location;
    }
    
    @Override
    public String toString() {
        return String.format("Object id [%s] (for %s) at %s", this._id, this._type, this._location);
    }
}
