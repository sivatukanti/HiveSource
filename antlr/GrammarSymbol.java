// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

abstract class GrammarSymbol
{
    protected String id;
    
    public GrammarSymbol() {
    }
    
    public GrammarSymbol(final String id) {
        this.id = id;
    }
    
    public String getId() {
        return this.id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
}
