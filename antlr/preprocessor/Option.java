// 
// Decompiled by Procyon v0.5.36
// 

package antlr.preprocessor;

class Option
{
    protected String name;
    protected String rhs;
    protected Grammar enclosingGrammar;
    
    public Option(final String name, final String rhs, final Grammar enclosingGrammar) {
        this.name = name;
        this.rhs = rhs;
        this.setEnclosingGrammar(enclosingGrammar);
    }
    
    public Grammar getEnclosingGrammar() {
        return this.enclosingGrammar;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getRHS() {
        return this.rhs;
    }
    
    public void setEnclosingGrammar(final Grammar enclosingGrammar) {
        this.enclosingGrammar = enclosingGrammar;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setRHS(final String rhs) {
        this.rhs = rhs;
    }
    
    public String toString() {
        return "\t" + this.name + "=" + this.rhs;
    }
}
