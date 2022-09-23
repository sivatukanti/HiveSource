// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.Vector;

class RuleSymbol extends GrammarSymbol
{
    RuleBlock block;
    boolean defined;
    Vector references;
    String access;
    String comment;
    
    public RuleSymbol(final String s) {
        super(s);
        this.references = new Vector();
    }
    
    public void addReference(final RuleRefElement ruleRefElement) {
        this.references.appendElement(ruleRefElement);
    }
    
    public RuleBlock getBlock() {
        return this.block;
    }
    
    public RuleRefElement getReference(final int n) {
        return (RuleRefElement)this.references.elementAt(n);
    }
    
    public boolean isDefined() {
        return this.defined;
    }
    
    public int numReferences() {
        return this.references.size();
    }
    
    public void setBlock(final RuleBlock block) {
        this.block = block;
    }
    
    public void setDefined() {
        this.defined = true;
    }
}
