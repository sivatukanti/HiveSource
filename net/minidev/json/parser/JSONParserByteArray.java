// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.parser;

import net.minidev.json.writer.JsonReaderI;
import net.minidev.json.JSONValue;

class JSONParserByteArray extends JSONParserMemory
{
    private byte[] in;
    
    public JSONParserByteArray(final int permissiveMode) {
        super(permissiveMode);
    }
    
    public Object parse(final byte[] in) throws ParseException {
        return this.parse(in, JSONValue.defaultReader.DEFAULT);
    }
    
    public <T> T parse(final byte[] in, final JsonReaderI<T> mapper) throws ParseException {
        this.base = mapper.base;
        this.in = in;
        this.len = in.length;
        return this.parse(mapper);
    }
    
    @Override
    protected void extractString(final int beginIndex, final int endIndex) {
        this.xs = new String(this.in, beginIndex, endIndex - beginIndex);
    }
    
    @Override
    protected void extractStringTrim(int start, int stop) {
        final byte[] val = this.in;
        while (start < stop) {
            if (val[start] > 32) {
                break;
            }
            ++start;
        }
        while (start < stop && val[stop - 1] <= 32) {
            --stop;
        }
        this.xs = new String(this.in, start, stop - start);
    }
    
    @Override
    protected int indexOf(final char c, final int pos) {
        int i = pos;
        while (pos < this.len) {
            if (this.in[i] == (byte)c) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    @Override
    protected void read() {
        if (++this.pos >= this.len) {
            this.c = '\u001a';
        }
        else {
            this.c = (char)this.in[this.pos];
        }
    }
    
    protected void readS() {
        if (++this.pos >= this.len) {
            this.c = '\u001a';
        }
        else {
            this.c = (char)this.in[this.pos];
        }
    }
    
    @Override
    protected void readNoEnd() throws ParseException {
        if (++this.pos >= this.len) {
            this.c = '\u001a';
            throw new ParseException(this.pos - 1, 3, "EOF");
        }
        this.c = (char)this.in[this.pos];
    }
}
