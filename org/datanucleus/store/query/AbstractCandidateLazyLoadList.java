// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query;

import org.datanucleus.metadata.MetaDataUtils;
import java.util.ArrayList;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.util.List;
import org.datanucleus.ExecutionContext;

public abstract class AbstractCandidateLazyLoadList extends AbstractLazyLoadList
{
    protected ExecutionContext ec;
    protected List<AbstractClassMetaData> cmds;
    
    public AbstractCandidateLazyLoadList(final Class cls, final boolean subclasses, final ExecutionContext ec, final String cacheType) {
        super(cacheType);
        this.cmds = new ArrayList<AbstractClassMetaData>();
        this.ec = ec;
        this.cmds = MetaDataUtils.getMetaDataForCandidates(cls, subclasses, ec);
    }
}
