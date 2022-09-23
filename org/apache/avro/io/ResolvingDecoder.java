// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import org.apache.avro.AvroTypeException;
import java.nio.ByteBuffer;
import org.apache.avro.util.Utf8;
import org.apache.avro.io.parsing.ResolvingGrammarGenerator;
import org.apache.avro.io.parsing.Symbol;
import java.io.IOException;
import org.apache.avro.Schema;
import java.nio.charset.Charset;

public class ResolvingDecoder extends ValidatingDecoder
{
    private Decoder backup;
    private static final Charset UTF8;
    
    ResolvingDecoder(final Schema writer, final Schema reader, final Decoder in) throws IOException {
        this(resolve(writer, reader), in);
    }
    
    private ResolvingDecoder(final Object resolver, final Decoder in) throws IOException {
        super((Symbol)resolver, in);
    }
    
    public static Object resolve(final Schema writer, final Schema reader) throws IOException {
        if (null == writer) {
            throw new NullPointerException("writer cannot be null!");
        }
        if (null == reader) {
            throw new NullPointerException("reader cannot be null!");
        }
        return new ResolvingGrammarGenerator().generate(writer, reader);
    }
    
    public final Schema.Field[] readFieldOrder() throws IOException {
        return ((Symbol.FieldOrderAction)this.parser.advance(Symbol.FIELD_ACTION)).fields;
    }
    
    public final void drain() throws IOException {
        this.parser.processImplicitActions();
    }
    
    @Override
    public long readLong() throws IOException {
        final Symbol actual = this.parser.advance(Symbol.LONG);
        if (actual == Symbol.INT) {
            return this.in.readInt();
        }
        if (actual == Symbol.DOUBLE) {
            return (long)this.in.readDouble();
        }
        assert actual == Symbol.LONG;
        return this.in.readLong();
    }
    
    @Override
    public float readFloat() throws IOException {
        final Symbol actual = this.parser.advance(Symbol.FLOAT);
        if (actual == Symbol.INT) {
            return (float)this.in.readInt();
        }
        if (actual == Symbol.LONG) {
            return (float)this.in.readLong();
        }
        assert actual == Symbol.FLOAT;
        return this.in.readFloat();
    }
    
    @Override
    public double readDouble() throws IOException {
        final Symbol actual = this.parser.advance(Symbol.DOUBLE);
        if (actual == Symbol.INT) {
            return this.in.readInt();
        }
        if (actual == Symbol.LONG) {
            return (double)this.in.readLong();
        }
        if (actual == Symbol.FLOAT) {
            return this.in.readFloat();
        }
        assert actual == Symbol.DOUBLE;
        return this.in.readDouble();
    }
    
    @Override
    public Utf8 readString(final Utf8 old) throws IOException {
        final Symbol actual = this.parser.advance(Symbol.STRING);
        if (actual == Symbol.BYTES) {
            return new Utf8(this.in.readBytes(null).array());
        }
        assert actual == Symbol.STRING;
        return this.in.readString(old);
    }
    
    @Override
    public String readString() throws IOException {
        final Symbol actual = this.parser.advance(Symbol.STRING);
        if (actual == Symbol.BYTES) {
            return new String(this.in.readBytes(null).array(), ResolvingDecoder.UTF8);
        }
        assert actual == Symbol.STRING;
        return this.in.readString();
    }
    
    @Override
    public void skipString() throws IOException {
        final Symbol actual = this.parser.advance(Symbol.STRING);
        if (actual == Symbol.BYTES) {
            this.in.skipBytes();
        }
        else {
            assert actual == Symbol.STRING;
            this.in.skipString();
        }
    }
    
    @Override
    public ByteBuffer readBytes(final ByteBuffer old) throws IOException {
        final Symbol actual = this.parser.advance(Symbol.BYTES);
        if (actual == Symbol.STRING) {
            final Utf8 s = this.in.readString(null);
            return ByteBuffer.wrap(s.getBytes(), 0, s.getByteLength());
        }
        assert actual == Symbol.BYTES;
        return this.in.readBytes(old);
    }
    
