// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser.std;

import org.apache.htrace.shaded.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import java.net.InetAddress;

public class InetAddressSerializer extends StdScalarSerializer<InetAddress>
{
    public InetAddressSerializer() {
        super(InetAddress.class);
    }
    
    @Override
    public void serialize(final InetAddress value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        String str = value.toString().trim();
        final int ix = str.indexOf(47);
        if (ix >= 0) {
            if (ix == 0) {
                str = str.substring(1);
            }
            else {
                str = str.substring(0, ix);
            }
        }
        jgen.writeString(str);
    }
    
    @Override
    public void serializeWithType(final InetAddress value, final JsonGenerator jgen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException, JsonGenerationException {
        typeSer.writeTypePrefixForScalar(value, jgen, InetAddress.class);
        this.serialize(value, jgen, provider);
        typeSer.writeTypeSuffixForScalar(value, jgen);
    }
}
