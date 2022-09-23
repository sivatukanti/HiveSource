// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map;

import parquet.org.codehaus.jackson.JsonProcessingException;
import java.io.IOException;

public abstract class KeyDeserializer
{
    public abstract Object deserializeKey(final String p0, final DeserializationContext p1) throws IOException, JsonProcessingException;
    
    public abstract static class None extends KeyDeserializer
    {
    }
}
