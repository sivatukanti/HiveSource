// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.deser;

import parquet.org.codehaus.jackson.map.deser.std.StdDeserializer;

@Deprecated
public abstract class StdScalarDeserializer<T> extends StdDeserializer<T>
{
    protected StdScalarDeserializer(final Class<?> vc) {
        super(vc);
    }
}
