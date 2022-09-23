// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.http.impl.cookie;

import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ParseException;
import java.util.List;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.NameValuePair;
import java.util.ArrayList;
import org.apache.http.util.Args;
import org.apache.http.HeaderElement;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.message.TokenParser;
import java.util.BitSet;
import org.apache.http.annotation.Immutable;

@Immutable
public class NetscapeDraftHeaderParser
{
    public static final NetscapeDraftHeaderParser DEFAULT;
    private static final char PARAM_DELIMITER = ';';
    private static final BitSet TOKEN_DELIMS;
    private static final BitSet VALUE_DELIMS;
    private final TokenParser tokenParser;
    
    public NetscapeDraftHeaderParser() {
        this.tokenParser = TokenParser.INSTANCE;
    }
    
    public HeaderElement parseHeader(final CharArrayBuffer buffer, final ParserCursor cursor) throws ParseException {
        Args.notNull(buffer, "Char array buffer");
        Args.notNull(cursor, "Parser cursor");
        final NameValuePair nvp = this.parseNameValuePair(buffer, cursor);
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        while (!cursor.atEnd()) {
            final NameValuePair param = this.parseNameValuePair(buffer, cursor);
            params.add(param);
        }
        return new BasicHeaderElement(nvp.getName(), nvp.getValue(), params.toArray(new NameValuePair[params.size()]));
    }
    
    private NameValuePair parseNameValuePair(final CharArrayBuffer buffer, final ParserCursor cursor) {
        final String name = this.tokenParser.parseToken(buffer, cursor, NetscapeDraftHeaderParser.TOKEN_DELIMS);
        if (cursor.atEnd()) {
            return new BasicNameValuePair(name, null);
        }
        final int delim = buffer.charAt(cursor.getPos());
        cursor.updatePos(cursor.getPos() + 1);
        if (delim != 61) {
            return new BasicNameValuePair(name, null);
        }
        final String value = this.tokenParser.parseToken(buffer, cursor, NetscapeDraftHeaderParser.VALUE_DELIMS);
        if (!cursor.atEnd()) {
            cursor.updatePos(cursor.getPos() + 1);
        }
        return new BasicNameValuePair(name, value);
    }
    
    static {
        DEFAULT = new NetscapeDraftHeaderParser();
        TOKEN_DELIMS = TokenParser.INIT_BITSET(61, 59);
        VALUE_DELIMS = TokenParser.INIT_BITSET(59);
    }
}
