// 
// Decompiled by Procyon v0.5.36
// 

package parquet.schema;

import java.util.StringTokenizer;
import java.util.Arrays;
import parquet.Log;

public class MessageTypeParser
{
    private static final Log LOG;
    
    private MessageTypeParser() {
    }
    
    public static MessageType parseMessageType(final String input) {
        return parse(input);
    }
    
    private static MessageType parse(final String schemaString) {
        final Tokenizer st = new Tokenizer(schemaString, " ;{}()\n\t");
        final Types.MessageTypeBuilder builder = Types.buildMessage();
        final String t = st.nextToken();
        check(t, "message", "start with 'message'", st);
        final String name = st.nextToken();
        addGroupTypeFields(st.nextToken(), st, builder);
        return builder.named(name);
    }
    
    private static void addGroupTypeFields(String t, final Tokenizer st, final Types.GroupBuilder builder) {
        check(t, "{", "start of message", st);
        while (!(t = st.nextToken()).equals("}")) {
            addType(t, st, builder);
        }
    }
    
    private static void addType(final String t, final Tokenizer st, final Types.GroupBuilder builder) {
        final Type.Repetition repetition = asRepetition(t, st);
        final String type = st.nextToken();
        if ("group".equalsIgnoreCase(type)) {
            addGroupType(t, st, repetition, builder);
        }
        else {
            addPrimitiveType(t, st, asPrimitive(type, st), repetition, builder);
        }
    }
    
    private static void addGroupType(String t, final Tokenizer st, final Type.Repetition r, final Types.GroupBuilder<?> builder) {
        final Types.GroupBuilder<?> childBuilder = builder.group(r);
        final String name = st.nextToken();
        t = st.nextToken();
        OriginalType originalType = null;
        if (t.equalsIgnoreCase("(")) {
            originalType = OriginalType.valueOf(st.nextToken());
            childBuilder.as(originalType);
            check(st.nextToken(), ")", "original type ended by )", st);
            t = st.nextToken();
        }
        if (t.equals("=")) {
            childBuilder.id(Integer.parseInt(st.nextToken()));
            t = st.nextToken();
        }
        try {
            addGroupTypeFields(t, st, childBuilder);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("problem reading type: type = group, name = " + name + ", original type = " + originalType, e);
        }
        childBuilder.named(name);
    }
    
    private static void addPrimitiveType(String t, final Tokenizer st, final PrimitiveType.PrimitiveTypeName type, final Type.Repetition r, final Types.GroupBuilder<?> builder) {
        final Types.PrimitiveBuilder<?> childBuilder = builder.primitive(type, r);
        if (type == PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY) {
            t = st.nextToken();
            if (!t.equalsIgnoreCase("(")) {
                throw new IllegalArgumentException("expecting (length) for field of type fixed_len_byte_array");
            }
            childBuilder.length(Integer.parseInt(st.nextToken()));
            check(st.nextToken(), ")", "type length ended by )", st);
        }
        final String name = st.nextToken();
        t = st.nextToken();
        OriginalType originalType = null;
        if (t.equalsIgnoreCase("(")) {
            originalType = OriginalType.valueOf(st.nextToken());
            childBuilder.as(originalType);
            if (OriginalType.DECIMAL == originalType) {
                t = st.nextToken();
                if (t.equalsIgnoreCase("(")) {
                    childBuilder.precision(Integer.parseInt(st.nextToken()));
                    t = st.nextToken();
                    if (t.equalsIgnoreCase(",")) {
                        childBuilder.scale(Integer.parseInt(st.nextToken()));
                        t = st.nextToken();
                    }
                    check(t, ")", "decimal type ended by )", st);
                    t = st.nextToken();
                }
            }
            else {
                t = st.nextToken();
            }
            check(t, ")", "original type ended by )", st);
            t = st.nextToken();
        }
        if (t.equals("=")) {
            childBuilder.id(Integer.parseInt(st.nextToken()));
            t = st.nextToken();
        }
        check(t, ";", "field ended by ';'", st);
        try {
            childBuilder.named(name);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("problem reading type: type = " + type + ", name = " + name + ", original type = " + originalType, e);
        }
    }
    
    private static PrimitiveType.PrimitiveTypeName asPrimitive(final String t, final Tokenizer st) {
        try {
            return PrimitiveType.PrimitiveTypeName.valueOf(t.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("expected one of " + Arrays.toString(PrimitiveType.PrimitiveTypeName.values()) + " got " + t + " at " + st.getLocationString(), e);
        }
    }
    
    private static Type.Repetition asRepetition(final String t, final Tokenizer st) {
        try {
            return Type.Repetition.valueOf(t.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("expected one of " + Arrays.toString(Type.Repetition.values()) + " got " + t + " at " + st.getLocationString(), e);
        }
    }
    
    private static void check(final String t, final String expected, final String message, final Tokenizer tokenizer) {
        if (!t.equalsIgnoreCase(expected)) {
            throw new IllegalArgumentException(message + ": expected '" + expected + "' but got '" + t + "' at " + tokenizer.getLocationString());
        }
    }
    
    static {
        LOG = Log.getLog(MessageTypeParser.class);
    }
    
    private static class Tokenizer
    {
        private StringTokenizer st;
        private int line;
        private StringBuffer currentLine;
        
        public Tokenizer(final String schemaString, final String string) {
            this.line = 0;
            this.currentLine = new StringBuffer();
            this.st = new StringTokenizer(schemaString, " ,;{}()\n\t=", true);
        }
        
        public String nextToken() {
            while (this.st.hasMoreTokens()) {
                final String t = this.st.nextToken();
                if (t.equals("\n")) {
                    ++this.line;
                    this.currentLine.setLength(0);
                }
                else {
                    this.currentLine.append(t);
                }
                if (!this.isWhitespace(t)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("unexpected end of schema");
        }
        
        private boolean isWhitespace(final String t) {
            return t.equals(" ") || t.equals("\t") || t.equals("\n");
        }
        
        public String getLocationString() {
            return "line " + this.line + ": " + this.currentLine.toString();
        }
    }
}
