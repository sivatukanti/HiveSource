// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class Alternative
{
    AlternativeElement head;
    AlternativeElement tail;
    protected SynPredBlock synPred;
    protected String semPred;
    protected ExceptionSpec exceptionSpec;
    protected Lookahead[] cache;
    protected int lookaheadDepth;
    protected Token treeSpecifier;
    private boolean doAutoGen;
    
    public Alternative() {
        this.treeSpecifier = null;
    }
    
    public Alternative(final AlternativeElement alternativeElement) {
        this.treeSpecifier = null;
        this.addElement(alternativeElement);
    }
    
    public void addElement(final AlternativeElement alternativeElement) {
        if (this.head == null) {
            this.tail = alternativeElement;
            this.head = alternativeElement;
        }
        else {
            this.tail.next = alternativeElement;
            this.tail = alternativeElement;
        }
    }
    
    public boolean atStart() {
        return this.head == null;
    }
    
    public boolean getAutoGen() {
        return this.doAutoGen && this.treeSpecifier == null;
    }
    
    public Token getTreeSpecifier() {
        return this.treeSpecifier;
    }
    
    public void setAutoGen(final boolean doAutoGen) {
        this.doAutoGen = doAutoGen;
    }
}
