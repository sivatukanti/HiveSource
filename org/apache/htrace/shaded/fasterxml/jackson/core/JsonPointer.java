// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.core;

import org.apache.htrace.shaded.fasterxml.jackson.core.io.NumberInput;

public class JsonPointer
{
    protected static final JsonPointer EMPTY;
    protected final JsonPointer _nextSegment;
    protected final String _asString;
    protected final String _matchingPropertyName;
    protected final int _matchingElementIndex;
    
    protected JsonPointer() {
        this._nextSegment = null;
        this._matchingPropertyName = "";
        this._matchingElementIndex = -1;
        this._asString = "";
    }
    
    protected JsonPointer(final String fullString, final String segment, final JsonPointer next) {
        this._asString = fullString;
        this._nextSegment = next;
        this._matchingPropertyName = segment;
        this._matchingElementIndex = _parseIndex(segment);
    }
    
    public static JsonPointer compile(final String input) throws IllegalArgumentException {
        if (input == null || input.length() == 0) {
            return JsonPointer.EMPTY;
        }
        if (input.charAt(0) != '/') {
            throw new IllegalArgumentException("Invalid input: JSON Pointer expression must start with '/': \"" + input + "\"");
        }
        return _parseTail(input);
    }
    
    public static JsonPointer valueOf(final String input) {
        return compile(input);
    }
    
    public boolean matches() {
        return this._nextSegment == null;
    }
    
    public String getMatchingProperty() {
        return this._matchingPropertyName;
    }
    
    public int getMatchingIndex() {
        return this._matchingElementIndex;
    }
    
    public boolean mayMatchProperty() {
        return this._matchingPropertyName != null;
    }
    
    public boolean mayMatchElement() {
        return this._matchingElementIndex >= 0;
    }
    
    public JsonPointer matchProperty(final String name) {
        if (this._nextSegment == null || !this._matchingPropertyName.equals(name)) {
            return null;
        }
        return this._nextSegment;
    }
    
    public JsonPointer matchElement(final int index) {
        if (index != this._matchingElementIndex || index < 0) {
            return null;
        }
        return this._nextSegment;
    }
    
    public JsonPointer tail() {
        return this._nextSegment;
    }
    
    @Override
    public String toString() {
        return this._asString;
    }
    
    @Override
    public int hashCode() {
        return this._asString.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o instanceof JsonPointer && this._asString.equals(((JsonPointer)o)._asString));
    }
    
    private static final int _parseIndex(final String str) {
        final int len = str.length();
        if (len == 0 || len > 10) {
            return -1;
        }
        for (int i = 0; i < len; ++i) {
            final char c = str.charAt(i++);
            if (c > '9' || c < '0') {
                return -1;
            }
        }
        if (len == 10) {
            final long l = NumberInput.parseLong(str);
            if (l > 2147483647L) {
                return -1;
            }
        }
        return NumberInput.parseInt(str);
    }
    
    protected static JsonPointer _parseTail(final String input) {
        final int end = input.length();
        int i = 1;
        while (i < end) {
            final char c = input.charAt(i);
            if (c == '/') {
                return new JsonPointer(input, input.substring(1, i), _parseTail(input.substring(i)));
            }
            ++i;
            if (c == '~' && i < end) {
                return _parseQuotedTail(input, i);
            }
        }
        return new JsonPointer(input, input.substring(1), JsonPointer.EMPTY);
    }
    
    protected static JsonPointer _parseQuotedTail(final String input, int i) {
        final int end = input.length();
        final StringBuilder sb = new StringBuilder(Math.max(16, end));
        if (i > 2) {
            sb.append(input, 1, i - 1);
        }
        _appendEscape(sb, input.charAt(i++));
        while (i < end) {
            final char c = input.charAt(i);
            if (c == '/') {
                return new JsonPointer(input, sb.toString(), _parseTail(input.substring(i)));
            }
            ++i;
            if (c == '~' && i < end) {
                _appendEscape(sb, input.charAt(i++));
            }
            else {
                sb.append(c);
            }
        }
        return new JsonPointer(input, sb.toString(), JsonPointer.EMPTY);
    }
    
    private static void _appendEscape(final StringBuilder sb, char c) {
        if (c == '0') {
            c = '~';
        }
        else if (c == '1') {
            c = '/';
        }
        else {
            sb.append('~');
        }
        sb.append(c);
    }
    
    static {
        EMPTY = new JsonPointer();
    }
}
