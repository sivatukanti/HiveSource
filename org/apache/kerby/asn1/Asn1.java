// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1;

import org.apache.kerby.asn1.util.HexUtil;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import org.apache.kerby.asn1.parse.Asn1Parser;
import java.io.IOException;
import org.apache.kerby.asn1.type.Asn1Type;
import java.nio.ByteBuffer;

public final class Asn1
{
    private Asn1() {
    }
    
    public static void encode(final ByteBuffer buffer, final Asn1Type value) throws IOException {
        value.encode(buffer);
    }
    
    public static byte[] encode(final Asn1Type value) throws IOException {
        return value.encode();
    }
    
    public static Asn1Type decode(final byte[] content) throws IOException {
        return decode(ByteBuffer.wrap(content));
    }
    
    public static Asn1Type decode(final ByteBuffer content) throws IOException {
        final Asn1ParseResult parseResult = Asn1Parser.parse(content);
        return Asn1Converter.convert(parseResult, false);
    }
    
    public static void decode(final byte[] content, final Asn1Type value) throws IOException {
        value.decode(content);
    }
    
    public static void decode(final ByteBuffer content, final Asn1Type value) throws IOException {
        value.decode(content);
    }
    
    public static Asn1ParseResult parse(final byte[] content) throws IOException {
        return parse(ByteBuffer.wrap(content));
    }
    
    public static Asn1ParseResult parse(final ByteBuffer content) throws IOException {
        return Asn1Parser.parse(content);
    }
    
    public static void dump(final Asn1Type value) {
        final Asn1Dumper dumper = new Asn1Dumper();
        dumper.dumpType(0, value);
        final String output = dumper.output();
        System.out.println(output);
    }
    
    public static void parseAndDump(final String hexStr) throws IOException {
        final byte[] data = HexUtil.hex2bytes(hexStr);
        parseAndDump(data);
    }
    
    public static void parseAndDump(final ByteBuffer content) throws IOException {
        final byte[] bytes = new byte[content.remaining()];
        content.get(bytes);
        parseAndDump(bytes);
    }
    
    public static void parseAndDump(final byte[] content) throws IOException {
        final Asn1Dumper dumper = new Asn1Dumper();
        dumper.parseAndDump(content);
        final String output = dumper.output();
        System.out.println(output);
    }
    
    public static void decodeAndDump(final String hexStr) throws IOException {
        final byte[] data = HexUtil.hex2bytes(hexStr);
        decodeAndDump(data);
    }
    
    public static void decodeAndDump(final ByteBuffer content) throws IOException {
        final byte[] bytes = new byte[content.remaining()];
        content.get(bytes);
        decodeAndDump(bytes);
    }
    
    public static void decodeAndDump(final byte[] content) throws IOException {
        final Asn1Dumper dumper = new Asn1Dumper();
        dumper.decodeAndDump(content);
        final String output = dumper.output();
        System.out.println(output);
    }
}
