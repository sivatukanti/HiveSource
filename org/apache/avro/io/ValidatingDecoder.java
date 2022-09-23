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

public class ValidatingDecoder extends ParsingDecoder implements Parser.ActionHandler
{
    protected Decoder in;
    
    ValidatingDecoder(final Symbol root, final Decoder in) throws IOException {
        super(root);
        this.configure(in);
    }
    
    ValidatingDecoder(final Schema schema, final Decoder in) throws IOException {
        this(getSymbol(schema), in);
    }
    
    private static Symbol getSymbol(final Schema schema) {
        if (null == schema) {
            throw new NullPointerException("Schema cannot be null");
        }
        return new ValidatingGrammarGenerator().generate(schema);
    }
    
    public ValidatingDecoder configure(final Decoder in) throws IOException {
        this.parser.reset();
        this.in = in;
        return this;
    }
    
    @Override
    public void readNull() throws IOException {
        this.parser.advance(Symbol.NULL);
        this.in.readNull();
    }
    
    @Override
    public boolean readBoolean() throws IOException {
        this.parser.advance(Symbol.BOOLEAN);
        return this.in.readBoolean();
    }
    
    @Override
    public int readInt() throws IOException {
        this.parser.advance(Symbol.INT);
        return this.in.readInt();
    }
    
    @Override
    public long readLong() throws IOException {
        this.parser.advance(Symbol.LONG);
        return this.in.readLong();
    }
    
    @Override
    public float readFloat() throws IOException {
        this.parser.advance(Symbol.FLOAT);
        return this.in.readFloat();
    }
    
    @Override
    public double readDouble() throws IOException {
        this.parser.advance(Symbol.DOUBLE);
        return this.in.readDouble();
    }
    
    @Override
    public Utf8 readString(final Utf8 old) throws IOException {
        this.parser.advance(Symbol.STRING);
        return this.in.readString(old);
    }
    
    @Override
    public String readString() throws IOException {
        this.parser.advance(Symbol.STRING);
        return this.in.readString();
    }
    
    @Override
    public void skipString() throws IOException {
        this.parser.advance(Symbol.STRING);
        this.in.skipString();
    }
    
    @Override
    public ByteBuffer readBytes(final ByteBuffer old) throws IOException {
        this.parser.advance(Symbol.BYTES);
        return this.in.readBytes(old);
    }
    
    @Override
    public void skipBytes() throws IOException {
        this.parser.advance(Symbol.BYTES);
        this.in.skipBytes();
    }
    
    private void checkFixed(final int size) throws IOException {
        this.parser.advance(Symbol.FIXED);
        final Symbol.IntCheckAction top = (Symbol.IntCheckAction)this.parser.popSymbol();
        if (size != top.size) {
            throw new AvroTypeException("Incorrect length for fixed binary: expected " + top.size + " but received " + size + " bytes.");
        }
    }
    
    @Override
    public void readFixed(final byte[] bytes, final int start, final int len) throws IOException {
        this.checkFixed(len);
        this.in.readFixed(bytes, start, len);
    }
    
    @Override
    public void skipFixed(final int length) throws IOException {
        this.checkFixed(length);
        this.in.skipFixed(length);
    }
    
    @Override
    protected void skipFixed() throws IOException {
        this.parser.advance(Symbol.FIXED);
        final Symbol.IntCheckAction top = (Symbol.IntCheckAction)this.parser.popSymbol();
        this.in.skipFixed(top.size);
    }
    
    @Override
    public int readEnum() throws IOException {
        this.parser.advance(Symbol.ENUM);
        final Symbol.IntCheckAction top = (Symbol.IntCheckAction)this.parser.popSymbol();
        final int result = this.in.readEnum();
        if (result < 0 || result >= top.size) {
            throw new AvroTypeException("Enumeration out of range: max is " + top.size + " but received " + result);
        }
        return result;
    }
    
    @Override
    public long readArrayStart() throws IOException {
        this.parser.advance(Symbol.ARRAY_START);
        final long result = this.in.readArrayStart();
        if (result == 0L) {
            this.parser.advance(Symbol.ARRAY_END);
        }
        return result;
    }
    
    @Override
    public long arrayNext() throws IOException {
        this.parser.processTrailingImplicitActions();
        final long result = this.in.arrayNext();
        if (result == 0L) {
            this.parser.advance(Symbol.ARRAY_END);
        }
        return result;
    }
    
    @Override
    public long skipArray() throws IOException {
        this.parser.advance(Symbol.ARRAY_START);
        for (long c = this.in.skipArray(); c != 0L; c = this.in.skipArray()) {
            while (c-- > 0L) {
                this.parser.skipRepeater();
            }
        }
        this.parser.advance(Symbol.ARRAY_END);
        return 0L;
    }
    
    @Override
    public long readMapStart() throws IOException {
        this.parser.advance(Symbol.MAP_START);
        final long result = this.in.readMapStart();
        if (result == 0L) {
            this.parser.advance(Symbol.MAP_END);
        }
        return result;
    }
    
    @Override
    public long mapNext() throws IOException {
        this.parser.processTrailingImplicitActions();
        final long result = this.in.mapNext();
        if (result == 0L) {
            this.parser.advance(Symbol.MAP_END);
        }
        return result;
    }
    
    @Override
    public long skipMap() throws IOException {
        this.parser.advance(Symbol.MAP_START);
        for (long c = this.in.skipMap(); c != 0L; c = this.in.skipMap()) {
            while (c-- > 0L) {
                this.parser.skipRepeater();
            }
        }
        this.parser.advance(Symbol.MAP_END);
        return 0L;
    }
    
    @Override
    public int readIndex() throws IOException {
        this.parser.advance(Symbol.UNION);
        final Symbol.Alternative top = (Symbol.Alternative)this.parser.popSymbol();
        final int result = this.in.readIndex();
        this.parser.pushSymbol(top.getSymbol(result));
        return result;
    }
    
    @Override
    public Symbol doAction(final Symbol input, final Symbol top) throws IOException {
        return null;
    }
}
