// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.parser;

import net.minidev.json.writer.JsonReaderI;
import net.minidev.json.JSONValue;

class JSONParserString extends JSONParserMemory
{
    private String in;
    
    public JSONParserString(final int permissiveMode) {
        super(permissiveMode);
    }
    
    public Object parse(final String in) throws ParseException {
        return this.parse(in, JSONValue.defaultReader.DEFAULT);
    }
    
    public <T> T parse(final String in, final JsonReaderI<T> mapper) throws ParseException {
        this.base = mapper.base;
        this.in = in;
        this.len = in.length();
        return this.parse(mapper);
    }
    
    @Override
    protected void extractString(final int beginIndex, final int endIndex) {
        this.xs = this.in.substring(beginIndex, endIndex);
    }
    
    @Override
    protected void extractStringTrim(int start, int stop) {
        while (start < stop - 1) {
            if (!Character.isWhitespace(this.in.charAt(start))) {
                break;
            }
            ++start;
        }
        while (stop - 1 > start && Character.isWhitespace(this.in.charAt(stop - 1))) {
            --stop;
        }
        this.extractString(start, stop);
    }
    
    @Override
    protected int indexOf(final char c, final int pos) {
        return this.in.indexOf(c, pos);
    }
    
    @Override
    protected void read() {
        if (++this.pos >= this.len) {
            this.c = '\u001a';
        }
        else {
            this.c = this.in.charAt(this.pos);
        }
    }
    
    protected void readS() {
        if (++this.pos >= this.len) {
            this.c = '\u001a';
        }
        else {
            this.c = this.in.charAt(this.pos);
        }
    }
    
    @Override
    protected void readNoEnd() throws ParseException {
        if (++this.pos >= this.len) {
            this.c = '\u001a';
            throw new ParseException(this.pos - 1, 3, "EOF");
        }
        this.c = this.in.charAt(this.pos);
    }
}
