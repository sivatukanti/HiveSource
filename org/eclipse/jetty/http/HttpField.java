// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import java.util.Objects;
import org.eclipse.jetty.util.StringUtil;

public class HttpField
{
    private static final String __zeroquality = "q=0";
    private final HttpHeader _header;
    private final String _name;
    private final String _value;
    private int hash;
    
    public HttpField(final HttpHeader header, final String name, final String value) {
        this.hash = 0;
        this._header = header;
        this._name = name;
        this._value = value;
    }
    
    public HttpField(final HttpHeader header, final String value) {
        this(header, header.asString(), value);
    }
    
    public HttpField(final HttpHeader header, final HttpHeaderValue value) {
        this(header, header.asString(), value.asString());
    }
    
    public HttpField(final String name, final String value) {
        this(HttpHeader.CACHE.get(name), name, value);
    }
    
    public HttpHeader getHeader() {
        return this._header;
    }
    
    public String getName() {
        return this._name;
    }
    
    public String getValue() {
        return this._value;
    }
    
    public int getIntValue() {
        return Integer.valueOf(this._value);
    }
    
    public long getLongValue() {
        return Long.valueOf(this._value);
    }
    
    public String[] getValues() {
        if (this._value == null) {
            return null;
        }
        final QuotedCSV list = new QuotedCSV(false, new String[] { this._value });
        return list.getValues().toArray(new String[list.size()]);
    }
    
    public boolean contains(String search) {
        if (search == null) {
            return this._value == null;
        }
        if (search.length() == 0) {
            return false;
        }
        if (this._value == null) {
            return false;
        }
        if (search.equals(this._value)) {
            return true;
        }
        search = StringUtil.asciiToLowerCase(search);
        int state = 0;
        int match = 0;
        int param = 0;
        for (int i = 0; i < this._value.length(); ++i) {
            final char c = this._value.charAt(i);
            switch (state) {
                case 0: {
                    switch (c) {
                        case '\"': {
                            match = 0;
                            state = 2;
                            continue;
                        }
                        case ',': {
                            continue;
                        }
                        case ';': {
                            param = -1;
                            match = -1;
                            state = 5;
                            continue;
                        }
                        case '\t':
                        case ' ': {
                            continue;
                        }
                        default: {
                            match = ((Character.toLowerCase(c) == search.charAt(0)) ? 1 : -1);
                            state = 1;
                            continue;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (c) {
                        case ',': {
                            if (match == search.length()) {
                                return true;
                            }
                            state = 0;
                            continue;
                        }
                        case ';': {
                            param = ((match >= 0) ? 0 : -1);
                            state = 5;
                            continue;
                        }
                        default: {
                            if (match <= 0) {
                                continue;
                            }
                            if (match < search.length()) {
                                match = ((Character.toLowerCase(c) == search.charAt(match)) ? (match + 1) : -1);
                                continue;
                            }
                            if (c != ' ' && c != '\t') {
                                match = -1;
                                continue;
                            }
                            continue;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (c) {
                        case '\\': {
                            state = 3;
                            continue;
                        }
                        case '\"': {
                            state = 4;
                            continue;
                        }
                        default: {
                            if (match < 0) {
                                continue;
                            }
                            if (match < search.length()) {
                                match = ((Character.toLowerCase(c) == search.charAt(match)) ? (match + 1) : -1);
                                continue;
                            }
                            match = -1;
                            continue;
                        }
                    }
                    break;
                }
                case 3: {
                    if (match >= 0) {
                        if (match < search.length()) {
                            match = ((Character.toLowerCase(c) == search.charAt(match)) ? (match + 1) : -1);
                        }
                        else {
                            match = -1;
                        }
                    }
                    state = 2;
                    break;
                }
                case 4: {
                    switch (c) {
                        case '\t':
                        case ' ': {
                            continue;
                        }
                        case ';': {
                            state = 5;
                            continue;
                        }
                        case ',': {
                            if (match == search.length()) {
                                return true;
                            }
                            state = 0;
                            continue;
                        }
                        default: {
                            match = -1;
                            continue;
                        }
                    }
                    break;
                }
                case 5: {
                    switch (c) {
                        case ',': {
                            if (param != "q=0".length() && match == search.length()) {
                                return true;
                            }
                            param = 0;
                            state = 0;
                            continue;
                        }
                        case '\t':
                        case ' ': {
                            continue;
                        }
                        default: {
                            if (param < 0) {
                                continue;
                            }
                            if (param < "q=0".length()) {
                                param = ((Character.toLowerCase(c) == "q=0".charAt(param)) ? (param + 1) : -1);
                                continue;
                            }
                            if (c != '0' && c != '.') {
                                param = -1;
                                continue;
                            }
                            continue;
                        }
                    }
                    break;
                }
                default: {
                    throw new IllegalStateException();
                }
            }
        }
        return param != "q=0".length() && match == search.length();
    }
    
    @Override
    public String toString() {
        final String v = this.getValue();
        return this.getName() + ": " + ((v == null) ? "" : v);
    }
    
    public boolean isSameName(final HttpField field) {
        return field != null && (field == this || (this._header != null && this._header == field.getHeader()) || this._name.equalsIgnoreCase(field.getName()));
    }
    
    private int nameHashCode() {
        int h = this.hash;
        final int len = this._name.length();
        if (h == 0 && len > 0) {
            for (int i = 0; i < len; ++i) {
                char c = this._name.charAt(i);
                if (c >= 'a' && c <= 'z') {
                    c -= ' ';
                }
                h = 31 * h + c;
            }
            this.hash = h;
        }
        return h;
    }
    
    @Override
    public int hashCode() {
        final int vhc = Objects.hashCode(this._value);
        if (this._header == null) {
            return vhc ^ this.nameHashCode();
        }
        return vhc ^ this._header.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof HttpField)) {
            return false;
        }
        final HttpField field = (HttpField)o;
        return this._header == field.getHeader() && this._name.equalsIgnoreCase(field.getName()) && (this._value != null || field.getValue() == null) && Objects.equals(this._value, field.getValue());
    }
    
    public static class IntValueHttpField extends HttpField
    {
        private final int _int;
        
        public IntValueHttpField(final HttpHeader header, final String name, final String value, final int intValue) {
            super(header, name, value);
            this._int = intValue;
        }
        
        public IntValueHttpField(final HttpHeader header, final String name, final String value) {
            this(header, name, value, Integer.valueOf(value));
        }
        
        public IntValueHttpField(final HttpHeader header, final String name, final int intValue) {
            this(header, name, Integer.toString(intValue), intValue);
        }
        
        public IntValueHttpField(final HttpHeader header, final int value) {
            this(header, header.asString(), value);
        }
        
        @Override
        public int getIntValue() {
            return this._int;
        }
        
        @Override
        public long getLongValue() {
            return this._int;
        }
    }
    
    public static class LongValueHttpField extends HttpField
    {
        private final long _long;
        
        public LongValueHttpField(final HttpHeader header, final String name, final String value, final long longValue) {
            super(header, name, value);
            this._long = longValue;
        }
        
        public LongValueHttpField(final HttpHeader header, final String name, final String value) {
            this(header, name, value, Long.valueOf(value));
        }
        
        public LongValueHttpField(final HttpHeader header, final String name, final long value) {
            this(header, name, Long.toString(value), value);
        }
        
        public LongValueHttpField(final HttpHeader header, final long value) {
            this(header, header.asString(), value);
        }
        
        @Override
        public int getIntValue() {
            return (int)this._long;
        }
        
        @Override
        public long getLongValue() {
            return this._long;
        }
    }
}
