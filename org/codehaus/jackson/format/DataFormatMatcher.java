// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.format;

import org.codehaus.jackson.io.IOContext;
import org.codehaus.jackson.io.MergedStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonFactory;
import java.io.InputStream;

public class DataFormatMatcher
{
    protected final InputStream _originalStream;
    protected final byte[] _bufferedData;
    protected final int _bufferedLength;
    protected final JsonFactory _match;
    protected final MatchStrength _matchStrength;
    
    protected DataFormatMatcher(final InputStream in, final byte[] buffered, final int bufferedLength, final JsonFactory match, final MatchStrength strength) {
        this._originalStream = in;
        this._bufferedData = buffered;
        this._bufferedLength = bufferedLength;
        this._match = match;
        this._matchStrength = strength;
    }
    
    public boolean hasMatch() {
        return this._match != null;
    }
    
    public MatchStrength getMatchStrength() {
        return (this._matchStrength == null) ? MatchStrength.INCONCLUSIVE : this._matchStrength;
    }
    
    public JsonFactory getMatch() {
        return this._match;
    }
    
    public String getMatchedFormatName() {
        return this._match.getFormatName();
    }
    
    public JsonParser createParserWithMatch() throws IOException {
        if (this._match == null) {
            return null;
        }
        if (this._originalStream == null) {
            return this._match.createJsonParser(this._bufferedData, 0, this._bufferedLength);
        }
        return this._match.createJsonParser(this.getDataStream());
    }
    
    public InputStream getDataStream() {
        if (this._originalStream == null) {
            return new ByteArrayInputStream(this._bufferedData, 0, this._bufferedLength);
        }
        return new MergedStream(null, this._originalStream, this._bufferedData, 0, this._bufferedLength);
    }
}
