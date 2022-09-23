// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import org.datanucleus.util.StringUtils;
import org.datanucleus.util.SoftValueMap;
import java.util.ArrayList;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.BitSet;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.datanucleus.util.Localiser;
import java.io.Serializable;

public class FetchPlan implements Serializable
{
    protected static final Localiser LOCALISER;
    public static final String DEFAULT = "default";
    public static final String ALL = "all";
    public static final String NONE = "none";
    public static final int DETACH_UNLOAD_FIELDS = 2;
    public static final int DETACH_LOAD_FIELDS = 1;
    public static final int FETCH_SIZE_GREEDY = -1;
    public static final int FETCH_SIZE_OPTIMAL = 0;
    final transient ExecutionContext ec;
    final transient ClassLoaderResolver clr;
    final Set<String> groups;
    transient Set<FetchGroup> dynamicGroups;
    int fetchSize;
    int detachmentOptions;
    final transient Map<String, FetchPlanForClass> managedClass;
    int maxFetchDepth;
    Class[] detachmentRootClasses;
    Collection detachmentRoots;
    private transient Map<AbstractClassMetaData, Map<BitSet, Boolean>> isToCallPostLoadFetchPlanByCmd;
    
    public FetchPlan(final ExecutionContext ec, final ClassLoaderResolver clr) {
        this.groups = new HashSet<String>();
        this.dynamicGroups = null;
        this.fetchSize = 0;
        this.detachmentOptions = 1;
        this.managedClass = new HashMap<String, FetchPlanForClass>();
        this.maxFetchDepth = 1;
        this.detachmentRootClasses = null;
        this.detachmentRoots = null;
        this.ec = ec;
        this.clr = clr;
        this.groups.add("default");
        final String flds = ec.getNucleusContext().getPersistenceConfiguration().getStringProperty("datanucleus.detachmentFields");
        if (flds != null) {
            if (flds.equals("load-unload-fields")) {
                this.detachmentOptions = 3;
            }
            else if (flds.equalsIgnoreCase("unload-fields")) {
                this.detachmentOptions = 2;
            }
            else if (flds.equalsIgnoreCase("load-fields")) {
                this.detachmentOptions = 1;
            }
        }
    }
    
    private void markDirty() {
        final Iterator<FetchPlanForClass> it = this.managedClass.values().iterator();
        while (it.hasNext()) {
            it.next().markDirty();
        }
    }
    
    public synchronized FetchPlanForClass getFetchPlanForClass(final AbstractClassMetaData cmd) {
        FetchPlanForClass fpClass = this.managedClass.get(cmd.getFullClassName());
        if (fpClass == null) {
            fpClass = new FetchPlanForClass(cmd, this);
            this.managedClass.put(cmd.getFullClassName(), fpClass);
        }
        return fpClass;
    }
    
    public synchronized FetchPlan addGroup(final String grpName) {
        if (grpName != null) {
            final boolean changed = this.groups.add(grpName);
            final boolean dynChanged = this.addDynamicGroup(grpName);
            if (changed || dynChanged) {
                this.markDirty();
            }
        }
        return this;
    }
    
    public synchronized FetchPlan removeGroup(final String grpName) {
        if (grpName != null) {
            boolean changed = false;
            changed = this.groups.remove(grpName);
            if (this.dynamicGroups != null) {
                final Iterator<FetchGroup> iter = this.dynamicGroups.iterator();
                while (iter.hasNext()) {
                    final FetchGroup grp = iter.next();
                    if (grp.getName().equals(grpName)) {
                        grp.deregisterListener(this);
                        changed = true;
                        iter.remove();
                    }
                }
            }
            if (changed) {
                this.markDirty();
            }
        }
        return this;
    }
    
    public synchronized FetchPlan clearGroups() {
        this.clearDynamicGroups();
        this.groups.clear();
        this.markDirty();
        return this;
    }
    
