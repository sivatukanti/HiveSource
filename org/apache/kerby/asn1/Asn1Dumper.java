// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1;

import org.apache.kerby.asn1.parse.Asn1Item;
import org.apache.kerby.asn1.type.Asn1Specific;
import org.apache.kerby.asn1.type.Asn1Simple;
import org.apache.kerby.asn1.parse.Asn1ParseResult;
import org.apache.kerby.asn1.parse.Asn1Parser;
import org.apache.kerby.asn1.type.Asn1Type;
import java.io.IOException;
import java.nio.ByteBuffer;

public final class Asn1Dumper
{
    private StringBuilder builder;
    
    public Asn1Dumper() {
        this.builder = new StringBuilder();
    }
    
    public String output() {
        return this.builder.toString();
    }
    
    public void parseAndDump(final byte[] content) throws IOException {
        this.parseAndDump(ByteBuffer.wrap(content));
    }
    
    public void decodeAndDump(final byte[] content) throws IOException {
        this.decodeAndDump(ByteBuffer.wrap(content));
    }
    
    public void decodeAndDump(final ByteBuffer content) throws IOException {
        final Asn1Type value = Asn1.decode(content);
        this.dumpType(0, value);
    }
    
    public void parseAndDump(final ByteBuffer content) throws IOException {
        final Asn1ParseResult parseResult = Asn1Parser.parse(content);
        this.dumpParseResult(0, parseResult);
    }
    
    public void dumpType(final Asn1Type value) {
        this.dumpType(0, value);
    }
    
    public Asn1Dumper dumpType(final int indents, final Asn1Type value) {
        if (value == null) {
            this.indent(indents).append("Null");
        }
        else if (value instanceof Asn1Simple) {
            this.indent(indents).append(value.toString());
        }
        else if (value instanceof Asn1Dumpable) {
            final Asn1Dumpable dumpable = (Asn1Dumpable)value;
            dumpable.dumpWith(this, indents);
        }
        else if (value instanceof Asn1Specific) {
            this.indent(indents).append(value.toString());
        }
        else {
            this.indent(indents).append("<Unknown>");
        }
        return this;
    }
    
    public Asn1Dumper dumpParseResult(final int indents, final Asn1ParseResult value) {
        if (value == null) {
            this.indent(indents).append("Null");
        }
        else if (value instanceof Asn1Item) {
            this.indent(indents).append(value.toString());
        }
        else if (value instanceof Asn1Dumpable) {
            final Asn1Dumpable dumpable = (Asn1Dumpable)value;
            dumpable.dumpWith(this, indents);
        }
        else {
            this.indent(indents).append("<Unknown>");
        }
        return this;
    }
    
    public Asn1Dumper indent(final int numSpaces) {
        for (int i = 0; i < numSpaces; ++i) {
            this.builder.append(' ');
        }
        return this;
    }
    
    public Asn1Dumper append(final Asn1Simple<?> simpleValue) {
        if (simpleValue != null) {
            this.builder.append(simpleValue.toString());
        }
        else {
            this.builder.append("null");
        }
        return this;
    }
    
    public Asn1Dumper append(final String string) {
        this.builder.append(string);
        return this;
    }
    
    public Asn1Dumper appendType(final Class<?> cls) {
        this.builder.append("<").append(cls.getSimpleName()).append("> ");
        return this;
    }
    
    public Asn1Dumper newLine() {
        this.builder.append("\n");
        return this;
    }
    
    public Asn1Dumper dumpData(final String hexData) {
        int pos;
        int range;
        for (range = (pos = 100); pos < hexData.length(); pos += range) {
            System.out.println(hexData.substring(pos - range, pos));
        }
        System.out.println(hexData.substring(pos - range, hexData.length()));
        return this;
    }
}
