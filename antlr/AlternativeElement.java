// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

abstract class AlternativeElement extends GrammarElement
{
    AlternativeElement next;
    protected int autoGenType;
    protected String enclosingRuleName;
    
    public AlternativeElement(final Grammar grammar) {
        super(grammar);
        this.autoGenType = 1;
    }
    
    public AlternativeElement(final Grammar grammar, final Token token) {
        super(grammar, token);
        this.autoGenType = 1;
    }
    
    public AlternativeElement(final Grammar grammar, final Token token, final int autoGenType) {
        super(grammar, token);
        this.autoGenType = 1;
        this.autoGenType = autoGenType;
    }
    
    public int getAutoGenType() {
        return this.autoGenType;
    }
    
    public void setAutoGenType(final int autoGenType) {
        this.autoGenType = autoGenType;
    }
    
    public String getLabel() {
        return null;
    }
    
    public void setLabel(final String s) {
    }
}
