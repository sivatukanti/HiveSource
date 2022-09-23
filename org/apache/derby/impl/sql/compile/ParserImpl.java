// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import java.io.Reader;
import java.io.StringReader;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.Visitable;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.sql.compile.Parser;

public class ParserImpl implements Parser
{
    static final int LARGE_TOKEN_SIZE = 128;
    private SQLParser cachedParser;
    protected Object cachedTokenManager;
    protected CharStream charStream;
    protected String SQLtext;
    protected final CompilerContext cc;
    
    public ParserImpl(final CompilerContext cc) {
        this.cc = cc;
    }
    
    public Visitable parseStatement(final String s) throws StandardException {
        return this.parseStatement(s, null);
    }
    
    protected Object getTokenManager() {
        SQLParserTokenManager cachedTokenManager = (SQLParserTokenManager)this.cachedTokenManager;
        if (cachedTokenManager == null) {
            cachedTokenManager = new SQLParserTokenManager(this.charStream);
            this.cachedTokenManager = cachedTokenManager;
        }
        else {
            cachedTokenManager.ReInit(this.charStream);
        }
        return cachedTokenManager;
    }
    
    private SQLParser getParser() {
        final SQLParserTokenManager sqlParserTokenManager = (SQLParserTokenManager)this.getTokenManager();
        SQLParser cachedParser = this.cachedParser;
        if (cachedParser == null) {
            cachedParser = new SQLParser(sqlParserTokenManager);
            cachedParser.setCompilerContext(this.cc);
            this.cachedParser = cachedParser;
        }
        else {
            cachedParser.ReInit(sqlParserTokenManager);
        }
        return cachedParser;
    }
    
    public Visitable parseStatement(final String s, final Object[] array) throws StandardException {
        final StringReader stringReader = new StringReader(s);
        if (this.charStream == null) {
            this.charStream = new UCode_CharStream(stringReader, 1, 1, 128);
        }
        else {
            this.charStream.ReInit(stringReader, 1, 1, 128);
        }
        this.SQLtext = s;
        try {
            return this.getParser().Statement(s, array);
        }
        catch (ParseException ex) {
            throw StandardException.newException("42X01", ex.getMessage());
        }
        catch (TokenMgrError tokenMgrError) {
            this.cachedParser = null;
            throw StandardException.newException("42X02", tokenMgrError.getMessage());
        }
    }
    
    public String getSQLtext() {
        return this.SQLtext;
    }
}
