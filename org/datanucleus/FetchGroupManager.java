// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import org.datanucleus.util.MultiMap;

public class FetchGroupManager
{
    private MultiMap fetchGroupByName;
    private NucleusContext nucleusCtx;
    
    public FetchGroupManager(final NucleusContext ctx) {
        this.nucleusCtx = ctx;
    }
    
    public synchronized void addFetchGroup(final FetchGroup grp) {
        if (this.fetchGroupByName == null) {
            this.fetchGroupByName = new MultiMap();
        }
        final Collection coll = this.fetchGroupByName.get(grp.getName());
        if (coll != null) {
            final Iterator iter = coll.iterator();
            while (iter.hasNext()) {
                final FetchGroup existingGrp = iter.next();
                if (existingGrp.getName().equals(grp.getName()) && existingGrp.getType().getName().equals(grp.getType().getName())) {
                    existingGrp.disconnectFromListeners();
                    iter.remove();
                }
            }
        }
        this.fetchGroupByName.put(grp.getName(), grp);
    }
    
    public synchronized void removeFetchGroup(final FetchGroup grp) {
        if (this.fetchGroupByName != null) {
            final Collection coll = this.fetchGroupByName.get(grp.getName());
            if (coll != null) {
                final Iterator iter = coll.iterator();
                while (iter.hasNext()) {
                    final Object obj = iter.next();
                    final FetchGroup existingGrp = (FetchGroup)obj;
                    if (existingGrp.getType() == grp.getType()) {
                        existingGrp.disconnectFromListeners();
                        iter.remove();
                    }
                }
            }
        }
    }
    
    public synchronized FetchGroup getFetchGroup(final Class cls, final String name) {
        if (this.fetchGroupByName != null) {
            final Collection coll = this.fetchGroupByName.get(name);
            if (coll != null) {
                for (final FetchGroup grp : coll) {
                    if (grp.getType() == cls) {
                        return grp;
                    }
                }
            }
        }
        final FetchGroup grp2 = this.createFetchGroup(cls, name);
        this.addFetchGroup(grp2);
        return grp2;
    }
    
    public FetchGroup createFetchGroup(final Class cls, final String name) {
        return new FetchGroup(this.nucleusCtx, name, cls);
    }
    
    public synchronized Set<FetchGroup> getFetchGroupsWithName(final String name) {
        if (this.fetchGroupByName != null) {
            final Collection coll = this.fetchGroupByName.get(name);
            if (coll != null) {
                return new HashSet<FetchGroup>(coll);
            }
        }
        return null;
    }
    
    public synchronized void clearFetchGroups() {
        if (this.fetchGroupByName != null) {
            final Collection fetchGroups = this.fetchGroupByName.values();
            for (final FetchGroup grp : fetchGroups) {
                grp.disconnectFromListeners();
            }
            this.fetchGroupByName.clear();
        }
    }
}
