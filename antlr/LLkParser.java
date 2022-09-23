// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class LLkParser extends Parser
{
    int k;
    
    public LLkParser(final int k) {
        this.k = k;
    }
    
    public LLkParser(final ParserSharedInputState parserSharedInputState, final int k) {
        super(parserSharedInputState);
        this.k = k;
    }
    
    public LLkParser(final TokenBuffer tokenBuffer, final int k) {
        this.k = k;
        this.setTokenBuffer(tokenBuffer);
    }
    
    public LLkParser(final TokenStream tokenStream, final int k) {
        this.k = k;
        this.setTokenBuffer(new TokenBuffer(tokenStream));
    }
    
    public void consume() throws TokenStreamException {
        this.inputState.input.consume();
    }
    
    public int LA(final int n) throws TokenStreamException {
        return this.inputState.input.LA(n);
    }
    
    public Token LT(final int n) throws TokenStreamException {
        return this.inputState.input.LT(n);
    }
    
    private void trace(final String str, final String str2) throws TokenStreamException {
        this.traceIndent();
        System.out.print(str + str2 + ((this.inputState.guessing > 0) ? "; [guessing]" : "; "));
        for (int i = 1; i <= this.k; ++i) {
            if (i != 1) {
                System.out.print(", ");
            }
            if (this.LT(i) != null) {
                System.out.print("LA(" + i + ")==" + this.LT(i).getText());
            }
            else {
                System.out.print("LA(" + i + ")==null");
            }
        }
        System.out.println("");
    }
    
    public void traceIn(final String s) throws TokenStreamException {
        ++this.traceDepth;
        this.trace("> ", s);
    }
    
    public void traceOut(final String s) throws TokenStreamException {
        this.trace("< ", s);
        --this.traceDepth;
    }
}
