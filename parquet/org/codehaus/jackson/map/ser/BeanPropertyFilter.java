// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.ser;

import parquet.org.codehaus.jackson.map.SerializerProvider;
import parquet.org.codehaus.jackson.JsonGenerator;

public interface BeanPropertyFilter
{
    void serializeAsField(final Object p0, final JsonGenerator p1, final SerializerProvider p2, final BeanPropertyWriter p3) throws Exception;
}
