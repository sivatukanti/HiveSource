// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.collections.functors;

import org.apache.commons.collections.Predicate;

public interface PredicateDecorator extends Predicate
{
    Predicate[] getPredicates();
}
