// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import org.datanucleus.exceptions.NucleusException;
import java.util.Collection;
import java.util.Set;
import java.io.Serializable;
import javax.jdo.FetchPlan;

public class JDOFetchPlan implements FetchPlan, Serializable
{
    org.datanucleus.FetchPlan fp;
    
    public JDOFetchPlan(final org.datanucleus.FetchPlan fp) {
        this.fp = null;
        this.fp = fp;
    }
    
    public Set getGroups() {
        return this.fp.getGroups();
    }
    
    public FetchPlan addGroup(final String group) {
        this.fp.addGroup(group);
        return this;
    }
    
    public FetchPlan clearGroups() {
        this.fp.clearGroups();
        return this;
    }
    
    public FetchPlan removeGroup(final String group) {
        this.fp.removeGroup(group);
        return this;
    }
    
    public FetchPlan setGroup(final String group) {
        this.fp.setGroup(group);
        return this;
    }
    
    public FetchPlan setGroups(final Collection groups) {
        this.fp.setGroups(groups);
        return this;
    }
    
    public FetchPlan setGroups(final String... groups) {
        this.fp.setGroups(groups);
        return this;
    }
    
    public int getFetchSize() {
        return this.fp.getFetchSize();
    }
    
    public FetchPlan setFetchSize(final int size) {
        this.fp.setFetchSize(size);
        return this;
    }
    
    public int getMaxFetchDepth() {
        return this.fp.getMaxFetchDepth();
    }
    
    public FetchPlan setMaxFetchDepth(final int depth) {
        try {
            this.fp.setMaxFetchDepth(depth);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
        return this;
    }
    
    public int getDetachmentOptions() {
        return this.fp.getDetachmentOptions();
    }
    
    public Class[] getDetachmentRootClasses() {
        return this.fp.getDetachmentRootClasses();
    }
    
    public Collection getDetachmentRoots() {
        return this.fp.getDetachmentRoots();
    }
    
    public FetchPlan setDetachmentOptions(final int options) {
        try {
            this.fp.setDetachmentOptions(options);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
        return this;
    }
    
    public FetchPlan setDetachmentRootClasses(final Class... rootClasses) {
        try {
            this.fp.setDetachmentRootClasses(rootClasses);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
        return this;
    }
    
    public FetchPlan setDetachmentRoots(final Collection roots) {
        this.fp.setDetachmentRoots(roots);
        return this;
    }
    
    public org.datanucleus.FetchPlan getInternalFetchPlan() {
        return this.fp;
    }
}
