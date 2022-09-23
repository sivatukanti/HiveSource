// 
// Decompiled by Procyon v0.5.36
// 

package com.google.protobuf;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.nio.CharBuffer;
import java.math.BigInteger;
import java.util.Locale;
import java.io.IOException;

public final class TextFormat
{
    private static final Printer DEFAULT_PRINTER;
    private static final Printer SINGLE_LINE_PRINTER;
    private static final Printer UNICODE_PRINTER;
    private static final int BUFFER_SIZE = 4096;
    
    private TextFormat() {
    }
    
    public static void print(final MessageOrBuilder message, final Appendable output) throws IOException {
        TextFormat.DEFAULT_PRINTER.print(message, new TextGenerator(output));
    }
    
    public static void print(final UnknownFieldSet fields, final Appendable output) throws IOException {
        TextFormat.DEFAULT_PRINTER.printUnknownFields(fields, new TextGenerator(output));
    }
    
    public static String shortDebugString(final MessageOrBuilder message) {
        try {
            final StringBuilder sb = new StringBuilder();
            TextFormat.SINGLE_LINE_PRINTER.print(message, new TextGenerator((Appendable)sb));
            return sb.toString().trim();
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static String shortDebugString(final UnknownFieldSet fields) {
        try {
            final StringBuilder sb = new StringBuilder();
            TextFormat.SINGLE_LINE_PRINTER.printUnknownFields(fields, new TextGenerator((Appendable)sb));
            return sb.toString().trim();
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static String printToString(final MessageOrBuilder message) {
        try {
            final StringBuilder text = new StringBuilder();
            print(message, text);
            return text.toString();
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static String printToString(final UnknownFieldSet fields) {
        try {
            final StringBuilder text = new StringBuilder();
            print(fields, text);
            return text.toString();
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static String printToUnicodeString(final MessageOrBuilder message) {
        try {
            final StringBuilder text = new StringBuilder();
            TextFormat.UNICODE_PRINTER.print(message, new TextGenerator((Appendable)text));
            return text.toString();
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static String printToUnicodeString(final UnknownFieldSet fields) {
        try {
            final StringBuilder text = new StringBuilder();
            TextFormat.UNICODE_PRINTER.printUnknownFields(fields, new TextGenerator((Appendable)text));
            return text.toString();
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static void printField(final Descriptors.FieldDescriptor field, final Object value, final Appendable output) throws IOException {
        TextFormat.DEFAULT_PRINTER.printField(field, value, new TextGenerator(output));
    }
    
    public static String printFieldToString(final Descriptors.FieldDescriptor field, final Object value) {
        try {
            final StringBuilder text = new StringBuilder();
            printField(field, value, text);
            return text.toString();
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static void printFieldValue(final Descriptors.FieldDescriptor field, final Object value, final Appendable output) throws IOException {
        TextFormat.DEFAULT_PRINTER.printFieldValue(field, value, new TextGenerator(output));
    }
    
    public static void printUnknownFieldValue(final int tag, final Object value, final Appendable output) throws IOException {
        printUnknownFieldValue(tag, value, new TextGenerator(output));
    }
    
    private static void printUnknownFieldValue(final int tag, final Object value, final TextGenerator generator) throws IOException {
        switch (WireFormat.getTagWireType(tag)) {
            case 0: {
                generator.print(unsignedToString((long)value));
                break;
            }
            case 5: {
                generator.print(String.format(null, "0x%08x", (Integer)value));
                break;
            }
            case 1: {
                generator.print(String.format(null, "0x%016x", (Long)value));
                break;
            }
            case 2: {
                generator.print("\"");
                generator.print(escapeBytes((ByteString)value));
                generator.print("\"");
                break;
            }
            case 3: {
                TextFormat.DEFAULT_PRINTER.printUnknownFields((UnknownFieldSet)value, generator);
                break;
            }
            default: {
                throw new IllegalArgumentException("Bad tag: " + tag);
            }
        }
    }
    
    private static String unsignedToString(final int value) {
        if (value >= 0) {
            return Integer.toString(value);
        }
        return Long.toString((long)value & 0xFFFFFFFFL);
    }
    
    private static String unsignedToString(final long value) {
        if (value >= 0L) {
            return Long.toString(value);
        }
        return BigInteger.valueOf(value & Long.MAX_VALUE).setBit(63).toString();
    }
    
    public static void merge(final Readable input, final Message.Builder builder) throws IOException {
        merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
    }
    
    public static void merge(final CharSequence input, final Message.Builder builder) throws ParseException {
        merge(input, ExtensionRegistry.getEmptyRegistry(), builder);
    }
    
    public static void merge(final Readable input, final ExtensionRegistry extensionRegistry, final Message.Builder builder) throws IOException {
        merge(toStringBuilder(input), extensionRegistry, builder);
    }
    
    private static StringBuilder toStringBuilder(final Readable input) throws IOException {
        final StringBuilder text = new StringBuilder();
        final CharBuffer buffer = CharBuffer.allocate(4096);
        while (true) {
            final int n = input.read(buffer);
            if (n == -1) {
                break;
            }
            buffer.flip();
            text.append(buffer, 0, n);
        }
        return text;
    }
    
    public static void merge(final CharSequence input, final ExtensionRegistry extensionRegistry, final Message.Builder builder) throws ParseException {
        final Tokenizer tokenizer = new Tokenizer(input);
        while (!tokenizer.atEnd()) {
            mergeField(tokenizer, extensionRegistry, builder);
        }
    }
    
    private static void mergeField(final Tokenizer tokenizer, final ExtensionRegistry extensionRegistry, final Message.Builder builder) throws ParseException {
        final Descriptors.Descriptor type = builder.getDescriptorForType();
        ExtensionRegistry.ExtensionInfo extension = null;
        Descriptors.FieldDescriptor field;
        if (tokenizer.tryConsume("[")) {
            final StringBuilder name = new StringBuilder(tokenizer.consumeIdentifier());
            while (tokenizer.tryConsume(".")) {
                name.append('.');
                name.append(tokenizer.consumeIdentifier());
            }
            extension = extensionRegistry.findExtensionByName(name.toString());
            if (extension == null) {
                throw tokenizer.parseExceptionPreviousToken("Extension \"" + (Object)name + "\" not found in the ExtensionRegistry.");
            }
            if (extension.descriptor.getContainingType() != type) {
                throw tokenizer.parseExceptionPreviousToken("Extension \"" + (Object)name + "\" does not extend message type \"" + type.getFullName() + "\".");
            }
            tokenizer.consume("]");
            field = extension.descriptor;
        }
        else {
            final String name2 = tokenizer.consumeIdentifier();
            field = type.findFieldByName(name2);
            if (field == null) {
                final String lowerName = name2.toLowerCase(Locale.US);
                field = type.findFieldByName(lowerName);
                if (field != null && field.getType() != Descriptors.FieldDescriptor.Type.GROUP) {
                    field = null;
                }
            }
            if (field != null && field.getType() == Descriptors.FieldDescriptor.Type.GROUP && !field.getMessageType().getName().equals(name2)) {
                field = null;
            }
            if (field == null) {
                throw tokenizer.parseExceptionPreviousToken("Message type \"" + type.getFullName() + "\" has no field named \"" + name2 + "\".");
            }
        }
        Object value = null;
        if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            tokenizer.tryConsume(":");
            String endToken;
            if (tokenizer.tryConsume("<")) {
                endToken = ">";
            }
            else {
                tokenizer.consume("{");
                endToken = "}";
            }
            Message.Builder subBuilder;
            if (extension == null) {
                subBuilder = builder.newBuilderForField(field);
            }
            else {
                subBuilder = extension.defaultInstance.newBuilderForType();
            }
            while (!tokenizer.tryConsume(endToken)) {
                if (tokenizer.atEnd()) {
                    throw tokenizer.parseException("Expected \"" + endToken + "\".");
                }
                mergeField(tokenizer, extensionRegistry, subBuilder);
            }
            value = subBuilder.buildPartial();
        }
        else {
            tokenizer.consume(":");
            switch (field.getType()) {
                case INT32:
                case SINT32:
                case SFIXED32: {
                    value = tokenizer.consumeInt32();
                    break;
                }
                case INT64:
                case SINT64:
                case SFIXED64: {
                    value = tokenizer.consumeInt64();
                    break;
                }
                case UINT32:
                case FIXED32: {
                    value = tokenizer.consumeUInt32();
                    break;
                }
                case UINT64:
                case FIXED64: {
                    value = tokenizer.consumeUInt64();
                    break;
                }
                case FLOAT: {
                    value = tokenizer.consumeFloat();
                    break;
                }
                case DOUBLE: {
                    value = tokenizer.consumeDouble();
                    break;
                }
                case BOOL: {
                    value = tokenizer.consumeBoolean();
                    break;
                }
                case STRING: {
                    value = tokenizer.consumeString();
                    break;
                }
                case BYTES: {
                    value = tokenizer.consumeByteString();
                    break;
                }
                case ENUM: {
                    final Descriptors.EnumDescriptor enumType = field.getEnumType();
                    if (tokenizer.lookingAtInteger()) {
                        final int number = tokenizer.consumeInt32();
                        value = enumType.findValueByNumber(number);
                        if (value == null) {
                            throw tokenizer.parseExceptionPreviousToken("Enum type \"" + enumType.getFullName() + "\" has no value with number " + number + '.');
                        }
                        break;
                    }
                    else {
                        final String id = tokenizer.consumeIdentifier();
                        value = enumType.findValueByName(id);
                        if (value == null) {
                            throw tokenizer.parseExceptionPreviousToken("Enum type \"" + enumType.getFullName() + "\" has no value named \"" + id + "\".");
                        }
                        break;
                    }
                    break;
                }
                case MESSAGE:
                case GROUP: {
                    throw new RuntimeException("Can't get here.");
                }
            }
        }
        if (field.isRepeated()) {
            builder.addRepeatedField(field, value);
        }
        else {
            builder.setField(field, value);
        }
    }
    
    static String escapeBytes(final ByteString input) {
        final StringBuilder builder = new StringBuilder(input.size());
        for (int i = 0; i < input.size(); ++i) {
            final byte b = input.byteAt(i);
            switch (b) {
                case 7: {
                    builder.append("\\a");
                    break;
                }
                case 8: {
                    builder.append("\\b");
                    break;
                }
                case 12: {
                    builder.append("\\f");
                    break;
                }
                case 10: {
                    builder.append("\\n");
                    break;
                }
                case 13: {
                    builder.append("\\r");
                    break;
                }
                case 9: {
                    builder.append("\\t");
                    break;
                }
                case 11: {
                    builder.append("\\v");
                    break;
                }
                case 92: {
                    builder.append("\\\\");
                    break;
                }
                case 39: {
                    builder.append("\\'");
                    break;
                }
                case 34: {
                    builder.append("\\\"");
                    break;
                }
                default: {
                    if (b >= 32) {
                        builder.append((char)b);
                        break;
                    }
                    builder.append('\\');
                    builder.append((char)(48 + (b >>> 6 & 0x3)));
                    builder.append((char)(48 + (b >>> 3 & 0x7)));
                    builder.append((char)(48 + (b & 0x7)));
                    break;
                }
            }
        }
        return builder.toString();
    }
    
    static ByteString unescapeBytes(final CharSequence charString) throws InvalidEscapeSequenceException {
        final ByteString input = ByteString.copyFromUtf8(charString.toString());
        final byte[] result = new byte[input.size()];
        int pos = 0;
        for (int i = 0; i < input.size(); ++i) {
            byte c = input.byteAt(i);
            if (c == 92) {
                if (i + 1 >= input.size()) {
                    throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\' at end of string.");
                }
                ++i;
                c = input.byteAt(i);
                if (isOctal(c)) {
                    int code = digitValue(c);
                    if (i + 1 < input.size() && isOctal(input.byteAt(i + 1))) {
                        ++i;
                        code = code * 8 + digitValue(input.byteAt(i));
                    }
                    if (i + 1 < input.size() && isOctal(input.byteAt(i + 1))) {
                        ++i;
                        code = code * 8 + digitValue(input.byteAt(i));
                    }
                    result[pos++] = (byte)code;
                }
                else {
                    switch (c) {
                        case 97: {
                            result[pos++] = 7;
                            break;
                        }
                        case 98: {
                            result[pos++] = 8;
                            break;
                        }
                        case 102: {
                            result[pos++] = 12;
                            break;
                        }
                        case 110: {
                            result[pos++] = 10;
                            break;
                        }
                        case 114: {
                            result[pos++] = 13;
                            break;
                        }
                        case 116: {
                            result[pos++] = 9;
                            break;
                        }
                        case 118: {
                            result[pos++] = 11;
                            break;
                        }
                        case 92: {
                            result[pos++] = 92;
                            break;
                        }
                        case 39: {
                            result[pos++] = 39;
                            break;
                        }
                        case 34: {
                            result[pos++] = 34;
                            break;
                        }
                        case 120: {
                            int code = 0;
                            if (i + 1 < input.size() && isHex(input.byteAt(i + 1))) {
                                ++i;
                                code = digitValue(input.byteAt(i));
                                if (i + 1 < input.size() && isHex(input.byteAt(i + 1))) {
                                    ++i;
                                    code = code * 16 + digitValue(input.byteAt(i));
                                }
                                result[pos++] = (byte)code;
                                break;
                            }
                            throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\x' with no digits");
                        }
                        default: {
                            throw new InvalidEscapeSequenceException("Invalid escape sequence: '\\" + (char)c + '\'');
                        }
                    }
                }
            }
            else {
                result[pos++] = c;
            }
        }
        return ByteString.copyFrom(result, 0, pos);
    }
    
    static String escapeText(final String input) {
        return escapeBytes(ByteString.copyFromUtf8(input));
    }
    
    static String unescapeText(final String input) throws InvalidEscapeSequenceException {
        return unescapeBytes(input).toStringUtf8();
    }
    
    private static boolean isOctal(final byte c) {
        return 48 <= c && c <= 55;
    }
    
    private static boolean isHex(final byte c) {
        return (48 <= c && c <= 57) || (97 <= c && c <= 102) || (65 <= c && c <= 70);
    }
    
    private static int digitValue(final byte c) {
        if (48 <= c && c <= 57) {
            return c - 48;
        }
        if (97 <= c && c <= 122) {
            return c - 97 + 10;
        }
        return c - 65 + 10;
    }
    
    static int parseInt32(final String text) throws NumberFormatException {
        return (int)parseInteger(text, true, false);
    }
    
    static int parseUInt32(final String text) throws NumberFormatException {
        return (int)parseInteger(text, false, false);
    }
    
    static long parseInt64(final String text) throws NumberFormatException {
        return parseInteger(text, true, true);
    }
    
    static long parseUInt64(final String text) throws NumberFormatException {
        return parseInteger(text, false, true);
    }
    
    private static long parseInteger(final String text, final boolean isSigned, final boolean isLong) throws NumberFormatException {
        int pos = 0;
        boolean negative = false;
        if (text.startsWith("-", pos)) {
            if (!isSigned) {
                throw new NumberFormatException("Number must be positive: " + text);
            }
            ++pos;
            negative = true;
        }
        int radix = 10;
        if (text.startsWith("0x", pos)) {
            pos += 2;
            radix = 16;
        }
        else if (text.startsWith("0", pos)) {
            radix = 8;
        }
        final String numberText = text.substring(pos);
        long result = 0L;
        if (numberText.length() < 16) {
            result = Long.parseLong(numberText, radix);
            if (negative) {
                result = -result;
            }
            if (!isLong) {
                if (isSigned) {
                    if (result > 2147483647L || result < -2147483648L) {
                        throw new NumberFormatException("Number out of range for 32-bit signed integer: " + text);
                    }
                }
                else if (result >= 4294967296L || result < 0L) {
                    throw new NumberFormatException("Number out of range for 32-bit unsigned integer: " + text);
                }
            }
        }
        else {
            BigInteger bigValue = new BigInteger(numberText, radix);
            if (negative) {
                bigValue = bigValue.negate();
            }
            if (!isLong) {
                if (isSigned) {
                    if (bigValue.bitLength() > 31) {
                        throw new NumberFormatException("Number out of range for 32-bit signed integer: " + text);
                    }
                }
                else if (bigValue.bitLength() > 32) {
                    throw new NumberFormatException("Number out of range for 32-bit unsigned integer: " + text);
                }
            }
            else if (isSigned) {
                if (bigValue.bitLength() > 63) {
                    throw new NumberFormatException("Number out of range for 64-bit signed integer: " + text);
                }
            }
            else if (bigValue.bitLength() > 64) {
                throw new NumberFormatException("Number out of range for 64-bit unsigned integer: " + text);
            }
            result = bigValue.longValue();
        }
        return result;
    }
    
    static {
        DEFAULT_PRINTER = new Printer();
        SINGLE_LINE_PRINTER = new Printer().setSingleLineMode(true);
        UNICODE_PRINTER = new Printer().setEscapeNonAscii(false);
    }
    
    private static final class Printer
    {
        boolean singleLineMode;
        boolean escapeNonAscii;
        
        private Printer() {
            this.singleLineMode = false;
            this.escapeNonAscii = true;
        }
        
        private Printer setSingleLineMode(final boolean singleLineMode) {
            this.singleLineMode = singleLineMode;
            return this;
        }
        
        private Printer setEscapeNonAscii(final boolean escapeNonAscii) {
            this.escapeNonAscii = escapeNonAscii;
            return this;
        }
        
        private void print(final MessageOrBuilder message, final TextGenerator generator) throws IOException {
            for (final Map.Entry<Descriptors.FieldDescriptor, Object> field : message.getAllFields().entrySet()) {
                this.printField(field.getKey(), field.getValue(), generator);
            }
            this.printUnknownFields(message.getUnknownFields(), generator);
        }
        
        private void printField(final Descriptors.FieldDescriptor field, final Object value, final TextGenerator generator) throws IOException {
            if (field.isRepeated()) {
                for (final Object element : (List)value) {
                    this.printSingleField(field, element, generator);
                }
            }
            else {
                this.printSingleField(field, value, generator);
            }
        }
        
        private void printSingleField(final Descriptors.FieldDescriptor field, final Object value, final TextGenerator generator) throws IOException {
            if (field.isExtension()) {
                generator.print("[");
                if (field.getContainingType().getOptions().getMessageSetWireFormat() && field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && field.isOptional() && field.getExtensionScope() == field.getMessageType()) {
                    generator.print(field.getMessageType().getFullName());
                }
                else {
                    generator.print(field.getFullName());
                }
                generator.print("]");
            }
            else if (field.getType() == Descriptors.FieldDescriptor.Type.GROUP) {
                generator.print(field.getMessageType().getName());
            }
            else {
                generator.print(field.getName());
            }
            if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                if (this.singleLineMode) {
                    generator.print(" { ");
                }
                else {
                    generator.print(" {\n");
                    generator.indent();
                }
            }
            else {
                generator.print(": ");
            }
            this.printFieldValue(field, value, generator);
            if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                if (this.singleLineMode) {
                    generator.print("} ");
                }
                else {
                    generator.outdent();
                    generator.print("}\n");
                }
            }
            else if (this.singleLineMode) {
                generator.print(" ");
            }
            else {
                generator.print("\n");
            }
        }
        
        private void printFieldValue(final Descriptors.FieldDescriptor field, final Object value, final TextGenerator generator) throws IOException {
            switch (field.getType()) {
                case INT32:
                case SINT32:
                case SFIXED32: {
                    generator.print(((Integer)value).toString());
                    break;
                }
                case INT64:
                case SINT64:
                case SFIXED64: {
                    generator.print(((Long)value).toString());
                    break;
                }
                case BOOL: {
                    generator.print(((Boolean)value).toString());
                    break;
                }
                case FLOAT: {
                    generator.print(((Float)value).toString());
                    break;
                }
                case DOUBLE: {
                    generator.print(((Double)value).toString());
                    break;
                }
                case UINT32:
                case FIXED32: {
                    generator.print(unsignedToString((int)value));
                    break;
                }
                case UINT64:
                case FIXED64: {
                    generator.print(unsignedToString((long)value));
                    break;
                }
                case STRING: {
                    generator.print("\"");
                    generator.print((CharSequence)(this.escapeNonAscii ? TextFormat.escapeText((String)value) : value));
                    generator.print("\"");
                    break;
                }
                case BYTES: {
                    generator.print("\"");
                    generator.print(TextFormat.escapeBytes((ByteString)value));
                    generator.print("\"");
                    break;
                }
                case ENUM: {
                    generator.print(((Descriptors.EnumValueDescriptor)value).getName());
                    break;
                }
                case MESSAGE:
                case GROUP: {
                    this.print((MessageOrBuilder)value, generator);
                    break;
                }
            }
        }
        
        private void printUnknownFields(final UnknownFieldSet unknownFields, final TextGenerator generator) throws IOException {
            for (final Map.Entry<Integer, UnknownFieldSet.Field> entry : unknownFields.asMap().entrySet()) {
                final int number = entry.getKey();
                final UnknownFieldSet.Field field = entry.getValue();
                this.printUnknownField(number, 0, field.getVarintList(), generator);
                this.printUnknownField(number, 5, field.getFixed32List(), generator);
                this.printUnknownField(number, 1, field.getFixed64List(), generator);
                this.printUnknownField(number, 2, field.getLengthDelimitedList(), generator);
                for (final UnknownFieldSet value : field.getGroupList()) {
                    generator.print(entry.getKey().toString());
                    if (this.singleLineMode) {
                        generator.print(" { ");
                    }
                    else {
                        generator.print(" {\n");
                        generator.indent();
                    }
                    this.printUnknownFields(value, generator);
                    if (this.singleLineMode) {
                        generator.print("} ");
                    }
                    else {
                        generator.outdent();
                        generator.print("}\n");
                    }
                }
            }
        }
        
        private void printUnknownField(final int number, final int wireType, final List<?> values, final TextGenerator generator) throws IOException {
            for (final Object value : values) {
                generator.print(String.valueOf(number));
                generator.print(": ");
                printUnknownFieldValue(wireType, value, generator);
                generator.print(this.singleLineMode ? " " : "\n");
            }
        }
    }
    
    private static final class TextGenerator
    {
        private final Appendable output;
        private final StringBuilder indent;
        private boolean atStartOfLine;
        
        private TextGenerator(final Appendable output) {
            this.indent = new StringBuilder();
            this.atStartOfLine = true;
            this.output = output;
        }
        
        public void indent() {
            this.indent.append("  ");
        }
        
        public void outdent() {
            final int length = this.indent.length();
            if (length == 0) {
                throw new IllegalArgumentException(" Outdent() without matching Indent().");
            }
            this.indent.delete(length - 2, length);
        }
        
        public void print(final CharSequence text) throws IOException {
            final int size = text.length();
            int pos = 0;
            for (int i = 0; i < size; ++i) {
                if (text.charAt(i) == '\n') {
                    this.write(text.subSequence(pos, size), i - pos + 1);
                    pos = i + 1;
                    this.atStartOfLine = true;
                }
            }
            this.write(text.subSequence(pos, size), size - pos);
        }
        
        private void write(final CharSequence data, final int size) throws IOException {
            if (size == 0) {
                return;
            }
            if (this.atStartOfLine) {
                this.atStartOfLine = false;
                this.output.append(this.indent);
            }
            this.output.append(data);
        }
    }
    
    private static final class Tokenizer
    {
        private final CharSequence text;
        private final Matcher matcher;
        private String currentToken;
        private int pos;
        private int line;
        private int column;
        private int previousLine;
        private int previousColumn;
        private static final Pattern WHITESPACE;
        private static final Pattern TOKEN;
        private static final Pattern DOUBLE_INFINITY;
        private static final Pattern FLOAT_INFINITY;
        private static final Pattern FLOAT_NAN;
        
        private Tokenizer(final CharSequence text) {
            this.pos = 0;
            this.line = 0;
            this.column = 0;
            this.previousLine = 0;
            this.previousColumn = 0;
            this.text = text;
            this.matcher = Tokenizer.WHITESPACE.matcher(text);
            this.skipWhitespace();
            this.nextToken();
        }
        
        public boolean atEnd() {
            return this.currentToken.length() == 0;
        }
        
        public void nextToken() {
            this.previousLine = this.line;
            this.previousColumn = this.column;
            while (this.pos < this.matcher.regionStart()) {
                if (this.text.charAt(this.pos) == '\n') {
                    ++this.line;
                    this.column = 0;
                }
                else {
                    ++this.column;
                }
                ++this.pos;
            }
            if (this.matcher.regionStart() == this.matcher.regionEnd()) {
                this.currentToken = "";
            }
            else {
                this.matcher.usePattern(Tokenizer.TOKEN);
                if (this.matcher.lookingAt()) {
                    this.currentToken = this.matcher.group();
                    this.matcher.region(this.matcher.end(), this.matcher.regionEnd());
                }
                else {
                    this.currentToken = String.valueOf(this.text.charAt(this.pos));
                    this.matcher.region(this.pos + 1, this.matcher.regionEnd());
                }
                this.skipWhitespace();
            }
        }
        
        private void skipWhitespace() {
            this.matcher.usePattern(Tokenizer.WHITESPACE);
            if (this.matcher.lookingAt()) {
                this.matcher.region(this.matcher.end(), this.matcher.regionEnd());
            }
        }
        
        public boolean tryConsume(final String token) {
            if (this.currentToken.equals(token)) {
                this.nextToken();
                return true;
            }
            return false;
        }
        
        public void consume(final String token) throws ParseException {
            if (!this.tryConsume(token)) {
                throw this.parseException("Expected \"" + token + "\".");
            }
        }
        
        public boolean lookingAtInteger() {
            if (this.currentToken.length() == 0) {
                return false;
            }
            final char c = this.currentToken.charAt(0);
            return ('0' <= c && c <= '9') || c == '-' || c == '+';
        }
        
        public String consumeIdentifier() throws ParseException {
            for (int i = 0; i < this.currentToken.length(); ++i) {
                final char c = this.currentToken.charAt(i);
                if (('a' > c || c > 'z') && ('A' > c || c > 'Z') && ('0' > c || c > '9') && c != '_' && c != '.') {
                    throw this.parseException("Expected identifier.");
                }
            }
            final String result = this.currentToken;
            this.nextToken();
            return result;
        }
        
        public int consumeInt32() throws ParseException {
            try {
                final int result = TextFormat.parseInt32(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.integerParseException(e);
            }
        }
        
        public int consumeUInt32() throws ParseException {
            try {
                final int result = TextFormat.parseUInt32(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.integerParseException(e);
            }
        }
        
        public long consumeInt64() throws ParseException {
            try {
                final long result = TextFormat.parseInt64(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.integerParseException(e);
            }
        }
        
        public long consumeUInt64() throws ParseException {
            try {
                final long result = TextFormat.parseUInt64(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.integerParseException(e);
            }
        }
        
        public double consumeDouble() throws ParseException {
            if (Tokenizer.DOUBLE_INFINITY.matcher(this.currentToken).matches()) {
                final boolean negative = this.currentToken.startsWith("-");
                this.nextToken();
                return negative ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            }
            if (this.currentToken.equalsIgnoreCase("nan")) {
                this.nextToken();
                return Double.NaN;
            }
            try {
                final double result = Double.parseDouble(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.floatParseException(e);
            }
        }
        
        public float consumeFloat() throws ParseException {
            if (Tokenizer.FLOAT_INFINITY.matcher(this.currentToken).matches()) {
                final boolean negative = this.currentToken.startsWith("-");
                this.nextToken();
                return negative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
            }
            if (Tokenizer.FLOAT_NAN.matcher(this.currentToken).matches()) {
                this.nextToken();
                return Float.NaN;
            }
            try {
                final float result = Float.parseFloat(this.currentToken);
                this.nextToken();
                return result;
            }
            catch (NumberFormatException e) {
                throw this.floatParseException(e);
            }
        }
        
        public boolean consumeBoolean() throws ParseException {
            if (this.currentToken.equals("true") || this.currentToken.equals("t") || this.currentToken.equals("1")) {
                this.nextToken();
                return true;
            }
            if (this.currentToken.equals("false") || this.currentToken.equals("f") || this.currentToken.equals("0")) {
                this.nextToken();
                return false;
            }
            throw this.parseException("Expected \"true\" or \"false\".");
        }
        
        public String consumeString() throws ParseException {
            return this.consumeByteString().toStringUtf8();
        }
        
        public ByteString consumeByteString() throws ParseException {
            final List<ByteString> list = new ArrayList<ByteString>();
            this.consumeByteString(list);
            while (this.currentToken.startsWith("'") || this.currentToken.startsWith("\"")) {
                this.consumeByteString(list);
            }
            return ByteString.copyFrom(list);
        }
        
        private void consumeByteString(final List<ByteString> list) throws ParseException {
            final char quote = (this.currentToken.length() > 0) ? this.currentToken.charAt(0) : '\0';
            if (quote != '\"' && quote != '\'') {
                throw this.parseException("Expected string.");
            }
            if (this.currentToken.length() < 2 || this.currentToken.charAt(this.currentToken.length() - 1) != quote) {
                throw this.parseException("String missing ending quote.");
            }
            try {
                final String escaped = this.currentToken.substring(1, this.currentToken.length() - 1);
                final ByteString result = TextFormat.unescapeBytes(escaped);
                this.nextToken();
                list.add(result);
            }
            catch (InvalidEscapeSequenceException e) {
                throw this.parseException(e.getMessage());
            }
        }
        
        public ParseException parseException(final String description) {
            return new ParseException(this.line + 1, this.column + 1, description);
        }
        
        public ParseException parseExceptionPreviousToken(final String description) {
            return new ParseException(this.previousLine + 1, this.previousColumn + 1, description);
        }
        
        private ParseException integerParseException(final NumberFormatException e) {
            return this.parseException("Couldn't parse integer: " + e.getMessage());
        }
        
        private ParseException floatParseException(final NumberFormatException e) {
            return this.parseException("Couldn't parse number: " + e.getMessage());
        }
        
        static {
            WHITESPACE = Pattern.compile("(\\s|(#.*$))++", 8);
            TOKEN = Pattern.compile("[a-zA-Z_][0-9a-zA-Z_+-]*+|[.]?[0-9+-][0-9a-zA-Z_.+-]*+|\"([^\"\n\\\\]|\\\\.)*+(\"|\\\\?$)|'([^'\n\\\\]|\\\\.)*+('|\\\\?$)", 8);
            DOUBLE_INFINITY = Pattern.compile("-?inf(inity)?", 2);
            FLOAT_INFINITY = Pattern.compile("-?inf(inity)?f?", 2);
            FLOAT_NAN = Pattern.compile("nanf?", 2);
        }
    }
    
    public static class ParseException extends IOException
    {
        private static final long serialVersionUID = 3196188060225107702L;
        private final int line;
        private final int column;
        
        public ParseException(final String message) {
            this(-1, -1, message);
        }
        
        public ParseException(final int line, final int column, final String message) {
            super(Integer.toString(line) + ":" + column + ": " + message);
            this.line = line;
            this.column = column;
        }
        
        public int getLine() {
            return this.line;
        }
        
        public int getColumn() {
            return this.column;
        }
    }
    
    static class InvalidEscapeSequenceException extends IOException
    {
        private static final long serialVersionUID = -8164033650142593304L;
        
        InvalidEscapeSequenceException(final String description) {
            super(description);
        }
    }
}
