// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.ser;

import parquet.org.codehaus.jackson.map.annotate.JacksonStdImpl;

@Deprecated
@JacksonStdImpl
public final class ToStringSerializer extends parquet.org.codehaus.jackson.map.ser.std.ToStringSerializer
{
    public static final ToStringSerializer instance;
    
    static {
        instance = new ToStringSerializer();
    }
}
