// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header.reader;

import java.text.ParseException;
import com.sun.jersey.core.header.GrammarUtil;

final class HttpHeaderReaderImpl extends HttpHeaderReader
{
    private String header;
    private boolean processComments;
    private int index;
    private int length;
    private Event event;
    private String value;
    
    public HttpHeaderReaderImpl(final String header, final boolean processComments) {
        this.header = ((header == null) ? "" : header);
        this.processComments = processComments;
        this.index = 0;
        this.length = this.header.length();
    }
    
    public HttpHeaderReaderImpl(final String header) {
        this(header, false);
    }
    
    @Override
    public boolean hasNext() {
        return this.skipWhiteSpace();
    }
    
    @Override
    public boolean hasNextSeparator(final char separator, final boolean skipWhiteSpace) {
        if (skipWhiteSpace) {
            this.skipWhiteSpace();
        }
        if (this.index >= this.length) {
            return false;
        }
        final char c = this.header.charAt(this.index);
        return GrammarUtil.TYPE_TABLE[c] == 3 && c == separator;
    }
    
    @Override
    public String nextSeparatedString(final char startSeparator, final char endSeparator) throws ParseException {
        this.nextSeparator(startSeparator);
        final int start = this.index;
        while (this.index < this.length && this.header.charAt(this.index) != endSeparator) {
            ++this.index;
        }
        if (start == this.index) {
            throw new ParseException("No characters between the separators '" + startSeparator + "' and '" + endSeparator + "'", this.index);
        }
        if (this.index == this.length) {
            throw new ParseException("No end separator '" + endSeparator + "'", this.index);
        }
        this.event = Event.Token;
        return this.value = this.header.substring(start, this.index++);
    }
    
    @Override
    public Event next() throws ParseException {
        return this.next(true);
    }
    
    @Override
    public Event next(final boolean skipWhiteSpace) throws ParseException {
        return this.next(skipWhiteSpace, false);
    }
    
    @Override
    public Event next(final boolean skipWhiteSpace, final boolean preserveBackslash) throws ParseException {
        return this.event = this.process(this.getNextCharacter(skipWhiteSpace), preserveBackslash);
    }
    
    @Override
    public Event getEvent() {
        return this.event;
    }
    
    @Override
    public String getEventValue() {
        return this.value;
    }
    
    @Override
    public String getRemainder() {
        return (this.index < this.length) ? this.header.substring(this.index) : null;
    }
    
    @Override
    public int getIndex() {
        return this.index;
    }
    
    private boolean skipWhiteSpace() {
        while (this.index < this.length) {
            if (!GrammarUtil.isWhiteSpace(this.header.charAt(this.index))) {
                return true;
            }
            ++this.index;
        }
        return false;
    }
    
    private char getNextCharacter(final boolean skipWhiteSpace) throws ParseException {
        if (skipWhiteSpace) {
            this.skipWhiteSpace();
        }
        if (this.index >= this.length) {
            throw new ParseException("End of header", this.index);
        }
        return this.header.charAt(this.index);
    }
    
    private Event process(final char c, final boolean preserveBackslash) throws ParseException {
        if (c > '\u007f') {
            ++this.index;
            return Event.Control;
        }
        switch (GrammarUtil.TYPE_TABLE[c]) {
            case 0: {
                final int start = this.index;
                ++this.index;
                while (this.index < this.length && GrammarUtil.isToken(this.header.charAt(this.index))) {
                    ++this.index;
                }
                this.value = this.header.substring(start, this.index);
                return Event.Token;
            }
            case 1: {
                this.processQuotedString(preserveBackslash);
                return Event.QuotedString;
            }
            case 2: {
                if (!this.processComments) {
                    throw new ParseException("Comments are not allowed", this.index);
                }
                this.processComment();
                return Event.Comment;
            }
            case 3: {
                ++this.index;
                this.value = String.valueOf(c);
                return Event.Separator;
            }
            case 4: {
                ++this.index;
                this.value = String.valueOf(c);
                return Event.Control;
            }
            default: {
                throw new ParseException("White space not allowed", this.index);
            }
        }
    }
    
    private void processComment() throws ParseException {
        boolean filter = false;
        final int start = ++this.index;
        int nesting = 1;
        while (nesting > 0 && this.index < this.length) {
            final char c = this.header.charAt(this.index);
            if (c == '\\') {
                ++this.index;
                filter = true;
            }
            else if (c == '\r') {
                filter = true;
            }
            else if (c == '(') {
                ++nesting;
            }
            else if (c == ')') {
                --nesting;
            }
            ++this.index;
        }
        if (nesting != 0) {
            throw new ParseException("Unbalanced comments", this.index);
        }
        this.value = (filter ? GrammarUtil.filterToken(this.header, start, this.index - 1) : this.header.substring(start, this.index - 1));
    }
    
    private void processQuotedString(final boolean preserveBackslash) throws ParseException {
        boolean filter = false;
        final int start = ++this.index;
        while (this.index < this.length) {
            final char c = this.header.charAt(this.index);
            if (!preserveBackslash && c == '\\') {
                ++this.index;
                filter = true;
            }
            else if (c == '\r') {
                filter = true;
            }
            else if (c == '\"') {
                this.value = (filter ? GrammarUtil.filterToken(this.header, start, this.index, preserveBackslash) : this.header.substring(start, this.index));
                ++this.index;
                return;
            }
            ++this.index;
        }
        throw new ParseException("Unbalanced quoted string", this.index);
    }
}
