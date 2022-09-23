// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.deser;

import parquet.org.codehaus.jackson.map.TypeDeserializer;
import parquet.org.codehaus.jackson.map.JsonDeserializer;
import parquet.org.codehaus.jackson.map.type.ArrayType;
import parquet.org.codehaus.jackson.map.deser.std.ObjectArrayDeserializer;

@Deprecated
public class ArrayDeserializer extends ObjectArrayDeserializer
{
    @Deprecated
    public ArrayDeserializer(final ArrayType arrayType, final JsonDeserializer<Object> elemDeser) {
        this(arrayType, elemDeser, null);
    }
    
    public ArrayDeserializer(final ArrayType arrayType, final JsonDeserializer<Object> elemDeser, final TypeDeserializer elemTypeDeser) {
        super(arrayType, elemDeser, elemTypeDeser);
    }
}
