// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.deser.std;

import parquet.org.codehaus.jackson.JsonProcessingException;
import java.io.IOException;
import parquet.org.codehaus.jackson.map.DeserializationContext;
import parquet.org.codehaus.jackson.JsonParser;
import parquet.org.codehaus.jackson.map.annotate.JacksonStdImpl;
import parquet.org.codehaus.jackson.util.TokenBuffer;

@JacksonStdImpl
public class TokenBufferDeserializer extends StdScalarDeserializer<TokenBuffer>
{
    public TokenBufferDeserializer() {
        super(TokenBuffer.class);
    }
    
    @Override
    public TokenBuffer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final TokenBuffer tb = new TokenBuffer(jp.getCodec());
        tb.copyCurrentStructure(jp);
        return tb;
    }
}
