// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.format.MatchStrength;
import org.apache.htrace.shaded.fasterxml.jackson.core.format.InputAccessor;
import org.apache.htrace.shaded.fasterxml.jackson.core.ObjectCodec;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonFactory;

public class MappingJsonFactory extends JsonFactory
{
    private static final long serialVersionUID = -6744103724013275513L;
    
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
    public JsonFactory copy() {
        this._checkInvalidCopy(MappingJsonFactory.class);
        return new MappingJsonFactory(null);
    }
    
    @Override
    public String getFormatName() {
        return "JSON";
    }
    
    @Override
    public MatchStrength hasFormat(final InputAccessor acc) throws IOException {
        if (this.getClass() == MappingJsonFactory.class) {
            return this.hasJSONFormat(acc);
        }
        return null;
    }
}
