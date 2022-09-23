// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.collections;

import java.util.Iterator;

public interface OrderedIterator extends Iterator
{
    boolean hasPrevious();
    
    Object previous();
}
