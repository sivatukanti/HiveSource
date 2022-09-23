// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import org.apache.avro.AvroTypeException;
import java.nio.ByteBuffer;
import org.apache.avro.util.Utf8;
import org.apache.avro.io.parsing.Symbol;
import org.codehaus.jackson.util.MinimalPrettyPrinter;
import org.codehaus.jackson.PrettyPrinter;
import org.codehaus.jackson.util.DefaultPrettyPrinter;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.apache.avro.io.parsing.JsonGrammarGenerator;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.avro.Schema;
import java.util.BitSet;
import org.codehaus.jackson.JsonGenerator;
import org.apache.avro.io.parsing.Parser;

public class JsonEncoder extends ParsingEncoder implements Parser.ActionHandler
{
    private static final String LINE_SEPARATOR;
    final Parser parser;
    private JsonGenerator out;
    protected BitSet isEmpty;
    
    JsonEncoder(final Schema sc, final OutputStream out) throws IOException {
        this(sc, getJsonGenerator(out, false));
    }
    
    JsonEncoder(final Schema sc, final OutputStream out, final boolean pretty) throws IOException {
        this(sc, getJsonGenerator(out, pretty));
    }
    
    JsonEncoder(final Schema sc, final JsonGenerator out) throws IOException {
        this.isEmpty = new BitSet();
        this.configure(out);
        this.parser = new Parser(new JsonGrammarGenerator().generate(sc), this);
    }
    
    @Override
    public void flush() throws IOException {
        this.parser.processImplicitActions();
        if (this.out != null) {
            this.out.flush();
        }
    }
    
    private static JsonGenerator getJsonGenerator(final OutputStream out, final boolean pretty) throws IOException {
        if (null == out) {
            throw new NullPointerException("OutputStream cannot be null");
        }
        final JsonGenerator g = new JsonFactory().createJsonGenerator(out, JsonEncoding.UTF8);
        if (pretty) {
            final DefaultPrettyPrinter pp = new DefaultPrettyPrinter() {
                @Override
                public void writeRootValueSeparator(final JsonGenerator jg) throws IOException {
                    jg.writeRaw(JsonEncoder.LINE_SEPARATOR);
                }
            };
            g.setPrettyPrinter(pp);
        }
        else {
            final MinimalPrettyPrinter pp2 = new MinimalPrettyPrinter();
            pp2.setRootValueSeparator(JsonEncoder.LINE_SEPARATOR);
            g.setPrettyPrinter(pp2);
        }
        return g;
    }
    
    public JsonEncoder configure(final OutputStream out) throws IOException {
        this.configure(getJsonGenerator(out, false));
        return this;
    }
    
    public JsonEncoder configure(final JsonGenerator generator) throws IOException {
        if (null == generator) {
            throw new NullPointerException("JsonGenerator cannot be null");
        }
        if (null != this.parser) {
            this.flush();
        }
        this.out = generator;
        return this;
    }
    
    @Override
    public void writeNull() throws IOException {
        this.parser.advance(Symbol.NULL);
        this.out.writeNull();
    }
    
    @Override
    public void writeBoolean(final boolean b) throws IOException {
        this.parser.advance(Symbol.BOOLEAN);
        this.out.writeBoolean(b);
    }
    
    @Override
    public void writeInt(final int n) throws IOException {
        this.parser.advance(Symbol.INT);
        this.out.writeNumber(n);
    }
    
    @Override
    public void writeLong(final long n) throws IOException {
        this.parser.advance(Symbol.LONG);
        this.out.writeNumber(n);
    }
    
    @Override
    public void writeFloat(final float f) throws IOException {
        this.parser.advance(Symbol.FLOAT);
        this.out.writeNumber(f);
    }
    
    @Override
    public void writeDouble(final double d) throws IOException {
        this.parser.advance(Symbol.DOUBLE);
        this.out.writeNumber(d);
    }
    
    @Override
    public void writeString(final Utf8 utf8) throws IOException {
        this.writeString(utf8.toString());
    }
    
    @Override
    public void writeString(final String str) throws IOException {
        this.parser.advance(Symbol.STRING);
        if (this.parser.topSymbol() == Symbol.MAP_KEY_MARKER) {
            this.parser.advance(Symbol.MAP_KEY_MARKER);
            this.out.writeFieldName(str);
        }
        else {
            this.out.writeString(str);
        }
    }
    
