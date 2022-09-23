// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io.parsing;

import java.util.Arrays;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.BinaryEncoder;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.codehaus.jackson.JsonNode;
import java.util.Iterator;
import java.util.List;
import org.apache.avro.AvroTypeException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import org.apache.avro.Schema;
import org.apache.avro.io.EncoderFactory;

public class ResolvingGrammarGenerator extends ValidatingGrammarGenerator
{
    private static EncoderFactory factory;
    
    public final Symbol generate(final Schema writer, final Schema reader) throws IOException {
        return Symbol.root(this.generate(writer, reader, new HashMap<LitS, Symbol>()));
    }
    
    public Symbol generate(final Schema writer, final Schema reader, final Map<LitS, Symbol> seen) throws IOException {
        final Schema.Type writerType = writer.getType();
        final Schema.Type readerType = reader.getType();
        Label_0785: {
            if (writerType == readerType) {
                switch (writerType) {
                    case NULL: {
                        return Symbol.NULL;
                    }
                    case BOOLEAN: {
                        return Symbol.BOOLEAN;
                    }
                    case INT: {
                        return Symbol.INT;
                    }
                    case LONG: {
                        return Symbol.LONG;
                    }
                    case FLOAT: {
                        return Symbol.FLOAT;
                    }
                    case DOUBLE: {
                        return Symbol.DOUBLE;
                    }
                    case STRING: {
                        return Symbol.STRING;
                    }
                    case BYTES: {
                        return Symbol.BYTES;
                    }
                    case FIXED: {
                        if (writer.getFullName().equals(reader.getFullName()) && writer.getFixedSize() == reader.getFixedSize()) {
                            return Symbol.seq(Symbol.intCheckAction(writer.getFixedSize()), Symbol.FIXED);
                        }
                        break;
                    }
                    case ENUM: {
                        if (writer.getFullName() == null || writer.getFullName().equals(reader.getFullName())) {
                            return Symbol.seq(mkEnumAdjust(writer.getEnumSymbols(), reader.getEnumSymbols()), Symbol.ENUM);
                        }
                        break;
                    }
                    case ARRAY: {
                        return Symbol.seq(Symbol.repeat(Symbol.ARRAY_END, this.generate(writer.getElementType(), reader.getElementType(), seen)), Symbol.ARRAY_START);
                    }
                    case MAP: {
                        return Symbol.seq(Symbol.repeat(Symbol.MAP_END, this.generate(writer.getValueType(), reader.getValueType(), seen), Symbol.STRING), Symbol.MAP_START);
                    }
                    case RECORD: {
                        return this.resolveRecords(writer, reader, seen);
                    }
                    case UNION: {
                        return this.resolveUnion(writer, reader, seen);
                    }
                    default: {
                        throw new AvroTypeException("Unkown type for schema: " + writerType);
                    }
                }
            }
            else {
                if (writerType == Schema.Type.UNION) {
                    return this.resolveUnion(writer, reader, seen);
                }
                switch (readerType) {
                    case LONG: {
                        switch (writerType) {
                            case INT: {
                                return Symbol.resolve(super.generate(writer, seen), Symbol.LONG);
                            }
                            default: {
                                break Label_0785;
                            }
                        }
                        break;
                    }
                    case FLOAT: {
                        switch (writerType) {
                            case INT:
                            case LONG: {
                                return Symbol.resolve(super.generate(writer, seen), Symbol.FLOAT);
                            }
                            default: {
                                break Label_0785;
                            }
                        }
                        break;
                    }
                    case DOUBLE: {
                        switch (writerType) {
                            case INT:
                            case LONG:
                            case FLOAT: {
                                return Symbol.resolve(super.generate(writer, seen), Symbol.DOUBLE);
                            }
                            default: {
                                break Label_0785;
                            }
                        }
                        break;
                    }
                    case BYTES: {
                        switch (writerType) {
                            case STRING: {
                                return Symbol.resolve(super.generate(writer, seen), Symbol.BYTES);
                            }
                            default: {
                                break Label_0785;
                            }
                        }
                        break;
                    }
                    case STRING: {
                        switch (writerType) {
                            case BYTES: {
                                return Symbol.resolve(super.generate(writer, seen), Symbol.STRING);
                            }
                            default: {
                                break Label_0785;
                            }
                        }
                        break;
                    }
                    case UNION: {
                        final int j = bestBranch(reader, writer);
                        if (j >= 0) {
                            final Symbol s = this.generate(writer, reader.getTypes().get(j), seen);
                            return Symbol.seq(Symbol.unionAdjustAction(j, s), Symbol.UNION);
                        }
                        break;
                    }
                    case NULL:
                    case BOOLEAN:
                    case INT:
                    case FIXED:
                    case ENUM:
                    case ARRAY:
                    case MAP:
                    case RECORD: {
                        break;
                    }
                    default: {
                        throw new RuntimeException("Unexpected schema type: " + readerType);
                    }
                }
            }
        }
        return Symbol.error("Found " + writer.getFullName() + ", expecting " + reader.getFullName());
    }
    
