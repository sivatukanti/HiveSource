// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.imps;

import org.apache.curator.framework.api.transaction.OperationType;
import org.apache.zookeeper.Op;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import java.util.List;
import org.apache.zookeeper.MultiTransactionRecord;

class CuratorMultiTransactionRecord extends MultiTransactionRecord
{
    private final List<TypeAndPath> metadata;
    
    CuratorMultiTransactionRecord() {
        this.metadata = (List<TypeAndPath>)Lists.newArrayList();
    }
    
    @Override
    public final void add(final Op op) {
        throw new UnsupportedOperationException();
    }
    
    void add(final Op op, final OperationType type, final String forPath) {
        super.add(op);
        this.metadata.add(new TypeAndPath(type, forPath));
    }
    
    TypeAndPath getMetadata(final int index) {
        return this.metadata.get(index);
    }
    
    int metadataSize() {
        return this.metadata.size();
    }
    
    static class TypeAndPath
    {
        final OperationType type;
        final String forPath;
        
        TypeAndPath(final OperationType type, final String forPath) {
            this.type = type;
            this.forPath = forPath;
        }
    }
}
