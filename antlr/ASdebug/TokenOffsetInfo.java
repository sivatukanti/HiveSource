// 
// Decompiled by Procyon v0.5.36
// 

package antlr.ASdebug;

public class TokenOffsetInfo
{
    public final int beginOffset;
    public final int length;
    
    public TokenOffsetInfo(final int beginOffset, final int length) {
        this.beginOffset = beginOffset;
        this.length = length;
    }
    
    public int getEndOffset() {
        return this.beginOffset + this.length - 1;
    }
}
