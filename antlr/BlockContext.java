// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class BlockContext
{
    AlternativeBlock block;
    int altNum;
    BlockEndElement blockEnd;
    
    public void addAlternativeElement(final AlternativeElement alternativeElement) {
        this.currentAlt().addElement(alternativeElement);
    }
    
    public Alternative currentAlt() {
        return (Alternative)this.block.alternatives.elementAt(this.altNum);
    }
    
    public AlternativeElement currentElement() {
        return this.currentAlt().tail;
    }
}
