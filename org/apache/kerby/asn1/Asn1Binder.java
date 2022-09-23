// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1;

import java.io.IOException;
import org.apache.kerby.asn1.type.Asn1Encodeable;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.parse.Asn1ParseResult;

public final class Asn1Binder
{
    private Asn1Binder() {
    }
    
    public static void bind(final Asn1ParseResult parseResult, final Asn1Type value) throws IOException {
        value.useDefinitiveLength(parseResult.isDefinitiveLength());
        ((Asn1Encodeable)value).decode(parseResult);
    }
    
    public static void bindWithTagging(final Asn1ParseResult parseResult, final Asn1Type value, final TaggingOption taggingOption) throws IOException {
        if (!parseResult.isTagSpecific()) {
            throw new IllegalArgumentException("Attempting to decode non-tagged value using tagging way");
        }
        ((Asn1Encodeable)value).taggedDecode(parseResult, taggingOption);
    }
}
