// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson;

public class JsonParseException extends JsonProcessingException
{
    static final long serialVersionUID = 123L;
    
    public JsonParseException(final String msg, final JsonLocation loc) {
        super(msg, loc);
    }
    
    public JsonParseException(final String msg, final JsonLocation loc, final Throwable root) {
        super(msg, loc, root);
    }
}
