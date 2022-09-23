// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map;

import java.io.IOException;
import parquet.org.codehaus.jackson.format.MatchStrength;
import parquet.org.codehaus.jackson.format.InputAccessor;
import parquet.org.codehaus.jackson.ObjectCodec;
import parquet.org.codehaus.jackson.JsonFactory;

public class MappingJsonFactory extends JsonFactory
{
    public MappingJsonFactory() {
        this(null);
    }
    
    public MappingJsonFactory(final ObjectMapper mapper) {
        super(mapper);
        if (mapper == null) {
            this.setCodec(new ObjectMapper(this));
        }
    }
    
    @Override
    public final ObjectMapper getCodec() {
        return (ObjectMapper)this._objectCodec;
    }
    
    @Override
    public String getFormatName() {
        return "JSON";
    }
    
    @Override
    public MatchStrength hasFormat(final InputAccessor acc) throws IOException {
        return this.hasJSONFormat(acc);
    }
}