    @Override
    public void writeBytes(final ByteBuffer bytes) throws IOException {
        if (bytes.hasArray()) {
            this.writeBytes(bytes.array(), bytes.position(), bytes.remaining());
        }
        else {
            final byte[] b = new byte[bytes.remaining()];
            bytes.duplicate().get(b);
            this.writeBytes(b);
        }
    }
    
    @Override
    public void writeBytes(final byte[] bytes, final int start, final int len) throws IOException {
        this.parser.advance(Symbol.BYTES);
        this.writeByteArray(bytes, start, len);
    }
    
    private void writeByteArray(final byte[] bytes, final int start, final int len) throws IOException {
        this.out.writeString(new String(bytes, start, len, "ISO-8859-1"));
    }
    
    @Override
    public void writeFixed(final byte[] bytes, final int start, final int len) throws IOException {
        this.parser.advance(Symbol.FIXED);
        final Symbol.IntCheckAction top = (Symbol.IntCheckAction)this.parser.popSymbol();
        if (len != top.size) {
            throw new AvroTypeException("Incorrect length for fixed binary: expected " + top.size + " but received " + len + " bytes.");
        }
        this.writeByteArray(bytes, start, len);
    }
    
    @Override
    public void writeEnum(final int e) throws IOException {
        this.parser.advance(Symbol.ENUM);
        final Symbol.EnumLabelsAction top = (Symbol.EnumLabelsAction)this.parser.popSymbol();
        if (e < 0 || e >= top.size) {
            throw new AvroTypeException("Enumeration out of range: max is " + top.size + " but received " + e);
        }
        this.out.writeString(top.getLabel(e));
    }
    
    @Override
    public void writeArrayStart() throws IOException {
        this.parser.advance(Symbol.ARRAY_START);
        this.out.writeStartArray();
        this.push();
        this.isEmpty.set(this.depth());
    }
    
    @Override
    public void writeArrayEnd() throws IOException {
        if (!this.isEmpty.get(this.pos)) {
            this.parser.advance(Symbol.ITEM_END);
        }
        this.pop();
        this.parser.advance(Symbol.ARRAY_END);
        this.out.writeEndArray();
    }
    
    @Override
    public void writeMapStart() throws IOException {
        this.push();
        this.isEmpty.set(this.depth());
        this.parser.advance(Symbol.MAP_START);
        this.out.writeStartObject();
    }
    
    @Override
    public void writeMapEnd() throws IOException {
        if (!this.isEmpty.get(this.pos)) {
            this.parser.advance(Symbol.ITEM_END);
        }
        this.pop();
        this.parser.advance(Symbol.MAP_END);
        this.out.writeEndObject();
    }
    
    @Override
    public void startItem() throws IOException {
        if (!this.isEmpty.get(this.pos)) {
            this.parser.advance(Symbol.ITEM_END);
        }
        super.startItem();
        this.isEmpty.clear(this.depth());
    }
    
    @Override
    public void writeIndex(final int unionIndex) throws IOException {
        this.parser.advance(Symbol.UNION);
        final Symbol.Alternative top = (Symbol.Alternative)this.parser.popSymbol();
        final Symbol symbol = top.getSymbol(unionIndex);
        if (symbol != Symbol.NULL) {
            this.out.writeStartObject();
            this.out.writeFieldName(top.getLabel(unionIndex));
            this.parser.pushSymbol(Symbol.UNION_END);
        }
        this.parser.pushSymbol(symbol);
    }
    
    @Override
    public Symbol doAction(final Symbol input, final Symbol top) throws IOException {
        if (top instanceof Symbol.FieldAdjustAction) {
            final Symbol.FieldAdjustAction fa = (Symbol.FieldAdjustAction)top;
            this.out.writeFieldName(fa.fname);
        }
        else if (top == Symbol.RECORD_START) {
            this.out.writeStartObject();
        }
        else if (top == Symbol.RECORD_END || top == Symbol.UNION_END) {
            this.out.writeEndObject();
        }
        else if (top != Symbol.FIELD_END) {
            throw new AvroTypeException("Unknown action symbol " + top);
        }
        return null;
    }
    
    static {
        LINE_SEPARATOR = System.getProperty("line.separator");
    }
}
