// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import org.apache.avro.AvroTypeException;
import java.nio.ByteBuffer;
import org.apache.avro.util.Utf8;
import org.apache.avro.io.parsing.ValidatingGrammarGenerator;
import org.apache.avro.Schema;
import java.io.IOException;
import org.apache.avro.io.parsing.Symbol;
import org.apache.avro.io.parsing.Parser;

public class ValidatingEncoder extends ParsingEncoder implements Parser.ActionHandler
{
    protected Encoder out;
    protected final Parser parser;
    
    ValidatingEncoder(final Symbol root, final Encoder out) throws IOException {
        this.out = out;
        this.parser = new Parser(root, this);
    }
    
    ValidatingEncoder(final Schema schema, final Encoder in) throws IOException {
        this(new ValidatingGrammarGenerator().generate(schema), in);
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    public ValidatingEncoder configure(final Encoder encoder) {
        this.parser.reset();
        this.out = encoder;
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
        this.out.writeInt(n);
    }
    
    @Override
    public void writeLong(final long n) throws IOException {
        this.parser.advance(Symbol.LONG);
        this.out.writeLong(n);
    }
    
    @Override
    public void writeFloat(final float f) throws IOException {
        this.parser.advance(Symbol.FLOAT);
        this.out.writeFloat(f);
    }
    
    @Override
    public void writeDouble(final double d) throws IOException {
        this.parser.advance(Symbol.DOUBLE);
        this.out.writeDouble(d);
    }
    
    @Override
    public void writeString(final Utf8 utf8) throws IOException {
        this.parser.advance(Symbol.STRING);
        this.out.writeString(utf8);
    }
    
    @Override
    public void writeString(final String str) throws IOException {
        this.parser.advance(Symbol.STRING);
        this.out.writeString(str);
    }
    
    @Override
    public void writeString(final CharSequence charSequence) throws IOException {
        this.parser.advance(Symbol.STRING);
        this.out.writeString(charSequence);
    }
    
    @Override
    public void writeBytes(final ByteBuffer bytes) throws IOException {
        this.parser.advance(Symbol.BYTES);
        this.out.writeBytes(bytes);
    }
    
    @Override
    public void writeBytes(final byte[] bytes, final int start, final int len) throws IOException {
        this.parser.advance(Symbol.BYTES);
        this.out.writeBytes(bytes, start, len);
    }
    
    @Override
    public void writeFixed(final byte[] bytes, final int start, final int len) throws IOException {
        this.parser.advance(Symbol.FIXED);
        final Symbol.IntCheckAction top = (Symbol.IntCheckAction)this.parser.popSymbol();
        if (len != top.size) {
            throw new AvroTypeException("Incorrect length for fixed binary: expected " + top.size + " but received " + len + " bytes.");
        }
        this.out.writeFixed(bytes, start, len);
    }
    
    @Override
    public void writeEnum(final int e) throws IOException {
        this.parser.advance(Symbol.ENUM);
        final Symbol.IntCheckAction top = (Symbol.IntCheckAction)this.parser.popSymbol();
        if (e < 0 || e >= top.size) {
            throw new AvroTypeException("Enumeration out of range: max is " + top.size + " but received " + e);
        }
        this.out.writeEnum(e);
    }
    
    @Override
    public void writeArrayStart() throws IOException {
        this.push();
        this.parser.advance(Symbol.ARRAY_START);
        this.out.writeArrayStart();
    }
    
    @Override
    public void writeArrayEnd() throws IOException {
        this.parser.advance(Symbol.ARRAY_END);
        this.out.writeArrayEnd();
        this.pop();
    }
    
    @Override
    public void writeMapStart() throws IOException {
        this.push();
        this.parser.advance(Symbol.MAP_START);
        this.out.writeMapStart();
    }
    
    @Override
    public void writeMapEnd() throws IOException {
        this.parser.advance(Symbol.MAP_END);
        this.out.writeMapEnd();
        this.pop();
    }
    
    @Override
    public void setItemCount(final long itemCount) throws IOException {
        super.setItemCount(itemCount);
        this.out.setItemCount(itemCount);
    }
    
    @Override
    public void startItem() throws IOException {
        super.startItem();
        this.out.startItem();
    }
    
    @Override
    public void writeIndex(final int unionIndex) throws IOException {
        this.parser.advance(Symbol.UNION);
        final Symbol.Alternative top = (Symbol.Alternative)this.parser.popSymbol();
        this.parser.pushSymbol(top.getSymbol(unionIndex));
        this.out.writeIndex(unionIndex);
    }
    
    @Override
    public Symbol doAction(final Symbol input, final Symbol top) throws IOException {
        return null;
    }
}
