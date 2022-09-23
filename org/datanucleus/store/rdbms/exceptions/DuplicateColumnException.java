// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.exceptions;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.util.Localiser;
import org.datanucleus.exceptions.NucleusException;

public class DuplicateColumnException extends NucleusException
{
    private static final Localiser LOCALISER;
    private Column conflictingColumn;
    
    public DuplicateColumnException(final String tableName, final Column col1, final Column col2) {
        super(DuplicateColumnException.LOCALISER.msg("020007", col1.getIdentifier(), tableName, (col1.getMemberMetaData() == null) ? DuplicateColumnException.LOCALISER.msg("020008") : ((col1.getMemberMetaData() != null) ? col1.getMemberMetaData().getFullFieldName() : null), (col2.getMemberMetaData() == null) ? DuplicateColumnException.LOCALISER.msg("020008") : ((col2.getMemberMetaData() != null) ? col2.getMemberMetaData().getFullFieldName() : null)));
        this.conflictingColumn = col2;
        this.setFatal();
    }
    
    public Column getConflictingColumn() {
        return this.conflictingColumn;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
