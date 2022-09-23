// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import javax.jdo.JDOUserException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.exceptions.NucleusException;
import java.util.Set;
import java.io.Serializable;
import javax.jdo.FetchGroup;

public class JDOFetchGroup implements FetchGroup, Serializable
{
    org.datanucleus.FetchGroup fg;
    
    public JDOFetchGroup(final org.datanucleus.FetchGroup fg) {
        this.fg = null;
        this.fg = fg;
    }
    
    public org.datanucleus.FetchGroup getInternalFetchGroup() {
        return this.fg;
    }
    
    public String getName() {
        return this.fg.getName();
    }
    
    public Class getType() {
        return this.fg.getType();
    }
    
    public FetchGroup setPostLoad(final boolean postLoad) {
        this.assertUnmodifiable();
        this.fg.setPostLoad(postLoad);
        return this;
    }
    
    public boolean getPostLoad() {
        return this.fg.getPostLoad();
    }
    
    public int getRecursionDepth(final String memberName) {
        return this.fg.getRecursionDepth(memberName);
    }
    
    public FetchGroup setRecursionDepth(final String memberName, final int recursionDepth) {
        this.assertUnmodifiable();
        this.fg.setRecursionDepth(memberName, recursionDepth);
        return this;
    }
    
    public FetchGroup setUnmodifiable() {
        this.fg.setUnmodifiable();
        return this;
    }
    
    public boolean isUnmodifiable() {
        return this.fg.isUnmodifiable();
    }
    
    public FetchGroup addCategory(final String categoryName) {
        this.assertUnmodifiable();
        this.fg.addCategory(categoryName);
        return this;
    }
    
    public FetchGroup removeCategory(final String categoryName) {
        this.assertUnmodifiable();
        this.fg.removeCategory(categoryName);
        return this;
    }
    
    public Set getMembers() {
        return this.fg.getMembers();
    }
    
    public FetchGroup addMember(final String memberName) {
        this.assertUnmodifiable();
        try {
            this.fg.addMember(memberName);
            return this;
        }
        catch (NucleusUserException nue) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(nue);
        }
    }
    
    public FetchGroup removeMember(final String memberName) {
        this.assertUnmodifiable();
        try {
            this.fg.removeMember(memberName);
            return this;
        }
        catch (NucleusUserException nue) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(nue);
        }
    }
    
    public FetchGroup addMembers(final String... members) {
        this.assertUnmodifiable();
        try {
            this.fg.addMembers(members);
            return this;
        }
        catch (NucleusUserException nue) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(nue);
        }
    }
    
    public FetchGroup removeMembers(final String... members) {
        this.assertUnmodifiable();
        try {
            this.fg.removeMembers(members);
            return this;
        }
        catch (NucleusUserException nue) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(nue);
        }
    }
    
    private void assertUnmodifiable() {
        if (this.fg.isUnmodifiable()) {
            throw new JDOUserException("FetchGroup is unmodifiable!");
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FetchGroup)) {
            return false;
        }
        final FetchGroup other = (FetchGroup)obj;
        return other.getName().equals(this.getName()) && other.getType() == this.getType();
    }
    
    @Override
    public int hashCode() {
        return this.fg.hashCode();
    }
    
    @Override
    public String toString() {
        return this.fg.toString();
    }
}
