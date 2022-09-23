// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.parser;

import net.minidev.json.writer.JsonReaderI;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;

class JSONParserInputStream extends JSONParserReader
{
    public JSONParserInputStream(final int permissiveMode) {
        super(permissiveMode);
    }
    
    public Object parse(final InputStream in) throws ParseException, UnsupportedEncodingException {
        final InputStreamReader i2 = new InputStreamReader(in, "utf8");
        return super.parse(i2);
    }
    
    public <T> T parse(final InputStream in, final JsonReaderI<T> mapper) throws ParseException, UnsupportedEncodingException {
        final InputStreamReader i2 = new InputStreamReader(in, "utf8");
        return super.parse(i2, mapper);
    }
}
