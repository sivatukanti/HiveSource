// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.parse;

import org.apache.kerby.asn1.Tag;
import java.nio.ByteBuffer;
import java.io.IOException;

public class Asn1Parser
{
    public static void parse(final Asn1Container container) throws IOException {
        final Asn1Reader reader = new Asn1Reader(container.getBuffer());
        int pos = container.getBodyStart();
        do {
            reader.setPosition(pos);
            final Asn1ParseResult asn1Obj = parse(reader);
            if (asn1Obj == null) {
                break;
            }
            container.addItem(asn1Obj);
            pos += asn1Obj.getEncodingLength();
            if (asn1Obj.isEOC()) {
                break;
            }
        } while (!container.checkBodyFinished(pos));
        container.setBodyEnd(pos);
    }
    
    public static Asn1ParseResult parse(final ByteBuffer content) throws IOException {
        final Asn1Reader reader = new Asn1Reader(content);
        return parse(reader);
    }
    
    public static Asn1ParseResult parse(final Asn1Reader reader) throws IOException {
        if (!reader.available()) {
            return null;
        }
        final Asn1Header header = reader.readHeader();
        final Tag tmpTag = header.getTag();
        final int bodyStart = reader.getPosition();
        Asn1ParseResult parseResult;
        if (tmpTag.isPrimitive()) {
            parseResult = new Asn1Item(header, bodyStart, reader.getBuffer());
        }
        else {
            final Asn1Container container = new Asn1Container(header, bodyStart, reader.getBuffer());
            if (header.getLength() != 0) {
                parse(container);
            }
            parseResult = container;
        }
        return parseResult;
    }
}
