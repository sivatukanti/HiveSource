// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import java.util.HashMap;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.NucleusLogger;
import java.util.BitSet;
import java.util.Iterator;
import org.datanucleus.metadata.FetchGroupMemberMetaData;
import java.util.Collection;
import java.util.HashSet;
import org.datanucleus.util.StringUtils;
import org.datanucleus.metadata.FetchGroupMetaData;
import java.util.Set;
import java.util.Map;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.util.Localiser;

public class FetchPlanForClass
{
    protected static final Localiser LOCALISER;
    final FetchPlan plan;
    final AbstractClassMetaData cmd;
    int[] memberNumbers;
    boolean dirty;
    private Map<Integer, Set<FetchGroupMetaData>> fetchGroupsByMemberNumber;
    
    public FetchPlanForClass(final AbstractClassMetaData cmd, final FetchPlan fetchPlan) {
        this.dirty = true;
        this.fetchGroupsByMemberNumber = null;
        this.cmd = cmd;
        this.plan = fetchPlan;
    }
    
    public final FetchPlan getFetchPlan() {
        return this.plan;
    }
    
    public final AbstractClassMetaData getAbstractClassMetaData() {
        return this.cmd;
    }
    
    @Override
    public String toString() {
        return this.cmd.getFullClassName() + "[members=" + StringUtils.intArrayToString(this.getMemberNumbers()) + "]";
    }
    
    void markDirty() {
        this.dirty = true;
        this.plan.invalidateCachedIsToCallPostLoadFetchPlan(this.cmd);
    }
    
    FetchPlanForClass getCopy(final FetchPlan fp) {
        final FetchPlanForClass fpCopy = new FetchPlanForClass(this.cmd, fp);
        if (this.memberNumbers != null) {
            fpCopy.memberNumbers = new int[this.memberNumbers.length];
            for (int i = 0; i < fpCopy.memberNumbers.length; ++i) {
                fpCopy.memberNumbers[i] = this.memberNumbers[i];
            }
        }
        fpCopy.dirty = this.dirty;
        return fpCopy;
    }
    
    public int getMaxRecursionDepthForMember(final int memberNum) {
        final Set<String> currentGroupNames = new HashSet<String>(this.plan.getGroups());
        final Set<FetchGroupMetaData> fetchGroupsContainingField = this.getFetchGroupsForMemberNumber(this.cmd.getFetchGroupMetaData(currentGroupNames), memberNum);
        int recursionDepth = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(memberNum).getRecursionDepth();
        if (recursionDepth == 0) {
            recursionDepth = 1;
        }
        final String fieldName = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(memberNum).getName();
        for (final FetchGroupMetaData fgmd : fetchGroupsContainingField) {
            final Set<FetchGroupMemberMetaData> fgmmds = fgmd.getMembers();
            if (fgmmds != null) {
                for (final FetchGroupMemberMetaData fgmmd : fgmmds) {
                    if (fgmmd.getName().equals(fieldName) && fgmmd.getRecursionDepth() != 0) {
                        recursionDepth = fgmmd.getRecursionDepth();
                    }
                }
            }
        }
        return recursionDepth;
    }
    
