// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.selectors.modifiedselector;

import java.util.Comparator;

public class EqualComparator implements Comparator<Object>
{
    public int compare(final Object o1, final Object o2) {
        if (o1 != null) {
            return o1.equals(o2) ? 0 : 1;
        }
        if (o2 == null) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return "EqualComparator";
    }
}
