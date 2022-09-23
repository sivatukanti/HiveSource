// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.optim.linear;

import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.math3.optim.OptimizationData;

public class LinearConstraintSet implements OptimizationData
{
    private final Set<LinearConstraint> linearConstraints;
    
    public LinearConstraintSet(final LinearConstraint... constraints) {
        this.linearConstraints = new HashSet<LinearConstraint>();
        for (final LinearConstraint c : constraints) {
            this.linearConstraints.add(c);
        }
    }
    
    public LinearConstraintSet(final Collection<LinearConstraint> constraints) {
        (this.linearConstraints = new HashSet<LinearConstraint>()).addAll(constraints);
    }
    
    public Collection<LinearConstraint> getConstraints() {
        return (Collection<LinearConstraint>)Collections.unmodifiableSet((Set<?>)this.linearConstraints);
    }
}
