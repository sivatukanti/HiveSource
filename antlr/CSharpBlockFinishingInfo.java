// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class CSharpBlockFinishingInfo
{
    String postscript;
    boolean generatedSwitch;
    boolean generatedAnIf;
    boolean needAnErrorClause;
    
    public CSharpBlockFinishingInfo() {
        this.postscript = null;
        final boolean b = false;
        this.generatedSwitch = b;
        this.generatedSwitch = b;
        this.needAnErrorClause = true;
    }
    
    public CSharpBlockFinishingInfo(final String postscript, final boolean generatedSwitch, final boolean generatedAnIf, final boolean needAnErrorClause) {
        this.postscript = postscript;
        this.generatedSwitch = generatedSwitch;
        this.generatedAnIf = generatedAnIf;
        this.needAnErrorClause = needAnErrorClause;
    }
}
