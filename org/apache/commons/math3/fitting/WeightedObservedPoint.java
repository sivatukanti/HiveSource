// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.fitting;

import java.io.Serializable;

public class WeightedObservedPoint implements Serializable
{
    private static final long serialVersionUID = 5306874947404636157L;
    private final double weight;
    private final double x;
    private final double y;
    
    public WeightedObservedPoint(final double weight, final double x, final double y) {
        this.weight = weight;
        this.x = x;
        this.y = y;
    }
    
    public double getWeight() {
        return this.weight;
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
}
