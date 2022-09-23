// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store;

import org.datanucleus.ClassConstants;
import org.datanucleus.exceptions.NucleusOptimisticException;
import org.datanucleus.exceptions.NucleusUserException;
import java.sql.Timestamp;
import org.datanucleus.metadata.VersionStrategy;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.util.Localiser;

public class VersionHelper
{
    protected static final Localiser LOCALISER;
    
    public static void performVersionCheck(final ObjectProvider op, final Object versionDatastore, final VersionMetaData versionMetaData) {
        final Object versionObject = op.getTransactionalVersion();
        if (versionObject == null) {
            return;
        }
        if (versionMetaData == null) {
            NucleusLogger.PERSISTENCE.info(op.getClassMetaData().getFullClassName() + " has no version metadata so no check of version is required, since this will not have the version flag in its table");
            return;
        }
        boolean valid;
        if (versionMetaData.getVersionStrategy() == VersionStrategy.DATE_TIME) {
            valid = (((Timestamp)versionObject).getTime() == ((Timestamp)versionDatastore).getTime());
        }
        else if (versionMetaData.getVersionStrategy() == VersionStrategy.VERSION_NUMBER) {
            valid = (((Number)versionObject).longValue() == ((Number)versionDatastore).longValue());
        }
        else {
            if (versionMetaData.getVersionStrategy() == VersionStrategy.STATE_IMAGE) {
                throw new NucleusUserException(VersionHelper.LOCALISER.msg("032017", op.getClassMetaData().getFullClassName(), versionMetaData.getVersionStrategy()));
            }
            throw new NucleusUserException(VersionHelper.LOCALISER.msg("032017", op.getClassMetaData().getFullClassName(), versionMetaData.getVersionStrategy()));
        }
        if (!valid) {
            final String msg = VersionHelper.LOCALISER.msg("032016", op.getObjectAsPrintable(), op.getInternalObjectId(), "" + versionDatastore, "" + versionObject);
            NucleusLogger.PERSISTENCE.error(msg);
            throw new NucleusOptimisticException(msg, op.getObject());
        }
    }
    
    public static Object getNextVersion(final VersionStrategy versionStrategy, final Object currentVersion) {
        if (versionStrategy == null) {
            return null;
        }
        if (versionStrategy == VersionStrategy.NONE) {
            if (currentVersion == null) {
                return 1L;
            }
            if (currentVersion instanceof Integer) {
                return (int)currentVersion + 1;
            }
            return (long)currentVersion + 1L;
        }
        else {
            if (versionStrategy == VersionStrategy.DATE_TIME) {
                return new Timestamp(System.currentTimeMillis());
            }
            if (versionStrategy == VersionStrategy.VERSION_NUMBER) {
                if (currentVersion == null) {
                    return 1L;
                }
                if (currentVersion instanceof Integer) {
                    return (int)currentVersion + 1;
                }
                return (long)currentVersion + 1L;
            }
            else {
                if (versionStrategy == VersionStrategy.STATE_IMAGE) {
                    throw new NucleusUserException("DataNucleus doesnt currently support version strategy \"state-image\"");
                }
                throw new NucleusUserException("Unknown version strategy - not supported");
            }
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
