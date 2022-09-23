// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.function.Predicate;
import java.util.Set;

public class IncludeExclude<ITEM> extends IncludeExcludeSet<ITEM, ITEM>
{
    public IncludeExclude() {
    }
    
    public <SET extends Set<ITEM>> IncludeExclude(final Class<SET> setClass) {
        super(setClass);
    }
    
    public <SET extends Set<ITEM>> IncludeExclude(final Set<ITEM> includeSet, final Predicate<ITEM> includePredicate, final Set<ITEM> excludeSet, final Predicate<ITEM> excludePredicate) {
        super(includeSet, includePredicate, excludeSet, excludePredicate);
    }
}