    private Symbol resolveUnion(final Schema writer, final Schema reader, final Map<LitS, Symbol> seen) throws IOException {
        final List<Schema> alts = writer.getTypes();
        final int size = alts.size();
        final Symbol[] symbols = new Symbol[size];
        final String[] labels = new String[size];
        int i = 0;
        for (final Schema w : alts) {
            symbols[i] = this.generate(w, reader, seen);
            labels[i] = w.getFullName();
            ++i;
        }
        return Symbol.seq(Symbol.alt(symbols, labels), Symbol.writerUnionAction());
    }
    
    private Symbol resolveRecords(final Schema writer, final Schema reader, final Map<LitS, Symbol> seen) throws IOException {
        final LitS wsc = new LitS2(writer, reader);
        Symbol result = seen.get(wsc);
        if (result == null) {
            final List<Schema.Field> wfields = writer.getFields();
            final List<Schema.Field> rfields = reader.getFields();
            final Schema.Field[] reordered = new Schema.Field[rfields.size()];
            int ridx = 0;
            int count = 1 + wfields.size();
            for (final Schema.Field f : wfields) {
                final Schema.Field rdrField = reader.getField(f.name());
                if (rdrField != null) {
                    reordered[ridx++] = rdrField;
                }
            }
            for (final Schema.Field rf : rfields) {
                final String fname = rf.name();
                if (writer.getField(fname) == null) {
                    if (rf.defaultValue() == null) {
                        result = Symbol.error("Found " + writer.getFullName() + ", expecting " + reader.getFullName() + ", missing required field " + fname);
                        seen.put(wsc, result);
                        return result;
                    }
                    reordered[ridx++] = rf;
                    count += 3;
                }
            }
            final Symbol[] production = new Symbol[count];
            production[--count] = Symbol.fieldOrderAction(reordered);
            result = Symbol.seq(production);
            seen.put(wsc, result);
            for (final Schema.Field wf : wfields) {
                final String fname2 = wf.name();
                final Schema.Field rf2 = reader.getField(fname2);
                if (rf2 == null) {
                    production[--count] = Symbol.skipAction(this.generate(wf.schema(), wf.schema(), seen));
                }
                else {
                    production[--count] = this.generate(wf.schema(), rf2.schema(), seen);
                }
            }
            for (final Schema.Field rf3 : rfields) {
                final String fname2 = rf3.name();
                final Schema.Field wf2 = writer.getField(fname2);
                if (wf2 == null) {
                    final byte[] bb = getBinary(rf3.schema(), rf3.defaultValue());
                    production[--count] = Symbol.defaultStartAction(bb);
                    production[--count] = this.generate(rf3.schema(), rf3.schema(), seen);
                    production[--count] = Symbol.DEFAULT_END_ACTION;
                }
            }
        }
        return result;
    }
    
