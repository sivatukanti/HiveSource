// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.Vector;
import antlr.collections.impl.BitSet;

public class Lookahead implements Cloneable
{
    BitSet fset;
    String cycle;
    BitSet epsilonDepth;
    boolean hasEpsilon;
    
    public Lookahead() {
        this.hasEpsilon = false;
        this.fset = new BitSet();
    }
    
    public Lookahead(final BitSet fset) {
        this.hasEpsilon = false;
        this.fset = fset;
    }
    
    public Lookahead(final String cycle) {
        this();
        this.cycle = cycle;
    }
    
    public Object clone() {
        Lookahead lookahead;
        try {
            lookahead = (Lookahead)super.clone();
            lookahead.fset = (BitSet)this.fset.clone();
            lookahead.cycle = this.cycle;
            if (this.epsilonDepth != null) {
                lookahead.epsilonDepth = (BitSet)this.epsilonDepth.clone();
            }
        }
        catch (CloneNotSupportedException ex) {
            throw new InternalError();
        }
        return lookahead;
    }
    
    public void combineWith(final Lookahead lookahead) {
        if (this.cycle == null) {
            this.cycle = lookahead.cycle;
        }
        if (lookahead.containsEpsilon()) {
            this.hasEpsilon = true;
        }
        if (this.epsilonDepth != null) {
            if (lookahead.epsilonDepth != null) {
                this.epsilonDepth.orInPlace(lookahead.epsilonDepth);
            }
        }
        else if (lookahead.epsilonDepth != null) {
            this.epsilonDepth = (BitSet)lookahead.epsilonDepth.clone();
        }
        this.fset.orInPlace(lookahead.fset);
    }
    
    public boolean containsEpsilon() {
        return this.hasEpsilon;
    }
    
    public Lookahead intersection(final Lookahead lookahead) {
        final Lookahead lookahead2 = new Lookahead(this.fset.and(lookahead.fset));
        if (this.hasEpsilon && lookahead.hasEpsilon) {
            lookahead2.setEpsilon();
        }
        return lookahead2;
    }
    
    public boolean nil() {
        return this.fset.nil() && !this.hasEpsilon;
    }
    
    public static Lookahead of(final int n) {
        final Lookahead lookahead = new Lookahead();
        lookahead.fset.add(n);
        return lookahead;
    }
    
    public void resetEpsilon() {
        this.hasEpsilon = false;
    }
    
    public void setEpsilon() {
        this.hasEpsilon = true;
    }
    
    public String toString() {
        String str = "";
        String string = "";
        String string2 = "";
        final String string3 = this.fset.toString(",");
        if (this.containsEpsilon()) {
            str = "+<epsilon>";
        }
        if (this.cycle != null) {
            string = "; FOLLOW(" + this.cycle + ")";
        }
        if (this.epsilonDepth != null) {
            string2 = "; depths=" + this.epsilonDepth.toString(",");
        }
        return string3 + str + string + string2;
    }
    
    public String toString(final String s, final CharFormatter charFormatter) {
        String str = "";
        String string = "";
        String string2 = "";
        final String string3 = this.fset.toString(s, charFormatter);
        if (this.containsEpsilon()) {
            str = "+<epsilon>";
        }
        if (this.cycle != null) {
            string = "; FOLLOW(" + this.cycle + ")";
        }
        if (this.epsilonDepth != null) {
            string2 = "; depths=" + this.epsilonDepth.toString(",");
        }
        return string3 + str + string + string2;
    }
    
    public String toString(final String s, final CharFormatter charFormatter, final Grammar grammar) {
        if (grammar instanceof LexerGrammar) {
            return this.toString(s, charFormatter);
        }
        return this.toString(s, grammar.tokenManager.getVocabulary());
    }
    
    public String toString(final String s, final Vector vector) {
        String string = "";
        String string2 = "";
        final String string3 = this.fset.toString(s, vector);
        if (this.cycle != null) {
            string = "; FOLLOW(" + this.cycle + ")";
        }
        if (this.epsilonDepth != null) {
            string2 = "; depths=" + this.epsilonDepth.toString(",");
        }
        return string3 + string + string2;
    }
}
