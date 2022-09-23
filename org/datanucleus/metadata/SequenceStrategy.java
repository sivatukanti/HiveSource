// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

public enum SequenceStrategy
{
    NONTRANSACTIONAL("nontransactional"), 
    CONTIGUOUS("contiguous"), 
    NONCONTIGUOUS("noncontiguous");
    
    String name;
    
    private SequenceStrategy(final String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    public static SequenceStrategy getStrategy(final String value) {
        if (value == null) {
            return null;
        }
        if (SequenceStrategy.NONTRANSACTIONAL.toString().equalsIgnoreCase(value)) {
            return SequenceStrategy.NONTRANSACTIONAL;
        }
        if (SequenceStrategy.CONTIGUOUS.toString().equalsIgnoreCase(value)) {
            return SequenceStrategy.CONTIGUOUS;
        }
        if (SequenceStrategy.NONCONTIGUOUS.toString().equalsIgnoreCase(value)) {
            return SequenceStrategy.NONCONTIGUOUS;
        }
        return null;
    }
}
