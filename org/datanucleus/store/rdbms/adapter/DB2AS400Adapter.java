// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.store.rdbms.identifier.IdentifierType;
import java.sql.DatabaseMetaData;

public class DB2AS400Adapter extends DB2Adapter
{
    public DB2AS400Adapter(final DatabaseMetaData metadata) {
        super(metadata);
    }
    
    @Override
    public int getTransactionIsolationForSchemaCreation() {
        return 4;
    }
    
    @Override
    public int getDatastoreIdentifierMaxLength(final IdentifierType identifierType) {
        if (identifierType == IdentifierType.CANDIDATE_KEY) {
            return this.maxConstraintNameLength;
        }
        if (identifierType == IdentifierType.FOREIGN_KEY) {
            return this.maxConstraintNameLength;
        }
        if (identifierType == IdentifierType.INDEX) {
            return this.maxIndexNameLength;
        }
        if (identifierType == IdentifierType.PRIMARY_KEY) {
            return this.maxConstraintNameLength;
        }
        return super.getDatastoreIdentifierMaxLength(identifierType);
    }
}
