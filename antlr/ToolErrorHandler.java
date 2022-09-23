// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

interface ToolErrorHandler
{
    void warnAltAmbiguity(final Grammar p0, final AlternativeBlock p1, final boolean p2, final int p3, final Lookahead[] p4, final int p5, final int p6);
    
    void warnAltExitAmbiguity(final Grammar p0, final BlockWithImpliedExitPath p1, final boolean p2, final int p3, final Lookahead[] p4, final int p5);
}
