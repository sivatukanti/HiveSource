// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.genetics;

import org.apache.commons.math3.random.RandomGenerator;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class UniformCrossover<T> implements CrossoverPolicy
{
    private final double ratio;
    
    public UniformCrossover(final double ratio) throws OutOfRangeException {
        if (ratio < 0.0 || ratio > 1.0) {
            throw new OutOfRangeException(LocalizedFormats.CROSSOVER_RATE, ratio, 0.0, 1.0);
        }
        this.ratio = ratio;
    }
    
    public double getRatio() {
        return this.ratio;
    }
    
    public ChromosomePair crossover(final Chromosome first, final Chromosome second) throws DimensionMismatchException, MathIllegalArgumentException {
        if (!(first instanceof AbstractListChromosome) || !(second instanceof AbstractListChromosome)) {
            throw new MathIllegalArgumentException(LocalizedFormats.INVALID_FIXED_LENGTH_CHROMOSOME, new Object[0]);
        }
        return this.mate((AbstractListChromosome<T>)first, (AbstractListChromosome<T>)second);
    }
    
    private ChromosomePair mate(final AbstractListChromosome<T> first, final AbstractListChromosome<T> second) throws DimensionMismatchException {
        final int length = first.getLength();
        if (length != second.getLength()) {
            throw new DimensionMismatchException(second.getLength(), length);
        }
        final List<T> parent1Rep = first.getRepresentation();
        final List<T> parent2Rep = second.getRepresentation();
        final List<T> child1Rep = new ArrayList<T>(first.getLength());
        final List<T> child2Rep = new ArrayList<T>(second.getLength());
        final RandomGenerator random = GeneticAlgorithm.getRandomGenerator();
        for (int index = 0; index < length; ++index) {
            if (random.nextDouble() < this.ratio) {
                child1Rep.add(parent2Rep.get(index));
                child2Rep.add(parent1Rep.get(index));
            }
            else {
                child1Rep.add(parent1Rep.get(index));
                child2Rep.add(parent2Rep.get(index));
            }
        }
        return new ChromosomePair(first.newFixedLengthChromosome(child1Rep), second.newFixedLengthChromosome(child2Rep));
    }
}
