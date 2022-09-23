// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.parser;

import java.io.IOException;
import net.minidev.json.writer.JsonReaderI;
import net.minidev.json.JSONValue;
import java.io.Reader;

class JSONParserReader extends JSONParserStream
{
    private Reader in;
    
    public JSONParserReader(final int permissiveMode) {
        super(permissiveMode);
    }
    
    public Object parse(final Reader in) throws ParseException {
        return this.parse(in, JSONValue.defaultReader.DEFAULT);
    }
    
    public <T> T parse(final Reader in, final JsonReaderI<T> mapper) throws ParseException {
        this.base = mapper.base;
        this.in = in;
        return super.parse(mapper);
    }
    
    @Override
    protected void read() throws IOException {
        final int i = this.in.read();
        this.c = ((i == -1) ? '\u001a' : ((char)i));
        ++this.pos;
    }
    
    protected void readS() throws IOException {
        this.sb.append(this.c);
        final int i = this.in.read();
        if (i == -1) {
            this.c = '\u001a';
        }
        else {
            this.c = (char)i;
            ++this.pos;
        }
    }
    
    @Override
    protected void readNoEnd() throws ParseException, IOException {
        final int i = this.in.read();
        if (i == -1) {
            throw new ParseException(this.pos - 1, 3, "EOF");
        }
        this.c = (char)i;
    }
}
