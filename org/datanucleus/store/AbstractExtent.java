// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store;

import org.datanucleus.ClassConstants;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ExecutionContext;
import org.datanucleus.util.Localiser;

public abstract class AbstractExtent implements Extent
{
    protected static final Localiser LOCALISER;
    protected final ExecutionContext ec;
    protected final Class candidateClass;
    protected final boolean subclasses;
    protected final AbstractClassMetaData cmd;
    
    public AbstractExtent(final ExecutionContext ec, final Class cls, final boolean subclasses, final AbstractClassMetaData cmd) {
        if (cls == null) {
            throw new NucleusUserException(AbstractExtent.LOCALISER.msg("033000")).setFatal();
        }
        if ((this.cmd = cmd) == null) {
            throw new NucleusUserException(AbstractExtent.LOCALISER.msg("033001", cls.getName())).setFatal();
        }
        this.ec = ec;
        this.candidateClass = cls;
        this.subclasses = subclasses;
    }
    
    @Override
    public boolean hasSubclasses() {
        return this.subclasses;
    }
    
    @Override
    public Class getCandidateClass() {
        return this.candidateClass;
    }
    
    @Override
    public ExecutionContext getExecutionContext() {
        return this.ec;
    }
    
    @Override
    public String toString() {
        return AbstractExtent.LOCALISER.msg("033002", this.candidateClass.getName(), "" + this.subclasses);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
