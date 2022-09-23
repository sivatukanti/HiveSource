// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.genetics;

public class ChromosomePair
{
    private final Chromosome first;
    private final Chromosome second;
    
    public ChromosomePair(final Chromosome c1, final Chromosome c2) {
        this.first = c1;
        this.second = c2;
    }
    
    public Chromosome getFirst() {
        return this.first;
    }
    
    public Chromosome getSecond() {
        return this.second;
    }
    
    @Override
    public String toString() {
        return String.format("(%s,%s)", this.getFirst(), this.getSecond());
    }
}
