// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.jsontype.impl;

import parquet.org.codehaus.jackson.annotate.JsonTypeInfo;
import parquet.org.codehaus.jackson.map.BeanProperty;
import parquet.org.codehaus.jackson.map.jsontype.TypeIdResolver;
import parquet.org.codehaus.jackson.type.JavaType;

public class AsExternalTypeDeserializer extends AsArrayTypeDeserializer
{
    protected final String _typePropertyName;
    
    public AsExternalTypeDeserializer(final JavaType bt, final TypeIdResolver idRes, final BeanProperty property, final Class<?> defaultImpl, final String typePropName) {
        super(bt, idRes, property, defaultImpl);
        this._typePropertyName = typePropName;
    }
    
    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.EXTERNAL_PROPERTY;
    }
    
    @Override
    public String getPropertyName() {
        return this._typePropertyName;
    }
}
