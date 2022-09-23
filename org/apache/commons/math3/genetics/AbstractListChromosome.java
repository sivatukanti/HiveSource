// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.genetics;

import java.util.Arrays;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractListChromosome<T> extends Chromosome
{
    private final List<T> representation;
    
    public AbstractListChromosome(final List<T> representation) throws InvalidRepresentationException {
        this.checkValidity(representation);
        this.representation = Collections.unmodifiableList((List<? extends T>)new ArrayList<T>((Collection<? extends T>)representation));
    }
    
    public AbstractListChromosome(final T[] representation) throws InvalidRepresentationException {
        this(Arrays.asList(representation));
    }
    
    protected abstract void checkValidity(final List<T> p0) throws InvalidRepresentationException;
    
    protected List<T> getRepresentation() {
        return this.representation;
    }
    
    public int getLength() {
        return this.getRepresentation().size();
    }
    
    public abstract AbstractListChromosome<T> newFixedLengthChromosome(final List<T> p0);
    
    @Override
    public String toString() {
        return String.format("(f=%s %s)", this.getFitness(), this.getRepresentation());
    }
}