    public boolean hasMember(final int memberNumber) {
        if (this.dirty) {
            final BitSet fieldsNumber = this.getMemberNumbersByBitSet();
            return fieldsNumber.get(memberNumber);
        }
        if (this.memberNumbers != null) {
            for (int i = 0; i < this.memberNumbers.length; ++i) {
                if (this.memberNumbers[i] == memberNumber) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public int[] getMemberNumbers() {
        if (this.dirty) {
            this.dirty = false;
            final BitSet fieldsNumber = this.getMemberNumbersByBitSet();
            int count = 0;
            for (int i = 0; i < fieldsNumber.length(); ++i) {
                if (fieldsNumber.get(i)) {
                    ++count;
                }
            }
            this.memberNumbers = new int[count];
            int nextField = 0;
            for (int j = 0; j < fieldsNumber.length(); ++j) {
                if (fieldsNumber.get(j)) {
                    this.memberNumbers[nextField++] = j;
                }
            }
        }
        return this.memberNumbers;
    }
    
    public BitSet getMemberNumbersByBitSet() {
        return this.getMemberNumbersByBitSet(this.cmd);
    }
    
    private BitSet getMemberNumbersByBitSet(final AbstractClassMetaData cmd) {
        final FetchPlanForClass fpc = this.plan.getFetchPlanForClass(cmd);
        final BitSet bitSet = fpc.getMemberNumbersForFetchGroups(cmd.getFetchGroupMetaData());
        if (cmd.getPersistenceCapableSuperclass() != null) {
            final AbstractClassMetaData superCmd = cmd.getSuperAbstractClassMetaData();
            final FetchPlanForClass superFpc = this.plan.getFetchPlanForClass(superCmd);
            bitSet.or(superFpc.getMemberNumbersByBitSet(superCmd));
        }
        else {
            fpc.setAsNone(bitSet);
        }
        if (this.plan.dynamicGroups != null) {
            for (final FetchGroup grp : this.plan.dynamicGroups) {
                if (grp.getType().getName().equals(cmd.getFullClassName())) {
                    final Set<String> members = grp.getMembers();
                    for (final String memberName : members) {
                        final int fieldPos = cmd.getAbsolutePositionOfMember(memberName);
                        if (fieldPos >= 0) {
                            bitSet.set(fieldPos);
                        }
                    }
                }
            }
        }
        return bitSet;
    }
    
    private BitSet getMemberNumbersForFetchGroups(final Set<FetchGroupMetaData> fgmds) {
        final BitSet memberNumbers = new BitSet(0);
        if (fgmds != null) {
            for (final FetchGroupMetaData fgmd : fgmds) {
                if (this.plan.groups.contains(fgmd.getName())) {
                    memberNumbers.or(this.getMemberNumbersForFetchGroup(fgmd));
                }
            }
        }
        if (this.plan.groups.contains("default")) {
            this.setAsDefault(memberNumbers);
        }
        if (this.plan.groups.contains("all")) {
            this.setAsAll(memberNumbers);
        }
        if (this.plan.groups.contains("none")) {
            this.setAsNone(memberNumbers);
        }
        return memberNumbers;
    }
    
    private BitSet getMemberNumbersForFetchGroup(final FetchGroupMetaData fgmd) {
        final BitSet memberNumbers = new BitSet(0);
        final Set<FetchGroupMemberMetaData> subFGmmds = fgmd.getMembers();
        if (subFGmmds != null) {
            for (final FetchGroupMemberMetaData subFGmmd : subFGmmds) {
                final int fieldNumber = this.cmd.getAbsolutePositionOfMember(subFGmmd.getName());
                if (fieldNumber == -1) {
                    final String msg = FetchPlanForClass.LOCALISER.msg("006000", subFGmmd.getName(), fgmd.getName(), this.cmd.getFullClassName());
                    NucleusLogger.PERSISTENCE.error(msg);
                    throw new NucleusUserException(msg).setFatal();
                }
                memberNumbers.set(fieldNumber);
            }
        }
        final Set<FetchGroupMetaData> subFGs = fgmd.getFetchGroups();
        if (subFGs != null) {
            for (final FetchGroupMetaData subFgmd : subFGs) {
                final String nestedGroupName = subFgmd.getName();
                if (nestedGroupName.equals("default")) {
                    this.setAsDefault(memberNumbers);
                }
                else if (nestedGroupName.equals("all")) {
                    this.setAsAll(memberNumbers);
                }
                else if (nestedGroupName.equals("none")) {
                    this.setAsNone(memberNumbers);
                }
                else {
                    final FetchGroupMetaData nestedFGMD = this.cmd.getFetchGroupMetaData(nestedGroupName);
                    if (nestedFGMD == null) {
                        throw new NucleusUserException(FetchPlanForClass.LOCALISER.msg("006001", subFgmd.getName(), fgmd.getName(), this.cmd.getFullClassName())).setFatal();
                    }
                    memberNumbers.or(this.getMemberNumbersForFetchGroup(nestedFGMD));
                }
            }
        }
        return memberNumbers;
    }
    
    private void setAsDefault(final BitSet memberNums) {
        for (int i = 0; i < this.cmd.getDFGMemberPositions().length; ++i) {
            memberNums.set(this.cmd.getDFGMemberPositions()[i]);
        }
    }
    
    private void setAsAll(final BitSet memberNums) {
        for (int i = 0; i < this.cmd.getNoOfManagedMembers(); ++i) {
            if (this.cmd.getMetaDataForManagedMemberAtRelativePosition(i).getPersistenceModifier() != FieldPersistenceModifier.NONE) {
                memberNums.set(this.cmd.getAbsoluteMemberPositionForRelativePosition(i));
            }
        }
    }
    
    private void setAsNone(final BitSet memberNums) {
        for (int i = 0; i < this.cmd.getNoOfManagedMembers(); ++i) {
            final AbstractMemberMetaData fmd = this.cmd.getMetaDataForMemberAtRelativePosition(i);
            if (fmd.isPrimaryKey()) {
                memberNums.set(fmd.getAbsoluteFieldNumber());
            }
        }
    }
    
    public boolean isToCallPostLoadFetchPlan(final boolean[] loadedMembers) {
        final BitSet cacheKey = new BitSet(loadedMembers.length);
        for (int i = 0; i < loadedMembers.length; ++i) {
            cacheKey.set(i, loadedMembers[i]);
        }
        Boolean result = this.plan.getCachedIsToCallPostLoadFetchPlan(this.cmd, cacheKey);
        if (result == null) {
            result = Boolean.FALSE;
            final int[] fieldsInActualFetchPlan = this.getMemberNumbers();
            for (int j = 0; j < fieldsInActualFetchPlan.length; ++j) {
                final int fieldNumber = fieldsInActualFetchPlan[j];
                final String fieldName = this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber).getFullFieldName();
                if (!loadedMembers[fieldNumber]) {
                    if (this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber).isDefaultFetchGroup() && this.plan.getGroups().contains("default")) {
                        result = Boolean.TRUE;
                    }
                    else {
                        if (this.cmd.hasFetchGroupWithPostLoad()) {
                            final Integer fieldNumberInteger = fieldNumber;
                            Set<FetchGroupMetaData> fetchGroups = null;
                            if (this.fetchGroupsByMemberNumber != null) {
                                fetchGroups = this.fetchGroupsByMemberNumber.get(fieldNumberInteger);
                            }
                            if (fetchGroups == null) {
                                fetchGroups = this.getFetchGroupsForMemberNumber(this.cmd.getFetchGroupMetaData(), fieldNumber);
                                if (this.fetchGroupsByMemberNumber == null) {
                                    this.fetchGroupsByMemberNumber = new HashMap<Integer, Set<FetchGroupMetaData>>();
                                }
                                this.fetchGroupsByMemberNumber.put(fieldNumberInteger, fetchGroups);
                            }
                            for (final FetchGroupMetaData fgmd : fetchGroups) {
                                if (fgmd.getPostLoad()) {
                                    result = Boolean.TRUE;
                                }
                            }
                        }
                        if (this.plan.dynamicGroups != null) {
                            final Class cls = this.plan.clr.classForName(this.cmd.getFullClassName());
                            for (final FetchGroup group : this.plan.dynamicGroups) {
                                final Set groupMembers = group.getMembers();
                                if (group.getType().isAssignableFrom(cls) && groupMembers.contains(fieldName) && group.getPostLoad()) {
                                    result = Boolean.TRUE;
                                }
                            }
                        }
                    }
                }
            }
            if (result == null) {
                result = Boolean.FALSE;
            }
            this.plan.cacheIsToCallPostLoadFetchPlan(this.cmd, cacheKey, result);
        }
        return result;
    }
    
    private Set<FetchGroupMetaData> getFetchGroupsForMemberNumber(final Set<FetchGroupMetaData> fgmds, final int memberNum) {
        final Set<FetchGroupMetaData> fetchGroups = new HashSet<FetchGroupMetaData>();
        if (fgmds != null) {
            for (final FetchGroupMetaData fgmd : fgmds) {
                final Set<FetchGroupMemberMetaData> subFGmmds = fgmd.getMembers();
                if (subFGmmds != null) {
                    for (final FetchGroupMemberMetaData subFGmmd : subFGmmds) {
                        if (subFGmmd.getName().equals(this.cmd.getMetaDataForManagedMemberAtAbsolutePosition(memberNum).getName())) {
                            fetchGroups.add(fgmd);
                        }
                    }
                }
                final Set<FetchGroupMetaData> subFGmds = fgmd.getFetchGroups();
                if (subFGmds != null) {
                    fetchGroups.addAll(this.getFetchGroupsForMemberNumber(subFGmds, memberNum));
                }
            }
        }
        return fetchGroups;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