    public synchronized Set<String> getGroups() {
        return Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(this.groups));
    }
    
    public synchronized FetchPlan setGroups(final Collection<String> grpNames) {
        this.clearDynamicGroups();
        this.groups.clear();
        if (grpNames != null) {
            final Set g = new HashSet(grpNames);
            this.groups.addAll(g);
            final Iterator<String> iter = grpNames.iterator();
            while (iter.hasNext()) {
                this.addDynamicGroup(iter.next());
            }
        }
        this.markDirty();
        return this;
    }
    
    public synchronized FetchPlan setGroups(final String[] grpNames) {
        this.clearDynamicGroups();
        this.groups.clear();
        if (grpNames != null) {
            for (int i = 0; i < grpNames.length; ++i) {
                this.groups.add(grpNames[i]);
            }
            for (int i = 0; i < grpNames.length; ++i) {
                this.addDynamicGroup(grpNames[i]);
            }
        }
        this.markDirty();
        return this;
    }
    
    public synchronized FetchPlan setGroup(final String grpName) {
        this.clearDynamicGroups();
        this.groups.clear();
        if (grpName != null) {
            this.groups.add(grpName);
            this.addDynamicGroup(grpName);
        }
        this.markDirty();
        return this;
    }
    
    private void clearDynamicGroups() {
        if (this.dynamicGroups != null) {
            final Iterator<FetchGroup> iter = this.dynamicGroups.iterator();
            while (iter.hasNext()) {
                iter.next().deregisterListener(this);
            }
            this.dynamicGroups.clear();
        }
    }
    
    private boolean addDynamicGroup(final String grpName) {
        boolean changed = false;
        final Set<FetchGroup> ecGrpsWithName = (Set<FetchGroup>)this.ec.getFetchGroupsWithName(grpName);
        if (ecGrpsWithName != null) {
            if (this.dynamicGroups == null) {
                this.dynamicGroups = new HashSet<FetchGroup>();
            }
            for (final FetchGroup grp : ecGrpsWithName) {
                this.dynamicGroups.add(grp);
                grp.registerListener(this);
                changed = true;
            }
        }
        if (!changed) {
            final Set<FetchGroup> grpsWithName = this.ec.getNucleusContext().getFetchGroupsWithName(grpName);
            if (grpsWithName != null) {
                if (this.dynamicGroups == null) {
                    this.dynamicGroups = new HashSet<FetchGroup>();
                }
                for (final FetchGroup grp2 : grpsWithName) {
                    this.dynamicGroups.add(grp2);
                    grp2.registerListener(this);
                    changed = true;
                }
            }
        }
        return changed;
    }
    
    public void notifyFetchGroupChange(final FetchGroup group) {
        final Collection fpClasses = this.managedClass.values();
        for (final FetchPlanForClass fpClass : fpClasses) {
            final Class cls = this.clr.classForName(fpClass.cmd.getFullClassName());
            if (cls.isAssignableFrom(group.getType()) || group.getType().isAssignableFrom(cls)) {
                fpClass.markDirty();
            }
        }
    }
    
    public void notifyFetchGroupRemove(final FetchGroup group) {
        this.dynamicGroups.remove(group);
        this.notifyFetchGroupChange(group);
    }
    
    public FetchPlan setDetachmentRoots(final Collection roots) {
        if (this.detachmentRootClasses != null || this.detachmentRoots != null) {
            throw new NucleusUserException(FetchPlan.LOCALISER.msg("006003"));
        }
        if (roots == null) {
            this.detachmentRoots = null;
        }
        (this.detachmentRoots = new ArrayList()).addAll(roots);
        return this;
    }
    
    public Collection getDetachmentRoots() {
        if (this.detachmentRoots == null) {
            return Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableCollection((Collection<?>)this.detachmentRoots);
    }
    
    public FetchPlan setDetachmentRootClasses(final Class[] rootClasses) {
        if (this.detachmentRootClasses != null || this.detachmentRoots != null) {
            throw new NucleusUserException(FetchPlan.LOCALISER.msg("006003"));
        }
        if (rootClasses == null) {
            this.detachmentRootClasses = null;
            return this;
        }
        this.detachmentRootClasses = new Class[rootClasses.length];
        for (int i = 0; i < rootClasses.length; ++i) {
            this.detachmentRootClasses[i] = rootClasses[i];
        }
        return this;
    }
    
    public Class[] getDetachmentRootClasses() {
        if (this.detachmentRootClasses == null) {
            return new Class[0];
        }
        return this.detachmentRootClasses;
    }
    
    void resetDetachmentRoots() {
        this.detachmentRootClasses = null;
        this.detachmentRoots = null;
    }
    
    public synchronized FetchPlan setMaxFetchDepth(final int max) {
        if (max == 0) {
            throw new NucleusUserException(FetchPlan.LOCALISER.msg("006002", max));
        }
        this.maxFetchDepth = max;
        return this;
    }
    
    public synchronized int getMaxFetchDepth() {
        return this.maxFetchDepth;
    }
    
    public synchronized FetchPlan setFetchSize(final int fetchSize) {
        if (fetchSize != -1 && fetchSize != 0 && fetchSize < 0) {
            return this;
        }
        this.fetchSize = fetchSize;
        return this;
    }
    
    public synchronized int getFetchSize() {
        return this.fetchSize;
    }
    
    public int getDetachmentOptions() {
        return this.detachmentOptions;
    }
    
    public FetchPlan setDetachmentOptions(final int options) {
        this.detachmentOptions = options;
        return this;
    }
    
    public synchronized FetchPlan getCopy() {
        final FetchPlan fp = new FetchPlan(this.ec, this.clr);
        fp.maxFetchDepth = this.maxFetchDepth;
        fp.groups.remove("default");
        fp.groups.addAll(this.groups);
        if (this.dynamicGroups != null) {
            fp.dynamicGroups = new HashSet<FetchGroup>(this.dynamicGroups);
        }
        for (final Map.Entry<String, FetchPlanForClass> entry : this.managedClass.entrySet()) {
            final String className = entry.getKey();
            final FetchPlanForClass fpcls = entry.getValue();
            fp.managedClass.put(className, fpcls.getCopy(fp));
        }
        fp.fetchSize = this.fetchSize;
        return fp;
    }
    
    Boolean getCachedIsToCallPostLoadFetchPlan(final AbstractClassMetaData cmd, final BitSet loadedFields) {
        if (this.isToCallPostLoadFetchPlanByCmd == null) {
            this.isToCallPostLoadFetchPlanByCmd = (Map<AbstractClassMetaData, Map<BitSet, Boolean>>)new SoftValueMap();
        }
        final Map cachedIsToCallPostLoadFetchPlan = this.isToCallPostLoadFetchPlanByCmd.get(cmd);
        if (cachedIsToCallPostLoadFetchPlan == null) {
            return null;
        }
        return cachedIsToCallPostLoadFetchPlan.get(loadedFields);
    }
    
    void cacheIsToCallPostLoadFetchPlan(final AbstractClassMetaData cmd, final BitSet loadedFields, final Boolean itcplfp) {
        if (this.isToCallPostLoadFetchPlanByCmd == null) {
            this.isToCallPostLoadFetchPlanByCmd = (Map<AbstractClassMetaData, Map<BitSet, Boolean>>)new SoftValueMap();
        }
        Map cachedIsToCallPostLoadFetchPlan = this.isToCallPostLoadFetchPlanByCmd.get(cmd);
        if (cachedIsToCallPostLoadFetchPlan == null) {
            cachedIsToCallPostLoadFetchPlan = new SoftValueMap();
            this.isToCallPostLoadFetchPlanByCmd.put(cmd, cachedIsToCallPostLoadFetchPlan);
        }
        cachedIsToCallPostLoadFetchPlan.put(loadedFields, itcplfp);
    }
    
    void invalidateCachedIsToCallPostLoadFetchPlan(final AbstractClassMetaData cmd) {
        if (this.isToCallPostLoadFetchPlanByCmd == null) {
            this.isToCallPostLoadFetchPlanByCmd = (Map<AbstractClassMetaData, Map<BitSet, Boolean>>)new SoftValueMap();
        }
        final Map cachedIsToCallPostLoadFetchPlan = this.isToCallPostLoadFetchPlanByCmd.get(cmd);
        if (cachedIsToCallPostLoadFetchPlan != null) {
            cachedIsToCallPostLoadFetchPlan.clear();
        }
    }
    
    public String toStringWithClasses() {
        return "FetchPlan " + this.groups.toString() + " classes=" + StringUtils.collectionToString(Collections.unmodifiableCollection((Collection<?>)this.managedClass.values()));
    }
    
    @Override
    public String toString() {
        return "FetchPlan " + this.groups.toString();
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
