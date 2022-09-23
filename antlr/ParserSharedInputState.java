// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class ParserSharedInputState
{
    protected TokenBuffer input;
    public int guessing;
    protected String filename;
    
    public ParserSharedInputState() {
        this.guessing = 0;
    }
    
    public void reset() {
        this.guessing = 0;
        this.filename = null;
        this.input.reset();
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public TokenBuffer getInput() {
        return this.input;
    }
}
