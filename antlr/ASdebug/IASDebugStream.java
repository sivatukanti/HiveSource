// 
// Decompiled by Procyon v0.5.36
// 

package antlr.ASdebug;

import antlr.Token;

public interface IASDebugStream
{
    String getEntireText();
    
    TokenOffsetInfo getOffsetInfo(final Token p0);
}
