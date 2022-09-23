// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.schema;

import parquet.org.codehaus.jackson.map.JsonMappingException;
import parquet.org.codehaus.jackson.JsonNode;
import java.lang.reflect.Type;
import parquet.org.codehaus.jackson.map.SerializerProvider;

public interface SchemaAware
{
    JsonNode getSchema(final SerializerProvider p0, final Type p1) throws JsonMappingException;
}