    @Override
    public void skipBytes() throws IOException {
        final Symbol actual = this.parser.advance(Symbol.BYTES);
        if (actual == Symbol.STRING) {
            this.in.skipString();
        }
        else {
            assert actual == Symbol.BYTES;
            this.in.skipBytes();
        }
    }
    
    @Override
    public int readEnum() throws IOException {
        this.parser.advance(Symbol.ENUM);
        final Symbol.EnumAdjustAction top = (Symbol.EnumAdjustAction)this.parser.popSymbol();
        final int n = this.in.readEnum();
        final Object o = top.adjustments[n];
        if (o instanceof Integer) {
            return (int)o;
        }
        throw new AvroTypeException((String)o);
    }
    
    @Override
    public int readIndex() throws IOException {
        this.parser.advance(Symbol.UNION);
        final Symbol.UnionAdjustAction top = (Symbol.UnionAdjustAction)this.parser.popSymbol();
        this.parser.pushSymbol(top.symToParse);
        return top.rindex;
    }
    
    @Override
    public Symbol doAction(final Symbol input, final Symbol top) throws IOException {
        if (top instanceof Symbol.FieldOrderAction) {
            return (input == Symbol.FIELD_ACTION) ? top : null;
        }
        if (!(top instanceof Symbol.ResolvingAction)) {
            if (top instanceof Symbol.SkipAction) {
                final Symbol symToSkip = ((Symbol.SkipAction)top).symToSkip;
                this.parser.skipSymbol(symToSkip);
            }
            else if (top instanceof Symbol.WriterUnionAction) {
                final Symbol.Alternative branches = (Symbol.Alternative)this.parser.popSymbol();
                this.parser.pushSymbol(branches.getSymbol(this.in.readIndex()));
            }
            else {
                if (top instanceof Symbol.ErrorAction) {
                    throw new AvroTypeException(((Symbol.ErrorAction)top).msg);
                }
                if (top instanceof Symbol.DefaultStartAction) {
                    final Symbol.DefaultStartAction dsa = (Symbol.DefaultStartAction)top;
                    this.backup = this.in;
                    this.in = DecoderFactory.get().binaryDecoder(dsa.contents, null);
                }
                else {
                    if (top != Symbol.DEFAULT_END_ACTION) {
                        throw new AvroTypeException("Unknown action: " + top);
                    }
                    this.in = this.backup;
                }
            }
            return null;
        }
        final Symbol.ResolvingAction t = (Symbol.ResolvingAction)top;
        if (t.reader != input) {
            throw new AvroTypeException("Found " + t.reader + " while looking for " + input);
        }
        return t.writer;
    }
    
    @Override
    public void skipAction() throws IOException {
        final Symbol top = this.parser.popSymbol();
        if (top instanceof Symbol.ResolvingAction) {
            this.parser.pushSymbol(((Symbol.ResolvingAction)top).writer);
        }
        else if (top instanceof Symbol.SkipAction) {
            this.parser.pushSymbol(((Symbol.SkipAction)top).symToSkip);
        }
        else if (top instanceof Symbol.WriterUnionAction) {
            final Symbol.Alternative branches = (Symbol.Alternative)this.parser.popSymbol();
            this.parser.pushSymbol(branches.getSymbol(this.in.readIndex()));
        }
        else {
            if (top instanceof Symbol.ErrorAction) {
                throw new AvroTypeException(((Symbol.ErrorAction)top).msg);
            }
            if (top instanceof Symbol.DefaultStartAction) {
                final Symbol.DefaultStartAction dsa = (Symbol.DefaultStartAction)top;
                this.backup = this.in;
                this.in = DecoderFactory.get().binaryDecoder(dsa.contents, null);
            }
            else if (top == Symbol.DEFAULT_END_ACTION) {
                this.in = this.backup;
            }
        }
    }
    
    static {
        UTF8 = Charset.forName("UTF-8");
    }
}
