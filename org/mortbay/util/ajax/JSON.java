// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util.ajax;

import org.mortbay.log.Log;
import java.util.ArrayList;
import org.mortbay.util.Loader;
import java.lang.reflect.Array;
import java.util.Iterator;
import org.mortbay.util.TypeUtil;
import org.mortbay.util.QuotedStringTokenizer;
import java.util.Collection;
import org.mortbay.util.IO;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JSON
{
    private static JSON __default;
    private Map _convertors;
    private int _stringBufferSize;
    
    public JSON() {
        this._convertors = Collections.synchronizedMap(new HashMap<Object, Object>());
        this._stringBufferSize = 256;
    }
    
    public int getStringBufferSize() {
        return this._stringBufferSize;
    }
    
    public void setStringBufferSize(final int stringBufferSize) {
        this._stringBufferSize = stringBufferSize;
    }
    
    public static void registerConvertor(final Class forClass, final Convertor convertor) {
        JSON.__default.addConvertor(forClass, convertor);
    }
    
    public static JSON getDefault() {
        return JSON.__default;
    }
    
    public static void setDefault(final JSON json) {
        JSON.__default = json;
    }
    
    public static String toString(final Object object) {
        final StringBuffer buffer = new StringBuffer(JSON.__default.getStringBufferSize());
        synchronized (buffer) {
            JSON.__default.append(buffer, object);
            return buffer.toString();
        }
    }
    
    public static String toString(final Map object) {
        final StringBuffer buffer = new StringBuffer(JSON.__default.getStringBufferSize());
        synchronized (buffer) {
            JSON.__default.appendMap(buffer, object);
            return buffer.toString();
        }
    }
    
    public static String toString(final Object[] array) {
        final StringBuffer buffer = new StringBuffer(JSON.__default.getStringBufferSize());
        synchronized (buffer) {
            JSON.__default.appendArray(buffer, array);
            return buffer.toString();
        }
    }
    
    public static Object parse(final String s) {
        return JSON.__default.parse(new StringSource(s), false);
    }
    
    public static Object parse(final String s, final boolean stripOuterComment) {
        return JSON.__default.parse(new StringSource(s), stripOuterComment);
    }
    
    public static Object parse(final Reader in) throws IOException {
        return JSON.__default.parse(new ReaderSource(in), false);
    }
    
    public static Object parse(final Reader in, final boolean stripOuterComment) throws IOException {
        return JSON.__default.parse(new ReaderSource(in), stripOuterComment);
    }
    
    public static Object parse(final InputStream in) throws IOException {
        return JSON.__default.parse(new StringSource(IO.toString(in)), false);
    }
    
    public static Object parse(final InputStream in, final boolean stripOuterComment) throws IOException {
        return JSON.__default.parse(new StringSource(IO.toString(in)), stripOuterComment);
    }
    
    public String toJSON(final Object object) {
        final StringBuffer buffer = new StringBuffer(this.getStringBufferSize());
        synchronized (buffer) {
            this.append(buffer, object);
            return buffer.toString();
        }
    }
    
    public Object fromJSON(final String json) {
        final Source source = new StringSource(json);
        return this.parse(source);
    }
    
    public void append(final StringBuffer buffer, final Object object) {
        if (object == null) {
            buffer.append("null");
        }
        else if (object instanceof Convertible) {
            this.appendJSON(buffer, (Convertible)object);
        }
        else if (object instanceof Generator) {
            this.appendJSON(buffer, (Generator)object);
        }
        else if (object instanceof Map) {
            this.appendMap(buffer, (Map)object);
        }
        else if (object instanceof Collection) {
            this.appendArray(buffer, (Collection)object);
        }
        else if (object.getClass().isArray()) {
            this.appendArray(buffer, object);
        }
        else if (object instanceof Number) {
            this.appendNumber(buffer, (Number)object);
        }
        else if (object instanceof Boolean) {
            this.appendBoolean(buffer, (Boolean)object);
        }
        else if (object instanceof String) {
            this.appendString(buffer, (String)object);
        }
        else {
            final Convertor convertor = this.getConvertor(object.getClass());
            if (convertor != null) {
                this.appendJSON(buffer, convertor, object);
            }
            else {
                this.appendString(buffer, object.toString());
            }
        }
    }
    
    public void appendNull(final StringBuffer buffer) {
        buffer.append("null");
    }
    
    public void appendJSON(final StringBuffer buffer, final Convertor convertor, final Object object) {
        this.appendJSON(buffer, new Convertible() {
            public void fromJSON(final Map object) {
            }
            
            public void toJSON(final Output out) {
                convertor.toJSON(object, out);
            }
        });
    }
    
    public void appendJSON(final StringBuffer buffer, final Convertible converter) {
        final char[] c = { '{' };
        converter.toJSON(new Output() {
            public void add(final Object obj) {
                if (c[0] == '\0') {
                    throw new IllegalStateException();
                }
                JSON.this.append(buffer, obj);
                c[0] = '\0';
            }
            
            public void addClass(final Class type) {
                if (c[0] == '\0') {
                    throw new IllegalStateException();
                }
                buffer.append(c);
                buffer.append("\"class\":");
                JSON.this.append(buffer, type.getName());
                c[0] = ',';
            }
            
            public void add(final String name, final Object value) {
                if (c[0] == '\0') {
                    throw new IllegalStateException();
                }
                buffer.append(c);
                QuotedStringTokenizer.quote(buffer, name);
                buffer.append(':');
                JSON.this.append(buffer, value);
                c[0] = ',';
            }
            
            public void add(final String name, final double value) {
                if (c[0] == '\0') {
                    throw new IllegalStateException();
                }
                buffer.append(c);
                QuotedStringTokenizer.quote(buffer, name);
                buffer.append(':');
                JSON.this.appendNumber(buffer, new Double(value));
                c[0] = ',';
            }
            
            public void add(final String name, final long value) {
                if (c[0] == '\0') {
                    throw new IllegalStateException();
                }
                buffer.append(c);
                QuotedStringTokenizer.quote(buffer, name);
                buffer.append(':');
                JSON.this.appendNumber(buffer, TypeUtil.newLong(value));
                c[0] = ',';
            }
            
            public void add(final String name, final boolean value) {
                if (c[0] == '\0') {
                    throw new IllegalStateException();
                }
                buffer.append(c);
                QuotedStringTokenizer.quote(buffer, name);
                buffer.append(':');
                JSON.this.appendBoolean(buffer, value ? Boolean.TRUE : Boolean.FALSE);
                c[0] = ',';
            }
        });
        if (c[0] == '{') {
            buffer.append("{}");
        }
        else if (c[0] != '\0') {
            buffer.append("}");
        }
    }
    
    public void appendJSON(final StringBuffer buffer, final Generator generator) {
        generator.addJSON(buffer);
    }
    
    public void appendMap(final StringBuffer buffer, final Map object) {
        if (object == null) {
            this.appendNull(buffer);
            return;
        }
        buffer.append('{');
        final Iterator iter = object.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry entry = iter.next();
            QuotedStringTokenizer.quote(buffer, entry.getKey().toString());
            buffer.append(':');
            this.append(buffer, entry.getValue());
            if (iter.hasNext()) {
                buffer.append(',');
            }
        }
        buffer.append('}');
    }
    
    public void appendArray(final StringBuffer buffer, final Collection collection) {
        if (collection == null) {
            this.appendNull(buffer);
            return;
        }
        buffer.append('[');
        final Iterator iter = collection.iterator();
        boolean first = true;
        while (iter.hasNext()) {
            if (!first) {
                buffer.append(',');
            }
            first = false;
            this.append(buffer, iter.next());
        }
        buffer.append(']');
    }
    
    public void appendArray(final StringBuffer buffer, final Object array) {
        if (array == null) {
            this.appendNull(buffer);
            return;
        }
        buffer.append('[');
        for (int length = Array.getLength(array), i = 0; i < length; ++i) {
            if (i != 0) {
                buffer.append(',');
            }
            this.append(buffer, Array.get(array, i));
        }
        buffer.append(']');
    }
    
    public void appendBoolean(final StringBuffer buffer, final Boolean b) {
        if (b == null) {
            this.appendNull(buffer);
            return;
        }
        buffer.append(b ? "true" : "false");
    }
    
    public void appendNumber(final StringBuffer buffer, final Number number) {
        if (number == null) {
            this.appendNull(buffer);
            return;
        }
        buffer.append(number);
    }
    
    public void appendString(final StringBuffer buffer, final String string) {
        if (string == null) {
            this.appendNull(buffer);
            return;
        }
        QuotedStringTokenizer.quote(buffer, string);
    }
    
    protected String toString(final char[] buffer, final int offset, final int length) {
        return new String(buffer, offset, length);
    }
    
    protected Map newMap() {
        return new HashMap();
    }
    
    protected Object[] newArray(final int size) {
        return new Object[size];
    }
    
    protected JSON contextForArray() {
        return this;
    }
    
    protected JSON contextFor(final String field) {
        return this;
    }
    
    protected Object convertTo(final Class type, final Map map) {
        if (type != null && Convertible.class.isAssignableFrom(type)) {
            try {
                final Convertible conv = type.newInstance();
                conv.fromJSON(map);
                return conv;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        final Convertor convertor = this.getConvertor(type);
        if (convertor != null) {
            return convertor.fromJSON(map);
        }
        return map;
    }
    
    public void addConvertor(final Class forClass, final Convertor convertor) {
        this._convertors.put(forClass.getName(), convertor);
    }
    
    protected Convertor getConvertor(final Class forClass) {
        Class cls = forClass;
        Convertor convertor = this._convertors.get(cls.getName());
        if (convertor == null && this != JSON.__default) {
            convertor = JSON.__default.getConvertor(cls);
        }
        while (convertor == null && cls != null && cls != Object.class) {
            final Class[] ifs = cls.getInterfaces();
            for (int i = 0; convertor == null && ifs != null && i < ifs.length; convertor = this._convertors.get(ifs[i++].getName())) {}
            if (convertor == null) {
                cls = cls.getSuperclass();
                convertor = this._convertors.get(cls.getName());
            }
        }
        return convertor;
    }
    
    public void addConvertorFor(final String name, final Convertor convertor) {
        this._convertors.put(name, convertor);
    }
    
    public Convertor getConvertorFor(final String name) {
        final String clsName = name;
        Convertor convertor = this._convertors.get(clsName);
        if (convertor == null && this != JSON.__default) {
            convertor = JSON.__default.getConvertorFor(clsName);
        }
        return convertor;
    }
    
    public Object parse(final Source source, final boolean stripOuterComment) {
        int comment_state = 0;
        if (!stripOuterComment) {
            return this.parse(source);
        }
        int strip_state = 1;
        Object o = null;
        while (source.hasNext()) {
            final char c = source.peek();
            if (comment_state == 1) {
                switch (c) {
                    case '/': {
                        comment_state = -1;
                        break;
                    }
                    case '*': {
                        comment_state = 2;
                        if (strip_state == 1) {
                            comment_state = 0;
                            strip_state = 2;
                            break;
                        }
                        break;
                    }
                }
            }
            else if (comment_state > 1) {
                switch (c) {
                    case '*': {
                        comment_state = 3;
                        break;
                    }
                    case '/': {
                        if (comment_state != 3) {
                            comment_state = 2;
                            break;
                        }
                        comment_state = 0;
                        if (strip_state == 2) {
                            return o;
                        }
                        break;
                    }
                    default: {
                        comment_state = 2;
                        break;
                    }
                }
            }
            else if (comment_state < 0) {
                switch (c) {
                    case '\n':
                    case '\r': {
                        comment_state = 0;
                        break;
                    }
                }
            }
            else if (!Character.isWhitespace(c)) {
                if (c == '/') {
                    comment_state = 1;
                }
                else if (c == '*') {
                    comment_state = 3;
                }
                else if (o == null) {
                    o = this.parse(source);
                    continue;
                }
            }
            source.next();
        }
        return o;
    }
    
    public Object parse(final Source source) {
        int comment_state = 0;
        while (source.hasNext()) {
            final char c = source.peek();
            if (comment_state == 1) {
                switch (c) {
                    case '/': {
                        comment_state = -1;
                        break;
                    }
                    case '*': {
                        comment_state = 2;
                        break;
                    }
                }
            }
            else if (comment_state > 1) {
                switch (c) {
                    case '*': {
                        comment_state = 3;
                        break;
                    }
                    case '/': {
                        if (comment_state == 3) {
                            comment_state = 0;
                            break;
                        }
                        comment_state = 2;
                        break;
                    }
                    default: {
                        comment_state = 2;
                        break;
                    }
                }
            }
            else if (comment_state < 0) {
                switch (c) {
                    case '\n':
                    case '\r': {
                        comment_state = 0;
                        break;
                    }
                }
            }
            else {
                switch (c) {
                    case '{': {
                        return this.parseObject(source);
                    }
                    case '[': {
                        return this.parseArray(source);
                    }
                    case '\"': {
                        return this.parseString(source);
                    }
                    case '-': {
                        return this.parseNumber(source);
                    }
                    case 'n': {
                        complete("null", source);
                        return null;
                    }
                    case 't': {
                        complete("true", source);
                        return Boolean.TRUE;
                    }
                    case 'f': {
                        complete("false", source);
                        return Boolean.FALSE;
                    }
                    case 'u': {
                        complete("undefined", source);
                        return null;
                    }
                    case 'N': {
                        complete("NaN", source);
                        return null;
                    }
                    case '/': {
                        comment_state = 1;
                        break;
                    }
                    default: {
                        if (Character.isDigit(c)) {
                            return this.parseNumber(source);
                        }
                        if (Character.isWhitespace(c)) {
                            break;
                        }
                        return this.handleUnknown(source, c);
                    }
                }
            }
            source.next();
        }
        return null;
    }
    
    protected Object handleUnknown(final Source source, final char c) {
        throw new IllegalStateException("unknown char '" + c + "'(" + (int)c + ") in " + source);
    }
    
    protected Object parseObject(final Source source) {
        if (source.next() != '{') {
            throw new IllegalStateException();
        }
        final Map map = this.newMap();
        char next = this.seekTo("\"}", source);
        while (source.hasNext()) {
            if (next == '}') {
                source.next();
                break;
            }
            final String name = this.parseString(source);
            this.seekTo(':', source);
            source.next();
            final Object value = this.contextFor(name).parse(source);
            map.put(name, value);
            this.seekTo(",}", source);
            next = source.next();
            if (next == '}') {
                break;
            }
            next = this.seekTo("\"}", source);
        }
        final String classname = map.get("class");
        if (classname != null) {
            try {
                final Class c = Loader.loadClass(JSON.class, classname);
                return this.convertTo(c, map);
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
    
    protected Object parseArray(final Source source) {
        if (source.next() != '[') {
            throw new IllegalStateException();
        }
        int size = 0;
        ArrayList list = null;
        Object item = null;
        boolean coma = true;
        while (source.hasNext()) {
            final char c = source.peek();
            switch (c) {
                case ']': {
                    source.next();
                    switch (size) {
                        case 0: {
                            return this.newArray(0);
                        }
                        case 1: {
                            final Object array = this.newArray(1);
                            Array.set(array, 0, item);
                            return array;
                        }
                        default: {
                            return list.toArray(this.newArray(list.size()));
                        }
                    }
                    break;
                }
                case ',': {
                    if (coma) {
                        throw new IllegalStateException();
                    }
                    coma = true;
                    source.next();
                    continue;
                }
                default: {
                    if (Character.isWhitespace(c)) {
                        source.next();
                        continue;
                    }
                    coma = false;
                    if (size++ == 0) {
                        item = this.contextForArray().parse(source);
                        continue;
                    }
                    if (list == null) {
                        list = new ArrayList();
                        list.add(item);
                        item = this.contextForArray().parse(source);
                        list.add(item);
                        item = null;
                        continue;
                    }
                    item = this.contextForArray().parse(source);
                    list.add(item);
                    item = null;
                    continue;
                }
            }
        }
        throw new IllegalStateException("unexpected end of array");
    }
    
    protected String parseString(final Source source) {
        if (source.next() != '\"') {
            throw new IllegalStateException();
        }
        boolean escape = false;
        StringBuffer b = null;
        final char[] scratch = source.scratchBuffer();
        if (scratch != null) {
            int i = 0;
            while (source.hasNext()) {
                if (i >= scratch.length) {
                    b = new StringBuffer(scratch.length * 2);
                    b.append(scratch, 0, i);
                    break;
                }
                final char c = source.next();
                if (escape) {
                    escape = false;
                    switch (c) {
                        case '\"': {
                            scratch[i++] = '\"';
                            continue;
                        }
                        case '\\': {
                            scratch[i++] = '\\';
                            continue;
                        }
                        case '/': {
                            scratch[i++] = '/';
                            continue;
                        }
                        case 'b': {
                            scratch[i++] = '\b';
                            continue;
                        }
                        case 'f': {
                            scratch[i++] = '\f';
                            continue;
                        }
                        case 'n': {
                            scratch[i++] = '\n';
                            continue;
                        }
                        case 'r': {
                            scratch[i++] = '\r';
                            continue;
                        }
                        case 't': {
                            scratch[i++] = '\t';
                            continue;
                        }
                        case 'u': {
                            final char uc = (char)((TypeUtil.convertHexDigit((byte)source.next()) << 12) + (TypeUtil.convertHexDigit((byte)source.next()) << 8) + (TypeUtil.convertHexDigit((byte)source.next()) << 4) + TypeUtil.convertHexDigit((byte)source.next()));
                            scratch[i++] = uc;
                            continue;
                        }
                        default: {
                            scratch[i++] = c;
                            continue;
                        }
                    }
                }
                else if (c == '\\') {
                    escape = true;
                }
                else {
                    if (c == '\"') {
                        return this.toString(scratch, 0, i);
                    }
                    scratch[i++] = c;
                }
            }
            if (b == null) {
                return this.toString(scratch, 0, i);
            }
        }
        else {
            b = new StringBuffer(this.getStringBufferSize());
        }
        synchronized (b) {
            while (source.hasNext()) {
                final char c = source.next();
                if (escape) {
                    escape = false;
                    switch (c) {
                        case '\"': {
                            b.append('\"');
                            continue;
                        }
                        case '\\': {
                            b.append('\\');
                            continue;
                        }
                        case '/': {
                            b.append('/');
                            continue;
                        }
                        case 'b': {
                            b.append('\b');
                            continue;
                        }
                        case 'f': {
                            b.append('\f');
                            continue;
                        }
                        case 'n': {
                            b.append('\n');
                            continue;
                        }
                        case 'r': {
                            b.append('\r');
                            continue;
                        }
                        case 't': {
                            b.append('\t');
                            continue;
                        }
                        case 'u': {
                            final char uc = (char)((TypeUtil.convertHexDigit((byte)source.next()) << 12) + (TypeUtil.convertHexDigit((byte)source.next()) << 8) + (TypeUtil.convertHexDigit((byte)source.next()) << 4) + TypeUtil.convertHexDigit((byte)source.next()));
                            b.append(uc);
                            continue;
                        }
                        default: {
                            b.append(c);
                            continue;
                        }
                    }
                }
                else if (c == '\\') {
                    escape = true;
                }
                else {
                    if (c == '\"') {
                        break;
                    }
                    b.append(c);
                }
            }
            return b.toString();
        }
    }
    
    public Number parseNumber(final Source source) {
        boolean minus = false;
        long number = 0L;
        StringBuffer buffer = null;
    Label_0381:
        while (source.hasNext()) {
            final char c = source.peek();
            switch (c) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    number = number * 10L + (c - '0');
                    source.next();
                    continue;
                }
                case '+':
                case '-': {
                    if (number != 0L) {
                        throw new IllegalStateException("bad number");
                    }
                    minus = true;
                    source.next();
                    continue;
                }
                case '.':
                case 'E':
                case 'e': {
                    buffer = new StringBuffer(16);
                    if (minus) {
                        buffer.append('-');
                    }
                    buffer.append(number);
                    buffer.append(c);
                    source.next();
                    break Label_0381;
                }
                default: {
                    break Label_0381;
                }
            }
        }
        if (buffer == null) {
            return TypeUtil.newLong(minus ? (-1L * number) : number);
        }
        synchronized (buffer) {
        Label_0704:
            while (source.hasNext()) {
                final char c2 = source.peek();
                switch (c2) {
                    case '+':
                    case '-':
                    case '.':
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                    case 'E':
                    case 'e': {
                        buffer.append(c2);
                        source.next();
                        continue;
                    }
                    default: {
                        break Label_0704;
                    }
                }
            }
            return new Double(buffer.toString());
        }
    }
    
    protected void seekTo(final char seek, final Source source) {
        while (source.hasNext()) {
            final char c = source.peek();
            if (c == seek) {
                return;
            }
            if (!Character.isWhitespace(c)) {
                throw new IllegalStateException("Unexpected '" + c + " while seeking '" + seek + "'");
            }
            source.next();
        }
        throw new IllegalStateException("Expected '" + seek + "'");
    }
    
    protected char seekTo(final String seek, final Source source) {
        while (source.hasNext()) {
            final char c = source.peek();
            if (seek.indexOf(c) >= 0) {
                return c;
            }
            if (!Character.isWhitespace(c)) {
                throw new IllegalStateException("Unexpected '" + c + "' while seeking one of '" + seek + "'");
            }
            source.next();
        }
        throw new IllegalStateException("Expected one of '" + seek + "'");
    }
    
    protected static void complete(final String seek, final Source source) {
        int i = 0;
        while (source.hasNext() && i < seek.length()) {
            final char c = source.next();
            if (c != seek.charAt(i++)) {
                throw new IllegalStateException("Unexpected '" + c + " while seeking  \"" + seek + "\"");
            }
        }
        if (i < seek.length()) {
            throw new IllegalStateException("Expected \"" + seek + "\"");
        }
    }
    
    static {
        JSON.__default = new JSON();
    }
    
    public static class StringSource implements Source
    {
        private final String string;
        private int index;
        private char[] scratch;
        
        public StringSource(final String s) {
            this.string = s;
        }
        
        public boolean hasNext() {
            if (this.index < this.string.length()) {
                return true;
            }
            this.scratch = null;
            return false;
        }
        
        public char next() {
            return this.string.charAt(this.index++);
        }
        
        public char peek() {
            return this.string.charAt(this.index);
        }
        
        public String toString() {
            return this.string.substring(0, this.index) + "|||" + this.string.substring(this.index);
        }
        
        public char[] scratchBuffer() {
            if (this.scratch == null) {
                this.scratch = new char[this.string.length()];
            }
            return this.scratch;
        }
    }
    
    public static class ReaderSource implements Source
    {
        private Reader _reader;
        private int _next;
        private char[] scratch;
        
        public ReaderSource(final Reader r) {
            this._next = -1;
            this._reader = r;
        }
        
        public void setReader(final Reader reader) {
            this._reader = reader;
            this._next = -1;
        }
        
        public boolean hasNext() {
            this.getNext();
            if (this._next < 0) {
                this.scratch = null;
                return false;
            }
            return true;
        }
        
        public char next() {
            this.getNext();
            final char c = (char)this._next;
            this._next = -1;
            return c;
        }
        
        public char peek() {
            this.getNext();
            return (char)this._next;
        }
        
        private void getNext() {
            if (this._next < 0) {
                try {
                    this._next = this._reader.read();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        
        public char[] scratchBuffer() {
            if (this.scratch == null) {
                this.scratch = new char[1024];
            }
            return this.scratch;
        }
    }
    
    public static class Literal implements Generator
    {
        private String _json;
        
        public Literal(final String json) {
            if (Log.isDebugEnabled()) {
                JSON.parse(json);
            }
            this._json = json;
        }
        
        public String toString() {
            return this._json;
        }
        
        public void addJSON(final StringBuffer buffer) {
            buffer.append(this._json);
        }
    }
    
    public interface Generator
    {
        void addJSON(final StringBuffer p0);
    }
    
    public interface Convertor
    {
        void toJSON(final Object p0, final Output p1);
        
        Object fromJSON(final Map p0);
    }
    
    public interface Output
    {
        void addClass(final Class p0);
        
        void add(final Object p0);
        
        void add(final String p0, final Object p1);
        
        void add(final String p0, final double p1);
        
        void add(final String p0, final long p1);
        
        void add(final String p0, final boolean p1);
    }
    
    public interface Convertible
    {
        void toJSON(final Output p0);
        
        void fromJSON(final Map p0);
    }
    
    public interface Source
    {
        boolean hasNext();
        
        char next();
        
        char peek();
        
        char[] scratchBuffer();
    }
}
