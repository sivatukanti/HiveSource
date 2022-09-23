// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.model;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Comparator;
import com.sun.jersey.server.impl.uri.PathPattern;
import java.util.TreeMap;

public class RulesMap<R> extends TreeMap<PathPattern, R>
{
    public RulesMap() {
        super(PathPattern.COMPARATOR);
    }
    
    public PathPattern hasConflict(final PathPattern p) {
        for (final PathPattern cp : this.keySet()) {
            if (cp.equals(p)) {
                return cp;
            }
        }
        return null;
    }
    
    public void processConflicts(final ConflictClosure cc) {
        final List<PathPattern> ks = new ArrayList<PathPattern>(this.keySet());
        for (int i = 0; i < ks.size(); ++i) {
            final PathPattern p1 = ks.get(i);
            for (int j = i + 1; j < ks.size(); ++j) {
                final PathPattern p2 = ks.get(j);
                if (p1.equals(p2)) {
                    cc.onConflict(p1, p2);
                }
            }
        }
    }
    
    public interface ConflictClosure
    {
        void onConflict(final PathPattern p0, final PathPattern p1);
    }
}
