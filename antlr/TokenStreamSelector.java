// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.ASdebug.TokenOffsetInfo;
import antlr.ASdebug.ASDebugStream;
import antlr.collections.impl.LList;
import antlr.collections.Stack;
import java.util.Hashtable;
import antlr.ASdebug.IASDebugStream;

public class TokenStreamSelector implements TokenStream, IASDebugStream
{
    protected Hashtable inputStreamNames;
    protected TokenStream input;
    protected Stack streamStack;
    
    public TokenStreamSelector() {
        this.streamStack = new LList();
        this.inputStreamNames = new Hashtable();
    }
    
    public void addInputStream(final TokenStream value, final String key) {
        this.inputStreamNames.put(key, value);
    }
    
    public TokenStream getCurrentStream() {
        return this.input;
    }
    
    public TokenStream getStream(final String s) {
        final TokenStream tokenStream = this.inputStreamNames.get(s);
        if (tokenStream == null) {
            throw new IllegalArgumentException("TokenStream " + s + " not found");
        }
        return tokenStream;
    }
    
    public Token nextToken() throws TokenStreamException {
        try {
            return this.input.nextToken();
        }
        catch (TokenStreamRetryException ex) {
            return this.input.nextToken();
        }
    }
    
    public TokenStream pop() {
        final TokenStream tokenStream = (TokenStream)this.streamStack.pop();
        this.select(tokenStream);
        return tokenStream;
    }
    
    public void push(final TokenStream tokenStream) {
        this.streamStack.push(this.input);
        this.select(tokenStream);
    }
    
    public void push(final String s) {
        this.streamStack.push(this.input);
        this.select(s);
    }
    
    public void retry() throws TokenStreamRetryException {
        throw new TokenStreamRetryException();
    }
    
    public void select(final TokenStream input) {
        this.input = input;
    }
    
    public void select(final String s) throws IllegalArgumentException {
        this.input = this.getStream(s);
    }
    
    public String getEntireText() {
        return ASDebugStream.getEntireText(this.input);
    }
    
    public TokenOffsetInfo getOffsetInfo(final Token token) {
        return ASDebugStream.getOffsetInfo(this.input, token);
    }
}
