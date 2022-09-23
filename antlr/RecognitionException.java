// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class RecognitionException extends ANTLRException
{
    public String fileName;
    public int line;
    public int column;
    
    public RecognitionException() {
        super("parsing error");
        this.fileName = null;
        this.line = -1;
        this.column = -1;
    }
    
    public RecognitionException(final String s) {
        super(s);
        this.fileName = null;
        this.line = -1;
        this.column = -1;
    }
    
    public RecognitionException(final String s, final String s2, final int n) {
        this(s, s2, n, -1);
    }
    
    public RecognitionException(final String s, final String fileName, final int line, final int column) {
        super(s);
        this.fileName = fileName;
        this.line = line;
        this.column = column;
    }
    
    public String getFilename() {
        return this.fileName;
    }
    
    public int getLine() {
        return this.line;
    }
    
    public int getColumn() {
        return this.column;
    }
    
    public String getErrorMessage() {
        return this.getMessage();
    }
    
    public String toString() {
        return FileLineFormatter.getFormatter().getFormatString(this.fileName, this.line, this.column) + this.getMessage();
    }
}
