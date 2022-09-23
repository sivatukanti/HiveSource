// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1;

import org.apache.kerby.asn1.type.Asn1Encodeable;
import java.io.IOException;
import org.apache.kerby.asn1.type.Asn1Specific;
import org.apache.kerby.asn1.type.Asn1Constructed;
import org.apache.kerby.asn1.type.Asn1Collection;
import org.apache.kerby.asn1.type.Asn1Simple;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.parse.Asn1ParseResult;

public final class Asn1Converter
{
    private Asn1Converter() {
    }
    
    public static Asn1Type convert(final Asn1ParseResult parseResult, final boolean isLazy) throws IOException {
        if (Asn1Simple.isSimple(parseResult.tag())) {
            return convertAsSimple(parseResult);
        }
        if (Asn1Collection.isCollection(parseResult.tag())) {
            return convertAsCollection(parseResult, isLazy);
        }
        if (!parseResult.tag().isPrimitive()) {
            final Asn1Encodeable tmpValue = new Asn1Constructed(parseResult.tag());
            tmpValue.decode(parseResult);
            return tmpValue;
        }
        if (parseResult.isTagSpecific()) {
            final Asn1Specific app = new Asn1Specific(parseResult.tag());
            app.decode(parseResult);
            return app;
        }
        throw new IOException("Unexpected item: " + parseResult.simpleInfo());
    }
    
    public static Asn1Type convertAsSimple(final Asn1ParseResult parseResult) throws IOException {
        final Asn1Encodeable value = Asn1Simple.createSimple(parseResult.tagNo());
        value.useDefinitiveLength(parseResult.isDefinitiveLength());
        Asn1Binder.bind(parseResult, value);
        return value;
    }
    
    public static Asn1Type convertAsCollection(final Asn1ParseResult parseResult, final boolean isLazy) throws IOException {
        final Asn1Collection value = Asn1Collection.createCollection(parseResult.tag());
        value.useDefinitiveLength(parseResult.isDefinitiveLength());
        value.setLazy(isLazy);
        Asn1Binder.bind(parseResult, value);
        return value;
    }
    
    public static Asn1Type convertAs(final Asn1ParseResult parseResult, final Class<? extends Asn1Type> type) throws IOException {
        Asn1Type value;
        try {
            value = (Asn1Type)type.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException("Invalid type: " + type.getCanonicalName(), e);
        }
        Asn1Binder.bind(parseResult, value);
        return value;
    }
}
