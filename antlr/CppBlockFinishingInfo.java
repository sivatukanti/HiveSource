// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class CppBlockFinishingInfo
{
    String postscript;
    boolean generatedSwitch;
    boolean generatedAnIf;
    boolean needAnErrorClause;
    
    public CppBlockFinishingInfo() {
        this.postscript = null;
        this.generatedSwitch = false;
        this.needAnErrorClause = true;
    }
    
    public CppBlockFinishingInfo(final String postscript, final boolean generatedSwitch, final boolean generatedAnIf, final boolean needAnErrorClause) {
        this.postscript = postscript;
        this.generatedSwitch = generatedSwitch;
        this.generatedAnIf = generatedAnIf;
        this.needAnErrorClause = needAnErrorClause;
    }
}
