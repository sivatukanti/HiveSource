// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms;

import org.datanucleus.store.rdbms.table.ClassTable;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import org.datanucleus.flush.FlushNonReferential;
import java.util.HashSet;
import org.datanucleus.exceptions.NucleusOptimisticException;
import org.datanucleus.flush.OperationQueue;
import org.datanucleus.state.ObjectProvider;
import java.util.List;
import org.datanucleus.ExecutionContext;
import org.datanucleus.flush.FlushOrdered;

public class FlushReferential extends FlushOrdered
{
    @Override
    public List<NucleusOptimisticException> execute(final ExecutionContext ec, final List<ObjectProvider> primaryOPs, final List<ObjectProvider> secondaryOPs, final OperationQueue opQueue) {
        List<NucleusOptimisticException> flushExcps = null;
        Set<ObjectProvider> unrelatedOPs = null;
        if (primaryOPs != null) {
            final Iterator<ObjectProvider> opIter = primaryOPs.iterator();
            while (opIter.hasNext()) {
                final ObjectProvider op = opIter.next();
                if (!op.isEmbedded() && this.isClassSuitableForBatching(ec, op.getClassMetaData())) {
                    if (unrelatedOPs == null) {
                        unrelatedOPs = new HashSet<ObjectProvider>();
                    }
                    unrelatedOPs.add(op);
                    opIter.remove();
                }
            }
        }
        if (secondaryOPs != null) {
            final Iterator<ObjectProvider> opIter = secondaryOPs.iterator();
            while (opIter.hasNext()) {
                final ObjectProvider op = opIter.next();
                if (!op.isEmbedded() && this.isClassSuitableForBatching(ec, op.getClassMetaData())) {
                    if (unrelatedOPs == null) {
                        unrelatedOPs = new HashSet<ObjectProvider>();
                    }
                    unrelatedOPs.add(op);
                    opIter.remove();
                }
            }
        }
        if (unrelatedOPs != null) {
            final FlushNonReferential groupedFlush = new FlushNonReferential();
            flushExcps = groupedFlush.flushDeleteInsertUpdateGrouped(unrelatedOPs, ec);
        }
        final List<NucleusOptimisticException> excps = super.execute(ec, primaryOPs, secondaryOPs, opQueue);
        if (excps != null) {
            if (flushExcps == null) {
                flushExcps = excps;
            }
            else {
                flushExcps.addAll(excps);
            }
        }
        return flushExcps;
    }
    
    private boolean isClassSuitableForBatching(final ExecutionContext ec, final AbstractClassMetaData cmd) {
        if (cmd.hasRelations(ec.getClassLoaderResolver(), ec.getMetaDataManager())) {
            return false;
        }
        final RDBMSStoreManager storeMgr = (RDBMSStoreManager)ec.getStoreManager();
        ClassTable table = (ClassTable)storeMgr.getDatastoreClass(cmd.getFullClassName(), ec.getClassLoaderResolver());
        while (this.isTableSuitableForBatching(table)) {
            table = (ClassTable)table.getSuperDatastoreClass();
            if (table == null) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isTableSuitableForBatching(final ClassTable table) {
        return !table.hasExternalFkMappings() && !table.isObjectIdDatastoreAttributed();
    }
}
