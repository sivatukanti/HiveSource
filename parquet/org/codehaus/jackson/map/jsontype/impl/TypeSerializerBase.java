// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.jsontype.impl;

import parquet.org.codehaus.jackson.annotate.JsonTypeInfo;
import parquet.org.codehaus.jackson.map.BeanProperty;
import parquet.org.codehaus.jackson.map.jsontype.TypeIdResolver;
import parquet.org.codehaus.jackson.map.TypeSerializer;

public abstract class TypeSerializerBase extends TypeSerializer
{
    protected final TypeIdResolver _idResolver;
    protected final BeanProperty _property;
    
    protected TypeSerializerBase(final TypeIdResolver idRes, final BeanProperty property) {
        this._idResolver = idRes;
        this._property = property;
    }
    
    @Override
    public abstract JsonTypeInfo.As getTypeInclusion();
    
    @Override
    public String getPropertyName() {
        return null;
    }
    
    @Override
    public TypeIdResolver getTypeIdResolver() {
        return this._idResolver;
    }
}
