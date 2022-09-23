// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.collections.set;

import java.util.Collection;
import java.util.Set;
import org.apache.commons.collections.collection.AbstractCollectionDecorator;

public abstract class AbstractSetDecorator extends AbstractCollectionDecorator implements Set
{
    protected AbstractSetDecorator() {
    }
    
    protected AbstractSetDecorator(final Set set) {
        super(set);
    }
    
    protected Set getSet() {
        return (Set)this.getCollection();
    }
}
