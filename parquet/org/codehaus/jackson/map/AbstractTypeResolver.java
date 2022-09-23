// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map;

import parquet.org.codehaus.jackson.type.JavaType;

public abstract class AbstractTypeResolver
{
    public JavaType findTypeMapping(final DeserializationConfig config, final JavaType type) {
        return null;
    }
    
    public JavaType resolveAbstractType(final DeserializationConfig config, final JavaType type) {
        return null;
    }
}