    private static byte[] getBinary(final Schema s, final JsonNode n) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final Encoder e = ResolvingGrammarGenerator.factory.binaryEncoder(out, null);
        encode(e, s, n);
        e.flush();
        return out.toByteArray();
    }
    
    public static void encode(final Encoder e, final Schema s, final JsonNode n) throws IOException {
        switch (s.getType()) {
            case RECORD: {
                for (final Schema.Field f : s.getFields()) {
                    final String name = f.name();
                    JsonNode v = n.get(name);
                    if (v == null) {
                        v = f.defaultValue();
                    }
                    if (v == null) {
                        throw new AvroTypeException("No default value for: " + name);
                    }
                    encode(e, f.schema(), v);
                }
                break;
            }
            case ENUM: {
                e.writeEnum(s.getEnumOrdinal(n.getTextValue()));
                break;
            }
            case ARRAY: {
                e.writeArrayStart();
                e.setItemCount(n.size());
                final Schema i = s.getElementType();
                for (final JsonNode node : n) {
                    e.startItem();
                    encode(e, i, node);
                }
                e.writeArrayEnd();
                break;
            }
            case MAP: {
                e.writeMapStart();
                e.setItemCount(n.size());
                final Schema v2 = s.getValueType();
                final Iterator<String> it = n.getFieldNames();
                while (it.hasNext()) {
                    e.startItem();
                    final String key = it.next();
                    e.writeString(key);
                    encode(e, v2, n.get(key));
                }
                e.writeMapEnd();
                break;
            }
            case UNION: {
                e.writeIndex(0);
                encode(e, s.getTypes().get(0), n);
                break;
            }
            case FIXED: {
                if (!n.isTextual()) {
                    throw new AvroTypeException("Non-string default value for fixed: " + n);
                }
                byte[] bb = n.getTextValue().getBytes("ISO-8859-1");
                if (bb.length != s.getFixedSize()) {
                    bb = Arrays.copyOf(bb, s.getFixedSize());
                }
                e.writeFixed(bb);
                break;
            }
            case STRING: {
                if (!n.isTextual()) {
                    throw new AvroTypeException("Non-string default value for string: " + n);
                }
                e.writeString(n.getTextValue());
                break;
            }
            case BYTES: {
                if (!n.isTextual()) {
                    throw new AvroTypeException("Non-string default value for bytes: " + n);
                }
                e.writeBytes(n.getTextValue().getBytes("ISO-8859-1"));
                break;
            }
            case INT: {
                if (!n.isNumber()) {
                    throw new AvroTypeException("Non-numeric default value for int: " + n);
                }
                e.writeInt(n.getIntValue());
                break;
            }
            case LONG: {
                if (!n.isNumber()) {
                    throw new AvroTypeException("Non-numeric default value for long: " + n);
                }
                e.writeLong(n.getLongValue());
                break;
            }
            case FLOAT: {
                if (!n.isNumber()) {
                    throw new AvroTypeException("Non-numeric default value for float: " + n);
                }
                e.writeFloat((float)n.getDoubleValue());
                break;
            }
            case DOUBLE: {
                if (!n.isNumber()) {
                    throw new AvroTypeException("Non-numeric default value for double: " + n);
                }
                e.writeDouble(n.getDoubleValue());
                break;
            }
            case BOOLEAN: {
                if (!n.isBoolean()) {
                    throw new AvroTypeException("Non-boolean default for boolean: " + n);
                }
                e.writeBoolean(n.getBooleanValue());
                break;
            }
            case NULL: {
                if (!n.isNull()) {
                    throw new AvroTypeException("Non-null default value for null type: " + n);
                }
                e.writeNull();
                break;
            }
        }
    }
    
    private static Symbol mkEnumAdjust(final List<String> wsymbols, final List<String> rsymbols) {
        final Object[] adjustments = new Object[wsymbols.size()];
        for (int i = 0; i < adjustments.length; ++i) {
            final int j = rsymbols.indexOf(wsymbols.get(i));
            adjustments[i] = ((j == -1) ? ("No match for " + wsymbols.get(i)) : new Integer(j));
        }
        return Symbol.enumAdjustAction(rsymbols.size(), adjustments);
    }
    
    private static int bestBranch(final Schema r, final Schema w) {
        final Schema.Type vt = w.getType();
        int j = 0;
        for (final Schema b : r.getTypes()) {
            if (vt == b.getType()) {
                if (vt != Schema.Type.RECORD && vt != Schema.Type.ENUM && vt != Schema.Type.FIXED) {
                    return j;
                }
                final String vname = w.getFullName();
                final String bname = b.getFullName();
                if ((vname != null && vname.equals(bname)) || (vname == bname && vt == Schema.Type.RECORD)) {
                    return j;
                }
            }
            ++j;
        }
        j = 0;
        for (final Schema b : r.getTypes()) {
            Label_0358: {
                switch (vt) {
                    case INT: {
                        switch (b.getType()) {
                            case LONG:
                            case DOUBLE: {
                                return j;
                            }
                            default: {
                                break Label_0358;
                            }
                        }
                        break;
                    }
                    case LONG:
                    case FLOAT: {
                        switch (b.getType()) {
                            case DOUBLE: {
                                return j;
                            }
                            default: {
                                break Label_0358;
                            }
                        }
                        break;
                    }
                    case STRING: {
                        switch (b.getType()) {
                            case BYTES: {
                                return j;
                            }
                            default: {
                                break Label_0358;
                            }
                        }
                        break;
                    }
                    case BYTES: {
                        switch (b.getType()) {
                            case STRING: {
                                return j;
                            }
                            default: {
                                break Label_0358;
                            }
                        }
                        break;
                    }
                }
            }
            ++j;
        }
        return -1;
    }
    
    static {
        ResolvingGrammarGenerator.factory = new EncoderFactory().configureBufferSize(32);
    }
    
    static class LitS2 extends LitS
    {
        public Schema expected;
        
        public LitS2(final Schema actual, final Schema expected) {
            super(actual);
            this.expected = expected;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof LitS2)) {
                return false;
            }
            final LitS2 other = (LitS2)o;
            return this.actual == other.actual && this.expected == other.expected;
        }
        
        @Override
        public int hashCode() {
            return super.hashCode() + this.expected.hashCode();
        }
    }
}
