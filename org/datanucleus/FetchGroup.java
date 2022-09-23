// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import org.datanucleus.util.StringUtils;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.datanucleus.util.Localiser;
import java.io.Serializable;

public class FetchGroup implements Serializable
{
    protected static final Localiser LOCALISER;
    public static final String DEFAULT = "default";
    public static final String RELATIONSHIP = "relationship";
    public static final String MULTIVALUED = "multivalued";
    public static final String BASIC = "basic";
    public static final String ALL = "all";
    private NucleusContext nucleusCtx;
    private String name;
    private Class cls;
    private boolean postLoad;
    private Set<String> memberNames;
    private Map<String, Integer> recursionDepthByMemberName;
    private Collection<FetchPlan> planListeners;
    private boolean unmodifiable;
    
    public FetchGroup(final NucleusContext nucleusCtx, final String name, final Class cls) {
        this.postLoad = false;
        this.memberNames = new HashSet<String>();
        this.recursionDepthByMemberName = null;
        this.planListeners = null;
        this.unmodifiable = false;
        this.nucleusCtx = nucleusCtx;
        this.name = name;
        this.cls = cls;
    }
    
    public FetchGroup(final FetchGroup grp) {
        this.postLoad = false;
        this.memberNames = new HashSet<String>();
        this.recursionDepthByMemberName = null;
        this.planListeners = null;
        this.unmodifiable = false;
        this.name = grp.name;
        this.cls = grp.cls;
        this.nucleusCtx = grp.nucleusCtx;
        this.postLoad = grp.postLoad;
        for (final String memberName : grp.memberNames) {
            this.addMember(memberName);
        }
        if (grp.recursionDepthByMemberName != null) {
            this.recursionDepthByMemberName = new HashMap<String, Integer>(grp.recursionDepthByMemberName);
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public Class getType() {
        return this.cls;
    }
    
    public void setPostLoad(final boolean postLoad) {
        this.assertUnmodifiable();
        this.postLoad = postLoad;
    }
    
    public boolean getPostLoad() {
        return this.postLoad;
    }
    
    public int getRecursionDepth(final String memberName) {
        if (this.recursionDepthByMemberName != null) {
            final Integer recursionValue = this.recursionDepthByMemberName.get(memberName);
            if (recursionValue != null) {
                return recursionValue;
            }
        }
        return 1;
    }
    
    public FetchGroup setRecursionDepth(final String memberName, final int recursionDepth) {
        this.assertUnmodifiable();
        this.assertNotMember(memberName);
        if (this.memberNames.contains(memberName)) {
            if (this.recursionDepthByMemberName == null) {
                this.recursionDepthByMemberName = new HashMap<String, Integer>();
            }
            this.recursionDepthByMemberName.put(memberName, recursionDepth);
        }
        return this;
    }
    
    public FetchGroup setUnmodifiable() {
        if (!this.unmodifiable) {
            this.unmodifiable = true;
        }
        return this;
    }
    
    public boolean isUnmodifiable() {
        return this.unmodifiable;
    }
    
    public FetchGroup addCategory(final String categoryName) {
        this.assertUnmodifiable();
        final String[] memberNames = this.getMemberNamesForCategory(categoryName);
        if (memberNames != null) {
            for (int i = 0; i < memberNames.length; ++i) {
                this.memberNames.add(memberNames[i]);
            }
            this.notifyListeners();
        }
        return this;
    }
    
    public FetchGroup removeCategory(final String categoryName) {
        this.assertUnmodifiable();
        final String[] memberNames = this.getMemberNamesForCategory(categoryName);
        if (memberNames != null) {
            for (int i = 0; i < memberNames.length; ++i) {
                this.memberNames.remove(memberNames[i]);
            }
            this.notifyListeners();
        }
        return this;
    }
    
    private String[] getMemberNamesForCategory(final String categoryName) {
        final AbstractClassMetaData acmd = this.getMetaDataForClass();
        int[] memberPositions = null;
        if (categoryName.equals("default")) {
            memberPositions = acmd.getDFGMemberPositions();
        }
        else if (categoryName.equals("all")) {
            memberPositions = acmd.getAllMemberPositions();
        }
        else if (categoryName.equals("basic")) {
            memberPositions = acmd.getBasicMemberPositions(this.nucleusCtx.getClassLoaderResolver(null), this.nucleusCtx.getMetaDataManager());
        }
        else if (categoryName.equals("relationship")) {
            memberPositions = acmd.getRelationMemberPositions(this.nucleusCtx.getClassLoaderResolver(null), this.nucleusCtx.getMetaDataManager());
        }
        else {
            if (!categoryName.equals("multivalued")) {
                throw this.nucleusCtx.getApiAdapter().getUserExceptionForException("Category " + categoryName + " is invalid", null);
            }
            memberPositions = acmd.getMultivaluedMemberPositions();
        }
        final String[] names = new String[memberPositions.length];
        for (int i = 0; i < memberPositions.length; ++i) {
            names[i] = acmd.getMetaDataForManagedMemberAtAbsolutePosition(memberPositions[i]).getName();
        }
        return names;
    }
    
    public Set<String> getMembers() {
        return this.memberNames;
    }
    
    public FetchGroup addMember(final String memberName) {
        this.assertUnmodifiable();
        this.assertNotMember(memberName);
        this.memberNames.add(memberName);
        this.notifyListeners();
        return this;
    }
    
    public FetchGroup removeMember(final String memberName) {
        this.assertUnmodifiable();
        this.assertNotMember(memberName);
        this.memberNames.remove(memberName);
        this.notifyListeners();
        return this;
    }
    
    public FetchGroup addMembers(final String[] members) {
        if (members == null) {
            return this;
        }
        for (int i = 0; i < members.length; ++i) {
            this.addMember(members[i]);
        }
        this.notifyListeners();
        return this;
    }
    
    public FetchGroup removeMembers(final String[] members) {
        if (members == null) {
            return this;
        }
        for (int i = 0; i < members.length; ++i) {
            this.removeMember(members[i]);
        }
        this.notifyListeners();
        return this;
    }
    
    private void notifyListeners() {
        if (this.planListeners != null) {
            final Iterator<FetchPlan> iter = this.planListeners.iterator();
            while (iter.hasNext()) {
                iter.next().notifyFetchGroupChange(this);
            }
        }
    }
    
    public void registerListener(final FetchPlan plan) {
        if (this.planListeners == null) {
            this.planListeners = new HashSet<FetchPlan>();
        }
        this.planListeners.add(plan);
    }
    
    public void deregisterListener(final FetchPlan plan) {
        if (this.planListeners != null) {
            this.planListeners.remove(plan);
        }
    }
    
    public void disconnectFromListeners() {
        if (this.planListeners != null) {
            final Iterator<FetchPlan> iter = this.planListeners.iterator();
            while (iter.hasNext()) {
                iter.next().notifyFetchGroupRemove(this);
            }
            this.planListeners.clear();
            this.planListeners = null;
        }
    }
    
    private void assertUnmodifiable() {
        if (this.unmodifiable) {
            throw this.nucleusCtx.getApiAdapter().getUserExceptionForException("FetchGroup is not modifiable!", null);
        }
    }
    
    private void assertNotMember(final String memberName) {
        final AbstractClassMetaData acmd = this.getMetaDataForClass();
        if (!acmd.hasMember(memberName)) {
            throw this.nucleusCtx.getApiAdapter().getUserExceptionForException(FetchGroup.LOCALISER.msg("006004", memberName, this.cls.getName()), null);
        }
    }
    
    private AbstractClassMetaData getMetaDataForClass() {
        AbstractClassMetaData acmd = null;
        if (this.cls.isInterface()) {
            acmd = this.nucleusCtx.getMetaDataManager().getMetaDataForInterface(this.cls, this.nucleusCtx.getClassLoaderResolver(null));
        }
        else {
            acmd = this.nucleusCtx.getMetaDataManager().getMetaDataForClass(this.cls, this.nucleusCtx.getClassLoaderResolver(null));
        }
        return acmd;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null || !(obj instanceof FetchGroup)) {
            return false;
        }
        final FetchGroup other = (FetchGroup)obj;
        return other.cls == this.cls && other.name.equals(this.name);
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode() ^ this.cls.hashCode();
    }
    
    @Override
    public String toString() {
        return "FetchGroup : " + this.name + " for " + this.cls.getName() + " members=" + StringUtils.collectionToString(this.memberNames) + ", modifiable=" + !this.unmodifiable + ", postLoad=" + this.postLoad + ", listeners.size=" + ((this.planListeners != null) ? this.planListeners.size() : 0);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
