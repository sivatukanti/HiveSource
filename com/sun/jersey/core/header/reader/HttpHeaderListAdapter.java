// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header.reader;

import java.text.ParseException;

class HttpHeaderListAdapter extends HttpHeaderReader
{
    private HttpHeaderReader reader;
    boolean isTerminated;
    
    public HttpHeaderListAdapter(final HttpHeaderReader reader) {
        this.reader = reader;
    }
    
    public void reset() {
        this.isTerminated = false;
    }
    
    @Override
    public boolean hasNext() {
        if (this.isTerminated) {
            return false;
        }
        if (!this.reader.hasNext()) {
            return false;
        }
        if (this.reader.hasNextSeparator(',', true)) {
            this.isTerminated = true;
            return false;
        }
        return true;
    }
    
    @Override
    public boolean hasNextSeparator(final char separator, final boolean skipWhiteSpace) {
        if (this.isTerminated) {
            return false;
        }
        if (this.reader.hasNextSeparator(',', skipWhiteSpace)) {
            this.isTerminated = true;
            return false;
        }
        return this.reader.hasNextSeparator(separator, skipWhiteSpace);
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
        if (this.isTerminated) {
            throw new ParseException("End of header", this.getIndex());
        }
        if (this.reader.hasNextSeparator(',', skipWhiteSpace)) {
            this.isTerminated = true;
            throw new ParseException("End of header", this.getIndex());
        }
        return this.reader.next(skipWhiteSpace, preserveBackslash);
    }
    
    @Override
    public String nextSeparatedString(final char startSeparator, final char endSeparator) throws ParseException {
        if (this.isTerminated) {
            throw new ParseException("End of header", this.getIndex());
        }
        if (this.reader.hasNextSeparator(',', true)) {
            this.isTerminated = true;
            throw new ParseException("End of header", this.getIndex());
        }
        return this.reader.nextSeparatedString(startSeparator, endSeparator);
    }
    
    @Override
    public Event getEvent() {
        return this.reader.getEvent();
    }
    
    @Override
    public String getEventValue() {
        return this.reader.getEventValue();
    }
    
    @Override
    public String getRemainder() {
        return this.reader.getRemainder();
    }
    
    @Override
    public int getIndex() {
        return this.reader.getIndex();
    }
}
