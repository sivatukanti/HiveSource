// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.flush;

import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.exceptions.NucleusOptimisticException;
import org.datanucleus.state.ObjectProvider;
import java.util.List;
import org.datanucleus.ExecutionContext;

public class FlushOrdered implements FlushProcess
{
    @Override
    public List<NucleusOptimisticException> execute(final ExecutionContext ec, final List<ObjectProvider> primaryOPs, final List<ObjectProvider> secondaryOPs, final OperationQueue opQueue) {
        List<NucleusOptimisticException> optimisticFailures = null;
        Object[] toFlushPrimary = null;
        Object[] toFlushSecondary = null;
        try {
            if (ec.getMultithreaded()) {
                ec.getLock().lock();
            }
            if (primaryOPs != null) {
                toFlushPrimary = primaryOPs.toArray();
                primaryOPs.clear();
            }
            if (secondaryOPs != null) {
                toFlushSecondary = secondaryOPs.toArray();
                secondaryOPs.clear();
            }
        }
        finally {
            if (ec.getMultithreaded()) {
                ec.getLock().unlock();
            }
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            int total = 0;
            if (toFlushPrimary != null) {
                total += toFlushPrimary.length;
            }
            if (toFlushSecondary != null) {
                total += toFlushSecondary.length;
            }
            NucleusLogger.PERSISTENCE.debug(FlushOrdered.LOCALISER.msg("010003", total));
        }
        Set<Class> classesToFlush = null;
        if (ec.getNucleusContext().getStoreManager().getQueryManager().getQueryResultsCache() != null) {
            classesToFlush = new HashSet<Class>();
        }
        if (toFlushPrimary != null) {
            for (int i = 0; i < toFlushPrimary.length; ++i) {
                final ObjectProvider op = (ObjectProvider)toFlushPrimary[i];
                try {
                    op.flush();
                    if (classesToFlush != null) {
                        classesToFlush.add(op.getObject().getClass());
                    }
                }
                catch (NucleusOptimisticException oe) {
                    if (optimisticFailures == null) {
                        optimisticFailures = new ArrayList<NucleusOptimisticException>();
                    }
                    optimisticFailures.add(oe);
                }
            }
        }
        if (toFlushSecondary != null) {
            for (int i = 0; i < toFlushSecondary.length; ++i) {
                final ObjectProvider sm = (ObjectProvider)toFlushSecondary[i];
                try {
                    sm.flush();
                    if (classesToFlush != null) {
                        classesToFlush.add(sm.getObject().getClass());
                    }
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
