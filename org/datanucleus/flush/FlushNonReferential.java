// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.flush;

import org.datanucleus.store.StorePersistenceHandler;
import java.util.Iterator;
import java.util.ArrayList;
import org.datanucleus.util.NucleusLogger;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import org.datanucleus.exceptions.NucleusOptimisticException;
import org.datanucleus.state.ObjectProvider;
import java.util.List;
import org.datanucleus.ExecutionContext;

public class FlushNonReferential implements FlushProcess
{
    @Override
    public List<NucleusOptimisticException> execute(final ExecutionContext ec, final List<ObjectProvider> primaryOPs, final List<ObjectProvider> secondaryOPs, final OperationQueue opQueue) {
        final Set<ObjectProvider> opsToFlush = new HashSet<ObjectProvider>();
        if (primaryOPs != null) {
            opsToFlush.addAll(primaryOPs);
            primaryOPs.clear();
        }
        if (secondaryOPs != null) {
            opsToFlush.addAll(secondaryOPs);
            secondaryOPs.clear();
        }
        return this.flushDeleteInsertUpdateGrouped(opsToFlush, ec);
    }
    
    public List<NucleusOptimisticException> flushDeleteInsertUpdateGrouped(final Set<ObjectProvider> opsToFlush, final ExecutionContext ec) {
        List<NucleusOptimisticException> optimisticFailures = null;
        Set<Class> classesToFlush = null;
        if (ec.getNucleusContext().getStoreManager().getQueryManager().getQueryResultsCache() != null) {
            classesToFlush = new HashSet<Class>();
        }
        final Set<ObjectProvider> opsToDelete = new HashSet<ObjectProvider>();
        final Set<ObjectProvider> opsToInsert = new HashSet<ObjectProvider>();
        final Iterator<ObjectProvider> opIter = opsToFlush.iterator();
        while (opIter.hasNext()) {
            final ObjectProvider op = opIter.next();
            if (op.isEmbedded()) {
                op.markAsFlushed();
                opIter.remove();
            }
            else {
                if (classesToFlush != null) {
                    classesToFlush.add(op.getObject().getClass());
                }
                if (op.getLifecycleState().isNew() && !op.isFlushedToDatastore() && !op.isFlushedNew()) {
                    opsToInsert.add(op);
                    opIter.remove();
                }
                else {
                    if (!op.getLifecycleState().isDeleted() || op.isFlushedToDatastore()) {
                        continue;
                    }
                    if (!op.getLifecycleState().isNew()) {
                        opsToDelete.add(op);
                        opIter.remove();
                    }
                    else {
                        if (!op.getLifecycleState().isNew() || !op.isFlushedNew()) {
                            continue;
                        }
                        opsToDelete.add(op);
                        opIter.remove();
                    }
                }
            }
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(FlushNonReferential.LOCALISER.msg("010046", opsToDelete.size(), opsToInsert.size(), opsToFlush.size()));
        }
        final StorePersistenceHandler persistenceHandler = ec.getStoreManager().getPersistenceHandler();
        if (!opsToDelete.isEmpty()) {
            for (final ObjectProvider op2 : opsToDelete) {
                op2.setFlushing(true);
                ec.getCallbackHandler().preDelete(op2.getObject());
            }
            try {
                persistenceHandler.deleteObjects((ObjectProvider[])opsToDelete.toArray(new ObjectProvider[opsToDelete.size()]));
            }
            catch (NucleusOptimisticException noe) {
                optimisticFailures = new ArrayList<NucleusOptimisticException>();
                final Throwable[] nestedExcs = noe.getNestedExceptions();
                if (nestedExcs != null && nestedExcs.length > 1) {
                    final NucleusOptimisticException[] noes = (NucleusOptimisticException[])nestedExcs;
                    for (int i = 0; i < nestedExcs.length; ++i) {
                        optimisticFailures.add(noes[i]);
                    }
                }
                else {
                    optimisticFailures.add(noe);
                }
            }
            for (final ObjectProvider op2 : opsToDelete) {
                ec.getCallbackHandler().postDelete(op2.getObject());
                op2.setFlushedNew(false);
                op2.markAsFlushed();
                op2.setFlushing(false);
            }
        }
        if (!opsToInsert.isEmpty()) {
            for (final ObjectProvider op2 : opsToInsert) {
                op2.setFlushing(true);
                ec.getCallbackHandler().preStore(op2.getObject());
            }
            persistenceHandler.insertObjects((ObjectProvider[])opsToInsert.toArray(new ObjectProvider[opsToInsert.size()]));
            for (final ObjectProvider op2 : opsToInsert) {
                ec.getCallbackHandler().postStore(op2.getObject());
                op2.setFlushedNew(true);
                op2.markAsFlushed();
                op2.setFlushing(false);
                ec.putObjectIntoLevel1Cache(op2);
            }
        }
        if (!opsToFlush.isEmpty()) {
            for (final ObjectProvider op2 : opsToFlush) {
                try {
                    op2.flush();
                }
                catch (NucleusOptimisticException oe) {
                    if (optimisticFailures == null) {
                        optimisticFailures = new ArrayList<NucleusOptimisticException>();
                    }
                    optimisticFailures.add(oe);
                }
            }
        }
        if (classesToFlush != null) {
            for (final Class cls : classesToFlush) {
                ec.getNucleusContext().getStoreManager().getQueryManager().evictQueryResultsForType(cls);
            }
        }
        return optimisticFailures;
    }
}
