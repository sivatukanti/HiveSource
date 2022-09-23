// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header;

import java.text.ParseException;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.util.Map;
import javax.ws.rs.core.MediaType;

public class AcceptableMediaType extends MediaType implements QualityFactor
{
    private final int q;
    
    public AcceptableMediaType(final String p, final String s) {
        super(p, s);
        this.q = 1000;
    }
    
    public AcceptableMediaType(final String p, final String s, final int q, final Map<String, String> parameters) {
        super(p, s, parameters);
        this.q = q;
    }
    
    @Override
    public int getQuality() {
        return this.q;
    }
    
    public static AcceptableMediaType valueOf(final HttpHeaderReader reader) throws ParseException {
        reader.hasNext();
        final String type = reader.nextToken();
        String subType = "*";
        if (reader.hasNextSeparator('/', false)) {
            reader.next(false);
            subType = reader.nextToken();
        }
        Map<String, String> parameters = null;
        int quality = 1000;
        if (reader.hasNext()) {
            parameters = HttpHeaderReader.readParameters(reader);
            if (parameters != null) {
                final String v = parameters.get("q");
                if (v != null) {
                    quality = HttpHeaderReader.readQualityFactor(v);
                }
            }
        }
        return new AcceptableMediaType(type, subType, quality, parameters);
    }
}
