// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.io.Reader;
import java.io.InputStream;

public class LexerSharedInputState
{
    protected int column;
    protected int line;
    protected int tokenStartColumn;
    protected int tokenStartLine;
    protected InputBuffer input;
    protected String filename;
    public int guessing;
    
    public LexerSharedInputState(final InputBuffer input) {
        this.column = 1;
        this.line = 1;
        this.tokenStartColumn = 1;
        this.tokenStartLine = 1;
        this.guessing = 0;
        this.input = input;
    }
    
    public LexerSharedInputState(final InputStream inputStream) {
        this(new ByteBuffer(inputStream));
    }
    
    public LexerSharedInputState(final Reader reader) {
        this(new CharBuffer(reader));
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public InputBuffer getInput() {
        return this.input;
    }
    
    public int getLine() {
        return this.line;
    }
    
    public int getTokenStartColumn() {
        return this.tokenStartColumn;
    }
    
    public int getTokenStartLine() {
        return this.tokenStartLine;
    }
    
    public int getColumn() {
        return this.column;
    }
    
    public void reset() {
        this.column = 1;
        this.line = 1;
        this.tokenStartColumn = 1;
        this.tokenStartLine = 1;
        this.guessing = 0;
        this.filename = null;
        this.input.reset();
    }
}
