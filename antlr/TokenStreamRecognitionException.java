// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class TokenStreamRecognitionException extends TokenStreamException
{
    public RecognitionException recog;
    
    public TokenStreamRecognitionException(final RecognitionException recog) {
        super(recog.getMessage());
        this.recog = recog;
    }
    
    public String toString() {
        return this.recog.toString();
    }
}
